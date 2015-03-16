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

angular.module('StatisticsApp').controller('businessLandingPageCtrl', ['$scope', '$location', '$cookies', 'statisticsData',
    function ($scope, $location, $cookies, statisticsData) {

        statisticsData.getLoginInfo(function (loginInfo) {
            if (loginInfo.businesses.length < 1) {
                $location.path("login");
            } else {
                $location.path("verksamhet/" + loginInfo.defaultVerksamhet.vardgivarId + "/oversikt");
            }
        }, function () {
            $scope.dataLoadingError = true;
        });

    }
]);
