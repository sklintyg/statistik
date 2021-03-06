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

var VerksamhetOverviewPage = function() {
  this.container = element(by.id('business-overview'));

  this.distributionPerSex = this.container.element(by.id('overview-distribution-per-sex-image'));
  this.alterationChart = this.container.element(by.id('alterationChart'));
  this.diagnosisChart = this.container.element(by.id('diagnosisChart'));
  this.ageChart = this.container.element(by.id('ageChart'));
  this.degreeOfSickLeaveChart = this.container.element(by.id('degreeOfSickLeaveChart'));
  this.sicklengthChart = this.container.element(by.id('sickLeaveLengthChart'));

  this.isAtPage = function() {
    expect(this.container.isDisplayed()).toBeTruthy('Är inte på översikten');
  };
};

module.exports = new VerksamhetOverviewPage();
