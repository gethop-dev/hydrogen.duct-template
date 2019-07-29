;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns hydrogen.duct-template
  (:require [hydrogen.core.duct-template :as core]
            [hydrogen.session.cognito.duct-template :as cognito]
            [hydrogen.session.keycloak.duct-template :as keycloak]
            [hydrogen.persistence.sql.duct-template :as sql]))

(def core-profile core/profile)
(def session.cognito-profile cognito/profile)
(def session.keycloak-profile keycloak/profile)
(def persistence.sql-profile sql/profile)
