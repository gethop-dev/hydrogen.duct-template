;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.hydrogen-demo.shop
  (:require [re-frame.core :as rf]
            [<<namespace>>.client.breadcrumbs :as breadcrumbs]
            [<<namespace>>.client.tooltip :as tooltip]
            [<<namespace>>.client.tooltip.loading-popup :as loading-popup]
            [<<namespace>>.client.view :as view]))

(rf/reg-event-fx
 ::view.enter
 (fn [{:keys [db]} _]
   {:dispatch-n [[::view/set-active-view [::view]]
                 [::breadcrumbs/set [{:title "Home" :url "/#/home"}
                                     {:title "Shop" :url "/#/shop" :disabled true}]]]
    ;; This db effect would normally be replaced by http req with an `:on-success` that would set appdb state
    :db (assoc db :shop {:items [:apple :orange :banana]})
    :redirect "/#/shop"}))

(rf/reg-event-fx
 ::view.leave
 (fn [{:keys [db]} _]
   {:db (dissoc db :shop)}))

(rf/reg-event-fx
 ::demo-request
 (fn [_ _]
   {:dispatch [::loading-popup/set-loading "Faking loading. I'll disappear after 5 seconds..."]
    ;; This http-xhrio should be there instead of the dispatch-later in real situation.
    ; http-xhrio {...}
    :dispatch-later [{:ms 5000
                      :dispatch [::loading-popup/stop-loading]}]}))

(defn- demo-modal-tooltip-component []
  [:div
   "Hello! I'm a demo modal tooltip. You can only close me via 'x' button."
   [:a.u-clickable
    {:on-click #(rf/dispatch [::tooltip/destroy-by-id "generic-popup"])} "x"]])

(defn- demo-modal-tooltip []
  [:button.btn
   {:on-click #(rf/dispatch [::tooltip/register {:id "generic-popup"
                                                 :component demo-modal-tooltip-component
                                                 :modal? true}])}
   "Demo modal tooltip"])

(defn- demo-tooltip-component []
  [:div "Hello! I'm a demo tooltip. You can only close me by clicking outside of me."])

(defn- demo-tooltip []
  [:button.btn
   {:on-click #(rf/dispatch [::tooltip/register {:id "generic-popup"
                                                 :component demo-tooltip-component}])}
   "Demo tooltip"])

(defn- demo-loading []
  [:button.btn {:on-click #(rf/dispatch [::demo-request])}
   "Pretend you are requesting something from the Internet"])

(defmethod view/view-display ::view
  [_]
  [:div
   [:p "I'm a shop!"]
   [demo-modal-tooltip]
   [demo-tooltip]
   [demo-loading]])
