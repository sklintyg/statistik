'use strict';

var underscore = angular.module('underscore', []);
underscore.factory('_', function() {
    return window._;
});

/* App Module */
var app = angular.module('StatisticsApp', [ 'ngRoute', 'ngCookies', 'ngSanitize', 'ui.bootstrap', 'underscore' ]).config(
    [ '$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {
        $routeProvider.when('/login', {
            templateUrl: 'views/login.html',
            controller: 'loginCtrl',
            title: 'Login Page'
        }).when('/fakelogin', {
            templateUrl: 'views/fakelogin.html',
            title: 'Fake Login Page'
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
        }).when('/verksamhet/:verksamhetId/oversikt', {
            templateUrl: 'views/business/businessOverview.html',
            controller: 'businessOverviewCtrl',
            title: 'Verksamhetsöversikt'
        }).when('/verksamhet/:verksamhetId/nodata', {
            templateUrl: 'views/business/noDataAvailable.html',
            title: 'Data saknas'
        }).when('/verksamhet/:verksamhetId/sjukfallPerManad', {
            templateUrl: 'views/detailsView.html',
            controller: 'singleLineChartCtrl',
            controllerAs: 'VerksamhetCasesPerMonthCtrl',
            resolve: { config: app.casesPerMonthConfig },
            title: 'Sjukfall per månad'
        }).when('/verksamhet/:verksamhetId/diagnosgrupp', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetDiagnoskapitelCtrl',
            resolve: { config: app.diagnosisGroupConfig },
            title: 'Diagnosgrupper'
        }).when('/verksamhet/:verksamhetId/diagnosavsnitt/:groupId/kategori/:kategoriId', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetDiagnoskategoriCtrl',
            resolve: { config: app.diagnosisSubGroupConfig },
            title: 'Enskilt diagnoskapitel'
        }).when('/verksamhet/:verksamhetId/diagnosavsnitt/:groupId', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetDiagnosavsnittCtrl',
            resolve: { config: app.diagnosisSubGroupConfig },
            title: 'Enskilt diagnoskapitel'
        }).when('/verksamhet/:verksamhetId/diagnosavsnitt', {
            redirectTo: '/verksamhet/:verksamhetId/diagnosavsnitt/A00-B99'
        }).when('/verksamhet/:verksamhetId/jamforDiagnoser/:diagnosHash', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetCompareDiagnosisCtrl',
            resolve: { config: app.compareDiagnosis },
            title: 'Jämför valfria diagnoser'
        }).when('/verksamhet/:verksamhetId/jamforDiagnoser', {
            redirectTo: '/verksamhet/:verksamhetId/jamforDiagnoser/-'
        }).when('/verksamhet/:verksamhetId/aldersgrupper', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetAgeGroupCtrl',
            resolve: { config: app.nationalAgeGroupConfig },
            title: 'Åldersgrupper'
        }).when('/verksamhet/:verksamhetId/aldersgrupperpagaende', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetAgeGroupCurrentCtrl',
            resolve: { config: app.nationalAgeGroupCurrentConfig },
            title: 'Åldersgrupper'
        }).when('/verksamhet/:verksamhetId/sjukskrivningsgrad', {
            templateUrl: 'views/detailsView.html',
            controller: 'doubleAreaChartsCtrl',
            controllerAs: 'VerksamhetDegreeOfSickLeaveCtrl',
            resolve: { config: app.degreeOfSickLeaveConfig },
            title: 'Sjukskrivningsgrad'
        }).when('/verksamhet/:verksamhetId/sjukskrivningslangd', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetSickLeaveLengthCtrl',
            resolve: { config: app.nationalSickLeaveLengthConfig },
            title: 'Sjukskrivningslängd'
        }).when('/verksamhet/:verksamhetId/sjukskrivningslangdpagaende', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetSickLeaveLengthCurrentCtrl',
            resolve: { config: app.nationalSickLeaveLengthCurrentConfig },
            title: 'Sjukskrivningslängd'
        }).when('/verksamhet/:verksamhetId/langasjukskrivningar', {
            templateUrl: 'views/detailsView.html',
            controller: 'singleLineChartCtrl',
            controllerAs: 'VerksamhetLongSickLeavesCtrl',
            resolve: { config: app.longSickLeavesConfig },
            title: 'Sjukskrivningslängd mer än 90 dagar'
        }).when('/verksamhet/:verksamhetId/sjukfallperenhet', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetCasesPerBusinessCtrl',
            resolve: { config: app.casesPerBusinessConfig },
            title: 'Antal sjukfall per vårdenhet'
        }).when('/verksamhet/:verksamhetId/sjukfallperlakare', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetCasesPerLakareCtrl',
            resolve: { config: app.casesPerLakareConfig },
            title: 'Antal sjukfall per läkare'
        }).when('/verksamhet/:verksamhetId/sjukfallperlakaresalderochkon', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetLakaresAlderOchKonCtrl',
            resolve: { config: app.casesPerLakaresAlderOchKonConfig },
            title: 'Sjukfall per läkarens ålder och kön'
        }).when('/verksamhet/:verksamhetId/sjukfallperlakarbefattning', {
            templateUrl: 'views/detailsView.html',
            controller: 'columnChartDetailsViewCtrl',
            controllerAs: 'VerksamhetLakarbefattningCtrl',
            resolve: { config: app.casesPerLakarbefattningConfig },
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
            $rootScope.verksamhetViewShowing = current.$$route.originalPath.indexOf("/verksamhet") == 0
        }
    });
} ]);
