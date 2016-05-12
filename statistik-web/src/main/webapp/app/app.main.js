var underscore = angular.module('underscore', []);
underscore.factory('_', function($window) {
    'use strict';
    return $window._;
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
     'dropzone',
     'ngStorage']);

app.run(['AppService', '$rootScope', function (AppService, $rootScope) {
    'use strict';

    $rootScope.isLoggedIn = false;

    AppService.get().then(function(data) {
        $rootScope.isLoggedIn = data.loggedIn;
    });
} ]);
