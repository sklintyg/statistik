describe('Controller: directiveTmsCtrl', function() {
    beforeEach(module('StatisticsApp'));

    var ctrl;
    var scope;

    beforeEach(inject(function($controller) {
        scope = {};
        ctrl = $controller('directiveTmsCtrl', {$scope: scope});
    }));
    
    it("parents should be intermediate when some child is selected", inject(function () {
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
        scope.menuOptions = {subs: menuItems};

        //When
        scope.itemClicked(sub121);

        //Then
        expect(menuItems[0].allSelected).toBe(false);
        expect(menuItems[0].someSelected).toBe(true);
        expect(sub12.allSelected).toBe(false);
        expect(sub12.someSelected).toBe(true);
        expect(sub121.allSelected).toBe(true);
        expect(sub121.someSelected).toBe(false);
    }));

    it("parents should be marked as allSelected when all children are selected", inject(function () {
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
        scope.menuOptions = {subs: menuItems};

        //When
        scope.itemClicked(sub121, {subs: menuItems});
        scope.itemClicked(sub122, {subs: menuItems});

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

    it("all children should be selected when a parent is selected", inject(function () {
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
        scope.menuOptions = {subs: menuItems};

        //When
        scope.itemClicked(sub12, {subs: menuItems});

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

    it("all children should be deselected when a parent is deselected", inject(function () {
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
        scope.menuOptions = {subs: menuItems};

        //When
        scope.itemClicked(sub12, {subs: menuItems});
        scope.itemClicked(sub12, {subs: menuItems});

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

    it("hide items not matching filter", inject(function () {
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
        scope.filterMenuItems(menuItems, "Enhet1");

        //Then
        expect(menuItems[0].hide).toBe(false);
        expect(menuItems[1].hide).toBe(true);
    }));

    xit("parent should be visible for matching node", inject(function () {
        //Given
        var sub11 = {name: "sub11"};
        var sub12 = {name: "sub12"};
        var menuItems = [
            {name: "Enhet1", subs: [sub11, sub12]}
        ];

        //When
        scope.filterMenuItems(menuItems, "sub12");

        //Then
        expect(menuItems[0].hide).toBe(false);
        expect(sub12.hide).toBe(false);
        expect(sub11.hide).toBe(true);
    }));

    it("grandparent should be visible for matching node", inject(function () {
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
        scope.filterMenuItems(menuItems, "sub122");

        //Then
        expect(menuItems[0].hide).toBe(false);
        expect(sub12.hide).toBe(false);
        expect(sub122.hide).toBe(false);
        expect(sub121.hide).toBe(true);
        expect(menuItems[1].hide).toBe(true);
    }));

    it("node should be fully expanded if only one match is found", inject(function () {
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
        scope.filterMenuItems(menuItems, "sub122");

        //Then
        expect(menuItems[0].hideChildren).toBe(false);
        expect(sub12.hideChildren).toBe(false);
        expect(sub122.hideChildren).toBe(false);
        expect(sub121.hide).toBe(true);
        expect(menuItems[1].hide).toBe(true);
    }));

    it("leaves count is counting correct when 0", inject(function () {
        //Given
        var sub11 = {name: "sub11", allSelected: false, someSelected: false};
        var sub12 = {name: "sub12", allSelected: false, someSelected: false};
        var menuItems = [
            {name: "Enhet1", subs: [sub11, sub12], someSelected: false, allSelected: false}
        ];

        //When
        var leavesCount = scope.selectedLeavesCount({subs: menuItems});

        //Then
        expect(leavesCount).toBe(0);
    }));

    it("leaves count is counting correct when 1", inject(function () {
        //Given
        var sub11 = {name: "sub11", allSelected: false, someSelected: false};
        var sub12 = {name: "sub12", allSelected: true, someSelected: false};
        var menuItems = [
            {name: "Enhet1", subs: [sub11, sub12], someSelected: true, allSelected: false}
        ];

        //When
        var leavesCount = scope.selectedLeavesCount({subs: menuItems});

        //Then
        expect(leavesCount).toBe(1);
    }));

    it("leaves count is counting correct when all", inject(function () {
        //Given
        var sub11 = {name: "sub11", allSelected: true, someSelected: false};
        var sub12 = {name: "sub12", allSelected: true, someSelected: false};
        var menuItems = [
            {name: "Enhet1", subs: [sub11, sub12], someSelected: false, allSelected: true}
        ];

        //When
        var leavesCount = scope.selectedLeavesCount({subs: menuItems});

        //Then
        expect(leavesCount).toBe(2);
    }));

    it("deselect all levels when kapitel is deselected", inject(function (businessFilter) {
        // Given

        // When
        businessFilter.setIcd10Structure(diagnoses);
        businessFilter.dataInitialized = true;
        scope.menuOptions = businessFilter.icd10;
        scope.itemClicked(A00B99, businessFilter.icd10);
        businessFilter.updateDiagnoses();

        //Then
        var diagnoser = businessFilter.getSelectedDiagnoses(true);
        expect(diagnoser.kategorier.length).toBe(0);
        expect(diagnoser.avsnitt.length).toBe(0);
        expect(diagnoser.kapitel.length).toBe(1);
        expect(diagnoser.kapitel).toContain(D50D89.numericalId);
    }));

    it("deselect all kategorier below unselected avsnitt", inject(function (businessFilter) {
        // Given

        // When
        businessFilter.setIcd10Structure(diagnoses);
        businessFilter.dataInitialized = true;
        scope.menuOptions = businessFilter.icd10;
        scope.itemClicked(A00A09, businessFilter.icd10);
        businessFilter.updateDiagnoses();

        //Then
        var diagnoser = businessFilter.getSelectedDiagnoses(true);
        expect(diagnoser.kategorier.length).toBe(0);
        expect(diagnoser.avsnitt.length).toBe(1);
        expect(diagnoser.kapitel.length).toBe(1);
        expect(diagnoser.avsnitt).toContain(B00B09.numericalId);
        expect(diagnoser.kapitel).toContain(D50D89.numericalId);
    }));

    it("report selected kategoris", inject(function (businessFilter) {
        // Given

        // When
        businessFilter.setIcd10Structure(diagnoses);
        businessFilter.dataInitialized = true;
        scope.menuOptions = businessFilter.icd10;
        scope.itemClicked(A01, businessFilter.icd10);
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
