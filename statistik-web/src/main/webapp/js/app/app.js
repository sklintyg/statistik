'use strict';

var underscore = angular.module('underscore', []);
underscore.factory('_', function() {
    return window._;
});

/* App Module */
var app = angular.module('StatisticsApp',
    ['ngRoute',
     'ngCookies',
     'ngSanitize',
     'ui.bootstrap',
     'underscore',
     'StatisticsApp.constants',
     'StatisticsApp.treeMultiSelector',
     'StatisticsApp.chartSeriesButtonGroup',
     'StatisticsApp.businessFilter',
     'StatisticsApp.charts',
     'dropzone',
     'ngStorage'])
    .config(
    [ '$routeProvider', function ($routeProvider) {
        $routeProvider.when('/login', {
            templateUrl: 'views/login.html',
            controller: 'loginCtrl',
            title: 'Inloggning'
        }).when('/fakelogin', {
            templateUrl: 'views/fakelogin.html',
            title: 'Fake Login Page'
        }).when('/serverbusy', {
            templateUrl: 'views/error/serverBusy.html',
            title: 'Tjänsten överbelastad'
        }).when('/nationell/oversikt', {
            templateUrl: 'views/overview.html',
            controller: 'overviewCtrl',
            controllerAs: 'NationalOverviewCtrl',
            title: 'Översikt'
        }).when('/nationell/sjukfallPerManad', {
            templateUrl: 'views/detailsView.html',
            controller: 'singleLineChartCtrl',
            controllerAs: 'NationalCasesPerMonthCtrl',
            resolve: { config: app.casesPerMonthConfig },
            title: 'Sjukfall per månad'
        }).when('/nationell/aldersgrupper', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'NationalAgeGroupCtrl',
            resolve: { config: app.nationalAgeGroupConfig },
            title: 'Åldersgrupper'
        }).when('/nationell/sjukskrivningslangd', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'NationalSickLeaveLengthCtrl',
            resolve: { config: app.nationalSickLeaveLengthConfig },
            title: 'Sjukskrivningslängd'
        }).when('/nationell/lan', {
            templateUrl: 'views/detailsView.html',
            controller: 'casesPerCountyCtrl',
            controllerAs: 'NationalCasesPerCountyCtrl',
            title: 'Län'
        }).when('/nationell/diagnosgrupp', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'NationalDiagnosgruppCtrl',
            resolve: { config: app.diagnosisGroupConfig },
            title: 'Diagnosgrupper'
        }).when('/nationell/diagnosavsnitt/kapitel/:kapitelId', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'NationalDiagnosavsnittCtrl',
            resolve: { config: app.diagnosisSubGroupConfig },
            title: 'Enskilt diagnoskapitel'
        }).when('/nationell/diagnosavsnitt', {
            redirectTo: '/nationell/diagnosavsnitt/kapitel/A00-B99'
        }).when('/nationell/sjukskrivningsgrad', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'NationalDegreeOfSickLeaveCtrl',
            resolve: { config: app.degreeOfSickLeaveConfig },
            title: 'Sjukskrivningsgrad'
        }).when('/nationell/andelSjukfallPerKon', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'NationalCasesPerSexCtrl',
            resolve: { config: app.casesPerSexConfig },
            title: 'Andel sjukfall per kön'
        }).when('/verksamhet', {
            templateUrl: 'views/empty.html',
            controller: 'businessLandingPageCtrl',
            title: 'Verksamhet'
        }).when('/verksamhet/oversikt', {
            templateUrl: 'views/business/businessOverview.html',
            controller: 'businessOverviewCtrl',
            title: 'Verksamhetsöversikt'
        }).when('/verksamhet/nodata', {
            templateUrl: 'views/business/noDataAvailable.html',
            title: 'Data saknas'
        }).when('/verksamhet/sjukfallPerManad', {
            templateUrl: 'views/detailsView.html',
            controller: 'singleLineChartCtrl',
            controllerAs: 'VerksamhetCasesPerMonthCtrl',
            resolve: { config: app.casesPerMonthConfig },
            title: 'Sjukfall per månad'
        }).when('/verksamhet/sjukfallPerManadTvarsnitt', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetCasesPerMonthCtrl',
            resolve: { config: app.casesPerMonthTvarsnittConfig },
            title: 'Sjukfall per månad'
        }).when('/verksamhet/diagnosgrupp', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetDiagnosgruppCtrl',
            resolve: { config: app.diagnosisGroupConfig },
            title: 'Diagnosgrupper'
        }).when('/verksamhet/diagnosgrupptvarsnitt', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetDiagnosgruppCtrl',
            resolve: { config: app.diagnosisGroupTvarsnittConfig },
            title: 'Diagnosgrupper'
        }).when('/verksamhet/diagnosavsnitt/kapitel/:kapitelId/avsnitt/:avsnittId/kategori/:kategoriId', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetDiagnosavsnittCtrl',
            resolve: { config: app.diagnosisSubGroupConfig },
            title: 'Enskilt diagnoskapitel'
        }).when('/verksamhet/diagnosavsnitt/kapitel/:kapitelId/avsnitt/:avsnittId', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetDiagnosavsnittCtrl',
            resolve: { config: app.diagnosisSubGroupConfig },
            title: 'Enskilt diagnoskapitel'
        }).when('/verksamhet/diagnosavsnitt/kapitel/:kapitelId', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetDiagnosavsnittCtrl',
            resolve: { config: app.diagnosisSubGroupConfig },
            title: 'Enskilt diagnoskapitel'
        }).when('/verksamhet/diagnosavsnitt', {
            redirectTo: '/verksamhet/diagnosavsnitt/kapitel/A00-B99'
        }).when('/verksamhet/diagnosavsnitttvarsnitt/kapitel/:kapitelId/avsnitt/:avsnittId/kategori/:kategoriId', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetDiagnosavsnittCtrl',
            resolve: { config: app.diagnosisSubGroupTvarsnittConfig },
            title: 'Enskilt diagnoskapitel'
        }).when('/verksamhet/diagnosavsnitttvarsnitt/kapitel/:kapitelId/avsnitt/:avsnittId', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetDiagnosavsnittCtrl',
            resolve: { config: app.diagnosisSubGroupTvarsnittConfig },
            title: 'Enskilt diagnoskapitel'
        }).when('/verksamhet/diagnosavsnitttvarsnitt/kapitel/:kapitelId', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetDiagnosavsnittCtrl',
            resolve: { config: app.diagnosisSubGroupTvarsnittConfig },
            title: 'Enskilt diagnoskapitel'
        }).when('/verksamhet/diagnosavsnitttvarsnitt', {
            redirectTo: '/verksamhet/diagnosavsnitttvarsnitt/kapitel/A00-B99'
        }).when('/verksamhet/jamforDiagnoser/:diagnosHash', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetCompareDiagnosisCtrl',
            resolve: { config: app.compareDiagnosis },
            title: 'Jämför valfria diagnoser'
        }).when('/verksamhet/jamforDiagnoser', {
            redirectTo: '/verksamhet/jamforDiagnoser/-'
        }).when('/verksamhet/jamforDiagnoserTidsserie/:diagnosHash', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetCompareDiagnosisCtrl',
            resolve: { config: app.compareDiagnosisTimeSeriesConfig },
            title: 'Jämför valfria diagnoser'
        }).when('/verksamhet/jamforDiagnoserTidsserie', {
            redirectTo: '/verksamhet/jamforDiagnoserTidsserie/-'
        }).when('/verksamhet/aldersgrupper', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetAgeGroupCtrl',
            resolve: { config: app.nationalAgeGroupConfig },
            title: 'Åldersgrupper'
        }).when('/verksamhet/aldersgrupperTidsserie', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetAgeGroupCtrl',
            resolve: { config: app.nationalAgeGroupTimeSeriesConfig },
            title: 'Åldersgrupper'
        }).when('/verksamhet/sjukskrivningsgrad', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetDegreeOfSickLeaveCtrl',
            resolve: { config: app.degreeOfSickLeaveConfig },
            title: 'Sjukskrivningsgrad'
        }).when('/verksamhet/sjukskrivningsgradtvarsnitt', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetDegreeOfSickLeaveCtrl',
            resolve: { config: app.degreeOfSickLeaveTvarsnittConfig },
            title: 'Sjukskrivningsgrad'
        }).when('/verksamhet/differentieratintygande', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetDifferentieratIntygandeCtrl',
            resolve: { config: app.differentieratIntygandeConfig },
            title: 'Differentierat intygande'
        }).when('/verksamhet/differentieratintygandetvarsnitt', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetDifferentieratIntygandeCtrl',
            resolve: { config: app.differentieratIntygandeTvarsnittConfig },
            title: 'Differentierat intygande'
        }).when('/verksamhet/sjukskrivningslangd', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetSickLeaveLengthCtrl',
            resolve: { config: app.nationalSickLeaveLengthConfig },
            title: 'Sjukskrivningslängd'
        }).when('/verksamhet/sjukskrivningslangdTidsserie', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetSickLeaveLengthCtrl',
            resolve: { config: app.sickLeaveLengthTimeSeriesConfig },
            title: 'Sjukskrivningslängd'
        }).when('/verksamhet/langasjukskrivningar', {
            templateUrl: 'views/detailsView.html',
            controller: 'singleLineChartCtrl',
            controllerAs: 'VerksamhetLongSickLeavesCtrl',
            resolve: { config: app.longSickLeavesConfig },
            title: 'Sjukskrivningslängd mer än 90 dagar'
        }).when('/verksamhet/langasjukskrivningartvarsnitt', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetLongSickLeavesCtrl',
            resolve: { config: app.longSickLeavesTvarsnittConfig },
            title: 'Sjukskrivningslängd mer än 90 dagar'
        }).when('/verksamhet/sjukfallperenhet', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetCasesPerBusinessCtrl',
            resolve: { config: app.casesPerBusinessConfig },
            title: 'Antal sjukfall per vårdenhet'
        }).when('/verksamhet/sjukfallperenhettidsserie', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetCasesPerBusinessCtrl',
            resolve: { config: app.casesPerBusinessTimeSeriesConfig },
            title: 'Antal sjukfall per vårdenhet'
        }).when('/verksamhet/sjukfallperlakare', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetCasesPerLakareCtrl',
            resolve: { config: app.casesPerLakareConfig },
            title: 'Antal sjukfall per läkare'
        }).when('/verksamhet/sjukfallperlakaretidsserie', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetCasesPerLakareCtrl',
            resolve: { config: app.casesPerLakareTimeSeriesConfig },
            title: 'Antal sjukfall per läkare'
        }).when('/verksamhet/sjukfallperlakaresalderochkon', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetLakaresAlderOchKonCtrl',
            resolve: { config: app.casesPerLakaresAlderOchKonConfig },
            title: 'Sjukfall per läkarens ålder och kön'
        }).when('/verksamhet/sjukfallperlakaresalderochkontidsserie', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetLakaresAlderOchKonCtrl',
            resolve: { config: app.casesPerLakaresAlderOchKonTidsserieConfig },
            title: 'Sjukfall per läkarens ålder och kön'
        }).when('/verksamhet/sjukfallperlakarbefattning', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetLakarbefattningCtrl',
            resolve: { config: app.casesPerLakarbefattningConfig },
            title: 'Sjukfall per läkarbefattning'
        }).when('/verksamhet/sjukfallperlakarbefattningtidsserie', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetLakarbefattningCtrl',
            resolve: { config: app.casesPerLakarbefattningTidsserieConfig },
            title: 'Sjukfall per läkarbefattning'
        }).when('/landsting/filuppladdning', {
            templateUrl: 'views/landsting/fileupload.html',
            controller: 'landstingFileUploadCtrl',
            controllerAs: 'LandstingFileUploadCtrl',
            title: 'Filuppladdning'
        }).when('/landsting/sjukfallPerManad', {
            templateUrl: 'views/detailsView.html',
            controller: 'singleLineChartCtrl',
            controllerAs: 'LandstingCasesPerMonthCtrl',
            resolve: { config: app.casesPerMonthConfig },
            title: 'Sjukfall per månad'
        }).when('/landsting/sjukfallPerEnhet', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'LandstingCasesPerBusinessCtrl',
            resolve: { config: app.casesPerBusinessConfig },
            title: 'Antal sjukfall per vårdenhet'
        }).when('/landsting/sjukfallPerListningarPerEnhet', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'LandstingCasesPerPatientsPerBusinessCtrl',
            resolve: { config: app.casesPerPatientsPerBusinessConfig },
            title: 'Antal sjukfall per 1000 listningar'
        }).when('/landsting/om', {
            templateUrl: 'views/landsting/aboutlandsting.html',
            controllerAs: 'LandstingAboutCtrl',
            title: 'Om Landstingsstatistik'
        }).when('/om/tjansten', {
            templateUrl: 'views/about/about.html',
            controllerAs: 'AboutServiceCtrl',
            title: 'Om tjänsten'
        }).when('/om/kontakt', {
            templateUrl: 'views/about/contact.html',
            controllerAs: 'AboutContactCtrl',
            title: 'Kontakt till support'
        }).when('/om/vanligafragor', {
            templateUrl: 'views/about/faq.html',
            controller: 'aboutFaqCtrl',
            controllerAs: 'AboutFaqCtrl',
            title: 'Vanliga frågor och svar'
        }).when('/om/inloggning', {
            templateUrl: 'views/about/login.html',
            controllerAs: 'AboutLoginCtrl',
            title: 'Inloggning och behörighet'
        }).when('/', {
            redirectTo: '/nationell/oversikt'
        }).otherwise({
            templateUrl: 'views/error/pageNotFound.html',
            title: 'Fel'
        });

    } ]);

