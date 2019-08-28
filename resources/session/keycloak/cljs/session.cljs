;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.session
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [<<namespace>>.client.session.oidc-sso :as oidc-sso]))

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
;; least hacky way of doing it is storing the Keycloak object in a
;; Reagent atom.
(def keycloak (r/atom nil))

(def kc-process-max-age
  "Time (in seconds) that we allow for the Keycloak login process to
  last, before timing it out"
  60)

(rf/reg-event-fx
 ::set-auth-error
 (fn [{:keys [db]} [_ error]]
   {:db (assoc db :auth-error error)
    :cookie/remove "KEYCLOAK_PROCESS"}))

(rf/reg-sub
 ::auth-error
 (fn [db]
   (:auth-error db)))

(defn- set-token-and-sched-refresh [keycloak-obj]
  ;; See comment at the top of this file to see
  ;; why we manage the keycloak object this way.
  (let [jwt-token (.-idToken keycloak-obj)
        token-exp (-> keycloak-obj .-idTokenParsed .-exp)]
    (reset! keycloak keycloak-obj)
    (rf/dispatch [::set-token jwt-token])
    (rf/dispatch [::schedule-token-refresh token-exp])))

(rf/reg-fx
 ::refresh-token-keycloak
 (fn [{:keys [min-validity]}]
   (let [keycloak-obj @keycloak]
     (-> keycloak-obj
         (.updateToken min-validity)
         (.success (fn [refreshed]
                     ;; If token was still valid, so do nothing
                     (when refreshed
                       (set-token-and-sched-refresh keycloak-obj))))
         (.error (fn []
                   (doseq [event [[::set-auth-error "Failed to refresh token, or the session has expired. Logging user out."]
                                  [::user-logout]]]
                     (rf/dispatch event))))))))

(rf/reg-event-fx
 ::refresh-token
 (fn [{:keys [db]} [_ min-validity]]
   {:db db
    ::refresh-token-keycloak {:min-validity min-validity}}))

(rf/reg-event-fx
 ::schedule-token-refresh
 (fn [{:keys [db]} [_ token-exp]]
   (let [now (/ (.getTime (js/Date.)) 1000)
         token-lifetime (int (- token-exp now))
         ;; If we refresh the token when it's close to the session
         ;; lifetime, keycloak returns a new token with a lifetime
         ;; that is the difference between the current time and the
         ;; session expiration time. Which may be lower than the
         ;; configured token lifetime. As we keep refreshing the token
         ;; the lifetime gets shorter and shorter, and eventually gets
         ;; smaller than 2 seconds. That means half-lifetime would be
         ;; zero. But half-lifetime must be greater than zero.
         ;; Otherwise :dispatch-later would get a zero min-delay
         ;; and return inmediatly without dispatching the event(s). So
         ;; make sure half-lifetime is at least 1 second.
         half-lifetime (max 1 (quot token-lifetime 2))
         min-validity token-lifetime]
     {:db db
      :dispatch-later [{:ms (* 1000 half-lifetime)
                        :dispatch [::refresh-token min-validity]}]})))

(rf/reg-event-fx
 ::set-token
 (fn [{:keys [db]} [_ jwt-token]]
   {:db (assoc db :jwt-token jwt-token)
    :cookie/remove "KEYCLOAK_PROCESS"
    :dispatch [::oidc-sso/trigger-sso-apps]}))

(rf/reg-fx
 :init-and-authenticate
 (fn [config]
   (let [{:keys [realm url client-id]} (get-in config [:oidc :keycloak])
         keycloak-obj (js/Keycloak #js {:realm realm
                                        :url url
                                        :clientId client-id})]
     (-> keycloak-obj
         (.init #js {"onLoad" "login-required"})
         (.success (fn [authenticated]
                     (when authenticated
                       (set-token-and-sched-refresh keycloak-obj))))
         (.error (fn []
                   (rf/dispatch [::set-auth-error "Failed to initialize Keycloak"])))))))

(rf/reg-event-fx
 ::auth
 (fn [{:keys [db]} _]
   {:cookie/set ["KEYCLOAK_PROCESS" true :max-age kc-process-max-age]
    :init-and-authenticate (:config db)}))

(defn keycloak-login-btn []
  [:div.btn.auth-btn {:on-click #(rf/dispatch [::auth])}
   [:span "Login using "]
   [:img.auth-btn__image {:src "https://www.keycloak.org/resources/images/keycloak_logo_480x108.png"}]])

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
