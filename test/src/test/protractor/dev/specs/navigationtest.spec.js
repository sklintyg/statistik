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

var testfw = require('../testFramework.js');
var pages = testfw.pages;
var features = testfw.features;
var navmenu = pages.navmenu;

describe('Navigering i intygsstatistik: ', function() {

    beforeAll(function() {
        browser.get('/');
    });

    describe('Nationellnivå', function() {
        beforeAll(function() {
            navmenu.navOverviewLink.click();
        });

        validateDetailReport('navCasesPerMonthLink', 1);
        validateDetailReport('navDiagnosisGroupsLink', 2);
        validateDetailReport('navDiagnosisSubGroupsLink', 2);
        validateDetailReport('navAgeGroupsLink', 1);
        validateDetailReport('navSickLeaveDegreeLink', 2);
        validateDetailReport('navSickLeaveLengthLink', 1);
        validateDetailReport('navCountyLink', 1);
        validateDetailReport('navCasesPerSexLink', 1);

        validateDetailReport('navNationalIntygPerTypeLink', 2);

        validateDetailReport('navMessagesLink', 2);
        validateDetailReport('navNationalAndelKompletteringarLink', 2);
    });

    describe('Verksamhetsnivå', function() {

        describe('Processledare', function() {
            beforeAll(function() {
                features.user.loginUser1(true);
            });

            verksamhetsNiva();

            validateDetailReport('navBusinessCasesPerBusinessLink', 1);
            validateDetailReport('navBusinessMessagesEnhetLink', 1);

            it('Not present', function() {
                expect(navmenu.navBusinessCasesPerLakareLink.isPresent()).toBeFalsy();
                expect(navmenu.navBusinessMessagesLakareLink.isPresent()).toBeFalsy();
            });
        });

        describe('Del Processledare', function() {
            beforeAll(function() {
                features.user.makeSureNotLoggedIn();
                features.user.loginUser2(false);
            });

            verksamhetsNiva();

            validateDetailReport('navBusinessCasesPerBusinessLink', 1);
            validateDetailReport('navBusinessMessagesEnhetLink', 1);
            validateDetailReport('navBusinessCasesPerLakareLink', 1);
            validateDetailReport('navBusinessMessagesLakareLink', 1);
        });

        describe('Verksamhetschef', function() {
            beforeAll(function() {
                features.user.makeSureNotLoggedIn();
                features.user.loginUser1(false);
            });

            verksamhetsNiva();

            validateDetailReport('navBusinessCasesPerLakareLink', 1);
            validateDetailReport('navBusinessMessagesLakareLink', 1);

            it('Not present', function() {
                expect(navmenu.navBusinessCasesPerBusinessLink.isPresent()).toBeFalsy();
                expect(navmenu.navBusinessMessagesEnhetLink.isPresent()).toBeFalsy();
            });
        });


        function verksamhetsNiva() {
            beforeAll(function() {
                navmenu.navVerksamhetOversiktLink.click();
            });

            validateDetailReport('navBusinessCasesPerMonthLink', 1);
            validateDetailReport('navBusinessDiagnosisGroupsLink', 2);
            validateDetailReport('navBusinessDiagnosisSubGroupsLink', 2);
            validateDetailReport('navBusinessCompareDiagnosisLink', 1);
            validateDetailReport('navBusinessAgeGroupsLink', 1);
            validateDetailReport('navBusinessSickLeaveDegreeLink', 2);
            validateDetailReport('navBusinessSickLeaveLengthLink', 1);
            validateDetailReport('navBusinessMoreNinetyDaysSickLeaveLink', 1);
            validateDetailReport('navBusinessCasesPerLakaresAlderOchKonLink', 1);
            validateDetailReport('navBusinessCasesPerLakarbefattningLink', 1);

            validateDetailReport('navBusinessIntygPerTypeLink', 2);

            validateDetailReport('navBusinessMessagesLink', 2);
            validateDetailReport('navBusinessAndelKompletteringarLink', 2);
        }
    });


    afterAll(function() {
        features.user.makeSureNotLoggedIn();
    });

    function validateDetailReport(menuId, expectedNumberOfCharts) {
        it('Sida: ' + menuId, function() {
            navmenu.clickOnMenu(menuId);
            pages.report.isAtPage();

            expect(pages.report.getNumberOfCharts()).toBe(expectedNumberOfCharts, 'Number of charts failed: ' + menuId);
        });
    }
});
