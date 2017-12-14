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

/*globals browser */
/*globals describe,it */
'use strict';

var testfw = require('../../testFramework.js');
var pages = testfw.pages;
var features = testfw.features;
var navmenu = pages.navmenu;

describe('Navigering landsting: ', function() {

    beforeAll(function() {
        browser.get('/');
    });


    describe('Processledare', function() {

        it('Processledare', function() {
            features.user.loginUser3(true);

            landstingsNiva();

            navmenu.navLandstingUpload.click();
            pages.landstingUpload.isAtPage();
        });

        it('Verksamhetschef', function() {
            features.user.makeSureNotLoggedIn();
            features.user.loginUser3(false);

            landstingsNiva();

            expect(navmenu.navLandstingUpload.isPresent()).toBeFalsy();
        });


        function landstingsNiva() {
            navmenu.landstingTab.click();

            navmenu.navLandstingAbout.click();
            pages.landstingAbout.isAtPage();
        }
    });


    afterAll(function() {
        features.user.makeSureNotLoggedIn();
    });
});
