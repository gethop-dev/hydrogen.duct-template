# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/).

## [UNRELEASED]

## [0.5.13] - 2022-04-13
### Added
- [Issue 47] Added an ssr module.
- Document in README the need for env variables config
when using session profiles.
- Added utility namespaces for API integration testing, and threaded transactions.
- Added a couple of commonly used functions in "util" namespace, to conditionally update a key in a map

### Changed
- Use version "2.9.8" of `leiningen`
- Use "v0.8.4" of `cljs-ajax/cljs-ajax`
- Use "v0.2.4" of `day8.re-frame/http-fx`
- Use "v1.1.2" of `re-frame/re-frame`
- Use "v1.1.1" of `reagent/reagent`
- Use "v0.3.5" of `metosin/jsonista`
- Use "v1.2.5" of `day8.re-frame/re-frame-10x`
- Use "v0.4.12" of `magnet/sql-utils`
- Use "v42.3.3" of `org.postgresql/postgresql`
- Use "v0.6.0" of `magnet/scheduling.twarc`

### Fixed
- [Issue 46] Fixed wrong aritiy in rf subscriptions
- [Issue 48] Fixed warnings from newer Google Clouse compiler when doing advanced compilation.
- Put one precondition in tooltip ns into a vector (this was making eastwood complain)
- Fix eastwood reflection warnings in "util" namespace.

## [0.5.12] - 2021-07-05
### Fixed
- [Issue 45] Fix token renewal when using AWS Cognito

## [0.5.11] - 2021-06-28
### Changed
- Use v0.4.2 of `hydrogen/module.core`
- Use version "0.8.3" of cljs-ajax

## Added
- [Issue 42] Include `metosin/jsonista` as a direct dependency for the created projects

## [0.5.10] - 2021-06-24
### Fixed
- [Issue 44] Themify doesn't work for pseudoelements/pseuselectors

## [0.5.9] - 2021-06-24
### Fixed
- [Issue 43] Fix landing active-view check in client.cljs ns

## [0.5.8] - 2021-05-16
### Fixed
- [Issue 40] Fix Keycloak token lifetime and refresh interval calculation

## [0.5.7] - 2021-04-09
### Changed
- Use v0.10.3 of `magnet/buddy-auth.jwt-oidc` (fixes a caching bug)

## [0.5.6] - 2021-03-17
### Changed
- Changed Keycloak session configuration variable from KEYCLOAK_URL to KEYCLOAK_FRONTEND_URL, to use the standard Keycloak variable name (instead of our own name).

## [0.5.5] - 2021-02-02
### Added
- [Issue 34] `.client.breadcrumbs` and `_breadcrumbs.scss` added.
- [Issue 20] Re-added sidebar component and populated it with demo content.
- Brand new and simplified system of registering views. It supports `view.enter` and `view.leave`
events which should greatly improve abilities to collect garbage after leaving a view.

### Changed
- Demo code used for capabilities presentation has been moved to separate namespaces and views.
- [Issue 35] Use explicit listing of migrations in ragtime configuration

## [0.5.4] - 2020-12-16
### Changed
- Use v0.2.1 of `hydrogen/module.core`

## [0.5.3] - 2020-12-15
### Fixed
- [Issue 36] User session is broken after 08bcff44

## [0.5.2] - 2020-12-14
### Fixed
- Fix `::fetch-user-data` event handler

    This event handler now gets jwt-token from session cofx instead of appdb.
    It is so because at times the token may not be present in appdb yet when
    `::ensure-data` is called.

- Don't add `[hydrogen/module.cljs "0.5.2"]` in core profile.

## [0.5.1] - 2020-12-03
### Added
- More btn class modifiers (btn--disabled, btn--secondary, btn--light)
- [Issue 28] - now Keycloak-based applications should be able to restore sessions after refreshing
- Support for prod and dev migrations sets
- [Issue 21][Issue 15] Refactor cognito's client-side flow
    - This solves the problem of an auth token not being refreshed.
- By default keep using lein-figwheel. If you want your new projects to use figwheel-main then add `+hydrogen/figwheel-main` profile.
- [Issue 31] Use Hikari database pool instead of creating a connection each time
- [Issue 32] Added basic clj-kondo configuration to the template for generated projects.

