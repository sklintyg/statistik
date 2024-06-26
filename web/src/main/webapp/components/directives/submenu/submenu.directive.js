/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

angular.module('StatisticsApp').directive('submenu',
    /** @ngInject */
    function() {
      'use strict';

      return {
        restrict: 'E',
        scope: {
          item: '=',
          depth: '=',
          maxDepth: '=',
          hideClick: '&',
          itemClick: '&'
        },
        templateUrl: '/components/directives/submenu/submenu.html',
        link: function($scope) {
          $scope.depthClass = 'depth' + $scope.depth;
          $scope.subDepth = $scope.depth + 1;
          $scope.maxDepthReached = $scope.maxDepth > 0 && $scope.maxDepth <= $scope.subDepth;
          $scope.isLeaf = $scope.maxDepthReached || !($scope.item.visibleSubs && $scope.item.visibleSubs.length > 0);

          $scope.subItemClick = function(node, event) {
            $scope.itemClick({node: node, event: event});
          };

          $scope.subHideClick = function(node, event) {
            $scope.hideClick({node: node, event: event});
          };

          $scope.isChecked = function(item) {
            if (item.numericalId && typeof item.numericalId === 'string' && item.numericalId.indexOf('vardenhet') > -1) {
              if (item.subs.length < 1) {
                return item.isSelected;
              } else {
                return item.isSelected;
              }
            } else {
              return item.allSelected;
            }
          };

          $scope.isIntermediate = function(item) {
            if ($scope.isCareUnit(item)) {
                return !item.isSelected && (item.someSelected || item.allSelected);
            } else {
              return item.someSelected;
            }
          };

          $scope.disableCheckbox = function(item) {
            return $scope.isMunipOrCounty(item);
          };

          $scope.isCareUnit = function(item) {
            return item.numericalId && typeof item.numericalId === 'string' && item.numericalId.indexOf('vardenhet') > -1;
          };

          $scope.isMunipOrCounty = function(item) {
            return item.numericalId && typeof item.numericalId === 'string' && (item.numericalId.indexOf('county') > -1 ||
                item.numericalId.indexOf('munip') > -1);
          };
        }
      };
    });
