;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.navigation
  (:require <<#hydrogen-ssr?>>[pushy.core :as pushy]
            [secretary.core :as secretary]
            <</hydrogen-ssr?>><<#hydrogen-session-keycloak?>>[clojure.string :as str]
            <</hydrogen-session-keycloak?>>[re-frame.core :as rf]))<<#hydrogen-ssr?>>

(def history (pushy/pushy secretary/dispatch!
                          (fn [x] (when (secretary/locate-route x) x))))

(defn redirect! [path]
  (pushy/set-token! history path))

(defn start!
  "Start navigation's event listeners (to process links and such)"
  []
  (pushy/start! history))<</hydrogen-ssr?>><<^hydrogen-ssr?>>

(defn redirect! [loc]
  (set! (.-location js/window) loc))<</hydrogen-ssr?>>

(rf/reg-fx
  :redirect
  (fn [loc]
    (redirect! loc)))<<#hydrogen-session-keycloak?>>

(defn remove-query-param [loc-hash param]
  (let [[path query-params] (str/split loc-hash #"\?")]
    (if query-params
      (->>
        (str/split query-params #"\&")
        (remove
          #(str/starts-with? % (str (name param) "=")))
        (str/join "&")
        (conj [path])
        (filter seq)
        (str/join "?"))
      path)))

(defn get-query-param [loc-hash param]
  (let [[_ query-params] (str/split loc-hash #"\?")]
    (when query-params
      (some->
        (some
          #(when (str/starts-with? % (str (name param) "=")) %)
          (str/split query-params #"\&"))
        (str/split #"\=")
        (second)))))

(defn fix-query-params
  "This function makes sure that query params list in location url
  starts with a question mark instead of an ampersand.

  It assumes that the only scenario when this happens is when
  keycloak appends its params necessary for the auth process
  and when the first query param doesn't come with a ? instead of &:
  `/foobar&state=...&session_state=...&code=...`."
  [loc-hash]
  (when (and (str/index-of loc-hash "&state=")
             (not (str/index-of loc-hash "?")))
    (redirect! (str/replace loc-hash "&state=" "?state="))))<</hydrogen-session-keycloak?>>
