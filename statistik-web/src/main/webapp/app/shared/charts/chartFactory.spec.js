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

describe('Test of common print services', function() {
    'use strict';

    beforeEach(module('StatisticsApp'));

    var chartFactory;
    var _;
    var nonFemaleOrMaleColors = [
        '#E11964',
        '#032C53',
        '#FFBA3E',
        '#799745',
        '#3CA3FF',
        '#C37EB2',
        '#2A5152',
        '#FB7F4D',
        '#5CC2BC',
        '#704F38'];

    beforeEach(inject(function(_chartFactory_, ___) {
        chartFactory = _chartFactory_;
        _ = ___;
    }));

    describe('Adding color to series', function () {
        it('can add 10 different colors for non female/male cases', function () {
            //given
            var listOfSeries = _.map(_.range(1, 11), function (item) {
                return {id: item};
            });

            //when
            chartFactory.addColor(listOfSeries);

            //then
            expect(listOfSeries[0].color).toBe(nonFemaleOrMaleColors[0]);
            expect(listOfSeries[1].color).toBe(nonFemaleOrMaleColors[1]);
            expect(listOfSeries[2].color).toBe(nonFemaleOrMaleColors[2]);
            expect(listOfSeries[3].color).toBe(nonFemaleOrMaleColors[3]);
            expect(listOfSeries[4].color).toBe(nonFemaleOrMaleColors[4]);
            expect(listOfSeries[5].color).toBe(nonFemaleOrMaleColors[5]);
            expect(listOfSeries[6].color).toBe(nonFemaleOrMaleColors[6]);
            expect(listOfSeries[7].color).toBe(nonFemaleOrMaleColors[7]);
            expect(listOfSeries[8].color).toBe(nonFemaleOrMaleColors[8]);
            expect(listOfSeries[9].color).toBe(nonFemaleOrMaleColors[9]);
        });

        it('will start over from the beginning when more than 10 colors are needed', function () {
            //given
            //Create a demand for mor than 10 colors
            var listOfSeries = _.map(_.range(1, 13), function (item) {
                return {id: item};
            });

            //when
            chartFactory.addColor(listOfSeries);

            //then
            expect(listOfSeries[10].color).toBe(nonFemaleOrMaleColors[0]);
            expect(listOfSeries[11].color).toBe(nonFemaleOrMaleColors[1]);
        });

        it('can add 2 different male colors', function () {
            //given
            var list = [{id: 1, sex: 'Male'}, {id: 2, sex: 'Male'}];

            //when
            chartFactory.addColor(list);

            //then
            expect(list[0].color).toBe('#008391');
            expect(list[1].color).toBe('#90cad0');
        });

        it('can add 2 different female colors', function () {
            //given
            var list = [{id: 1, sex: 'Female'}, {id: 2, sex: 'Female'}];

            //when
            chartFactory.addColor(list);

            //then
            expect(list[0].color).toBe('#EA8025');
            expect(list[1].color).toBe('#f6c08d');
        });
    });

});
