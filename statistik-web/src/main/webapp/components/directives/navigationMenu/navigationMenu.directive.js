angular.module('StatisticsApp').directive('navigationMenu',
    [function() {
        'use strict';

        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'components/directives/navigationMenu/navigationMenu.html',
            controller: 'navigationMenuCtrl'
        };
    }]);