/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

/**
 * Figwheel expects files with .js extension inside its source
 * directories to be a foreign library. And foreign libraries *MUST*
 * declare a namespace. In fact, figwheel assumes it, and if it
 * doesn't find it and can't map the file back to a source .cljs file,
 * it bombs out with a NullPointerException.
 *
 * So even if this is *NOT* a foreign library, but just an externs file,
 * add a namespace declaration to prevent figwheel from crashing.
 *
 * Note: we use a fixed namespace here, because this file *doesn't*
 * have to use a project namespace. In fact if it does, Google Closure
 * compiler complains that "name <<namespace>> is not defined in the
 * externs".
 */
goog.provide('Keycloak');

/**
 * From here below, it's just regular externs file declarations.
 */
var Keycloak = function(){};
keycloak.accountManagement = function(){};
keycloak.catch = function(options){};
keycloak.clearToken = function(){};
keycloak.createAccountUrl = function(options){};
keycloak.createLoginUrl = function(options){};
keycloak.createLogoutUrl = function(options){};
keycloak.createRegisterUrl = function(options){};
keycloak.error = function(options){};
keycloak.hasRealmRole = function(role){};
keycloak.hasResourceRole = function(role, resource){};
keycloak.init = function(initOptions){};
keycloak.isTokenExpired = function(minValidity){};
keycloak.loadUserInfo = function(){};
keycloak.loadUserProfile = function(){};
keycloak.login = function(options){};
keycloak.logout = function(options){};
keycloak.register = function(options){};
keycloak.success = function(options){};
keycloak.then = function(options){};
keycloak.updateToken = function(minValidity){};

keycloak.idToken;
keycloak.idTokenParsed;
keycloak.idTokenParsed.exp;
