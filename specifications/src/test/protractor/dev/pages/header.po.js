/* globals browser */

'use strict';

var pohelper = require('./pohelper.js');

var loginBtn = element(by.id('business-login-btn'));
var logoutLink = element(by.id('logoutLink'));

var clickLogin = function() {
    return loginBtn.click();
};

var clickLogout = function() {
    pohelper.clickAndWaitForPageRedirect(logoutLink);
};

module.exports = {
    'clickLogin': clickLogin,
    'clickLogout': clickLogout,
    'loginBtn': loginBtn,
    'logoutLink': logoutLink
};
