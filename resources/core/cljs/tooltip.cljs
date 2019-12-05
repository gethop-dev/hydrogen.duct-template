;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.tooltip
  (:require [re-frame.core :as rf]
            [clojure.string :as str]))

(def ^:const controller-class-prefix "js-tooltip-controller-")
(def ^:const controller-class-pattern
  (re-pattern (str controller-class-prefix "([\\w\\-]+)")))

(rf/reg-sub
 ::controls
 (fn [db _]
   (:tooltip-controls db)))

(rf/reg-sub
 ::by-id
 (fn [db _]
   (rf/subscribe [::controls]))
 (fn [tooltips-controls [_ id]]
   (get tooltips-controls id)))

(defn default-tooltip-data []
  {:id (str (random-uuid))
   :destroy-on-click-out? true})

(rf/reg-event-db
 ::register
 (fn [db [_ data]]
   (let [data (merge (default-tooltip-data) data)]
     (assoc-in db [:tooltip-controls (:id data)] data))))

(rf/reg-event-db
 ::destroy-by-id
 (fn [db [_ id]]
   (update db :tooltip-controls dissoc id)))

(rf/reg-event-db
 ::destroy-by-ids
 (fn [db [_ ids]]
   (update db :tooltip-controls
           #(apply dissoc % ids))))

(defn find-tooltip-controller-class-in-node [node]
  (some->> (.-className node)
           (re-find controller-class-pattern)
           (first)))

(defn find-tooltip-controller-class [node]
  (or (find-tooltip-controller-class-in-node node)
      (some-> (.-parentNode node) (find-tooltip-controller-class))))

(defn destroy-on-click-out [clicked-node]
  (let [clicked-controller (some->
                            (find-tooltip-controller-class clicked-node)
                            (str/split controller-class-prefix)
                            (second))
        controls-ids (->> @(rf/subscribe [::controls])
                          (vals)
                          (filter :destroy-on-click-out?)
                          (map :id)
                          (set))]
    (rf/dispatch [::destroy-by-ids (disj controls-ids clicked-controller)])))

(defn gen-controller-class [tooltip-id]
  {:pre (string? tooltip-id)}
  (str controller-class-prefix tooltip-id))
