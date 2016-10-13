/* globals browser */

'use strict';

var features = require('../features/features.js');

var loginBtn = element(by.id('business-login-btn'));
var logoutLink = element(by.id('logoutLink'));

var clickLogin = function() {
    loginBtn.click();
};

var clickLogout = function() {
    features.helper.clickAndWaitForPageRedirect(logoutLink);
};

module.exports = {
    'clickLogin': clickLogin,
    'clickLogout': clickLogout,
    'loginBtn': loginBtn,
    'logoutLink': logoutLink
};
