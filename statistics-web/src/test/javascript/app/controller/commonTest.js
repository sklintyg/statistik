describe("Test of common functions for controllers", function() {
    var casesPerMonthResponse = {
        "rows" : [ {
            "name" : "mar 2012",
            "data" : [ 23906, 11851, 12055, 23906 ]
        }, {
            "name" : "apr 2012",
            "data" : [ 21087, 9475, 11612, 44993 ]
        }, {
            "name" : "maj 2012",
            "data" : [ 21259, 9737, 11522, 66252 ]
        } ],
        "headers" : [ "Antal sjukfall", "Antal kvinnor", "Antal mÃ¤n",
                "Summering" ]
    };

    it("addColor", function() {
        var list = [ { id : 1 }, { id : 2 } ];
        ControllerCommons.addColor(list);
        expect(list[0].color).toBe("#fbb10c");
        expect(list[1].color).toBe("#2ca2c6");
    });

    it("getChartCategories", function() {
        var categories = ControllerCommons.getChartCategories(casesPerMonthResponse);
        expect(categories[0]).toBe("mar 2012");
        expect(categories[1]).toBe("apr 2012");
    });
  
    it("getChartSeries", function() {
        var series = ControllerCommons.getChartSeries(casesPerMonthResponse);
        expect(series[0]).toEqual({ name : 'Antal sjukfall', data : [ 23906, 21087, 21259 ] });
        expect(series[1]).toEqual({ name : 'Antal kvinnor', data : [ 11851, 9475, 9737 ] });
    });
    
    
});