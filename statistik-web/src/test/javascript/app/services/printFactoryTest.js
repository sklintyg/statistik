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

    it("addColor", function() {
        var list = [ { id : 1 }, { id : 2 } ];
        printFactory.addColor(list);
        expect(list[0].color).toBe("#E40E62");
        expect(list[1].color).toBe("#00AEEF");
    });

    it("addColor male", function() {
        var list = [ { id : 1, sex: 'Male' }, { id : 2, sex: 'Male' } ];
        printFactory.addColor(list);
        expect(list[0].color).toBe("#008391");
        expect(list[1].color).toBe("#90cad0");
    });

    it("addColor female", function() {
        var list = [ { id : 1, sex: 'Female' }, { id : 2, sex: 'Female' } ];
        printFactory.addColor(list);
        expect(list[0].color).toBe("#EA8025");
        expect(list[1].color).toBe("#f6c08d");
    });

    it("addColor with black and white print === true should use color objects with bw patterns", function() {
        var list = [ { id : 1 }, { id : 2 }, { id : 3, sex: 'Male' }, { id : 4, sex: 'Male' }, { id : 5, sex: 'Female' }, { id : 6, sex: 'Female' } ];
        printFactory.addColor(list, true);
        expect(list[0].color.pattern).toMatch(/.*1\.png/);
        expect(list[1].color.pattern).toMatch(/.*2\.png/);
        expect(list[2].color.pattern).toMatch(/.*5\.png/); //Male bw pattern 1
        expect(list[3].color.pattern).toMatch(/.*6\.png/); //Male bw pattern 2
        expect(list[4].color.pattern).toMatch(/.*7\.png/); //Female bw pattern 1
        expect(list[5].color.pattern).toMatch(/.*8\.png/); //Female bw pattern 2
    });

    it("addColor with black and white print === true should use color objects with bw patterns", function() {
        var list = [ { id : 1 }, { id : 2 } ];
        printFactory.addColor(list, true);
        expect(list[0].color.pattern).toMatch(/.*1\.png/);
        expect(list[1].color.pattern).toMatch(/.*2\.png/);
    });

    it("setupSeriesForDisplayType BW", function() {
        var result = printFactory.setupSeriesForDisplayType(true, [{}], "line");
        expect(result[0].color).toBe("black");
    });

    it("setupSeriesForDisplayType color", function() {
        var result = printFactory.setupSeriesForDisplayType(false, [{}], "line");
        expect(result[0].color).toBe("#E40E62");
    });

});
