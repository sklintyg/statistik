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
/* globals sinon */

describe('Tests for directive button-filter', function () {
    'use strict';

    var element, outerScope, innerScope, compiled;

    //The module under test
    beforeEach(module('StatisticsApp.businessFilter'));

    //Needed to get serviceinjections from this namespace
    beforeEach(module('StatisticsApp'));

    //All htmlTemplates are wrapped in a module so that we can load them properly during test time, otherwise we get an:
    // Error: Unexpected request: GET js/app/shared/businessfilter/businessFilterView.html
    // No more request expected
    beforeEach(module('htmlTemplates'));

    beforeEach(module(function ($provide) {
        var mockStatistics = {
            getStaticFilterData: function () {
            },
            getFilterHash: function () {
                return {
                    then: function () {}
                };
            }
        };

        //Use $provide to mock a service before it is inject into the service, controller or directive that you want to test
        $provide.value('statisticsData', mockStatistics);
    }));

    beforeEach(inject(function ($rootScope, $compile) {
        /*
         jqLite is really limited (ng 1.2) so if you need more expressive power you will
         have to include jquery before angular in your karma config
         */

        //get the jqLite or jQuery element
        element = angular.element('<business-filter></business-filter>');

        //create a scope (you could use $rootScope.$new(); as well)
        outerScope = $rootScope;

        /*
            $compile is your friend.
            To test a directive, you're going to need to compile a view featuring the directive,
            then probe DOM elements in that view to assert that they've been affected properly.
        */

        //compile the element into a function to
        // process the view.
        compiled = $compile(element);

        //run the compiled view.
        compiled(outerScope);

        /*
         IMPORTANT: Be sure to call scope.$digest() after you make changes to your scope and before you make your assertions!
         */
        outerScope.$digest();

        //Get the isolate scope
        innerScope = element.scope();
    }));

    //STATISTIK-784
    describe('Pick to and from dates in the filter component', function () {

        it('has two datepickers', function () {
            expect(element.find('#filterFromDate').length).toEqual(1);
            expect(element.find('#filterToDate').length).toEqual(1);
        });

        it('has month as the basis for all dates', function () {
            var dateFormat = 'yyyy-MM';

            //Assert that the scope has the format function
            expect(innerScope.format).toBeDefined();
            expect(innerScope.format).toMatch(dateFormat);

            //Assert that the format is reflected in the view
            expect(element.find('#filterFromDate').attr('uib-datepicker-popup')).toMatch(dateFormat);
            expect(element.find('#filterToDate').attr('uib-datepicker-popup')).toMatch(dateFormat);
        });

        it('has a minimum date set on the scope', inject(function (TIME_INTERVAL_MIN_DATE) {
            expect(innerScope.dateOptions.minDate).toBeDefined('Min date must exist');
            expect(innerScope.dateOptions.minDate).toEqual(TIME_INTERVAL_MIN_DATE, 'minDate did not match TIME_INTERVAL_MIN_DATE');
        }));

        it('has a maximum date set on the scope', inject(function (TIME_INTERVAL_MAX_DATE) {
            expect(innerScope.dateOptions.maxDate).toBeDefined('Max date must exist');
            expect(innerScope.dateOptions.maxDate).toEqual(TIME_INTERVAL_MAX_DATE, 'maxDate did not match TIME_INTERVAL_MAX_DATE');
        }));

        it('sets the state of the datepicker to open when it is clicked', function() {
            expect(innerScope.isFromDateOpen).toBeDefined();
            expect(innerScope.isToDateOpen).toBeDefined();

            expect(innerScope.isFromDateOpen).not.toBeTruthy();
            expect(innerScope.isToDateOpen).not.toBeTruthy();

            //click the buttons to open the datepickers
            //element.find('#fromDatePickerBtn').click();
            //expect(innerScope.isFromDateOpen).toBeTruthy();
            //expect(element.find('#filterFromDate').attr('is-open')).toBeTruthy();

            //element.find('#toDatePickerBtn').click();
            //expect(innerScope.isToDateOpen).toBeTruthy();
            //expect(element.find('#filterToDate').attr('is-open')).toBeTruthy();

        });

    });

    describe('Making selections', function() {
        var statisticsData, businessFilter;

        beforeEach(inject(function (_statisticsData_, _businessFilterFactory_) {
            statisticsData = _statisticsData_;
            businessFilter = _businessFilterFactory_;
        }));

        it('sets all needed parameters on params object', function() {
            //given
            var spy = sinon.spy(statisticsData, 'getFilterHash');

            var fromDate = moment('2015-01-01', 'YYYY-MM-DD'), toDate = moment('2015-04-01', 'YYYY-MM-DD');
            innerScope.businessFilter.selectedDiagnoses = ['A00B99', 'D50D89'];
            innerScope.businessFilter.fromDate = fromDate.toDate();
            innerScope.businessFilter.toDate = toDate.toDate();

            //when
            innerScope.makeSelection();

            //then
            expect(spy.calledOnce).toBeTruthy();

            //then the parameters are defined in the params object sent to the server
            expect(spy.getCall(0).args[0].enheter).toBeDefined('Enheter was not defined as expected');
            expect(spy.getCall(0).args[0].diagnoser).toBeDefined('Diagnoser was not defined as expected');
            expect(spy.getCall(0).args[0].verksamhetstyper).toBeDefined('Verksamhetstyper was not defined as expected');
            expect(spy.getCall(0).args[0].fromDate).toBeDefined('fromDate was not defined as expected');
            expect(spy.getCall(0).args[0].toDate).toBeDefined('toDate was not defined as expected');
            expect(spy.getCall(0).args[0].useDefaultPeriod).toBeDefined('useDefault was not defined as expected');
        });

        it('formats the internal date to a datestring with yyyy-mm-dd', inject(function(moment) {

            //given
            var spy = sinon.spy(statisticsData, 'getFilterHash');
            var fromDate = moment('2015-01-01', 'YYYY-MM-DD'), toDate = moment('2015-04-01', 'YYYY-MM-DD');
            innerScope.businessFilter.selectedDiagnoses = ['A00B99', 'D50D89'];
            innerScope.businessFilter.fromDate = fromDate.toDate();
            innerScope.businessFilter.toDate = toDate.toDate();

            //when
            innerScope.makeSelection();

            //Ensure that we sue the right format

            expect(moment(spy.getCall(0).args[0].fromDate, 'YYYY-MM-DD').isValid()).toBeTruthy('From date doesn\'t have the right format');
            expect(moment(spy.getCall(0).args[0].toDate, 'YYYY-MM-DD').isValid()).toBeTruthy('To date doesn\'t have the right format');
        }));

        it('sets toDate to the last of the month', function() {
            //given
            var spy = sinon.spy(statisticsData, 'getFilterHash');
            var fromDate = moment('2015-01-01', 'YYYY-MM-DD'),
                inputToDate = moment('2015-04-01', 'YYYY-MM-DD'),
                expectedToDate = moment('2015-04-30', 'YYYY-MM-DD');

            innerScope.businessFilter.selectedDiagnoses = ['A00B99', 'D50D89'];
            innerScope.businessFilter.fromDate = fromDate.toDate();
            innerScope.businessFilter.toDate = inputToDate.toDate();

            //when
            innerScope.makeSelection();

            //Ensure that we sue the right format
            expect(spy.getCall(0).args[0].toDate).toEqual(expectedToDate.format('YYYY-MM-DD'), 'To date wasn\'t set correctly');

        });
    });

    describe('Validating dates on selection', function() {
        var statisticsData, businessFilter;

        beforeEach(inject(function (_statisticsData_, _businessFilterFactory_) {
            statisticsData = _statisticsData_;
            businessFilter = _businessFilterFactory_;
        }));

        it('will pass validation if we have both to and from date set correct', function() {
            //given
            var fromDate = moment('2015-01-01', 'YYYY-MM-DD'),
                inputToDate = moment('2015-04-01', 'YYYY-MM-DD');

            innerScope.businessFilter.fromDate = fromDate.toDate();
            innerScope.businessFilter.toDate = inputToDate.toDate();

            //when
            innerScope.makeSelection();

            //then
            expect(innerScope.showDateValidationError).toBeDefined();
            expect(innerScope.showDateValidationError).toBeFalsy('showDateValidationError wasn\'t false as expected');
        });

        it('will not pass validation if object is not a date', function() {
            //given
            innerScope.businessFilter.fromDate = '!#€%&/()=?';
            innerScope.businessFilter.toDate = '!#€%&/()=?';

            //when
            innerScope.makeSelection();

            //then
            expect(innerScope.showDateValidationError).toBeTruthy('showDateValidationError wasn\'t true as expected');
        });

        it('will not pass validation if date is of correct format but improper input', function() {

            var fromDate = moment(new Date('2015-13')),
                inputToDate = moment(new Date('2015-13'));

            //given
            innerScope.businessFilter.fromDate = fromDate.toDate();
            innerScope.businessFilter.toDate = inputToDate.toDate();

            //when
            innerScope.makeSelection();

            //then
            expect(innerScope.showDateValidationError).toBeTruthy('showDateValidationError wasn\'t true as expected');
        });

        it('will pass validation if date follows the format yyyy-MM-DD', function() {
            var fromDate = moment('2015-01-01', 'YYYY-MM-DD'),
                inputToDate = moment('2015-01-01', 'YYYY-MM-DD');

            //given
            innerScope.timeIntervalChecked = true;
            innerScope.businessFilter.fromDate = fromDate.toDate();
            innerScope.businessFilter.toDate = inputToDate.toDate();

            //when
            innerScope.makeSelection();

            //then
            expect(innerScope.showDateValidationError).toBeFalsy('showDateValidationError wasn\'t true as expected');
        });

        it('will not pass validation if we have an empty from date', function() {
            var inputToDate = moment('2015-01-01', 'YYYY-MM-DD');

            //given
            innerScope.businessFilter.fromDate = null;
            innerScope.businessFilter.toDate = inputToDate.toDate();
            //when
            innerScope.makeSelection();
            //then
            expect(innerScope.showDateValidationError).toBeTruthy('showDateValidationError wasn\'t true as expected');
        });

        it('will not pass validation if we have an empty to date', function() {
            var inputFromDate = moment('2015-01-01', 'YYYY-MM-DD');
            //given
            innerScope.businessFilter.fromDate = inputFromDate.toDate();
            innerScope.businessFilter.toDate = null;
            //when
            innerScope.makeSelection();
            //then
            expect(innerScope.showDateValidationError).toBeTruthy('showDateValidationError wasn\'t true as expected');
        });

        it('will pass validation if from date is before 2013-10', function() {
            var fromDate = moment('2013-09-01', 'YYYY-MM-DD'),
                inputToDate = moment('2015-12-01', 'YYYY-MM-DD');

            //given
            innerScope.businessFilter.fromDate = fromDate.toDate();
            innerScope.businessFilter.toDate = inputToDate.toDate();
            //when
            innerScope.makeSelection();
            //then
            expect(innerScope.showDateValidationError).toBeFalsy('showDateValidationError wasn\'t true as expected');
        });

        it('will pass validation if from date is variations of 2013-10', function() {
            var fromDate = moment('2013-10-01', 'YYYY-MM-DD').utc(),
                inputToDate = moment('2015-01-01', 'YYYY-MM-DD').utc();

            //given
            innerScope.businessFilter.fromDate = fromDate.toDate();
            innerScope.businessFilter.toDate = inputToDate.toDate();

            //when
            innerScope.makeSelection();

            //then
            expect(innerScope.showDateValidationError).toBeFalsy('showDateValidationError wasn\'t true as expected');
        });

        it('will not pass validation if to date is before the from date', function() {
            var fromDate = moment('2015-05-01', 'YYYY-MM-DD'),
                inputToDate = moment('2015-04-01', 'YYYY-MM-DD');

            //given
            innerScope.businessFilter.fromDate = fromDate.toDate();
            innerScope.businessFilter.toDate = inputToDate.toDate();
            //when
            innerScope.makeSelection();
            //then
            expect(innerScope.showDateValidationError).toBeTruthy('showDateValidationError wasn\'t true as expected');
        });

        it('will pass validation if to date is beyond the current month', function() {
            var fromDate = moment().add(1, 'months').date(1).hours(0).minutes(0).seconds(0),
                inputToDate = moment().add(2, 'months').date(1).hours(0).minutes(0).seconds(0);

            //given
            innerScope.businessFilter.fromDate = fromDate.toDate();
            innerScope.businessFilter.toDate = inputToDate.toDate();
            //when
            innerScope.makeSelection();
            //then
            expect(innerScope.showDateValidationError).toBeFalsy('showDateValidationError wasn\'t true as expected');
        });

        it('will pass validation both dates are empty', function() {
            //given
            innerScope.businessFilter.fromDate = null;
            innerScope.businessFilter.toDate = null;
            //when
            innerScope.makeSelection();
            //then
            expect(innerScope.showDateValidationError).toBeFalsy('showDateValidationError wasn\'t false as expected');
        });
    });

});
