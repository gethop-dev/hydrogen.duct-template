;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.tooltip.generic-popup
  (:require [re-frame.core :as rf]
            [<<namespace>>.client.tooltip :as tooltip]))

(def ^:const popup-id "generic-popup")

(def ^:const ^:private default-data
  {:modal? false
   :lightbox? true
   :argv []})

(defn main []
  (let [popup-data (rf/subscribe [::tooltip/by-id popup-id])]
    (fn []
      (when (:component @popup-data)
        (let [{:keys [modal? lightbox? component argv]} (merge default-data @popup-data)]
          [:div.generic-popup__backdrop
           (merge
            (when modal? {:on-click #(.stopPropagation %)})
            (when lightbox? {:class "generic-popup__backdrop--lightbox"}))
           [:div.generic-popup__container
            {:class (tooltip/gen-controller-class popup-id)}
            (into [component] argv)]])))))
