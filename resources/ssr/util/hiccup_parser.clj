;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.util.hiccup-parser
  (:import [clojure.lang MultiFn]))

(declare parse-tag-content)

(defn fn-or-multimethod?
  [e]
  (or (fn? e)
      (= (type e) MultiFn)))

(defn- maybe-resolve-form-2
  "Resolving the tag returns an fn, then it means it's a form 2.
  If that's the case, apply the content again to the inner fn to obtain form 1.
  In both cases, this function will return a form 1."
  [r content]
  (if (fn-or-multimethod? r)
    (apply r content)
    r))

(defn- ensure-content-unpacked
  "Sometimes the content might be packed in a list (e.g. because of a for-loop).
  Hiccup is unable to work with those so we need to ensure they get unpacked,
  no matter how deeply nested the package is."
  [content]
  (->
    (reduce
      (fn [acc current]
        (if (seq? current)
          (concat acc (ensure-content-unpacked current))
          (conj acc current)))
      []
      content)
    (vec)))

(defn- wrap-empty-component-failsafe
  "Sometimes a component might return nil (e.g. when we want to display a breadcrumbs container
  only when there is breadcrumbs data in place)
  In that case we need to make sure that no [nil] is returned, otherwise the html macro will complain."
  [tag-content]
  (if (= [nil] tag-content)
    nil
    tag-content))

(defn- maybe-parse-component-forward
  "Sometimes a component is implemented in a way that it simply forwards to another component (a dispatch of sorts)
  When that's the case, we want to apply another parsing."
  [tag-content]
  (if (and (vector? tag-content)
           (fn-or-multimethod? (first tag-content)))
    (parse-tag-content tag-content)
    tag-content))

(defn parse-tag-content
  "Reads the provided hiccup-like components and parses them so that
  they are understandable by the hiccup compiler.

  The original hiccup compiler (by weavejester) is not designed to work
  with some sugar that Reagent provides.
  See this [PR](https://github.com/weavejester/hiccup/pull/153)
  and this [comment](https://github.com/reagent-project/reagent/issues/247#issuecomment-427806061)
  for more insight."
  [tag-content]
  (if-let [component-syntax? (vector? tag-content)]
    (let [[tag & content] tag-content
          parseable? (and component-syntax?
                          (fn-or-multimethod? tag))
          parsed-result (if parseable?
                          (let [r (apply tag content)]
                            (maybe-resolve-form-2 r content))
                          tag-content)
          [new-tag & new-content] parsed-result
          new-content (->> new-content
                           (ensure-content-unpacked)
                           (map parse-tag-content))]
      (->
        (vec (cons new-tag new-content))
        (wrap-empty-component-failsafe)
        (maybe-parse-component-forward)))
    tag-content))
