;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.home
  (:require [re-frame.core :as rf]<<#hydrogen-session?>>
            [<<namespace>>.client.session :as session]<</hydrogen-session?>>
            [<<namespace>>.client.tooltip :as tooltip]
            [<<namespace>>.client.tooltip.loading-popup :as loading-popup]<<#hydrogen-session?>>
            [<<namespace>>.client.user :as user]<</hydrogen-session?>>
            [<<namespace>>.client.view :as view]))

(rf/reg-event-fx
 ::go-to-home
 (fn [_ _]
   {:dispatch [::view/set-active-view :home]
    :redirect "/#/home"}))

;; This section contains code solely for demo purposes.
;; Go ahead and delete it.
(rf/reg-event-fx
  ::DELETEME-demo-request
  (fn [_ _]
    {:dispatch [::loading-popup/set-loading "Faking loading. I'll disappear after 5 seconds..."]
     ;; This http-xhrio should be there instead of the dispatch-later in real situation.
     ; http-xhrio {...}
     :dispatch-later [{:ms 5000
                       :dispatch [::loading-popup/stop-loading]}]}))

(defn- DELETEME-demo-tooltip-component []
  [:div
   "Hello! I'm a demo tooltip. "
   [:a.u-clickable
    {:on-click #(rf/dispatch [::tooltip/destroy-by-id "generic-popup"])} "x"]])

(defn- DELETEME-demo-tooltip []
  [:button.btn
   {:on-click #(rf/dispatch [::tooltip/register {:id "generic-popup"
                                                 :component DELETEME-demo-tooltip-component
                                                 :modal? true}])}
   "Demo tooltip"])

(defn- DELETEME-demo-loading []
  [:button.btn {:on-click #(rf/dispatch [::DELETEME-demo-request])}
   "Pretend you are requesting something from the Internet"])
;; end of obsolete demo code<<#hydrogen-session?>>

(defn- user-details []
  (let [user-data (rf/subscribe [::user/user-data])]
    (fn []
      (when @user-data
        [:div {:style {:text-align :center}}
         [:p (str "Hello again " (:first-name @user-data) " " (:last-name @user-data) "!")]
         [:img {:src (or (:avatar @user-data) "images/user.svg")
                :style {:width "100px" :height "100px" :border-radius "50%"}}]]))))

(defn- logout []
  [:div.logout
   {:on-click #(do (rf/dispatch [::session/user-logout])
                   (view/redirect! "/#/landing"))}
   "Logout"])<</hydrogen-session?>>

(defn main []
  [:div
   [:img {:src "images/hydrogen-logo-white.svg" :alt "Hydrogen logo"}]
   [:h1.demo-greeting "Welcome to Hydrogen!"]
   [DELETEME-demo-tooltip]
   [DELETEME-demo-loading]<<#hydrogen-session?>>
   [user-details]
   [logout]<</hydrogen-session?>>])
