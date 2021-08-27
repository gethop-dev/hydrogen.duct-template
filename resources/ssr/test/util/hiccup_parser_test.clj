;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.util.hiccup-parser-test
  (:require [clojure.test :refer :all]
            [<<namespace>>.util.hiccup-parser :as sut]))

(defn form1-comp
  [& [another-comp]]
  [:div "I'm a form 1" another-comp])

(defn form1-comp-with-for-loop
  []
  [:div.form1-comp-with-for-loop
   (for [x [1 2 3]]
     ^{:key (gensym x)}
     [:p.for-loop-product x])])

(defn form1-comp-with-2-for-loops
  []
  [:div.form1-comp-with-for-loop
   (for [x [1 2 3]]
     ^{:key (gensym x)}
     [:p.for-a-loop-product x])
   (for [x ["a" "b" "c"]]
     ^{:key (gensym x)}
     [:p.for-b-loop-product x])])

(defn form1-for-in-for-in-for
  []
  [:div.form1-comp-with-for-loop
   (for [x [1]]
     (for [y [x]]
       (for [z [y]]
         ^{:key (gensym z)}
         [:p.for-in-for-in-for z])))])

(defn form1-comp-with-map
  []
  ;; NOTE: the keyword values (like :red in this example) not being supported by hiccup compiler
  ;; is a known issue. https://github.com/weavejester/hiccup/issues/159
  [:div#inline-id.foo {:style {:background "tomato" :color :red}
                       :class "foo--bar"}
   "Highly customized form1 comp"])

(defn form2-comp
  []
  (let [foo (atom 1)]
    (fn []
      [:div "I'm a form 2" @foo])))

(defn form1-immediate-forward
  []
  [form1-comp])

(defmulti foo identity)

(defmethod foo :default
  [_]
  [:div "Something"])

(defmethod foo :home
  [_]
  [form1-comp])

(deftest parse-tag-content
  (is (= (sut/parse-tag-content [:div [form1-comp]])
         [:div [:div "I'm a form 1" nil]]))
  (is (= (sut/parse-tag-content [form1-comp])
         [:div "I'm a form 1" nil]))
  (is (= (sut/parse-tag-content [form1-comp [:div "Hello"]])
         [:div "I'm a form 1" [:div "Hello"]]))
  (is (= (sut/parse-tag-content [form1-comp '([:div "Hello"] [:p "Howdy"])])
         [:div "I'm a form 1" [:div "Hello"] [:p "Howdy"]]))
  (is (= (sut/parse-tag-content [form1-comp-with-for-loop])
         [:div.form1-comp-with-for-loop
          [:p.for-loop-product 1]
          [:p.for-loop-product 2]
          [:p.for-loop-product 3]]))
  (is (= (sut/parse-tag-content [form1-comp-with-2-for-loops])
         [:div.form1-comp-with-for-loop
          [:p.for-a-loop-product 1]
          [:p.for-a-loop-product 2]
          [:p.for-a-loop-product 3]
          [:p.for-b-loop-product "a"]
          [:p.for-b-loop-product "b"]
          [:p.for-b-loop-product "c"]]))
  (is (= (sut/parse-tag-content [form1-for-in-for-in-for])
         [:div.form1-comp-with-for-loop [:p.for-in-for-in-for 1]]))
  (is (= (sut/parse-tag-content [form1-comp-with-map])
         [:div#inline-id.foo
          {:style {:background "tomato", :color :red},
           :class "foo--bar"}
          "Highly customized form1 comp"]))
  (is (= (sut/parse-tag-content [form2-comp])
         [:div "I'm a form 2" 1]))
  (is (= (sut/parse-tag-content [form1-comp [form2-comp]])
         [:div "I'm a form 1" [:div "I'm a form 2" 1]]))
  (is (= (sut/parse-tag-content [form1-immediate-forward])
         [:div "I'm a form 1" nil]))
  (testing "multimethods"
    (is (= (sut/parse-tag-content [foo nil])
           [:div "Something"]))
    (is (= (sut/parse-tag-content [foo :home])
           [:div "I'm a form 1" nil]))))
