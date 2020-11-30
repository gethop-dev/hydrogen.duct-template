;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [clojure.spec.alpha :as s]
            [goog.events]
            [goog.history.EventType :as EventType]
            [re-frame.core :as rf]
            [secretary.core :as secretary]
            [<<namespace>>.client.home :as home]<<#hydrogen-session?>>
            [<<namespace>>.client.landing :as landing]
            [<<namespace>>.client.session :as session]
            [<<namespace>>.client.user :as user]<</hydrogen-session?>>
            [<<namespace>>.client.util :as util]
            [<<namespace>>.client.view :as view]))

(defn hook-browser-navigation! []
  (doto (History.)
    (goog.events/listen EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
    (.setEnabled true)))<<#hydrogen-session?>>

(def ^:const access-config-defaults
  {:allow-unauthenticated? false
   :allow-authenticated? true})

(def ^:const default-number-retries 10)

(def ^:const default-delay-time 250)

(defn config-exists? [db]
  (get db :config))

(defn- ensure-data-event-fx
  [{:keys [db session] :as cofx} _]
  {:pre [(contains? cofx :session)
         (s/valid? ::session/session-cofx-spec session)]}
  (let [jwt-token (:jwt-token session)]
    {:dispatch-n [(when (and jwt-token
                             (not (get db :user)))
                    [::user/fetch-user-data])
                  (when (and jwt-token
                             (not (get db :jwt-token)))
                    [::session/set-token-and-schedule-refresh])]}))

(rf/reg-event-fx
 ::ensure-data
 [(rf/inject-cofx :session)]
 ensure-data-event-fx)

(defn- deny-access [access-config jwt-token fx]
  (rf/console :warn "access denied"
  (clj->js {:access-config access-config
            :jwt-token jwt-token
            :fx fx}))
  fx)

(defn- go-to*-event-fx
  [{:keys [session] :as cofx} [_ evt access-config]]
  {:pre [(contains? cofx :session)
         (s/valid? ::session/session-cofx-spec session)]}
  (rf/console :log "go-to*" (clj->js {:session session
                                      :evt evt
                                      :access-config access-config}))
  (let [jwt-token (:jwt-token session)
        access-config (merge access-config-defaults access-config)]
    (cond
      (and (not (:allow-authenticated? access-config)) jwt-token)
      (deny-access access-config jwt-token {:redirect "/#/home"})

      (and (not (:allow-unauthenticated? access-config)) (not jwt-token))<<#hydrogen-session-keycloak?>>
      (deny-access access-config jwt-token {:dispatch [::session/user-login]})<</hydrogen-session-keycloak?>><<#hydrogen-session-cognito?>>
      (deny-access access-config jwt-token {:redirect "/#/landing"})<</hydrogen-session-cognito?>>

      :else
      {:dispatch-n [[::ensure-data]
                    evt]})))

(rf/reg-event-fx
 :go-to*
 [(rf/inject-cofx :session)]
 go-to*-event-fx)<<#hydrogen-session-keycloak?>>

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
  :remaining-retries - how many times this handler can be debounced until it meets
                       obligatory conditions. This is internal data and you probably don't want to touch it."
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
    :else {:dispatch [::util/generic-error ::route-access-error]}))<</hydrogen-session-keycloak?>><<#hydrogen-session-cognito?>>

(defn- go-to-handler
  "This rf event handler is responsible for making sure that
  user is eligible for accessing a view.

  For this handler to let through a config needs to exists in appdb.
  It's the only way to know if user is authenticated

  This handler accepts second, optional, parameter to tune it more:
  :allow-authenticated? - if false then it will throw an error for authenticated users
                          (`true` by default)
  :allow-unauthenticated? - if false then it will throw an error for unauthenticated users
                            (`false` by default)
  :remaining-retries - how many times this handler can be debounced until it meets
                       obligatory conditions. This is internal data and you probably don't want to touch it."
  [{:keys [db]}
   [_ evt & [{:keys [allow-authenticated? allow-unauthenticated remaining-retries]
              :or {remaining-retries default-number-retries}
              :as access-config}]]]
   (cond
     (config-exists? db) {:dispatch [:go-to* evt access-config]}
     (> remaining-retries 0) {:dispatch-later
                              [{:ms default-delay-time
                                :dispatch [:go-to evt
                                           (assoc access-config :remaining-retries (dec remaining-retries))]}]}
     :else {:dispatch [::util/generic-error ::route-access-error]}))<</hydrogen-session-cognito?>>

(rf/reg-event-fx :go-to go-to-handler)

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
