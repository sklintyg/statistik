'use strict';

/* App Module */
var app = {};

app.statisticsApp = angular.module('StatisticsApp', [ 'ngCookies', 'ui.bootstrap' ]).config(
        [ '$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
            $routeProvider.when('/nationell/oversikt', {
                templateUrl : 'views/overview.html',
                controller : 'OverviewCtrl',
                controllerAs : 'NationalOverviewCtrl',
	            title: 'Översikt'
            }).when('/nationell/sjukfallPerManad', {
                templateUrl : 'views/detailsView.html',
                controller : 'CasesPerMonthCtrl',
                controllerAs : 'NationalCasesPerMonthCtrl',
                resolve : { config: app.casesPerMonthConfig }, 
                title: 'Sjukfall per månad'
            }).when('/nationell/aldersgrupper', {
                templateUrl : 'views/detailsView.html',
                controller : 'AgeGroupsCtrl',
                controllerAs : 'NationalAgeGroupCtrl',
                resolve : { config: app.nationalAgeGroupConfig }, 
                title: 'Åldersgrupper'
            }).when('/nationell/sjukskrivningslangd', {
                templateUrl : 'views/detailsView.html',
                controller : 'SickLeaveLengthCtrl',
                controllerAs : 'NationalSickLeaveLengthCtrl',
                resolve : { config: app.nationalSickLeaveLengthConfig }, 
                title: 'Sjukskrivningslängd'
            }).when('/nationell/lan', {
                templateUrl : 'views/detailsView.html',
                controller : 'CasesPerCountyCtrl',
                controllerAs : 'NationalCasesPerCountyCtrl',
                title: 'Län'
            }).when('/nationell/diagnosgrupper', {
                templateUrl : 'views/detailsView.html',
                controller : 'DiagnosisGroupsCtrl',
                controllerAs : 'NationalDiagnosisGroupsCtrl',
                resolve : { config: app.diagnosisGroupConfig }, 
                title: 'Diagnosgrupper'
            }).when('/nationell/diagnoskapitel/:groupId', {
                templateUrl : 'views/detailsView.html',
                controller : 'DiagnosisGroupsCtrl',
                controllerAs : 'NationalDiagnosisSubGroupsCtrl',
                resolve : { config: app.diagnosisSubGroupConfig },
                title: 'Enskilt diagnoskapitel'
            }).when('/nationell/diagnoskapitel', {
                redirectTo : '/nationell/diagnoskapitel/A00-B99'
            }).when('/nationell/sjukskrivningsgrad', {
                templateUrl : 'views/detailsView.html',
                controller : 'DegreeOfSickLeaveCtrl',
                controllerAs : 'NationalDegreeOfSickLeaveCtrl',
                resolve : { config: app.degreeOfSickLeaveConfig },
                title: 'Sjukskrivningsgrad'
            }).when('/nationell/andelSjukfallPerKon', {
                templateUrl : 'views/detailsView.html',
                controller : 'CasesPerSexCtrl',
                controllerAs : 'NationalCasesPerSexCtrl',
                resolve : { config: app.casesPerSexConfig },
                title: 'Andel sjukfall per kön'
            }).when('/verksamhet', {
                templateUrl : 'views/empty.html',
                controller : 'BusinessLandingPageCtrl',
                title: 'Verksamhet'
            }).when('/verksamhet/:verksamhetId/oversikt', {
                templateUrl : 'views/business/businessOverview.html',
                controller : 'BusinessOverviewCtrl',
                controllerAs : 'BusinessOverviewCtrl',
                title: 'Verksamhetsöversikt'
            }).when('/verksamhet/:verksamhetId/sjukfallPerManad', {
                templateUrl : 'views/detailsView.html',
                controller : 'CasesPerMonthCtrl',
                controllerAs : 'VerksamhetCasesPerMonthCtrl',
                resolve : { config: app.casesPerMonthConfig }, 
                title: 'Sjukfall per månad'
            }).when('/verksamhet/:verksamhetId/diagnosgrupper', {
                templateUrl : 'views/detailsView.html',
                controller : 'DiagnosisGroupsCtrl',
                controllerAs : 'VerksamhetDiagnosisGroupsCtrl',
                resolve : { config: app.diagnosisGroupConfig }, 
                title: 'Diagnosgrupper'
            }).when('/verksamhet/:verksamhetId/diagnoskapitel/:groupId', {
                templateUrl : 'views/detailsView.html',
                controller : 'DiagnosisGroupsCtrl',
                controllerAs : 'VerksamhetDiagnosisSubGroupsCtrl',
                resolve : { config: app.diagnosisSubGroupConfig },
                title: 'Enskilt diagnoskapitel'
            }).when('/verksamhet/:verksamhetId/diagnoskapitel', {
                redirectTo : '/verksamhet/:verksamhetId/diagnoskapitel/A00-B99'
            }).when('/verksamhet/:verksamhetId/aldersgrupper', {
                templateUrl : 'views/detailsView.html',
                controller : 'AgeGroupsCtrl',
                controllerAs : 'VerksamhetAgeGroupCtrl',
                resolve : { config: app.nationalAgeGroupConfig }, 
                title: 'Åldersgrupper'
            }).when('/verksamhet/:verksamhetId/aldersgrupperpagaende', {
                templateUrl : 'views/detailsView.html',
                controller : 'AgeGroupsCtrl',
                controllerAs : 'VerksamhetAgeGroupCurrentCtrl',
                resolve : { config: app.nationalAgeGroupCurrentConfig }, 
                title: 'Åldersgrupper'
            }).when('/verksamhet/:verksamhetId/sjukskrivningsgrad', {
                templateUrl : 'views/detailsView.html',
                controller : 'DegreeOfSickLeaveCtrl',
                controllerAs : 'VerksamhetDegreeOfSickLeaveCtrl',
                resolve : { config: app.degreeOfSickLeaveConfig },
                title: 'Sjukskrivningsgrad'
            }).when('/verksamhet/:verksamhetId/sjukskrivningslangd', {
                templateUrl : 'views/detailsView.html',
                controller : 'SickLeaveLengthCtrl',
                controllerAs : 'VerksamhetSickLeaveLengthCtrl',
                resolve : { config: app.nationalSickLeaveLengthConfig }, 
                title: 'Sjukskrivningslängd'
            }).when('/verksamhet/:verksamhetId/sjukskrivningslangdpagaende', {
                templateUrl : 'views/detailsView.html',
                controller : 'SickLeaveLengthCtrl',
                controllerAs : 'VerksamhetSickLeaveLengthCurrentCtrl',
                resolve : { config: app.nationalSickLeaveLengthCurrentConfig }, 
                title: 'Sjukskrivningslängd'
            }).when('/verksamhet/:verksamhetId/langasjukskrivningar', {
                templateUrl : 'views/detailsView.html',
                controller : 'LongSickLeavesCtrl',
                controllerAs : 'VerksamhetLongSickLeavesCtrl',
                resolve : { config: app.longSickLeavesConfig }, 
                title: 'Sjukskrivningslängd mer än 90 dagar'
            }).when('/om/tjansten', {
                templateUrl : 'views/about/about.html',
                controllerAs : 'AboutServiceCtrl',
                title: 'Om tjänsten'
            }).when('/om/kontakt', {
                templateUrl : 'views/about/contact.html',
                controllerAs : 'AboutContactCtrl',
                title: 'Kontakt till support'
            }).when('/om/vanligafragor', {
                templateUrl : 'views/about/faq.html',
                controllerAs : 'AboutFaqCtrl',
                title: 'Vanliga frågor och svar'
            }).when('/om/inloggning', {
                templateUrl : 'views/about/login.html',
                controllerAs : 'AboutLoginCtrl',
                title: 'Inloggning och behörighet'
            }).when('/', {
                redirectTo : '/nationell/oversikt'
            }).otherwise({
                templateUrl : 'views/error/pageNotFound.html',
                title: 'Fel'
            });

        } ]);

app.statisticsApp.run([ '$rootScope', '$route', function($rootScope, $route) {
    $rootScope.lang = 'sv';
    $rootScope.DEFAULT_LANG = 'sv';
    
    Highcharts.setOptions({
        lang: { thousandsSep: ' ' }
    });
    
	// Update page title
	$rootScope.page_title = 'Titel';
    $rootScope.$on('$routeChangeSuccess', function() {
	  if ($route.current.$$route){
		  $rootScope.page_title = $route.current.$$route.title + ' | Nationella statistiktjänsten';
	  }
    });
} ]);