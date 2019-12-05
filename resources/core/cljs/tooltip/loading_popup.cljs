;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.tooltip.loading-popup
  (:require [re-frame.core :as rf]
            [<<namespace>>.client.tooltip :as tooltip]))

(def ^:const popup-id "loading-popup")

(rf/reg-sub
 ::loading
 (fn [db]
   (:loading db)))

(rf/reg-event-fx
 ::set-loading
 (fn [{:keys [db]} [_ message]]
   {:db (assoc db :loading {:message message})
    :dispatch [::tooltip/register {:id popup-id}]}))

(rf/reg-event-fx
 ::stop-loading
 (fn [{:keys [db]} _]
   {:db (dissoc db :loading)
    :dispatch [::tooltip/destroy-by-id popup-id]}))

(defn main []
  (let [loading (rf/subscribe [::loading])
        popup-data (rf/subscribe [::tooltip/by-id popup-id])]
    (fn []
      (when @popup-data
        [:div.loading-popup__backdrop
         {:class "loading-popup__backdrop--lightbox"
          :on-click #(.stopPropagation %)}
         [:div.loading-popup__container
          {:class (tooltip/gen-controller-class popup-id)}
          [:img.loading-popup__spinner {:src "images/spinner.svg"}]
          [:span (:message @loading)]]]))))
