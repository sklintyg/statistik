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

angular.module('StatisticsApp.filter.directive', []);

angular.module('StatisticsApp.filter.directive')
    .directive('businessFilter',
        /** @ngInject */
        function(businessFilterFactory, $location, messageService, statisticsData, moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE, _) {
        'use strict';

        return {
            scope: true,
            restrict: 'E',
            link: function(scope) {
                scope.filterButtonIdText = 'Verksamhet';
                scope.filterHashParamName = 'filter';
                linkFunction(_, scope, businessFilterFactory, $location, messageService, statisticsData,
                                moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE);
            },
            templateUrl: '/app/shared/businessfilter/businessFilterView.html'
        };
    });

angular.module('StatisticsApp.filter.directive')
    .directive('landstingFilter',
        /** @ngInject */
        function(landstingFilterFactory, $location, messageService, statisticsData, moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE, _) {
        'use strict';

        return {
            scope: true,
            restrict: 'E',
            link: function(scope) {
                scope.filterButtonIdText = 'Landsting';
                scope.filterHashParamName = 'landstingfilter';
                linkFunction(_, scope, landstingFilterFactory, $location, messageService, statisticsData,
                                moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE);
            },
            templateUrl: '/app/shared/businessfilter/businessFilterView.html'
        };
    });

