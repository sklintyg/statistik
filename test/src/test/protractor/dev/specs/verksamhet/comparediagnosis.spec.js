/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

    report.compareDiagnosisSaveAndCloseBtn.click();

    expect(report.getTableRowsLabel().first().getText()).toEqual(diagnoses);
  });

  it('Rapportens töms när man byter indelning', function() {
    report.compareDiagnosisLevelCategory.click();
    expect(report.compareDiagnosisLevelCategory.isSelected()).toBeTruthy();

    expect(report.getTableRowsLabel().count()).toEqual(0);
  });

  it('Finns 3 nivåer i dialogen när man har valt kategori', function() {
    report.compareDiagnosisLevelCategory.click();

    report.compareDiagnosisBtn.click();

    report.compareDiagnosisDepthList(0).first().click();
    report.compareDiagnosisDepthList(1).first().click();

    var first = report.compareDiagnosisDepthList(2).first();
    var diagnoses = first.getText();
    first.element(by.css('input')).click();

    expect(report.compareDiagnosisDepthList(3).count()).toEqual(0);

    report.compareDiagnosisSaveAndCloseBtn.click();

    expect(report.getTableRowsLabel().first().getText()).toEqual(diagnoses);
  });

  it('Finns 2 nivåer i dialogen när man har valt avsnitt', function() {
    report.compareDiagnosisLevelSection.click();

    report.compareDiagnosisBtn.click();

    report.compareDiagnosisDepthList(0).first().click();
    report.compareDiagnosisDepthList(1).first().click();

    var first = report.compareDiagnosisDepthList(1).first();
    var diagnoses = first.getText();
    first.element(by.css('input')).click();

    expect(report.compareDiagnosisDepthList(2).count()).toEqual(0);

    report.compareDiagnosisSaveAndCloseBtn.click();

    expect(report.getTableRowsLabel().first().getText()).toEqual(diagnoses);
  });

  it('Finns 1 nivåer i dialogen när man har valt avsnitt', function() {
    report.compareDiagnosisLevelChapter.click();

    report.compareDiagnosisBtn.click();

    report.compareDiagnosisDepthList(0).first().click();

    var first = report.compareDiagnosisDepthList(0).first();
    var diagnoses = first.getText();
    first.element(by.css('input')).click();

    expect(report.compareDiagnosisDepthList(1).count()).toEqual(0);

    report.compareDiagnosisSaveAndCloseBtn.click();

    expect(report.getTableRowsLabel().first().getText()).toEqual(diagnoses);
  });

  it('Det är möjligt att spara valen först när minst en diagnos är vald', function() {
    //Make sure no selection exists by reloading report
    pages.navmenu.navBusinessAgeGroupsLink.click();
    pages.navmenu.navBusinessCompareDiagnosisLink.click();

    //Open dx selection dialog
    report.compareDiagnosisBtn.click();

    //Save button should only be enabled when at least one diagnosis is selected (INTYG-3922)
    expect(report.compareDiagnosisSaveAndCloseBtn.isEnabled()).toBe(false);

    report.compareDiagnosisDepthList(0).first().element(by.css('input')).click();
    expect(report.compareDiagnosisSaveAndCloseBtn.isEnabled()).toBe(true);

    report.compareDiagnosisDepthList(0).first().element(by.css('input')).click();
    expect(report.compareDiagnosisSaveAndCloseBtn.isEnabled()).toBe(false);

    // Make sure it is possible to close dialog even when save button is disabled (INTYG-3921)
    report.compareDiagnosisCloseBtn.click();
    expect(report.compareDiagnosisSaveAndCloseBtn.isPresent()).toBe(false); //Dialog closed
  });

  it('Valen återställs när dialogen stängts utan att spara enligt INTYG-3993', function() {
    //Make sure no selection exists by reloading report
    pages.navmenu.navBusinessAgeGroupsLink.click();
    pages.navmenu.navBusinessCompareDiagnosisLink.click();

    //Open dx selection dialog
    report.compareDiagnosisBtn.click();

    // No selections are made when dialog is first opened
    expect(report.compareDiagnosisDepthList(0).first().element(by.css('input')).isSelected()).toBe(false);

    //Check first and verify that is actually checked
    report.compareDiagnosisDepthList(0).first().element(by.css('input')).click();
    expect(report.compareDiagnosisDepthList(0).first().element(by.css('input')).isSelected()).toBe(true);

    // Close (without saving) and reopen dialog
    report.compareDiagnosisCloseBtn.click();
    report.compareDiagnosisBtn.click();

    //The first checkbox was selected before but should now have been resetted to deselected (INTYG-3993)
    expect(report.compareDiagnosisDepthList(0).first().element(by.css('input')).isSelected()).toBe(false);

    //Clean up by closing dialog
    report.compareDiagnosisCloseBtn.click();
    expect(report.compareDiagnosisSaveAndCloseBtn.isPresent()).toBe(false); //Dialog closed
  });

  afterAll(function() {
    features.user.makeSureNotLoggedIn();
  });
});
