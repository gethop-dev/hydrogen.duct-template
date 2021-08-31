;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.api.util<<#hydrogen-session?>>
  (:require [buddy.auth :refer [authenticated?]]
            [compojure.core :refer [wrap-routes]])<</hydrogen-session?>>)<<#hydrogen-session?>>

(defn- restrict-fn
  "Restrict access to the handler. Only allow access if the request
  contains a valid identity that has already been checked."
  [handler]
  (fn [req]
    (if (authenticated? req)
      (handler req)
      {:status 401
       :body {:error "Authentication required"}
       :headers {"content-type" "application/json"}})))

(defn wrap-authentication-required [handler auth-middleware]
  (-> handler
      (wrap-routes restrict-fn)
      (wrap-routes auth-middleware)))

(defn wrap-authentication [handler auth-middleware]
  (wrap-routes handler auth-middleware))<</hydrogen-session?>>
