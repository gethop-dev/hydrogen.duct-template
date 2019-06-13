;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.session
  (:require [clojure.string :as s]
            [re-frame.core :as rf]
            [<<namespace>>.client.view :as view]))

(rf/reg-sub
  ::token
  (fn [db]
      (:token db)))

(rf/reg-event-fx
  ::set-auth-error
  (fn [{:keys [db]} [_ error]]
      {:db (assoc db :auth-error error)
       :cookie/remove "KEYCLOAK_PROCESS"}))

(rf/reg-sub
  ::auth-error
  (fn [db]
      (:auth-error db)))

(rf/reg-event-fx
  ::set-jwt-token
  (fn [{:keys [db]} [_ jwt-token]]
      {:db (assoc db :jwt-token jwt-token)
       :cookie/remove "KEYCLOAK_PROCESS"}))

(rf/reg-event-fx
  ::user-logout
  (fn [{:keys [db]} [_]]
      (js/alert "TBD")
      {:db db}))

(rf/reg-event-fx
  ::auth
  (fn [{:keys [db]} _]
      {:cookie/set ["KEYCLOAK_PROCESS" true :max-age 60]
       :init-and-authenticate (get-in db [:config :keycloak])}))

(rf/reg-fx
  :init-and-authenticate
  (fn [keycloak-config]
      (let [js-keycloak-config (clj->js keycloak-config)
            keycloak-obj (js/Keycloak js-keycloak-config)]
           (-> keycloak-obj
               (.init #js {"onLoad" "login-required"})
               (.success (fn [authenticated]
                             (when authenticated
                                   (rf/dispatch [::set-jwt-token (.-idToken keycloak-obj)]))))
               (.error (fn []
                           (prn "Failed to initialize Keycloak")
                           (rf/dispatch [::set-auth-error "Failed to initialize Keycloak"])))))))

(defn keycloak-login-btn []
      [:div.btn.auth-btn {:on-click #(rf/dispatch [::auth])}
       [:span "Login using "]
       [:img.auth-btn__image {:src "https://www.keycloak.org/resources/images/keycloak_logo_480x108.png"}]])
