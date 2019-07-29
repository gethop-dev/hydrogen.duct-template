;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.session.oidc-sso
  (:require [ajax.core :as ajax]
            [day8.re-frame.http-fx]
            [re-frame.core :as rf]
            [<<namespace>>.client.util :as util]))

;; No-op. We just need this because of the AJAX calls we do in `::trigger-sso-apps`
(rf/reg-event-db
 ::app-sso-completed
 (fn [db _]
   db))

;; This is to avoid a race condition when trying to do multiple
;; authentications simultaneously in Grafana. To solve it we want to
;; login into Grafana before asking for any content from it. In fact
;; we'll have the same problem with any other external application
;; that we integrate with using OIDC SSO.
(rf/reg-event-fx
 ::trigger-sso-apps
 (fn [{:keys [db]} _]
   (let [apps (get-in db [:config :oidc :sso-apps])
         requests (mapv (fn [app]
                          {:uri (:login-url app)
                           :method (:login-method app)
                           :response-format (ajax/raw-response-format)
                           :on-success [::app-sso-completed]
                           :on-failure [::util/generic-error]})
                        apps)]
     {:http-xhrio requests})))

;; No-op. We just need this because of the AJAX calls we do in `::trigger-logout-apps`
(rf/reg-event-db
 ::app-logout-completed
 (fn [db _]
   db))

;; This is to logout from any other external application that we
;; integrate with using OIDC SSO. This is the complement of ::trigger-sso-apps
(rf/reg-event-fx
 ::trigger-logout-apps
 (fn [{:keys [db]} _]
   (let [apps (get-in db [:config :oidc :sso-apps])
         requests (mapv (fn [app]
                          {:uri (:logout-url app)
                           :method (:logout-method app)
                           :response-format (ajax/raw-response-format)
                           :on-success [::app-logout-completed]
                           :on-failure [::util/generic-error]})
                        apps)]
     {:http-xhrio requests})))
