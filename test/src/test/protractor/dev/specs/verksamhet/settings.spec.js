/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
var header = pages.headerPo;
var overview = pages.verksamhetOverview;

describe('Inställningar: ', function() {

    beforeAll(function() {
        browser.get('/');
        features.user.loginUser1(true);
        overview.isAtPage();
    });

    it('Öppna och stäng inställningar', function() {
        header.settingsLink.click();

        header.settingsCloseBtn.click();
    });


    it('Öppna och spara inställningar', function() {
        header.settingsLink.click();

        header.changeSetting('showMessagesPerLakare');

        header.settingsSaveBtn.click();
    });

    it('Återställ tidigare inställning', function() {
        header.settingsLink.click();

        header.changeSetting('showMessagesPerLakare');

        header.settingsSaveBtn.click();
    });

    afterAll(function() {
        features.user.makeSureNotLoggedIn();
    });
});