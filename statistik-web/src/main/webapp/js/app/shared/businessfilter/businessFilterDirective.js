/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
'use strict';
angular.module('StatisticsApp.businessFilter.directive', [])
    .directive('businessFilter', ['businessFilter', '$location', 'messageService', 'statisticsData', 'moment', 'TIME_INTERVAL_MIN_DATE', 'TIME_INTERVAL_MAX_DATE', function(businessFilter, $location, messageService, statisticsData, moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE) {
        return {
            scope: true,
            restrict: 'E',
            link: function(scope, element, attrs) {
                scope.filterButtonIdText = "Verksamhet";
                scope.filterHashParamName = "filter";
                linkFunction(scope, businessFilter, $location, messageService, statisticsData, moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE);
            },
            templateUrl: 'js/app/shared/businessfilter/businessFilterView.html'
        };
    }]);

angular.module('StatisticsApp.landstingFilter.directive', [])
    .directive('landstingFilter', ['landstingFilter', '$location', 'messageService', 'statisticsData', 'moment', 'TIME_INTERVAL_MIN_DATE', 'TIME_INTERVAL_MAX_DATE', function(landstingFilter, $location, messageService, statisticsData, moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE) {
        return {
            scope: true,
            restrict: 'E',
            link: function(scope, element, attrs) {
                scope.filterButtonIdText = "Landsting";
                scope.filterHashParamName = "landstingfilter";
                linkFunction(scope, landstingFilter, $location, messageService, statisticsData, moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE);
            },
            templateUrl: 'js/app/shared/businessfilter/businessFilterView.html'
        };
    }]);

