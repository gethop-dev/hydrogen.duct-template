;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.util
  (:require [re-frame.core :as rf]
            [<<namespace>>.client.tooltip :as tooltip]
            [<<namespace>>.client.tooltip.loading-popup :as loading-popup]))

(defn index-by
  "Indexes a collection of maps by the provided key."
  [coll k]
  {:pre [(every? map? coll)]}
  (reduce #(assoc %1 (get %2 k) %2) {} coll))

(defn generic-error-popup [error-msg]
  [:div.generic-error-popup
   [:img {:src "images/close.svg"
          :on-click #(rf/dispatch [::tooltip/destroy-by-id "generic-popup"])}]
   [:span error-msg]])

(rf/reg-event-fx
  ::generic-success
  (fn [{:keys [db]} _]
      {:dispatch [::loading-popup/stop-loading]
       :db db}))

(rf/reg-event-fx
  ::generic-error
  (fn [{:keys [db]} [_ e]]
      {:dispatch [::loading-popup/stop-loading]
       :db (update db :errors-log (fnil conj []) e)}))
