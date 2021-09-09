;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require <<#hydrogen-session?>>[clojure.spec.alpha :as s]
            <</hydrogen-session?>>[goog.events]
            [goog.history.EventType :as EventType]
            [re-frame.core :as rf]
            [secretary.core :as secretary]
            [<<namespace>>.client.home :as home]
            [<<namespace>>.client.hydrogen-demo.shop :as hydrogen-demo.shop]
            [<<namespace>>.client.navigation :as navigation]
            [<<namespace>>.client.hydrogen-demo.shop-item :as hydrogen-demo.shop-item]<<#hydrogen-session?>>
            [<<namespace>>.client.landing :as landing]
            [<<namespace>>.client.session :as session]
            [<<namespace>>.client.user :as user]
            [<<namespace>>.client.util :as util]<</hydrogen-session?>>))

(defn- compose-nav-evt
       [direction [view-key & argv]]
       {:pre [(#{:enter :leave} direction)]}
       (let [evt-key (if (namespace view-key)
                       (keyword
                         (namespace view-key)
                         (str (name view-key) "." (name direction)))
                       (keyword
                         (str (name view-key) "." (name direction))))]
            (vec (cons evt-key argv))))<<#hydrogen-session?>>

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
                   #js {:access-config access-config
                        :jwt-token jwt-token
                        :fx fx})
       fx)

(defn- go-to*-event-fx
       [{:keys [db session] :as cofx} [_ new-view access-config]]
       {:pre [(contains? cofx :session)
              (s/valid? ::session/session-cofx-spec session)]}
       (rf/console :log "go-to*" #js {:session session
                                      :new-view new-view
                                      :access-config access-config})
       (let [jwt-token (:jwt-token session)
             access-config (merge access-config-defaults access-config)]
            (cond
              (and (not (:allow-authenticated? access-config)) jwt-token)
              (deny-access access-config jwt-token {:redirect "/home"})

              (and (not (:allow-unauthenticated? access-config)) (not jwt-token))<<#hydrogen-session-keycloak?>>
              (deny-access access-config jwt-token {:dispatch [::session/user-login]})<</hydrogen-session-keycloak?>><<#hydrogen-session-cognito?>>
              (deny-access access-config jwt-token {:redirect "/landing"})<</hydrogen-session-cognito?>>

              :else
              ; This part takes a vector for new view to navigate to.
              ; That vector has min. arity 1 - a keyword identifying a view.
              ; By convention we recommend using a namespaced one with 'view' as a name (e.g. ::shop/view).
              ; The optional remaining arguments of that view vector are arguments to the view.
              ;
              ; Then this handler composes up to two event handlers and dispatches them:
              ; - New view always is composed into ::*/view.enter event
              ; - Previously active view (if present) is composed into ::*/view.leave event
              ;
              ; That said, each namespace introducing a new view need to have both ::view.enter and ::view.leave
              ; events defined.
              (let [enter-evt (compose-nav-evt :enter new-view)
                    set-active-view-event nil
                    leave-evt (when-let [active-view (:active-view db)]
                                        (compose-nav-evt :leave active-view))]
                   {:dispatch-n [[::ensure-data]
                                 leave-evt
                                 enter-evt]}))))

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
        [_ evt & [{:keys [remaining-retries]
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

         :else
         {:dispatch [::util/generic-error ::route-access-error]}))<</hydrogen-session-keycloak?>><<#hydrogen-session-cognito?>>

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
        [_ evt & [{:keys [remaining-retries]
                   :or {remaining-retries default-number-retries}
                   :as access-config}]]]
       (cond
         (config-exists? db)
         {:dispatch [:go-to* evt access-config]}

         (> remaining-retries 0)
         {:dispatch-later
          [{:ms default-delay-time
            :dispatch [:go-to evt
                       (assoc access-config :remaining-retries (dec remaining-retries))]}]}

         :else
         {:dispatch [::util/generic-error ::route-access-error]}))<</hydrogen-session-cognito?>>

(rf/reg-event-fx :go-to go-to-handler)

(defn app-routes []
      ;; NOTE: this routes registry needs to be a ONE TO ONE matching in regards to :<<namespace>>.ssr/root routes
      (secretary/set-config! :prefix "#")
      ;; --------------------
      ;; define routes here

      (defroute "/landing" []
                (rf/dispatch [:go-to [::landing/view]
                              {:allow-authenticated? false :allow-unauthenticated? true}]))

      (defroute "/home" []
                (rf/dispatch [:go-to [::home/view]]))

      (defroute "/shop" []
                (rf/dispatch [:go-to [::hydrogen-demo.shop/view]]))

      (defroute "/shop/:item-id" [item-id]
                (rf/dispatch [:go-to [::hydrogen-demo.shop-item/view item-id]]))

      (defroute "*" []
                (navigation/redirect! "/landing"))

      ;; --------------------
      (navigation/start!))<</hydrogen-session?>><<^hydrogen-session?>>

(defn- go-to-handler
       "This handler takes a vector for new view to navigate to.
       That vector has min. arity 1 - a keyword identifying a view.
       By convention we recommend using a namespaced one with 'view' as a name (e.g. ::shop/view).
       The optional remaining arguments of that view vector are arguments to the view.

       Then this handler composes up to two event handlers and dispatches them:
       - New view always is composed into ::*/view.enter event
       - Previously active view (if present) is composed into ::*/view.leave event

       That said, each namespace introducing a new view need to have both ::view.enter and ::view.leave
       events defined."
       [{:keys [db]} [_ new-view]]
       (let [enter-evt (compose-nav-evt :enter new-view)
             leave-evt (when-let [active-view (:active-view db)]
                                 (compose-nav-evt :leave active-view))]
            {:dispatch-n [leave-evt
                          enter-evt]}))

(rf/reg-event-fx :go-to go-to-handler)

(defn app-routes []
      ;; NOTE: this routes registry needs to be a ONE TO ONE matching in regards to :<<namespace>>.ssr/root routes
      (secretary/set-config! :prefix "#")
      ;; --------------------
      ;; define routes here

      (defroute "/home" []
                (rf/dispatch [:go-to [::home/view]]))

      (defroute "/shop" []
                (rf/dispatch [:go-to [::hydrogen-demo.shop/view]]))

      (defroute "/shop/:item-id" [item-id]
                (rf/dispatch [:go-to [::hydrogen-demo.shop-item/view item-id]]))

      (defroute "*" []
                (navigation/redirect! "/home"))

      ;; --------------------
      (navigation/start!))<</hydrogen-session?>>
