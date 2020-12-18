;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.breadcrumbs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::breadcrumbs
 (fn [db _]
   (get db :breadcrumbs)))

(rf/reg-event-db
 ::set
 (fn [db [_ breadcrumbs]]
   (assoc db :breadcrumbs breadcrumbs)))

(defn main []
  (let [breadcrumbs (rf/subscribe [::breadcrumbs])]
    (fn []
      (when @breadcrumbs
        [:div.breadcrumbs
         (for [{:keys [title url disabled]} @breadcrumbs]
           ^{:key (gensym url)}
           [(if-not disabled :a :span)
            {:href (when-not disabled url)
             :class "breadcrumb"}
            title])]))))
