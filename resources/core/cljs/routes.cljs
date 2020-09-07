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
            [<<namespace>>.client.util :as util]
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

(defn config-exists? [db]
  (get db :config))<<#hydrogen-session-keycloak?>>

(defn hook-browser-navigation! []
  (doto (History.)
    (goog.events/listen EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
    (.setEnabled true)))

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

(defn- go-to-handler
  "This rf event handler is responsible for making sure that
  user is eligible for accessing a view.

  Two conditions need to be met for this handler to let through:
  1) Config needs to exists in appdb.
     It's the only way to know if user is authenticated
  2) Keycloak process cannot be ongoing.
     Finishing that process is trivial so it should finish in time before
     this handler reaches its retrials limit.
     (see :init-and-authenticate effect)

  This handler accepts second, optional, parameter to tune it more:
  :allow-authenticated? - if false then it will throw an error for authenticated users
                          (`true` by default)
  :allow-unauthenticated? - if false then it will throw an error for unauthenticated users
                            (`false` by default)
  :remaining-retries - how times this handler can be debounced until it meets
                       obligatory conditions"
  [{:keys [db]}
   [_ evt & [{:keys [allow-authenticated? allow-unauthenticated remaining-retries]
              :or {remaining-retries default-number-retries}
              :as access-config}]]]
  (cond
    (and
      (config-exists? db)
      (not (session/keycloak-process-ongoing?)))
    {:dispatch [:go-to* evt access-config]}
    (> remaining-retries 0)
    {:dispatch-later
     [{:ms default-delay-time
       :dispatch [:go-to evt
                  (assoc access-config :remaining-retries (dec remaining-retries))]}]}
    :else {:dispatch [::util/generic-error ::route-access-error]}))

(rf/reg-event-fx
  :go-to
  go-to-handler)<</hydrogen-session-keycloak?>><<#hydrogen-session-cognito?>>

(defn- anyone? [{:keys [allow-unauthenticated? allow-authenticated?]}]
  (and allow-unauthenticated? allow-authenticated?))

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
     :else {:dispatch [::util/generic-error ::route-access-error]})))<</hydrogen-session-cognito?>>

(defn app-routes []
  (secretary/set-config! :prefix "#")
  ;; --------------------
  ;; define routes here

  (defroute "/landing" []
    (rf/dispatch [:go-to [::landing/go-to-landing]
                  {:allow-authenticated? false :allow-unauthenticated? true}]))

  (defroute "/home" []
    (rf/dispatch [:go-to [::home/go-to-home]]))

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

  ;; --------------------
  (hook-browser-navigation!))<</hydrogen-session?>>
