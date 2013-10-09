'use strict';

/* App Module */
var app = {};

app.statisticsApp = angular.module('StatisticsApp', [  ]).config(
        [ '$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
            $routeProvider.when('/oversikt', {
                templateUrl : 'views/overview.html',
                controller : 'OverviewCtrl',
	            title: 'Översikt'
            }).when('/verksamhet/oversikt', {
                    templateUrl : 'views/verksamhet/verksamhet.html',
                    controller : 'VOverviewCtrl',
                    title: 'Verksamhetsöversikt'
            }).when('/sjukfallPerManad', {
                    templateUrl : 'views/chart.html',
                    controller : 'CasesPerMonthCtrl',
                    title: 'Sjukfall per månad'
            }).when('/aldersgrupper', {
                templateUrl : 'views/chart.html',
                controller : 'AgeGroupsCtrl',
                title: 'Sjukfall per månad'
            }).when('/diagnosgrupper', {
                templateUrl : 'views/chart.html',
                controller : 'DiagnosisGroupsCtrl',
                resolve : {dataFetcher: function(){ return "getDiagnosisGroupData"; }, showDetailsOptions: function(){ return false; } }, 
                title: 'Diagnosgrupper'
            }).when('/underdiagnosgrupper/:groupId', {
                templateUrl : 'views/chart.html',
                controller : 'DiagnosisGroupsCtrl',
                resolve : {dataFetcher: function(){ return "getSubDiagnosisGroupData"; }, showDetailsOptions: function(){ return true; } },
                title: 'Underdiagnosgrupper'
            }).when('/underdiagnosgrupper', {
                redirectTo : '/underdiagnosgrupper/A00-B99'
            }).when('/om/tjansten', {
                templateUrl : 'views/about/about.html',
                title: 'Om tjänsten'
            }).when('/om/kontakt', {
                templateUrl : 'views/about/contact.html',
                title: 'Kontakt till support'
            }).when('/om/vanligafragor', {
                templateUrl : 'views/about/faq.html',
                title: 'Vanliga frågor och svar'
            }).when('/om/inloggning', {
                templateUrl : 'views/about/login.html',
                title: 'Inloggning och behörighet'
            }).when('/', {
                redirectTo : '/oversikt'
            }).otherwise({
                templateUrl : 'views/error/pageNotFound.html',
                title: 'Fel'
            });

        } ]);

app.statisticsApp.run([ '$rootScope', '$route', function($rootScope, $route) {
    $rootScope.lang = 'sv';
    $rootScope.DEFAULT_LANG = 'sv';

	// Update page title
	$rootScope.page_title = 'Titel';
    $rootScope.$on('$routeChangeSuccess', function() {
	  if ($route.current.$$route){
		  $rootScope.page_title = $route.current.$$route.title + ' | Statistiktjänsten';
	  }
    });
} ]);
