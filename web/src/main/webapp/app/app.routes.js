/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
        /** @ngInject */
        function ($routeProvider) {
            'use strict';

            $routeProvider.when('/login', {
                templateUrl: '/app/views/login.html',
                controller: 'loginCtrl',
                title: 'Inloggning'
            }).when('/fakelogin', {
                templateUrl: '/app/views/fakelogin.html',
                controller: 'fakeLoginCtrl',
                title: 'Fake Login Page'
            }).when('/serverbusy', {
                templateUrl: '/app/views/error/serverBusy.html',
                title: 'Tjänsten överbelastad'
            }).when('/fetchdxsfailed', {
                templateUrl: '/app/views/error/fetchDxsFailed.html',
                title: 'Kunde inte ladda data'
            }).when('/fetchfilterhashfailed', {
                templateUrl: '/app/views/error/fetchFilterHashFailed.html',
                title: 'Kunde inte ladda data'
            }).when('/nationell/oversikt', {
                templateUrl: '/app/views/overview.html',
                controller: 'overviewCtrl',
                controllerAs: 'NationalOverviewCtrl',
                title: 'nav.oversikt'
            }).when('/nationell/sjukfallPerManad', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'singleLineChartCtrl',
                controllerAs: 'NationalCasesPerMonthCtrl',
                resolve: { config: app.casesPerMonthConfig },
                title: 'nav.sjukfall-totalt'
            }).when('/nationell/aldersgrupper', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'NationalAgeGroupCtrl',
                resolve: { config: app.nationalAgeGroupConfig },
                title: 'nav.aldersgrupp'
            }).when('/nationell/sjukskrivningslangd', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'NationalSickLeaveLengthCtrl',
                resolve: { config: app.nationalSickLeaveLengthConfig },
                title: 'nav.sjukskrivningslangd'
            }).when('/nationell/kompletteringarperfraga', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'NationalKompletteringarPerFragaCtrl',
                resolve: { config: app.kompletteringarPerFragaTvarsnittConfig },
                title: 'nav.kompletteringarperfraga'
            }).when('/nationell/lan', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'NationalCasesPerCountyCtrl',
                resolve: { config: app.casesPerCountyConfig },
                title: 'nav.lan'
            }).when('/nationell/diagnosgrupp', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'NationalDiagnosgruppCtrl',
                resolve: { config: app.diagnosisGroupConfig },
                title: 'nav.diagnosgrupp'
            }).when('/nationell/diagnosavsnitt/kapitel/:kapitelId', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'NationalDiagnosavsnittCtrl',
                resolve: { config: app.diagnosisSubGroupConfig },
                title: 'nav.enskilt-diagnoskapitel'
            }).when('/nationell/diagnosavsnitt', {
                redirectTo: '/nationell/diagnosavsnitt/kapitel/A00-B99'
            }).when('/nationell/sjukskrivningsgrad', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'NationalDegreeOfSickLeaveCtrl',
                resolve: { config: app.degreeOfSickLeaveConfig },
                title: 'nav.sjukskrivningsgrad'
            }).when('/nationell/andelSjukfallPerKon', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'NationalCasesPerSexCtrl',
                resolve: { config: app.casesPerSexConfig },
                title: 'nav.lan-andel-sjukfall-per-kon'
            }).when('/nationell/meddelanden', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'singleLineChartCtrl',
                controllerAs: 'NationalMeddelandenPerMonthCtrl',
                resolve: { config: app.meddelandenPerMonthConfig },
                title: 'nav.meddelanden'
            }).when('/nationell/meddelandenPerAmne', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'NationalMeddelandenPerAmneCtrl',
                resolve: { config: app.meddelandenPerAmneConfig },
                title: 'nav.meddelandenperamne'
            }).when('/nationell/andelkompletteringar', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'NationalAndelKompletteringarCtrl',
                resolve: { config: app.andelKompletteringarConfig },
                title: 'nav.andelkompletteringar'
            }).when('/nationell/intygPerTyp', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'NationalIntygPerTypCtrl',
                resolve: { config: app.intygPerTypePerMonthConfig },
                title: 'nav.intygpertyp'
            }).when('/nationell/certificatePerCaseTvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'NationalCertificatePerCaseCtrl',
                resolve: { config: app.certificatePerCaseTvarsnittConfig },
                title: 'nav.intyg-pers-jukfall'
            }).when('/verksamhet', {
                redirectTo: '/verksamhet/oversikt'
            }).when('/valjVardgivare', {
                templateUrl: '/app/views/business/selectVg.html',
                controller: 'selectVgCtrl',
                title: 'Välj vårdgivare'
            }).when('/verksamhet/oversikt', {
                templateUrl: '/app/views/business/businessOverview.html',
                controller: 'businessOverviewCtrl',
                title: 'nav.oversikt'
            }).when('/verksamhet/nodata', {
                templateUrl: '/app/views/business/noDataAvailable.html',
                title: 'Data saknas'
            }).when('/verksamhet/sjukfallPerManad', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'singleLineChartCtrl',
                controllerAs: 'VerksamhetCasesPerMonthCtrl',
                resolve: { config: app.casesPerMonthConfig },
                title: 'nav.sjukfall-totalt'
            }).when('/verksamhet/sjukfallPerManadTvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetCasesPerMonthCtrl',
                resolve: { config: app.casesPerMonthTvarsnittConfig },
                title: 'nav.sjukfall-totalt'
            }).when('/verksamhet/diagnosgrupp', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetDiagnosgruppCtrl',
                resolve: { config: app.diagnosisGroupConfig },
                title: 'nav.diagnosgrupp'
            }).when('/verksamhet/diagnosgrupptvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetDiagnosgruppCtrl',
                resolve: { config: app.diagnosisGroupTvarsnittConfig },
                title: 'nav.diagnosgrupp'
            }).when('/verksamhet/diagnosavsnitt/kapitel/:kapitelId/avsnitt/:avsnittId/kategori/:kategoriId', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetDiagnosavsnittCtrl',
                resolve: { config: app.diagnosisSubGroupConfig },
                title: 'nav.enskilt-diagnoskapitel'
            }).when('/verksamhet/diagnosavsnitt/kapitel/:kapitelId/avsnitt/:avsnittId', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetDiagnosavsnittCtrl',
                resolve: { config: app.diagnosisSubGroupConfig },
                title: 'nav.enskilt-diagnoskapitel'
            }).when('/verksamhet/diagnosavsnitt/kapitel/:kapitelId', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetDiagnosavsnittCtrl',
                resolve: { config: app.diagnosisSubGroupConfig },
                title: 'nav.enskilt-diagnoskapitel'
            }).when('/verksamhet/diagnosavsnitt', {
                redirectTo: '/verksamhet/diagnosavsnitt/kapitel/A00-B99'
            }).when('/verksamhet/diagnosavsnitttvarsnitt/kapitel/:kapitelId/avsnitt/:avsnittId/kategori/:kategoriId', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetDiagnosavsnittCtrl',
                resolve: { config: app.diagnosisSubGroupTvarsnittConfig },
                title: 'nav.enskilt-diagnoskapitel'
            }).when('/verksamhet/diagnosavsnitttvarsnitt/kapitel/:kapitelId/avsnitt/:avsnittId', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetDiagnosavsnittCtrl',
                resolve: { config: app.diagnosisSubGroupTvarsnittConfig },
                title: 'nav.enskilt-diagnoskapitel'
            }).when('/verksamhet/diagnosavsnitttvarsnitt/kapitel/:kapitelId', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetDiagnosavsnittCtrl',
                resolve: { config: app.diagnosisSubGroupTvarsnittConfig },
                title: 'nav.enskilt-diagnoskapitel'
            }).when('/verksamhet/diagnosavsnitttvarsnitt', {
                redirectTo: '/verksamhet/diagnosavsnitttvarsnitt/kapitel/A00-B99'
            }).when('/verksamhet/jamforDiagnoser/:diagnosHash', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetCompareDiagnosisCtrl',
                resolve: { config: app.compareDiagnosis },
                title: 'nav.jamfor-diagnoser'
            }).when('/verksamhet/jamforDiagnoser', {
                redirectTo: '/verksamhet/jamforDiagnoser/-'
            }).when('/verksamhet/jamforDiagnoserTidsserie/:diagnosHash', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetCompareDiagnosisCtrl',
                resolve: { config: app.compareDiagnosisTimeSeriesConfig },
                title: 'nav.jamfor-diagnoser'
            }).when('/verksamhet/jamforDiagnoserTidsserie', {
                redirectTo: '/verksamhet/jamforDiagnoserTidsserie/-'
            }).when('/verksamhet/aldersgrupper', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetAgeGroupCtrl',
                resolve: { config: app.nationalAgeGroupConfig },
                title: 'nav.aldersgrupp'
            }).when('/verksamhet/aldersgrupperTidsserie', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetAgeGroupCtrl',
                resolve: { config: app.nationalAgeGroupTimeSeriesConfig },
                title: 'nav.aldersgrupp'
            }).when('/verksamhet/sjukskrivningsgrad', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetDegreeOfSickLeaveCtrl',
                resolve: { config: app.degreeOfSickLeaveConfig },
                title: 'nav.sjukskrivningsgrad'
            }).when('/verksamhet/sjukskrivningsgradtvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetDegreeOfSickLeaveCtrl',
                resolve: { config: app.degreeOfSickLeaveTvarsnittConfig },
                title: 'nav.sjukskrivningsgrad'
            }).when('/verksamhet/kompletteringarperfraga', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetKompletteringarPerFragaCtrl',
                resolve: { config: app.kompletteringarPerFragaTvarsnittConfig },
                title: 'nav.kompletteringarperfraga'
            }).when('/verksamhet/kompletteringarperfragaTidsserie', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetKompletteringarPerFragaCtrl',
                resolve: { config: app.kompletteringarPerFragaTimeSeriesConfig },
                title: 'nav.kompletteringarperfraga'
            }).when('/verksamhet/sjukskrivningslangd', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetSickLeaveLengthCtrl',
                resolve: { config: app.nationalSickLeaveLengthConfig },
                title: 'nav.sjukskrivningslangd'
            }).when('/verksamhet/sjukskrivningslangdTidsserie', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetSickLeaveLengthCtrl',
                resolve: { config: app.sickLeaveLengthTimeSeriesConfig },
                title: 'nav.sjukskrivningslangd'
            }).when('/verksamhet/langasjukskrivningar', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'singleLineChartCtrl',
                controllerAs: 'VerksamhetLongSickLeavesCtrl',
                resolve: { config: app.longSickLeavesConfig },
                title: 'nav.sjukskrivningslangd-mer-an-90-dagar'
            }).when('/verksamhet/langasjukskrivningartvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetLongSickLeavesCtrl',
                resolve: { config: app.longSickLeavesTvarsnittConfig },
                title: 'nav.sjukskrivningslangd-mer-an-90-dagar'
            }).when('/verksamhet/sjukfallperenhet', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetCasesPerBusinessCtrl',
                resolve: { config: app.casesPerBusinessConfig },
                title: 'nav.vardenhet'
            }).when('/verksamhet/sjukfallperenhettidsserie', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetCasesPerBusinessCtrl',
                resolve: { config: app.casesPerBusinessTimeSeriesConfig },
                title: 'nav.vardenhet'
            }).when('/verksamhet/sjukfallperlakare', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetCasesPerLakareCtrl',
                resolve: { config: app.casesPerLakareConfig },
                title: 'nav.lakare'
            }).when('/verksamhet/sjukfallperlakaretidsserie', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetCasesPerLakareCtrl',
                resolve: { config: app.casesPerLakareTimeSeriesConfig },
                title: 'nav.lakare'
            }).when('/verksamhet/sjukfallperlakaresalderochkon', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetLakaresAlderOchKonCtrl',
                resolve: { config: app.casesPerLakaresAlderOchKonConfig },
                title: 'nav.lakaralder-kon'
            }).when('/verksamhet/sjukfallperlakaresalderochkontidsserie', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetLakaresAlderOchKonCtrl',
                resolve: { config: app.casesPerLakaresAlderOchKonTidsserieConfig },
                title: 'nav.lakaralder-kon'
            }).when('/verksamhet/sjukfallperlakarbefattning', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetLakarbefattningCtrl',
                resolve: { config: app.casesPerLakarbefattningConfig },
                title: 'nav.lakarbefattning'
            }).when('/verksamhet/sjukfallperlakarbefattningtidsserie', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetLakarbefattningCtrl',
                resolve: { config: app.casesPerLakarbefattningTidsserieConfig },
                title: 'nav.lakarbefattning'
            }).when('/verksamhet/certificatePerCaseTvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetCertificatePerCaseCtrl',
                resolve: { config: app.certificatePerCaseTvarsnittConfig },
                title: 'nav.intyg-pers-jukfall'
            }).when('/verksamhet/certificatePerCaseTidsserie', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetCertificatePerCaseCtrl',
                resolve: { config: app.certificatePerCaseTidsserieConfig },
                title: 'nav.intyg-pers-jukfall'
            }).when('/verksamhet/meddelanden', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'singleLineChartCtrl',
                controllerAs: 'VerksamhetMeddelandenPerMonthCtrl',
                resolve: { config: app.meddelandenPerMonthConfig },
                title: 'nav.meddelanden'
            }).when('/verksamhet/meddelandenTvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetMeddelandenPerMonthCtrl',
                resolve: { config: app.meddelandenPerMonthTvarsnittConfig },
                title: 'nav.meddelanden'
            }).when('/verksamhet/meddelandenPerAmne', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetMeddelandenPerAmneCtrl',
                resolve: { config: app.meddelandenPerAmneConfig },
                title: 'nav.meddelandenperamne'
            }).when('/verksamhet/meddelandenPerAmneTvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetMeddelandenPerAmneCtrl',
                resolve: { config: app.meddelandenPerAmneTvarsnittConfig },
                title: 'nav.meddelandenperamne'
            }).when('/verksamhet/meddelandenPerAmneOchEnhet', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'singleLineChartCtrl',
                controllerAs: 'VerksamhetMeddelandenPerAmneOchEnhetCtrl',
                resolve: { config: app.meddelandenPerAmneOchEnhetConfig },
                title: 'nav.meddelandenperamneochenhet'
            }).when('/verksamhet/meddelandenPerAmneOchEnhetTvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetMeddelandenPerAmneOchEnhetCtrl',
                resolve: { config: app.meddelandenPerAmneOchEnhetTvarsnittConfig },
                title: 'nav.meddelandenperamneochenhet'
            }).when('/verksamhet/meddelandenPerAmneOchLakare', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'singleLineChartCtrl',
                controllerAs: 'VerksamhetMeddelandenPerAmneOchLakareCtrl',
                resolve: { config: app.meddelandenPerAmneOchLakareConfig },
                title: 'nav.meddelandenperamneochlakare'
            }).when('/verksamhet/meddelandenPerAmneOchLakareTvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetMeddelandenPerAmneOchLakareCtrl',
                resolve: { config: app.meddelandenPerAmneOchLakareTvarsnittConfig },
                title: 'nav.meddelandenperamneochlakare'
            }).when('/verksamhet/intyg', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'singleLineChartCtrl',
                controllerAs: 'VerksamhetIntygPerMonthCtrl',
                resolve: { config: app.intygPerMonthConfig },
                title: 'nav.intyg'
            }).when('/verksamhet/intygTvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetIntygPerMonthTvarsnittCtrl',
                resolve: { config: app.intygPerMonthTvarsnittConfig },
                title: 'nav.intyg'
            }).when('/verksamhet/intygPerTyp', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetIntygPerTypeCtrl',
                resolve: { config: app.intygPerTypePerMonthConfig },
                title: 'nav.intygpertyp'
            }).when('/verksamhet/intygPerTypTvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetIntygPerTypeCtrl',
                resolve: { config: app.intygPerTypeTvarsnittConfig },
                title: 'nav.intygpertyp'
            }).when('/verksamhet/andelkompletteringar', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'VerksamhetAndelKompletteringarCtrl',
                resolve: { config: app.andelKompletteringarConfig },
                title: 'nav.andelkompletteringar'
            }).when('/verksamhet/andelkompletteringartvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'VerksamhetAndelKompletteringarCtrl',
                resolve: { config: app.andelKompletteringarTvarsnittConfig },
                title: 'nav.andelkompletteringar'
            }).when('/region/filuppladdning', {
                templateUrl: '/app/views/region/fileupload.html',
                controller: 'regionFileUploadCtrl',
                controllerAs: 'RegionFileUploadCtrl',
                title: 'Filuppladdning'
            }).when('/region/sjukfallPerManad', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'singleLineChartCtrl',
                controllerAs: 'RegionCasesPerMonthCtrl',
                resolve: { config: app.casesPerMonthConfig },
                title: 'nav.sjukfall-totalt'
            }).when('/region/sjukfallPerEnhet', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'RegionCasesPerBusinessCtrl',
                resolve: { config: app.casesPerBusinessConfig },
                title: 'nav.vardenhet'
            }).when('/region/sjukfallPerListningarPerEnhet', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'RegionCasesPerPatientsPerBusinessCtrl',
                resolve: { config: app.casesPerPatientsPerBusinessConfig },
                title: 'nav.region.listningsjamforelse'
            }).when('/region/certificatePerCaseTvarsnitt', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'RegionCertificatePerCaseCtrl',
                resolve: { config: app.certificatePerCaseTvarsnittConfig },
                title: 'nav.intyg-pers-jukfall'
            }).when('/region/meddelandenPerAmne', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'RegionMeddelandenPerAmneCtrl',
                resolve: { config: app.meddelandenPerAmneRegionConfig },
                title: 'nav.meddelandenperamne'
            }).when('/region/meddelandenPerAmneOchEnhet', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'RegionMeddelandenPerAmneOchEnhetCtrl',
                resolve: { config: app.meddelandenPerAmneOchEnhetRegionConfig },
                title: 'nav.meddelandenperamneochenhet'
            }).when('/region/intygPerTyp', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'RegionIntygPerTypCtrl',
                resolve: { config: app.intygPerTypePerMonthRegionConfig },
                title: 'nav.intygpertyp'
            }).when('/region/andelkompletteringar', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'doubleAreaChartsCtrl',
                controllerAs: 'RegionAndelKompletteringarCtrl',
                resolve: { config: app.andelKompletteringarRegionConfig },
                title: 'nav.andelkompletteringar'
            }).when('/region/kompletteringarperfraga', {
                templateUrl: '/app/views/detailsView.html',
                controller: 'columnChartDetailsViewCtrl',
                controllerAs: 'RegionKompletteringarPerFragaCtrl',
                resolve: { config: app.kompletteringarPerFragaRegionConfig },
                title: 'nav.kompletteringarperfraga'
            }).when('/region/om', {
                templateUrl: '/app/views/region/aboutregion.html',
                controllerAs: 'RegionAboutCtrl',
                title: 'Om Regionsstatistik'
            }).when('/om/kontakt', {
                templateUrl: '/app/views/about/contact.html',
                controllerAs: 'AboutContactCtrl',
                title: 'Kontakt till support'
            }).when('/', {
                redirectTo: '/nationell/oversikt'
            }).when('/nationell', {
                redirectTo: '/nationell/oversikt'
            }).when('/landsting:rest*', {
                redirectTo: function(path, path2, search) {
                    var searchPath = '';

                    angular.forEach(search, function(value, key) {
                        var returnVal = '';

                        if (searchPath) {
                            returnVal += '&';
                        }

                        if (key === 'landstingfilter') {
                            key = 'regionfilter';
                        }

                        returnVal +=  key + '=' + value;

                        searchPath += returnVal;
                    });

                    var url = '/region' + path.rest;

                    if (searchPath) {
                        url += '?' + searchPath;
                    }

                    return url;
                }
            }).otherwise({
                templateUrl: '/app/views/error/pageNotFound.html',
                title: 'Fel'
            });

        });
