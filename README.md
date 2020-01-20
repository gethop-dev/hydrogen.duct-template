# hydrogen.duct-template
[![Build Status](https://travis-ci.com/magnetcoop/hydrogen.duct-template.svg?branch=master)](https://travis-ci.com/magnetcoop/hydrogen.duct-template)
[![Clojars Project](https://img.shields.io/clojars/v/hydrogen/duct-template.svg)](https://clojars.org/hydrogen/duct-template)

An external profile for [Duct template](https://github.com/duct-framework/duct) that introduces Hydrogen code.

## Usage

This template profile needs to be used in conjunction with `+site` and `+cljs` hints.

`lein new duct <project name> +site +cljs +hydrogen/core`

It creates an SPA app that's ready for you to run. Front to back. It comes packed with some of the features we use in most of our projects:
- API endpoint for downloading initial config from the server
- Bread and butter FE code that manages routes, [themes toggling (just run `(themes/toggle-theme)`)](https://github.com/magnetcoop/hydrogen.duct-template/blob/master/resources/core/cljs/theme.cljs#L27-L32), js externs, etc.

### Additional profiles

#### Authentication and session management
Hydrogen also offers two profiles that provide session management using OpenID Connect ID Tokens.
To use them simply add:
- `+hydrogen/session.cognito` for AWS Cognito User Pools-based session management or
- `+hydrogen/session.keycloak` to add Keycloak-based session management.

Keep in mind that those two profiles are mutually exclusive.

#### SQL persistence boundary
Hydrogen also offers a profile that provides a boundary (based on Clojure protocols and records) for data persistence using SQL databases. By default it uses Postgresql (by adding its JDBC driver as a dependency), but can be used with any JDBC compatible database as long as you add its driver as a dependency.
To use it simply add:
- `+hydrogen/persistence.sql` to the list of required profiles

### What else can it do?

In order to be able to finally share our toolset with the community, we had to cut some corners
and narrow down the scope of the template's content. However we'll be delighted to highlight some of our
libs, gists and blog posts with our know-how:

- #### OpenID Authentication
  - [buddy-auth.jwt-oidc](https://github.com/magnetcoop/buddy-auth.jwt-oidc) - Integrant keys and associated code implementing a :duct.middleware.buddy/authentication compatible JWT token validation function for OpenID Connect ID Tokens
- #### Object storage
  - [object-storage.core](https://github.com/magnetcoop/object-storage.core) - Library that provides an object-storage protocol that can be implemented by other libraries.
  - [object-storage.ftp](https://github.com/magnetcoop/object-storage.ftp) - Integrant keys for managing objects in an FTP server
  - [object-storage.s3](https://github.com/magnetcoop/object-storage.s3) - Integrant keys for managing AWS S3 objects
- #### Integration with third party systems
  - [cms.webflow](https://github.com/magnetcoop/cms.webflow) A Duct library for managing Webflow CMS
  - [payments.stripe](https://github.com/magnetcoop/payments.stripe) - A Duct library for interacting with Stripe
  - [dashboard-manager.grafana](https://github.com/magnetcoop/dashboard-manager.grafana) - A Duct library for managing dashboards and associated users and organizations in Grafana
- #### Persistence
  - [sql-utils](https://github.com/magnetcoop/sql-utils) - A library designed as a thin convenience wapper over clojure.java.jdbc
  - [ragtime-wrapper](https://github.com/magnetcoop/hydrogen.module.ragtime-wrapper) - Duct module wrapping configuration for Ragtime migrations
  - [stork](https://github.com/magnetcoop/stork) - A Clojure/Datomic migrations library heavily inspired by rkneufeld/conformity
- #### IoT
  - [pubsub](https://github.com/magnetcoop/pubsub) - MQTT and AMQP Publish Subscribe library
- #### Scheduling
  - [scheduling.twarc](https://github.com/magnetcoop/scheduling.twarc) - Integrant keys for using Twarc scheduling library, with persistent JobStore backed by a Postgresql database
- #### Crypto
  - [encryption](https://github.com/magnetcoop/encryption) - Library for encrypting and decrypting arbitrary Clojure values, using caesium symmetric encryption primitives.
  - [secret-storage.aws-ssm-ps](https://github.com/magnetcoop/secret-storage.aws-ssm-ps) - Duct library with a boundary for obtaining secrets from AWS SSM PS
  - [Example client code using the components mentioned above](https://gist.github.com/werenall/c2a0187c8c4a66e25645edae57fb9a60)
- #### Misc.
  - [CLJS image cropping](https://medium.com/magnetcoop/cropping-images-in-clojurescript-aed776747a65) - Small image cropper implementation for ClojureScript
  - [tooltips/popovers](https://medium.com/magnetcoop/data-driven-tooltips-popovers-in-re-frame-de70d5412151) - Generic tooltip implementation

## Future work

For the list of our features to come please take a look at this project's [issues list](https://github.com/magnetcoop/hydrogen.duct-template/issues).

## License

Copyright (c) 2018, 2019, 2020 Magnet S Coop.

The source code for the library is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
