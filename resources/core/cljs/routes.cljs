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
            [<<namespace>>.client.home :as home]<<#hydrogen-cljs-session?>>
            [<<namespace>>.client.landing :as landing]
            [<<namespace>>.client.session :as session]<</hydrogen-cljs-session?>>
            [<<namespace>>.client.todo :as todo]
            [<<namespace>>.client.view :as view]))

(defn hook-browser-navigation! []
  (doto (History.)
    (goog.events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))<<#hydrogen-cljs-session?>>

(defn- anyone? [access-config]
  (every? #(true? (val %)) access-config))

(defn- only-authenticated? [{:keys [allow-unauthenticated? allow-authenticated?]}]
  (and allow-authenticated? (not allow-unauthenticated?)))

(defn- only-unauthenticated? [{:keys [allow-unauthenticated? allow-authenticated?]}]
  (and (not allow-authenticated?) allow-unauthenticated?))

(def ^:const access-config-defaults
  {:allow-unauthenticated? false
   :allow-authenticated? true})

(def ^:const default-number-retries 5)

(def ^:const default-delay-time 50)

(defn config-exists? [db]
  (get db :config))

(rf/reg-event-db
 ::error
 (fn [db _]
   (assoc db :error "request timed out!")))

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
 (fn [{:keys [db]} [_ evt & [{:keys [allow-authenticated? allow-unauthenticated remaining-retries]
                              :or {remaining-retries default-number-retries}
                              :as access-config}]]]
   (cond
     (config-exists? db) {:dispatch [:go-to* evt access-config]}
     (> remaining-retries 0) {:dispatch-later
                              [{:ms default-delay-time
                                :dispatch [:go-to evt
                                           (assoc access-config :remaining-retries (dec remaining-retries))]}]}
     :else {:dispatch [::error]})))

(defn app-routes []
  (secretary/set-config! :prefix "#")
  ;; --------------------
  ;; define routes here

  (defroute "/" []
    (view/redirect! "/#/landing"))

  (defroute "/landing" []
    (rf/dispatch [:go-to [::landing/go-to-landing]
                  {:allow-authenticated? false :allow-unauthenticated? true}]))

  (defroute "/home" []
    (rf/dispatch [:go-to [::home/go-to-home]]))

  (defroute "/todo-list" []
    (rf/dispatch [:go-to [::todo/go-to-todo]]))

  ;; --------------------
  (hook-browser-navigation!))<</hydrogen-cljs-session?>><<^hydrogen-cljs-session?>>

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
  (hook-browser-navigation!))<</hydrogen-cljs-session?>>
