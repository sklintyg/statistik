describe("Test of common functions for controllers", function() {
    var casesPerMonthResponse = {"rows":[{"name":"mar 2012","data":[23906,11851,12055,23906]},{"name":"apr 2012","data":[21087,9475,11612,44993]},{"name":"maj 2012","data":[21259,9737,11522,66252]},{"name":"jun 2012","data":[16687,8395,8292,82939]},{"name":"jul 2012","data":[21737,11435,10302,104676]},{"name":"aug 2012","data":[19041,10916,8125,123717]},{"name":"sep 2012","data":[22201,10911,11290,145918]},{"name":"okt 2012","data":[23552,11605,11947,169470]},{"name":"nov 2012","data":[23402,14042,9360,192872]},{"name":"dec 2012","data":[22776,11986,10790,215648]},{"name":"jan 2013","data":[25511,10999,14512,241159]},{"name":"feb 2013","data":[18862,11017,7845,260021]},{"name":"mar 2013","data":[15984,6728,9256,276005]},{"name":"apr 2013","data":[22718,11617,11101,298723]},{"name":"maj 2013","data":[22778,12162,10616,321501]},{"name":"jun 2013","data":[19088,8262,10826,340589]},{"name":"jul 2013","data":[18691,7807,10884,359280]},{"name":"aug 2013","data":[22382,13325,9057,381662]}],"headers":["Antal sjukfall","Antal kvinnor","Antal mÃ¤n","Summering"]};
    
  it("addColor", function() {
    var list = [{id:1}, {id:2}];
        addColor(list);
        expect(list[0].color).toBe("#fbb10c");
        expect(list[1].color).toBe("#2ca2c6");
      });
    
  it("getChartCategories", function() {
      var categories = getChartCategories(casesPerMonthResponse);
      expect(categories[0]).toBe("mar 2012");
  });
  
    
});