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

angular.module('StatisticsApp').controller('pageCtrl', [ '$scope', '$rootScope', '$window', '$cookies', 'statisticsData', 'businessFilter', '_',
    function ($scope, $rootScope, $window, $cookies, statisticsData, businessFilter, _) {
        var self = this;

        self.getSelectedVerksamhet = function (selectedVerksamhetId, verksamhets) {
            var selectedVerksamhet = _.findWhere(verksamhets, { vardgivarId: selectedVerksamhetId });
            return selectedVerksamhet ? selectedVerksamhet : {name: "Okänd verksamhet"};
        };

        $rootScope.$on('$routeChangeSuccess', function (angularEvent, next, current) {
            var verksamhetId = next.params.verksamhetId;
            $scope.verksamhetIdParam = verksamhetId;
            $scope.currentUrl = window.location.href;

            var d = new Date();
            var currDate = d.getDate();
            var currMonth = d.getMonth() + 1; //Months are zero based
            var currYear = d.getFullYear();
            $scope.currentTime = currYear + "-" + currMonth + "-" + currDate;

            $scope.viewHeader = verksamhetId ? "Verksamhetsstatistik" : "Nationell statistik";

            if ($rootScope.isLoggedIn) {
                if (verksamhetId) {
                    $scope.businessId = verksamhetId;
                    $cookies.verksamhetId = verksamhetId;
                } else if ($cookies.verksamhetId) {
                    $scope.businessId = $cookies.verksamhetId;
                }

                var diagnosExclusionPattern = /.*(oversikt|diagnosgrupp|diagnosavsnitt|jamforDiagnoser).*/;
                $scope.showDiagnosFilter = !diagnosExclusionPattern.test(next.$$route.originalPath);

                businessFilter.resetSelections();

                statisticsData.getLoginInfo(function (loginInfo) {
                    businessFilter.loggedIn(loginInfo.businesses);
                    var v = self.getSelectedVerksamhet($scope.businessId, loginInfo.businesses);
                    $scope.verksamhetName = loginInfo.businesses && loginInfo.businesses.length == 1 ? v.name : v.vardgivarName;
                    $scope.userName = loginInfo.name;
                    $scope.userNameWithAccess = loginInfo.name;

                    $scope.isDelprocessledare = loginInfo.delprocessledare;
                    $scope.isProcessledare = loginInfo.processledare;
                }, function () {
                    $scope.dataLoadingError = true;
                });
            } else {
                businessFilter.loggedOut();
            }
        });

        $scope.isLoggedIn = $rootScope.isLoggedIn;

        $scope.loginClicked = function (url) {
            $window.location.href = "#/" + url;
        }

    }
]);
