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

angular.module('StatisticsApp')
.directive('statsHeader', [function() {
  'use strict';
  return {
    scope: {
      isLoggedIn: '=',
      pageHeaderVerksamhetName: '=',
      showBusinessesDetails: '=',
      vgName: '=',
      loginClicked: '&',
      changeVg: '&'
    },
    restrict: 'E',
    templateUrl: '/components/directives/statsHeader/statsHeader.html',
    controller: function($window, $scope, AppModel, UserModel, $uibModal, $http) {
      $scope.AppModel = AppModel;
      $scope.UserModel = UserModel;

      $scope.showSettings = function() {
        return !UserModel.get().isProcessledare;
      };

      $scope.userMenu = {
        expanded: false
      };

      $scope.unitMenu = {
        expanded: false
      };

      $scope.toggleMenu = function($event, menu) {
        $event.stopPropagation();
        menu.expanded = !menu.expanded;

      };

      $scope.changeVardgivare = function(vgId) {
        $scope.changeVg({vgId: vgId});
      };

      $scope.openSettings = function() {
        $uibModal.open({
          animation: true,
          templateUrl: '/components/directives/statSettings/statSettings.html',
          controller: 'StatSettingsCtrl',
          size: 'lg',
          backdrop: 'true'
        });
      };

      $scope.onLogoutClick = function() {
        if (UserModel.get().authenticationMethod === 'FAKE') {
          $http({
            url: '/api/testability/logout',
            method: 'POST'
          })
          .then(function() {
            $window.location.href = '/#/fakelogin';
            $window.location.reload();
          });
        } else {
          $window.location = '/saml/logout/';
        }
      };

    }
  };
}]);