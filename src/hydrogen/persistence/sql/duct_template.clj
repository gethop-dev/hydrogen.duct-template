;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns hydrogen.persistence.sql.duct-template
  (:require [hydrogen.utils :refer [resource]]))

(def ^:private ^:const sql-config
  "
  {:connection-uri #duct/env [\"JDBC_DATABASE_URL\" Str]}")

(defn- ragtime-config
  [project-ns]
  (format
    "
  {:database #ig/ref :duct.database/sql
   :logger #ig/ref :duct/logger
   :strategy :raise-error
   :migrations [#ig/ref :%s.migrations/prod]}"
    project-ns))

(defn- dev-ragtime-config
  [project-ns]
  (format
    "
 {:migrations ^:replace [#ig/ref :%s.migrations/prod
                         #ig/ref :%s.migrations/dev]}"
    project-ns
    project-ns))

(defn- persistence-sql-kw
  [project-ns]
  [(keyword (str project-ns ".boundary.adapter.persistence") "sql")
   (keyword (str project-ns ".boundary.adapter.persistence.sql") "postgres")])

(defn profile [{:keys [project-ns]}]
  {:vars {:hydrogen-persistence-sql? true}
   :deps '[[duct/module.sql "0.6.1"]
           [magnet/sql-utils "0.4.11"]
           [org.postgresql/postgresql "42.2.16"]]
   :templates {"src/{{dirs}}/boundary/adapter/persistence/connector.clj" (resource "core/boundary/adapter/persistence/connector.clj")
               "src/{{dirs}}/boundary/adapter/persistence/sql.clj" (resource "core/boundary/adapter/persistence/sql.clj")
               "src/{{dirs}}/boundary/port/persistence.clj" (resource "core/boundary/port/persistence.clj")}
   :profile-base {(persistence-sql-kw project-ns) sql-config

                  :duct.migrator/ragtime (ragtime-config project-ns)

                  [:duct.migrator.ragtime/resources (keyword (str project-ns ".migrations") "prod")]
                  {:path (format "%s/migrations" project-ns)}}
   :profile-dev {:duct.migrator/ragtime (dev-ragtime-config project-ns)

                 [:duct.migrator.ragtime/resources (keyword (str project-ns ".migrations") "dev")]
                 {:path (format "%s/dev_migrations" project-ns)}}
   :modules {:duct.module/sql {}}})
