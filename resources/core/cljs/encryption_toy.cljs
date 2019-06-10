;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.encryption-toy
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [<<namespace>>.client.view :as view]))

(rf/reg-event-fx
 ::go-to-encryption-toy
 (fn [_ _]
   {:dispatch [::view/set-active-view :encryption-toy]}))

(rf/reg-event-fx
 ::encrypt-message
 (fn [{:keys [db]} [_ message]]
   {:http-xhrio
    {:method :get
     :headers {"Authorization" (str "Bearer " (:token db))}
     :uri (str "/api/encryption/encrypt")
     :params {:message message}
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success [::add-encryption-result]
        ;; TODO add on-failure
     }
    :db (assoc db :encryption-result :loading)}))

(rf/reg-event-fx
 ::decrypt-message
 (fn [{:keys [db]} [_ message]]
   {:http-xhrio
    {:method :get
     :headers {"Authorization" (str "Bearer " (:token db))}
     :uri (str "/api/encryption/decrypt")
     :params {:message message}
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success [::add-decryption-result]
     :on-failure [::fail-decryption]}
    :db (assoc db :decryption-result :loading)}))

(rf/reg-event-db
 ::add-encryption-result
 (fn [db [_ {:keys [enc-msg]}]]
   (assoc db :encryption-result enc-msg)))

(rf/reg-event-db
 ::add-decryption-result
 (fn [db [_ {:keys [msg]}]]
   (assoc db :decryption-result msg)))

(rf/reg-event-db
 ::fail-decryption
 (fn [db _]
   (assoc db :decryption-result :error)))

(rf/reg-sub
 ::encryption-result
 (fn [db _]
   (get db :encryption-result)))

(rf/reg-sub
 ::decryption-result
 (fn [db _]
   (get db :decryption-result)))

(defn encrypt-controls []
  (let [input-message (reagent/atom nil)]
    (fn []
      [:div
       [:input
        {:placeholder "Put your message here"
         :on-change #(reset! input-message (.. % -target -value))
         :on-key-press #(when (= (.-key %) "Enter")
                          (rf/dispatch [::encrypt-message @input-message]))}]
       [:button
        {:on-click #(rf/dispatch [::encrypt-message @input-message])}
        "ENCRYPT!"]])))

(defn encrypted-msg [enc-msg]
  [:p enc-msg [:button {:on-click
                        (fn []
                          (js/navigator.clipboard.writeText enc-msg)
                          (.focus (js/document.getElementById "decrypt-msg-input")))} "COPY"]])

(defn encrypt-results []
  (let [results-sub (rf/subscribe [::encryption-result])]
    (fn []
      (cond
        (= @results-sub :loading)
        [:p "Loading..."]
        (seq @results-sub)
        [encrypted-msg @results-sub]
        :else [:p "Go ahead! Try it!"]))))

(defn encrypt-component []
  [:div
   [encrypt-controls]
   [encrypt-results]])

(defn decrypt-controls []
  (let [input-message (reagent/atom nil)]
    (fn []
      [:div
       [:input
        {:id "decrypt-msg-input"
         :placeholder "Put your encrypted message here"
         :on-change #(reset! input-message (.. % -target -value))
         :on-key-press #(when (= (.-key %) "Enter")
                          (rf/dispatch [::decrypt-message @input-message]))}]
       [:button
        {:on-click #(rf/dispatch [::decrypt-message @input-message])}
        "RUN!"]])))

(defn decrypted-msg [msg]
  [:p msg])

(defn decrypt-results []
  (let [results-sub (rf/subscribe [::decryption-result])]
    (fn []
      (cond
        (= @results-sub :loading)
        [:p "Loading..."]
        (= @results-sub :error)
        [:p "Uh oh, something went wrong. Are you sure you copied the encrypted message right?"]
        (seq @results-sub)
        [decrypted-msg @results-sub]
        :else [:p "Go ahead! Try it!"]))))

(defn decrypt-component []
  [:div
   [decrypt-controls]
   [decrypt-results]])

(defn main []
  [:div
   [encrypt-component]
   [decrypt-component]])

