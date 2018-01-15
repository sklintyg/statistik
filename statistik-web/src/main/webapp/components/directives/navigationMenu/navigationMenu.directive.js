/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
(function() {
    'use strict';

    angular.module('StatisticsApp').directive('navigationMenu',
        [function() {

            return {
                restrict: 'E',
                scope: {
                    isMobile: '=',
                    loginVisible: '=',
                    loginClicked: '&',
                    queryString: '='
                },
                templateUrl: '/components/directives/navigationMenu/navigationMenu.html',
                controller: NavigationMenuCtrl
            };
        }]);

    /** @ngInject */
    function NavigationMenuCtrl($rootScope, $scope, $location, AppModel, UserModel, StaticFilterData, _, ControllerCommons) {
        $scope.mobile = $scope.isMobile;
        $scope.menus = [];
        $scope.AppModel = AppModel;
        $scope.UserModel = UserModel;
        $scope.StaticFilterData = StaticFilterData;
        $scope.isLoggedIn = AppModel.get().isLoggedIn;
        $scope.isCollapsed = true;
        $scope.isMenuOpen = false;
        var oldValue = $scope.isLoggedIn;

        // Nationell
        var sjukfallNationell = {
            id: 'sjukfall-statistics-toggle',
            name: 'nav.sjukfall-header',
            navigationId: 'sjukfall-statistics-collapse',
            show: true,
            subMenu: [{
                id: 'navOverviewLink',
                link: '#/nationell/oversikt',
                name: 'nav.oversikt',
                ctrl: 'NationalOverviewCtrl'
            }, {
                id: 'navCasesPerMonthLink',
                link: '#/nationell/sjukfallPerManad',
                name: 'nav.sjukfall-totalt',
                ctrl: 'NationalCasesPerMonthCtrl'
            }, {
                id: 'navDiagnosisGroupsLink',
                link: '#/nationell/diagnosgrupp',
                name: 'nav.diagnosgrupp',
                ctrl: 'NationalDiagnosgruppCtrl',
                subMenuId: 'sub-menu-diagnostics',
                subMenuIdLink: '#sub-menu-diagnostics',
                show: true,
                subMenu: [{
                    id: 'navDiagnosisSubGroupsLink',
                    link: '#/nationell/diagnosavsnitt',
                    name: 'nav.enskilt-diagnoskapitel',
                    ctrl: 'NationalDiagnosavsnittCtrl'
                }]
            }, {
                id: 'navAgeGroupsLink',
                link: '#/nationell/aldersgrupper',
                name: 'nav.aldersgrupp',
                ctrl: 'NationalAgeGroupCtrl'
            }, {
                id: 'navSickLeaveDegreeLink',
                link: '#/nationell/sjukskrivningsgrad',
                name: 'nav.sjukskrivningsgrad',
                ctrl: 'NationalDegreeOfSickLeaveCtrl'
            }, {
                id: 'navSickLeaveLengthLink',
                link: '#/nationell/sjukskrivningslangd',
                name: 'nav.sjukskrivningslangd',
                ctrl: 'NationalSickLeaveLengthCtrl'
            }, {
                id: 'navCountyLink',
                link: '#/nationell/lan',
                name: 'nav.lan',
                ctrl: 'NationalCasesPerCountyCtrl',
                subMenuId: 'sub-menu-cases-per-county',
                subMenuIdLink: '#sub-menu-cases-per-county',
                show: true,
                subMenu: [{
                    id: 'navCasesPerSexLink',
                    link: '#/nationell/andelSjukfallPerKon',
                    name: 'nav.lan-andel-sjukfall-per-kon',
                    ctrl: 'NationalCasesPerSexCtrl'
                }]
            }]
        };

        var intygNationell = {
            id: 'intyg-statistics-toggle',
            name: 'nav.intyg-header',
            navigationId: 'intyg-statistics-collapse',
            show: true,
            subMenu: [
            //Not yet available on nationel level
            //     {
            //     id: 'navIntygLink',
            //     link: '#/nationell/intyg',
            //     name: 'title.intygstyp',
            //     ctrl: 'NationalIntygCtrl'
            // }
            ]
        };

        var kommunikationNationell = {
            id: 'kommunikation-statistics-toggle',
            name: 'nav.kommunikation-header',
            navigationId: 'kommunikation-statistics-collapse',
            show: true,
            subMenu: [{
                id: 'navMessagesLink',
                link: '#/nationell/meddelandenPerAmne',
                name: 'nav.meddelanden',
                ctrl: 'NationalMeddelandenPerAmneCtrl'
            }]
        };

        // Verksamhet
        var sjukfallVerksamhet = {
            id: 'sjukfall-statistics-toggle',
            name: 'nav.sjukfall-header',
            navigationId: 'sjukfall-statistics-collapse',
            show: true,
            subMenu: [{
                id: 'navVerksamhetOversiktLink',
                link: '#/verksamhet/oversikt',
                name: 'nav.oversikt',
                ctrl: 'businessOverviewCtrl'
            },{
                checkVisible: function() {
                    return UserModel.get().isProcessledare || UserModel.get().isDelprocessledare;
                },
                id: 'navBusinessCasesPerBusinessLink',
                link: '#/verksamhet/sjukfallperenhet',
                name: 'nav.vardenhet',
                ctrl: 'VerksamhetCasesPerBusinessCtrl'
            },{
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
                show: true,
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
                show: true,
                subMenu: [{
                    id: 'navBusinessMoreNinetyDaysSickLeaveLink',
                    link: '#/verksamhet/langasjukskrivningar',
                    name: 'nav.sjukskrivningslangd-mer-an-90-dagar',
                    ctrl: 'VerksamhetLongSickLeavesCtrl'
                }]
            },{
                checkVisible: function() {
                    return !UserModel.get().isProcessledare;
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
            }, {
                id: 'navBusinessCasesPerLakarbefattningLink',
                link: '#/verksamhet/sjukfallperlakarbefattning',
                name: 'nav.lakarbefattning',
                ctrl: 'VerksamhetLakarbefattningCtrl'
            }
            ]
        };

        var intygVerksamhet = {
            id: 'intyg-statistics-toggle',
            name: 'nav.intyg-header',
            navigationId: 'intyg-statistics-collapse',
            show: true,
            subMenu: []
        };

        var kommunikationVerksamhet = {
            id: 'kommunikation-statistics-toggle',
            name: 'nav.kommunikation-header',
            navigationId: 'kommunikation-statistics-collapse',
            show: true,
            subMenu: [{
                id: 'navBusinessMessagesLink',
                link: '#/verksamhet/meddelandenPerAmne',
                name: 'nav.meddelandenperamne',
                ctrl: 'VerksamhetMeddelandenPerAmneCtrl'
            }, {
                id: 'navBusinessMessagesEnhetLink',
                link: '#/verksamhet/meddelandenPerAmneOchEnhetTvarsnitt',
                name: 'nav.meddelandenperamneochenhet',
                ctrl: 'VerksamhetMeddelandenPerAmneOchEnhetCtrl'
            }]
        };

        // Landsting
        var sjukfallLandsting = {
            id: 'sjukfall-statistics-toggle',
            name: 'nav.sjukfall-header',
            navigationId: 'sjukfall-statistics-collapse',
            show: true,
            subMenu: [
                {
                    checkEnable: function() {
                        return UserModel.get().landstingAvailable;
                    },
                    id: 'navLandstingCasesPerMonthLink',
                    link: '#/landsting/sjukfallPerManad',
                    name: 'nav.sjukfall-totalt',
                    ctrl: 'LandstingCasesPerMonthCtrl'
                },{
                    checkEnable: function() {
                        return UserModel.get().landstingAvailable;
                    },
                    id: 'navLandstingCasesPerEnhetLink',
                    link: '#/landsting/sjukfallPerEnhet',
                    name: 'nav.vardenhet',
                    ctrl: 'LandstingCasesPerBusinessCtrl'
                },{
                    checkEnable: function() {
                        return UserModel.get().landstingAvailable;
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
                        return UserModel.get().isLandstingAdmin;
                    },
                    id: 'navLandstingUploadLink',
                    link: '#/landsting/filuppladdning',
                    name: 'nav.landsting.filuppladdning',
                    ctrl: 'LandstingFileUploadCtrl'
                }
            ]
        };

        var intygLandsting = {
            id: 'intyg-statistics-toggle',
            name: 'nav.intyg-header',
            navigationId: 'intyg-statistics-collapse',
            show: true,
            subMenu: []
        };

        var kommunikationLandsting = {
            id: 'kommunikation-statistics-toggle',
            name: 'nav.kommunikation-header',
            navigationId: 'kommunikation-statistics-collapse',
            show: true,
            subMenu: [{
                        id: 'navLandstingMessagesLink',
                        link: '#/landsting/meddelandenPerAmne',
                        name: 'nav.meddelanden',
                        ctrl: 'LandstingMeddelandenPerAmneCtrl'
                    },{
                        id: 'navLandstingMessagesEnhetLink',
                        link: '#/landsting/meddelandenPerAmneOchEnhet',
                        name: 'nav.meddelandenperamneochenhet',
                        ctrl: 'LandstingMeddelandenPerAmneOchEnhetCtrl'
                    }]
        };

        var about = {
            id: 'about-statistics-toggle',
            menuId: 'about-statistic-menu-content',
            navigationId: 'about-statistics-collapse',
            name: 'nav.about-header',
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

        var nationell = [sjukfallNationell, intygNationell, kommunikationNationell, about];
        var verksamhet = [sjukfallVerksamhet, intygVerksamhet, kommunikationVerksamhet, about];
        var landsting = [sjukfallLandsting, intygLandsting, kommunikationLandsting, about];

        $scope.$on('navigationUpdate', function (event, navigationGroupId) {

            $scope.hideDropDown();

            _.each($scope.menus, function(item) {
                if (navigationGroupId === item.navigationId) {
                    item.show = true;
                }
            });
        });

        $scope.$watch('AppModel.get()', function() {
            $scope.isLoggedIn = AppModel.get().isLoggedIn;

            if (oldValue !== $scope.isLoggedIn) {
                oldValue = $scope.isLoggedIn;
                initMenu();
            }
        }, true);

        $scope.$watch('UserModel.get().enableVerksamhetMenu', function() {
            initMenu();
        });

        $rootScope.$on('$routeChangeSuccess', function () {
            initMenu();
        });

        $scope.toggleMenu = function(menu, $event) {
            $event.preventDefault();
            if (!menu.disabled) {
                menu.show = !menu.show;
            }
        };

        $scope.showMenu = function(menu) {
            if (angular.isFunction(menu.checkVisible)) {
                return menu.checkVisible();
            } else {
                return true;
            }
        };

        $scope.hideDropDown = function() {
            $scope.isMenuOpen = false;
        };

        $scope.menuEnabled = function(menu) {
            if (angular.isFunction(menu.checkEnable)) {
                return menu.checkEnable();
            } else {
                return true;
            }
        };

        function initMenu() {
            var isVerksamhetShowing = ControllerCommons.isShowingVerksamhet($location);
            var isLandstingShowing = ControllerCommons.isShowingLandsting($location);
            var isShowingNationell = ControllerCommons.isShowingNationell($location);

            if (isVerksamhetShowing) {
                $scope.menus = verksamhet;
            } else if (isLandstingShowing) {
                $scope.menus = landsting;
            } else if (isShowingNationell || $scope.menus.length === 0) {
                $scope.menus = nationell;
            }
        }

        initMenu();
    }
})();
