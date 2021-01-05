/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

'use strict';

var pohelper = require('./pohelper.js');

var NavMenu = function() {
  var that = this;
  this.nationalTab = element(by.id('tab-nationell'));
  this.verksamhetTab = element(by.id('tab-verksamhet'));
  this.regionTab = element(by.id('tab-region'));

  this.nationalStatisticsToggle = element(by.id('national-statistics-toggle'));
  this.regionStatisticsToggle = element(by.id('region-statistics-toggle'));
  this.businessStatisticsToggle = element(by.id('business-statistics-toggle'));

  this.navOverviewLink = element(by.id('navOverviewLink'));
  this.navCasesPerMonthLink = element(by.id('navCasesPerMonthLink'));
  this.navDiagnosisGroupsLink = element(by.id('navDiagnosisGroupsLink'));
  this.navDiagnosisSubGroupsLink = element(by.id('navDiagnosisSubGroupsLink'));
  this.navAgeGroupsLink = element(by.id('navAgeGroupsLink'));
  this.navSickLeaveDegreeLink = element(by.id('navSickLeaveDegreeLink'));
  this.navSickLeaveLengthLink = element(by.id('navSickLeaveLengthLink'));
  this.navCountyLink = element(by.id('navCountyLink'));
  this.navCasesPerSexLink = element(by.id('navCasesPerSexLink'));

  this.navNationalIntygPerTypeLink = element(by.id('navNationalIntygPerTypeLink'));
  this.navNationalAndelKompletteringarLink = element(by.id('navNationalAndelKompletteringarLink'));

  this.navMessagesLink = element(by.id('navMessagesLink'));

  this.navRegionUpload = element(by.id('navRegionUploadLink'));

  this.navVerksamhetOversiktLink = element(by.id('navVerksamhetOversiktLink'));

  this.navBusinessCasesPerBusinessLink = element(by.id('navBusinessCasesPerBusinessLink'));
  this.navBusinessCasesPerMonthLink = element(by.id('navBusinessCasesPerMonthLink'));
  this.navBusinessDiagnosisGroupsLink = element(by.id('navBusinessDiagnosisGroupsLink'));
  this.navBusinessDiagnosisSubGroupsLink = element(by.id('navBusinessDiagnosisSubGroupsLink'));
  this.navBusinessCompareDiagnosisLink = element(by.id('navBusinessCompareDiagnosisLink'));
  this.navBusinessAgeGroupsLink = element(by.id('navBusinessAgeGroupsLink'));
  this.navBusinessSickLeaveDegreeLink = element(by.id('navBusinessSickLeaveDegreeLink'));
  this.navBusinessSickLeaveLengthLink = element(by.id('navBusinessSickLeaveLengthLink'));
  this.navBusinessMoreNinetyDaysSickLeaveLink = element(by.id('navBusinessMoreNinetyDaysSickLeaveLink'));
  this.navBusinessCasesPerLakareLink = element(by.id('navBusinessCasesPerLakareLink')); //Not visible to processledare
  this.navBusinessCasesPerLakaresAlderOchKonLink = element(by.id('navBusinessCasesPerLakaresAlderOchKonLink'));
  this.navBusinessCasesPerLakarbefattningLink = element(by.id('navBusinessCasesPerLakarbefattningLink'));
  this.navBusinessMessagesLakareLink = element(by.id('navBusinessMessagesLakareLink')); //Not visible to processledare
  this.navBusinessMessagesEnhetLink = element(by.id('navBusinessMessagesEnhetLink'));

  this.navAboutTjanstLink = element(by.id('navAboutTjanstLink'));

  this.clickOnMenu = function(id) {
    var menu = element(by.id(id));
    browser.executeScript("arguments[0].scrollIntoView(true);", menu.getWebElement());
    menu.click();
  };

  var clickRegionStatisticsToggle = function() {
    that.regionStatisticsToggle.click();
  };

  this.expandRegionStatisticsToggle = function() {
    expandMenu(this.regionStatisticsToggle, clickRegionStatisticsToggle);
  };

  var clickBusinessStatisticsToggle = function() {
    that.businessStatisticsToggle.click();
  };

  this.expandBusinessStatisticsToggle = function() {
    expandMenu(this.businessStatisticsToggle, clickBusinessStatisticsToggle);
  };

  var clickNationalStatisticsToggle = function() {
    that.nationalStatisticsToggle.click();
  };

  this.expandNationalStatisticsToggle = function() {
    expandMenu(this.nationalStatisticsToggle, clickNationalStatisticsToggle);
  };

  function expandMenu(menu, openFunction) {
    var icon = menu.element(by.css('.statistict-left-menu-expand-icon'));

    pohelper.hasClass(icon, 'collapsed').then(function(value) {
      if (value) {
        openFunction();
      }
    });
  }
};

module.exports = new NavMenu();
