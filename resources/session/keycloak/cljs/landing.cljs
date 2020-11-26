;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.landing
  (:require [re-frame.core :as rf]
            [<<namespace>>.client.session :as session]
            [<<namespace>>.client.theme :as theme]
            [<<namespace>>.client.view :as view]))

(rf/reg-event-fx
 ::go-to-landing
 (fn [_ _]
   {:dispatch [::view/set-active-view :landing]}))

(defn login-form []
  [:div.login-form
   [:button.button.landing__button.button--square {:on-click #(rf/dispatch [::session/user-login])}
    "Login"]])

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
