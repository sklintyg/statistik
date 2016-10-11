/* globals browser */

'use strict';

var loginBtn = element(by.id('business-login-btn'));

var clickLogin = function() {
    loginBtn.click();
};

module.exports = {
    'clickLogin': clickLogin
};
