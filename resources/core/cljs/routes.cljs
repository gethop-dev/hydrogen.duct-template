;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [goog.events]
            [goog.history.EventType :as EventType]
            [re-frame.core :as rf]
            [secretary.core :as secretary]
            [clojure.string :as str]
            [<<namespace>>.client.home :as home]<<#hydrogen-session?>>
            [<<namespace>>.client.landing :as landing]
            [<<namespace>>.client.session :as session]<</hydrogen-session?>>
            [<<namespace>>.client.todo :as todo]
            [<<namespace>>.client.view :as view]))<<^hydrogen-session-keycloak?>>

(defn hook-browser-navigation! []
      (doto (History.)
            (goog.events/listen
              EventType/NAVIGATE
              (fn [event]
                  (secretary/dispatch! (.-token event))))
            (.setEnabled true)))<</hydrogen-session-keycloak?>><<#hydrogen-session?>>

(def ^:const access-config-defaults
  {:allow-unauthenticated? false
   :allow-authenticated? true})

(def ^:const default-number-retries 10)

(def ^:const default-delay-time 250)

(rf/reg-event-db
  ::error
  (fn [db _]
      (assoc db :error "request timed out!")))

(defn config-exists? [db]
      (get db :config))<<#hydrogen-session-keycloak?>>

(defn- fix-callbacks
       [route]
       (cond
         (re-find #"&state=" route)
         (let [fixed-route (str/replace route "&state=" "?state=")]
              fixed-route)
         :else
         route))

(defn hook-browser-navigation! []
      (let [history (History.)]
           (doto history
                 (goog.events/listen
                   EventType/NAVIGATE
                   (fn [event]
                       (let [route (fix-callbacks (.-token event))]
                            (secretary/dispatch! route))))
                 (.setEnabled true))))

(defn- go-authenticated [evt access-config]
       (if (:allow-authenticated? access-config)
         {:dispatch evt}
         {:redirect "/#/home"}))

(defn- go-unauthenticated [evt access-config]
       (if (:allow-unauthenticated? access-config)
         {:dispatch evt}
         {:redirect "/#/landing"}))

(rf/reg-event-fx
  :go-to*
  (fn [{:keys [db]} [_ evt access-config]]
      (let [access-config (merge access-config-defaults access-config)]
           (if (:jwt-token db)
             (go-authenticated evt access-config)
             (go-unauthenticated evt access-config)))))

(rf/reg-event-fx
  :go-to
  [(rf/inject-cofx :cookie/get "KEYCLOAK_PROCESS")]
  (fn [{:keys [db cookies]}
       [_ evt & [{:keys [allow-authenticated? allow-unauthenticated remaining-retries]
                  :or {remaining-retries default-number-retries}
                  :as access-config}]]]
      (cond
        (and
          (config-exists? db)
          (not (get cookies "KEYCLOAK_PROCESS")))
        {:dispatch [:go-to* evt access-config]}
        (> remaining-retries 0)
        {:dispatch-later
         [{:ms default-delay-time
           :dispatch [:go-to evt
                      (assoc access-config :remaining-retries (dec remaining-retries))]}]}
        :else {:dispatch [::error]})))<</hydrogen-session-keycloak?>><<#hydrogen-session-cognito?>>

(defn- anyone? [access-config]
  (every? #(true? (val %)) access-config))

(defn- only-authenticated? [{:keys [allow-unauthenticated? allow-authenticated?]}]
  (and allow-authenticated? (not allow-unauthenticated?)))

(defn- only-unauthenticated? [{:keys [allow-unauthenticated? allow-authenticated?]}]
  (and (not allow-authenticated?) allow-unauthenticated?))

(rf/reg-event-fx
 :go-to*
 [(rf/inject-cofx ::session/jwt-token)]
 (fn [{:keys [db jwt-token]} [_ evt access-config]]
     (let [access-config (merge access-config-defaults access-config)]
          (merge
            {:db db}
            (cond
              (anyone? access-config) {:dispatch evt}
              (only-unauthenticated? access-config) (if jwt-token {:redirect "/#/home"} {:dispatch evt})
              (only-authenticated? access-config) (if jwt-token {:dispatch evt} {:redirect "/#/landing"}))))))

(rf/reg-event-fx
 :go-to
 (fn [{:keys [db]}
      [_ evt & [{:keys [allow-authenticated? allow-unauthenticated remaining-retries]
                 :or {remaining-retries default-number-retries}
                 :as access-config}]]]
     (cond
       (config-exists? db) {:dispatch [:go-to* evt access-config]}
     (> remaining-retries 0) {:dispatch-later
                              [{:ms default-delay-time
                                :dispatch [:go-to evt
                                           (assoc access-config :remaining-retries (dec remaining-retries))]}]}
     :else {:dispatch [::error]})))<</hydrogen-session-cognito?>>

(defn app-routes []
  (secretary/set-config! :prefix "#")
  ;; --------------------
  ;; define routes here

      (defroute "/landing" []
                (rf/dispatch [:go-to [::landing/go-to-landing]
                                    {:allow-authenticated? false :allow-unauthenticated? true}]))

      (defroute "/home" []
                (rf/dispatch [:go-to [::home/go-to-home]]))

      (defroute "/todo-list" []
                (rf/dispatch [:go-to [::todo/go-to-todo]
                                    {:allow-unauthenticated? true}]))

      (defroute "*" []
                (view/redirect! "/#/landing"))

  ;; --------------------
  (hook-browser-navigation!))<</hydrogen-session?>><<^hydrogen-session?>>

(defn app-routes []
  (secretary/set-config! :prefix "#")
  ;; --------------------
  ;; define routes here

  (defroute "/" []
    (view/redirect! "/#/home"))

  (defroute "/home" []
    (rf/dispatch [::home/go-to-home]))

  (defroute "/todo-list" []
    (rf/dispatch [::todo/go-to-todo]))

  ;; --------------------
  (hook-browser-navigation!))<</hydrogen-session?>>
