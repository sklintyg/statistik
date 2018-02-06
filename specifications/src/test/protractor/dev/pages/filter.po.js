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

'use strict';

var Filter = function() {
    this.container = element(by.id('businessFilterContainer'));

    this.messages = this.container.all(by.repeater('resultMessage in filterViewState.messages'));

    this.applyBtn = this.container.element(by.id('filterApplyBtn'));
    this.resetBtn = this.container.element(by.id('filterResetBtn'));

    this.dateSelectBtn = this.container.element(by.id('select-dateintervall'));
    this.fromDate = this.container.element(by.id('filterFromDate'));
    this.getFromDate = function() {
        return this.fromDate.getAttribute('value');
    };
    this.toDate = this.container.element(by.id('filterToDate'));
    this.getToDate = function() {
        return this.toDate.getAttribute('value');
    };
    this.dateResetBtn = this.container.element(by.id('filterDateResetBtn'));

    this.dialog = element(by.css('.tree-multi-selector'));
    this.enhetBtn = this.container.element(by.css('.select-business button'));
    this.enhetDepth1List = this.dialog.all(by.css('.depth1'));
    this.enhetDepth2List = this.dialog.all(by.css('.depth2'));
    this.enhetCloseBtn = element(by.id('treeMultiSelectorCloseBtn'));
    this.enhetSaveAndCloseBtn = element(by.id('treeMultiSelectorSaveBtn'));

    this.diagnosesBtn = this.container.element(by.css('.select-diagnoses  button'));


    this.diagnosesDepth0List = this.dialog.all(by.css('.depth0'));
    this.diagnosesCloseBtn = this.dialog.element(by.id('treeMultiSelectorCloseBtn'));
    this.diagnosesSaveAndCloseBtn = this.dialog.element(by.id('treeMultiSelectorSaveBtn'));
    this.diagnosesSelectAll = this.dialog.element(by.id('select-all-diagnoses'));

    this.sickLeaveLengthBtn = this.container.element(by.css('.select-sjukskrivningslangd button'));
    this.sickLeaveLengthList = this.container.all(by.css('.select-sjukskrivningslangd .multiselect-container a'));

    this.ageGroupBtn = this.container.element(by.css('.select-aldersgrupp button'));
    this.ageGroupList = this.container.all(by.css('.select-aldersgrupp .multiselect-container a'));

    this.showAllActiveBtn = this.container.element(by.id('filter-show-all-btn'));

    this.activeIntervall = element(by.id('filter-active-intervall')).all(by.css('li'));
    this.activeDiganoser = element(by.id('filter-active-dignoser')).all(by.css('li'));
    this.activeEnheter = element(by.id('filter-active-enheter')).all(by.css('li'));
    this.activeAldersgrupper = element(by.id('filter-active-aldersgrupper')).all(by.css('li'));
    this.activeSjukskrivningslangd = element(by.id('filter-active-sjukskrivningslangd')).all(by.css('li'));
    this.activeIntygstyper = element(by.id('filter-active-intygstyper')).all(by.css('li'));

    this.getNames = function(list) {
        return list.map(function(elm) {
            return elm.getText();
        });
    };

    this.filterActiveModal = element(by.id('filterActiveModel'));
    this.filterActiveModalCloseBtn = this.filterActiveModal.element(by.css('button[data-dismiss="modal"]'));

    this.isFilterActive = function() {
        expect(this.showAllActiveBtn.isDisplayed()).toBeTruthy();
    };

    this.isFilterInactive = function() {
        expect(this.showAllActiveBtn.isPresent()).toBeFalsy();
    };

};

module.exports = new Filter();
