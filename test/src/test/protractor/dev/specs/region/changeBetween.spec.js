/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

/*globals browser */
/*globals describe,it */
'use strict';

var testfw = require('../../testFramework.js');
var pages = testfw.pages;
var features = testfw.features;
var navmenu = pages.navmenu;

describe('Logga in med och utan regionsaccess ', function() {

  beforeAll(function() {
    browser.get('/');
  });

  it('Processledare', function() {
    // Login as user without access
    features.user.loginUser1(true);

    navmenu.nationalTab.click();
    navmenu.verksamhetTab.click();

    pages.verksamhetOverview.isAtPage();

    // Logga ut
    features.user.makeSureNotLoggedIn();

    // Loggain med region
    features.user.loginUser3(false);

    navmenu.nationalTab.click();
    navmenu.regionTab.click();
  });

  afterAll(function() {
    features.user.makeSureNotLoggedIn();
  });
});
