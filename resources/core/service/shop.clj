;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.service.shop)

(def FAKE-REGISTRY
  {"111-111-111" {:id "111-111-111"
                  :name "Apple"
                  :tasty? true}
   "222-222-222" {:id "222-222-222"
                  :name "Orange"}})

(def fallback-shop-item
  {:name "Unknown"})

(defn get-shop-item
  [id]
  (get FAKE-REGISTRY id fallback-shop-item))
