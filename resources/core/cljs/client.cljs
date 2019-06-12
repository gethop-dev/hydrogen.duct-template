;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [reagent.core :as reagent]
            [<<namespace>>.client.home :as home]<<#hydrogen-session?>>
            [<<namespace>>.client.landing :as landing]<</hydrogen-session?>>
            [<<namespace>>.client.routes :as routes]
            [<<namespace>>.client.theme :as theme]
            [<<namespace>>.client.todo :as todo]
            [<<namespace>>.client.tooltip :as tooltip]
            [<<namespace>>.client.view :as view]))

(def default-db
  {:theme :light})<<#hydrogen-session?>>

(rf/reg-event-db
 ::set-config
 (fn [db [_ config]]
   (assoc db :config config)))

(rf/reg-event-db
 ::error
 (fn [db [_ _]]
   (assoc db :error :unable-to-load-config)))<</hydrogen-session?>>

(rf/reg-event-fx
 ::load-app
 (fn [{:keys [db]} [_]]
   {:db default-db<<#hydrogen-session?>>
    :http-xhrio {:method :get
                 :uri "/api/config"
                 :format (ajax/json-request-format)
                 :response-format (ajax/transit-response-format)
                 :on-success [::set-config]
                 :on-failure [::error]}<</hydrogen-session?>>}))

(defn main []
  (let [active-view (rf/subscribe [::view/active-view])]
    (fn []
      (case @active-view<<#hydrogen-session?>>
        :landing [landing/main]<</hydrogen-session?>>
        :home [home/main]
        :todo-list [todo/main]))))

(defn dev-setup []
  (when goog.DEBUG
    (enable-console-print!)
    (println "Dev mode")))

(defn app []
  (let [theme (rf/subscribe [::theme/get-theme])]
    (fn []
      [:div.app-container
       {:on-click #(tooltip/destroy-on-click-out (.. % -target))
        :class (str "theme-" (name @theme))}
       [main]])))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (reagent/render [app] (.getElementById js/document "app")))

(defn ^:export init []
  (dev-setup)
  (rf/dispatch-sync [::load-app])
  (routes/app-routes)
  (mount-root))
