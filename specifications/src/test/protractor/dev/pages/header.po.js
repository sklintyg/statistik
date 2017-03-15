/* globals browser */

'use strict';

var pohelper = require('./pohelper.js');

var HeaderPage = function() {
    this.loginBtn = element(by.id('business-login-btn'));
    this.logoutLink = element(by.id('logoutLink'));
    this.changeVardgivareBtn = element(by.id('changeVardgivareBtn'));
    this.verksamhetsNameLabel = element(by.id('verksamhetsNameLabel'));

    this.clickLogin = function() {
        return this.loginBtn.click();
    };

    this.clickLogout = function() {
        pohelper.clickAndWaitForPageRedirect(this.logoutLink);
    };
};

module.exports = new HeaderPage();
