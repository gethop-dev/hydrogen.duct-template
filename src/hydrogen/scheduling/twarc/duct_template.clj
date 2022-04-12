;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns hydrogen.scheduling.twarc.duct-template)

(def ^:private ^:const twarc-config
  "
  {:postgres-url #duct/env [\"JDBC_DATABASE_URL\" Str]
   :scheduler-name \"main-scheduler\"
   :thread-count 10
   :logger #ig/ref :duct/logger}")

(def ^:private ^:const twarc-psql-config
  "
  {:migrations-table \"ragtime_migrations_twarc\"}")

(defn profile [_]
  {:deps '[[magnet/scheduling.twarc "0.6.0"]]
   :profile-base {:magnet.scheduling/twarc twarc-config}
   :modules {:magnet.module.scheduling/twarc-pgsql twarc-psql-config}})
