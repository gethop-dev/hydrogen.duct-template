;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.view
  (:require <<#hydrogen-session-keycloak?>>[clojure.string :as str]
            <</hydrogen-session-keycloak?>>[re-frame.core :as rf]))

(rf/reg-sub
 ::active-view
 (fn [db]
   (get db :active-view)))

(rf/reg-event-db
 ::set-active-view
 (fn [db [_ active-view]]
   (assoc db :active-view active-view)))

(defn redirect! [loc]
  (set! (.-location js/window) loc))

(rf/reg-fx
 :redirect
 (fn [loc]
   (redirect! loc)))

(defmulti view-display #(when (vector? %) (first %)))

(defmethod view-display :default
  [_]
  [:div "No content :("])

(defn main
  []
  (let [active-view (rf/subscribe [::active-view])]
    (fn []
      (view-display @active-view))))<<#hydrogen-session-keycloak?>>

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
  `localhost/#/foobar&state=...&session_state=...&code=...`."
  [loc-hash]
  (when (and (str/index-of loc-hash "&state=")
             (not (str/index-of loc-hash "?")))
    (redirect! (str/replace loc-hash "&state=" "?state="))))<</hydrogen-session-keycloak?>>
