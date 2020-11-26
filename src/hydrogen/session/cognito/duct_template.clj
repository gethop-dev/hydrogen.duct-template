;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns hydrogen.session.cognito.duct-template
  (:require [hydrogen.session.core :as core]
            [hydrogen.utils :as utils :refer [resource ns->dir-name]]))

(def ^:private ^:const cognito-config
  ":cognito
    {:iss #duct/env [\"OIDC_ISSUER_URL\" Str]
     :client-id #duct/env [\"OIDC_AUDIENCE\" Str]}")

(defn- profile-base [project-ns]
  (let [api-config-kw (keyword (str project-ns ".api") "config")
        api-config (format core/api-config-profile-base cognito-config)]
    (assoc core/session-core-profile-base api-config-kw api-config)))

(defn profile [{:keys [project-ns profiles]}]
  {:vars {:hydrogen-session? true
          :hydrogen-session-cognito? true}
   :deps '[[duct/middleware.buddy "0.2.0"]
           [magnet/buddy-auth.jwt-oidc "0.10.0"]]
   :templates {;; Client
               "src/{{dirs}}/client/landing.cljs" (resource "session/cognito/cljs/landing.cljs")
               "src/{{dirs}}/client/session.cljs" (resource "session/cognito/cljs/session.cljs")
               "src/{{dirs}}/client/user.cljs" (resource "session/user.cljs")
               "src/{{dirs}}/client/foreign-libs/externs/cognito.js" (resource "session/cognito/cljs/foreign-libs/externs/cognito.js")
               ;; Resources
               "resources/{{dirs}}/public/css/landing.scss" (resource "session/cognito/resources/css/landing.scss")
               "resources/{{dirs}}/public/images/email-address.svg" (resource "session/cognito/resources/images/email-address.svg")
               "resources/{{dirs}}/public/images/password.svg" (resource "session/cognito/resources/images/password.svg")}
   :profile-base (profile-base project-ns)
   :modules {:hydrogen.module/core (cond->
                                    {:externs-paths
                                     {:production
                                      [(str (ns->dir-name project-ns) "/client/foreign-libs/externs/cognito.js")]}}
                                     (utils/use-figwheel-main? profiles)
                                     (assoc :figwheel-main true))}})
