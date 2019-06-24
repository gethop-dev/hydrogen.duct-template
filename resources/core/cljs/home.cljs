;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.home
  (:require [re-frame.core :as rf]<<#hydrogen-session?>>
            [<<namespace>>.client.session :as session]<</hydrogen-session?>>
            [<<namespace>>.client.view :as view]))

(rf/reg-event-fx
 ::go-to-home
 (fn [_ _]
   {:dispatch [::view/set-active-view :home]
    :redirect "/#/home"}))<<#hydrogen-session?>>

(defn logout []
  [:div.logout
   {:on-click #(do (rf/dispatch [::session/user-logout])
                   (view/redirect! "/#/landing"))}
   "Logout"])<</hydrogen-session?>>

(defn main []
  [:div {:id "home"}
   [:img {:src "images/hydrogen-logo-white.svg" :alt "Hydrogen logo"}]
   [:h1 "Welcome to Hydrogen!"]<<#hydrogen-session?>>
   [logout]<</hydrogen-session?>>])
