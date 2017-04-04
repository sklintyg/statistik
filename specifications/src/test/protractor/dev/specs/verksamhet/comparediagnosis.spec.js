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
var report = pages.report;

describe('Jämför diagnoser: ', function() {

    beforeAll(function() {
        browser.get('/');
        features.user.loginUser1(true);
        pages.verksamhetOverview.isAtPage();

        pages.navmenu.navBusinessCompareDiagnosisLink.click();
    });

    it('Kategori är vald som default', function() {
       expect(report.compareDiagnosisLevelCategory.isSelected()).toBeTruthy();
    });

    it('Kommer ihåg valet mellan sidor', function() {
        report.compareDiagnosisLevelCode.click();
        expect(report.compareDiagnosisLevelCode.isSelected()).toBeTruthy();

        pages.navmenu.navBusinessAgeGroupsLink.click();

        pages.navmenu.navBusinessCompareDiagnosisLink.click();
        expect(report.compareDiagnosisLevelCode.isSelected()).toBeTruthy();
    });

    it('Finns 4 nivåer i dialogen när man har valt code', function() {
        report.compareDiagnosisLevelCode.click();

        report.compareDiagnosisBtn.click();

        report.compareDiagnosisDepthList(0).first().click();
        report.compareDiagnosisDepthList(1).first().click();
        report.compareDiagnosisDepthList(2).first().click();

        var first = report.compareDiagnosisDepthList(3).first();
        var diagnoses = first.getText();
        first.element(by.css('input')).click();

        report.compareDiagnosisCloseBtn.click();

        expect(report.getTableRowsLabel().first().getText()).toEqual(diagnoses);
    });

    it('Rapportens töms när man byter indelning', function() {
        report.compareDiagnosisLevelCategory.click();
        expect(report.compareDiagnosisLevelCategory.isSelected()).toBeTruthy();

        expect(report.getTableRowsLabel().count()).toEqual(0);
    });

    it('Finns 3 nivåer i dialogen när man har valt category', function() {
        report.compareDiagnosisLevelCategory.click();

        report.compareDiagnosisBtn.click();

        report.compareDiagnosisDepthList(0).first().click();
        report.compareDiagnosisDepthList(1).first().click();

        var first = report.compareDiagnosisDepthList(2).first();
        var diagnoses = first.getText();
        first.element(by.css('input')).click();

        expect(report.compareDiagnosisDepthList(3).count()).toEqual(0);

        report.compareDiagnosisCloseBtn.click();

        expect(report.getTableRowsLabel().first().getText()).toEqual(diagnoses);
    });

    afterAll(function() {
        features.user.makeSureNotLoggedIn();
    });
});