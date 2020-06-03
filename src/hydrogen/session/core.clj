(ns hydrogen.session.core)

(def ^:const jwt-oidc-profile-base
  "
  {:claims
   {:iss #duct/env [\"OIDC_ISSUER_URL\" Str]
    :aud #duct/env [\"OIDC_AUDIENCE\" Str]}
   :jwks-uri #duct/env [\"OIDC_JWKS_URI\" Str]
   :logger #ig/ref :duct/logger}")

(def ^:const buddy-auth-profile-base
  "
  {:backend :token
   :token-name \"Bearer\"
   :authfn #ig/ref :magnet.buddy-auth/jwt-oidc}")

(def ^:const api-config-profile-base
  "
  {:oidc
   {%s}}")

(def ^:const api-user-profile-base
  "
  {:auth-middleware #ig/ref :duct.middleware.buddy/authentication
   :logger #ig/ref :duct/logger}")

(def ^:const session-core-profile-base
  {:magnet.buddy-auth/jwt-oidc jwt-oidc-profile-base
   :duct.middleware.buddy/authentication buddy-auth-profile-base})
