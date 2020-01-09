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

angular.module('StatisticsApp.filter.directive', []);

angular.module('StatisticsApp.filter.directive')
.directive('businessFilter',
    /** @ngInject */
    function(businessFilterFactory, $location, messageService, statisticsData, moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE, _,
        $rootScope, $filter, filterViewState, $timeout) {
      'use strict';

      return {
        scope: true,
        restrict: 'E',
        controller: function($scope) {
          $scope.filterHashParamName = 'filter';
          linkFunction(_, $scope, businessFilterFactory, $location, messageService, statisticsData,
              moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE, $rootScope, $filter, filterViewState, $timeout);
        },
        templateUrl: '/app/shared/businessfilter/businessFilterView.html'
      };
    });

angular.module('StatisticsApp.filter.directive')
.directive('regionFilter',
    /** @ngInject */
    function(regionFilterFactory, $location, messageService, statisticsData, moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE, _,
        $rootScope, $filter, filterViewState, $timeout) {
      'use strict';

      return {
        scope: true,
        restrict: 'E',
        controller: function($scope) {
          $scope.filterHashParamName = 'regionfilter';
          linkFunction(_, $scope, regionFilterFactory, $location, messageService, statisticsData,
              moment, TIME_INTERVAL_MIN_DATE, TIME_INTERVAL_MAX_DATE, $rootScope, $filter, filterViewState, $timeout);
        },
        templateUrl: '/app/shared/businessfilter/businessFilterView.html'
      };
    });

