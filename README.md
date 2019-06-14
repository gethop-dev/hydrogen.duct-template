# hydrogen.duct-template

An external profile for [Duct template](https://github.com/duct-framework/duct) that introduces Hydrogen code.

## Usage

This template profile needs to be used in conjunction with `+site` and `+cljs` hints.

`lein new duct <project name> +site +cljs +hydrogen/core`

You can optionally add `+hydrogen/session.cognito` to add AWS Cognito User Pools-based session management, using OpenID Connect ID Tokens. Or `+hydrogen/session.keycloak` to add Keycloak-based session management (using OpenID Connect ID tokens too). Keep in mind that those two profiles are mutually exclusive.

## License

Copyright (c) Magnet S Coop 2018.

The source code for the library is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
