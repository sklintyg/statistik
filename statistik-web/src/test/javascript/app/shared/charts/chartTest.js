describe("Chart services", function() {
    var chartFactory;
    beforeEach(module('StatisticsApp.charts'));

    beforeEach(inject(function(_chartFactory_, ___) {
        chartFactory = _chartFactory_;
        _ = ___; //This set the local underscore variable
    }));


    describe("General configuration of charts", function() {
        it("can setup a basic configuration for a chart", function() {
            highchartsExportUrl = "http://www.testurl.com:1234/exporttest";
            var categories = ["Namn 1", "Namn < 1"];
            var series = [{b: 4, data: []}];
            var result = chartFactory.getHighChartConfigBase(categories, series);
            expect(result.xAxis.categories.length).toBe(2);
            expect(result.xAxis.categories[0]).toBe("Namn 1");
            expect(result.xAxis.categories[1]).toBe("Namn &lt; 1");
            expect(result.series.length).toBe(1);
            expect(result.series[0].b).toBe(4);
        });

        it("will enable markers on specific series if there is only one data point for the series", function() {
            highchartsExportUrl = "http://www.testurl.com:1234/exporttest";
            var categories = ["Namn 1", "Namn < 1"];
            var seriesWithOneDataPoint = [{b: 4, data: [1]}];
            var result = chartFactory.getHighChartConfigBase(categories, seriesWithOneDataPoint);

            expect(result.series[0].marker.enabled).toBeTruthy();
        });
    });

    describe("Export charts", function () {
        it("can export a chart", function () {
            var callTimes = 0;
            var name = "testName";
            var chart = {
                exportChart: function (opt, chartOpt) {
                    callTimes++;
                    expect(opt.filename).toMatch(/testName/);
                    expect(chartOpt.legend.enabled).toBe(true);
                    expect(chartOpt.legend.layout).toBe("MyLayout");
                    expect(chartOpt.title.text).toBe("title text");
                }
            };
            chartFactory.exportChart(chart, name, "title text", null, "MyLayout");
            expect(callTimes).toBe(1);
        });
        //code goes here
    });
});