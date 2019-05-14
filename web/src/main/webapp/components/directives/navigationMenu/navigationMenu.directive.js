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
    function NavigationMenuCtrl($rootScope, $scope, $location, AppModel, UserModel, StaticData, _, ControllerCommons, navigationViewState) {
        $scope.mobile = $scope.isMobile;
        $scope.menus = [];
        $scope.AppModel = AppModel;
        $scope.UserModel = UserModel;
        $scope.StaticData = StaticData;
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
            }, {
                id: 'navCertificatePerCaseLink',
                link: '#/nationell/certificatePerCaseTvarsnitt',
                name: 'nav.intyg-pers-jukfall',
                ctrl: 'NationalCertificatePerCaseCtrl'
            }]
        };

        var intygNationell = {
            id: 'intyg-statistics-toggle',
            name: 'nav.intyg-header',
            navigationId: 'intyg-statistics-collapse',
            show: true,
            subMenu: [{
                id: 'navNationalIntygPerTypeLink',
                link: '#/nationell/intygPerTyp',
                name: 'nav.intygpertyp',
                ctrl: 'NationalIntygPerTypCtrl'
            }]
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
            }, {
                id: 'navNationalAndelKompletteringarLink',
                link: '#/nationell/andelkompletteringar',
                name: 'nav.andelkompletteringar',
                ctrl: 'NationalAndelKompletteringarCtrl'
            }, {
                id: 'navNationalKompletteringarPerFragaLink',
                link: '#/nationell/kompletteringarperfraga',
                name: 'nav.kompletteringarperfraga',
                ctrl: 'NationalKompletteringarPerFragaCtrl'
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
            }, {
                id: 'navBusinessCertificatePerCaseLink',
                link: '#/verksamhet/certificatePerCaseTvarsnitt',
                name: 'nav.intyg-pers-jukfall',
                ctrl: 'VerksamhetCertificatePerCaseCtrl'
            }
            ]
        };

        var intygVerksamhet = {
            id: 'intyg-statistics-toggle',
            name: 'nav.intyg-header',
            navigationId: 'intyg-statistics-collapse',
            show: true,
            subMenu: [{
                id: 'navBusinessIntygPerTypeLink',
                link: '#/verksamhet/intygPerTyp',
                name: 'nav.intygpertyp',
                ctrl: 'VerksamhetIntygPerTypeCtrl'
            }]
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
                checkVisible: function() {
                    return UserModel.get().isProcessledare || UserModel.get().isDelprocessledare;
                },
                id: 'navBusinessMessagesEnhetLink',
                link: '#/verksamhet/meddelandenPerAmneOchEnhetTvarsnitt',
                name: 'nav.meddelandenperamneochenhet',
                ctrl: 'VerksamhetMeddelandenPerAmneOchEnhetCtrl'
            }, {
                checkVisible: function() {
                    return !UserModel.get().isProcessledare;
                },
                id: 'navBusinessMessagesLakareLink',
                link: '#/verksamhet/meddelandenPerAmneOchLakareTvarsnitt',
                name: 'nav.meddelandenperamneochlakare',
                ctrl: 'VerksamhetMeddelandenPerAmneOchLakareCtrl'
            }, {
                id: 'navBusinessAndelKompletteringarLink',
                link: '#/verksamhet/andelkompletteringar',
                name: 'nav.andelkompletteringar',
                ctrl: 'VerksamhetAndelKompletteringarCtrl'
            }, {
                id: 'navBusinessKompletteringarPerFragaLink',
                link: '#/verksamhet/kompletteringarperfraga',
                name: 'nav.kompletteringarperfraga',
                ctrl: 'VerksamhetKompletteringarPerFragaCtrl'
            }]
        };

        // Region
        var sjukfallRegion = {
            id: 'sjukfall-statistics-toggle',
            name: 'nav.sjukfall-header',
            navigationId: 'sjukfall-statistics-collapse',
            show: true,
            subMenu: [
                {
                    checkEnable: function() {
                        return UserModel.get().regionAvailable;
                    },
                    id: 'navRegionCasesPerMonthLink',
                    link: '#/region/sjukfallPerManad',
                    name: 'nav.sjukfall-totalt',
                    ctrl: 'RegionCasesPerMonthCtrl'
                },{
                    checkEnable: function() {
                        return UserModel.get().regionAvailable;
                    },
                    id: 'navRegionCasesPerEnhetLink',
                    link: '#/region/sjukfallPerEnhet',
                    name: 'nav.vardenhet',
                    ctrl: 'RegionCasesPerBusinessCtrl'
                },{
                    checkEnable: function() {
                        return UserModel.get().regionAvailable;
                    },
                    id: 'navRegionCasesPerPatientsPerEnhetLink',
                    link: '#/region/sjukfallPerListningarPerEnhet',
                    name: 'nav.region.listningsjamforelse',
                    ctrl: 'RegionCasesPerPatientsPerBusinessCtrl'
                },{
                    checkEnable: function() {
                        return UserModel.get().regionAvailable;
                    },
                    id: 'navRegionCertificatePerCaseLink',
                    link: '#/region/certificatePerCaseTvarsnitt',
                    name: 'nav.intyg-pers-jukfall',
                    ctrl: 'RegionCertificatePerCaseCtrl'
                }
            ]
        };

        var intygRegion = {
            id: 'intyg-statistics-toggle',
            name: 'nav.intyg-header',
            navigationId: 'intyg-statistics-collapse',
            show: true,
            subMenu: [{
                checkEnable: function() {
                    return UserModel.get().regionAvailable;
                },
                id: 'navRegionIntygPerTypeLink',
                link: '#/region/intygPerTyp',
                name: 'nav.intygpertyp',
                ctrl: 'RegionIntygPerTypCtrl'
            }]
        };

        var kommunikationRegion = {
            id: 'kommunikation-statistics-toggle',
            name: 'nav.kommunikation-header',
            navigationId: 'kommunikation-statistics-collapse',
            show: true,
            subMenu: [{
                checkEnable: function() {
                    return UserModel.get().regionAvailable;
                },
                id: 'navRegionMessagesLink',
                link: '#/region/meddelandenPerAmne',
                name: 'nav.meddelanden',
                ctrl: 'RegionMeddelandenPerAmneCtrl'
            },{
                checkEnable: function() {
                    return UserModel.get().regionAvailable;
                },
                id: 'navRegionMessagesEnhetLink',
                link: '#/region/meddelandenPerAmneOchEnhet',
                name: 'nav.meddelandenperamneochenhet',
                ctrl: 'RegionMeddelandenPerAmneOchEnhetCtrl'
            }, {
                checkEnable: function() {
                    return UserModel.get().regionAvailable;
                },
                id: 'navRegionAndelKompletteringarLink',
                link: '#/region/andelkompletteringar',
                name: 'nav.andelkompletteringar',
                ctrl: 'RegionAndelKompletteringarCtrl'
            }, {
                checkEnable: function() {
                    return UserModel.get().regionAvailable;
                },
                id: 'navRegionKompletteringarPerFragaLink',
                link: '#/region/kompletteringarperfraga',
                name: 'nav.kompletteringarperfraga',
                ctrl: 'RegionKompletteringarPerFragaCtrl'
            }]
        };

        var miscRegion = {
            id: 'miscregion-statistics-toggle',
            name: 'nav.region-header',
            navigationId: 'miscregion-statistics-collapse',
            show: true,
            subMenu: [{
                checkVisible: function() {
                    return UserModel.get().isRegionAdmin;
                },
                id: 'navRegionUploadLink',
                link: '#/region/filuppladdning',
                name: 'nav.region.filuppladdning',
                ctrl: 'RegionFileUploadCtrl'
            },{
                checkVisible: function() {
                    return UserModel.get().hasRegionAccess;
                },
                id: 'navRegionAboutLink',
                link: '#/region/om',
                name: 'nav.region.om',
                ctrl: 'RegionAboutCtrl'
            }]
        };

        var nationell = [sjukfallNationell, intygNationell, kommunikationNationell];
        var verksamhet = [sjukfallVerksamhet, intygVerksamhet, kommunikationVerksamhet];
        var region = [sjukfallRegion, intygRegion, kommunikationRegion, miscRegion];

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

        $scope.$watch(
            navigationViewState.get,
            initMenu,
            true
        );

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
            var isVerksamhetShowing = navigationViewState.get().active === navigationViewState.ids.verksamhet;
            var isRegionShowing = navigationViewState.get().active === navigationViewState.ids.region;
            var isShowingNationell = navigationViewState.get().active === navigationViewState.ids.nationell;

            if (isVerksamhetShowing) {
                $scope.menus = verksamhet;
            } else if (isRegionShowing) {
                $scope.menus = region;
            } else if (isShowingNationell || $scope.menus.length === 0) {
                $scope.menus = nationell;
            }
        }

        initMenu();
    }
})();
