'use strict';

/* App Module */

var statisticsApp = angular.module('StatisticsApp', [  ]).config(
        [ '$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
            $routeProvider.when('/oversikt', {
                templateUrl : '/views/overview.html',
                controller : 'OverviewCtrl',
	            title: 'Översikt'
            }).when('/sjukfallPerManad', {
                templateUrl : '/views/chart.html',
                controller : 'CasesPerMonthCtrl',
			    title: 'Sjukfall per månad'
            }).when('/om', {
                templateUrl : '/views/about.html',
                title: 'Om tjänsten'
            }).otherwise({
                redirectTo : '/oversikt'
            });

        } ]);

statisticsApp.run([ '$rootScope', '$route', function($rootScope, $route) {
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

