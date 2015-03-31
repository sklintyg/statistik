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

angular.module('StatisticsApp').controller('pageCtrl', [ '$scope', '$rootScope', '$window', '$location', 'statisticsData', 'businessFilter', '_',
    function ($scope, $rootScope, $window, $location, statisticsData, businessFilter, _) {
        var self = this;

        self.getSelectedVerksamhet = function (selectedVerksamhetId, verksamhets) {
            var selectedVerksamhet = _.findWhere(verksamhets, { vardgivarId: selectedVerksamhetId });
            return selectedVerksamhet ? selectedVerksamhet : {name: "Okänd verksamhet"};
        };

        $rootScope.$on('$routeChangeSuccess', function (angularEvent, next, current) {
            $scope.verksamhetIdParam = $scope.businessId;

            var d = new Date();
            var currDate = d.getDate();
            var currMonth = d.getMonth() + 1; //Months are zero based
            var currYear = d.getFullYear();
            $scope.currentTime = currYear + "-" + currMonth + "-" + currDate;

            var isVerksamhet = ControllerCommons.isShowingVerksamhet($location);
            $scope.viewHeader = isVerksamhet ? "Verksamhetsstatistik" : "Nationell statistik";

            if ($rootScope.isLoggedIn && !$scope.isLoginInfoFetched) {
                statisticsData.getLoginInfo(function (loginInfo) {
                    businessFilter.setup(loginInfo.businesses, $location.$$search.filter);

                    var v = loginInfo.defaultVerksamhet;
                    $scope.businessId = v.vardgivarId;
                    $scope.verksamhetName = loginInfo.businesses && loginInfo.businesses.length === 1 ? v.name : (loginInfo.processledare ? v.vardgivarName : "");
                    $scope.userName = loginInfo.name;
                    $scope.userNameWithAccess = loginInfo.name;

                    $scope.isDelprocessledare = loginInfo.delprocessledare;
                    $scope.isProcessledare = loginInfo.processledare;

                    $scope.isLoginInfoFetched = true;
                }, function () {
                    $scope.dataLoadingError = true;
                });
            }
        });

        $scope.loginClicked = function (url) {
            $location.path(url);
        };

        $scope.showAlternativeView = function(name) {
            $location.path("/verksamhet/" + name);
        };

    }
]);
