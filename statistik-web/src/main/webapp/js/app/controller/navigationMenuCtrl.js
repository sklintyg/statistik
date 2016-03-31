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

'use strict';

angular.module('StatisticsApp').controller('navigationMenuCtrl', [ '$scope', '$rootScope',
    function ($scope, $rootScope) {

        $scope.organisationMenuLabel = $rootScope.isLoggedIn ? "Verksamhetsstatistik" : "Logga in för verksamhetsstatistik";

        $scope.showNational = false;
        $scope.showLandsting = false;
        $scope.showOperation = false;
        $scope.showAbout = false;
        $scope.isLoggedIn = $rootScope.isLoggedIn;

        $scope.toggleNationalAccordion = function () {
            $scope.showNational = !$scope.showNational;
            $scope.showLandsting = false;
            $scope.showOperation = false;
            $scope.showAbout = false;
        };

        $scope.toggleLandstingAccordion = function () {
            if ($rootScope.isLoggedIn) {
                $scope.showLandsting = !$scope.showLandsting;
                $scope.showOperation = false;
                $scope.showNational = false;
                $scope.showAbout = false;
            }
        };

        $scope.toggleOperationAccordion = function () {
            if ($rootScope.isLoggedIn && $scope.enableVerksamhetMenu) {
                $scope.showOperation = !$scope.showOperation;
                $scope.showLandsting = false;
                $scope.showNational = false;
                $scope.showAbout = false;
            }
        };

        $scope.toggleAboutAccordion = function () {
            $scope.showAbout = !$scope.showAbout;
            $scope.showLandsting = false;
            $scope.showNational = false;
            $scope.showOperation = false;
        };

        $scope.$on('navigationUpdate', function (event, navigationGroupId) {
            if (navigationGroupId === "about-statistics-collapse") {
                $scope.showNational = false;
                $scope.showLandsting = false;
                $scope.showOperation = false;
                $scope.showAbout = true;
            } else if (navigationGroupId === "national-statistics-collapse") {
                $scope.showNational = true;
                $scope.showLandsting = false;
                $scope.showOperation = false;
                $scope.showAbout = false;
            } else if (navigationGroupId === "landsting-statistics-collapse") {
                $scope.showNational = false;
                $scope.showLandsting = true;
                $scope.showOperation = false;
                $scope.showAbout = false;
            } else if (navigationGroupId === "business-statistics-collapse") {
                $scope.showNational = false;
                $scope.showLandsting = false;
                $scope.showOperation = $scope.isLoggedIn ? true : false;
                $scope.showAbout = false;
            } else {
                $scope.showNational = true;
                $scope.showOperation = false;
                $scope.showAbout = false;
            }
        });

    }
]);
