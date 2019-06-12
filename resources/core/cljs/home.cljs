;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.home
  (:require [re-frame.core :as rf]<<#hydrogen-session?>>
            [<<namespace>>.client.session :as session]<</hydrogen-session?>>
            [<<namespace>>.client.tooltip :as tooltip]
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

(defn links []
  [:div {:id "home-links"}
   [:a {:href "/#/todo-list"} "TODO LIST"]])

(defn example-tooltip [content]
  [:div.tooltip.tooltip--left content])

(defn persistent-tooltip-controller []
  (let [tooltip-id (str (random-uuid))
        tooltip-data (rf/subscribe [::tooltip/by-id tooltip-id])]
    (fn []
      [:div.u-position-relative.u-display-inline-block
       {:class (tooltip/gen-controller-class tooltip-id)}
       [:button.btn {:on-click #(if @tooltip-data
                                  (rf/dispatch [::tooltip/destroy-by-id tooltip-id])
                                  (rf/dispatch [::tooltip/register {:id tooltip-id
                                                                    :destroy-on-click-out? false}]))}
        (if @tooltip-data "Destroy tooltip" "Spawn persistent tooltip")]
       (when @tooltip-data
         [example-tooltip "You can destroy me only by clicking the button that created me."])])))

(defn regular-tooltip-controller []
  (let [tooltip-id (str (random-uuid))
        tooltip-data (rf/subscribe [::tooltip/by-id tooltip-id])]
    (fn []
      [:div.u-position-relative.u-display-inline-block
       {:class (tooltip/gen-controller-class tooltip-id)}
       [:button.btn {:on-click #(rf/dispatch [::tooltip/register {:id tooltip-id}])}
        "Spawn regular tooltip"]
       (when @tooltip-data
         [example-tooltip "Hello! What a wonderful day!"])])))

(defn tooltip-sandbox []
  [:div
   [regular-tooltip-controller]
   [persistent-tooltip-controller]])

(defn main []
  [:div {:id "home"}
   [:img {:src "images/hydrogen-logo-white.svg" :alt "Hydrogen logo"}]
   [:h1 "Welcome to Hydrogen!"]
   [tooltip-sandbox]
   [:p "What do you want to play with?"]
   [links]<<#hydrogen-session?>>
   [logout]<</hydrogen-session?>>])
