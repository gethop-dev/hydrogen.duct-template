;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.api.util
  (:require <<#hydrogen-session?>>[buddy.auth :refer [authenticated?]]
            <</hydrogen-session?>>[compojure.core :refer [GET POST context]]))<<#hydrogen-session?>>

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
      (compojure.core/wrap-routes restrict-fn)
      (compojure.core/wrap-routes auth-middleware)))<</hydrogen-session?>>
