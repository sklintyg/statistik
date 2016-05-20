/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

describe('Test of common functions for controllers', function() {
    'use strict';

    beforeEach(module('StatisticsApp'));

    var ControllerCommons;

    // Inject dependencies and mocks
    beforeEach(inject(function(_ControllerCommons_) {
        ControllerCommons = _ControllerCommons_;
    }));

    it('makeThousandSeparated', function() {
        expect(ControllerCommons.makeThousandSeparated()).toBe();
        expect(ControllerCommons.makeThousandSeparated(0)).toBe('0');
        expect(ControllerCommons.makeThousandSeparated(1000)).toBe('1\u00A0000');
        expect(ControllerCommons.makeThousandSeparated(9999999)).toBe('9\u00A0999\u00A0999');
        expect(ControllerCommons.makeThousandSeparated(-5000)).toBe('-5\u00A0000');
        expect(ControllerCommons.makeThousandSeparated('Fiftysix')).toBe('Fiftysix');
        expect(ControllerCommons.makeThousandSeparated(0.16)).toBe('0,16');
        expect(ControllerCommons.makeThousandSeparated(45321.16)).toBe('45\u00A0321,16');
        expect(ControllerCommons.makeThousandSeparated(0.123456)).toBe('0,123456');
    });
    
    it('getFileName', function() {
        expect(ControllerCommons.getFileName()).toMatch(/_\d{8}_\d{6}/);
        expect(ControllerCommons.getFileName(123456)).toMatch(/^123456_\d{8}_\d{6}$/);
        expect(ControllerCommons.getFileName('123456')).toMatch(/^123456_\d{8}_\d{6}$/);
        expect(ControllerCommons.getFileName('MittDiagram')).toMatch(/^MittDiagram_\d{8}_\d{6}$/);
        expect(ControllerCommons.getFileName('Mitt Diagram')).toMatch(/^Mitt_Diagram_\d{8}_\d{6}$/);
        expect(ControllerCommons.getFileName('Mitt     Diagram')).toMatch(/^Mitt_Diagram_\d{8}_\d{6}$/);
    });

    it('INTYG-1853: populateActiveEnhetsFilter with all enhets selected shows verksamhet name', function() {
        //Given
        var scope = {verksamhetName: 'VerksamhetName'};
        var enhetnames = ['OneEnhet'];

        //When
        ControllerCommons.populateActiveEnhetsFilter(scope, 'hash', true, enhetnames);

        //Then
        expect(scope.headerEnhetInfo).toMatch('VerksamhetName');
    });
    
    it('INTYG-1853: populateActiveEnhetsFilter without active filter shows verksamhet name', function() {
        //Given
        var scope = {verksamhetName: 'VerksamhetName'};
        var enhetnames = ['OneEnhet'];

        //When
        ControllerCommons.populateActiveEnhetsFilter(scope, null, false, enhetnames);

        //Then
        expect(scope.headerEnhetInfo).toMatch('VerksamhetName');
    });

    it('INTYG-1853: populateActiveEnhetsFilter with more than one (but not all) enhetes in filter shows nothing in title', function() {
        //Given
        var scope = {verksamhetName: 'VerksamhetName'};
        var enhetnames = ['OneEnhet', 'SecondEnhet'];

        //When
        ControllerCommons.populateActiveEnhetsFilter(scope, 'hash', false, enhetnames);

        //Then
        expect(scope.headerEnhetInfo).toMatch('');
    });

    it('INTYG-1853: populateActiveEnhetsFilter with more than one (but not all) enhetes in filter shows list of filtered enhets', function() {
        //Given
        var scope = {verksamhetName: 'VerksamhetName'};
        var enhetnames = ['OneEnhet', 'SecondEnhet'];

        //When
        ControllerCommons.populateActiveEnhetsFilter(scope, 'hash', false, enhetnames);

        //Then
        expect(scope.activeEnhetsFilters).toEqual(['OneEnhet', 'SecondEnhet']);
    });

    it('INTYG-1854: populateActiveEnhetsFilter with one enhet in filter shows enhet name in title', function() {
        //Given
        var scope = {verksamhetName: 'VerksamhetName'};
        var enhetnames = ['OneEnhet'];

        //When
        ControllerCommons.populateActiveEnhetsFilter(scope, 'hash', false, enhetnames);

        //Then
        expect(scope.headerEnhetInfo).toMatch('OneEnhet');
    });

    it('INTYG-1854: populateActiveEnhetsFilter with more than one enhet in filter shows no list of filtered enhets', function() {
        //Given
        var scope = {verksamhetName: 'VerksamhetName'};
        var enhetnames = ['OneEnhet'];

        //When
        ControllerCommons.populateActiveEnhetsFilter(scope, 'hash', false, enhetnames);

        //Then
        expect(scope.activeEnhetsFilters).toEqual([]);
    });

    it('isNumber', function() {
        expect(ControllerCommons.isNumber()).toBe(false);
        expect(ControllerCommons.isNumber(0)).toBe(true);
        expect(ControllerCommons.isNumber(-1)).toBe(true);
        expect(ControllerCommons.isNumber(1000000)).toBe(true);
        expect(ControllerCommons.isNumber(1.0)).toBe(true);
        expect(ControllerCommons.isNumber('1')).toBe(true);
        expect(ControllerCommons.isNumber('ett')).toBe(false);
        expect(ControllerCommons.isNumber('7ett9')).toBe(false);
        expect(ControllerCommons.isNumber({1: 3})).toBe(false);
    });
    
    it('htmlsafe', function() {
        expect(ControllerCommons.htmlsafe('f')).toBe('f');
        expect(ControllerCommons.htmlsafe('f&<')).toBe('f&amp;&lt;');
    });

    it('create query string of query params', function() {
        var emptyQueryParams = {}, queryParamsSingle = {filter: 'sjskdjdsk'},
            queryParamsMultiple = {filter: '123345', another: '122114', more: '287218723'};

        expect(ControllerCommons.createQueryStringOfQueryParams(emptyQueryParams)).toEqual('');
        expect(ControllerCommons.createQueryStringOfQueryParams(queryParamsSingle)).toEqual('filter=sjskdjdsk');
        expect(ControllerCommons.createQueryStringOfQueryParams(queryParamsMultiple)).toEqual('filter=123345&another=122114&more=287218723');
    });

    it('string with path is created either with diagnos hash or other', function() {
        var routeParamsWithDiagnosHash = {diagnosHash: '12345'};
        expect(ControllerCommons.createDiagnosHashPathOrAlternativePath(routeParamsWithDiagnosHash)).toEqual('/12345');

        var routeParamsWithGroupId = {kapitelId: 'A00-B99'};
        expect(ControllerCommons.createDiagnosHashPathOrAlternativePath(routeParamsWithGroupId)).toEqual('/kapitel/A00-B99');

        //Add a avsnittId to the routeparams with groupid
        routeParamsWithGroupId.avsnittId = 'someId';
        expect(ControllerCommons.createDiagnosHashPathOrAlternativePath(routeParamsWithGroupId)).toEqual('/kapitel/A00-B99/avsnitt/someId');

        //Add a kategoriId to the routeparams with groupid
        routeParamsWithGroupId.kategoriId = 'someKatId';
        var diagnosPath = ControllerCommons.createDiagnosHashPathOrAlternativePath(routeParamsWithGroupId);
        expect(diagnosPath).toEqual('/kapitel/A00-B99/avsnitt/someId/kategori/someKatId');

        //No route params will just produce an empty string
        expect(ControllerCommons.createDiagnosHashPathOrAlternativePath({})).toEqual('');

    });

    describe('Test check nation result', function() {
        var scope, result, called;
        var successFunction = function() {
            called = true;
        };

        beforeEach(function() {
            called = false;
            result = '';
            scope = {};
        });

        it('Nationell and empty result', function() {
            ControllerCommons.checkNationalResult(scope, result, false, false, successFunction);

            expect(called).toBeFalsy();
            expect(scope.dataLoadingError).toBeTruthy();
            expect(scope.errorPageUrl).toBe('app/views/error/statisticNotDone.html');
        });

        it('Nationell and empty result checkCache', inject(function($cacheFactory) {
            $cacheFactory.get('$http').put('test', 'hej');

            expect($cacheFactory.get('$http').info().size).toBe(1);

            ControllerCommons.checkNationalResult(scope, result, false, false, successFunction);

            expect($cacheFactory.get('$http').info().size).toBe(0);
        }));

        it('Nationell and not result', function() {
            result = {test: 'testData'};
            ControllerCommons.checkNationalResult(scope, result, false, false, successFunction);

            expect(called).toBeTruthy();
            expect(scope.errorPageUrl).toBeNull();
        });

        it('Verksamhet and empty result', function() {
            ControllerCommons.checkNationalResult(scope, result, true, false, successFunction);

            expect(called).toBeTruthy();
            expect(scope.errorPageUrl).toBeNull();
        });

        it('Landsting and empty result', function() {
            ControllerCommons.checkNationalResult(scope, result, false, true, successFunction);

            expect(called).toBeTruthy();
            expect(scope.errorPageUrl).toBeNull();
        });
    });

});
