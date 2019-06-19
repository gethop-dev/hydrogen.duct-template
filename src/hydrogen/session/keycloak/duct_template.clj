;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns hydrogen.session.keycloak.duct-template
  (:require [hydrogen.utils :refer [resource]]))

(defn profile [_]
  {:vars {:hydrogen-session? true
          :hydrogen-session-keycloak? true}
   :deps '[[duct/middleware.buddy "0.1.0"]
           [magnet/buddy-auth.jwt-oidc "0.6.0"]
           [hydrogen/module.session.keycloak "0.1.1"]]
   :templates {;; Client
               "src/{{dirs}}/client/cookie.cljs" (resource "session/keycloak/cljs/cookie.cljs")
               "src/{{dirs}}/client/landing.cljs" (resource "session/keycloak/cljs/landing.cljs")
               "src/{{dirs}}/client/session.cljs" (resource "session/keycloak/cljs/session.cljs")
               ;; Resources
               "resources/{{dirs}}/public/css/auth.scss" (resource "session/keycloak/resources/css/auth.scss")
               "resources/{{dirs}}/public/css/landing.scss" (resource "session/keycloak/resources/css/landing.scss")}
   :modules {:hydrogen.module/session.keycloak {:add-example-api? true}}})
