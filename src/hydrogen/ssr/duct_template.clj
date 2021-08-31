(ns hydrogen.ssr.duct-template
  (:require [hydrogen.utils :refer [resource ns->dir-name gen-cascading-routes]]
            [hydrogen.utils :as utils]))

(defn- cljs-templates
  []
  {"src/{{dirs}}/client.cljs" (resource "core/cljs/client.cljs")
   "src/{{dirs}}/client/routes.cljs" (resource "ssr/cljs/routes.cljs")
   "src/{{dirs}}/client/navigation.cljs" (resource "core/cljs/navigation.cljs")})

(defn- cljc-templates
  []
  {"src/{{dirs}}/client/breadcrumbs.cljc" (resource "core/cljs/breadcrumbs.cljs")
   "src/{{dirs}}/client/home.cljs" (resource "ssr/cljs/home.cljs")
   "src/{{dirs}}/client/sidebar.cljc" (resource "ssr/cljs/sidebar.cljc")
   "src/{{dirs}}/client/theme.cljc" (resource "core/cljs/theme.cljs")
   "src/{{dirs}}/client/tooltip.cljc" (resource "ssr/cljs/tooltip.cljc")
   "src/{{dirs}}/client/util.cljc" (resource "core/cljs/util.cljs")
   "src/{{dirs}}/client/view.cljc" (resource "ssr/cljs/view.cljc")
   "src/{{dirs}}/client/hydrogen_demo/shop.cljc" (resource "ssr/cljs/hydrogen_demo/shop.cljc")
   "src/{{dirs}}/client/hydrogen_demo/shop_item.cljc" (resource "ssr/cljs/hydrogen_demo/shop_item.cljc")
   "src/{{dirs}}/client/tooltip/generic_popup.cljc" (resource "core/cljs/tooltip/generic_popup.cljs")
   "src/{{dirs}}/client/tooltip/loading_popup.cljc" (resource "core/cljs/tooltip/loading_popup.cljs")})

(defn- other-templates
  [profiles]
  (cond->
   {"src/{{dirs}}/client/externs.js" (resource "core/cljs/externs.js")
    "src/{{dirs}}/ssr/root.clj" (resource "ssr/ssr/root.clj")
    "src/{{dirs}}/util/hiccup_parser.clj" (resource "ssr/util/hiccup_parser.clj")
    "test/{{dirs}}/util/hiccup_parser_test.clj" (resource "ssr/test/util/hiccup_parser_test.clj")}
    (utils/use-session-profile? profiles)
    (merge {"src/{{dirs}}/client/home.clj" (resource "ssr/client_substitutes/home.clj")
            "src/{{dirs}}/client/landing.clj" (resource "ssr/client_substitutes/landing.clj")})))

(defn cascading-routes
  [profiles]
  (if (utils/use-session-profile? profiles)
    ["api/config"
     "api/example"
     "api/user"
     "ssr/root"]
    ["api/config"
     "api/example"
     "ssr/root"]))

(defn profile [{:keys [project-ns profiles]}]
  {:vars {:hydrogen-ssr? true
          :cascading-routes (cascading-routes profiles)}
   :templates (merge
               (cljs-templates)
               (cljc-templates)
               (other-templates profiles))
   :deps '[[kibu/pushy "0.3.8"]
           [hiccup "2.0.0-alpha2"]]
   :profile-base {(keyword (str project-ns ".ssr/root")) (if (utils/use-session-profile? profiles)
                                                           " {:auth-middleware #ig/ref :duct.middleware.buddy/authentication}"
                                                           " {}")}})