function linkFunction(_, scope, businessFilter, $location, messageService, statisticsData, moment, TIME_INTERVAL_MIN_DATE,
    TIME_INTERVAL_MAX_DATE, $rootScope, $filter, filterViewState, $timeout) {
  'use strict';

  scope.filterToApply = false;
  scope.filterViewState = filterViewState.get();
  scope.businessFilter = businessFilter;
  scope.useDefaultPeriod = true;
  scope.showDateValidationError = false;
  scope.loadingFilter = false;
  scope.isDateSelectOpen = false;

  //Are the datepicker dialogs open or not
  scope.isFromDateOpen = false;
  scope.isToDateOpen = false;

  //The format of dates date we show in the GUI when the user selects something
  scope.dateFormat = 'yyyy-MM';
  scope.dateFormatPlaceholder = 'ÅÅÅÅ-MM';

  //Configuration and scope functions for datepicker
  scope.dateOptions = {
    minDate: TIME_INTERVAL_MIN_DATE,
    maxDate: TIME_INTERVAL_MAX_DATE,
    showWeeks: false,
    minMode: 'month',
    datepickerMode: 'month'
  };

  scope.dateOptionsTo = _.cloneDeep(scope.dateOptions);

  scope.sidebarState = {
    collapsed: true
  };

  scope.sidebarStateDiagnose = {
    collapsed: true
  };

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
    thirdLevelLabelText: messageService.getProperty('lbl.filter.modal.kategorier', null, '', null, true),
    leavesLevelLabelText: messageService.getProperty('lbl.filter.modal.leaves', null, '', null, true)
  };

  function isValidDate(date) {
    if (date === 'undefined') {
      return false;
    }

    return moment(new Date(date)).isValid() || moment(new Date(date).getUTCDate()).isValid();
  }

  function hasDatepickersValidationError() {
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
  }

  function validateDateFocus() {
    scope.fromDateValidationError = false;
    scope.toDateValidationError = false;
    scope.errorMessage = messageService.getProperty('alert.filter.date-invalid');

    // Fel format
    var inValidFromDate = !scope.myForm.fromDate.$valid || !isValidDate(scope.businessFilter.fromDate);
    var inValidToDate = !scope.myForm.toDate.$valid || !isValidDate(scope.businessFilter.toDate);

    if (inValidFromDate || inValidToDate) {
      if (inValidToDate) {
        scope.toDateValidationError = true;
      }
      if (inValidFromDate) {
        scope.fromDateValidationError = true;
      }

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

  function resetDatePickers() {
    scope.showDateValidationError = false;
    scope.fromDateValidationError = false;
    scope.toDateValidationError = false;
  }

  scope.validateDate = function() {
    scope.showDateValidationError = validateDateFocus();
  };

  scope.makeSelection = function() {
    $timeout(makeSelection, 50);
  };

  function makeSelection() {
    if (hasDatepickersValidationError()) {
      scope.showDateValidationError = true;
      scope.isDateSelectOpen = true;
    } else {
      var formattedFromDate, formattedToDate;
      scope.isDateSelectOpen = false;
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
        sjukskrivningslangd: scope.businessFilter.selectedSjukskrivningslangdIds,
        intygstyper: scope.businessFilter.selectedIntygstyperIds,
        aldersgrupp: scope.businessFilter.selectedAldersgruppIds,
        fromDate: formattedFromDate,
        toDate: formattedToDate,
        useDefaultPeriod: scope.businessFilter.useDefaultPeriod
      };

      var success = function(filterHash) {
        var queryParams = $location.search();
        queryParams[scope.filterHashParamName] = filterHash;
        $location.search(queryParams);
        scope.isFilterCollapsed = !scope.isFilterCollapsed;
        scope.loadingFilter = false;

        scope.businessFilter.aldersgruppSaved = params.aldersgrupp ? _.cloneDeep(params.aldersgrupp) : [];
        scope.businessFilter.sjukskrivningslangdSaved = params.sjukskrivningslangd ? _.cloneDeep(params.sjukskrivningslangd) : [];
        scope.businessFilter.intygstyperSaved = params.intygstyper ? _.cloneDeep(params.intygstyper) : [];
        scope.businessFilter.diagnoserSaved = params.diagnoser ? _.cloneDeep(params.diagnoser) : [];
        scope.businessFilter.geographyBusinessIdsSaved = params.enheter ? _.cloneDeep(params.enheter) : [];
        scope.businessFilter.fromDateSaved = scope.businessFilter.fromDate;
        scope.businessFilter.toDateSaved = scope.businessFilter.toDate;

        scope.filterToApply = scope.businessFilter.filterChanged();
      };

      var error = function() {
        scope.loadingFilter = false;
        throw new Error('Failed to get filter hash value');
      };

      statisticsData.getFilterHash(params).then(success, error);
    }
  }

  scope.resetBusinessFilter = function(form) {
    scope.isDateSelectOpen = false;

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

  scope.$on('$routeChangeSuccess', function() {
    businessFilter.updateHasUserSelection();
  });

  scope.$watch('businessFilter.fromDate', function(newValue) {
    scope.dateOptionsTo.minDate = newValue;
    if (scope.businessFilter.toDate < newValue) {
      scope.businessFilter.toDate = null;
    }

    scope.filterToApply = scope.businessFilter.filterChanged();
  });

  scope.$watch('businessFilter', function(newValue, oldValue, scope) {
    scope.icd10 = newValue.icd10;
  });

  scope.$watch('businessFilter.geography', function(newValue, oldValue, scope) {
    scope.geography = newValue.geography;
  });

  scope.$watch('businessFilter.selectedDiagnoses', function() {
    scope.filterToApply = scope.businessFilter.filterChanged();
  });

  scope.$watch('businessFilter.geographyBusinessIds', function() {
    scope.filterToApply = scope.businessFilter.filterChanged();
  });

  scope.$watch('businessFilter.selectedSjukskrivningslangdIds', function() {
    scope.filterToApply = scope.businessFilter.filterChanged();
  });

  scope.$watch('businessFilter.selectedIntygstyperIds', function() {
    scope.filterToApply = scope.businessFilter.filterChanged();
  });

  scope.$watch('businessFilter.selectedAldersgruppIds', function() {
    scope.filterToApply = scope.businessFilter.filterChanged();
  });

  scope.$watch('businessFilter.toDate', function() {
    scope.filterToApply = scope.businessFilter.filterChanged();
  });
}
