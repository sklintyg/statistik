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

/*globals browser*/

var FakeLoginPage = function() {
  this.jsonInput = element(by.name('userJsonDisplay'));
  this.loginBtn = element(by.id('login_btn'));

  this.login = function(firstName, lastName, userId, vgId, isProcessledare) {
    this.jsonInput.clear();
    this.jsonInput.sendKeys('{' +
        '"fornamn":"' + firstName + '",' +
        '"efternamn":"' + lastName + '",' +
        '"hsaId":"' + userId + '",' +
        '"vardgivarIdSomProcessLedare":["' + vgId + '"],' +
        '"vardgivarniva":"' + isProcessledare + '"}');

    this.loginBtn.click();

    // Reload to trigger addMockModule after login.
    browser.waitForAngular();
    
    browser.refresh();

  };

  this.isAtPage = function() {
    expect(this.jsonInput.isDisplayed()).toBeTruthy('jsoninput-fältet saknas på fakeinloggningssidan');
    expect(this.loginBtn.isDisplayed()).toBeTruthy('inloggningsknappen saknas på fakeinloggningssidan');
  };
};

module.exports = new FakeLoginPage();