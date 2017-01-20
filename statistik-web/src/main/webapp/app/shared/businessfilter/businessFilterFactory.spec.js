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

describe('Tests for business filter factory', function () {
    'use strict';

    var A00, A01, B07, D50, D70;
    var A00A09, B00B09, D50D53, D70D77, A00B99, D50D89;
    var diagnoses, businessFilter, _;

    beforeEach(module('StatisticsApp.businessFilter'));

    //Needed to get serviceinjections from this namespace
    beforeEach(module('StatisticsApp'));

    beforeEach(module(function ($provide) {
        var mockStatistics = {
            getIcd10Structure: function () { }
        };

        $provide.value('statisticsData', mockStatistics);
    }));

    beforeEach(inject(function(_businessFilterFactory_, ___) {
        businessFilter = _businessFilterFactory_;
        _ = ___; //This set the local underscore variable
    }));

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

    describe('Populate verksamheter', function () {
        it('populates verksamheter correctly', function () {
            //Given
            var verksamhetsTyper1 = [{id: 1, name: 'v1'}, {id: 2, name: 'v2'}];
            var verksamhetsTyper2 = [{id: 1, name: 'v1'}, {id: 3, name: 'v3'}];
            var business1 = {id: 1, name: 'b1', verksamhetsTyper: verksamhetsTyper1};
            var business2 = {id: 2, name: 'b2', verksamhetsTyper: verksamhetsTyper2};

            //When
            businessFilter.populateVerksamhetsTyper([business1, business2]);

            //Then
            var verksamheter = businessFilter.verksamhetsTyper;
            expect(verksamheter.length).toBe(3);
            _.each(verksamheter, function (verksamhet) {
                if (verksamhet.id === 1) {
                    expect(verksamhet.name).toBe('v1');
                    expect(verksamhet.units.length).toBe(2);
                    expect(_.find(verksamhet.units, {id: 1})).toBeDefined();
                    expect(_.find(verksamhet.units, {id: 2})).toBeDefined();
                }
                if (verksamhet.id === 2) {
                    expect(verksamhet.name).toBe('v2');
                    expect(verksamhet.units.length).toBe(1);
                    expect(_.find(verksamhet.units, {id: 1})).toBeDefined();
                    expect(_.find(verksamhet.units, {id: 2})).toBeUndefined();
                }
                if (verksamhet.id === 3) {
                    expect(verksamhet.name).toBe('v3');
                    expect(verksamhet.units.length).toBe(1);
                    expect(_.find(verksamhet.units, {id: 1})).toBeUndefined();
                    expect(_.find(verksamhet.units, {id: 2})).toBeDefined();
                }
            });
        });
        it('populated verksamheter are sorted correctly STATISTIK1103', function () {
            //Given
            var verksamhetsTyper1 = [{id: 3, name: 'Okänd verksamhetstyp'}];
            var verksamhetsTyper2 = [{id: 1, name: 'ÅÄÖ'}, {id: 2, name: 'Abc'}];
            var business1 = {id: 1, name: 'b1', verksamhetsTyper: verksamhetsTyper1};
            var business2 = {id: 2, name: 'b2', verksamhetsTyper: verksamhetsTyper2};

            //When
            businessFilter.populateVerksamhetsTyper([business1, business2]);

            //Then
            var verksamheter = businessFilter.verksamhetsTyper;
            expect(verksamheter.length).toBe(3);
            expect(verksamheter[0].name).toBe('Abc');
            expect(verksamheter[1].name).toBe('ÅÄÖ');
            expect(verksamheter[2].name).toBe('Okänd verksamhetstyp');
        });
    });

    describe('Setting Icd10 structure and selections on that structure', function () {
        it('selects no diagnoses by default when setting icd 10 structure - INTYG-1855', function () {
            // Given

            // When
            businessFilter.setIcd10Structure(diagnoses);

            //Then
            expect(A00.allSelected).not.toBeTruthy();
            expect(B07.allSelected).not.toBeTruthy();
            expect(D50.allSelected).not.toBeTruthy();
            expect(D70.allSelected).not.toBeTruthy();
            expect(A00A09.allSelected).not.toBeTruthy();
            expect(D70D77.allSelected).not.toBeTruthy();
            expect(A00B99.allSelected).not.toBeTruthy();

            expect(A00.someSelected).not.toBeTruthy();
            expect(B07.someSelected).not.toBeTruthy();
            expect(D50.someSelected).not.toBeTruthy();
            expect(D70.someSelected).not.toBeTruthy();
            expect(A00A09.someSelected).not.toBeTruthy();
            expect(D70D77.someSelected).not.toBeTruthy();
            expect(A00B99.someSelected).not.toBeTruthy();
        });

        it('can select diagnoses by id attribute', function () {
            // Given
            businessFilter.setIcd10Structure(diagnoses);

            // When
            businessFilter.selectByAttribute(businessFilter.icd10, ['D50-D89', 'B07'], 'id');

            //Then
            expect(A00.allSelected).toBeFalsy();
            expect(B07.allSelected).toBeTruthy();
            expect(D50.allSelected).toBeTruthy();
            expect(D70.allSelected).toBeTruthy();
            expect(A00A09.allSelected).toBeFalsy();
            expect(D70D77.allSelected).toBeTruthy();
            expect(A00B99.allSelected).toBeFalsy();

        });

        it('can select diagnoses by numericalId attribute', function () {
            // Given
            businessFilter.setIcd10Structure(diagnoses);

            // When
            businessFilter.selectByAttribute(businessFilter.icd10, [2, 23], 'numericalId');

            //Then
            expect(A00.allSelected).toBeFalsy();
            expect(B07.allSelected).toBeTruthy();
            expect(D50.allSelected).toBeTruthy();
            expect(D70.allSelected).toBeTruthy();
            expect(A00A09.allSelected).toBeFalsy();
            expect(D70D77.allSelected).toBeTruthy();
            expect(A00B99.allSelected).toBeFalsy();

        });
    });

    describe('Initialization of the business filter', function() {
        it('init toDate and fromDate to null', function() {
            expect(businessFilter.toDate).toBeNull('To date should be null at this stage');
            expect(businessFilter.fromDate).toBeNull('From date should be null at this stage');
        });

        it('init useDefaultPeriod to true', function() {
            expect(businessFilter.useDefaultPeriod).toBeTruthy('useDefaultPeriod should be true after reset');
        });

    });

    describe('Resetting selections', function() {
        it('resets to date and from date', function() {
            //Given
            businessFilter.toDate = new Date();
            businessFilter.fromDate = new Date();

            //When
            businessFilter.resetSelections();

            //Then
            expect(businessFilter.toDate).toBeNull('To date should be null after reset');
            expect(businessFilter.fromDate).toBeNull('From date should be null after reset');
        });

        it('resets useDefaultPeriod to true', function() {
            //Given
            businessFilter.useDefaultPeriod = false;

            //When
            businessFilter.resetSelections();

            //Then
            expect(businessFilter.useDefaultPeriod).toBeTruthy('useDefaultPeriod should be true after reset');
        });
    });

});
