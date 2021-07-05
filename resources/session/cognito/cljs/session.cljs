;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.session
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [re-frame.core :as rf]
            [<<namespace>>.client.session.oidc-sso :as oidc-sso]))

(rf/reg-event-db
 ::set-auth-error
 (fn [db [_ error]]
   (assoc db :auth-error error)))

(rf/reg-sub
 ::auth-error
 (fn [db]
   (:auth-error db)))

(defn- get-user-pool [db]
  (let [user-pool-id (last (str/split (get-in db [:config :oidc :cognito :iss]) #"/"))
        client-id (get-in db [:config :oidc :cognito :client-id])]
    (new js/AmazonCognitoIdentity.CognitoUserPool #js {:UserPoolId user-pool-id
                                                       :ClientId client-id})))

(defn- user-pool-cofx
  [{:keys [db] :as cofx} _]
  {:pre [(:config db)]
   :post [(contains? % :user-pool)]}
  (rf/console :log "user-pool cofx" (clj->js cofx))
  (assoc cofx :user-pool (get-user-pool db)))

(rf/reg-cofx :user-pool user-pool-cofx)

(def session-error-events
  "Collection of events to dispatch on session error"
  [[::set-auth-error "Failed to refresh token, or the session has expired. Logging user out."]
   [::user-logout]])

(defn- session-error
  []
  (doseq [event session-error-events]
    (rf/dispatch event)))

(defn- get-user-session
  [current-user]
  (.getSession current-user (fn [err session]
                              (if err
                                (do
                                  (session-error)
                                  ;; Make sure we return nil to signal there is no active session.
                                  nil)
                                session))))

(defn- session-cofx
  [{:keys [db user-pool] :as cofx} _]
  {:pre [(or user-pool (:config db))]
   :post [(contains? % :session)
          (s/valid? ::session-cofx-spec (:session %))]}
  (rf/console :log "Calculating session cofx" (clj->js cofx))
  (let [user-pool (or user-pool (get-user-pool db))
        session (when-let [current-user (.getCurrentUser user-pool)]
                  (when-let [user-session (get-user-session current-user)]
                    (let [id-token (.getIdToken user-session)
                          jwt-token (.getJwtToken id-token)
                          token-exp (.getExpiration id-token)]
                      (when (and user-session id-token jwt-token token-exp)
                        {:current-user current-user
                         :user-session user-session
                         :id-token id-token
                         :jwt-token jwt-token
                         :token-exp token-exp}))))]
    (assoc cofx :session session)))

(rf/reg-cofx :session session-cofx)

(s/def ::token-exp number?)
(s/def ::session-cofx-spec
  (s/nilable (s/keys :req-un [::current-user
                              ::user-session
                              ::id-token
                              ::jwt-token
                              ::token-exp])))

(defn- refresh-token-event-fx
  [{:keys [session] :as cofx} _]
  {:pre [(contains? cofx :session)
         (s/valid? ::session-cofx-spec session)]}
  (if session
    (.refreshSession (:current-user session)
                     (.getRefreshToken (:user-session session))
                     (fn [err _]
                       (if err
                         (session-error)
                         (rf/dispatch [::set-token-and-schedule-refresh]))))
    {:dispatch-n session-error-events}))

(rf/reg-event-fx
 ::refresh-token
 [(rf/inject-cofx :user-pool)
  (rf/inject-cofx :session)]
 refresh-token-event-fx)

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
         ;; lifetime, cognito returns a new token with a lifetime
         ;; that is the difference between the current time and the
         ;; session expiration time. Which may be lower than the
         ;; configured token lifetime. As we keep refreshing the token
         ;; the lifetime gets shorter and shorter. But we want the dispatch
         ;; not to be more frequent than one second, hence the `(max)` function.
         half-lifetime (quot token-lifetime 2)
         min-validity token-lifetime]
     {:dispatch-later [{:ms (* 1000 (max 1 half-lifetime))
                        :dispatch [::refresh-token min-validity]}]})))

(defn- set-token-and-schedule-refresh-event-fx
  [{:keys [session]} _]
  {:pre [session
         (s/valid? ::session-cofx-spec session)]}
  {:dispatch-n [[::set-token (:jwt-token session)]
                [::schedule-token-refresh (:token-exp session)]]})

(rf/reg-event-fx
 ::set-token-and-schedule-refresh
 [(rf/inject-cofx :user-pool)
  (rf/inject-cofx :session)]
 set-token-and-schedule-refresh-event-fx)

(rf/reg-event-fx
 ::set-token
 (fn [{:keys [db]} [_ jwt-token]]
   {:db (assoc db :jwt-token jwt-token)
    :dispatch [::oidc-sso/trigger-sso-apps]}))

(rf/reg-sub
 ::token
 (fn [db]
   (:jwt-token db)))

(rf/reg-event-fx
 ::remove-token
 (fn [{:keys [db]} [_]]
   {:db (dissoc db :jwt-token)
    :dispatch [::oidc-sso/trigger-logout-apps]}))

(rf/reg-event-fx
 ::on-login-success
 (fn [_ _]
   {:dispatch-n [[::set-token-and-schedule-refresh]
                 [::set-auth-error nil]]
    :redirect "/#/home"}))

(rf/reg-fx
 ::do-user-login
 (fn [{:keys [user-pool username password]}]
   (let [auth-data #js {:Username username
                        :Password password}
         auth-details (new js/AmazonCognitoIdentity.AuthenticationDetails auth-data)
         user-data #js {:Username username
                        :Pool user-pool}
         cognito-user (new js/AmazonCognitoIdentity.CognitoUser user-data)]
     (.authenticateUser
      cognito-user
      auth-details
      #js {:onSuccess #(rf/dispatch [::on-login-success])
           :onFailure #(rf/dispatch [::set-auth-error "Incorrect username or password"])}))))

(defn- user-login-event-fx
  [{:keys [user-pool]} [_ {:keys [username password]}]]
  {:pre [user-pool]}
  {::do-user-login {:user-pool user-pool
                    :username username
                    :password password}})

(rf/reg-event-fx
 ::user-login
 [(rf/inject-cofx :user-pool)]
 user-login-event-fx)

(rf/reg-fx
 ::sign-out
 (fn [current-user]
   (.signOut current-user)))

(defn- user-logout-event-fx
  [{:keys [session] :as cofx} _]
  {:pre [(contains? cofx :session)
         (s/valid? ::session-cofx-spec session)]}
  (when-let [current-user (:current-user session)]
    {::sign-out current-user
     :dispatch [::remove-token]}))

(rf/reg-event-fx
 ::user-logout
 [(rf/inject-cofx :user-pool)
  (rf/inject-cofx :session)]
 user-logout-event-fx)
