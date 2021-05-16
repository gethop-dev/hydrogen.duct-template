;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.session
  (:require [clojure.spec.alpha :as s]
            [re-frame.core :as rf]
            [<<namespace>>.client.session.oidc-sso :as oidc-sso]
            [<<namespace>>.client.view :as view]))

;; Keycloak Javascript library is not designed to be used in a
;; functional way. When you create a keycloak object to interact with
;; it, it keeps a lot of internal state that it needs to perform
;; operations like login state, logout, token refreshment, etc. If we
;; create a new object with the same configuration settings, we don't
;; get any of that internal state back. It's only available in the
;; original object. In practice, that means we need to keep a copy of
;; the original Keycloak object that we used to log in, so we can do
;; operations like logout.
;;
;; Because of the way re-frame recommends to design event handler
;; side-effects, we shouldn't build the Keycloak object in the event
;; handler (that would be side-effectful!). But if we build it in the
;; effect handler, we can't store in the appdb (it's not available
;; there). So after an internal discussion, we have decided that the
;; least hacky way of doing it is storing the Keycloak object in an
;; atom.
(def keycloak (atom nil))

(defn keycloak-process-ongoing? []
  (or
   (nil? @keycloak)
   (and
    (view/get-query-param js/location.hash "state")
    (view/get-query-param js/location.hash "session_state")
    (view/get-query-param js/location.hash "code"))))

(rf/reg-event-fx
 ::set-auth-error
 (fn [{:keys [db]} [_ error]]
   {:db (assoc db :auth-error error)}))

(rf/reg-sub
 ::auth-error
 (fn [db]
   (:auth-error db)))

(defn- handle-keycloak-obj-change [keycloak-obj]
  (reset! keycloak keycloak-obj)
  (rf/dispatch [::set-token-and-schedule-refresh]))

(rf/reg-fx
 ::refresh-token-keycloak
 (fn [{:keys [min-validity]}]
   (let [keycloak-obj @keycloak]
     (-> keycloak-obj
         (.updateToken min-validity)
         (.then
          (fn [refreshed]
            ;; If token was still valid, do nothing
            (when refreshed
              (handle-keycloak-obj-change keycloak-obj))))
         (.catch
          (fn []
            (doseq [event [[::set-auth-error "Failed to refresh token, or the session has expired. Logging user out."]
                           [::user-logout]]]
              (rf/dispatch event))))))))

(rf/reg-event-fx
 ::refresh-token
 (fn [_ [_ min-validity]]
   {::refresh-token-keycloak {:min-validity min-validity}}))

(defn- now-cofx
  "Adds a cofx with a current timestamp in seconds"
  [cofx]
  (assoc cofx :now (quot (.getTime (js/Date.)) 1000)))

(rf/reg-cofx :now now-cofx)

(rf/reg-event-fx
 ::schedule-token-refresh
 [(rf/inject-cofx :now)]
 (fn [{:keys [now]} [_ token-exp]]
   (let [token-lifetime (int (- token-exp now))
         ;; If we refresh the token when it's close to the session
         ;; lifetime, keycloak returns a new token with a lifetime
         ;; that is the difference between the current time and the
         ;; session expiration time. Which may be lower than the
         ;; configured token lifetime. As we keep refreshing the token
         ;; the lifetime gets shorter and shorter. But we want the dispatch
         ;; not to be more frequent than one second, hence the `(max)` function.
         half-lifetime (quot token-lifetime 2)
         min-validity token-lifetime]
     {:dispatch-later [{:ms (* 1000 (max 1 half-lifetime))
                        :dispatch [::refresh-token min-validity]}]})))

(rf/reg-event-fx
 ::set-token
 (fn [{:keys [db]} [_ jwt-token]]
   {:db (assoc db :jwt-token jwt-token)
    :dispatch [::oidc-sso/trigger-sso-apps]}))

(defn- keycloak-cofx
  [cofx _]
  {:post [(contains? % :keycloak)]}
  (assoc cofx :keycloak @keycloak))

(rf/reg-cofx :keycloak keycloak-cofx)

(defn- session-cofx
  [cofx _]
  {:pre [(or (:keycloak cofx) @keycloak)]
   :post [(contains? % :session)
          (s/valid? ::session-cofx-spec (:session %))]}
  (rf/console :log "Calculating session cofx" (clj->js cofx))
  (let [keycloak-state (or (:keycloak cofx) @keycloak)
        session (when-let [jwt-token (.-idToken keycloak-state)]
                  (let [token-exp (-> keycloak-state .-idTokenParsed .-exp)]
                    {:jwt-token jwt-token
                     :token-exp token-exp}))]
    (assoc cofx :session session)))

(rf/reg-cofx :session session-cofx)

(s/def ::token-exp number?)
(s/def ::session-cofx-spec
  (s/nilable (s/keys :req-un [::jwt-token ::token-exp])))

(defn- set-token-and-schedule-refresh-event-fx
  [{:keys [session]} _]
  {:pre [(some? session)
         (s/valid? ::session-cofx-spec session)]}
  {:dispatch-n [[::set-token (:jwt-token session)]
                [::schedule-token-refresh (:token-exp session)]]})

(rf/reg-event-fx
 ::set-token-and-schedule-refresh
 [(rf/inject-cofx :session)]
 set-token-and-schedule-refresh-event-fx)

(defn- remove-keycloak-process-query-params
  [location-hash]
  (-> location-hash
      (view/remove-query-param :state)
      (view/remove-query-param :code)
      (view/remove-query-param :session_state)))

(rf/reg-event-fx
 ::on-login-success
 (fn [_ _]
   {:dispatch-n [[::set-token-and-schedule-refresh]
                 [::set-auth-error nil]]
    :redirect (remove-keycloak-process-query-params js/location.hash)}))

(rf/reg-fx
 :init-and-try-to-authenticate
 (fn [config]
   (let [{:keys [realm url client-id]} (get-in config [:oidc :keycloak])
         keycloak-obj (js/Keycloak #js {:realm realm
                                        :url url
                                        :clientId client-id})]
     (-> keycloak-obj
         (.init #js {"onLoad" "check-sso"
                     "promiseType" "native"
                     "silentCheckSsoRedirectUri" (str js/window.location.origin "/silent-check.html")})
         (.then (fn [authenticated]
                  (reset! keycloak keycloak-obj)
                  (when authenticated
                    (handle-keycloak-obj-change keycloak-obj)
                    ;; Since we sometime turn &state into ?state, Keycloak
                    ;; is unable to clean up after itself.
                    (view/redirect! (remove-keycloak-process-query-params js/location.hash)))))
         (.catch #(rf/dispatch [::set-auth-error "Failed to initialize Keycloak"]))))))

(rf/reg-fx
 ::login
 (fn [_]
   (when @keycloak
     (.login @keycloak))))

(rf/reg-event-fx
 ::user-login
 (fn [_ _]
   {::login true}))

(rf/reg-fx
 ::logout
 (fn [_]
   (when @keycloak
     (.logout @keycloak)
     ;; See comment at the top of this file to see why we manage the
     ;; keycloak object this way.
     (reset! keycloak nil))))

(rf/reg-event-fx
 ::user-logout
 (fn [{:keys [db]} [_]]
   {:db (dissoc db :jwt-token)
    ::logout []
    :dispatch [::oidc-sso/trigger-logout-apps]}))