function linkFunction(_, scope, businessFilter, $location, messageService, statisticsData, moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE) {
    'use strict';

    //Initially we don't want to see the filter
    scope.isFilterCollapsed = true;
    scope.businessFilter = businessFilter;
    scope.useDefaultPeriod = true;
    scope.showDateValidationError = false;
    scope.loadingFilter = false;

    scope.$watch('businessFilter', function(newValue,oldValue,scope) {
        scope.icd10 = newValue.icd10;
    });
    scope.$watch('businessFilter.geography', function(newValue,oldValue,scope) {
        scope.geography = newValue.geography;
    });

    scope.geographyFilterSelectorData = {
        titleText: messageService.getProperty('lbl.filter.val-av-enheter', null, '', null, true),
        buttonLabelText: 'Enheter',
        firstLevelLabelText: messageService.getProperty('lbl.filter.modal.lan', null, '', null, true),
        secondLevelLabelText: messageService.getProperty('lbl.filter.modal.kommuner', null, '', null, true),
        thirdLevelLabelText: messageService.getProperty('lbl.filter.modal.enheter', null, '', null, true)
    };

    scope.diagnosisFilterSelectorData = {
        titleText: messageService.getProperty('lbl.filter.val-av-diagnoser', null, '', null, true),
        buttonLabelText: messageService.getProperty('lbl.filter.val-av-diagnoser-knapp', null, '', null, true),
        firstLevelLabelText: messageService.getProperty('lbl.filter.modal.kapitel', null, '', null, true),
        secondLevelLabelText: messageService.getProperty('lbl.filter.modal.avsnitt', null, '', null, true),
        thirdLevelLabelText: messageService.getProperty('lbl.filter.modal.kategorier', null, '', null, true)
    };


    var getVerksamhetstyper = function () {
        var selectedVerksamhettyps = _.filter(scope.businessFilter.verksamhetsTyper, function(verksamhetstyp) {
            return _.contains(scope.businessFilter.selectedVerksamhetTypIds, verksamhetstyp.id);
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

    var hasDatepickersValidationError = function() {
        scope.errorMessage = messageService.getProperty('alert.filter.date-invalid');

        // Fel format
        var validate = validateDateFocus();

        if (validate) {
            return validate;
        }

        if (scope.businessFilter.fromDate || scope.businessFilter.toDate) {
            // Till är tomt men inte från
            if (scope.businessFilter.fromDate && !scope.businessFilter.toDate || !isValidDate(businessFilter.toDate)) {
                scope.errorMessage = messageService.getProperty('alert.filter.date.empty');
                scope.toDateValidationError = true;
                return true;
            }

            // Från är tomt men inte till
            if (!scope.businessFilter.fromDate && scope.businessFilter.toDate || !isValidDate(businessFilter.fromDate)) {
                scope.errorMessage = messageService.getProperty('alert.filter.date.empty');
                scope.fromDateValidationError = true;
                return true;
            }
        }

        return false;
    };

    function validateDateFocus() {
        scope.fromDateValidationError = false;
        scope.toDateValidationError = false;
        scope.errorMessage = messageService.getProperty('alert.filter.date-invalid');

        // Fel format
        if (!scope.myForm.fromDate.$valid || !isValidDate(scope.businessFilter.fromDate) || !scope.myForm.toDate.$valid || !isValidDate(scope.businessFilter.toDate)) {
            return true;
        }

        // Från är satt till efter till
        if (moment(scope.businessFilter.toDate).isBefore(scope.businessFilter.fromDate)) {
            scope.errorMessage = messageService.getProperty('alert.filter.date.wrong-order');
            scope.fromDateValidationError = true;
            scope.toDateValidationError = true;
            return true;
        }

        return false;
    }

    scope.validateDate = function() {
        scope.showDateValidationError = validateDateFocus();
    };

    scope.makeSelection = function () {
        var formattedFromDate, formattedToDate;

        if (hasDatepickersValidationError()) {
            scope.showDateValidationError = true;
        } else {
            scope.showDateValidationError = false;
            scope.loadingFilter = true;

            //Be sure to format the date objects correctly before sending anything to the server
            if (scope.businessFilter.fromDate && scope.businessFilter.toDate) {
                formattedFromDate = moment(scope.businessFilter.fromDate).format('YYYY-MM-DD');
                formattedToDate = moment(scope.businessFilter.toDate);
                formattedToDate = formattedToDate.date(formattedToDate.daysInMonth()).format('YYYY-MM-DD');
            }
            //Only use a non default period if everything is set as expected
            if (scope.businessFilter.fromDate && scope.businessFilter.toDate) {
                scope.businessFilter.useDefaultPeriod = false;
            } else {
                scope.businessFilter.useDefaultPeriod = true;
            }

            var params = {
                diagnoser: scope.businessFilter.selectedDiagnoses,
                enheter: scope.businessFilter.geographyBusinessIds,
                verksamhetstyper: getVerksamhetstyper(),
                sjukskrivningslangd: scope.businessFilter.selectedSjukskrivningslangdIds,
                aldersgrupp: scope.businessFilter.selectedAldersgruppIds,
                fromDate: formattedFromDate,
                toDate: formattedToDate,
                useDefaultPeriod: scope.businessFilter.useDefaultPeriod
            };

            var success = function (filterHash) {
                var queryParams = $location.search();
                queryParams[scope.filterHashParamName] = filterHash;
                $location.search(queryParams);
                scope.isFilterCollapsed = !scope.isFilterCollapsed;
                scope.loadingFilter = false;

                scope.businessFilter.aldersgruppSaved = angular.copy(params.aldersgrupp);
                scope.businessFilter.sjukskrivningslangdSaved = angular.copy(params.sjukskrivningslangd);
                scope.businessFilter.diagnoserSaved = angular.copy(params.diagnoser);
                scope.businessFilter.geographyBusinessIdsSaved = angular.copy(params.enheter);
            };

            var error = function () {
                scope.loadingFilter = false;
                throw new Error('Failed to get filter hash value');
            };

            statisticsData.getFilterHash(params).then(success, error);
        }
    };

    var resetDatePickers = function() {
        scope.showDateValidationError = false;
        scope.fromDateValidationError = false;
        scope.toDateValidationError = false;
    };

    scope.resetBusinessFilter = function(form) {
        if (form) {
            form.$setPristine();
            form.$setUntouched();
        }

        scope.businessFilter.resetSelections();
        resetDatePickers();
        var queryParams = $location.search();
        delete queryParams[scope.filterHashParamName];
        $location.search(queryParams);
    };

    scope.clearDates = function() {
        scope.businessFilter.fromDate = null;
        scope.businessFilter.toDate = null;

        resetDatePickers();
    };

    //Are the datepicker dialogs open or not
    scope.isFromDateOpen = false;
    scope.isToDateOpen = false;

    scope.$on('$routeChangeSuccess', function(){
        scope.filterIsActive = !!$location.search()[scope.filterHashParamName];
    });
    scope.filterIsActive = !!$location.search()[scope.filterHashParamName];

    scope.$watch('businessFilter.fromDate', function(newValue) {
        scope.dateOptionsTo.minDate = newValue;
        if(scope.businessFilter.toDate < newValue) {
            scope.businessFilter.toDate = null;
        }
    });

    //The format of dates date we show in the GUI when the user selects something
    scope.format =  'yyyy-MM';
    scope.dateFormatPlaceholder= 'ÅÅÅÅ-MM';

    //Configuration and scope functions for datepicker
    scope.dateOptions = {
        minDate: TIME_INTERVAL_MIN_DATE,
        maxDate: TIME_INTERVAL_MAX_DATE,
        showWeeks: false,
        minMode: 'month',
        datepickerMode: 'month'
    };

    scope.dateOptionsTo = angular.copy(scope.dateOptions);

    scope.isVerksamhetTypeCollapsed = true;

    scope.selectVerksamhetsTyp = function(verksamhetsTyp) {
        var checked = !verksamhetsTyp.checked;

        angular.forEach(verksamhetsTyp.units, function(businesse) {
            if (checked || businesse.verksamhetsTyper.length === 1) {
                businesse.allSelected = checked;
            } else {
                var shouldUncheck = true;
                angular.forEach(scope.businessFilter.verksamhetsTyper, function(type) {
                    if (type !== verksamhetsTyp && type.checked &&
                        type.units.indexOf(businesse) > -1) {
                        shouldUncheck = false;
                    }
                });
                if (shouldUncheck) {
                    businesse.allSelected = false;
                }
            }
        });

        verksamhetsTyp.checked = checked;

        scope.$broadcast('updateSelections');
    };

    scope.$on('selectionsChanged', function() {
        scope.businessFilter.updateSelectionVerksamhetsTyper(scope.businessFilter.verksamhetsTyper);
    });
}

angular.module('StatisticsApp.filter.directive').directive('multiselectDropdown',
    /** @ngInject */
    function () {
    'use strict';

    return function(scope, element, attrs) {
        var text = attrs.multiselectDropdown;
        var icon = attrs.multiselectDropdownIcon;
        element.multiselect({
            buttonText: function () {
                return '<span class="fa ' + icon + '"></span> ' + text;
            },
            onChange: function (optionElement, checked) {
                if (optionElement) {
                    optionElement.removeAttr('selected');
                    if (checked) {
                        optionElement.prop('selected', 'selected');
                    }
                }

                element.change();
            },
            enableHTML: true,
            includeSelectAllOption: true,
            selectAllText: 'Markera alla'
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

angular.module('StatisticsApp.filter.directive').directive('filterButton', function () {
    'use strict';

    return {
        restrict: 'E',
        template:
        '<button id="show-hide-filter-btn" type="button" class="btn btn-small center-block" ' +
            'ng-class="{filterbtnactivefilter: filterIsActive}" ng-click="isFilterCollapsed = !isFilterCollapsed">' +
        '<i class="glyphicon" ng-class="{\'glyphicon-chevron-down\': isFilterCollapsed, \'glyphicon-chevron-up\': !isFilterCollapsed}"></i> ' +
            '{{!isFilterCollapsed ? "Stäng filter" : "Öppna filter"}}<span style="font-size: 12px; font-style: italic;"><br/>' +
            '{{filterButtonIdText}}</span><span ng-show="filterIsActive" style="font-size: 12px; font-style: italic;"><br/>Val gjorda</span>' +
        '</button>'
    };
});

