;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

;; Borrowed from https://gist.github.com/mtnygard/9e6a3c5a107eed02f7616393cbb124b7
;; Michael Nygard

{{=<< >>=}}
(ns <<namespace>>.api.user
  (:require [compojure.core :refer [context GET POST ANY context wrap-routes routes]]
            [integrant.core :as ig]
            [<<namespace>>.api.util :as api-util]
            [<<namespace>>.util :as util]))

(defn- get-dummy-user-data
  [req]
  (let [user-id (-> req :identity util/uuid)]
    {:status 200
     :headers {"content-type" "application/json"}
     :body {:avatar "https://www.w3schools.com/w3images/avatar2.png"
            :first-name "John"
            :last-name "Doe"}}))

(defmethod ig/init-key :<<namespace>>.api/user [_ {:keys [auth-middleware] :as config}]
  (context "/api/user" []
    (->
      (routes
        (GET "/" req
             (get-dummy-user-data req)))
      (api-util/wrap-authentication-required auth-middleware))))
