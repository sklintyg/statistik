/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

describe('Controller: overviewCtrl', function () {
    'use strict';

    var statisticsData, scope, $window, $timeout, $rootScope, $routeParams, ctrl;

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
            $routeParams: $routeParams
        });
        scope.$digest();
    }));


    it('getCoordinates', function () {
        expect(ctrl.getCoordinates({name: 'blekinge'})).toEqual({'x': 35, 'y': 14});
        expect(ctrl.getCoordinates({name: 'dalarna'})).toEqual({'x': 29, 'y': 49});
        expect(ctrl.getCoordinates({name: 'halland'})).toEqual({'x': 15, 'y': 19});
        expect(ctrl.getCoordinates({name: 'kalmar'})).toEqual({'x': 40, 'y': 20});
        expect(ctrl.getCoordinates({name: 'kronoberg'})).toEqual({'x': 29, 'y': 19});
        expect(ctrl.getCoordinates({name: 'gotland'})).toEqual({'x': 55, 'y': 22});
        expect(ctrl.getCoordinates({name: 'gävleborg'})).toEqual({'x': 45, 'y': 50});
        expect(ctrl.getCoordinates({name: 'jämtland'})).toEqual({'x': 29, 'y': 62});
        expect(ctrl.getCoordinates({name: 'jönköping'})).toEqual({'x': 28, 'y': 24});
        expect(ctrl.getCoordinates({name: 'norrbotten'})).toEqual({'x': 59, 'y': 87});
        expect(ctrl.getCoordinates({name: 'skåne'})).toEqual({'x': 21, 'y': 11});
        expect(ctrl.getCoordinates({name: 'stockholm'})).toEqual({'x': 52, 'y': 37});
        expect(ctrl.getCoordinates({name: 'södermanland'})).toEqual({'x': 44, 'y': 33});
        expect(ctrl.getCoordinates({name: 'uppsala'})).toEqual({'x': 50, 'y': 42});
        expect(ctrl.getCoordinates({name: 'värmland'})).toEqual({'x': 18, 'y': 38});
        expect(ctrl.getCoordinates({name: 'västerbotten'})).toEqual({'x': 50, 'y': 72});
        expect(ctrl.getCoordinates({name: 'västernorrland'})).toEqual({'x': 48, 'y': 62});
        expect(ctrl.getCoordinates({name: 'västmanland'})).toEqual({'x': 42, 'y': 40});
        expect(ctrl.getCoordinates({name: 'västra götaland'})).toEqual({'x': 12, 'y': 25});
        expect(ctrl.getCoordinates({name: 'örebro'})).toEqual({'x': 32, 'y': 36});
        expect(ctrl.getCoordinates({name: 'östergötland'})).toEqual({'x': 39, 'y': 29});
        expect(ctrl.getCoordinates({name: 'no match'})).toEqual({'x': 12, 'y': 94});
    });

});
