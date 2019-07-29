;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.session
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            [<<namespace>>.client.session.oidc-sso :as oidc-sso]
            [<<namespace>>.client.view :as view]))

(rf/reg-sub
 ::token
 (fn [db]
   (:jwt-token db)))

(rf/reg-event-fx
 ::set-token
 (fn [{:keys [db]} [_ jwt-token]]
   {:db (assoc db :jwt-token jwt-token)
    :dispatch [::oidc-sso/trigger-sso-apps]}))

(rf/reg-event-fx
 ::remove-token
 (fn [{:keys [db]} [_]]
   {:db (dissoc db :jwt-token)
    :dispatch [::oidc-sso/trigger-logout-apps]}))

(rf/reg-event-db
 ::set-auth-error
 (fn [db [_ error]]
   (assoc db :auth-error error)))

(rf/reg-sub
 ::auth-error
 (fn [db]
   (:auth-error db)))

(defn- get-user-pool [db]
  (let [awscog-user-pool-id (last (str/split (get-in db [:config :oidc :cognito :iss]) #"/"))
        awscog-app-client-id (get-in db [:config :oidc :cognito :client-id])]
    (new js/AmazonCognitoIdentity.CognitoUserPool #js {:UserPoolId awscog-user-pool-id
                                                       :ClientId awscog-app-client-id})))

(defn- assoc-jwt-token-to-cofx
  [cofx current-user]
  (let [jwt-token (.getSession current-user (fn [err session]
                                              (when (not err)
                                                (-> session .-idToken .-jwtToken))))]
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
      #js {:onSuccess (fn [cognitoAuthResult]
                        (let [jwt-token (-> cognitoAuthResult .-idToken .-jwtToken)]
                          (rf/dispatch [::set-auth-error nil])
                          (rf/dispatch [::set-token jwt-token])
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
