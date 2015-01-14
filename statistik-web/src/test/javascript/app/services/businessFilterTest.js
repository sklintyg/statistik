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

    xit("parents should be intermediate when some child is selected", inject(function (businessFilter) {
        //Given
        var sub121 = {name: "sub121"};
        var sub12 = {name: "sub12", subs: [sub121, {name: "sub122"}]};
        var subs1 = [
            {name: "sub11"},
            sub12,
            {name: "sub13"}
        ];
        var menuItems = [
            {name: "Enhet1", subs: subs1},
            {
                name: "Enhet2", subs: [
                {name: "sub4"},
                {name: "sub5"},
                {name: "sub6"}
            ]
            }
        ];

        //When
        businessFilter.itemClicked(sub121, {subs: menuItems});

        //Then
        expect(menuItems[0].allSelected).toBe(false);
        expect(menuItems[0].someSelected).toBe(true);
        expect(sub12.allSelected).toBe(false);
        expect(sub12.someSelected).toBe(true);
        expect(sub121.allSelected).toBe(true);
        expect(sub121.someSelected).toBe(false);
    }));

    xit("parents should be marked as allSelected when all children are selected", inject(function (businessFilter) {
        //Given
        var sub121 = {name: "sub121"};
        var sub122 = {name: "sub122"};
        var sub12 = {name: "sub12", subs: [sub121, sub122]};
        var subs1 = [
            {name: "sub11"},
            sub12,
            {name: "sub13"}
        ];
        var menuItems = [
            {name: "Enhet1", subs: subs1},
            {
                name: "Enhet2", subs: [
                {name: "sub4"},
                {name: "sub5"},
                {name: "sub6"}
            ]
            }
        ];

        //When
        businessFilter.itemClicked(sub121, {subs: menuItems});
        businessFilter.itemClicked(sub122, {subs: menuItems});

        //Then
        expect(menuItems[0].allSelected).toBe(false);
        expect(menuItems[0].someSelected).toBe(true);
        expect(sub12.allSelected).toBe(true);
        expect(sub12.someSelected).toBe(false);
        expect(sub121.allSelected).toBe(true);
        expect(sub121.someSelected).toBe(false);
        expect(sub122.allSelected).toBe(true);
        expect(sub122.someSelected).toBe(false);
    }));

    xit("all children should be selected when a parent is selected", inject(function (businessFilter) {
        //Given
        var sub121 = {name: "sub121"};
        var sub122 = {name: "sub122"};
        var sub12 = {name: "sub12", subs: [sub121, sub122]};
        var subs1 = [
            {name: "sub11"},
            sub12,
            {name: "sub13"}
        ];
        var menuItems = [
            {name: "Enhet1", subs: subs1},
            {
                name: "Enhet2", subs: [
                {name: "sub4"},
                {name: "sub5"},
                {name: "sub6"}
            ]
            }
        ];

        //When
        businessFilter.itemClicked(sub12, {subs: menuItems});

        //Then
        expect(menuItems[0].allSelected).toBe(false);
        expect(menuItems[0].someSelected).toBe(true);
        expect(sub12.allSelected).toBe(true);
        expect(sub12.someSelected).toBe(false);
        expect(sub121.allSelected).toBe(true);
        expect(sub121.someSelected).toBe(false);
        expect(sub122.allSelected).toBe(true);
        expect(sub122.someSelected).toBe(false);
    }));

    xit("all children should be deselected when a parent is deselected", inject(function (businessFilter) {
        //Given
        var sub121 = {name: "sub121"};
        var sub122 = {name: "sub122"};
        var sub12 = {name: "sub12", subs: [sub121, sub122]};
        var subs1 = [
            {name: "sub11"},
            sub12,
            {name: "sub13"}
        ];
        var menuItems = [
            {name: "Enhet1", subs: subs1},
            {
                name: "Enhet2", subs: [
                {name: "sub4"},
                {name: "sub5"},
                {name: "sub6"}
            ]
            }
        ];

        //When
        businessFilter.itemClicked(sub12, {subs: menuItems});
        businessFilter.itemClicked(sub12, {subs: menuItems});

        //Then
        expect(menuItems[0].allSelected).toBe(false);
        expect(menuItems[0].someSelected).toBe(false);
        expect(sub12.allSelected).toBe(false);
        expect(sub12.someSelected).toBe(false);
        expect(sub121.allSelected).toBe(false);
        expect(sub121.someSelected).toBe(false);
        expect(sub122.allSelected).toBe(false);
        expect(sub122.someSelected).toBe(false);
    }));

    xit("hide items not matching filter", inject(function (businessFilter) {
        //Given
        var sub121 = {name: "sub121"};
        var sub122 = {name: "sub122"};
        var sub12 = {name: "sub12", subs: [sub121, sub122]};
        var subs1 = [
            {name: "sub11"},
            sub12,
            {name: "sub13"}
        ];
        var menuItems = [
            {name: "Enhet1", subs: subs1},
            {
                name: "Enhet2", subs: [
                {name: "sub4"},
                {name: "sub5"},
                {name: "sub6"}
            ]
            }
        ];

        //When
        businessFilter.filterMenuItems(menuItems, "Enhet1");

        //Then
        expect(menuItems[0].hide).toBe(false);
        expect(menuItems[1].hide).toBe(true);
    }));

    xit("parent should be visible for matching node", inject(function (businessFilter) {
        //Given
        var sub11 = {name: "sub11"};
        var sub12 = {name: "sub12"};
        var menuItems = [
            {name: "Enhet1", subs: [sub11, sub12]}
        ];

        //When
        businessFilter.filterMenuItems(menuItems, "sub12");

        //Then
        expect(menuItems[0].hide).toBe(false);
        expect(sub12.hide).toBe(false);
        expect(sub11.hide).toBe(true);
    }));

    xit("grandparent should be visible for matching node", inject(function (businessFilter) {
        //Given
        var sub121 = {name: "sub121"};
        var sub122 = {name: "sub122"};
        var sub12 = {name: "sub12", subs: [sub121, sub122]};
        var subs1 = [
            {name: "sub11"},
            sub12,
            {name: "sub13"}
        ];
        var menuItems = [
            {name: "Enhet1", subs: subs1},
            {
                name: "Enhet2", subs: [
                {name: "sub4"},
                {name: "sub5"},
                {name: "sub6"}
            ]
            }
        ];

        //When
        businessFilter.filterMenuItems(menuItems, "sub122");

        //Then
        expect(menuItems[0].hide).toBe(false);
        expect(sub12.hide).toBe(false);
        expect(sub122.hide).toBe(false);
        expect(sub121.hide).toBe(true);
        expect(menuItems[1].hide).toBe(true);
    }));

    xit("node should be fully expanded if only one match is found", inject(function (businessFilter) {
        //Given
        var sub121 = {name: "sub121"};
        var sub122 = {name: "sub122"};
        var sub12 = {name: "sub12", subs: [sub121, sub122]};
        var subs1 = [
            {name: "sub11"},
            sub12,
            {name: "sub13"}
        ];
        var menuItems = [
            {name: "Enhet1", subs: subs1},
            {
                name: "Enhet2", subs: [
                {name: "sub4"},
                {name: "sub5"},
                {name: "sub6"}
            ]
            }
        ];

        //When
        businessFilter.filterMenuItems(menuItems, "sub122");

        //Then
        expect(menuItems[0].hideChildren).toBe(false);
        expect(sub12.hideChildren).toBe(false);
        expect(sub122.hideChildren).toBe(false);
        expect(sub121.hide).toBe(true);
        expect(menuItems[1].hide).toBe(true);
    }));

    xit("leaves count is counting correct when 0", inject(function (businessFilter) {
        //Given
        var sub11 = {name: "sub11", allSelected: false, someSelected: false};
        var sub12 = {name: "sub12", allSelected: false, someSelected: false};
        var menuItems = [
            {name: "Enhet1", subs: [sub11, sub12], someSelected: false, allSelected: false}
        ];

        //When
        var leavesCount = businessFilter.selectedLeavesCount({subs: menuItems});

        //Then
        expect(leavesCount).toBe(0);
    }));

    xit("leaves count is counting correct when 1", inject(function (businessFilter) {
        //Given
        var sub11 = {name: "sub11", allSelected: false, someSelected: false};
        var sub12 = {name: "sub12", allSelected: true, someSelected: false};
        var menuItems = [
            {name: "Enhet1", subs: [sub11, sub12], someSelected: true, allSelected: false}
        ];

        //When
        var leavesCount = businessFilter.selectedLeavesCount({subs: menuItems});

        //Then
        expect(leavesCount).toBe(1);
    }));

    xit("leaves count is counting correct when all", inject(function (businessFilter) {
        //Given
        var sub11 = {name: "sub11", allSelected: true, someSelected: false};
        var sub12 = {name: "sub12", allSelected: true, someSelected: false};
        var menuItems = [
            {name: "Enhet1", subs: [sub11, sub12], someSelected: false, allSelected: true}
        ];

        //When
        var leavesCount = businessFilter.selectedLeavesCount({subs: menuItems});

        //Then
        expect(leavesCount).toBe(2);
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

    xit("deselect all levels when kapitel is deselected", inject(function (businessFilter, _) {
        // Given

        // When
        businessFilter.setIcd10Structure(diagnoses);
        businessFilter.dataInitialized = true;
        businessFilter.itemClicked(A00B99, businessFilter.icd10);
        businessFilter.updateDiagnoses();

        //Then
        var diagnoser = businessFilter.getSelectedDiagnoses(true);
        expect(diagnoser.kategorier.length).toBe(0);
        expect(diagnoser.avsnitt.length).toBe(0);
        expect(diagnoser.kapitel.length).toBe(1);
        expect(diagnoser.kapitel).toContain(D50D89.numericalId);

    }));

    xit("deselect all kategorier below unselected avsnitt", inject(function (businessFilter, treeMultiSelector, _) {
        // Given

        // When
        businessFilter.setIcd10Structure(diagnoses);
        businessFilter.dataInitialized = true;
        businessFilter.itemClicked(A00A09, businessFilter.icd10);
        businessFilter.updateDiagnoses();

        //Then
        var diagnoser = businessFilter.getSelectedDiagnoses(true);
        expect(diagnoser.kategorier.length).toBe(0);
        expect(diagnoser.avsnitt.length).toBe(1);
        expect(diagnoser.kapitel.length).toBe(1);
        expect(diagnoser.avsnitt).toContain(B00B09.numericalId);
        expect(diagnoser.kapitel).toContain(D50D89.numericalId);

    }));

    xit("report selected kategoris", inject(function (businessFilter, _) {
        // Given

        // When
        businessFilter.setIcd10Structure(diagnoses);
        businessFilter.dataInitialized = true;
        businessFilter.itemClicked(A01, businessFilter.icd10);
        businessFilter.updateDiagnoses();

        //Then
        var diagnoser = businessFilter.getSelectedDiagnoses(true);
        expect(diagnoser.kategorier.length).toBe(1);
        expect(diagnoser.avsnitt.length).toBe(1);
        expect(diagnoser.kapitel.length).toBe(1);
        expect(diagnoser.kategorier).toContain(A00.numericalId);
        expect(diagnoser.avsnitt).toContain(B00B09.numericalId);
        expect(diagnoser.kapitel).toContain(D50D89.numericalId);

    }));

});
