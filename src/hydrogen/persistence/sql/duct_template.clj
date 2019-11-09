;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns hydrogen.persistence.sql.duct-template
  (:require [hydrogen.utils :refer [resource]]))

(def ^:private ^:const sql-config
  "
  {:connection-uri #duct/env [\"JDBC_DATABASE_URL\" Str]}")

(def ^:private ^:const ragtime-config
  "
  {:database #ig/ref :duct.database/sql
   :logger #ig/ref :duct/logger
   :strategy :raise-error
   :migrations
   []}")

(defn- persistence-sql-kw [project-ns]
  [(keyword (str project-ns ".boundary.adapter.persistence") "sql")
   (keyword (str project-ns ".boundary.adapter.persistence.sql") "postgres")])

(defn profile [{:keys [project-ns]}]
  {:vars {:hydrogen-persistence-sql? true}
   :deps '[[duct/module.sql "0.5.0"]
           [magnet/sql-utils "0.4.5"]
           [org.postgresql/postgresql "42.2.5"]]
   :templates {"src/{{dirs}}/boundary/adapter/persistence/connector.clj" (resource "core/boundary/adapter/persistence/connector.clj")
               "src/{{dirs}}/boundary/adapter/persistence/sql.clj" (resource "core/boundary/adapter/persistence/sql.clj")
               "src/{{dirs}}/boundary/port/persistence.clj" (resource "core/boundary/port/persistence.clj")}
   :profile-base {(persistence-sql-kw project-ns) sql-config
                  :duct.migrator/ragtime ragtime-config}
   :modules {:duct.module/sql {}}})
