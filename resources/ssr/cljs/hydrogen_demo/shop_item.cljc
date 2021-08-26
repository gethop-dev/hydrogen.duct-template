;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.hydrogen-demo.shop-item
  (:require [re-frame.core :as rf]
            [<<namespace>>.client.breadcrumbs :as breadcrumbs]
            [<<namespace>>.client.view :as view]))

(rf/reg-event-fx
 ::view.enter
 (fn [{:keys [db]} [_ item-id]]
   {:dispatch-n [[::view/set-active-view [::view item-id]]
                 [::breadcrumbs/set [{:title "Home" :url "/home"}
                                     {:title "Shop" :url "/shop"}
                                     {:title "Apple" :url (str "/" item-id) :disabled true}]]]
    :db (assoc db :shop-item {:name "Apple"
                              :tasty? true})
    :redirect (str "/shop/" item-id)}))

(rf/reg-event-fx
 ::view.leave
 (fn [{:keys [db]} _]
   {:db (dissoc db :shop-item)}))

(defmethod view/view-display ::view
  [[_ item-id]]
  [:div (str "I'm a shop item " item-id)])
