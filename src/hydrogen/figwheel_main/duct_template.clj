(ns hydrogen.figwheel-main.duct-template)

(defn profile [_]
  {:vars {:hydrogen-figwheel-main? true}
   :deps '[[hydrogen/module.cljs "0.5.2"]]})
