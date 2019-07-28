;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

;; Borrowed from https://gist.github.com/mtnygard/9e6a3c5a107eed02f7616393cbb124b7
;; Michael Nygard

{{=<< >>=}}
(ns <<namespace>>.api.responses)

(defn response
  ([status body]
   {:status  status
    :headers {"content-type" "application/json"}
    :body    body}))

(defmacro http-status [code sym]
  `(def ~sym (partial response ~code)))

(defmacro http-statuses [& pairs]
  (assert (even? (count pairs)))
  `(do
     ~@(for [[c s] (partition 2 pairs)]
         `(http-status ~c ~s))))

(http-statuses
 200 ok
 201 created
 202 accepted
 204 no-content
 205 reset-content
 206 partial-content
 207 multi-status
 208 already-reported
 226 im-used

 300 multiple-choices
 301 moved-permanently
 302 found
 303 see-other
 304 not-modified
 305 use-proxy
 306 switch-proxy
 307 temporary-redirect
 308 permanent-redirect

 400 bad-request
 401 unauthorized
 402 payment-required
 403 forbidden
 404 not-found
 405 method-not-allowed
 406 not-acceptable
 407 proxy-authentication-required
 408 request-timeout
 409 conflict
 410 gone
 411 length-required
 412 precondition-failed
 413 payload-too-large
 414 uri-too-long
 415 unsupported-media-type
 416 range-not-satisfiable
 417 expectation-failed
 418 im-a-teapot
 421 misdirected-request
 422 unprocessable-entity
 423 locked
 424 failed-dependency
 425 too-early
 426 upgrade-required
 428 precondition-required
 429 too-many-requests
 431 header-fields-too-large
 451 unavailable-for-legal-reasons

 500 server-error
 501 not-implemented
 502 bad-gateway
 503 service-unavailable
 504 gateway-timeout
 505 http-version-not-supported
 506 variant-also-negotiates
 507 insufficient-storage
 508 loop-detected
 510 not-extended
 511 network-authentication-required)
