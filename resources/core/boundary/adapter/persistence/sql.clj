;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.boundary.adapter.persistence.sql
  (:require [integrant.core :as ig]
            [<<namespace>>.boundary.adapter.persistence.connector :as connector]))

(defmethod ig/init-key :<<namespace>>.boundary.adapter.persistence/sql [_ {:keys [spec]}]
  (connector/->Sql spec))
