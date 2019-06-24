;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns hydrogen.core.duct-template
  (:require [clojure.string :as str]
            [hydrogen.utils :refer [resource]]))

(defn- gen-cascading-routes [project-ns routes-refs]
  (as-> routes-refs $
    (map #(format "#ig/ref :%s.%s" project-ns %) $)
    (str/join "\n   " $)
    (str "\n  [" $ "]")))

(defn profile [{:keys [project-ns]}]
  {:vars {:hydrogen-core? true
          :cascading-routes (gen-cascading-routes project-ns ["static/root"
                                                              "api/config"
                                                              "api/example"])}
   :deps '[[cljs-ajax "0.7.5"]
           [day8.re-frame/http-fx "0.1.6"]
           [duct/compiler.sass "0.2.1"]
           [org.clojure/clojurescript "1.10.339"]
           [re-frame "0.10.6"]
           [reagent "0.8.1"]
           [secretary "1.2.3"]
           [hydrogen/module.core "0.1.6"]]
   :dev-deps '[[day8.re-frame/re-frame-10x "0.3.7"]]
   :templates {;; Client
               "src/{{dirs}}/client.cljs" (resource "core/cljs/client.cljs")
               "src/{{dirs}}/client/externs.js" (resource "core/cljs/externs.js")
               "src/{{dirs}}/client/home.cljs" (resource "core/cljs/home.cljs")
               "src/{{dirs}}/client/routes.cljs" (resource "core/cljs/routes.cljs")
               "src/{{dirs}}/client/theme.cljs" (resource "core/cljs/theme.cljs")
               "src/{{dirs}}/client/todo.cljs" (resource "core/cljs/todo.cljs")
               "src/{{dirs}}/client/tooltip.cljs" (resource "core/cljs/tooltip.cljs")
               "src/{{dirs}}/client/view.cljs" (resource "core/cljs/view.cljs")
               ;; API
               "src/{{dirs}}/api/config.clj" (resource "core/api/config.clj")
               "src/{{dirs}}/api/example.clj" (resource "core/api/example.clj")
               "src/{{dirs}}/api/util.clj" (resource "core/api/util.clj")
               ;; Static
               "src/{{dirs}}/static/root.clj" (resource "core/static/root.clj")
               ;; Resources
               "resources/{{dirs}}/index.html" (resource "core/resources/index.html")
               "resources/{{dirs}}/public/images/hydrogen-logo-white.svg" (resource "core/resources/images/hydrogen-logo-white.svg")
               "resources/{{dirs}}/public/css/button.scss" (resource "core/resources/css/button.scss")
               "resources/{{dirs}}/public/css/main.scss" (resource "core/resources/css/main.scss")
               "resources/{{dirs}}/public/css/theming.scss" (resource "core/resources/css/theming.scss")
               "resources/{{dirs}}/public/css/tooltip.scss" (resource "core/resources/css/tooltip.scss")
               "resources/{{dirs}}/public/css/utils.scss" (resource "core/resources/css/utils.scss")}
   :modules {:hydrogen.module/core {:add-example-api? true}}
   :dirs ["src/{{dirs}}/boundary/adapter"
          "src/{{dirs}}/boundary/port"
          "src/{{dirs}}/service"
          "src/{{dirs}}/domain"]
   :repl-options {:host "0.0.0.0"
                  :port 4001}})
