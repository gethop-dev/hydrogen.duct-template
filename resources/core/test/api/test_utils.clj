;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.api.test-utils
  (:require  [clojure.java.io :as io]
             [duct.core :as duct]
             [integrant.core :as ig]))

(defn authfn
  [_req token]
  token)

(def auth-middleware
  {:backend :token
   :token-name "Bearer"
   :authfn authfn})

(defn get-config []
  (duct/load-hierarchy)
  (->
   (io/resource "<<namespace>>/config.edn")
   (duct/read-config)
   (duct/prep-config)
   (assoc :duct.middleware.buddy/authentication auth-middleware)))

(defn get-adapters
  [system]
  {:logger (-> system
               (ig/find-derived-1 :duct/logger)
               ;; find-derived-1 returns a [k v] value, where k is
               ;; the key we asked for, and v is its value.
               second)
   :p-adapter (get system
                   [:<<namespace>>.boundary.adapter.persistence/sql
                    :<<namespace>>.boundary.adapter.persistence.sql/postgres])})

