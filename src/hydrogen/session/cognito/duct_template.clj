;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns hydrogen.session.cognito.duct-template
  (:require [hydrogen.utils :refer [resource]]))

(defn profile [_]
  {:vars {:hydrogen-session? true
          :hydrogen-session-cognito? true}
   :deps '[[duct/middleware.buddy "0.1.0"]
           [magnet/buddy-auth.jwt-oidc "0.6.0"]
           [hydrogen/module.session.cognito "0.1.4"]]
   :templates {;; Client
               "src/{{dirs}}/client/landing.cljs" (resource "session/cognito/cljs/landing.cljs")
               "src/{{dirs}}/client/session.cljs" (resource "session/cognito/cljs/session.cljs")
               ;; Resources
               "resources/{{dirs}}/public/css/landing.scss" (resource "session/cognito/resources/css/landing.scss")}
   :modules {:hydrogen.module/session.cognito
             "\n {:add-example-api? true
  :oidc {:issuer #duct/env [\"OIDC_ISSUER_URL\" Str]
         :audience #duct/env [\"OIDC_AUDIENCE\" Str]
         :jwks-uri #duct/env [\"OIDC_JWKS_URI\" Str]}}"}})
