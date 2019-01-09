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
var overview = pages.verksamhetOverview;
var report = pages.report;

describe('Verksamhetsöversikt: ', function() {

    beforeAll(function() {
        browser.get('/');
        features.user.loginUser1(true);
        overview.isAtPage();
    });

    it('Alla diagram syns', function() {
        expect(overview.alterationChart.isDisplayed()).toBeTruthy();
        expect(overview.distributionPerSex.isDisplayed()).toBeTruthy();
        expect(overview.diagnosisChart.isDisplayed()).toBeTruthy();
        expect(overview.ageChart.isDisplayed()).toBeTruthy();
        expect(overview.degreeOfSickLeaveChart.isDisplayed()).toBeTruthy();
        expect(overview.sicklengthChart.isDisplayed()).toBeTruthy();
    });

    describe('Navigera', function() {
        beforeEach(function() {
            navmenu.navVerksamhetOversiktLink.click();
            overview.isAtPage();
        });

        it('Fördelning per kön', function() {
            overview.distributionPerSex.click();
            report.isAtPage('title.sickleave');
        });

        it('Förändring', function() {
            overview.alterationChart.click();
            report.isAtPage('title.sickleave');
        });

        it('Diagnosgrupper', function() {
            overview.diagnosisChart.click();
            report.isAtPage('title.diagnosisgroup');
        });

        it('Ålder', function() {
            overview.ageChart.click();
            report.isAtPage('title.agegroup');
        });

        it('Sjukskrivningsgrad', function() {
            overview.degreeOfSickLeaveChart.click();
            report.isAtPage('title.degreeofsickleave');
        });

        it('Sjukskrivningslängd', function() {
            overview.sicklengthChart.click();
            report.isAtPage('title.sickleavelength');
        });
    });

    afterAll(function() {
        features.user.makeSureNotLoggedIn();
    });
});
