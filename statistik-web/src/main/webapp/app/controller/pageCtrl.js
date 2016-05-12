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

angular.module('StatisticsApp').controller('pageCtrl',
    [ '$scope', '$rootScope', '$window', '$location', 'statisticsData', 'businessFilterFactory', 'landstingFilterFactory', '_', 'ControllerCommons',
    function ($scope, $rootScope, $window, $location, statisticsData, businessFilterFactory, landstingFilterFactory, _, ControllerCommons) {
        'use strict';

        var self = this;

        self.getSelectedVerksamhet = function (selectedVerksamhetId, verksamhets) {
            var selectedVerksamhet = _.findWhere(verksamhets, { vardgivarId: selectedVerksamhetId });
            return selectedVerksamhet ? selectedVerksamhet : {name: 'Okänd verksamhet'};
        };

        $rootScope.$on('$routeChangeSuccess', function () {
            var d = new Date();
            var currDate = d.getDate();
            var currMonth = d.getMonth() + 1; //Months are zero based
            var currYear = d.getFullYear();
            $scope.currentTime = currYear + '-' + currMonth + '-' + currDate;

            $scope.isVerksamhetShowing = ControllerCommons.isShowingVerksamhet($location);
            $scope.isLandstingShowing = ControllerCommons.isShowingLandsting($location);

            if ($scope.isVerksamhetShowing) {
                $scope.viewHeader = 'Verksamhetsstatistik';
            }
            else if (ControllerCommons.isShowingLandsting($location)) {
                $scope.viewHeader = 'Landstingsstatistik';
            }
            else {
                $scope.viewHeader = 'Nationell statistik';
            }

            if ($rootScope.isLoggedIn) {
                if (!$scope.isLoginInfoFetched) {
                    statisticsData.getLoginInfo(function (loginInfo) {
                        businessFilterFactory.setup(loginInfo.businesses, $location.$$search.filter);

                        var v = loginInfo.defaultVerksamhet;

                        $scope.verksamhetName = '';

                        if (v) {
                            if (loginInfo.businesses && loginInfo.businesses.length === 1) {
                                $scope.verksamhetName = v.name;
                            }
                            else if (loginInfo.processledare) {
                                $scope.verksamhetName = v.vardgivarName;
                            }
                        }

                        $scope.userName = loginInfo.name;
                        $scope.userNameWithAccess = loginInfo.name;

                        $scope.loggedInWithoutStatistikuppdrag = !(loginInfo.businesses && loginInfo.businesses.length >= 1);

                        $scope.isDelprocessledare = loginInfo.delprocessledare;
                        $scope.isProcessledare = loginInfo.processledare;

                        $scope.hasLandstingAccess = loginInfo.landstingsvardgivare;
                        $rootScope.landstingAvailable = loginInfo.landstingsvardgivareWithUpload;
                        $scope.isLandstingAdmin = loginInfo.landstingAdmin;

                        $scope.enableVerksamhetMenu = loginInfo.businesses && loginInfo.businesses.length >= 1;

                        if ($rootScope.landstingAvailable) {
                            statisticsData.getLandstingFilterInfo(function (landstingFilterInfo) {
                                landstingFilterFactory.setup(landstingFilterInfo.businesses, $location.$$search.landstingfilter);
                                $scope.isLandstingInfoFetched = true;
                            });
                        }

                        $scope.isLoginInfoFetched = true;
                    }, function () {
                        $scope.dataLoadingError = true;
                    });
                } else {
                    businessFilterFactory.selectPreselectedFilter($location.$$search.filter);
                    if ($scope.isLandstingInfoFetched) {
                        landstingFilterFactory.selectPreselectedFilter($location.$$search.landstingfilter);
                    }
                }
            }
        });

        $scope.loginClicked = function (url) {
            $location.path(url);
        };
    }
]);
