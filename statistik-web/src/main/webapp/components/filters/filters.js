angular.module('StatisticsApp').filter('thousandseparated', function() {
    'use strict';

    return function(input) {
        return ControllerCommons.makeThousandSeparated(input);
    };
});

