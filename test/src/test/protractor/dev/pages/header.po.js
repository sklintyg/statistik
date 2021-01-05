/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

'use strict';

var pohelper = require('./pohelper.js');

var HeaderPage = function() {
  this.loginBtn = element(by.id('business-login-btn'));
  this.logoutLink = element(by.id('logoutLink'));
  this.changeVardgivareBtn = element(by.id('changeVardgivareBtn'));
  this.verksamhetsNameLabel = element(by.id('verksamhetsNameLabel'));
  this.unitMenuBtn = element(by.id('expand-unit-menu-btn'));
  this.userMenuBtn = element(by.id('expand-user-menu-btn'));

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
