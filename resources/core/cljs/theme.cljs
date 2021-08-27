;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.theme
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::get-theme
 (fn [db _]
   (get db :theme :light)))

(rf/reg-event-db
 ::set-theme
 (fn [db [_ theme]]
   (assoc db :theme theme)))

(defn get-theme []
  (or
   @(rf/subscribe [::get-theme])
   :light))

(defn set-theme [theme]
  (rf/dispatch [::set-theme theme]))

(defn toggle-theme []
  (set-theme
   (get
    {:dark :light
     :light :dark}
    (get-theme))))
