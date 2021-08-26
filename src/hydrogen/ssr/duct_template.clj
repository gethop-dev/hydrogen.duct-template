(ns hydrogen.ssr.duct-template
  (:require [hydrogen.utils :as utils :refer [resource ns->dir-name gen-cascading-routes]]))

(def cljs-templates
  {"src/{{dirs}}/client.cljs" (resource "core/cljs/client.cljs")
   "src/{{dirs}}/client/routes.cljs" (resource "ssr/cljs/routes.cljs")
   "src/{{dirs}}/client/navigation.cljs" (resource "ssr/cljs/navigation.cljs")})

(def cljc-templates
  {"src/{{dirs}}/client/breadcrumbs.cljc" (resource "core/cljs/breadcrumbs.cljs")
   "src/{{dirs}}/client/home.cljc" (resource "ssr/cljs/home.cljc")
   "src/{{dirs}}/client/sidebar.cljc" (resource "ssr/cljs/sidebar.cljc")
   "src/{{dirs}}/client/theme.cljc" (resource "core/cljs/theme.cljs")
   "src/{{dirs}}/client/tooltip.cljc" (resource "ssr/cljs/tooltip.cljc")
   "src/{{dirs}}/client/util.cljc" (resource "core/cljs/util.cljs")
   "src/{{dirs}}/client/view.cljc" (resource "ssr/cljs/view.cljc")
   "src/{{dirs}}/client/hydrogen_demo/shop.cljc" (resource "ssr/cljs/hydrogen_demo/shop.cljc")
   "src/{{dirs}}/client/hydrogen_demo/shop_item.cljc" (resource "ssr/cljs/hydrogen_demo/shop_item.cljc")
   "src/{{dirs}}/client/tooltip/generic_popup.cljc" (resource "core/cljs/tooltip/generic_popup.cljs")
   "src/{{dirs}}/client/tooltip/loading_popup.cljc" (resource "core/cljs/tooltip/loading_popup.cljs")})

(def other-templates
  {"src/{{dirs}}/client/externs.js" (resource "core/cljs/externs.js")})

(def client-templates
  (merge
    cljs-templates
    cljc-templates
    other-templates))

(defn profile [_]
  {:vars {:hydrogen-ssr? true}
   :templates client-templates
   :deps '[[kibu/pushy "0.3.8"]]})
