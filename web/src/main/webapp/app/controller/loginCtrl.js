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

angular.module('StatisticsApp').controller('loginCtrl',
    /** @ngInject */
    function($scope, $rootScope, $uibModal, $location, $cookies) {
      'use strict';

      $rootScope.hideNavigationTabs = true;

      $scope.isLoggedIn = $rootScope.isLoggedIn;

      $scope.open = function() {
        $scope.modalInstance = $uibModal.open({
          templateUrl: '/app/views/siths.help.html',
          scope: $scope,
          size: 'lg',
          windowClass: 'login-modal'
        });
      };

      $scope.ok = function() {
        $scope.modalInstance.close();
      };

      $scope.errorUrlParam = $location.search().error;
      $scope.hasActiveStatUrl = $cookies.get('statUrl');

      $scope.defaultUrl = '/saml/login';
    }
);