app.run([ '$rootScope', '$route', 'messageService', function ($rootScope, $route, messageService) {
    $rootScope.lang = 'sv';
    $rootScope.DEFAULT_LANG = 'sv';
    messageService.addResources(stMessages);

    Highcharts.setOptions({
        lang: { thousandsSep: ' ' }
    });

    $rootScope.page_title = 'Statistiktjänsten';
    $rootScope.pageName = '';
    $rootScope.isLoggedIn = isLoggedIn;

    $rootScope.$on('$routeChangeSuccess', function (e, current) {
        if ($route.current.$$route) {
            $rootScope.pageName = $route.current.$$route.title;
            $rootScope.page_title = ($rootScope.pageName ? $rootScope.pageName + ' | ' : '') + 'Statistiktjänsten';
            $rootScope.queryString = current.params.filter ? "?filter=" + current.params.filter : "";
            $rootScope.queryString += current.params.landstingfilter ? ($rootScope.queryString.length > 0 ? "&" : "?") + ("landstingfilter=" + current.params.landstingfilter) : "";
            $rootScope.verksamhetViewShowing = current.$$route.originalPath.indexOf("/verksamhet") === 0;
        }
    });
} ]);

app.config(['$httpProvider', function($httpProvider) {
    if (!$httpProvider.defaults.headers.get) {
        $httpProvider.defaults.headers.get = {};
    }

    //disable IE ajax request caching
    $httpProvider.defaults.headers.get['If-Modified-Since'] = 'Mon, 26 Jul 1997 05:00:00 GMT';

    $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
    $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
}]);
