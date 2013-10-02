describe("Tests for diagnosis groups controller", function() {
    var serverResponse = {"maleTable": {"rows": [
        {"name": "mar 2012", "data": [50, 98, 85]},
        {"name": "apr 2012", "data": [78, 64, 50]},
        {"name": "maj 2012", "data": [38, 33, 58]}
    ], "headers": ["A00-B99 Vissa infektionssjukdomar och parasitsjukdomar", "C00-D48 TumÃ¶rer", "D50-D89 Blodsjukdomar"]}, "femaleTable": {"rows": [
        {"name": "mar 2012", "data": [36, 87, 61]},
        {"name": "apr 2012", "data": [47, 40, 72]},
        {"name": "maj 2012", "data": [94, 81, 66]}
    ], "headers": ["A00-B99 Vissa infektionssjukdomar och parasitsjukdomar", "C00-D48 TumÃ¶rer", "D50-D89 Blodsjukdomar"]}, "maleChart": {"rows": [
        {"name": "Somatiska sjukdomar (A00-E90, G00-L99, N00-N99)", "data": [697, 625, 627]},
        {"name": "Psykiska sjukdomar (F00-F99)", "data": [50, 46, 50]},
        {"name": "Muskuloskeletala sjukdomar (M00-M99)", "data": [78, 55, 11]}
    ], "headers": ["mar 2012", "apr 2012", "maj 2012"]}, "femaleChart": {"rows": [
        {"name": "Somatiska sjukdomar (A00-E90, G00-L99, N00-N99)", "data": [536, 664, 690]},
        {"name": "Psykiska sjukdomar (F00-F99)", "data": [83, 13, 51]},
        {"name": "Muskuloskeletala sjukdomar (M00-M99)", "data": [47, 86, 85]}
    ], "headers": ["mar 2012", "apr 2012", "maj 2012"]}};

    var diagnosisGroupsCtrl = app.diagnosisGroupsCtrl({}, {}, {}, {"fetchMethod" : function(){}}, "fetchMethod", false);

    it("getTopHeaders", function() {
        var topHeaders = diagnosisGroupsCtrl.getTopHeaders(serverResponse);
        expect(topHeaders).toEqual([ { text : '', colspan : '1' }, { text : 'A00-B99 Vissa infektionssjukdomar och parasitsjukdomar', colspan : '2' }, { text : 'C00-D48 TumÃ¶rer', colspan : '2' }, { text : 'D50-D89 Blodsjukdomar', colspan : '2' },
            { text : '', colspan : '1' } ]);
    });

    it("getSubHeaders", function() {
        var subHeaders = diagnosisGroupsCtrl.getSubHeaders(serverResponse);
        expect(subHeaders).toEqual([ { text : 'Period', colspan : '1' }, { text : 'Kvinnor', colspan : '1' }, { text : 'Män', colspan : '1' }, { text : 'Kvinnor', colspan : '1' }, { text : 'Män', colspan : '1' }, { text : 'Kvinnor', colspan : '1' }, { text : 'Män', colspan : '1' }, { text : 'Summering', colspan : '1' } ]);
    });

    it("getRows", function() {
        var rows = diagnosisGroupsCtrl.getRows(serverResponse);
        expect(rows).toEqual([ { name : 'mar 2012', data : [ 36, 50, 87, 98, 61, 85, 417 ] }, { name : 'apr 2012', data : [ 47, 78, 40, 64, 72, 50, 351 ] }, { name : 'maj 2012', data : [ 94, 38, 81, 33, 66, 58, 370 ] } ]);
    });

    it("zipArrays", function() {
        var zippedArray = diagnosisGroupsCtrl.zipArrays([1,2,3,4], ["a","b","c","d"]);
        expect(zippedArray).toEqual([1,"a",2,"b",3,"c",4,"d"]);
    });

    it("appendSum", function() {
        var appendedSum = diagnosisGroupsCtrl.appendSum([1,2,3,4]);
        expect(appendedSum).toEqual([1,2,3,4,10]);
    });


});