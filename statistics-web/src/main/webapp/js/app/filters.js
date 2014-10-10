'use strict';

angular.module('StatisticsApp').filter('thousandseparated', function() {
    return function(input) {
        return ControllerCommons.makeThousandSeparated(input);
    };
});

