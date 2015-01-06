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

describe("Test of common functions for controllers", function() {

    it("addColor", function() {
        var list = [ { id : 1 }, { id : 2 } ];
        ControllerCommons.addColor(list);
        expect(list[0].color).toBe("#E40E62");
        expect(list[1].color).toBe("#00AEEF");
    });

    it("makeThousandSeparated", function() {
        expect(ControllerCommons.makeThousandSeparated()).toBe();
        expect(ControllerCommons.makeThousandSeparated(0)).toBe("0");
        expect(ControllerCommons.makeThousandSeparated(1000)).toBe("1\u00A0000");
        expect(ControllerCommons.makeThousandSeparated(9999999)).toBe("9\u00A0999\u00A0999");
        expect(ControllerCommons.makeThousandSeparated(-5000)).toBe("-5\u00A0000");
        expect(ControllerCommons.makeThousandSeparated("Fiftysix")).toBe("Fiftysix");
    });
    
    it("getFileName", function() {
        expect(ControllerCommons.getFileName()).toMatch(/_\d{8}_\d{6}/);
        expect(ControllerCommons.getFileName(123456)).toMatch(/^123456_\d{8}_\d{6}$/);
        expect(ControllerCommons.getFileName("123456")).toMatch(/^123456_\d{8}_\d{6}$/);
        expect(ControllerCommons.getFileName("MittDiagram")).toMatch(/^MittDiagram_\d{8}_\d{6}$/);
        expect(ControllerCommons.getFileName("Mitt Diagram")).toMatch(/^Mitt_Diagram_\d{8}_\d{6}$/);
        expect(ControllerCommons.getFileName("Mitt     Diagram")).toMatch(/^Mitt_Diagram_\d{8}_\d{6}$/);
    });
    
    it("getHighChartConfigBase", function() {
        highchartsExportUrl = "http://www.testurl.com:1234/exporttest";
        var categories = ["Namn 1", "Namn < 1"];
        var series = [{b: 4}];
        var result = ControllerCommons.getHighChartConfigBase(categories, series);
        expect(result.xAxis.categories.length).toBe(2);
        expect(result.xAxis.categories[0]).toBe("Namn 1");
        expect(result.xAxis.categories[1]).toBe("Namn &lt; 1");
        expect(result.series.length).toBe(1);
        expect(result.series[0].b).toBe(4);
    });
    
    it("exportChart", function() {
        var callTimes = 0;
        var name = "testName";
        var chart = {
                exportChart: function(opt, chartOpt){
                    callTimes++;
                    expect(opt.filename).toMatch(/testName/);
                    expect(chartOpt.legend.enabled).toBe(true);
                    expect(chartOpt.legend.layout).toBe("MyLayout");
                    expect(chartOpt.title.text).toBe("title text");
                }
        };
        ControllerCommons.exportChart(chart, name, "title text", "MyLayout");
        expect(callTimes).toBe(1);
    });
    
    it("map", function() {
        expect(ControllerCommons.map([], function(e){return e+1})).toEqual([]);
        expect(ControllerCommons.map([1,2], function(e){return e+1})).toEqual([2,3]);
        expect(ControllerCommons.map(["1","2"], function(e){return e+1})).toEqual(["11","21"]);
    });
    
    it("isNumber", function() {
        expect(ControllerCommons.isNumber()).toBe(false);
        expect(ControllerCommons.isNumber(0)).toBe(true);
        expect(ControllerCommons.isNumber(-1)).toBe(true);
        expect(ControllerCommons.isNumber(1000000)).toBe(true);
        expect(ControllerCommons.isNumber(1.0)).toBe(true);
        expect(ControllerCommons.isNumber("1")).toBe(true);
        expect(ControllerCommons.isNumber("ett")).toBe(false);
        expect(ControllerCommons.isNumber("7ett9")).toBe(false);
        expect(ControllerCommons.isNumber({1: 3})).toBe(false);
    });
    
    it("htmlsafe", function() {
        expect(ControllerCommons.htmlsafe("f")).toBe("f");
        expect(ControllerCommons.htmlsafe("f&<")).toBe("f&amp;&lt;");
    });
    
    it("setupSeriesForDisplayType BW", function() {
        var result = ControllerCommons.setupSeriesForDisplayType(true, [{}], "line");
        expect(result[0].color).toBe("black");
    });
    
    it("setupSeriesForDisplayType color", function() {
        var result = ControllerCommons.setupSeriesForDisplayType(false, [{}], "line");
        expect(result[0].color).toBe("#E40E62");
    });
    
});
