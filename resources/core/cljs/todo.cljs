;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.todo
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [clojure.string :refer [lower-case]]
            [<<namespace>>.client.view :as view]))

(rf/reg-event-fx
 ::go-to-todo
 (fn [_ _]
   {:dispatch [::view/set-active-view :todo-list]}))

(rf/reg-sub
 ::visibility-mode
 (fn [db _]
   (get db :visibility-mode :all)))

(rf/reg-sub
 ::all-todos
 (fn [db _]
   (:todos db)))

(rf/reg-sub
 ::visible-todos
 (fn [_ _]
   [(rf/subscribe [::all-todos])
    (rf/subscribe [::visibility-mode])])
 (fn [[all-todos visibility-mode]]
   (let [all-todos (vals all-todos)]
     (case visibility-mode
       :all all-todos
       :completed (filter :checked? all-todos)
       :pending (remove :checked? all-todos)))))

(rf/reg-event-db
 ::add-todo
 (fn [db [_ {:keys [id] :as todo-data}]]
   (assoc-in db [:todos id] todo-data)))

(rf/reg-event-db
 ::delete-todo
 (fn [db [_ id]]
   (update db :todos dissoc id)))

(rf/reg-event-db
 ::toggle-todo
 (fn [db [_ id]]
   (update-in db [:todos id :checked?] not)))

(rf/reg-event-db
 ::select-visibility-mode
 (fn [db [_ mode]]
   (assoc db :visibility-mode mode)))

(defn options []
  [:div {:style {:padding "10px" :background "green"}}
   [:select {:on-change (fn [%]
                          (rf/dispatch
                           [::select-visibility-mode (keyword (lower-case (.. % -target -value)))]))}
    [:option "All"]
    [:option "Completed"]
    [:option "Pending"]]])

(defn add-new-todo [todo-content]
  (rf/dispatch [::add-todo {:content @todo-content
                            :id (random-uuid)}])
  (reset! todo-content nil)
  (.focus (.getElementById js/document "todo-content-input")))

(defn new-todo-input []
  (let [todo-content (reagent/atom nil)]
    (fn []
      [:div {:style {:padding "10px" :background "yellow"}}
       [:input {:id "todo-content-input"
                :on-change #(reset! todo-content (.. % -target -value))
                :on-key-press (fn [%]
                                (when (= (.-which %) 13)
                                  (add-new-todo todo-content)))
                :value @todo-content}]
       [:div {:style {:background "tomato" :border-radius "5px" :padding "10px" :display "inline-block"}
              :on-click #(add-new-todo todo-content)}
        "SUBMIT"]])))

(defn todo-element [{:keys [id checked? content]}]
  [:li {:style {:display :flex}}
   [:div {:style {:flex 2}} content]
   [:div {:style {:flex 1}
          :on-click #(rf/dispatch [::toggle-todo id])}
    (str (boolean checked?))]
   [:div {:on-click #(rf/dispatch [::delete-todo id])
          :style {:flex 1
                  :cursor :pointer}}
    "delete"]])

(defn legend []
  [:div {:style {:display :flex}}
   [:div {:style {:flex 2}} "content"]
   [:div {:style {:flex 1}} "completed?"]
   [:div {:style {:flex 1}} ""]])

(defn todo-list []
  (let [todos-sub (rf/subscribe [::visible-todos])]
    (fn []
      [:div {:style {:padding "10px" :background "cyan"}}
       [legend]
       (for [{:keys [id] :as todo} @todos-sub]
         ^{:key id}
         [todo-element todo])])))

(defn main []
  [:div.todo-main
   [options]
   [todo-list]
   [new-todo-input]])
