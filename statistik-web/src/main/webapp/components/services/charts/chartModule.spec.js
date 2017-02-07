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

describe('Chart services', function() {
    'use strict';

    var chartFactory;
    var _;
    var AppModel;

    beforeEach(module('StatisticsApp'));

    beforeEach(inject(function(_chartFactory_, ___, _AppModel_) {
        chartFactory = _chartFactory_;
        AppModel = _AppModel_;
        _ = ___; //This set the local underscore variable
    }));


    describe('General configuration of charts', function() {
        it('can setup a basic configuration for a chart', function() {
            AppModel.set({
                highchartsExportUrl: 'http://www.testurl.com:1234/exporttest'
            });
            var categories = [{name:'Namn 1'}, {name:'Namn < 1'}];
            var series = [{b: 4, data: []}];
            var result = chartFactory.getHighChartConfigBase(categories, series);
            expect(result.xAxis.categories.length).toBe(2);
            expect(result.xAxis.categories[0]).toBe('Namn 1');
            expect(result.xAxis.categories[1]).toBe('Namn &lt; 1');
            expect(result.series.length).toBe(1);
            expect(result.series[0].b).toBe(4);
        });

        it('will enable markers on specific series if there is only one data point for the series', function() {
            AppModel.set({
                highchartsExportUrl: 'http://www.testurl.com:1234/exporttest'
            });
            var categories = [{name:'Namn 1'}, {name:'Namn < 1'}];
            var seriesWithOneDataPoint = [{b: 4, data: [1]}];
            var result = chartFactory.getHighChartConfigBase(categories, seriesWithOneDataPoint);

            expect(result.series[0].marker.enabled).toBeTruthy();
        });
    });

    describe('Export charts', function () {
        it('INTYG-1829: can export a chart with fewer than 10 series', function () {
            var callTimes = 0;
            var name = 'testName';
            var chart = {
                exportChartLocal: function (opt, chartOpt) {
                    callTimes++;
                    expect(opt.filename).toMatch(/testName/);
                    expect(chartOpt.legend.enabled).toBe(true);
                    expect(chartOpt.title.text).toBe('title text');
                },
                series: ['1', '2'],
                yAxis: [{}],
                options: { chart: {}}
            };
            chartFactory.exportChart(chart, name, 'title text');
            expect(callTimes).toBe(1);
        });

        it('INTYG-1829: can export a chart with more than 10 series', function () {
            var callTimes = 0;
            var name = 'testName';
            var chart = {
                exportChartLocal: function (opt, chartOpt) {
                    callTimes++;
                    expect(opt.filename).toMatch(/testName/);
                    expect(chartOpt.legend).toBeUndefined();
                    expect(chartOpt.title.text).toBe('title text');
                },
                series: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11'],
                options: { chart: {}}
            };
            chartFactory.exportChart(chart, name, 'title text');
            expect(callTimes).toBe(1);
        });
    });

});
