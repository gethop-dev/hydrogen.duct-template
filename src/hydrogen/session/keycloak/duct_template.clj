;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns hydrogen.session.keycloak.duct-template
  (:require [hydrogen.utils :refer [resource ns->dir-name gen-cascading-routes]]
            [hydrogen.session.core :as core]))

(def ^:private ^:const keycloak-config
  ":keycloak
    {:realm #duct/env [\"KEYCLOAK_REALM\" Str]
     :url #duct/env [\"KEYCLOAK_URL\" Str]
     :client-id #duct/env [\"KEYCLOAK_CLIENT_ID\" Str]}
   :sso-apps
    [{:name #duct/env [\"OIDC_SSO_APP_1_NAME\" Str]
      :login-url #duct/env [\"OIDC_SSO_APP_1_LOGIN_URL\" Str]
      :login-method #duct/env [\"OIDC_SSO_APP_1_LOGIN_METHOD\" Str]
      :logout-url #duct/env [\"OIDC_SSO_APP_1_LOGOUT_URL\" Str]
      :logout-method #duct/env [\"OIDC_SSO_APP_1_LOGOUT_METHOD\" Str]}]")

(defn- profile-base [project-ns]
  (let [api-config-kw (keyword (str project-ns ".api") "config")
        api-config (format core/api-config-profile-base keycloak-config)
        api-user-kw (keyword (str project-ns ".api") "user")
        api-user core/api-user-profile-base]
    (-> core/session-core-profile-base
        (assoc api-config-kw api-config)
        (assoc api-user-kw api-user))))

(defn profile [{:keys [project-ns]}]
  {:vars {:hydrogen-session? true
          :hydrogen-session-keycloak? true
          :cascading-routes (gen-cascading-routes project-ns ["static/root"
                                                              "api/config"
                                                              "api/example"
                                                              "api/user"])}
   :deps '[[duct/middleware.buddy "0.1.0"]
           [magnet/buddy-auth.jwt-oidc "0.9.0"]]
   :templates {;; Client
               "src/{{dirs}}/client/landing.cljs" (resource "session/keycloak/cljs/landing.cljs")
               "src/{{dirs}}/client/session.cljs" (resource "session/keycloak/cljs/session.cljs")
               "src/{{dirs}}/client/user.cljs" (resource "session/user.cljs")
               "src/{{dirs}}/client/foreign-libs/externs/keycloak.js" (resource "session/keycloak/cljs/foreign-libs/externs/keycloak.js")
               ;;API
               "src/{{dirs}}/api/user.clj" (resource "session/keycloak/api/user.clj")
               ;; Resources
               "resources/{{dirs}}/public/css/landing.scss" (resource "session/keycloak/resources/css/landing.scss")
               "resources/{{dirs}}/public/images/user.svg" (resource "session/keycloak/resources/images/user.svg")}
   :profile-base (profile-base project-ns)
   :modules {:hydrogen.module/core {:externs-paths
                                    {:production
                                     [(str (ns->dir-name project-ns) "/client/foreign-libs/externs/keycloak.js")]}}}})
