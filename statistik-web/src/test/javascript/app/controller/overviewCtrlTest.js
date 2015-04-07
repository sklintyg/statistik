describe('Controller: overviewCtrl', function () {

    var statisticsData, scope, $window, $timeout, $rootScope, $routeParams, ctrl;

    isLoggedIn = false;

    beforeEach(function () {

        var mockDependency = {};

        angular.mock.module('StatisticsApp', function ($provide) {
            $provide.value('statisticsData', mockDependency);
        });

        mockDependency.getOverview = function () {
            return 'mockReturnValue';
        };
    });

    beforeEach(inject(function(_$rootScope_, $controller, _statisticsData_, _$window_, _$timeout_, _$routeParams_) {
        scope = _$rootScope_.$new();
        $rootScope = _$rootScope_;
        $window = _$window_;
        $timeout = _$timeout_;
        $routeParams = _$routeParams_;
        statisticsData = _statisticsData_;

        ctrl = $controller('overviewCtrl', {
            $scope: scope,
            $rootScope: $rootScope,
            $window: $window,
            $timeout: $timeout,
            statisticsData: statisticsData,
            $routeParams: $routeParams,
            printFactory: {}
        });
        scope.$digest();
    }));


    it('getCoordinates', function () {
        expect(ctrl.getCoordinates({name: 'blekinge'})).toEqual({'x': 35, 'y': 15});
        expect(ctrl.getCoordinates({name: 'dalarna'})).toEqual({'x': 31, 'y': 50});
        expect(ctrl.getCoordinates({name: 'halland'})).toEqual({'x': 14, 'y': 20});
        expect(ctrl.getCoordinates({name: 'kalmar'})).toEqual({'x': 40, 'y': 20});
        expect(ctrl.getCoordinates({name: 'kronoberg'})).toEqual({'x': 32, 'y': 19});
        expect(ctrl.getCoordinates({name: 'gotland'})).toEqual({'x': 55, 'y': 22});
        expect(ctrl.getCoordinates({name: 'gävleborg'})).toEqual({'x': 45, 'y': 50});
        expect(ctrl.getCoordinates({name: 'jämtland'})).toEqual({'x': 29, 'y': 66});
        expect(ctrl.getCoordinates({name: 'jönköping'})).toEqual({'x': 28, 'y': 24});
        expect(ctrl.getCoordinates({name: 'norrbotten'})).toEqual({'x': 59, 'y': 94});
        expect(ctrl.getCoordinates({name: 'skåne'})).toEqual({'x': 21, 'y': 11});
        expect(ctrl.getCoordinates({name: 'stockholm'})).toEqual({'x': 52, 'y': 37});
        expect(ctrl.getCoordinates({name: 'södermanland'})).toEqual({'x': 44, 'y': 34});
        expect(ctrl.getCoordinates({name: 'uppsala'})).toEqual({'x': 50, 'y': 42});
        expect(ctrl.getCoordinates({name: 'värmland'})).toEqual({'x': 21, 'y': 42});
        expect(ctrl.getCoordinates({name: 'västerbotten'})).toEqual({'x': 51, 'y': 80});
        expect(ctrl.getCoordinates({name: 'västernorrland'})).toEqual({'x': 48, 'y': 67});
        expect(ctrl.getCoordinates({name: 'västmanland'})).toEqual({'x': 42, 'y': 42});
        expect(ctrl.getCoordinates({name: 'västra götaland'})).toEqual({'x': 12, 'y': 32});
        expect(ctrl.getCoordinates({name: 'örebro'})).toEqual({'x': 32, 'y': 38});
        expect(ctrl.getCoordinates({name: 'östergötland'})).toEqual({'x': 40, 'y': 30});
        expect(ctrl.getCoordinates({name: 'no match'})).toEqual({'x': 12, 'y': 94});
    });

});
