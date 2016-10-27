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
var wait = require('wait.for');

describe('Smoketester av statistiktjänsten: ', function() {

    beforeEach(function() {
        browser.get("http://localhost:8080");
    });

    it('Grundläggande kontroll av samtliga vyer', function() {

        function validateAllNationalReports() {
            console.log("In validateAllNationalReports");

            return pages.navmenu.expandNationalStatisticsToggle().then(function () {
                pages.navmenu.clickNavOverviewLink();
            }).then(function() {
                pages.navmenu.clickNavCasesPerMonthLink();
            }).then(function() {
                pages.navmenu.clickNavDiagnosisGroupsLink();
            }).then(function() {
                pages.navmenu.clickNavDiagnosisSubGroupsLink();
            }).then(function() {
                pages.navmenu.clickNavAgeGroupsLink();
            }).then(function() {
                pages.navmenu.clickNavSickLeaveDegreeLink();
            }).then(function() {
                pages.navmenu.clickNavSickLeaveLengthLink();
            }).then(function() {
                pages.navmenu.clickNavCountyLink();
            }).then(function() {
                pages.navmenu.clickNavCasesPerSexLink();
            });

        }

        function validateAllAboutViews() {
            console.log("In validateAllAboutViews, tests to be implemented");
            // pages.navmenu.expandAboutStatisticsToggle();
            // pages.navmenu.clickNavAboutTjanstLink();
            // pages.navmenu.clickNavAboutInloggningLink();
            // pages.navmenu.clickNavAboutFaqLink();
            // pages.navmenu.clickNavAboutContactLink();
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
            pages.navmenu.expandBusinessStatisticsToggle();
        }).then(function() {
            pages.navmenu.clickNavVerksamhetOversiktLink();
        }).then(function() {
            validateDetailReport('clickNavBusinessCasesPerBusinessLink', 1, 2, 20);
        }).then(function() {
            validateDetailReport('clickNavBusinessCasesPerMonthLink', 1, 3, 19);
        }).then(function() {
            validateDetailReport('clickNavBusinessDiagnosisGroupsLink', 2, 8, 20);
        }).then(function() {
            validateDetailReport('clickNavBusinessDiagnosisSubGroupsLink', 2, 2, 20);
        }).then(function() {
            validateDetailReport('clickNavBusinessCompareDiagnosisLink', 1, 0, 0);
        }).then(function() {
            validateDetailReport('clickNavBusinessAgeGroupsLink', 1, 2, 11);
        }).then(function() {
            validateDetailReport('clickNavBusinessSickLeaveDegreeLink', 2, 4, 20);
        }).then(function() {
            validateDetailReport('clickNavBusinessSickLeaveLengthLink', 1, 2, 8);
        }).then(function() {
            validateDetailReport('clickNavBusinessMoreNinetyDaysSickLeaveLink', 1, 3, 19);
        }).then(function() {
            validateDetailReport('clickNavBusinessCasesPerLakaresAlderOchKonLink', 1, 2, 12);
        }).then(function() {
            validateDetailReport('clickNavBusinessCasesPerLakarbefattningLink', 1, 2, 2);
        }).then(function() {
            validateDetailReport('clickNavBusinessDifferentieratIntygandeLink', 2, 3, 20);
        }).then(function() {
            features.user.makeSureNotLoggedIn();
        }).then(function() {
            features.user.loginUser1(false);
        }).then(function() {
            validateDetailReport('clickNavBusinessCasesPerLakareLink', 1, 2, 1); //Not for processledare
        });

    });

});
