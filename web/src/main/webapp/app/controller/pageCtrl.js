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
    /** @ngInject */
    function ($scope, $rootScope, $window, $location, statisticsData, businessFilterFactory, landstingFilterFactory,
        _, ControllerCommons, AppModel, UserModel, filterViewState) {
        'use strict';

        $scope.AppModel = AppModel;
        $scope.UserModel = UserModel;

        $rootScope.$on('$routeChangeSuccess', function () {
            filterViewState.setMessages([]);

            $rootScope.hideNavigationTabs = false;

            $scope.isVerksamhetShowing = ControllerCommons.isShowingVerksamhet($location);
            $scope.isLandstingShowing = ControllerCommons.isShowingLandsting($location);

            if ($scope.isVerksamhetShowing) {
                $scope.viewHeader = 'Verksamhetsstatistik';
            }
            else if ($scope.isLandstingShowing) {
                $scope.viewHeader = 'Landstingsstatistik';
            }
            else {
                $scope.viewHeader = 'Nationell statistik';
            }

            if (ControllerCommons.isShowingProtectedPage($location)) {
                var vgid = $location.search().vgid;
                if (!vgid && UserModel.get().vgs.length > 0) {
                    $location.path('valjVardgivare');
                } else if ($scope.previousVgid !== vgid) {
                    setSelectedVardgivare(vgid);
                    $scope.previousVgid = vgid;
                }
            }
        });

        function setupSelectedVardgivare(userAccessInfo) {
            if (userAccessInfo.vgInfo) {
                UserModel.setUserAccessInfo(userAccessInfo);
                $scope.vgName = userAccessInfo.vgInfo.name;
                if (userAccessInfo.businesses && userAccessInfo.businesses.length === 1) {
                    $scope.pageHeaderVerksamhetName = userAccessInfo.businesses[0].name;
                } else {
                    $scope.pageHeaderVerksamhetName = userAccessInfo.vgInfo.name;
                }
                $scope.showBusinessesDetails = userAccessInfo.businesses && userAccessInfo.businesses.length > 1;
                $rootScope.landstingAvailable = userAccessInfo.vgInfo.landstingsvardgivareWithUpload;
                if ($rootScope.landstingAvailable) {
                    statisticsData.getLandstingFilterInfo(function(landstingFilterInfo) {
                        landstingFilterFactory.setup(landstingFilterInfo.businesses,
                            $location.search().landstingfilter);
                        $scope.isLandstingInfoFetched = true;
                    });
                }
            } else {
                UserModel.resetUserAccessInfo();
                $scope.showBusinessesDetails = false;
                $rootScope.landstingAvailable = false;
                $scope.pageHeaderVerksamhetName = '';
                $scope.vgName = '';
            }
            businessFilterFactory.setup(userAccessInfo.businesses, $location.search().filter);
        }

        $scope.changeVardgivare = function(vgId) {
            $location.url($location.path()); //Clear query params (e.g. filter)
            $location.search('vgid', vgId);
            $location.path('verksamhet');
        };

        function setSelectedVardgivare(vgId) {
            statisticsData.getUserAccessInfo(vgId, function(userAccessInfo) {
                setupSelectedVardgivare(userAccessInfo);
            }, function() {
                $scope.dataLoadingError = true;
            });
        }

        var watchQueryString = $rootScope.$watch('queryString', function() {

            if (AppModel.get().isLoggedIn) {
                businessFilterFactory.selectPreselectedFilter($location.search().filter);
                if ($scope.isLandstingInfoFetched) {
                    landstingFilterFactory.selectPreselectedFilter($location.search().landstingfilter);
                }
            }
        });

        $scope.$on('$destroy', function() {
            if (watchQueryString && typeof watchQueryString.destroy === 'function') {
                watchQueryString.destroy();
            }
        });

        $scope.$watch('AppModel.get().isLoggedIn', function(value){
            if (value) {
                if (!$scope.isLoginInfoFetched) {
                    statisticsData.getLoginInfo(function (loginInfo) {
                        UserModel.setLoginInfo(loginInfo);

                        $scope.loggedInWithoutStatistikuppdrag = !(loginInfo.vgs && loginInfo.vgs.length >= 1);

                        $scope.isLoginInfoFetched = true;

                        var vgid = $location.search().vgid;
                        if (!vgid) {
                            if (loginInfo.vgs.length === 0) {
                                $location.path('/login');
                            } else if (loginInfo.vgs.length === 1) {
                                $scope.changeVardgivare(loginInfo.vgs[0].hsaId);
                            } else {
                                $location.path('/valjVardgivare');
                            }
                        } else {
                            setSelectedVardgivare(vgid);
                            $scope.previousVgid = vgid;
                        }
                    }, function () {
                        $scope.dataLoadingError = true;
                    });
                } else {
                    businessFilterFactory.selectPreselectedFilter($location.search().filter);
                    if ($scope.isLandstingInfoFetched) {
                        landstingFilterFactory.selectPreselectedFilter($location.search().landstingfilter);
                    }
                }
            }
        });

        $scope.loginClicked = function () {
            if ($rootScope.isLoggedIn && !$scope.loggedInWithoutStatistikuppdrag) {
                $location.path('valjVardgivare');
            } else {
                $location.path(AppModel.get().loginUrl);
            }
        };

    }
);
