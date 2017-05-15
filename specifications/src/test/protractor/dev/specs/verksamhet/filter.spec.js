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
/*globals describe,it */
'use strict';

var testfw = require('../../testFramework.js');
var pages = testfw.pages;
var features = testfw.features;
var filter = pages.filter;

describe('Verksamhetsfilter: ', function() {
    var toDate = new Date().getFullYear() + '-12';
    var fromDate = '2015-12';

    beforeAll(function() {
        browser.get('/');
        features.user.loginUser1(true);
        pages.verksamhetOverview.isAtPage();
    });

    it('Filterknappen syns', function() {
        expect(filter.button.isDisplayed()).toBeTruthy();
    });

    it('Öppna och stäng filtret', function() {
        filter.button.click();
        expect(filter.content.isDisplayed()).toBeTruthy();

        filter.button.click();
        expect(filter.content.isDisplayed()).toBeFalsy();
    });

    describe('Datum', function() {
        beforeAll(function() {
            // Open filter
            filter.button.click();
            expect(filter.content.isDisplayed()).toBeTruthy();
        });

        it('Fyll i', function() {
            // set date
            filter.fromDate.sendKeys(fromDate);
            filter.toDate.sendKeys(toDate);
            filter.applyBtn.click();

            // Validate status
            filter.isFilterActive();
        });

        it('Kolla så de stämmer', function() {
            // Check values
            filter.button.click();
            expect(filter.getFromDate()).toEqual(fromDate);
            expect(filter.getToDate()).toEqual(toDate);
        });

        it('Kolla infon om att datum inte appliceras ', function() {
            expect(filter.messages.count()).toBe(1);
        });

        it('Ladda om sidan och filtret är hämtat', function() {
            browser.refresh();

            // Validate status
            filter.isFilterActive();

            // Check values
            filter.button.click();
            expect(filter.getFromDate()).toEqual(fromDate);
            expect(filter.getToDate()).toEqual(toDate);
        });

        it('Töm filtret', function() {
            // Clear fields
            filter.dateResetBtn.click();
            expect(filter.getFromDate()).toEqual('');
            expect(filter.getToDate()).toEqual('');

            filter.applyBtn.click();

            // Validate status
            filter.isFilterInactive();
        });
    });

    describe('Fyll i hela filtret', function() {
        var length1, length2, age1, age2, enhet, diagnoses;
        beforeAll(function() {
            // Open filter
            filter.button.click();
            expect(filter.content.isDisplayed()).toBeTruthy();
        });

        it('Datum', function() {
            filter.fromDate.sendKeys(fromDate);
            filter.toDate.sendKeys(toDate);
        });

        it('Sjukskrivningslängd', function() {
            filter.sickLeaveLengthBtn.click();

            var first = filter.sickLeaveLengthList.get(1);
            var second = filter.sickLeaveLengthList.get(2);

            length1 = first.getText();
            length2 = second.getText();

            first.click();
            second.click();

            expect(filter.getChipNames()).toContain(length1);
            expect(filter.getChipNames()).toContain(length2);
        });

        it('Åldersgrupp', function() {
            filter.ageGroupBtn.click();

            var first = filter.ageGroupList.get(1);
            var second = filter.ageGroupList.get(2);

            age1 = first.getText();
            age2 = second.getText();

            first.click();
            second.click();

            expect(filter.getChipNames()).toContain(age1);
            expect(filter.getChipNames()).toContain(age2);
        });

        it('Enheter', function() {
            filter.enhetBtn.click();

            filter.enhetDepth1List.first().click();

            var first = filter.enhetDepth2List.first();
            enhet = first.getText();

            first.element(by.css('input')).click();

            filter.enhetSaveAndCloseBtn.click();

            expect(filter.getChipNames()).toContain(enhet);
        });

        it('Diagnoser', function() {
            filter.diagnosesBtn.click();

            var first = filter.diagnosesDepth0List.first();
            diagnoses = first.getText();

            first.element(by.css('input')).click();

            filter.diagnosesSaveAndCloseBtn.click();

            expect(filter.getChipNames()).toContain(diagnoses);
        });

        it('Applicera filtret', function() {
            filter.applyBtn.click();

            // Validate status
            filter.isFilterActive();
        });

        it('Validera filtret', function() {
            validateActiveFilter();
        });

        it('Gå till en annan rapport och se att filtret är kvar', function() {
            // close filter
            filter.button.sendKeys(protractor.Key.ENTER);

            // Navigate
            pages.navmenu.navBusinessCasesPerMonthLink.click();
            pages.report.isAtPage();


            validateActiveFilter();
        });


        it('Ladda om sidan och filtret är hämtat', function() {
            browser.refresh();

            validateActiveFilter();
        });


        it('Återställ', function() {
            filter.resetBtn.click();

            // Validate status
            filter.isFilterInactive();

            // Close filter
            filter.button.click();
        });

        function validateActiveFilter() {
            // Validate status
            filter.isFilterActive();

            filter.button.sendKeys(protractor.Key.ENTER);

            // Check values
            expect(filter.getFromDate()).toEqual(fromDate);
            expect(filter.getToDate()).toEqual(toDate);

            expect(filter.getChipNames()).toContain(length1);
            expect(filter.getChipNames()).toContain(length2);
            expect(filter.getChipNames()).toContain(age1);
            expect(filter.getChipNames()).toContain(age2);
            expect(filter.getChipNames()).toContain(enhet);
            expect(filter.getChipNames()).toContain(diagnoses);
        }
    });

    it ('öppna listan med alla filter om man har mer än två rader', function() {
        filter.button.click();

        // Select all diagnoses
        filter.diagnosesBtn.click();
        filter.diagnosesSelectAll.click();
        filter.diagnosesSaveAndCloseBtn.click();

        expect(filter.chipsShowAll.isDisplayed()).toBeTruthy();

        filter.chipsShowAll.click();

        expect(filter.chipsShowAllModal.isDisplayed()).toBeTruthy();

        filter.chipsAllCloseBtn.click();
    });

    it('Enhetsvalet syns inte när man bara har en enhet', function() {
        features.user.makeSureNotLoggedIn();
        features.user.loginUser1(false);

        filter.button.click();
        expect(filter.content.isDisplayed()).toBeTruthy();

        expect(filter.enhetBtn.isPresent()).toBeFalsy();

        // Close filter
        filter.button.click();
    });

    it('Ändra url för att ladda filtret', function() {

        filter.isFilterInactive();

        filter.button.click();

        filter.sickLeaveLengthBtn.click();
        var first = filter.sickLeaveLengthList.get(1);
        first.click();

        filter.applyBtn.click();

        filter.isFilterActive();

        browser.getCurrentUrl().then(function(url) {

            var param = getParam('filter', url);

            browser.setLocation('verksamhet/oversikt?vgid=VG1');

            filter.isFilterInactive();

            browser.setLocation('verksamhet/oversikt?vgid=VG1&'+ param);

            filter.isFilterActive();
        });
    });

    afterAll(function() {
        features.user.makeSureNotLoggedIn();
    });
});

function getParam(key, sourceURL) {
    var param,
        paramsArr = [],
        queryString = (sourceURL.indexOf('?') !== -1) ? sourceURL.split('?')[1] : '';

    if (queryString !== '') {
        paramsArr = queryString.split('&');
        for (var i = paramsArr.length - 1; i >= 0; i -= 1) {
            param = paramsArr[i].split('=')[0];
            if (param === key) {
                return paramsArr.splice(i, 1);
            }
        }
    }

    return null;
}
