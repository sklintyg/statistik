'use strict';

var pohelper = require('./pohelper.js');

var HeaderPage = function() {
  this.loginBtn = element(by.id('business-login-btn'));
  this.logoutLink = element(by.id('logoutLink'));
  this.changeVardgivareBtn = element(by.id('changeVardgivareBtn'));
  this.verksamhetsNameLabel = element(by.id('verksamhetsNameLabel'));

  this.settingsLink = element(by.id('settingsLink'));
  this.settingsSaveBtn = element(by.id('settings-save-btn'));
  this.settingsCloseBtn = element(by.id('settings-close-btn'));

  this.changeSetting = function(settingId) {
    return element(by.id('setting-' + settingId.toLowerCase())).element(by.xpath('..')).click();
  };

  this.clickLogin = function() {
    return this.loginBtn.click();
  };

  this.clickLogout = function() {
    pohelper.clickAndWaitForPageRedirect(this.logoutLink);
  };
};

module.exports = new HeaderPage();
