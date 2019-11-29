;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns hydrogen.session.keycloak.duct-template
  (:require [hydrogen.utils :refer [resource ns->dir-name]]
            [hydrogen.session.core :as core]))

(def ^:private ^:const keycloak-config
  ":keycloak
    {:realm #duct/env [\"KEYCLOAK_REALM\" Str]
     :url #duct/env [\"KEYCLOAK_URL\" Str]
     :client-id #duct/env [\"KEYCLOAK_CLIENT_ID\" Str]}")

(defn- profile-base [project-ns]
  (let [api-config-kw (keyword (str project-ns ".api") "config")
        api-config (format core/api-config-profile-base keycloak-config)]
    (assoc core/session-core-profile-base api-config-kw api-config)))

(defn profile [{:keys [project-ns]}]
  {:vars {:hydrogen-session? true
          :hydrogen-session-keycloak? true}
   :deps '[[duct/middleware.buddy "0.1.0"]
           [magnet/buddy-auth.jwt-oidc "0.7.0"]]
   :templates {;; Client
               "src/{{dirs}}/client/landing.cljs" (resource "session/keycloak/cljs/landing.cljs")
               "src/{{dirs}}/client/session.cljs" (resource "session/keycloak/cljs/session.cljs")
               "src/{{dirs}}/client/foreign-libs/externs/keycloak.js" (resource "session/keycloak/cljs/foreign-libs/externs/keycloak.js")
               ;; Resources
               "resources/{{dirs}}/public/css/auth.scss" (resource "session/keycloak/resources/css/auth.scss")
               "resources/{{dirs}}/public/css/landing.scss" (resource "session/keycloak/resources/css/landing.scss")}
   :profile-base (profile-base project-ns)
   :modules {:hydrogen.module/core {:externs-paths
                                    {:production
                                     [(str (ns->dir-name project-ns) "/client/foreign-libs/externs/keycloak.js")]}}}})
