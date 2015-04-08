angular.module('StatisticsApp.chartSeriesButtonGroup.directive', [])
    .directive('chartSeriesBg', [function() {
        'use strict';
        return {
            scope: {
                views: '=' //Expects an array of objects [{description: 'Tidsserie', state: '#/verksamhet/someKindOfState', active: true/false}]
            },
            replace: true,
            restrict: 'E',
            templateUrl: 'js/app/shared/chartseriesbuttongroup/chartSeriesButtonGroupView.html'
        };
    }]);