'use strict';

app.statisticsApp.filter('thousandseparated', function() {
    return function(input) {
        return ControllerCommons.makeThousandSeparated(input);
    };
});
