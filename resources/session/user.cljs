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

(rf/reg-event-fx
 ::fetch-user-data
 (fn [{:keys [db]} _]
   {:http-xhrio {:headers {"Authorization" (str "Bearer " (:jwt-token db))}
                 :method :get
                 :uri "/api/user"
                 :format (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::set-user-data]
                 :on-failure [::util/generic-error]}}))
