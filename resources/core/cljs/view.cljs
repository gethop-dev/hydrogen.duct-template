;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.view
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::active-view
 (fn [db]
   (get db :active-view <<#hydrogen-session?>>:landing<</hydrogen-session?>><<^hydrogen-session?>>:home<</hydrogen-session?>>)))

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
