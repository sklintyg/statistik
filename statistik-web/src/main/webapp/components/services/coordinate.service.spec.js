/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

describe('Service: coordinate', function () {
    'use strict';

    var CoordinateService;

    beforeEach(module('StatisticsApp'));

    beforeEach(inject(function(_CoordinateService_) {
        CoordinateService = _CoordinateService_;
    }));

    it('getCoordinates', function () {
        expect(CoordinateService.getCoordinates({name: 'blekinge'})).toEqual({'x': 35, 'y': 14});
        expect(CoordinateService.getCoordinates({name: 'dalarna'})).toEqual({'x': 29, 'y': 49});
        expect(CoordinateService.getCoordinates({name: 'halland'})).toEqual({'x': 15, 'y': 19});
        expect(CoordinateService.getCoordinates({name: 'kalmar'})).toEqual({'x': 40, 'y': 20});
        expect(CoordinateService.getCoordinates({name: 'kronoberg'})).toEqual({'x': 29, 'y': 19});
        expect(CoordinateService.getCoordinates({name: 'gotland'})).toEqual({'x': 55, 'y': 22});
        expect(CoordinateService.getCoordinates({name: 'gävleborg'})).toEqual({'x': 45, 'y': 50});
        expect(CoordinateService.getCoordinates({name: 'jämtland'})).toEqual({'x': 29, 'y': 62});
        expect(CoordinateService.getCoordinates({name: 'jönköping'})).toEqual({'x': 28, 'y': 24});
        expect(CoordinateService.getCoordinates({name: 'norrbotten'})).toEqual({'x': 59, 'y': 87});
        expect(CoordinateService.getCoordinates({name: 'skåne'})).toEqual({'x': 21, 'y': 11});
        expect(CoordinateService.getCoordinates({name: 'stockholm'})).toEqual({'x': 52, 'y': 37});
        expect(CoordinateService.getCoordinates({name: 'södermanland'})).toEqual({'x': 44, 'y': 33});
        expect(CoordinateService.getCoordinates({name: 'uppsala'})).toEqual({'x': 50, 'y': 42});
        expect(CoordinateService.getCoordinates({name: 'värmland'})).toEqual({'x': 18, 'y': 38});
        expect(CoordinateService.getCoordinates({name: 'västerbotten'})).toEqual({'x': 50, 'y': 72});
        expect(CoordinateService.getCoordinates({name: 'västernorrland'})).toEqual({'x': 48, 'y': 62});
        expect(CoordinateService.getCoordinates({name: 'västmanland'})).toEqual({'x': 42, 'y': 40});
        expect(CoordinateService.getCoordinates({name: 'västra götaland'})).toEqual({'x': 12, 'y': 25});
        expect(CoordinateService.getCoordinates({name: 'örebro'})).toEqual({'x': 32, 'y': 36});
        expect(CoordinateService.getCoordinates({name: 'östergötland'})).toEqual({'x': 39, 'y': 29});
        expect(CoordinateService.getCoordinates({name: 'no match'})).toEqual(null);
    });

});
