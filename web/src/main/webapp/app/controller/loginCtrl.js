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

angular.module('StatisticsApp').controller('loginCtrl',
    /** @ngInject */
    function ($scope, $rootScope, $uibModal, $location, $cookies, AppModel) {
        'use strict';

        $rootScope.hideNavigationTabs = true;

        $scope.isLoggedIn = $rootScope.isLoggedIn;

        $scope.open = function () {
            $scope.modalInstance = $uibModal.open({
                templateUrl: '/app/views/siths.help.html',
                scope: $scope,
                size: 'lg',
                windowClass: 'login-modal'
            });
        };

        $scope.ok = function () {
            $scope.modalInstance.close();
        };

        $scope.errorUrlParam = $location.search().error;
        $scope.hasActiveStatUrl = $cookies.get('statUrl');

        // Start SAMBI code
        var SELECTED_SAMBI_IDP_KEY = 'selectedSambiIdp';
        var DEFAULT_LOGIN_URL = '/saml/login/alias/' + AppModel.get().defaultAlias;

        $scope.resetCookie = function () {
            $cookies.remove(SELECTED_SAMBI_IDP_KEY);
            $scope.refresh();
        };

        $scope.refresh = function () {
            var storedIdpEntityId = $cookies.get(SELECTED_SAMBI_IDP_KEY);

            $scope.defaultUrl = DEFAULT_LOGIN_URL + '?idp=' + AppModel.get().defaultIDP;
            $scope.hasStoredLogin = typeof storedIdpEntityId !== 'undefined';

            if ($scope.hasStoredLogin) {
                var decodedIdpEntityId = decodeURIComponent(storedIdpEntityId);
                $scope.sambiUrl = DEFAULT_LOGIN_URL + '?idp=' + decodedIdpEntityId;
                $scope.sambiLabel = resolveIdpName(decodedIdpEntityId);
            } else {
                $scope.sambiUrl = '/saml/login/discovery';
                $scope.sambiLabel = 'Logga in via Sambi';
            }
        };

        $scope.refresh();

        function resolveIdpName(entityId) {
            if (typeof AppModel.get().idpMap[entityId] !== 'undefined') {
                return AppModel.get().idpMap[entityId];
            }
            return entityId;
        }

    }
);
