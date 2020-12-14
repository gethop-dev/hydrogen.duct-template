;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.user
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf]
            [<<namespace>>.client.util :as util]))

(rf/reg-sub
 ::user-data
 (fn [db _]
   (get db :user)))

(rf/reg-event-db
 ::set-user-data
 (fn [db [_ user]]
   (assoc db :user user)))

(defn- fetch-user-data-event-fx
  "This event handler gets jwt-token from session cofx instead of appdb.
  It is so because at times the token may not be present in appdb yet when
  ::ensure-data is called."
  [{:keys [session] :as cofx} _]
  {:pre [(contains? cofx :session)
         (s/valid? ::session/session-cofx-spec session)]}
  {:http-xhrio {:headers {"Authorization" (str "Bearer " (:jwt-token session))}
                :method :get
                :uri "/api/user"
                :format (ajax/json-request-format)
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success [::set-user-data]
                :on-failure [::util/generic-error]}})

(rf/reg-event-fx
  ::fetch-user-data
  [(rf/inject-cofx :session)]
  fetch-user-data-event-fx)
