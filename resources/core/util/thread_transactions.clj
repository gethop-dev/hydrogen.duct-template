;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

{{=<< >>=}}
(ns <<namespace>>.util.thread-transactions
  (:require [clojure.spec.alpha :as s]))

(s/def ::fn fn?)
(s/def ::txn-fn ::fn)
(s/def ::rollback-fn ::fn)
(s/def ::transaction (s/keys :req-un [::txn-fn]
                             :opt-un [::rollback-fn]))
(s/def ::transactions (s/coll-of ::transaction))
(s/def ::args-map map?)
(s/def ::result-map map?)
(s/def ::thread-transactions-args (s/cat :transactions ::transactions
                                         :args-map ::args-map))
(s/def ::thread-transactions-ret ::result-map)
(s/fdef thread-transactions
  :args ::thread-transactions-args
  :ret ::thread-transactions-ret)

(defn- safe-run [f m]
  (try
    (f m)
    (catch Throwable e
      (merge m {:success? false
                :error-details {:reason (class e)
                                :message (.getMessage e)}}))))

(defn thread-transactions
  [txns args-map]
  {:pre [(s/valid? ::transactions txns)
         (s/valid? ::args-map args-map)]}
  (if-not (seq txns)
    ;; If there are no more transactions to process, then the passed
    ;; in `args-map` is the return value of the last transaction. If we
    ;; reached here is because the last transaction was successful. So
    ;; simply return `args-map` as the final value of the whole
    ;; transactions application.
    args-map
    (let [{:keys [txn-fn rollback-fn]} (first txns)
          result (safe-run txn-fn args-map)]
      (if-not (:success? result)
        result
        (let [next-result (thread-transactions (rest txns) result)]
          (if (:success? next-result)
            next-result
            (if-not rollback-fn
              next-result
              (safe-run rollback-fn next-result))))))))
