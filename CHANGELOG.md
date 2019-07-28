# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/).

## [Unreleased]
### Changed
- Chunks of code irrelevant to the main purpose of this template
were moved to an index of suggested next steps in the relevant `.md` file.
- Add Eastwood as a development dependency and bump lein-cljfmt version
- Reorganized the CLJS externs file. Split them into individual files (one per library/external service).

### Fixed
- Refactored login form in Cognito profile to make autocompletion work properly.
- Added missing images used in Cognito profile.

### Added
- A couple of back-end utility namespaces containing commonly used functionality (HTTP response methods, Base64 encoding/decoding, UUID generation, etc.)

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
 
[UNRELEASED]:  https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.1.7...HEAD
[0.1.7]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.1.6...v0.1.7
[0.1.6]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.1.5...v0.1.6
[0.1.5]: https://github.com/magnetcoop/hydrogen.duct-template/compare/v0.1.4...v0.1.5
[0.1.4]: https://github.com/magnetcoop/hydrogen.duct-template/v0.1.4
[0.1.3]: https://github.com/magnetcoop/hydrogen.cljs.duct-template/v0.1.3
