/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
            controller: function($scope, AppModel, UserModel, $uibModal) {
                $scope.AppModel = AppModel;
                $scope.UserModel = UserModel;

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

            }
        };
    }]);
