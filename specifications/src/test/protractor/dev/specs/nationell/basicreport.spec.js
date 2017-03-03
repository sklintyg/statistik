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
var navmenu = pages.navmenu;

describe('Nationell basic: ', function() {

    beforeAll(function() {
        browser.get('/');
    });

    it('Rätt antal rader', function() {
        navmenu.navOverviewLink.click();
        validateDetailReport('navCasesPerMonthLink', 1, 3, 19);
        validateDetailReport('navDiagnosisGroupsLink', 2, 8, 20);
        validateDetailReport('navDiagnosisSubGroupsLink', 2, 1, 20);
        validateDetailReport('navAgeGroupsLink', 1, 2, 11);
        validateDetailReport('navSickLeaveDegreeLink', 2, 4, 20);
        validateDetailReport('navSickLeaveLengthLink', 1, 2, 8);
        validateDetailReport('navCountyLink', 1, 2, 24);
        validateDetailReport('navCasesPerSexLink', 1, 2, 23);
    });

    function validateDetailReport(clickFuncName, expectedNumberOfCharts, expectedNumberOfLegends, expectedRowsInTable) {
        navmenu[clickFuncName].click();
        pages.report.isAtPage();

        expect(pages.report.getNumberOfCharts()).toBe(expectedNumberOfCharts, 'Number of charts failed: ' +clickFuncName);
        expect(pages.report.getChartLegendLabels().count()).toBe(expectedNumberOfLegends, 'Number of legends failed: ' + clickFuncName);
        expect(pages.report.getTableRows().count()).toBe(expectedRowsInTable, 'Number of table rows failed: ' + clickFuncName);
    }
});
