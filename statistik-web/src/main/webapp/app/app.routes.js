/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

var app = angular.module('StatisticsApp')
    .config(
        [ '$routeProvider', function ($routeProvider) {
            'use strict';

            $routeProvider.when('/login', {
                templateUrl: '/app/views/login.html',
                controller: 'loginCtrl',
                title: 'Inloggning'
            }).when('/fakelogin', {
                templateUrl: '/app/views/fakelogin.html',
                title: 'Fake Login Page'
            }).when('/serverbusy', {
                templateUrl: '/app/views/error/serverBusy.html',
                title: 'Tjänsten överbelastad'
            }).when('/nationell/oversikt', {
                templateUrl: '/app/views/overview.html',
                controller: 'overviewCtrl',
                controllerAs: 'NationalOverviewCtrl',
                title: 'Översikt'
            }).when('/nationell/sjukfallPerManad', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'singleLineChartCtrl',
                controllerAs: 'NationalCasesPerMonthCtrl',
                resolve: { config: app.casesPerMonthConfig },
                title: 'Sjukfall per månad'
            }).when('/nationell/aldersgrupper', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'NationalAgeGroupCtrl',
                resolve: { config: app.nationalAgeGroupConfig },
                title: 'Åldersgrupper'
            }).when('/nationell/sjukskrivningslangd', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'NationalSickLeaveLengthCtrl',
                resolve: { config: app.nationalSickLeaveLengthConfig },
                title: 'Sjukskrivningslängd'
            }).when('/nationell/lan', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'NationalCasesPerCountyCtrl',
                resolve: { config: app.casesPerCountyConfig },
                title: 'Län'
            }).when('/nationell/diagnosgrupp', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'NationalDiagnosgruppCtrl',
                resolve: { config: app.diagnosisGroupConfig },
                title: 'Diagnosgrupper'
            }).when('/nationell/diagnosavsnitt/kapitel/:kapitelId', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'NationalDiagnosavsnittCtrl',
                resolve: { config: app.diagnosisSubGroupConfig },
                title: 'Enskilt diagnoskapitel'
            }).when('/nationell/diagnosavsnitt', {
                redirectTo: '/nationell/diagnosavsnitt/kapitel/A00-B99'
            }).when('/nationell/sjukskrivningsgrad', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'NationalDegreeOfSickLeaveCtrl',
                resolve: { config: app.degreeOfSickLeaveConfig },
                title: 'Sjukskrivningsgrad'
            }).when('/nationell/andelSjukfallPerKon', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'NationalCasesPerSexCtrl',
                resolve: { config: app.casesPerSexConfig },
                title: 'Andel sjukfall per kön'
            }).when('/nationell/meddelanden', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'singleLineChartCtrl',
                controllerAs: 'NationalMeddelandenPerMonthCtrl',
                resolve: { config: app.meddelandenPerMonthConfig },
                title: 'Andel inkomna meddelanden'
            }).when('/verksamhet', {
                redirectTo: '/verksamhet/oversikt'
            }).when('/valjVardgivare', {
                templateUrl: '/app/views/business/selectVg.html',
                controller: 'selectVgCtrl',
                title: 'Välj vårdgivare'
            }).when('/verksamhet/oversikt', {
                templateUrl: '/app/views/business/businessOverview.html',
                controller: 'businessOverviewCtrl',
                title: 'Verksamhetsöversikt'
            }).when('/verksamhet/nodata', {
                templateUrl: '/app/views/business/noDataAvailable.html',
                title: 'Data saknas'
            }).when('/verksamhet/sjukfallPerManad', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'singleLineChartCtrl',
                controllerAs: 'VerksamhetCasesPerMonthCtrl',
                resolve: { config: app.casesPerMonthConfig },
                title: 'Sjukfall per månad'
            }).when('/verksamhet/sjukfallPerManadTvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetCasesPerMonthCtrl',
                resolve: { config: app.casesPerMonthTvarsnittConfig },
                title: 'Sjukfall per månad'
            }).when('/verksamhet/diagnosgrupp', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetDiagnosgruppCtrl',
                resolve: { config: app.diagnosisGroupConfig },
                title: 'Diagnosgrupper'
            }).when('/verksamhet/diagnosgrupptvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetDiagnosgruppCtrl',
                resolve: { config: app.diagnosisGroupTvarsnittConfig },
                title: 'Diagnosgrupper'
            }).when('/verksamhet/diagnosavsnitt/kapitel/:kapitelId/avsnitt/:avsnittId/kategori/:kategoriId', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetDiagnosavsnittCtrl',
                resolve: { config: app.diagnosisSubGroupConfig },
                title: 'Enskilt diagnoskapitel'
            }).when('/verksamhet/diagnosavsnitt/kapitel/:kapitelId/avsnitt/:avsnittId', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetDiagnosavsnittCtrl',
                resolve: { config: app.diagnosisSubGroupConfig },
                title: 'Enskilt diagnoskapitel'
            }).when('/verksamhet/diagnosavsnitt/kapitel/:kapitelId', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetDiagnosavsnittCtrl',
                resolve: { config: app.diagnosisSubGroupConfig },
                title: 'Enskilt diagnoskapitel'
            }).when('/verksamhet/diagnosavsnitt', {
                redirectTo: '/verksamhet/diagnosavsnitt/kapitel/A00-B99'
            }).when('/verksamhet/diagnosavsnitttvarsnitt/kapitel/:kapitelId/avsnitt/:avsnittId/kategori/:kategoriId', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetDiagnosavsnittCtrl',
                resolve: { config: app.diagnosisSubGroupTvarsnittConfig },
                title: 'Enskilt diagnoskapitel'
            }).when('/verksamhet/diagnosavsnitttvarsnitt/kapitel/:kapitelId/avsnitt/:avsnittId', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetDiagnosavsnittCtrl',
                resolve: { config: app.diagnosisSubGroupTvarsnittConfig },
                title: 'Enskilt diagnoskapitel'
            }).when('/verksamhet/diagnosavsnitttvarsnitt/kapitel/:kapitelId', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetDiagnosavsnittCtrl',
                resolve: { config: app.diagnosisSubGroupTvarsnittConfig },
                title: 'Enskilt diagnoskapitel'
            }).when('/verksamhet/diagnosavsnitttvarsnitt', {
                redirectTo: '/verksamhet/diagnosavsnitttvarsnitt/kapitel/A00-B99'
            }).when('/verksamhet/jamforDiagnoser/:diagnosHash', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetCompareDiagnosisCtrl',
                resolve: { config: app.compareDiagnosis },
                title: 'Jämför valfria diagnoser'
            }).when('/verksamhet/jamforDiagnoser', {
                redirectTo: '/verksamhet/jamforDiagnoser/-'
            }).when('/verksamhet/jamforDiagnoserTidsserie/:diagnosHash', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetCompareDiagnosisCtrl',
                resolve: { config: app.compareDiagnosisTimeSeriesConfig },
                title: 'Jämför valfria diagnoser'
            }).when('/verksamhet/jamforDiagnoserTidsserie', {
                redirectTo: '/verksamhet/jamforDiagnoserTidsserie/-'
            }).when('/verksamhet/aldersgrupper', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetAgeGroupCtrl',
                resolve: { config: app.nationalAgeGroupConfig },
                title: 'Åldersgrupper'
            }).when('/verksamhet/aldersgrupperTidsserie', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetAgeGroupCtrl',
                resolve: { config: app.nationalAgeGroupTimeSeriesConfig },
                title: 'Åldersgrupper'
            }).when('/verksamhet/sjukskrivningsgrad', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetDegreeOfSickLeaveCtrl',
                resolve: { config: app.degreeOfSickLeaveConfig },
                title: 'Sjukskrivningsgrad'
            }).when('/verksamhet/sjukskrivningsgradtvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetDegreeOfSickLeaveCtrl',
                resolve: { config: app.degreeOfSickLeaveTvarsnittConfig },
                title: 'Sjukskrivningsgrad'
            }).when('/verksamhet/differentieratintygande', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetDifferentieratIntygandeCtrl',
                resolve: { config: app.differentieratIntygandeConfig },
                title: 'Differentierat intygande'
            }).when('/verksamhet/differentieratintygandetvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetDifferentieratIntygandeCtrl',
                resolve: { config: app.differentieratIntygandeTvarsnittConfig },
                title: 'Differentierat intygande'
            }).when('/verksamhet/sjukskrivningslangd', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetSickLeaveLengthCtrl',
                resolve: { config: app.nationalSickLeaveLengthConfig },
                title: 'Sjukskrivningslängd'
            }).when('/verksamhet/sjukskrivningslangdTidsserie', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetSickLeaveLengthCtrl',
                resolve: { config: app.sickLeaveLengthTimeSeriesConfig },
                title: 'Sjukskrivningslängd'
            }).when('/verksamhet/langasjukskrivningar', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'singleLineChartCtrl',
                controllerAs: 'VerksamhetLongSickLeavesCtrl',
                resolve: { config: app.longSickLeavesConfig },
                title: 'Sjukskrivningslängd mer än 90 dagar'
            }).when('/verksamhet/langasjukskrivningartvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetLongSickLeavesCtrl',
                resolve: { config: app.longSickLeavesTvarsnittConfig },
                title: 'Sjukskrivningslängd mer än 90 dagar'
            }).when('/verksamhet/sjukfallperenhet', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetCasesPerBusinessCtrl',
                resolve: { config: app.casesPerBusinessConfig },
                title: 'Antal sjukfall per vårdenhet'
            }).when('/verksamhet/sjukfallperenhettidsserie', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetCasesPerBusinessCtrl',
                resolve: { config: app.casesPerBusinessTimeSeriesConfig },
                title: 'Antal sjukfall per vårdenhet'
            }).when('/verksamhet/sjukfallperlakare', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetCasesPerLakareCtrl',
                resolve: { config: app.casesPerLakareConfig },
                title: 'Antal sjukfall per läkare'
            }).when('/verksamhet/sjukfallperlakaretidsserie', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetCasesPerLakareCtrl',
                resolve: { config: app.casesPerLakareTimeSeriesConfig },
                title: 'Antal sjukfall per läkare'
            }).when('/verksamhet/sjukfallperlakaresalderochkon', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetLakaresAlderOchKonCtrl',
                resolve: { config: app.casesPerLakaresAlderOchKonConfig },
                title: 'Sjukfall per läkarens ålder och kön'
            }).when('/verksamhet/sjukfallperlakaresalderochkontidsserie', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetLakaresAlderOchKonCtrl',
                resolve: { config: app.casesPerLakaresAlderOchKonTidsserieConfig },
                title: 'Sjukfall per läkarens ålder och kön'
            }).when('/verksamhet/sjukfallperlakarbefattning', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetLakarbefattningCtrl',
                resolve: { config: app.casesPerLakarbefattningConfig },
                title: 'Sjukfall per läkarbefattning'
            }).when('/verksamhet/sjukfallperlakarbefattningtidsserie', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetLakarbefattningCtrl',
                resolve: { config: app.casesPerLakarbefattningTidsserieConfig },
                title: 'Sjukfall per läkarbefattning'
            }).when('/verksamhet/meddelanden', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'singleLineChartCtrl',
                controllerAs: 'VerksamhetMeddelandenPerMonthCtrl',
                resolve: { config: app.meddelandenPerMonthConfig },
                title: 'Andel inkomna meddelanden'
            }).when('/verksamhet/meddelandenTvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetMeddelandenPerMonthCtrl',
                resolve: { config: app.meddelandenPerMonthTvarsnittConfig },
                title: 'Andel inkomna meddelanden'
            }).when('/verksamhet/intyg', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'singleLineChartCtrl',
                controllerAs: 'VerksamhetIntygPerMonthCtrl',
                resolve: { config: app.intygPerMonthConfig },
                title: 'Andel inkomna intyg'
            }).when('/verksamhet/intygTvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetIntygPerMonthCtrl',
                resolve: { config: app.intygPerMonthTvarsnittConfig },
                title: 'Andel inkomna intyg'
            }).when('/landsting/filuppladdning', {
                templateUrl: '/app/views/landsting/fileupload.html',
                controller: 'landstingFileUploadCtrl',
                controllerAs: 'LandstingFileUploadCtrl',
                title: 'Filuppladdning'
            }).when('/landsting/sjukfallPerManad', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'singleLineChartCtrl',
                controllerAs: 'LandstingCasesPerMonthCtrl',
                resolve: { config: app.casesPerMonthConfig },
                title: 'Sjukfall per månad'
            }).when('/landsting/sjukfallPerEnhet', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'LandstingCasesPerBusinessCtrl',
                resolve: { config: app.casesPerBusinessConfig },
                title: 'Antal sjukfall per vårdenhet'
            }).when('/landsting/sjukfallPerListningarPerEnhet', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'LandstingCasesPerPatientsPerBusinessCtrl',
                resolve: { config: app.casesPerPatientsPerBusinessConfig },
                title: 'Antal sjukfall per 1000 listningar'
            }).when('/landsting/om', {
                templateUrl: '/app/views/landsting/aboutlandsting.html',
                controllerAs: 'LandstingAboutCtrl',
                title: 'Om Landstingsstatistik'
            }).when('/om/tjansten', {
                templateUrl: '/app/views/about/about.html',
                controllerAs: 'AboutServiceCtrl',
                title: 'Om tjänsten'
            }).when('/om/kontakt', {
                templateUrl: '/app/views/about/contact.html',
                controllerAs: 'AboutContactCtrl',
                title: 'Kontakt till support'
            }).when('/om/vanligafragor', {
                templateUrl: '/app/views/about/faq.html',
                controller: 'aboutFaqCtrl',
                controllerAs: 'AboutFaqCtrl',
                title: 'Vanliga frågor och svar'
            }).when('/om/inloggning', {
                templateUrl: '/app/views/about/login.html',
                controllerAs: 'AboutLoginCtrl',
                title: 'Inloggning och behörighet'
            }).when('/', {
                redirectTo: '/nationell/oversikt'
            }).when('/nationell', {
                redirectTo: '/nationell/oversikt'
            }).otherwise({
                templateUrl: '/app/views/error/pageNotFound.html',
                title: 'Fel'
            });

        } ]);
