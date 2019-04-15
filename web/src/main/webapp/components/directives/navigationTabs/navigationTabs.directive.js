/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

(function() {
    'use strict';

    var baseTemplateUrl = '/components/directives/navigationTabs/';

    angular.module('StatisticsApp').directive('navigationTabs',
        [function() {

            return {
                restrict: 'E',
                scope: {
                    isMobile: '=',
                    loginVisible: '=',
                    loginClicked: '&',
                    queryString: '='
                },
                templateUrl: baseTemplateUrl + 'navigationTabs.directive.html',
                controller: Ctrl,
                controllerAs: 'vm',
                bindToController: true
            };
        }]);

    /** @ngInject */
    function Ctrl($scope, $rootScope, $location, UserModel, AppModel, ControllerCommons, navigationViewState) {
        var vm = this;
        vm.AppModel = AppModel;
        vm.UserModel = UserModel;
        vm.activeTab = 0;

        var isLoggedIn = AppModel.get().isLoggedIn;

        var nationellTab = {
            id: navigationViewState.ids.nationell,
            title: 'nav.national-header',
            url: 'nationell/oversikt',
            urlPrefix: 'nationell'
        };

        var verksamhetTab = {
            id: navigationViewState.ids.verksamhet,
            title: 'nav.business-header',
            url: 'verksamhet/oversikt',
            urlPrefix: 'verksamhet',
            content: baseTemplateUrl + 'navigationTabs.verksamhet.html'
        };

        var regionTab = {
            id: navigationViewState.ids.region,
            title: 'nav.region-header',
            url: function() {
                return UserModel.get().regionAvailable ? 'landsting/sjukfallPerManad' : 'landsting/om';
            },
            urlPrefix: 'landsting',
            content: baseTemplateUrl + 'navigationTabs.region.html'
        };

        $scope.$watch('vm.UserModel.get().businesses', function(newValue, oldValue) {
            if (oldValue !== newValue) {
                isLoggedIn = AppModel.get().isLoggedIn;
                initTabs();
            }
        });

        $scope.$watch('vm.UserModel.get().enableVerksamhetMenu', function() {
            initTabs();
        });

        $rootScope.$on('$routeChangeSuccess', function () {
            setActiveTab();
        });

        vm.tabActivated = function(tab) {
            if (!ControllerCommons.isShowing($location, tab.urlPrefix) && !ControllerCommons.isShowing($location, 'om')) {
                if (angular.isFunction(tab.url)) {
                    $location.path(tab.url());
                } else {
                    $location.path(tab.url);
                }
            }

            navigationViewState.set({
                active: tab.id
            });
        };

        vm.tabDeselect = function(tab) {
            if (ControllerCommons.isShowing($location, tab.urlPrefix)) {
                tab.url = $location.path();
            }
        };

        function initTabs() {
            var tabs = [];

            var verksamhet = UserModel.get().enableVerksamhetMenu;
            var region = UserModel.get().hasRegionAccess;

            if (isLoggedIn && (verksamhet || region)) {
                if (verksamhet) {
                    tabs.push(verksamhetTab);
                }

                if (region) {
                    tabs.push(regionTab);
                }

                tabs.push(nationellTab);

                vm.tabClass = tabs.length > 2 ? 'navigation-three-tabs' : 'navigation-two-tabs';
            }

            vm.tabs = tabs;

            setActiveTab();
        }

        function setActiveTab() {
            var isVerksamhetShowing = ControllerCommons.isShowingVerksamhet($location);
            var isRegionShowing = ControllerCommons.isShowingRegion($location);
            var isShowingNationell = ControllerCommons.isShowingNationell($location);

            if (isVerksamhetShowing) {
                vm.activeTab = verksamhetTab.id;
            } else if (isRegionShowing) {
                vm.activeTab = regionTab.id;
            } else if (isShowingNationell) {
                vm.activeTab = nationellTab.id;
            } else if (vm.activeTab === 0) {
                vm.activeTab = vm.tabs.length ? vm.tabs[0].id : 0;
            }
        }

        initTabs();
    }
}());
