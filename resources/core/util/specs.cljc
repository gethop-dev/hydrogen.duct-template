;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.util.specs
  (:require #?(:cljs [cljs.spec.alpha :as s]
               :clj [clojure.spec.alpha :as s]))
  #?(:clj (:import [java.net URL])))

(s/def ::uuid uuid?)

(def ^:private ^:const uuid-regex #"^[0-9a-zA-Z]{8}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{4}-[0-9a-zA-Z]{12}$")
(s/def ::str-uuid (s/and string? #(re-matches uuid-regex %)))

(s/def ::id (s/or :uuid ::uuid :str-uuid ::str-uuid))

(defn- try-url-str [url]
  (try
    #?(:clj (URL. url)
       :cljs (js/URL. url))
    (catch #?(:clj Exception :cljs :default) _
      false)))

(s/def ::url-str (s/and string? try-url-str))
(s/def ::url (s/or :string ::url-str
                   :url #(instance? #?(:cljs js/URL :clj URL) %)))

(s/def ::str-number (s/and string?
                           #(re-matches #"[0-9]+" %)))

(s/def ::email (s/and string?
                      #(re-matches #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$" %)))
