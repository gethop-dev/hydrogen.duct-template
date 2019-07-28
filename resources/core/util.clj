;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.util
  (:require [clojure.spec.alpha :as s])
  (:import [java.util UUID]
           [java.util Base64]))

(defn uuid
  ([] (UUID/randomUUID))
  ([str-uuid] (UUID/fromString str-uuid)))

(defn base64?
  "Check that `src` is a valid Base64 encoded String"
  [src]
  (and (re-matches #"[0-9a-zA-Z+/]+={0,2}" src)
       (= 0 (rem (count src) 4))))

(defn encode-base64
  "Encodes a byte[] as String using Base64"
  [src]
  (.encodeToString (Base64/getEncoder) src))

(s/fdef encode-base64
  :args (s/cat :src bytes?)
  :ret base64?)

(defn decode-base64
  "Returns a byte[] from a Base64 encoded String"
  [src]
  (.decode (Base64/getDecoder) src))

(s/fdef decode-base64
  :args (s/cat :src base64?)
  :ret bytes?)
