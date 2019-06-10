;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.landing
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [<<namespace>>.client.session :as session]
            [<<namespace>>.client.view :as view]))

(rf/reg-event-fx
 ::go-to-landing
 (fn [_ _]
   {:dispatch [::view/set-active-view :landing]}))

(def credentials (reagent/atom {:username "" :password ""}))

(defn swap-input! [event atom field]
  (swap! atom assoc field (.. event -target -value)))

(defn- do-login-if-enter-pressed [event credentials]
  (when (= (.-key event) "Enter")
    (rf/dispatch [::session/user-login credentials])
    (.preventDefault event)))

(defn login-form []
  (let [auth-error (rf/subscribe [::session/auth-error])]
    (fn []
      [:div.login-form
       [:form
        [:label {:for "username"} "Username"]
        [:input {:type "text"
                 :id "username"
                 :value (:username @credentials)
                 :on-key-press #(do-login-if-enter-pressed % @credentials)
                 :on-change #(swap-input! % credentials :username)}]]
       [:form
        [:label {:for "password"} "Password"]
        [:input {:type "password"
                 :id "password"
                 :value (:password @credentials)
                 :on-key-press #(do-login-if-enter-pressed % @credentials)
                 :on-change #(swap-input! % credentials :password)}]]
       [:div.btn
        {:on-click #(rf/dispatch [::session/user-login @credentials])}
        "Login"]
       (when @auth-error
         [:p {:style {:color :red}} (name @auth-error)])])))

(defn header []
  [:header
   [:h1 "Hydrogen"]])

(defn main []
  [:div.landing-container
   [header]
   [login-form]])
