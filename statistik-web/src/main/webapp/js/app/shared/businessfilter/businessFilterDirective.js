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
angular.module('StatisticsApp.filter.directive', []);

angular.module('StatisticsApp.filter.directive')
    .directive('businessFilter', ['businessFilterFactory', '$location', 'messageService', 'statisticsData', 'moment', 'TIME_INTERVAL_MIN_DATE', 'TIME_INTERVAL_MAX_DATE', function(businessFilterFactory, $location, messageService, statisticsData, moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE) {
        return {
            scope: true,
            restrict: 'E',
            link: function(scope, element, attrs) {
                scope.filterButtonIdText = "Verksamhet";
                scope.filterHashParamName = "filter";
                linkFunction(scope, businessFilterFactory, $location, messageService, statisticsData, moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE);
            },
            templateUrl: 'js/app/shared/businessfilter/businessFilterView.html'
        };
    }]);

angular.module('StatisticsApp.filter.directive')
    .directive('landstingFilter', ['landstingFilterFactory', '$location', 'messageService', 'statisticsData', 'moment', 'TIME_INTERVAL_MIN_DATE', 'TIME_INTERVAL_MAX_DATE', function(landstingFilterFactory, $location, messageService, statisticsData, moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE) {
        return {
            scope: true,
            restrict: 'E',
            link: function(scope, element, attrs) {
                scope.filterButtonIdText = "Landsting";
                scope.filterHashParamName = "landstingfilter";
                linkFunction(scope, landstingFilterFactory, $location, messageService, statisticsData, moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE);
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
        var selected = scope.businessFilter.geographyBusinessIds.length;
        var total = scope.businessFilter.businesses.length;

        var text;
        if (selected === 0 || selected === total) {
            text = 'Alla valda';
        }
        else {
            text = selected + " av " + total + " valda";
        }

        scope.geographyFilterSelectorData.buttonLabelText = text;
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

    scope.$watch('businessFilter.useDefaultPeriod', function(newValue, oldvalue, scope) {
        if(!newValue && businessFilter.toDate && businessFilter.fromDate) {
            scope.timeIntervalChecked = true;
        } else {
            scope.timeIntervalChecked = false;
        }
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

    var isValidDate = function isValidDate(date) {
        if(date === 'undefined') {
            return false;
        }

        return moment(new Date(date)).isValid() || moment(new Date(date).getUTCDate()).isValid();
    };

    var hasFromDateValidationError = function() {
        return !businessFilter.fromDate ||
            !isValidDate(businessFilter.fromDate) ||
            moment(businessFilter.fromDate).isBefore(TIME_INTERVAL_MIN_DATE) ||
            moment(businessFilter.fromDate).isAfter(moment());
    };

    var hasToDateValidationError = function() {
        return !businessFilter.toDate ||
            !isValidDate(businessFilter.toDate) ||
            moment(businessFilter.toDate).isBefore(businessFilter.fromDate) ||
            moment(businessFilter.toDate).isAfter(moment().date(moment().daysInMonth()).toDate());
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

angular.module('StatisticsApp.filter.directive').directive('multiselectDropdown', function () {
    function multiselectSize($el) {
        return $('option', $el).length;
    }

    return function(scope, element, attrs) {
        var bf = scope.$parent.businessFilter;
        element.multiselect({
            buttonText: function (options, select) {
                if (options.length === 0 || options.length === multiselectSize(select)) {
                    return 'Alla valda ';
                }

                return options.length + ' av ' + multiselectSize(select) + ' valda ';
            },
            onChange: function (optionElement, checked) {
                optionElement.removeAttr('selected');
                if (checked) {
                    optionElement.prop('selected', 'selected');
                }
                bf.filterChanged();

                element.change();
            },
            includeSelectAllOption: true,
            selectAllText: "Markera alla"
        });

        // Watch for any changes to the length of our select element
        scope.$watch(function () {
            return element[0].length;
        }, function () {
            element.multiselect('rebuild');
        });

        // Watch for any changes from outside the directive and refresh
        scope.$watchCollection(attrs.ngModel, function () {
            element.multiselect('refresh');
        });
    };
});

angular.module('StatisticsApp.filter.directive').directive("filterButton", function () {
    return {
        restrict: "E",
        template:
        '<button id="show-hide-filter-btn" type="button" class="btn btn-small pull-right" ng-class="{filterbtnactivefilter: filterIsActive}" ng-click="isFilterCollapsed = !isFilterCollapsed">' +
        '<i class="glyphicon" ng-class="{glyphiconDownSign: isFilterCollapsed, glyphiconUpSign: !isFilterCollapsed}"></i> {{!isFilterCollapsed ? "Dölj filter" : "Visa filter"}}<span style="font-size: 12px; font-style: italic;"><br/>{{filterButtonIdText}}</span><span ng-show="filterIsActive" style="font-size: 12px; font-style: italic;"><br/>Val gjorda</span>' +
        '</button>'
    };
});

