'use strict';

var underscore = angular.module('underscore', []);
underscore.factory('_', function() {
    return window._;
});

/* App Module */
var app = angular.module('StatisticsApp', [ 'ngRoute', 'ngCookies', 'ngSanitize', 'ui.bootstrap', 'underscore' ])
    .constant("COUNTY_COORDS", [{name: 'blekinge', xy: {"x": 35, "y": 15}} , {name: 'dalarna', xy: {"x": 31, "y": 50}},
        {name: 'halland', xy: {"x": 14, "y": 20}}, {name: 'kalmar', xy: {"x": 40, "y": 20}},
        {name: 'kronoberg', xy: {"x": 32, "y": 19}}, {name: 'gotland', xy: {"x": 55, "y": 22}},
        {name: 'gävleborg', xy: {"x": 45, "y": 50}}, {name: 'jämtland', xy: {"x": 29, "y": 66}},
        {name: 'jönköping', xy: {"x": 28, "y": 24}}, {name: 'norrbotten', xy: {"x": 59, "y": 94}},
        {name: 'skåne', xy: {"x": 21, "y": 11}}, {name: 'stockholm', xy: {"x": 52, "y": 37}},
        {name: 'södermanland', xy: {"x": 44, "y": 34}}, {name: 'uppsala', xy: {"x": 50, "y": 42}},
        {name: 'värmland', xy: {"x": 21, "y": 42}}, {name: 'västerbotten', xy: {"x": 51, "y": 80}},
        {name: 'västernorrland', xy: {"x": 48, "y": 67}}, {name: 'västmanland', xy: {"x": 42, "y": 42}},
        {name: 'västra götaland', xy: {"x": 12, "y": 32}}, {name: 'örebro', xy: {"x": 32, "y": 38}},
        {name: 'östergötland', xy: {"x": 40, "y": 30}}, {name: 'DEFAULT', xy: {"x": 12, "y": 94}}]
    )
    .config(
    [ '$routeProvider', function ($routeProvider) {
        $routeProvider.when('/login', {
            templateUrl: 'views/login.html',
            controller: 'loginCtrl',
            title: 'Login Page'
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
        }).when('/nationell/diagnosavsnitt/:groupId', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'NationalDiagnosavsnittCtrl',
            resolve: { config: app.diagnosisSubGroupConfig },
            title: 'Enskilt diagnoskapitel'
        }).when('/nationell/diagnosavsnitt', {
            redirectTo: '/nationell/diagnosavsnitt/A00-B99'
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
        }).when('/verksamhet/diagnosgrupp', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetDiagnosgruppCtrl',
            resolve: { config: app.diagnosisGroupConfig },
            title: 'Diagnosgrupper'
        }).when('/verksamhet/diagnosavsnitt/:groupId/kategori/:kategoriId', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetDiagnoskategoriCtrl',
            resolve: { config: app.diagnosisSubGroupConfig },
            title: 'Enskilt diagnoskapitel'
        }).when('/verksamhet/diagnosavsnitt/:groupId', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetDiagnosavsnittCtrl',
            resolve: { config: app.diagnosisSubGroupConfig },
            title: 'Enskilt diagnoskapitel'
        }).when('/verksamhet/diagnosavsnitt', {
            redirectTo: '/verksamhet/diagnosavsnitt/A00-B99'
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
        }).when('/verksamhet/aldersgrupperpagaende', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetAgeGroupCurrentCtrl',
            resolve: { config: app.nationalAgeGroupCurrentConfig },
            title: 'Åldersgrupper'
        }).when('/verksamhet/sjukskrivningsgrad', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetDegreeOfSickLeaveCtrl',
            resolve: { config: app.degreeOfSickLeaveConfig },
            title: 'Sjukskrivningsgrad'
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
        }).when('/verksamhet/sjukskrivningslangdpagaende', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetSickLeaveLengthCurrentCtrl',
            resolve: { config: app.nationalSickLeaveLengthCurrentConfig },
            title: 'Sjukskrivningslängd'
        }).when('/verksamhet/langasjukskrivningar', {
            templateUrl: 'views/detailsView.html',
            controller: 'singleLineChartCtrl',
            controllerAs: 'VerksamhetLongSickLeavesCtrl',
            resolve: { config: app.longSickLeavesConfig },
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

    $rootScope.page_title = 'Titel';
    $rootScope.pageName = '';
    $rootScope.isLoggedIn = isLoggedIn;

    $rootScope.$on('$routeChangeSuccess', function (e, current) {
        if ($route.current.$$route) {
            $rootScope.pageName = $route.current.$$route.title;
            $rootScope.page_title = $route.current.$$route.title + ' | Statistiktjänsten';
            $rootScope.queryString = current.params.filter ? "?filter=" + current.params.filter : "";
            $rootScope.filterIsActive = !!current.params.filter;
            $rootScope.verksamhetViewShowing = current.$$route.originalPath.indexOf("/verksamhet") === 0;
        }
    });
} ]);
