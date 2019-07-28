;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.client.util
  (:require [re-frame.core :as rf]))

(rf/reg-event-fx
 ::generic-error
 (fn [{:keys [db]} [_ e]]
   {:db (update db :errors-log (fnil conj []) e)}))
