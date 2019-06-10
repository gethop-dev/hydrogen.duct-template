;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns hydrogen.session.cognito.duct-template
  (:require [hydrogen.utils :refer [resource]]))

(defn profile [_]
  {:vars {:hydrogen-session? true
          :hydrogen-session-cognito? true}
   :deps '[[duct/middleware.buddy "0.1.0"]
           [magnet/buddy-auth.jwt-oidc "0.5.0"]]
   :templates {;; Client
               "src/{{dirs}}/client/landing.cljs" (resource "cognito/cljs/landing.cljs")
               "src/{{dirs}}/client/session.cljs" (resource "cognito/cljs/session.cljs")
               ;; Resources
               "resources/{{dirs}}/public/css/landing.scss" (resource "cognito/resources/css/landing.scss")}
   :modules {:hydrogen.module.cljs/session {:add-example-api? true}}})
