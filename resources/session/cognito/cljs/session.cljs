;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.session
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            [<<namespace>>.client.session.oidc-sso :as oidc-sso]
            [<<namespace>>.client.view :as view]))

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

(defn- get-id-token [current-user]
  (.getSession current-user (fn [err session]
                              (when (not err)
                                (-> session .-idToken)))))

(defn- get-jwt-token [current-user]
  (.getSession current-user (fn [err session]
                              (when (not err)
                                (-> session .-idToken .-jwtToken)))))

(defn- set-token-and-sched-refresh [id-token]
  (let [jwt-token (.getJwtToken id-token)
        token-exp (.getExpiration id-token)]
    (rf/dispatch [::set-token jwt-token])
    (rf/dispatch [::schedule-token-refresh token-exp])))

(rf/reg-fx
 ::refresh-token-cognito
 (fn [{:keys [current-user]}]
   (if-let [id-token (get-id-token current-user)]
     (set-token-and-sched-refresh id-token)
     (doseq [event [[::set-auth-error "Failed to refresh token, or the session has expired. Logging user out."]
                    [::user-logout]]]
       (rf/dispatch event)))))

(rf/reg-event-fx
 ::refresh-token
 (fn [{:keys [db]} [_]]
   (let [current-user (-> (get-user-pool db)
                          (.getCurrentUser))]
     {:db db
      ::refresh-token-cognito {:current-user current-user}})))

(rf/reg-event-fx
 ::schedule-token-refresh
 (fn [{:keys [db]} [_ token-exp]]
   (let [now (/ (.getTime (js/Date.)) 1000)
         token-lifetime (int (- token-exp now))
         ;; If we refresh the token when it's close to the session
         ;; lifetime, cognito returns a new token with a lifetime
         ;; that is the difference between the current time and the
         ;; session expiration time. Which may be lower than the
         ;; configured token lifetime. As we keep refreshing the token
         ;; the lifetime gets shorter and shorter, and eventually may
         ;; get to 1 second. But half-lifetime must be greater than
         ;; zero. Otherwise :dispatch-later gets a zero min-delay and
         ;; returns inmediatly without dispatching the event(s). So
         ;; make sure half-lifetime is at least 1 second.
         half-lifetime (max 1 (quot token-lifetime 2))
         min-validity token-lifetime]
     {:db db
      :dispatch-later [{:ms (* 1000 half-lifetime)
                        :dispatch [::refresh-token]}]})))

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

(defn- assoc-jwt-token-to-cofx
  [cofx current-user]
  (let [jwt-token (get-jwt-token current-user)]
    (-> cofx
        (assoc-in [:db :jwt-token] jwt-token)
        (assoc :jwt-token jwt-token))))

(rf/reg-cofx
 ::jwt-token
 (fn [{:keys [db] :as cofx} _]
   (let [current-user (-> (get-user-pool db)
                          (.getCurrentUser))]
     (cond-> cofx
       current-user (assoc-jwt-token-to-cofx current-user)))))

(rf/reg-event-fx
 ::user-login
 (fn [{:keys [db]} [_ {:keys [username password]}]]
   (let [user-pool (get-user-pool db)
         auth-data #js {:Username username
                        :Password password}
         auth-details (new js/AmazonCognitoIdentity.AuthenticationDetails auth-data)
         user-data #js {:Username username
                        :Pool user-pool}
         cognito-user (new js/AmazonCognitoIdentity.CognitoUser user-data)]
     (.authenticateUser
      cognito-user
      auth-details
      #js {:onSuccess (fn [user-session]
                        (let [id-token (-> user-session .-idToken)]
                          (set-token-and-sched-refresh id-token)
                          (rf/dispatch [::set-auth-error nil])
                          (view/redirect! "/#/home")))
           :onFailure (fn [err]
                        (rf/dispatch [::set-auth-error "Incorrect username or password"]))}))))

(rf/reg-event-fx
 ::user-logout
 (fn [{:keys [db]} [_]]
   (let [user-pool (get-user-pool db)
         current-user (.getCurrentUser user-pool)]
     (when current-user
       (.signOut current-user)
       {:dispatch [::remove-token]}))))
