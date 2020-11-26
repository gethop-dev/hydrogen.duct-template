;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.api.example
  (:require [compojure.core :refer [context GET]]
            [integrant.core :as ig]<<#hydrogen-session?>>
            [<<namespace>>.api.util :as util]<</hydrogen-session?>>))

(defmethod ig/init-key :<<namespace>>.api/example [_ <<#hydrogen-session?>>{:keys [auth-middleware]}<</hydrogen-session?>><<^hydrogen-session?>>_<</hydrogen-session?>>]
  (context "/api/example" []
    (GET "/" []
      {:status 200
       :body {:msg "Welcome!"}
       :headers {"content-type" "application/json"}})<<#hydrogen-session?>>
    (->
     (GET "/for-authenticated" []
       {:status 200
        :body {:msg "Hello again!"}
        :headers {"content-type" "application/json"}})
     (util/wrap-authentication-required auth-middleware))<</hydrogen-session?>>))
