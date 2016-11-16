/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
/*globals pages */
/*globals describe,it,helpers */
'use strict';

var testfw = require('../testFramework.js');
var pages = testfw.pages;
var features = testfw.features;
var fakeloginPage = pages.fakeloginPo;
var headerPage = pages.headerPo;

xdescribe('Smoketester av statistiktjänsten: ', function() {

    beforeEach(function() {
        browser.get("http://localhost:8080");
    });

    it('Grundläggande kontroll av samtliga vyer', function() {

        function validateAllNationalReports() {
            console.log("In validateAllNationalReports");
            return pages.navmenu.expandNationalStatisticsToggle().then(function () {
                pages.navmenu.clickNavOverviewLink();
                validateDetailReport('clickNavCasesPerMonthLink', 1, 3, 19);
                validateDetailReport('clickNavDiagnosisGroupsLink', 2, 7, 20);
                validateDetailReport('clickNavDiagnosisSubGroupsLink', 2, 2, 20);
                validateDetailReport('clickNavAgeGroupsLink', 1, 2, 11);
                validateDetailReport('clickNavSickLeaveDegreeLink', 2, 4, 20);
                validateDetailReport('clickNavSickLeaveLengthLink', 1, 2, 8);
                validateDetailReport('clickNavCountyLink', 1, 2, 24);
                validateDetailReport('clickNavCasesPerSexLink', 1, 2, 23);
            });
        }

        function validateAllAboutViews() {
            console.log("In validateAllAboutViews");
            pages.navmenu.expandAboutStatisticsToggle();
            pages.navmenu.clickNavAboutTjanstLink();
            pages.navmenu.clickNavAboutInloggningLink();
            pages.navmenu.clickNavAboutFaqLink();
            pages.navmenu.clickNavAboutContactLink();
        }

        function validateAllVerksamhetReportsForProcessledare() {
            console.log("In validateAllVerksamhetReportsForProcessledare");
            pages.navmenu.expandBusinessStatisticsToggle().then(function() {
                pages.navmenu.clickNavVerksamhetOversiktLink();
                validateDetailReport('clickNavBusinessCasesPerBusinessLink', 1, 2, 3);
                validateDetailReport('clickNavBusinessCasesPerMonthLink', 1, 3, 19);
                validateDetailReport('clickNavBusinessDiagnosisGroupsLink', 2, 7, 20);
                validateDetailReport('clickNavBusinessDiagnosisSubGroupsLink', 2, 4, 20);
                validateDetailReport('clickNavBusinessCompareDiagnosisLink', 1, 0, 0);
                validateDetailReport('clickNavBusinessAgeGroupsLink', 1, 2, 11);
                validateDetailReport('clickNavBusinessSickLeaveDegreeLink', 2, 4, 20);
                validateDetailReport('clickNavBusinessSickLeaveLengthLink', 1, 2, 8);
                validateDetailReport('clickNavBusinessMoreNinetyDaysSickLeaveLink', 1, 3, 19);
                validateDetailReport('clickNavBusinessCasesPerLakaresAlderOchKonLink', 1, 2, 12);
                validateDetailReport('clickNavBusinessCasesPerLakarbefattningLink', 1, 2, 2);
                validateDetailReport('clickNavBusinessDifferentieratIntygandeLink', 2, 3, 20);
            });
        }


        function validateAllSpecificReportsForVerksamhetschef() {
            console.log("In validateAllSpecificReportsForVerksamhetschef");
            pages.navmenu.expandBusinessStatisticsToggle().then(function() {
                validateDetailReport('clickNavBusinessCasesPerLakareLink', 1, 2, 2); //Not visible to processledare
            });
        }

        function validateDetailReport(clickFuncName, expectedNumberOfCharts, expectedNumberOfLegends, expectedRowsInTable) {
            console.log("In validateDetailReport");
            pages.navmenu[clickFuncName]();
            pages.report.verifyAt();
            pages.report.getNumberOfCharts().then(function(result) {expect(result).toBe(expectedNumberOfCharts, 'Number of charts failed: ' +clickFuncName)});
            pages.report.getChartLegendLabels().then(function(result) {expect(result.length).toBe(expectedNumberOfLegends, 'Number of legends failed: ' + clickFuncName)});
            pages.report.getTableRows().then(function(result) {expect(result.length).toBe(expectedRowsInTable, 'Number of table rows failed: ' + clickFuncName)});
        }

        features.user.makeSureNotLoggedIn();
        validateAllNationalReports().then(function() {
            validateAllAboutViews();
        }).then(function() {
            features.user.loginUser1(true);
        }).then(function() {
            validateAllNationalReports();
        }).then(function() {
            validateAllAboutViews();
        }).then(function() {
            validateAllVerksamhetReportsForProcessledare();
        }).then(function() {
            features.user.makeSureNotLoggedIn();
        }).then(function() {
            features.user.loginUser1(false);
        }).then(function() {
            validateAllSpecificReportsForVerksamhetschef();
        });

    });

});
