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
        _, ControllerCommons, AppModel, UserModel) {
        'use strict';

        var self = this;

        self.getSelectedVerksamhet = function (selectedVerksamhetId, verksamhets) {
            var selectedVerksamhet = _.findWhere(verksamhets, { vardgivarId: selectedVerksamhetId });
            return selectedVerksamhet ? selectedVerksamhet : {name: 'Okänd verksamhet'};
        };
        
        $scope.AppModel = AppModel;
        $scope.UserModel = UserModel;

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
        });

        function setupSelectedVardgivare(userAccessInfo) {
            UserModel.setUserAccessInfo(userAccessInfo);

            businessFilterFactory.setup(userAccessInfo.businesses, $location.$$search.filter);

            $scope.verksamhetName = '';
            if (userAccessInfo.businesses && userAccessInfo.businesses.length === 1) {
                $scope.verksamhetName = userAccessInfo.businesses[0].name;
            } else {
                $scope.verksamhetName = userAccessInfo.vgInfo.name;
            }
            $scope.showBusinessesDetails = userAccessInfo.businesses && userAccessInfo.businesses.length > 1;
            $rootScope.landstingAvailable = userAccessInfo.vgInfo.landstingsvardgivareWithUpload;

            if ($rootScope.landstingAvailable) {
                statisticsData.getLandstingFilterInfo(function(landstingFilterInfo) {
                    landstingFilterFactory.setup(landstingFilterInfo.businesses,
                        $location.$$search.landstingfilter);
                    $scope.isLandstingInfoFetched = true;
                });
            }
        }

        $scope.setSelectedVardgivare = function(vgId) {
            statisticsData.setSelectedVg(vgId, function(userAccessInfo) {
                setupSelectedVardgivare(userAccessInfo);
                $location.path('verksamhet');
            }, function() {
                $scope.dataLoadingError = true;
            });
        };

        $scope.$watch('AppModel.get().isLoggedIn', function(value){
            if (value) {
                if (!$scope.isLoginInfoFetched) {
                    statisticsData.getLoginInfo(function (loginInfo) {
                        UserModel.setLoginInfo(loginInfo);

                        $scope.loggedInWithoutStatistikuppdrag = !(loginInfo.businesses && loginInfo.businesses.length >= 1);

                        $scope.isLoginInfoFetched = true;

                        statisticsData.getUserAccessInfo(function(userAccesInfo) {
                            if (!!userAccesInfo.vgInfo) {
                                setupSelectedVardgivare(userAccesInfo);
                            } else {
                                if (loginInfo.vgs.length === 1) {
                                    $scope.setSelectedVardgivare(loginInfo.vgs[0].hsaId);
                                } else {
                                    $location.path('valjVardgivare');
                                }
                            }

                        }, function () {
                            $scope.dataLoadingError = true;
                        });
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

        $scope.loginClicked = function () {
            if ($rootScope.isLoggedIn) {
                $location.path('valjVardgivare');
            } else {
                $location.path(AppModel.get().loginUrl);
            }
        };
    }
);
