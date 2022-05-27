# hydrogen.duct-template
[![ci-cd](https://github.com/gethop-dev/hydrogen.duct-template/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/gethop-dev/hydrogen.duct-template/actions/workflows/ci-cd.yml)
[![Clojars Project](https://img.shields.io/clojars/v/dev.gethop/hydrogen.duct-template.svg)](https://clojars.org/dev.gethop/hydrogen.duct-template)


An external profile for [Duct template](https://github.com/duct-framework/duct) that introduces Hydrogen code.

## Usage

This template profile needs to be used in conjunction with `+site` and `+cljs` hints.

`lein new duct <project name> +site +cljs +hydrogen/core`

It creates an SPA app that's ready for you to run. Front to back. It comes packed with some of the features we use in most of our projects:
- API endpoint for downloading initial config from the server
- Bread and butter FE code that manages routes, [themes toggling (just run `(themes/toggle-theme)`)](https://github.com/gethop-dev/hydrogen.duct-template/blob/master/resources/core/cljs/theme.cljs#L27-L32), js externs, etc.

### Additional profiles

#### Authentication and session management
Hydrogen also offers two profiles that provide session management using OpenID Connect ID Tokens.
To use them simply add:
- `+hydrogen/session.cognito` for AWS Cognito User Pools-based session management or
- `+hydrogen/session.keycloak` to add Keycloak-based session management.

Keep in mind that those two profiles are mutually exclusive.

These authentication profiles expect you to configure some environment variables in order to work properly.
Please see the specs in [buddy-auth.jwt-oid](https://github.com/gethop-dev/buddy-auth.jwt-oidc/blob/master/src/dev/gethop/buddy_auth/jwt_oidc.clj).

#### Figwheel main


##### Warning!
Until further notice, usage of `+hydrogen/figwheel-main` profile will require some manual fixes in `project.clj`:
1. The are two conflicting dependencies: `hydrogen/module.cljs` and `duct/module.cljs`. Please remove the latter one.
2. Because of a [known `figwheel-main` issue](https://github.com/bhauman/figwheel-main/pull/276), even though `:resource-paths` includes `target/resources`, the compiler will complain about `target/resources/<project name>` not being found on classpath. Please add that path too.


#### SQL persistence boundary
Hydrogen also offers a profile that provides a boundary (based on Clojure protocols and records) for data persistence using SQL databases. By default it uses Postgresql (by adding its JDBC driver as a dependency), but can be used with any JDBC compatible database as long as you add its driver as a dependency.
To use it simply add:
- `+hydrogen/persistence.sql` to the list of required profiles

#### Job Scheduling
Hydrogen also offers a profile that provides job scheduling using Twarc scheduling library, with persistent JobStore backed by a Postgresql database, through [scheduling.twarc](https://github.com/gethop-dev/scheduling.twarc) Duct library. This profile depends on `+hydrogen/persistence.sql` profile, so make sure you also specify it when adding the job scheduling profile.
To use it simply add:
- `+hydrogen/persistence.sql +hydrogen/scheduling.twarc` to the list of required profiles

#### Isomorphic / Server Side Rendering
If you want to use either of those, hydrogen leverages the [blogpost of Techascent](https://techascent.com/blog/isomorphic-rendering.html)
to deliver this feature. Note that this is still in its infancy and it will be improved in future. 

Usage: `+hydrogen/ssr` (NOTE: combo with session.keycloak profile is not fully supported yet)

##### Warning!

Current re-frame (1.2.0 in the time of writing this documentation) doesn't support reliably using
app db state in a multithreaded environment without running into concurrency problems.
However there is a [fork](https://github.com/techascent/re-frame) by Techascent that patches that.
Please see the `handle-route` function in `ssr/root.clj` file to read more. 

### What else can it do?

In order to be able to finally share our toolset with the community, we had to cut some corners
and narrow down the scope of the template's content. However we'll be delighted to highlight some of our
libs, gists and blog posts with our know-how:

- #### OpenID Authentication
  - [buddy-auth.jwt-oidc](https://github.com/gethop-dev/buddy-auth.jwt-oidc) - Integrant keys and associated code implementing a :duct.middleware.buddy/authentication compatible JWT token validation function for OpenID Connect ID Tokens
- #### Object storage
  - [object-storage.core](https://github.com/gethop-dev/object-storage.core) - Library that provides an object-storage protocol that can be implemented by other libraries.
  - [object-storage.ftp](https://github.com/gethop-dev/object-storage.ftp) - Integrant keys for managing objects in an FTP server
  - [object-storage.s3](https://github.com/gethop-dev/object-storage.s3) - Integrant keys for managing AWS S3 objects
- #### Integration with third party systems
  - [cms.webflow](https://github.com/magnetcoop/cms.webflow) A Duct library for managing Webflow CMS
  - [payments.stripe](https://github.com/gethop-dev/payments.stripe) - A Duct library for interacting with Stripe
  - [dashboard-manager.grafana](https://github.com/gethop-dev/dashboard-manager.grafana) - A Duct library for managing dashboards and associated users and organizations in Grafana
  - [esignatures.docusign](https://github.com/gethop-dev/esignatures.docusign) - A Duct library for interacting with the Docusign eSignature API 
- #### Persistence
  - [sql-utils](https://github.com/gethop-dev/sql-utils) - A library designed as a thin convenience wapper over clojure.java.jdbc
  - [ragtime-wrapper](https://github.com/gethop-dev/hydrogen.module.ragtime-wrapper) - Duct module wrapping configuration for Ragtime migrations
  - [stork](https://github.com/gethop-dev/stork) - A Clojure/Datomic migrations library heavily inspired by rkneufeld/conformity
- #### IoT
  - [pubsub](https://github.com/gethop-dev/pubsub) - MQTT and AMQP Publish Subscribe library
- #### Scheduling
  - [scheduling.twarc](https://github.com/gethop-dev/scheduling.twarc) - Integrant keys for using Twarc scheduling library, with persistent JobStore backed by a Postgresql database
- #### Crypto
  - [encryption](https://github.com/gethop-dev/encryption) - Library for encrypting and decrypting arbitrary Clojure values, using caesium symmetric encryption primitives.
  - [secret-storage.aws-ssm-ps](https://github.com/gethop-dev/secret-storage.aws-ssm-ps) - Duct library with a boundary for obtaining secrets from AWS SSM PS
  - [Example client code using the components mentioned above](https://gist.github.com/werenall/c2a0187c8c4a66e25645edae57fb9a60)
- #### Misc.
  - [CLJS image cropping](https://medium.com/magnetcoop/cropping-images-in-clojurescript-aed776747a65) - Small image cropper implementation for ClojureScript
  - [tooltips/popovers](https://medium.com/magnetcoop/data-driven-tooltips-popovers-in-re-frame-de70d5412151) - Generic tooltip implementation
  
### Running the application

Development
Start the REPL.

```sh
lein repl
```

Then load the development environment.

```clojure
user=> (dev)
:loaded
```

Run go to prep and initiate the system.

```clojure
dev=> (go)
:duct.server.http.jetty/starting-server {:port 3000}
:initiated
```

By default this creates a web server at http://localhost:3000.

When you make changes to your source files, use reset to reload any modified files and reset the server.

```clojure
dev=> (reset)
:reloading (...)
:resumed
```

## Future work

For the list of our features to come please take a look at this project's [issues list](https://github.com/gethop-dev/hydrogen.duct-template/issues).

## License

Copyright (c) 2022 HOP Technologies

The source code for the library is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
