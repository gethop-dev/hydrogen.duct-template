;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.landing
  "This namespace is there to solve cross-platform compatibility for BE and FE when doing isomorphic rendering"
  (:require [re-frame.core :as rf]
            [<<namespace>>.client.theme :as theme]
            [<<namespace>>.client.view :as view]))

(rf/reg-event-fx
 ::view.enter
 (fn [_ _]
   {:dispatch [::view/set-active-view [::view]]
    :redirect "/landing"}))

(rf/reg-event-fx
 ::view.leave
 (fn [_ _]
   {}))

(def credentials (atom {:username "" :password ""}))

(defn swap-input! [event atom field]
  (swap! atom assoc field (.. event -target -value)))

(defn login-form []
  [:div.login-form-container
   [:form.login-form
    [:div.form-field
     [:img.form-field__icon {:src "images/email-address.svg"}]
     [:input.form-field__input
      {:type "email"
       :auto-complete "username"
       :placeholder "Email"
       :id "email"
       :value (:username @credentials)
       :on-change #(swap-input! % credentials :username)}]]
    [:div.form-field
     [:img.form-field__icon {:src "images/password.svg"}]
     [:input.form-field__input
      {:type "password"
       :auto-complete "current-password"
       :placeholder "Password"
       :id "password"
       :value (:password @credentials)
       :on-change #(swap-input! % credentials :password)}]]]
   [:button.btn.btn--gradient "Login"]])

(defn header []
  [:header
   [:h1 "Hydrogen"]])

(defn main []
  (let [theme (rf/subscribe [::theme/get-theme])]
    (fn []
      [:div.landing-container
       {:class (str "theme-" (name @theme))}
       [header]
       [login-form]])))

(defmethod view/view-display ::view
  [_]
  [main])
