var underscore = angular.module('underscore', []);
underscore.factory('_', function($window) {
    'use strict';
    return $window._;
});

/* App Module */
angular.module('StatisticsApp',
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