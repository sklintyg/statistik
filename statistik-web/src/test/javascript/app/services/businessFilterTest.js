describe("Tests for business overview controller", function () {
    var A00, A01, B07, D50, D70;
    var A00A09, B00B09, D50D53, D70D77, A00B99, D50D89;
    var diagnoses;

    beforeEach(function () {
        A00 = {id: "A00", name: "Kolera", numericalId: 1};
        A01 = {id: "A01", name: "Tyfoidfeber och paratyfoidfeber", numericalId: 2};
        B07 = {id: "B07", name: "Virusvårtor", numericalId: 3};
        D50 = {id: "D50", name: "Järnbristanemi", numericalId: 4};
        D70 = {id: "D70", name: "Agranulocytos", numericalId: 5};

        A00A09 = {
            id: "A00-A09",
            name: "Infektionssjukdomar utgående från mag-tarmkanalen",
            subItems: [A00, A01],
            numericalId: 1
        };
        B00B09 = {
            id: "B00-B09",
            name: "Virussjukdomar med hudutslag och slemhinneutslag",
            subItems: [B07],
            numericalId: 2
        };
        D50D53 = {
            id: "D50-D53",
            name: "Nutritionsanemier",
            subItems: [D50],
            numericalId: 3};
        D70D77 = {
            id: "D70-D77",
            name: "Andra sjukdomar i blod och blodbildande organ",
            subItems: [D70],
            numericalId: 4
        };

        A00B99 = {
            id: "A00-B99",
            name: "Vissa infektionssjukdomar och parasitsjukdomar",
            subItems: [A00A09, B00B09],
            numericalId: 1
        };
        D50D89 = {
            id: "D50-D89",
            name: "Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet",
            subItems: [D50D53, D70D77],
            numericalId: 2
        };

        diagnoses = [A00B99, D50D89]
    });

    beforeEach(module('StatisticsApp'));

    beforeEach(module(function ($provide) {
        mockStatistics = {
            getIcd10Structure: function () { }
        };
        $provide.value('statisticsData', mockStatistics);
    }));

    it("populates verksamheter correctly", inject(function (businessFilter, _) {
        //Given
        var verksamhetsTyper1 = [{id: 1, name: "v1"}, {id: 2, name: "v2"}];
        var verksamhetsTyper2 = [{id: 1, name: "v1"}, {id: 3, name: "v3"}];
        var business1 = {id: 1, name: "b1", verksamhetsTyper: verksamhetsTyper1};
        var business2 = {id: 2, name: "b2", verksamhetsTyper: verksamhetsTyper2};

        //When
        businessFilter.populateVerksamhetsTyper([business1, business2]);

        //Then
        var verksamheter = businessFilter.verksamhetsTyper;
        expect(verksamheter.length).toBe(3);
        _.each(verksamheter, function (verksamhet) {
            if (verksamhet.id === 1) {
                expect(verksamhet.name).toBe("v1 (2 enheter)");
                expect(verksamhet.units.length).toBe(2);
                expect(_.findWhere(verksamhet.units, {id: 1})).toBeDefined();
                expect(_.findWhere(verksamhet.units, {id: 2})).toBeDefined();
            }
            if (verksamhet.id === 2) {
                expect(verksamhet.name).toBe("v2 (1 enhet)");
                expect(verksamhet.units.length).toBe(1);
                expect(_.findWhere(verksamhet.units, {id: 1})).toBeDefined();
                expect(_.findWhere(verksamhet.units, {id: 2})).toBeUndefined();
            }
            if (verksamhet.id === 3) {
                expect(verksamhet.name).toBe("v3 (1 enhet)");
                expect(verksamhet.units.length).toBe(1);
                expect(_.findWhere(verksamhet.units, {id: 1})).toBeUndefined();
                expect(_.findWhere(verksamhet.units, {id: 2})).toBeDefined();
            }
        })
    }));

    it("all are selected by default", inject(function (businessFilter, _) {
        // Given

        // When
        businessFilter.setIcd10Structure(diagnoses);

        //Then
        expect(A00.allSelected).toBeTruthy();
        expect(B07.allSelected).toBeTruthy();
        expect(D50.allSelected).toBeTruthy();
        expect(D70.allSelected).toBeTruthy();
        expect(A00A09.allSelected).toBeTruthy();
        expect(D70D77.allSelected).toBeTruthy();
        expect(A00B99.allSelected).toBeTruthy();

    }));

});
