/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

var testfw = require('../../testFramework.js');
var pages = testfw.pages;
var features = testfw.features;
var filter = pages.filter;

describe('Verksamhetsfilter: ', function() {

    beforeAll(function() {
        browser.get('/');
        features.user.loginUser1(true);
        pages.verksamhetOverview.isAtPage();
    });

    it('Filterknappen syns', function() {
        expect(filter.button.isDisplayed()).toBeTruthy();
    });

    it('Öppna och stäng filtret', function() {
        filter.button.click();
        expect(filter.content.isDisplayed()).toBeTruthy();

        filter.button.click();
        expect(filter.content.isDisplayed()).toBeFalsy();
    });

    // TODO: Testa att fylla i filtret
    // TODO: Ladda om sidan och se att värderna laddas in
    // TODO: Se att applicera och återställ fungerar
    // TODO: Att fel ifrån servern hamnar uppe i filtret

    afterAll(function() {
        features.user.makeSureNotLoggedIn();
    });
});
