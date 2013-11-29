describe("Tests for business overview controller", function() {

    it("should forward to missing data page if totalt number of cases is 0", function() {
        var $scope = {};
        var $timeout = {};
        var statisticsData = {getBusinessOverview: function(id, cb, err){cb({casesPerMonth: {totalCases: 0}});}};
        var $routeParams = {};
        var $window = {location: {href: "https://www.statistik.com/verksamhet/verksamhet1/oversikt"}};
        app.businessOverviewCtrl($scope, $timeout, statisticsData, $routeParams, $window);
        expect($window.location.href).toBe("https://www.statistik.com/verksamhet/verksamhet1/nodata");
    });

    it("should not forward to missing data page if totalt number of cases is more than 0", function() {
        var $scope = {};
        var $timeout = function(){};
        var statisticsData = {getBusinessOverview: function(id, cb, err){cb({casesPerMonth: {totalCases: 1}});}};
        var $routeParams = {};
        var $window = {location: {href: "https://www.statistik.com/verksamhet/verksamhet1/oversikt"}};
        app.businessOverviewCtrl($scope, $timeout, statisticsData, $routeParams, $window);
        expect($window.location.href).toBe("https://www.statistik.com/verksamhet/verksamhet1/oversikt");
    });
    
    
});