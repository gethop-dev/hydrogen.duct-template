;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.sidebar)

(defn main
  []
  [:nav.app-container__sidebar
   [:a {:href "/home"} "Home"]
   [:p "Demo content"]
   [:a {:href "/shop"} "Shop"]
   [:ul
    [:li [:a {:href "/shop/111-111-111"} "Apple"]]
    [:li [:a {:href "/shop/222-222-222"} "Banana"]]]])
