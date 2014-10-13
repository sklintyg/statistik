/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

describe("Tests for business overview controller", function () {
    beforeEach(module('StatisticsApp'));

    var ctrl, scope;
    var businessFilter = { getSelectedBusinesses: function () {
    }};

    beforeEach(inject(function($rootScope, $controller, $window) {
        var scope = $rootScope.$new();
        ctrl = $controller('pageCtrl', {$scope: scope, $rootScope: $rootScope, $window: $window, $cookies: {}, statisticsData: {}, businessFilter: {}});
    }));

    it("should forward to missing data page if totalt number of cases is 0", function () {
        var statisticsData = { getBusinessOverview: function (id, pr, callback, err) {
            callback({ casesPerMonth: { totalCases: 0 } });
        }};
        var $window = { location: { href: "https://www.statistik.com/verksamhet/verksamhet1/oversikt" } };
        inject(function ($rootScope, $controller, $timeout, $routeParams) {
            scope = $rootScope.$new();
            ctrl = $controller('businessOverviewCtrl', {$scope: scope, $timeout: $timeout, statisticsData: statisticsData,
                businessFilter: businessFilter, $routeParams: $routeParams, $window: $window });
        });
        expect($window.location.href).toBe("https://www.statistik.com/verksamhet/verksamhet1/nodata");
    });

    it("should not forward to missing data page if totalt number of cases is more than 0", function () {
        var statisticsData = { getBusinessOverview: function (id, pr, callback, err) {
            callback({ casesPerMonth: { totalCases: 1 } });
        }};
        var $window = { location: { href: "https://www.statistik.com/verksamhet/verksamhet1/oversikt" } };
        inject(function ($rootScope, $controller, $timeout, $routeParams) {
            scope = $rootScope.$new();
            ctrl = $controller('businessOverviewCtrl', {$scope: scope, $timeout: $timeout, statisticsData: statisticsData,
                businessFilter: businessFilter, $routeParams: $routeParams, $window: $window });
        });
        expect($window.location.href).toBe("https://www.statistik.com/verksamhet/verksamhet1/oversikt");
    });

});
