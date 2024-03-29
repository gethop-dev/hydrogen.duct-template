;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns ^:figwheel-hooks <<namespace>>.client
  (:require <<#hydrogen-session?>>[ajax.core :as ajax]
            <</hydrogen-session?>><<#hydrogen-ssr?>>
            [cognitect.transit :as transit]<</hydrogen-ssr?>>[day8.re-frame.http-fx]
            [re-frame.core :as rf]
            [reagent.dom :as rd]
            [<<namespace>>.client.breadcrumbs :as breadcrumbs]<<#hydrogen-session?>>
            [<<namespace>>.client.landing :as landing]<</hydrogen-session?>>
            [<<namespace>>.client.routes :as routes]<<#hydrogen-session-keycloak?>>
            [<<namespace>>.client.session]<</hydrogen-session-keycloak?>>
            [<<namespace>>.client.sidebar :as sidebar]
            [<<namespace>>.client.theme :as theme]
            [<<namespace>>.client.tooltip :as tooltip]
            [<<namespace>>.client.tooltip.generic-popup :as tooltip.generic-popup]
            [<<namespace>>.client.tooltip.loading-popup :as tooltip.loading-popup]<<#hydrogen-session?>>
            [<<namespace>>.client.util :as util]<</hydrogen-session?>>
            [<<namespace>>.client.view :as view]))

(def default-db
  {:theme :light})<<#hydrogen-ssr?>>

(rf/reg-cofx
 ::initial-app-db
 (fn [cofx _]
   (let [initial-app-db js/INITIAL_APP_DB
         transit-reader (transit/reader :json)]
     (assoc cofx :initial-app-db (if initial-app-db
                                   (transit/read transit-reader initial-app-db)
                                   default-db)))))<</hydrogen-ssr?>><<^hydrogen-ssr?>>

(rf/reg-cofx
 ::initial-app-db
 (fn [cofx _]
   (assoc cofx :initial-app-db default-db)))<</hydrogen-ssr?>><<#hydrogen-session-cognito?>>

(rf/reg-event-db
 ::set-config
 (fn [db [_ config]]
   (assoc db :config config)))<</hydrogen-session-cognito?>><<#hydrogen-session-keycloak?>>

(rf/reg-event-fx
 ::set-config
 (fn [{:keys [db]} [_ config]]
   {:db (assoc db :config config)
    :init-and-try-to-authenticate config}))<</hydrogen-session-keycloak?>>

(rf/reg-event-fx
 ::load-app
 [(rf/inject-cofx ::initial-app-db)]
 (fn [{:keys [initial-app-db]} _]
   {:db initial-app-db<<#hydrogen-session?>>
    :http-xhrio {:method :get
                 :uri "/api/config"
                 :format (ajax/json-request-format)
                 :response-format (ajax/transit-response-format)
                 :on-success [::set-config]
                 :on-failure [::util/generic-error]}<</hydrogen-session?>>}))

(defn app []
  (let [theme (rf/subscribe [::theme/get-theme])]
    (fn []
      [:div.app-container
       {:on-click #(tooltip/destroy-on-click-out (.. % -target))
        :class (str "theme-" (name @theme))}
       [sidebar/main]
       [:div.app-container__main
        {:id "app-container__main"}
        [breadcrumbs/main]
        [view/main]]
       [tooltip.loading-popup/main]
       [tooltip.generic-popup/main]])))

(defn main []<<#hydrogen-session?>>
  (let [active-view (rf/subscribe [::view/active-view])]
    (fn []
      (if (= @active-view ::landing/view)
        [landing/main]
        [app])))<</hydrogen-session?>><<^hydrogen-session?>>
  [app]<</hydrogen-session?>>)

;; Make log level logs no-ops for production environment.
(rf/set-loggers! {:log (fn [& _])})

(defn dev-setup []
  (when goog.DEBUG
    ;; Reenable log level logs no-ops for dev environment.
    (rf/set-loggers! {:log js/console.log})
    (enable-console-print!)
    (println "Dev mode")))

(defn mount-root []
  (rf/clear-subscription-cache!)
  (rd/render [main] (.getElementById js/document "app")))

(defn ^:after-load re-render []
  (mount-root))

(defn ^:export init []
  (dev-setup)
  (rf/dispatch-sync [::load-app])<<#hydrogen-session-keycloak?>>
  (navigation/fix-query-params js/location.hash)<</hydrogen-session-keycloak?>>
  (routes/app-routes)
  (mount-root))
