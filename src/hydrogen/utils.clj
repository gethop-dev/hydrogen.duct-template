;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns hydrogen.utils
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn resource [path]
  (io/resource path))

(defn ns->dir-name [ns]
  (str/replace (name ns) "-" "_"))

(def ns->js-ns ns->dir-name)

(defn gen-cascading-routes [project-ns routes-refs]
  (as-> routes-refs $
    (map #(format "#ig/ref :%s.%s" project-ns %) $)
    (str/join "\n   " $)
    (str "\n  [" $ "]")))

(defn use-profile?
  [profiles p]
  (get (set profiles) p))
