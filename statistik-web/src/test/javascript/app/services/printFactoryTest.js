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

describe("Test of common print services", function() {
    "use strict";

    beforeEach(module('StatisticsApp'));

    var printFactory;

    beforeEach(inject(function(_printFactory_) {
        printFactory = _printFactory_;
    }));

    describe("Add color to series", function () {
        it("add non female/male colors", function () {
            var nonFemaleOrMaleColors = ["#E40E62", "#00AEEF", "#57843B", "#002B54", "#F9B02D", "#724E86", "#34655e", "#8A6B61"];
            var listOfSeries = [];
            _.each(_.range(1, 11), function (item) {
                listOfSeries.push({id: item});
            });
            printFactory.addColor(listOfSeries);
            expect(listOfSeries[0].color).toBe(nonFemaleOrMaleColors[0]);
            expect(listOfSeries[1].color).toBe(nonFemaleOrMaleColors[1]);
            expect(listOfSeries[2].color).toBe(nonFemaleOrMaleColors[2]);
            expect(listOfSeries[3].color).toBe(nonFemaleOrMaleColors[3]);
            expect(listOfSeries[4].color).toBe(nonFemaleOrMaleColors[4]);
            expect(listOfSeries[5].color).toBe(nonFemaleOrMaleColors[5]);
            expect(listOfSeries[6].color).toBe(nonFemaleOrMaleColors[6]);
            expect(listOfSeries[7].color).toBe(nonFemaleOrMaleColors[7]);
        });

        it("add male colors", function () {
            var list = [{id: 1, sex: 'Male'}, {id: 2, sex: 'Male'}];
            printFactory.addColor(list);
            expect(list[0].color).toBe("#008391");
            expect(list[1].color).toBe("#90cad0");
        });

        it("add female colors", function () {
            var list = [{id: 1, sex: 'Female'}, {id: 2, sex: 'Female'}];
            printFactory.addColor(list);
            expect(list[0].color).toBe("#EA8025");
            expect(list[1].color).toBe("#f6c08d");
        });

        it("black and white print === true should use color objects with bw patterns", function () {
            var list = [{id: 1}, {id: 2}, {id: 3, sex: 'Male'}, {id: 4, sex: 'Male'}, {id: 5, sex: 'Female'}, {
                id: 6,
                sex: 'Female'
            }];
            printFactory.addColor(list, true);
            expect(list[0].color.pattern).toMatch(/.*1\.png/);
            expect(list[1].color.pattern).toMatch(/.*2\.png/);
            expect(list[2].color.pattern).toMatch(/.*5\.png/); //Male bw pattern 1
            expect(list[3].color.pattern).toMatch(/.*6\.png/); //Male bw pattern 2
            expect(list[4].color.pattern).toMatch(/.*7\.png/); //Female bw pattern 1
            expect(list[5].color.pattern).toMatch(/.*8\.png/); //Female bw pattern 2
        });

    });

    describe("Setup series for display type", function () {
        it("black color will be returned when bw argument is true", function () {
            var result = printFactory.setupSeriesForDisplayType(true, [{}], "line");
            expect(result[0].color).toBe("black");
        });

        it("10 black patterns will be set when the bw argument is true and chart type not line chart ", function () {
            var listOfSeries = [];
            _.each(_.range(1, 11), function (item) {
                listOfSeries.push({id: item});
            });

            printFactory.setupSeriesForDisplayType(true, listOfSeries, 'bar');

            expect(listOfSeries[0].color.pattern).toMatch(/.*1\.png/);
            expect(listOfSeries[1].color.pattern).toMatch(/.*2\.png/);
            expect(listOfSeries[2].color.pattern).toMatch(/.*3\.png/);
            expect(listOfSeries[3].color.pattern).toMatch(/.*4\.png/);
            expect(listOfSeries[4].color.pattern).toMatch(/.*5\.png/);
            expect(listOfSeries[5].color.pattern).toMatch(/.*6\.png/);
            expect(listOfSeries[6].color.pattern).toMatch(/.*7\.png/);
            expect(listOfSeries[7].color.pattern).toMatch(/.*8\.png/);
            expect(listOfSeries[8].color.pattern).toMatch(/.*9\.png/);
            expect(listOfSeries[9].color.pattern).toMatch(/.*10\.png/);
        });

        it("10 dash styles patterns will be set when the bw argument is true and chart type is line", function () {
            var dashStyles = [ 'shortdashdotdot', 'dashdot', 'dot', 'longdash', 'shortdot', 'solid', 'shortdash', 'shortdashdot', 'dash', 'longdashdot', 'longdashdotdot' ];
            var listOfSeries = [];
            _.each(_.range(1, 12), function (item) {
                listOfSeries.push({id: item});
            });

            printFactory.setupSeriesForDisplayType(true, listOfSeries, 'line');

            expect(listOfSeries[0].dashStyle).toMatch(dashStyles[0]);
            expect(listOfSeries[1].dashStyle).toMatch(dashStyles[1]);
            expect(listOfSeries[2].dashStyle).toMatch(dashStyles[2]);
            expect(listOfSeries[3].dashStyle).toMatch(dashStyles[3]);
            expect(listOfSeries[4].dashStyle).toMatch(dashStyles[4]);
            expect(listOfSeries[5].dashStyle).toMatch(dashStyles[5]);
            expect(listOfSeries[6].dashStyle).toMatch(dashStyles[6]);
            expect(listOfSeries[7].dashStyle).toMatch(dashStyles[7]);
            expect(listOfSeries[8].dashStyle).toMatch(dashStyles[8]);
            expect(listOfSeries[9].dashStyle).toMatch(dashStyles[9]);
            expect(listOfSeries[10].dashStyle).toMatch(dashStyles[10]);
        });

        it("color property will have the right colors when bw argument is false", function () {
            var result = printFactory.setupSeriesForDisplayType(false, [{}], "line");
            expect(result[0].color).toBe("#E40E62");
        });

    });

});
