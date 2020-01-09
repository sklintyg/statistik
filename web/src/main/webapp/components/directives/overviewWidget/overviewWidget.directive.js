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

/* globals Highcharts */
angular.module('StatisticsApp').directive('overviewWidget',
    /** @ngInject */
    function() {
      'use strict';

      return {
        restrict: 'E',
        scope: {
          titleKey: '@',
          tooltipContent: '<',
          chartId: '@',
          options: '<',
          groups: '<',
          tableQuantityPostfix: '@',
          columnTitle1: '@',
          columnTitle2: '@',
          columnTitle3: '@'
        },
        templateUrl: '/components/directives/overviewWidget/overviewWidget.html',
        link: function($scope) {
          var chart = null;

          $scope.$watch('options', function(newValue) {
            if (newValue) {
              chart = Highcharts.chart($scope.chartId, newValue.options, newValue.onComplete);
            }
          });

          $scope.$on('$destroy', function() {
            if (chart && typeof chart.destroy === 'function') {
              chart.destroy();
            }
          });

          $scope.tableFilter = function(item) {
            if (!item.hideInTable) {
              return item;
            }
          };
        }
      };
    });
