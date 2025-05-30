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

angular.module('StatisticsApp').controller('pageCtrl',
    /** @ngInject */
    function($scope, $rootScope, $window, $location, statisticsData, businessFilterFactory, regionFilterFactory,
        _, ControllerCommons, AppModel, UserModel, filterViewState, sessionCheckService) {
      'use strict';

      $scope.AppModel = AppModel;
      $scope.UserModel = UserModel;
      $scope.sessionCheckService = sessionCheckService;

      $rootScope.$on('$routeChangeSuccess', function() {
        filterViewState.setMessages([]);

        $rootScope.hideNavigationTabs = false;

        $scope.isVerksamhetShowing = ControllerCommons.isShowingVerksamhet($location);
        $scope.isRegionShowing = ControllerCommons.isShowingRegion($location);

        if ($scope.isVerksamhetShowing) {
          $scope.viewHeader = 'Verksamhetsstatistik';
        } else if ($scope.isRegionShowing) {
          $scope.viewHeader = 'Regionstatistik';
        } else {
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
          $rootScope.regionAvailable = userAccessInfo.vgInfo.regionsvardgivareWithUpload;
          if ($rootScope.regionAvailable) {
            statisticsData.getRegionFilterInfo(function(regionFilterInfo) {
              regionFilterFactory.setup(regionFilterInfo.businesses,
                  $location.search().regionfilter);
              $scope.isRegionInfoFetched = true;
            });
          }
          AppModel.set({
            loggedIn: true,
            loginVisible: false,
            loginUrl: AppModel.get().loginUrl,
            projectVersion: AppModel.get().projectVersion,
            driftbanners: AppModel.get().driftbanners,
          });
        } else {
          UserModel.resetUserAccessInfo();
          $scope.showBusinessesDetails = false;
          $rootScope.regionAvailable = false;
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
          if ($scope.isRegionInfoFetched) {
            regionFilterFactory.selectPreselectedFilter($location.search().regionfilter);
          }
        }
      });

      $scope.$on('$destroy', function() {
        if (watchQueryString && typeof watchQueryString.destroy === 'function') {
          watchQueryString.destroy();
        }
      });

      $scope.$watch('AppModel.get().driftbanners', function(value) {
        $scope.serviceBannerMessageList = value;
      });

      $scope.$watch('AppModel.get().isLoggedIn', function(value) {
        if (value) {
          // Start session polling
          $scope.sessionCheckService.startPolling();

          if (!$scope.isLoginInfoFetched) {
            statisticsData.getLoginInfo(function(loginInfo) {
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
            }, function() {
              $scope.dataLoadingError = true;
            });
          } else {
            businessFilterFactory.selectPreselectedFilter($location.search().filter);
            if ($scope.isRegionInfoFetched) {
              regionFilterFactory.selectPreselectedFilter($location.search().regionfilter);
            }
          }
        }
      });

      $scope.loginClicked = function() {
        if ($rootScope.isLoggedIn && !$scope.loggedInWithoutStatistikuppdrag) {
          $location.path('valjVardgivare');
        } else {
          $window.location.href = '/saml2/authenticate/siths';
        }
      };
    }
);