function linkFunction(scope, businessFilter, $location, messageService, statisticsData, moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE) {
    //Initially we don't want to see the filter
    scope.isFilterCollapsed = true;

    scope.businessFilter = businessFilter;

    scope.useDefaultPeriod = true;

    scope.showDateValidationError = false;

    function updateGeographyFilterSelectorDataButtonLabelText() {
        scope.geographyFilterSelectorData.buttonLabelText = scope.businessFilter.geographyBusinessIds.length + " av " + scope.businessFilter.businesses.length + " valda";
    }

    scope.$watch('businessFilter.geographyBusinessIds', function(newValue,oldValue,scope) {
        updateGeographyFilterSelectorDataButtonLabelText();
    });
    scope.$watch('businessFilter.businesses', function(newValue,oldValue,scope) {
        updateGeographyFilterSelectorDataButtonLabelText();
    });
    scope.$watch('businessFilter', function(newValue,oldValue,scope) {
        scope.icd10 = newValue.icd10;
    });
    scope.$watch('businessFilter.geography', function(newValue,oldValue,scope) {
        scope.geography = newValue.geography;
    });

    scope.geographyFilterSelectorData = {
        titleText: messageService.getProperty("lbl.filter.val-av-enheter", null, "", null, true),
        buttonLabelText:"Enheter",
        firstLevelLabelText: messageService.getProperty("lbl.filter.modal.lan", null, "", null, true),
        secondLevelLabelText: messageService.getProperty("lbl.filter.modal.kommuner", null, "", null, true),
        thirdLevelLabelText: messageService.getProperty("lbl.filter.modal.enheter", null, "", null, true)
    };

    scope.diagnosisFilterSelectorData = {
        titleText: messageService.getProperty("lbl.filter.val-av-diagnoser", null, "", null, true),
        buttonLabelText: messageService.getProperty("lbl.filter.val-av-diagnoser-knapp", null, "", null, true),
        firstLevelLabelText: messageService.getProperty("lbl.filter.modal.kapitel", null, "", null, true),
        secondLevelLabelText: messageService.getProperty("lbl.filter.modal.avsnitt", null, "", null, true),
        thirdLevelLabelText: messageService.getProperty("lbl.filter.modal.kategorier", null, "", null, true)
    };


    var getVerksamhetstyper = function () {
        var selectedVerksamhettyps = _.filter(businessFilter.verksamhetsTyper, function(verksamhetstyp) {
            return _.contains(businessFilter.selectedVerksamhetTypIds, verksamhetstyp.id);
        });
        var selectedIdsFromVerksamhetstyps = _.map(selectedVerksamhettyps, function (verksamhetstyp) {
            return verksamhetstyp.ids;
        });
        var selectedIdsFromVerksamhetstypsFlattened = _.flatten(selectedIdsFromVerksamhetstyps);
        return _.uniq(selectedIdsFromVerksamhetstypsFlattened);
    };

    var hasFromDateValidationError = function() {
        console.log("*** Start of validation of from date ***");
        console.log("Locale: " + moment.locale());
        console.log("Locale of to date: " + moment(businessFilter.toDate).locale());
        console.log('The from date: ' + businessFilter.fromDate);
        console.log('Now: ' + moment());

        console.log('Is the from date valid, yyyy-MM? ' + moment(businessFilter.fromDate, 'yyyy-MM').isValid() );
        console.log('Is from date before 2013-10? ' + moment(businessFilter.fromDate).isBefore(TIME_INTERVAL_MIN_DATE));
        console.log('Is from date after now? ' + moment(businessFilter.toDate).isAfter(moment()));

        return !businessFilter.fromDate ||
            !moment(businessFilter.fromDate, 'yyyy-MM').isValid() ||
            moment(businessFilter.fromDate).isBefore(TIME_INTERVAL_MIN_DATE) ||
            moment(businessFilter.fromDate).isAfter(moment());
    };

    var hasToDateValidationError = function() {
        console.log("*** Start of validation of to date ***");
        console.log("Locale: " + moment.locale());
        console.log("Locale of to date: " + moment(businessFilter.toDate).locale());
        console.log('The to date: ' + businessFilter.toDate);
        console.log('Now: ' + moment());

        console.log('Is the to date valid, yyyy-MM? ' + moment(businessFilter.toDate, 'yyyy-MM').isValid() );
        console.log('Invalid at? ' + moment(businessFilter.toDate, 'yyyy-MM').invalidAt() );
        console.log('Is to date before the fromdate? ' + moment(businessFilter.toDate).isBefore(businessFilter.fromDate));
        console.log('Is to date after now? ' + moment(businessFilter.toDate).isAfter(moment()));

        return !businessFilter.toDate ||
            !moment(businessFilter.toDate, 'yyyy-MM').isValid() ||
            moment(businessFilter.toDate).isBefore(businessFilter.fromDate) ||
            moment(businessFilter.toDate).isAfter(moment());
    };

    var hasDatepickersValidationError = function() {
        var fromDateValidationError, toDateValidationError;

        if (scope.timeIntervalChecked) {
            fromDateValidationError = hasFromDateValidationError();
            toDateValidationError = hasToDateValidationError();

            if(fromDateValidationError) {
                scope.fromDateValidationError = true;
            }
            if(toDateValidationError) {
                scope.toDateValidationError = true;
            }
        }

        return fromDateValidationError || toDateValidationError ;
    };

    scope.makeSelection = function ($event) {
        var formattedFromDate, formattedToDate;

        if (hasDatepickersValidationError()) {
            scope.showDateValidationError = true;
        } else {
            scope.isFilterCollapsed = !scope.isFilterCollapsed;
            scope.showDateValidationError = false;
            scope.toDateValidationError = false;
            scope.fromDateValidationError = false;

            //Be sure to format the date objects correctly before sending anything to the server
            if (businessFilter.fromDate && businessFilter.toDate) {
                formattedFromDate = moment(businessFilter.fromDate).format('YYYY-MM-DD');
                formattedToDate = moment(businessFilter.toDate);
                formattedToDate = formattedToDate.date(formattedToDate.daysInMonth()).format('YYYY-MM-DD');
            }
            //Only use a non default period if everything is set as expected
            if (scope.timeIntervalChecked && businessFilter.fromDate && businessFilter.toDate) {
                businessFilter.useDefaultPeriod = false;
            } else {
                businessFilter.useDefaultPeriod = true;
            }

            var params = {
                diagnoser: businessFilter.selectedDiagnoses,
                enheter: businessFilter.geographyBusinessIds,
                verksamhetstyper: getVerksamhetstyper(),
                fromDate: formattedFromDate,
                toDate: formattedToDate,
                useDefaultPeriod: businessFilter.useDefaultPeriod
            };

            var success = function (filterHash) {
                var queryParams = $location.search();
                queryParams[scope.filterHashParamName] = filterHash;
                $location.search(queryParams);
            };

            var error = function () {
                throw new Error("Failed to get filter hash value");
            };

            statisticsData.getFilterHash(params).then(success, error);
        }
    };

    var resetDatePickers = function() {
        scope.timeIntervalChecked = false;
        scope.showDateValidationError = false;
        scope.fromDateValidationError = false;
        scope.toDateValidationError = false;
    };

    scope.resetFilter = function() {
        businessFilter.resetSelections();
        resetDatePickers();
        updateGeographyFilterSelectorDataButtonLabelText();
        var queryParams = $location.search();
        delete queryParams[scope.filterHashParamName];
        $location.search(queryParams);
    };

    //Configuration and scope functions for datepicker
    scope.minDate = TIME_INTERVAL_MIN_DATE;
    scope.maxDate = TIME_INTERVAL_MAX_DATE;

    scope.minToDate = businessFilter.fromDate;

    //Are the datepicker dialogs open or not
    scope.isFromDateOpen = false;
    scope.isToDateOpen = false;

    scope.openFromDate = function($event) {
        $event.preventDefault();
        $event.stopPropagation();

        scope.isFromDateOpen = true;
    };

    scope.openToDate = function($event) {
        $event.preventDefault();
        $event.stopPropagation();

        scope.isToDateOpen = true;
    };

    scope.$on('$routeChangeSuccess', function(){
        scope.filterIsActive = !!$location.search()[scope.filterHashParamName];
    });
    scope.filterIsActive = !!$location.search()[scope.filterHashParamName];

    scope.$watch('businessFilter.fromDate', function(newValue, oldValue) {
        scope.minToDate = newValue;
        if(businessFilter.toDate < newValue) {
            businessFilter.toDate = null;
        }
    });

    //The format of dates date we show in the GUI when the user selects something
    scope.format =  'yyyy-MM';

}
