describe('Controller: TreeMultiSelectorModalCtrl', function() {
    'use strict';
    var ctrl, scope, directiveScope;
    var A00, A01, B07, D50, D70;
    var A00A09, B00B09, D50D53, D70D77, A00B99, D50D89;


    beforeEach(function() {
        angular.mock.module(function ($provide) {
                var mockStatistics = {
                    getStaticFilterData: function () {
                    }
                };
                $provide.value('statisticsData', mockStatistics);
            }
        );

        angular.mock.module('StatisticsApp.treeMultiSelector');
        angular.mock.module('StatisticsApp'); //We need this for the businessFilter injection in some tests

    });

    beforeEach(inject(function($controller, treeMultiSelectorUtil) {
        scope = {
            '$on' : function() {},
            '$emit' : function() {},
            '$watch': function() {},
            '$evalAsync': function() {}
        };
        directiveScope = {
            '$on' : function() {},
            '$emit' : function() {},
            '$watch': function() {},
            '$evalAsync': function() {}
        };

        ctrl = $controller('TreeMultiSelectorModalCtrl', {$scope: scope, directiveScope: directiveScope, $element: {}, $uibModalInstance: {}, treeMultiSelectorUtil: treeMultiSelectorUtil});
    }));

    var diagnoses;

    beforeEach(function () {
        A00 = {id: 'A00', visibleName: 'Kolera', numericalId: 21};
        A01 = {id: 'A01', visibleName: 'Tyfoidfeber och paratyfoidfeber', numericalId: 22};
        B07 = {id: 'B07', visibleName: 'Virusvårtor', numericalId: 23};
        D50 = {id: 'D50', visibleName: 'Järnbristanemi', numericalId: 24};
        D70 = {id: 'D70', visibleName: 'Agranulocytos', numericalId: 25};

        A00A09 = {
            id: 'A00-A09',
            visibleName: 'Infektionssjukdomar utgående från mag-tarmkanalen',
            subItems: [A00, A01],
            numericalId: 11
        };
        B00B09 = {
            id: 'B00-B09',
            visibleName: 'Virussjukdomar med hudutslag och slemhinneutslag',
            subItems: [B07],
            numericalId: 12
        };
        D50D53 = {
            id: 'D50-D53',
            visibleName: 'Nutritionsanemier',
            subItems: [D50],
            numericalId: 3};
        D70D77 = {
            id: 'D70-D77',
            visibleName: 'Andra sjukdomar i blod och blodbildande organ',
            subItems: [D70],
            numericalId: 14
        };

        A00B99 = {
            id: 'A00-B99',
            visibleName: 'Vissa infektionssjukdomar och parasitsjukdomar',
            subItems: [A00A09, B00B09],
            numericalId: 1
        };
        D50D89 = {
            id: 'D50-D89',
            visibleName: 'Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet',
            subItems: [D50D53, D70D77],
            numericalId: 2
        };

        diagnoses = [A00B99, D50D89];
    });

    it('parents should be intermediate when some child is selected', inject(function () {
        //Given
        var sub121 = {visibleName: 'sub121'};
        var sub12 = {visibleName: 'sub12', subs: [sub121, {visibleName: 'sub122'}]};
        var subs1 = [
            {visibleName: 'sub11'},
            sub12,
            {visibleName: 'sub13'}
        ];
        var menuItems = [
            {visibleName: 'Enhet1', subs: subs1},
            {
                visibleName: 'Enhet2', subs: [
                {visibleName: 'sub4'},
                {visibleName: 'sub5'},
                {visibleName: 'sub6'}
            ]
            }
        ];
        directiveScope.menuOptions = {subs: menuItems};

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

    it('parents should be marked as allSelected when all children are selected', inject(function () {
        //Given
        var sub121 = {visibleName: 'sub121'};
        var sub122 = {visibleName: 'sub122'};
        var sub12 = {visibleName: 'sub12', subs: [sub121, sub122]};
        var subs1 = [
            {visibleName: 'sub11'},
            sub12,
            {visibleName: 'sub13'}
        ];
        var menuItems = [
            {visibleName: 'Enhet1', subs: subs1},
            {
                visibleName: 'Enhet2', subs: [
                {visibleName: 'sub4'},
                {visibleName: 'sub5'},
                {visibleName: 'sub6'}
            ]
            }
        ];
        directiveScope.menuOptions = {subs: menuItems};

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

    it('all children should be selected when a parent is selected', inject(function () {
        //Given
        var sub121 = {visibleName: 'sub121'};
        var sub122 = {visibleName: 'sub122'};
        var sub12 = {visibleName: 'sub12', subs: [sub121, sub122]};
        var subs1 = [
            {visibleName: 'sub11'},
            sub12,
            {visibleName: 'sub13'}
        ];
        var menuItems = [
            {visibleName: 'Enhet1', subs: subs1},
            {
                visibleName: 'Enhet2', subs: [
                {visibleName: 'sub4'},
                {visibleName: 'sub5'},
                {visibleName: 'sub6'}
            ]
            }
        ];
        directiveScope.menuOptions = {subs: menuItems};

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

    it('all children should be deselected when a parent is deselected', inject(function () {
        //Given
        var sub121 = {visibleName: 'sub121'};
        var sub122 = {visibleName: 'sub122'};
        var sub12 = {visibleName: 'sub12', subs: [sub121, sub122]};
        var subs1 = [
            {visibleName: 'sub11'},
            sub12,
            {visibleName: 'sub13'}
        ];
        var menuItems = [
            {visibleName: 'enhet1', subs: subs1},
            {
                visibleName: 'enhet2', subs: [
                {visibleName: 'sub4'},
                {visibleName: 'sub5'},
                {visibleName: 'sub6'}
            ]
            }
        ];
        directiveScope.menuOptions = {subs: menuItems};

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

    it('hide items not matching filter', inject(function () {
        //Given
        var menuItems = {subs: [
                {visibleName: 'enhet1', subs: [
                {visibleName: 'sub11'},
                {visibleName: 'sub12', subs: [{visibleName: 'sub121'}, {visibleName: 'sub122'}]},
                {visibleName: 'sub13'}
            ]},
            {
                visibleName: 'enhet2', subs: [
                {visibleName: 'sub4'},
                {visibleName: 'sub5'},
                {visibleName: 'sub6'}
            ]
            }
        ]};
        scope.setupVisibleSubs(menuItems);

        //When
        scope.filterMenuItems(menuItems, 'Enhet1');

        //Then
        var joc = jasmine.objectContaining;
        var expectedResult = joc({visibleSubs: [
            joc({visibleName: 'enhet1', visibleSubs: [
                joc({visibleName: 'sub11'}),
                joc({visibleName: 'sub12', visibleSubs: [joc({visibleName: 'sub121'}), joc({visibleName: 'sub122'})]}),
                joc({visibleName: 'sub13'})
            ]})
        ]});
        expect(menuItems).toEqual(expectedResult);
    }));

    it('hide items not matching filter ignore "." ', inject(function () {
        //Given
        var menuItems = {subs: [
            {visibleName: 'enhet1'},
            {visibleName: 'enhet11'},
            {visibleName: 'enhet2'}
        ]};
        scope.setupVisibleSubs(menuItems);

        //When
        directiveScope.ignoreCharsInSearch = '.';
        scope.filterMenuItems(menuItems, 'Enhet.1');

        //Then
        var joc = jasmine.objectContaining;
        var expectedResult = joc({visibleSubs: [
            joc({visibleName: 'enhet1'}),
            joc({visibleName: 'enhet11'})
        ]});
        expect(menuItems).toEqual(expectedResult);
    }));

    it('hide items not matching filter', inject(function () {
        //Given
        var menuItems = {subs: [
            {visibleName: 'enhet.1'},
            {visibleName: 'enhet11'}
        ]};
        scope.setupVisibleSubs(menuItems);

        //When
        directiveScope.ignoreCharsInSearch = '';
        scope.filterMenuItems(menuItems, 'Enhet.1');

        //Then
        var joc = jasmine.objectContaining;
        var expectedResult = joc({visibleSubs: [
            joc({visibleName: 'enhet.1'})
        ]});
        expect(menuItems).toEqual(expectedResult);
    }));

    it('parent should be visible for matching node', inject(function () {
        //Given
        var menuItems = {subs: [
            {visibleName: 'enhet1', subs: [{visibleName: 'sub11'}, {visibleName: 'sub12'}]}
        ]};
        scope.setupVisibleSubs(menuItems);

        //When
        scope.filterMenuItems(menuItems, 'sub12');

        //Then
        var joc = jasmine.objectContaining;
        var expectedResult = joc({visibleSubs: [
            joc({visibleName: 'enhet1', visibleSubs: [joc({visibleName: 'sub12'})]})
        ]});
        expect(menuItems).toEqual(expectedResult);
    }));

    it('grandparent should be visible for matching node', inject(function () {
        //Given
        var menuItems = {subs: [
            {visibleName: 'enhet1', subs: [
                {visibleName: 'sub11'},
                {visibleName: 'sub12', subs: [{visibleName: 'sub121'}, {visibleName: 'sub122'}]},
                {visibleName: 'sub13'}
            ]},
            {
                visibleName: 'enhet2', subs: [
                {visibleName: 'sub4'},
                {visibleName: 'sub5'},
                {visibleName: 'sub6'}
            ]
            }
        ]};
        scope.setupVisibleSubs(menuItems);

        //When
        scope.filterMenuItems(menuItems, 'sub122');

        //Then
        var joc = jasmine.objectContaining;
        var expectedResult = joc({visibleSubs: [
            joc({visibleName: 'enhet1', visibleSubs: [
                joc({visibleName: 'sub12', visibleSubs: [joc({visibleName: 'sub122'})]})
            ]})
        ]});
        expect(menuItems).toEqual(expectedResult);
    }));

    it('child items from matching node should be visible', inject(function () {
        //Given
        var menuItems = {subs: [
            {visibleName: 'enhet1', subs: [
                {visibleName: 'sub11'},
                {visibleName: 'sub12', subs: [{visibleName: 'sub121'}, {visibleName: 'sub122'}]},
                {visibleName: 'sub13'}
            ]},
            {
                visibleName: 'enhet2', subs: [
                {visibleName: 'sub4'},
                {visibleName: 'sub5'},
                {visibleName: 'sub6'}
            ]
            }
        ]};
        scope.setupVisibleSubs(menuItems);

        //When
        scope.filterMenuItems(menuItems, 'Enhet1');

        //Then
        var joc = jasmine.objectContaining;
        var expectedResult = joc({visibleSubs: [
            joc({visibleName: 'enhet1', visibleSubs: [
                joc({visibleName: 'sub11'}),
                joc({visibleName: 'sub12', visibleSubs: [joc({visibleName: 'sub121'}), joc({visibleName: 'sub122'})]}),
                joc({visibleName: 'sub13'})
            ]})
        ]});
        expect(menuItems).toEqual(expectedResult);
    }));


    it('node should be fully expanded if only one match is found', inject(function () {
        //Given
        var menuItems = {subs: [
            {visibleName: 'Enhet1', subs: [
                {visibleName: 'sub11'},
                {visibleName: 'sub12', subs: [{visibleName: 'sub121'}, {visibleName: 'sub122'}]},
                {visibleName: 'sub13'}
            ]},
            {
                visibleName: 'Enhet2', subs: [
                {visibleName: 'sub4'},
                {visibleName: 'sub5'},
                {visibleName: 'sub6'}
            ]
            }
        ]};
        scope.setupVisibleSubs(menuItems);

        //When
        scope.filterMenuItems(menuItems, 'sub122', true);

        //Then
        var joc = jasmine.objectContaining;
        var expectedResult = joc({visibleSubs: [
            joc({visibleName: 'Enhet1', visibleSubs: [
                joc({visibleName: 'sub12', visibleSubs: [joc({visibleName: 'sub122'})]})
            ]})
        ]});
        expect(menuItems).toEqual(expectedResult);
    }));

    it('leaves count is counting correct when 0', inject(function () {
        //Given
        var sub11 = {visibleName: 'sub11', allSelected: false, someSelected: false};
        var sub12 = {visibleName: 'sub12', allSelected: false, someSelected: false};
        var menuItems = [
            {visibleName: 'Enhet1', subs: [sub11, sub12], someSelected: false, allSelected: false}
        ];

        directiveScope.menuOptions = {subs: menuItems};

        //When
        var selectedCount = scope.countSelectedByLevel();

        //Then
        expect(selectedCount[1]).toBe(undefined);
        expect(selectedCount[2]).toBe(undefined);
    }));

    it('leaves count is counting correct when 1', inject(function () {
        //Given
        var sub11 = {visibleName: 'sub11', allSelected: false, someSelected: false};
        var sub12 = {visibleName: 'sub12', allSelected: true, someSelected: false};
        var menuItems = [
            {visibleName: 'Enhet1', subs: [sub11, sub12], someSelected: true, allSelected: false}
        ];

        directiveScope.menuOptions = {subs: menuItems};

        //When
        var selectedCount = scope.countSelectedByLevel();

        //Then
        expect(selectedCount[1]).toBe(0);
        expect(selectedCount[2]).toBe(1);
    }));

    it('leaves count is counting correct when all', inject(function () {
        //Given
        var sub11 = {visibleName: 'sub11', allSelected: true, someSelected: false};
        var sub12 = {visibleName: 'sub12', allSelected: true, someSelected: false};
        var menuItems = [
            {visibleName: 'Enhet1', subs: [sub11, sub12], someSelected: false, allSelected: true}
        ];

        directiveScope.menuOptions = {subs: menuItems};

        //When
        var selectedCount = scope.countSelectedByLevel();

        //Then
        expect(selectedCount[1]).toBe(1);
        expect(selectedCount[2]).toBe(2);
    }));

    it('deselect all levels when kapitel is deselected', inject(function (businessFilterFactory) {
        // Given
        businessFilterFactory.setIcd10Structure(diagnoses);
        businessFilterFactory.dataInitialized = true;
        directiveScope.menuOptions = businessFilterFactory.icd10;
        businessFilterFactory.selectAll(businessFilterFactory.icd10);

        // When
        scope.itemClicked(A00B99, businessFilterFactory.icd10);
        businessFilterFactory.updateDiagnoses();

        //Then
        var diagnoser = businessFilterFactory.selectedDiagnoses;
        expect(diagnoser.length).toBe(1);
        expect(diagnoser).toContain(D50D89.numericalId);
    }));

    it('deselect all kategorier below unselected avsnitt', inject(function (businessFilterFactory, StaticFilterData) {
        // Given
        StaticFilterData.set({dxs: diagnoses});
        businessFilterFactory.setIcd10Structure(StaticFilterData.get().icd10Structure);
        businessFilterFactory.dataInitialized = true;
        directiveScope.menuOptions = businessFilterFactory.icd10;
        businessFilterFactory.selectAll(businessFilterFactory.icd10);

        // When
        scope.itemClicked(A00A09, businessFilterFactory.icd10);
        businessFilterFactory.updateDiagnoses();

        //Then
        var diagnoser = businessFilterFactory.selectedDiagnoses;
        expect(diagnoser.length).toBe(2);
        expect(diagnoser).toContain(B00B09.numericalId);
        expect(diagnoser).toContain(D50D89.numericalId);
    }));

    it('report selected kategoris', inject(function (businessFilterFactory, StaticFilterData) {
        // Given
        StaticFilterData.set({dxs: diagnoses});
        businessFilterFactory.setIcd10Structure(StaticFilterData.get().icd10Structure);
        businessFilterFactory.dataInitialized = true;
        directiveScope.menuOptions = businessFilterFactory.icd10;
        businessFilterFactory.selectAll(businessFilterFactory.icd10);

        // When
        scope.itemClicked(A01, businessFilterFactory.icd10);
        businessFilterFactory.updateDiagnoses();

        //Then
        var diagnoser = businessFilterFactory.selectedDiagnoses;
        expect(diagnoser.length).toBe(3);
        expect(diagnoser).toContain(A00.numericalId);
        expect(diagnoser).toContain(B00B09.numericalId);
        expect(diagnoser).toContain(D50D89.numericalId);
    }));

    it('"Utan gilitig ICD-10 kod" counts as a category only ', inject(function () {
        // Given
        var menuItems = {
            subs: [{
                visibleName: ' Utan gilitig ICD-10 kod',
                numericalId: 1670110002,
                someSelected: false,
                allSelected: true
            }]};
        directiveScope.menuOptions = menuItems;

        // When
        scope.updateCountersNow();

        //Then
        expect(scope.selectedPrimaryCounter).toBe(1);
        expect(scope.selectedQuaternaryCounter).toBe(0);
    }));

    it('should only count nodes which are marked as allSelected', inject(function (businessFilterFactory, StaticFilterData) {
        StaticFilterData.set({dxs: diagnoses});
        businessFilterFactory.setIcd10Structure(StaticFilterData.get().icd10Structure);
        businessFilterFactory.dataInitialized = true;
        directiveScope.menuOptions = businessFilterFactory.icd10;

        scope.updateCountersNow();

        expect(scope.selectedPrimaryCounter).toBe(0);
        expect(scope.selectedSecondaryCounter).toBe(0);
        expect(scope.selectedTertiaryCounter).toBe(0);

        scope.itemClicked(A00B99, businessFilterFactory.icd10);
        scope.updateCountersNow();

        expect(scope.selectedPrimaryCounter).toBe(1);
        expect(scope.selectedSecondaryCounter).toBe(2);
        expect(scope.selectedTertiaryCounter).toBe(3);

        scope.itemClicked(A01, businessFilterFactory.icd10);
        scope.updateCountersNow();

        expect(scope.selectedPrimaryCounter).toBe(0);
        expect(scope.selectedSecondaryCounter).toBe(1);
        expect(scope.selectedTertiaryCounter).toBe(2);

    }));
});
