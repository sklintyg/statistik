describe("Test of common functions for controllers", function() {

    it("addColor", function() {
        var list = [ { id : 1 }, { id : 2 } ];
        ControllerCommons.addColor(list);
        expect(list[0].color).toBe("#fbb10c");
        expect(list[1].color).toBe("#2ca2c6");
    });

    it("makeThousandSeparated", function() {
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
    
});