/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

describe('Test of common print services', function() {
    'use strict';

    beforeEach(module('StatisticsApp'));

    var chartFactory;
    var route;
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
        '#704F38',
        '#600030',
        '#006697'];

    beforeEach(inject(function(_chartFactory_, $route, ___) {
        chartFactory = _chartFactory_;
        route = $route;
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

        it('will start over from the beginning when more than 12 colors are needed', function () {
            //given
            //Create a demand for mor than 10 colors
            var listOfSeries = _.map(_.range(1, 15), function (item) {
                return {id: item};
            });

            //when
            chartFactory.addColor(listOfSeries);

            //then
            expect(listOfSeries[12].color).toBe(nonFemaleOrMaleColors[0]);
            expect(listOfSeries[13].color).toBe(nonFemaleOrMaleColors[1]);
        });

        it('can add 2 different male colors', function () {
            //given
            var list = [{id: 1, sex: 'MALE'}, {id: 2, sex: 'MALE'}];

            //when
            chartFactory.addColor(list);

            //then
            expect(list[0].color).toBe('#008391');
            expect(list[1].color).toBe('#90cad0');
        });

        it('can add 2 different female colors', function () {
            //given
            var list = [{id: 1, sex: 'FEMALE'}, {id: 2, sex: 'FEMALE'}];

            //when
            chartFactory.addColor(list);

            //then
            expect(list[0].color).toBe('#EA8025');
            expect(list[1].color).toBe('#f6c08d');
        });

        it('set grey color on none sex series', function () {
            //given
            var list = [{id: 1, name: 'Män', sex: 'MALE'},
                {id: 2, name: 'Kvinnor', sex: 'FEMALE'},
                {id: 3, name: 'Totalt', sex: null}];

            //when
            chartFactory.addColor(list);

            //then
            expect(list[0].color).toBe('#008391');
            expect(list[1].color).toBe('#EA8025');
            expect(list[2].color).toBe('#E11964');

            //when
            chartFactory.setColorToTotalCasesSeries(list);

            //then
            expect(list[2].color).toBe('#5D5D5D');
        });

        it('use color on data', function () {
            //given
            var list = [{id: 1, sex: 'MALE', color: '#123123'}, {id: 2, sex: 'FEMALE', color: '#234234'}, {id: 2, sex: null, color: '#567567'}];

            //when
            chartFactory.addColor(list);

            //then
            expect(list[0].color).toBe('#123123');
            expect(list[1].color).toBe('#234234');
            expect(list[2].color).toBe('#567567');
        });

    });

    describe('Generating filename for chart image export', function () {

        it('getFileName', function() {
            route.current = {title: 'My Report title'};
            expect(chartFactory.getChartExportFileName('')).toMatch(/_MyReporttitle_\d{6}_\d{6}/);

            route.current = {title: 'My Report title'};
            expect(chartFactory.getChartExportFileName(123456)).toMatch(/^123456_MyReporttitle_\d{6}_\d{6}$/);

            route.current = {title: 'My Report title'};
            expect(chartFactory.getChartExportFileName('123456')).toMatch(/^123456_MyReporttitle_\d{6}_\d{6}$/);

            route.current = {title: 'My Report title'};
            expect(chartFactory.getChartExportFileName('Verksamhetsnivå')).toMatch(/^Verksamhetsniva_MyReporttitle_\d{6}_\d{6}$/);

            route.current = {title: 'My Report title'};
            expect(chartFactory.getChartExportFileName('Nationell nivå')).toMatch(/^Nationellniva_MyReporttitle_\d{6}_\d{6}$/);

            route.current = {title: 'My Other Report title'};
            expect(chartFactory.getChartExportFileName('Landstingz   Nivå')).toMatch(/^LandstingzNiva_MyOtherReporttitle_\d{6}_\d{6}$/);

            route.current = {title: 'Title with numbers like 123'};
            expect(chartFactory.getChartExportFileName('Nationell', 'Män')).toMatch(/^Nationell_Titlewithnumberslike123_Man_\d{6}_\d{6}$/);
        });

    });


    });
