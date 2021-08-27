;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.ssr.root
  (:require [cognitect.transit :as transit]
            [compojure.core :refer [context GET routes]]
            [hiccup.util :refer [as-str]]
            [hiccup.util :refer [escape-html as-str raw-string]]
            [hiccup2.core :refer [html]]
            [integrant.core :as ig]
            [re-frame.core :as rf]
            [re-frame.db :as rf.db]
            [<<namespace>>.client.breadcrumbs :as client.breadcrumbs]
            [<<namespace>>.client.home :as client.home]
            [<<namespace>>.client.hydrogen-demo.shop :as client.shop]
            [<<namespace>>.client.hydrogen-demo.shop-item :as client.shop-item]
            [<<namespace>>.client.sidebar :as client.sidebar]
            [<<namespace>>.client.theme :as client.theme]
            [<<namespace>>.client.view :as client.view]
            [<<namespace>>.service.shop :as srv.shop]
            [<<namespace>>.util :as util]
            [<<namespace>>.util.hiccup-parser :as hiccup-parser])
  (:import [java.io ByteArrayOutputStream]))

(defonce default-app-db-ba-output-stream-buffer-size 4096)

(defn load-initial-app-db-script [app-db]
  (with-open [app-db-output-stream (ByteArrayOutputStream. default-app-db-ba-output-stream-buffer-size)]
    (let [app-db-writer (transit/writer app-db-output-stream :json)]
      (transit/write app-db-writer app-db)
      [:script
       (raw-string (format "INITIAL_APP_DB=%s;"
                           (pr-str (.toString app-db-output-stream))))])))

(defn app
  ;; TODO it's just a modified version of client.cljs's app. Put it in a cljc namespace
  []
  (let [theme (rf/subscribe [::client.theme/get-theme])]
    (fn []
      [:div.app-container
       {:class (str "theme-" (name @theme))}
       [client.sidebar/main]
       [:div.app-container__main
        {:id "app-container__main"}
        [client.breadcrumbs/main]
        [client.view/main]]])))

(defn- gen-html
  [app-db]
  (->
    [:html {:lang "en"}
     [:head
      [:meta {:charset "UTF-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
      [:title <<front-page-title>>]
      [:link {:href "https://uploads-ssl.webflow.com/5a68a8bef9676b00011ad3be/5a8d32501a5b5000018eb866_favicon.png"
              :rel "shortcut icon"
              :type "image/x-icon"}]
      [:link {:href "https://fonts.googleapis.com/css?family=Poppins:400,700" :rel "stylesheet"}]
      [:link {:rel "stylesheet" :href "/css/main.css"}]]
     [:script "var INITIAL_APP_DB;"]
     (some-> app-db (load-initial-app-db-script))
     [:body
      [:div#app
       (hiccup-parser/parse-tag-content
         [app])]
      [:script {:src "/js/main.js"}]
      [:script "<<js-namespace>>.client.init(true);"]]]
    (html)
    (as-str)))

;; TODO use Teachascent's fork to make it work on multiple threads
;(defn- handle-route
;  [app-db]
;  (let [db-id (util/uuid)
;        result (with-bindings {#'re-frame.db/app-db-id db-id}
;                 (swap! rf.db/db-atoms* assoc db-id app-db)
;                 (gen-html app-db))]
;    (re-frame.db/clear-app-db db-id)
;    result))

(defn- handle-route
  ;; This solution will not work right with multiple clients asking for SSR as they will compete for the same resource.
  ;; Look at the solution above.
  [app-db]
  (reset! rf.db/app-db app-db)
  (gen-html app-db))

(defmethod ig/init-key :<<namespace>>.ssr/root [_ _]
  ;; NOTE: this routes registry needs to be a ONE TO ONE matching in regards to client's app-routes
  (context "/" []
           (routes
             (GET "/home" req
                  (handle-route
                    {:active-view [::client.home/view]
                     :breadcrumbs []}))
             (GET "/shop" req
                  (handle-route
                    {:active-view [::client.shop/view]
                     :shop {:items [:apple :orange :banana]}
                     :breadcrumbs [{:title "Home" :url "/home"}
                                   {:title "Shop" :url "/shop" :disabled true}]}))
             (GET "/shop/:id" [id :as req]
                  (handle-route
                    (let [shop-item (srv.shop/get-shop-item id)]
                      {:active-view [::client.shop-item/view]
                       :breadcrumbs [{:title "Home" :url "/home"}
                                     {:title "Shop" :url "/shop"}
                                     {:title (:name shop-item)
                                      :url (str "/" id)
                                      :disabled true}]
                       :shop-item shop-item})))
             (GET "*" req
                  (handle-route {:error "I don't know where I am"})))))
