/*
 * Copyright (C) 2013 - 2016 Inera AB (http://www.inera.se)
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

(function() {
    'use strict';

    angular.module('StatisticsApp').directive('navigationMenu',
        [function() {

            return {
                restrict: 'E',
                scope: {
                    isMobile: '='
                },
                templateUrl: 'components/directives/navigationMenu/navigationMenu.html',
                controller: NavigationMenuCtrl
            };
        }]);

    /** @ngInject */
    function NavigationMenuCtrl($scope, $rootScope) {
        $scope.mobile = $scope.isMobile;
        $scope.menus = [];
        $scope.isLoggedIn = $rootScope.isLoggedIn;
        $scope.isCollapsed = true;

        var national = {
            id: 'national-statistics-toggle',
            menuId: 'national-statistic-menu-content',
            navigationId: 'national-statistics-collapse',
            name: 'nav.national-header',
            hiddenTitle: 'statistics.hidden-header.nationell-navigering',
            show: true,
            subMenu: [{
                id: 'navOverviewLink',
                link: '#/nationell/oversikt',
                name: 'nav.oversikt',
                ctrl: 'NationalOverviewCtrl'
            },{
                id: 'navCasesPerMonthLink',
                link: '#/nationell/sjukfallPerManad',
                name: 'nav.sjukfall-totalt',
                ctrl: 'NationalCasesPerMonthCtrl'
            },{
                id: 'navDiagnosisGroupsLink',
                link: '#/nationell/diagnosgrupp',
                name: 'nav.diagnosgrupp',
                ctrl: 'NationalDiagnosgruppCtrl',
                subMenuId: 'sub-menu-diagnostics',
                subMenuIdLink: '#sub-menu-diagnostics',
                subMenu: [{
                    id: 'navDiagnosisSubGroupsLink',
                    link: '#/nationell/diagnosavsnitt',
                    name: 'nav.enskilt-diagnoskapitel',
                    ctrl: 'NationalDiagnosavsnittCtrl'
                }]
            },{
                id: 'navAgeGroupsLink',
                link: '#/nationell/aldersgrupper',
                name: 'nav.aldersgrupp',
                ctrl: 'NationalAgeGroupCtrl'
            },{
                id: 'navSickLeaveDegreeLink',
                link: '#/nationell/sjukskrivningsgrad',
                name: 'nav.sjukskrivningsgrad',
                ctrl: 'NationalDegreeOfSickLeaveCtrl'
            },{
                id: 'navSickLeaveLengthLink',
                link: '#/nationell/sjukskrivningslangd',
                name: 'nav.sjukskrivningslangd',
                ctrl: 'NationalSickLeaveLengthCtrl'
            },{
                id: 'navCountyLink',
                link: '#/nationell/lan',
                name: 'nav.lan',
                ctrl: 'NationalCasesPerCountyCtrl',
                subMenuId: 'sub-menu-cases-per-county',
                subMenuIdLink: '#sub-menu-cases-per-county',
                subMenu: [{
                    id: 'navCasesPerSexLink',
                    link: '#/nationell/andelSjukfallPerKon',
                    name: 'nav.lan-andel-sjukfall-per-kon',
                    ctrl: 'NationalCasesPerSexCtrl'
                }]
            }]
        };

        var landsting = {
            checkVisible: function() {
                return $scope.hasLandstingAccess;
            },
            id: 'landsting-statistics-toggle',
            menuId: 'landsting-statistic-menu-content',
            navigationId: 'landsting-statistics-collapse',
            name: 'nav.landsting-header',
            hiddenTitle: 'statistics.hidden-header.landsting-navigering',
            show: false,
            subMenu: [{
                checkEnable: function() {
                    return $scope.landstingAvailable;
                },
                id: 'navLandstingCasesPerMonthLink',
                link: '#/landsting/sjukfallPerManad',
                name: 'nav.sjukfall-totalt',
                ctrl: 'LandstingCasesPerMonthCtrl'
            },{
                checkEnable: function() {
                    return $scope.landstingAvailable;
                },
                id: 'navLandstingCasesPerEnhetLink',
                link: '#/landsting/sjukfallPerEnhet',
                name: 'nav.vardenhet',
                ctrl: 'LandstingCasesPerBusinessCtrl'
            },{
                checkEnable: function() {
                    return $scope.landstingAvailable;
                },
                id: 'navLandstingCasesPerPatientsPerEnhetLink',
                link: '#/landsting/sjukfallPerListningarPerEnhet',
                name: 'nav.landsting.listningsjamforelse',
                ctrl: 'LandstingCasesPerPatientsPerBusinessCtrl'
            },{
                id: 'navLandstingAboutLink',
                link: '#/landsting/om',
                name: 'nav.landsting.om',
                ctrl: 'LandstingAboutCtrl'
            },{
                checkVisible: function() {
                    return $scope.isLandstingAdmin;
                },
                id: 'navLandstingUploadLink',
                link: '#/landsting/filuppladdning',
                name: 'nav.landsting.filuppladdning',
                ctrl: 'LandstingFileUploadCtrl'
            }]
        };

        var operation = {
            checkVisible: function() {
                if ($scope.isMobile) {
                    return $rootScope.isLoggedIn;
                }
                return true;
            },
            id: 'business-statistics-toggle',
            menuId: 'business-statistic-menu-content',
            navigationId: 'business-statistics-collapse',
            name: 'nav.business-header',
            hiddenTitle: 'statistics.hidden-header.business-navigering',
            show: false,
            subMenu: [{
                id: 'navVerksamhetOversiktLink',
                link: '#/verksamhet/oversikt',
                name: 'nav.oversikt',
                ctrl: 'businessOverviewCtrl'
            },{
                id: 'navBusinessCasesPerBusinessLink',
                link: '#/verksamhet/sjukfallperenhet',
                name: 'nav.vardenhet',
                ctrl: 'VerksamhetCasesPerBusinessCtrl'
            },{
                checkVisible: function() {
                    return $scope.isProcessledare || $scope.isDelprocessledare;
                },
                id: 'navBusinessCasesPerMonthLink',
                link: '#/verksamhet/sjukfallPerManad',
                name: 'nav.sjukfall-totalt',
                ctrl: 'VerksamhetCasesPerMonthCtrl'
            },{
                id: 'navBusinessDiagnosisGroupsLink',
                link: '#/verksamhet/diagnosgrupp',
                name: 'nav.diagnosgrupp',
                ctrl: 'VerksamhetDiagnosgruppCtrl',
                subMenuId: 'sub-menu-business-diagnostics',
                subMenuIdLink: '#sub-menu-business-diagnostics',
                subMenu: [{
                    id: 'navBusinessDiagnosisSubGroupsLink',
                    link: '#/verksamhet/diagnosavsnitt',
                    name: 'nav.enskilt-diagnoskapitel',
                    ctrl: 'VerksamhetDiagnosavsnittCtrl'
                },{
                    id: 'navBusinessCompareDiagnosisLink',
                    link: '#/verksamhet/jamforDiagnoser',
                    name: 'nav.jamfor-diagnoser',
                    ctrl: 'VerksamhetCompareDiagnosisCtrl'
                }]
            },{
                id: 'navBusinessAgeGroupsLink',
                link: '#/verksamhet/aldersgrupper',
                name: 'nav.aldersgrupp',
                ctrl: 'VerksamhetAgeGroupCtrl'
            },{
                id: 'navBusinessSickLeaveDegreeLink',
                link: '#/verksamhet/sjukskrivningsgrad',
                name: 'nav.sjukskrivningsgrad',
                ctrl: 'VerksamhetDegreeOfSickLeaveCtrl'
            },{
                id: 'navBusinessSickLeaveLengthLink',
                link: '#/verksamhet/sjukskrivningslangd',
                name: 'nav.sjukskrivningslangd',
                ctrl: 'VerksamhetSickLeaveLengthCtrl',
                subMenuId: 'sub-menu-business-sick-leave-length',
                subMenuIdLink: '#sub-menu-business-sick-leave-length',
                subMenu: [{
                    id: 'navBusinessMoreNinetyDaysSickLeaveLink',
                    link: '#/verksamhet/langasjukskrivningar',
                    name: 'nav.sjukskrivningslangd-mer-an-90-dagar',
                    ctrl: 'VerksamhetLongSickLeavesCtrl'
                }]
            },{
                checkVisible: function() {
                    return !$scope.isProcessledare;
                },
                id: 'navBusinessCasesPerLakareLink',
                link: '#/verksamhet/sjukfallperlakare',
                name: 'nav.lakare',
                ctrl: 'VerksamhetCasesPerLakareCtrl'
            },{
                id: 'navBusinessCasesPerLakaresAlderOchKonLink',
                link: '#/verksamhet/sjukfallperlakaresalderochkon',
                name: 'nav.lakaralder-kon',
                ctrl: 'VerksamhetLakaresAlderOchKonCtrl'
            },{
                id: 'navBusinessCasesPerLakarbefattningLink',
                link: '#/verksamhet/sjukfallperlakarbefattning',
                name: 'nav.lakarbefattning',
                ctrl: 'VerksamhetLakarbefattningCtrl'
            },{
                id: 'navBusinessDifferentieratIntygandeLink',
                link: '#/verksamhet/differentieratintygande',
                name: 'nav.differentieratintygande',
                ctrl: 'VerksamhetDifferentieratIntygandeCtrl'
            }]
        };

        var about = {
            id: 'about-statistics-toggle',
            menuId: 'about-statistic-menu-content',
            navigationId: 'about-statistics-collapse',
            name: 'nav.about-header',
            hiddenTitle: 'statistics.hidden-header.about-navigering',
            show: false,
            subMenu: [{
                id: 'navAboutTjanstLink',
                link: '#/om/tjansten',
                name: 'nav.allmant-om-tjansten',
                ctrl: 'AboutServiceCtrl'
            },{
                id: 'navAboutInloggningLink',
                link: '#/om/inloggning',
                name: 'nav.inloggning-behorighet',
                ctrl: 'AboutLoginCtrl'
            },{
                id: 'navAboutFaqLink',
                link: '#/om/vanligafragor',
                name: 'nav.faq',
                ctrl: 'AboutFaqCtrl'
            },{
                id: 'navAboutContactLink',
                link: '#/om/kontakt',
                name: 'nav.kontakt-support',
                ctrl: 'AboutContactCtrl'
            }]
        };

        $scope.menus.push(national);
        $scope.menus.push(landsting);

        if ($rootScope.isLoggedIn) {
            $scope.menus.push(operation);
        }
        else {
            $scope.menus.push({
                id: 'business-statistics-toggle',
                name: 'login.header',
                show: false,
                checkVisible: function() {
                    if ($scope.isMobile) {
                        return $scope.isLoggedIn;
                    }
                    return true;
                }
            });
        }

        $scope.menus.push(about);

        if ($scope.isMobile) {
            angular.forEach($scope.menus, function(m) {
                m.show = false;
            });
        }

        $scope.toggleMobileMenu = function() {
            $scope.isCollapsed = !$scope.isCollapsed;
        };

        $scope.toggleMenu = function(menu) {
            var temp = menu.show;
            angular.forEach($scope.menus, function(m) {
                m.show = false;
            });

            menu.show = !temp;
        };

        $scope.showMenu = function(menu) {
            if (angular.isFunction(menu.checkVisible)) {
                return menu.checkVisible();
            } else {
                return true;
            }
        };

        $scope.menuEnabled = function(menu) {
            if (angular.isFunction(menu.checkEnable)) {
                return menu.checkEnable();
            }
            else {
                return true;
            }
        };

        $scope.$on('navigationUpdate', function (event, navigationGroupId) {
            angular.forEach($scope.menus, function(m) {
                m.show = false;
            });

            switch(navigationGroupId) {
            case 'about-statistics-collapse':
                about.show = true;
                break;
            case 'landsting-statistics-collapse':
                landsting.show = true;
                break;
            case 'business-statistics-collapse':
                operation.show = true;
                break;
                //case 'national-statistics-collapse':
            default:
                national.show = true;
            }
        });

    }
})();