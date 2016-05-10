angular.module('StatisticsApp').filter('thousandseparated', ['ControllerCommons',
    function(ControllerCommons) {
        'use strict';

        return function(input) {
            return ControllerCommons.makeThousandSeparated(input);
        };
}]);

