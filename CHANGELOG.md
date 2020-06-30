# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/).

## [Unreleased]
### Changed
- Use newer versions of dependencies

## [0.4.2] - 2020-06-03
### Changed
- Upgraded Secretary dependency version. The old one wasn't compatible with the Clojurescript version we are using.

## [0.4.1] - 2020-06-03
### Changed
- Only add additional SSO-ed apps to config.edn when using Keycloak sessions. They are not used with Cognito and then need to be removed manually.

## [0.4.0] - 2020-05-03
### Changed
- Use newer versions of dependencies

### Added
- Added `+hydrogen/scheduling.twarc` profile, for job scheduling using `magnet/scheduling.twarc` Duct library.

## [0.3.9] - 2020-05-01
### Fixed
- Added some new externs definitions to Cognito externs file (backported from one customer project)

## [0.3.8] - 2020-05-01
### Changed
- Use v0.1.9 of `hydrogen/module.core`

## [0.3.7] - 2020-05-01
### Changed
- Use newer versions of dependencies

## [0.3.6] - 2020-04-29
### Fixed
- Make the empty string a valid Bas64 encoded value

## [0.3.5] - 2020-02-26
### Fixed
- Upgraded Keycloak integration library to one that includes native promises (and added associated externs definitions)

## [0.3.4] - 2020-02-25
### Added
- `util.specs` cljc namespace got added. We use it to verify stuff like urls both on BE and FE.
### Changed
- The main layout in `client.cljs` has a new, more intuitive structure.
- Switched to using native promises in Keycloak integration library (custom legacy ones are deprecated now)

## [0.3.3] - 2019-12-05
### Added
- [Keycloak] - User data is fetched on successful login and displayed in home ns.
- Tooltip, generic popup and loading popup best practices.
### Changed
- Stopped using cookies for tracking keycloak process status.
- We decided to temporarily stop updating Cognito in sync with Keycloak.
Bugs may start happening more frequently when using Cognito profile from this version onward.
We'll resume work on Cognito soon.

## [0.3.2] - 2019-11-03
### Changed
- Use v0.3.5 of `magnet/sql-utils`

## [0.3.1] - 2019-09-09
### Changed
- Use v0.7.0 of `magnet/buddy-auth.jwt-oidc`

## [0.3.0] - 2019-08-04
### Added
- Keycloak and Cognito session modules now automatically refresh ID tokens, based on their expiration time.

### Fixed
- Fixed externs files path generation. Project namespace that contain hyphens must be translated into underscores.

## [0.2.0] - 2019-07-29
### Changed
- Chunks of code irrelevant to the main purpose of this template
were moved to an index of suggested next steps in the relevant `.md` file.
- Add Eastwood as a development dependency and bump lein-cljfmt version
- Reorganized the CLJS externs file. Split them into individual files (one per library/external service).
- Mode Cognito and Keycloak session management back into this template, instead of using external Duct modules. We no longer need to use modules (we can do all we need from the template) and this adds at least two benefits: a) a single place to maintain code, and b) configuration is explicit and visible in `config.edn`.
- Use newer versions of 3rd party dependencies (re-frame and cljs-ajax)
- Use v0.1.8 of `hydrogen/module.core`

### Fixed
- Refactored login form in Cognito profile to make autocompletion work properly.
- Added missing images used in Cognito profile.
- Handle project names containing hyphens correctly. We used the name as-is in Javascript code, which is not valid.

### Added
- A couple of back-end utility namespaces containing commonly used functionality (HTTP response methods, Base64 encoding/decoding, UUID generation, etc.)
- A new persistence profile for SQL databases (only the Postgresql driver dependency is added by default). It adds all the boundary namespaces neede (port, adapter(s), etc.).

## [0.1.7] - 2019-06-24
### Changed
- **Breaking change** changed expected keycloak config key from `:clientId` to `:client-id`.
- Changed parameters of debouncing for authentication checks.
- Use v0.1.2 of `hydrogen/module.session.keycloak`
- Use v0.1.8 of `hydrogen/module.session.cognito`

### Added
- Generate `src/service/` and `src/domain/` directories
- Explicit configuration to keycloak and cognito profiles 

## [0.1.6] - 2019-06-19
### Changed
- Use v0.1.1 of `hydrogen/module.session.keycloak`
- Use v0.6.0 of `magnet/buddy-auth.jwt-oidc`

### Fixed
- Projects without session profile had routing broken on v0.1.5. It's fixed now.
- Fix license file (the template is licensed under MPL 2.0, but the
  license file contained the EPL 2.0 license text)

## [0.1.5] - 2019-06-10

### Fixed
- hydrogen.module.keycloak version

## [0.1.4] - 2019-06-10

### Changed
- Moved repository to new one without `.cljs` in name.

### Added
- `+hydrogen/session.keycloak` profile!

## [0.1.3] - 2019-05-09

### Added
- This CHANGELOG
- Theming capabilities
- Tooltips/popovers engine

### Changed
- Directory `resources/assets` was renamed to `resources/images` to reflect its content more accurately.
- We were not consistent in using `re-frame.core` aliases.
Sometimes it was `re-frame` and sometimes it was `rf`.
This version unifies it to `rf`.
 
[UNRELEASED]:  https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.4.2...HEAD
[0.4.2]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.4.1...v0.4.2
[0.4.1]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.4.0...v0.4.1
[0.4.0]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.3.9...v0.4.0
[0.3.9]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.3.8...v0.3.9
[0.3.8]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.3.7...v0.3.8
[0.3.7]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.3.6...v0.3.7
[0.3.6]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.3.5...v0.3.6
[0.3.5]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.3.4...v0.3.5
[0.3.4]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.3.3...v0.3.4
[0.3.3]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.3.2...v0.3.3
[0.3.2]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.3.1...v0.3.2
[0.3.1]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.3.0...v0.3.1
[0.3.0]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.1.7...v0.2.0
[0.1.7]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.1.6...v0.1.7
[0.1.6]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.1.5...v0.1.6
[0.1.5]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.1.4...v0.1.5
[0.1.4]: https://github.com/magnetcoop/hydrogen.duct-template/v0.1.4
[0.1.3]: https://github.com/magnetcoop/hydrogen.cljs.duct-template/v0.1.3
