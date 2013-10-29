describe("Test of common functions for controllers", function() {

    it("addColor", function() {
        var list = [ { id : 1 }, { id : 2 } ];
        ControllerCommons.addColor(list);
        expect(list[0].color).toBe("#fbb10c");
        expect(list[1].color).toBe("#2ca2c6");
    });

    it("makeThousandSeparated", function() {
        expect(ControllerCommons.makeThousandSeparated(0)).toBe("0");
        expect(ControllerCommons.makeThousandSeparated(1000)).toBe("1 000");
        expect(ControllerCommons.makeThousandSeparated(9999999)).toBe("9 999 999");
        expect(ControllerCommons.makeThousandSeparated(-5000)).toBe("-5 000");
    });
    
});