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

- #### Crypto
  - [Library for encrypting and decrypting arbitrary Clojure values, using caesium symmetric encryption primitives.](https://github.com/magnetcoop/encryption)
  - [Duct library with a boundary for obtaining secrets from AWS SSM PS](https://github.com/magnetcoop/secret-storage.aws-ssm-ps)
  - [Example client code using the components mentioned above](https://gist.github.com/werenall/c2a0187c8c4a66e25645edae57fb9a60)
- #### Misc.
  - [tooltips/popovers](https://medium.com/magnetcoop/data-driven-tooltips-popovers-in-re-frame-de70d5412151)

## Future work

For the list of our features to come please take a look at this project's [issues list](https://github.com/magnetcoop/hydrogen.duct-template/issues).

## License

Copyright (c) 2018, 2019, 2020 Magnet S Coop.

The source code for the library is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
