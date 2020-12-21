/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

var fakeloginPo = require('../pages/pages.js').fakeloginPo;
var headerPo = require('../pages/pages.js').headerPo;

var User = function() {
  this.loginUser1 = function(processledare) {
    this.setCookieConsentBannerState(true);
    headerPo.clickLogin();
    fakeloginPo.login('Anna', 'Modig', 'user1', 'fvg1', processledare);
  };

  this.loginUser2 = function(processledare) {
    this.setCookieConsentBannerState(true);
    headerPo.clickLogin();
    fakeloginPo.login('Anna', 'Modig', 'user2', 'fvg1', processledare);
  };

  this.loginUser3 = function(processledare) {
    this.setCookieConsentBannerState(true);
    headerPo.clickLogin();
    fakeloginPo.login('Anna', 'Modig', 'user3', 'fvg3', processledare);
  };

  this.loginUser5 = function(processledare) {
    this.setCookieConsentBannerState(true);
    headerPo.clickLogin();
    fakeloginPo.login('Anna', 'Modig', 'user5', 'fvg1', processledare);
  };

  this.isLoggedIn = function() {
    return headerPo.logoutLink.isPresent();
  };

  this.makeSureNotLoggedIn = function() {
    this.isLoggedIn().then(function(result) {
      if (result) {
        headerPo.clickLogout();
      }
    });

    expect(headerPo.loginBtn.isDisplayed()).toBeTruthy('Loginknappen syns ej');
  };

  this.setCookieConsentBannerState = function(state) {
    //If this flag is missing or set to false in localStorage the cookiebanner will appear.
    // We pre-set this before logging in to avoid having to click on that button
    //for every test.
    return browser.executeScript('window.localStorage.setItem("ngStorage-cookieBannerShown", "' + state + '");');
  };
};

module.exports = new User();
