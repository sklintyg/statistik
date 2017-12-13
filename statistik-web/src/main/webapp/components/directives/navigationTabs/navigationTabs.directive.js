/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
    function Ctrl($scope, UserModel, AppModel) {
        var vm = this;
        vm.AppModel = AppModel;
        vm.UserModel = UserModel;

        var isLoggedIn = AppModel.get().isLoggedIn;

        var nationellTab = {
            id: 'nationell',
            title: 'nav.national-header'
        };

        var verksamhetTab = {
            id: 'verksamhet',
            title: 'nav.business-header',
            content: baseTemplateUrl + 'navigationTabs.verksamhet.html'
        };

        var landstingTab = {
            id: 'landsting',
            title: 'nav.landsting-header',
            content: baseTemplateUrl + 'navigationTabs.landsting.html'
        };

        $scope.$watch('vm.AppModel.get().isLoggedIn', function(newValue, oldValue) {
            if (oldValue !== newValue) {
                isLoggedIn = AppModel.get().isLoggedIn;
                initTabs();
            }
        });

        $scope.$watch('vm.UserModel.get().enableVerksamhetMenu', function() {
            initTabs();
        });

        function initTabs() {
            var tabs = [];

            var verksamhet = UserModel.get().enableVerksamhetMenu;
            var landsting = UserModel.get().hasLandstingAccess;

            if (isLoggedIn && (verksamhet || landsting)) {
                if (verksamhet) {
                    tabs.push(verksamhetTab);
                }

                if (landsting) {
                    tabs.push(landstingTab);
                }

                tabs.push(nationellTab);
            }

            vm.tabs = tabs;
        }

        initTabs();
    }
}());
