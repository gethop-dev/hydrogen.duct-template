;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns hydrogen.core.duct-template
  (:require [hydrogen.utils :as utils :refer [resource ns->js-ns gen-cascading-routes]]))

(defn client-files
  [profiles]
  (when-not (utils/use-profile? profiles :hydrogen/ssr)
    {"src/{{dirs}}/client.cljs" (resource "core/cljs/client.cljs")
     "src/{{dirs}}/client/externs.js" (resource "core/cljs/externs.js")
     "src/{{dirs}}/client/breadcrumbs.cljs" (resource "core/cljs/breadcrumbs.cljs")
     "src/{{dirs}}/client/home.cljs" (resource "core/cljs/home.cljs")
     "src/{{dirs}}/client/routes.cljs" (resource "core/cljs/routes.cljs")
     "src/{{dirs}}/client/sidebar.cljs" (resource "core/cljs/sidebar.cljs")
     "src/{{dirs}}/client/theme.cljs" (resource "core/cljs/theme.cljs")
     "src/{{dirs}}/client/tooltip.cljs" (resource "core/cljs/tooltip.cljs")
     "src/{{dirs}}/client/tooltip/generic_popup.cljs" (resource "core/cljs/tooltip/generic_popup.cljs")
     "src/{{dirs}}/client/tooltip/loading_popup.cljs" (resource "core/cljs/tooltip/loading_popup.cljs")
     "src/{{dirs}}/client/util.cljs" (resource "core/cljs/util.cljs")
     "src/{{dirs}}/client/view.cljs" (resource "core/cljs/view.cljs")
     "src/{{dirs}}/client/hydrogen_demo/shop.cljs" (resource "core/cljs/hydrogen_demo/shop.cljs")
     "src/{{dirs}}/client/hydrogen_demo/shop_item.cljs" (resource "core/cljs/hydrogen_demo/shop_item.cljs")
     "src/{{dirs}}/client/navigation.cljs" (resource "core/cljs/navigation.cljs")}))

(defn api-files
  []
  {"src/{{dirs}}/api/config.clj" (resource "core/api/config.clj")
   "src/{{dirs}}/api/example.clj" (resource "core/api/example.clj")
   "src/{{dirs}}/api/util.clj" (resource "core/api/util.clj")
   "src/{{dirs}}/api/responses.clj" (resource "core/api/responses.clj")
   "test/{{dirs}}/api/test_utils.clj" (resource "core/test/api/test_utils.clj")})

(defn static-files
  []
  {"src/{{dirs}}/static/root.clj" (resource "core/static/root.clj")})

(defn service-files
  []
  {"src/{{dirs}}/service/shop.clj" (resource "core/service/shop.clj")})

(defn utils-files
  []
  {"src/{{dirs}}/util.clj" (resource "core/util.clj")
   "src/{{dirs}}/util/specs.cljc" (resource "core/util/specs.cljc")
   "src/{{dirs}}/util/thread_transactions.clj" (resource "core/util/thread_transactions.clj")})

(defn resources-files
  []
  {"resources/{{dirs}}/index.html" (resource "core/resources/index.html")
   "resources/{{dirs}}/public/images/hydrogen-logo-white.svg" (resource "core/resources/images/hydrogen-logo-white.svg")
   "resources/{{dirs}}/public/images/spinner.svg" (resource "core/resources/images/spinner.svg")
   "resources/{{dirs}}/public/css/main.scss" (resource "core/resources/css/main.scss")
   "resources/{{dirs}}/public/css/_breadcrumbs.scss" (resource "core/resources/css/_breadcrumbs.scss")
   "resources/{{dirs}}/public/css/_button.scss" (resource "core/resources/css/_button.scss")
   "resources/{{dirs}}/public/css/_theming.scss" (resource "core/resources/css/_theming.scss")
   "resources/{{dirs}}/public/css/_tooltip.scss" (resource "core/resources/css/_tooltip.scss")
   "resources/{{dirs}}/public/css/_generic-popup.scss" (resource "core/resources/css/_generic-popup.scss")
   "resources/{{dirs}}/public/css/_loading-popup.scss" (resource "core/resources/css/_loading-popup.scss")
   "resources/{{dirs}}/public/css/_utils.scss" (resource "core/resources/css/_utils.scss")})

(defn tooling-files
  []
  {".clj-kondo/.gitignore" (resource "tooling/clj-kondo/gitignore")
   ".clj-kondo/config.edn" (resource "tooling/clj-kondo/config.edn")})

(defn profile [{:keys [project-ns profiles]}]
  {:vars (cond->
          {:hydrogen-core? true
           :js-namespace (ns->js-ns project-ns)}
           (not (utils/use-profile? profiles :hydrogen/ssr))
           (assoc :cascading-routes (gen-cascading-routes project-ns ["static/root"
                                                                      "api/config"
                                                                      "api/example"])))
   :deps '[[clj-commons/secretary "1.2.4"]
           [cljs-ajax/cljs-ajax "0.8.4"]
           [day8.re-frame/http-fx "0.2.4"]
           [duct/compiler.sass "0.2.1"]
           [hydrogen/module.core "0.4.2"]
           [org.clojure/clojurescript "1.10.773"]
           [re-frame/re-frame "1.1.2"]
           [reagent/reagent "1.1.1"]
           [metosin/jsonista "0.3.5"]]
   :dev-deps '[[day8.re-frame/re-frame-10x "1.2.5"]]
   :templates (merge
               (client-files profiles)
               (api-files)
               (static-files)
               (service-files)
               (utils-files)
               (resources-files)
               (tooling-files))
   :modules {:hydrogen.module/core (cond-> {}
                                     (utils/use-profile? profiles :hydrogen/figwheel-main)
                                     (assoc :figwheel-main {}))}
   :profile-base {:duct.middleware.web/defaults " {:security {:anti-forgery false}}"
                  :duct.middleware.web/format " {}"
                  :duct.handler/root " {:middleware [#ig/ref :duct.middleware.web/format]}"
                  :duct.compiler/sass "\n  {:source-paths [\"resources\"]\n   :output-path \"target/resources\"}"
                  (keyword (str project-ns ".static/root")) " {}"
                  (keyword (str project-ns ".api/example")) (if (utils/use-session-profile? profiles)
                                                              " {:auth-middleware #ig/ref :duct.middleware.buddy/authentication}"
                                                              " {}")
                  (keyword (str project-ns ".api/config")) " {}"}
   :dirs ["src/{{dirs}}/boundary/adapter"
          "src/{{dirs}}/boundary/port"
          "src/{{dirs}}/service"
          "src/{{dirs}}/domain"]
   :repl-options {:host "0.0.0.0"
                  :port 4001}})