### Fixed
- [Issue 27]
- [Issue 30]
- [Issue 33]
- Make tooltip manager more robust
    - Sometimes, when gettings a class on click, the output is an object
    instead of a string. For that reason, if we were to call `re-find` on
    that then it would explode. Hence the `string?` check.
- Minor indentaton issues
- Lots of warnings produced by clj-kondo

## [0.5.0] - 2020-09-14
### Added
- Add `:figwheel-main true` config to `:hydrogen.module/core` key.
- [Issue 19] - Landing containers now have theme classes assigned. Just like main component does.

### Changed
- [Issue 24] - `reagent.core/render` is deprecated. Now we use `reagent.dom/render` instead.
- [Issue 17] - The atom holding keycloak state is now a clojure.core/atom rather than reagent.core/atom.
There was no reason to use a ratom here.
- Bumped dependencies
- **BREAKING CHANGE** - newly generated projects will have two conflicting dependencies:
                        `hydrogen/module.cljs` and `duct/module.cljs`.
                        Please remove the latter one fromm the generated `project.clj` file.

### Fixed
- [Issue 23] - Now the DELETEME-demo-* functions definitions will get generated for all profiles.
- [Issue 22] - Fix conditional checking if a route is available to a user irregardless of authentication.

## [0.4.3] - 2020-07-01
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

[UNRELEASED]:  https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.5.13...HEAD
[0.5.13]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.5.13...v0.5.13
[0.5.12]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.5.11...v0.5.12
[0.5.11]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.5.10...v0.5.11
[0.5.10]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.5.9...v0.5.10
[0.5.9]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.5.8...v0.5.9
[0.5.8]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.5.7...v0.5.8
[0.5.7]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.5.6...v0.5.7
[0.5.6]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.5.5...v0.5.6
[0.5.5]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.5.4...v0.5.5
[0.5.4]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.5.3...v0.5.4
[0.5.3]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.5.2...v0.5.3
[0.5.2]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.5.1...v0.5.2
[0.5.1]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.5.0...v0.5.1
[0.5.0]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.4.3...v0.5.0
[0.4.3]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.4.2...v0.4.3
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

[Issue 15]: https://github.com/magnetcoop/hydrogen.duct-template/issues/15
[Issue 17]: https://github.com/magnetcoop/hydrogen.duct-template/issues/17
[Issue 19]: https://github.com/magnetcoop/hydrogen.duct-template/issues/19
[Issue 20]: https://github.com/magnetcoop/hydrogen.duct-template/issues/20
[Issue 21]: https://github.com/magnetcoop/hydrogen.duct-template/issues/21
[Issue 22]: https://github.com/magnetcoop/hydrogen.duct-template/issues/22
[Issue 23]: https://github.com/magnetcoop/hydrogen.duct-template/issues/23
[Issue 24]: https://github.com/magnetcoop/hydrogen.duct-template/issues/24
[Issue 27]: https://github.com/magnetcoop/hydrogen.duct-template/issues/27
[Issue 28]: https://github.com/magnetcoop/hydrogen.duct-template/issues/28
[Issue 30]: https://github.com/magnetcoop/hydrogen.duct-template/issues/30
[Issue 31]: https://github.com/magnetcoop/hydrogen.duct-template/issues/31
[Issue 32]: https://github.com/magnetcoop/hydrogen.duct-template/issues/32
[Issue 33]: https://github.com/magnetcoop/hydrogen.duct-template/issues/33
[Issue 34]: https://github.com/magnetcoop/hydrogen.duct-template/issues/34
[Issue 35]: https://github.com/magnetcoop/hydrogen.duct-template/issues/35
[Issue 36]: https://github.com/magnetcoop/hydrogen.duct-template/issues/36
[Issue 40]: https://github.com/magnetcoop/hydrogen.duct-template/issues/40
[Issue 42]: https://github.com/magnetcoop/hydrogen.duct-template/issues/42
[Issue 43]: https://github.com/magnetcoop/hydrogen.duct-template/issues/43
[Issue 44]: https://github.com/magnetcoop/hydrogen.duct-template/issues/44
[Issue 45]: https://github.com/magnetcoop/hydrogen.duct-template/issues/45
[Issue 46]: https://github.com/magnetcoop/hydrogen.duct-template/issues/46
[Issue 47]: https://github.com/magnetcoop/hydrogen.duct-template/issues/47
[Issue 48]: https://github.com/magnetcoop/hydrogen.duct-template/issues/48
