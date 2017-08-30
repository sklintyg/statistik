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

var testfw = require('../testFramework.js');
var pages = testfw.pages;
var features = testfw.features;
var navmenu = pages.navmenu;

describe('Navigering i statistiktjänsten: ', function() {

    beforeAll(function() {
        browser.get('/');
    });

    it('Nationellnivå', function() {
        navmenu.navOverviewLink.click();
        validateDetailReport('navCasesPerMonthLink', 1);
        validateDetailReport('navDiagnosisGroupsLink', 2);
        validateDetailReport('navDiagnosisSubGroupsLink', 2);
        validateDetailReport('navAgeGroupsLink', 1);
        validateDetailReport('navSickLeaveDegreeLink', 2);
        validateDetailReport('navSickLeaveLengthLink', 1);
        validateDetailReport('navCountyLink', 1);
        validateDetailReport('navCasesPerSexLink', 1);
    });

    it('About', function() {
        var about = pages.about;
        
        navmenu.expandAboutStatisticsToggle();
        navmenu.navAboutTjanstLink.click();
        about.isAtAbout();
        navmenu.navAboutInloggningLink.click();
        about.isAtLogin();
        navmenu.navAboutFaqLink.click();
        about.isAtFaq();
        navmenu.navAboutContactLink.click();
        about.isAtContact();
    });


    describe('Verksamhetsnivå', function() {

        it('Processledare', function() {
            features.user.loginUser1(true);

            verksamhetsNiva();

            validateDetailReport('navBusinessCasesPerBusinessLink', 1, 2, 21);
            expect(navmenu.navBusinessCasesPerLakareLink.isPresent()).toBeFalsy();
        });

        it('Verksamhetschef', function() {
            features.user.makeSureNotLoggedIn();
            features.user.loginUser1(false);

            verksamhetsNiva();

            expect(navmenu.navBusinessCasesPerBusinessLink.isPresent()).toBeFalsy();
            validateDetailReport('navBusinessCasesPerLakareLink', 1, 2, 1);
        });


        function verksamhetsNiva() {
            navmenu.expandBusinessStatisticsToggle();

            navmenu.navVerksamhetOversiktLink.click();

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
        }
    });


    afterAll(function() {
        features.user.makeSureNotLoggedIn();
    });

    function validateDetailReport(clickFuncName, expectedNumberOfCharts) {
        navmenu[clickFuncName].click();
        pages.report.isAtPage();

        expect(pages.report.getNumberOfCharts()).toBe(expectedNumberOfCharts, 'Number of charts failed: ' +clickFuncName);
    }
});
