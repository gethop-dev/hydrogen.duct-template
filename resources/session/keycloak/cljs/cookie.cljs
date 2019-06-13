;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.cookie
  "This namespace is provides re-frame utilities to
  get, set and remove cookies."
  (:require [goog.net.cookies]
            [re-frame.core :as rf]))

(rf/reg-cofx
 :cookie/get
 (fn [cofx cname]
   (assoc-in cofx [:cookies cname]
             (.get goog.net.cookies cname))))

(rf/reg-fx
 :cookie/set
 (fn [[cname v & {:keys [max-age]
                  :or {max-age -1}}]]
   (.set goog.net.cookies cname v max-age)))

(rf/reg-fx
 :cookie/remove
 (fn [cname]
   (.remove goog.net.cookies cname)))
