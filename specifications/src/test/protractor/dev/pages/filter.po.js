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

'use strict';

var Filter = function() {
    this.container = element(by.id('businessFilterContainer'));
    this.button = this.container.element(by.id('show-hide-filter-btn'));
    this.content = this.container.element(by.id('statistics-filter-container'));

    this.messages = this.container.all(by.repeater('resultMessage in messages'));

    this.applyBtn = this.container.element(by.id('filterApplyBtn'));
    this.resetBtn = this.container.element(by.id('filterResetBtn'));

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


    this.chipsList = this.container.all(by.repeater('chip in shownChips'));
    this.chipsShowAll = this.container.element(by.id('filter-ships-show-all-btn'));
    this.chipsShowAllModal = element(by.id('filterChipModel'));
    this.chipsAllCloseBtn = this.chipsShowAllModal.element(by.css('button[data-dismiss="modal"]'));

    this.getChipNames = function() {
        return this.chipsList.map(function(elm) {
            return elm.getText();
        });
    };

    this.isFilterActive = function() {
        expect(this.button.getAttribute('class')).toContain('filterbtnactivefilter');
    };

    this.isFilterInactive = function() {
        expect(this.button.getAttribute('class')).not.toContain('filterbtnactivefilter');
    };

};

module.exports = new Filter();
