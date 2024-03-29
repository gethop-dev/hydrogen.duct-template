;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns hydrogen.persistence.sql.duct-template
  (:require [hydrogen.utils :refer [resource]]))

(def ^:private ^:const sql-config
  "
  #ig/ref :duct.database/sql")

(def ^:private hikaricp-config
  "
  {:jdbc-url #duct/env [\"JDBC_DATABASE_URL\" Str]
   :logger nil ;; We don't want log entries for sql sentences (not event ragtime ones!)
   :minimum-idle 10
   :maximum-pool-size 25}")

(def ^:private ragtime-config
  "
  {:database #ig/ref :duct.database/sql
   :logger #ig/ref :duct/logger
   :strategy :raise-error
   :migrations-table \"ragtime_migrations\"
   :migrations []}")

(defn- dev-ragtime-config
  [project-ns]
  (format
   "
  {:database #ig/ref :duct.database/sql
   :logger #ig/ref :duct/logger
   :strategy :raise-error
   :migrations-table \"ragtime_migrations_dev\"
   :fake-dependency-to-force-initialization-order #ig/ref [:duct.migrator/ragtime :%s/prod]
   :migrations []}"
   project-ns))

(defn- persistence-sql-kw
  [project-ns]
  [(keyword (str project-ns ".boundary.adapter.persistence") "sql")
   (keyword (str project-ns ".boundary.adapter.persistence.sql") "postgres")])

(defn profile [{:keys [project-ns]}]
  {:vars {:hydrogen-persistence-sql? true}
   :deps '[[duct/module.sql "0.6.1"]
           [dev.gethop/sql-utils "0.4.13"]
           [org.postgresql/postgresql "42.3.3"]]
   :dirs ["dev/resources/{{dirs}}/dev_migrations"
          "resources/{{dirs}}/migrations"]
   :templates {"src/{{dirs}}/boundary/adapter/persistence/connector.clj" (resource "core/boundary/adapter/persistence/connector.clj")
               "src/{{dirs}}/boundary/adapter/persistence/sql.clj" (resource "core/boundary/adapter/persistence/sql.clj")
               "src/{{dirs}}/boundary/port/persistence.clj" (resource "core/boundary/port/persistence.clj")}
   :profile-base {(persistence-sql-kw project-ns) sql-config
                  :duct.database.sql/hikaricp hikaricp-config
                  [:duct.migrator/ragtime (keyword (str project-ns "/prod"))] ragtime-config}
   :profile-dev {[:duct.migrator/ragtime (keyword (str project-ns "/dev"))] (dev-ragtime-config project-ns)}})
