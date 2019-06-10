;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.api.example
  (:require [compojure.core :refer [context GET]]
            [integrant.core :as ig]
            [<<namespace>>.api.util :as util]))

(defmethod ig/init-key :<<namespace>>.api/example [_ {:keys [<<#hydrogen-cljs-session?>>auth-middleware<</hydrogen-cljs-session?>>] :as options}]
  (context "/api/example" []
    (GET "/" []
      {:status 200
       :body {:msg "Welcome!"}
       :headers {"content-type" "application/json"}})<<#hydrogen-cljs-session?>>
    (->
     (GET "/for-authenticated" []
       {:status 200
        :body {:msg "Hello again!"}
        :headers {"content-type" "application/json"}})
     (util/wrap-authentication-required auth-middleware))<</hydrogen-cljs-session?>>))
