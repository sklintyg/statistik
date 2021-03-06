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

/*globals browser,protractor */
/*globals describe,it,helpers */
'use strict';

var testfw = require('../testFramework.js');
var pages = testfw.pages;
var features = testfw.features;
var fakeloginPage = pages.fakeloginPo;
var headerPage = pages.headerPo;

describe('Grundläggande tester av intygsstatistik: ', function() {

  beforeAll(function() {
    browser.get('/');
  });

  it('Användaren routas till nationell översikt när man inte anger någon sökväg', function() {
    pages.overview.isAtPage();
  });

  it('Inloggning fungerar', function() {
    features.user.makeSureNotLoggedIn();

    headerPage.clickLogin();
    fakeloginPage.isAtPage();
    features.user.loginUser1(true);

    pages.verksamhetOverview.isAtPage();

    features.user.makeSureNotLoggedIn();
  });

});
