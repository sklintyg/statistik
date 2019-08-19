/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

angular.module('StatisticsApp').directive('statCookieBanner',
    /** @ngInject */
    function($uibModal) {
      'use strict';

      return {
        restrict: 'E',
        scope: {},
        templateUrl: '/components/directives/statCookieBanner/statCookieBanner.html',
        controller: function($scope, $timeout, $localStorage) {
          $scope.isOpen = false;

          if (!$localStorage.cookieBannerShown) {
            $timeout(function() {
              $scope.isOpen = true;
            }, 500);
          }

          $scope.onCookieConsentClick = function() {
            $scope.isOpen = false;
            $localStorage.cookieBannerShown = true;
          };
        },
        link: function($scope) {
          $scope.openDialog = function() {
            var modalInstance = $uibModal.open({
              animation: true,
              templateUrl: '/components/directives/statCookieBanner/modal/modal.html',
              controller: 'StatCookieBannerModalCtrl',
              size: 'lg',
              backdrop: 'true',
              scope: $scope,
              resolve: {}
            });

            modalInstance.result.then(function() {
            }, function() {
            });
          };
        }
      };
    }
);
