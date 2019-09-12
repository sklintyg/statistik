/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

angular.module('StatisticsApp').controller('FilterActiveSelectionModalCtrl',
    function($scope, $uibModalInstance, selections, filterViewState, _) {
      'use strict';

      $scope.selections = selections;
      $scope.filterViewState = filterViewState.get();

      var filters = [ {
        id: 'filter-active-intervall',
        list: 'date',
        name: 'Valt tidsintervall',
        icon: 'fa-clock-o',
        disabled: function() {
          return false;
        }
      }, {
        id: 'filter-active-enheter',
        list: 'enheter',
        name: 'Valda enheter',
        icon: 'fa-building-o',
        disabled: function() {
          return false;
        }
      }, {
        id: 'filter-active-dignoser',
        list: 'diagnos',
        name: 'Valda diagnoser',
        icon: 'fa-stethoscope',
        disabled: function() {
          return false;
        }
      }, {
        id: 'filter-active-aldersgrupper',
        list: 'aldersgrupp',
        name: 'Valda åldersgrupper',
        icon: 'fa-users',
        disabled: function() {
          return false;
        }
      }, {
        id: 'filter-active-sjukskrivningslangd',
        list: 'sjukskrivningslangd',
        name: 'Valda sjukskrivningslängder',
        icon: 'fa-calendar',
        disabled: function() {
          return !filterViewState.sjukskrivningslangd;
        }
      }, {
        id: 'filter-active-intygstyper',
        list: 'intygstyper',
        name: 'Valda intygstyper',
        icon: 'fa-file-text-o',
        disabled: function() {
          return !filterViewState.intygstyper;
        }
      }];

      $scope.filters = _.filter(filters, function(filter) {
        if ($scope.selections[filter.list].length > 0) {
          return true;
        }
      });

      $scope.visibleFilterClass = 'number-of-active-filters-' + $scope.filters.length;

      $scope.cancel = function() {
        $uibModalInstance.dismiss();
      };
    }
);
