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

describe("Tests for business overview controller", function() {

    it("should forward to missing data page if totalt number of cases is 0", function() {
        var $scope = {};
        $scope.$on = function (eventName, func) {};
        var $timeout = {};
        var statisticsData = {getBusinessOverview: function(id, pr, cb, err){cb({casesPerMonth: {totalCases: 0}});}};
        var businessFilter = { selectedBusinesses: [] };
        var $routeParams = {};
        var $window = {location: {href: "https://www.statistik.com/verksamhet/verksamhet1/oversikt"}};
        app.businessOverviewCtrl($scope, $timeout, statisticsData, businessFilter, $routeParams, $window);
        expect($window.location.href).toBe("https://www.statistik.com/verksamhet/verksamhet1/nodata");
    });

    it("should not forward to missing data page if totalt number of cases is more than 0", function() {
        var $scope = {};
        $scope.$on = function (eventName, func) {};
        var $timeout = function(){};
        var statisticsData = {getBusinessOverview: function(id, pr, cb, err){cb({casesPerMonth: {totalCases: 1}});}};
        var businessFilter = { selectedBusinesses: [] };
        var $routeParams = {};
        var $window = {location: {href: "https://www.statistik.com/verksamhet/verksamhet1/oversikt"}};
        app.businessOverviewCtrl($scope, $timeout, statisticsData, businessFilter, $routeParams, $window);
        expect($window.location.href).toBe("https://www.statistik.com/verksamhet/verksamhet1/oversikt");
    });

});
