describe('Controller: treeMultiSelectorCtrl', function() {
    'use strict';
    var ctrl, scope;
    var A00, A01, B07, D50, D70;
    var A00A09, B00B09, D50D53, D70D77, A00B99, D50D89;


    beforeEach(function() {
        angular.mock.module(function ($provide) {
                var mockStatistics = {
                    getIcd10Structure: function () {
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
            '$watch': function() {}
        };
        ctrl = $controller('treeMultiSelectorCtrl', {$scope: scope, $element: {}, treeMultiSelectorUtil: treeMultiSelectorUtil});
    }));

    var diagnoses;

    beforeEach(function () {
        A00 = {id: 'A00', name: 'Kolera', numericalId: 21};
        A01 = {id: 'A01', name: 'Tyfoidfeber och paratyfoidfeber', numericalId: 22};
        B07 = {id: 'B07', name: 'Virusvårtor', numericalId: 23};
        D50 = {id: 'D50', name: 'Järnbristanemi', numericalId: 24};
        D70 = {id: 'D70', name: 'Agranulocytos', numericalId: 25};

        A00A09 = {
            id: 'A00-A09',
            name: 'Infektionssjukdomar utgående från mag-tarmkanalen',
            subItems: [A00, A01],
            numericalId: 11
        };
        B00B09 = {
            id: 'B00-B09',
            name: 'Virussjukdomar med hudutslag och slemhinneutslag',
            subItems: [B07],
            numericalId: 12
        };
        D50D53 = {
            id: 'D50-D53',
            name: 'Nutritionsanemier',
            subItems: [D50],
            numericalId: 3};
        D70D77 = {
            id: 'D70-D77',
            name: 'Andra sjukdomar i blod och blodbildande organ',
            subItems: [D70],
            numericalId: 14
        };

        A00B99 = {
            id: 'A00-B99',
            name: 'Vissa infektionssjukdomar och parasitsjukdomar',
            subItems: [A00A09, B00B09],
            numericalId: 1
        };
        D50D89 = {
            id: 'D50-D89',
            name: 'Sjukdomar i blod och blodbildande organ samt vissa rubbningar i immunsystemet',
            subItems: [D50D53, D70D77],
            numericalId: 2
        };

        diagnoses = [A00B99, D50D89];
    });

    it('parents should be intermediate when some child is selected', inject(function () {
        //Given
        var sub121 = {name: 'sub121'};
        var sub12 = {name: 'sub12', subs: [sub121, {name: 'sub122'}]};
        var subs1 = [
            {name: 'sub11'},
            sub12,
            {name: 'sub13'}
        ];
        var menuItems = [
            {name: 'Enhet1', subs: subs1},
            {
                name: 'Enhet2', subs: [
                {name: 'sub4'},
                {name: 'sub5'},
                {name: 'sub6'}
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

    it('parents should be marked as allSelected when all children are selected', inject(function () {
        //Given
        var sub121 = {name: 'sub121'};
        var sub122 = {name: 'sub122'};
        var sub12 = {name: 'sub12', subs: [sub121, sub122]};
        var subs1 = [
            {name: 'sub11'},
            sub12,
            {name: 'sub13'}
        ];
        var menuItems = [
            {name: 'Enhet1', subs: subs1},
            {
                name: 'Enhet2', subs: [
                {name: 'sub4'},
                {name: 'sub5'},
                {name: 'sub6'}
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

    it('all children should be selected when a parent is selected', inject(function () {
        //Given
        var sub121 = {name: 'sub121'};
        var sub122 = {name: 'sub122'};
        var sub12 = {name: 'sub12', subs: [sub121, sub122]};
        var subs1 = [
            {name: 'sub11'},
            sub12,
            {name: 'sub13'}
        ];
        var menuItems = [
            {name: 'Enhet1', subs: subs1},
            {
                name: 'Enhet2', subs: [
                {name: 'sub4'},
                {name: 'sub5'},
                {name: 'sub6'}
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

    it('all children should be deselected when a parent is deselected', inject(function () {
        //Given
        var sub121 = {name: 'sub121'};
        var sub122 = {name: 'sub122'};
        var sub12 = {name: 'sub12', subs: [sub121, sub122]};
        var subs1 = [
            {name: 'sub11'},
            sub12,
            {name: 'sub13'}
        ];
        var menuItems = [
            {name: 'Enhet1', subs: subs1},
            {
                name: 'Enhet2', subs: [
                {name: 'sub4'},
                {name: 'sub5'},
                {name: 'sub6'}
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

    it('hide items not matching filter', inject(function ($timeout) {
        //Given
        var sub121 = {name: 'sub121'};
        var sub122 = {name: 'sub122'};
        var sub12 = {name: 'sub12', subs: [sub121, sub122]};
        var subs1 = [
            {name: 'sub11'},
            sub12,
            {name: 'sub13'}
        ];
        var menuItems = [
            {name: 'Enhet1', subs: subs1},
            {
                name: 'Enhet2', subs: [
                {name: 'sub4'},
                {name: 'sub5'},
                {name: 'sub6'}
            ]
            }
        ];

        //When
        scope.filterMenuItems(menuItems, 'Enhet1');

        //Then
        $timeout(function(){
            expect(menuItems[0].hide).toBeFalsy();
            expect(menuItems[1].hide).toBeTruthy();
        }, 100);
    }));

    it('hide items not matching filter ignore "." ', inject(function ($timeout) {
        //Given
        var menuItems = [
            {name: 'Enhet.1'},
            {name: 'Enhet2'}
        ];

        //When
        scope.ignoreCharsInSearch = '';
        scope.filterMenuItems(menuItems, 'Enhet.1');

        //Then
        $timeout(function(){
            expect(menuItems[0].hide).toBeTruthy();
            expect(menuItems[1].hide).toBeTruthy();
        }, 100);
    }));

    it('hide items not matching filter', inject(function ($timeout) {
        //Given
        var menuItems = [
            {name: 'Enhet1'},
            {name: 'Enhet2'}
        ];

        //When
        scope.ignoreCharsInSearch = '';
        scope.filterMenuItems(menuItems, 'sub1.21');

        //Then
        $timeout(function(){
            expect(menuItems[0].hide).toBeTruthy();
            expect(menuItems[1].hide).toBeTruthy();
        }, 100);
    }));

    it('parent should be visible for matching node', inject(function ($timeout) {
        //Given
        var sub11 = {name: 'sub11'};
        var sub12 = {name: 'sub12'};
        var menuItems = [
            {name: 'Enhet1', subs: [sub11, sub12]}
        ];

        //When
        scope.filterMenuItems(menuItems, 'sub12');

        //Then
        $timeout(function(){
            expect(menuItems[0].hide).toBe(false);
            expect(sub12.hide).toBe(false);
            expect(sub11.hide).toBe(true);
        }, 100);
    }));

    it('grandparent should be visible for matching node', inject(function ($timeout) {
        //Given
        var sub121 = {name: 'sub121'};
        var sub122 = {name: 'sub122'};
        var sub12 = {name: 'sub12', subs: [sub121, sub122]};
        var subs1 = [
            {name: 'sub11'},
            sub12,
            {name: 'sub13'}
        ];
        var menuItems = [
            {name: 'Enhet1', subs: subs1},
            {
                name: 'Enhet2', subs: [
                {name: 'sub4'},
                {name: 'sub5'},
                {name: 'sub6'}
            ]
            }
        ];

        //When
        scope.filterMenuItems(menuItems, 'sub122');

        //Then
        $timeout(function(){
            expect(menuItems[0].hide).toBe(false);
            expect(sub12.hide).toBe(false);
            expect(sub122.hide).toBe(false);
            expect(sub121.hide).toBe(true);
            expect(menuItems[1].hide).toBe(true);
        }, 100);
    }));

    it('child items from matching node should be visible', inject(function ($timeout) {
        //Given
        var sub121 = {name: 'sub121', hide: true};
        var sub122 = {name: 'sub122', hide: true};
        var sub12 = {name: 'sub12', hide: true, subs: [sub121, sub122]};
        var subs1 = [
            {name: 'sub11', hide: true},
            sub12,
            {name: 'sub13', hide: true}
        ];
        var sub22 = {name: 'sub5', hide: true};
        var menuItems = [
            {name: 'Enhet1', hide: true, subs: subs1},
            {
                name: 'Enhet2', hide: true, subs: [
                {name: 'sub4', hide: true},
                sub22,
                {name: 'sub6', hide: true}
            ]
            }
        ];

        //When
        scope.filterMenuItems(menuItems, 'Enhet1');

        //Then
        $timeout(function(){
            expect(menuItems[0].hide).toBe(false);
            expect(subs1[1].hide).toBe(false);
            expect(sub12.hide).toBe(false);
            expect(sub122.hide).toBe(false);
            expect(menuItems[1].hide).toBe(true);
            expect(sub22.hide).toBe(true);
        }, 100);
    }));


    it('node should be fully expanded if only one match is found', inject(function ($timeout) {
        //Given
        var sub121 = {name: 'sub121'};
        var sub122 = {name: 'sub122'};
        var sub12 = {name: 'sub12', subs: [sub121, sub122]};
        var subs1 = [
            {name: 'sub11'},
            sub12,
            {name: 'sub13'}
        ];
        var menuItems = [
            {name: 'Enhet1', subs: subs1},
            {
                name: 'Enhet2', subs: [
                {name: 'sub4'},
                {name: 'sub5'},
                {name: 'sub6'}
            ]
            }
        ];

        //When
        scope.filterMenuItems(menuItems, 'sub122');

        //Then
        $timeout(function(){
            expect(menuItems[0].showChildren).toBe(true);
            expect(sub12.showChildren).toBe(true);
            expect(sub122.showChildren).toBe(true);
            expect(sub121.hide).toBe(true);
            expect(menuItems[1].hide).toBe(true);
        }, 100);
    }));

    it('leaves count is counting correct when 0', inject(function () {
        //Given
        var sub11 = {name: 'sub11', allSelected: false, someSelected: false};
        var sub12 = {name: 'sub12', allSelected: false, someSelected: false};
        var menuItems = [
            {name: 'Enhet1', subs: [sub11, sub12], someSelected: false, allSelected: false}
        ];

        //When
        var leavesCount = ctrl.selectedLeavesCount({subs: menuItems});

        //Then
        expect(leavesCount).toBe(0);
    }));

    it('leaves count is counting correct when 1', inject(function () {
        //Given
        var sub11 = {name: 'sub11', allSelected: false, someSelected: false};
        var sub12 = {name: 'sub12', allSelected: true, someSelected: false};
        var menuItems = [
            {name: 'Enhet1', subs: [sub11, sub12], someSelected: true, allSelected: false}
        ];

        //When
        var leavesCount = ctrl.selectedLeavesCount({subs: menuItems});

        //Then
        expect(leavesCount).toBe(1);
    }));

    it('leaves count is counting correct when all', inject(function () {
        //Given
        var sub11 = {name: 'sub11', allSelected: true, someSelected: false};
        var sub12 = {name: 'sub12', allSelected: true, someSelected: false};
        var menuItems = [
            {name: 'Enhet1', subs: [sub11, sub12], someSelected: false, allSelected: true}
        ];

        //When
        var leavesCount = ctrl.selectedLeavesCount({subs: menuItems});

        //Then
        expect(leavesCount).toBe(2);
    }));

    it('deselect all levels when kapitel is deselected', inject(function (businessFilterFactory) {
        // Given
        businessFilterFactory.setIcd10Structure(diagnoses);
        businessFilterFactory.dataInitialized = true;
        scope.menuOptions = businessFilterFactory.icd10;
        businessFilterFactory.selectAll(businessFilterFactory.icd10);

        // When
        scope.itemClicked(A00B99, businessFilterFactory.icd10);
        businessFilterFactory.updateDiagnoses();

        //Then
        var diagnoser = businessFilterFactory.selectedDiagnoses;
        expect(diagnoser.length).toBe(1);
        expect(diagnoser).toContain(D50D89.numericalId);
    }));

    it('deselect all kategorier below unselected avsnitt', inject(function (businessFilterFactory) {
        // Given
        businessFilterFactory.setIcd10Structure(diagnoses);
        businessFilterFactory.dataInitialized = true;
        scope.menuOptions = businessFilterFactory.icd10;
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

    it('report selected kategoris', inject(function (businessFilterFactory) {
        // Given
        businessFilterFactory.setIcd10Structure(diagnoses);
        businessFilterFactory.dataInitialized = true;
        scope.menuOptions = businessFilterFactory.icd10;
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

});
