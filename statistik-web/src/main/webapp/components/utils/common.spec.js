/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

    it('INTYG-3021: populateActiveEnhetsFilter shows all verksamhet names when not processledare', inject(function(UserModel) {
        //Given
        var scope = {vgName: 'VardgivareTestName'};
        var enhetnames = ['OneEnhet'];
        UserModel.setUserAccessInfo({vgInfo: {processledare: false}});

        //When
        ControllerCommons.populateActiveEnhetsFilter(scope, enhetnames, true);

        //Then
        expect(scope.activeEnhetsFilters).toEqual(['OneEnhet']);
    }));

    it('INTYG-3021: populateActiveEnhetsFilter shows vgname when processledare with enhet-filter', inject(function(UserModel) {
        //Given
        var scope = {vgName: 'VardgivareTestName'};
        var enhetnames = ['OneEnhet'];
        UserModel.setUserAccessInfo({vgInfo: {processledare: true}});

        //When
        ControllerCommons.populateActiveEnhetsFilter(scope, enhetnames, false);

        //Then
        expect(scope.activeEnhetsFilters).toEqual(['OneEnhet']);
    }));

    it('INTYG-3021: populateActiveEnhetsFilter shows vgname when processledare without enhet-filter', inject(function(UserModel) {
        //Given
        var scope = {vgName: 'VardgivareTestName'};
        var enhetnames = ['OneEnhet'];
        UserModel.setUserAccessInfo({vgInfo: {processledare: true}});

        //When
        ControllerCommons.populateActiveEnhetsFilter(scope, enhetnames, true);

        //Then
        expect(scope.activeEnhetsFilters).toEqual(['Samtliga enheter inom vårdgivaren VardgivareTestName']);
    }));

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
            ControllerCommons.checkNationalResultAndEnableExport(scope, result, false, false, successFunction);

            expect(called).toBeFalsy();
            expect(scope.dataLoadingError).toBeTruthy();
            expect(scope.errorPageUrl).toBe('/app/views/error/statisticNotDone.html');
        });

        it('Nationell and empty result checkCache', inject(function($cacheFactory) {
            $cacheFactory.get('$http').put('test', 'hej');

            expect($cacheFactory.get('$http').info().size).toBe(1);

            ControllerCommons.checkNationalResultAndEnableExport(scope, result, false, false, successFunction);

            expect($cacheFactory.get('$http').info().size).toBe(0);
        }));

        it('Nationell and not result', function() {
            result = {test: 'testData'};
            ControllerCommons.checkNationalResultAndEnableExport(scope, result, false, false, successFunction);

            expect(called).toBeTruthy();
            expect(scope.errorPageUrl).toBeNull();
        });

        it('Verksamhet and empty result', function() {
            ControllerCommons.checkNationalResultAndEnableExport(scope, result, true, false, successFunction);

            expect(called).toBeTruthy();
            expect(scope.errorPageUrl).toBeNull();
        });

        it('Landsting and empty result', function() {
            ControllerCommons.checkNationalResultAndEnableExport(scope, result, false, true, successFunction);

            expect(called).toBeTruthy();
            expect(scope.errorPageUrl).toBeNull();
        });

        describe('getResultMessageList', function() {

            it('empty', function() {
                var result = {empty: true};
                var messageService = {getProperty: function() {
                    return 'message';
                }};
                var messages = ControllerCommons.getResultMessageList(result, messageService);

                expect(messages).toEqual([ {type: 'UNSET', severity: 'WARN', message: 'message' }]);
            });

            it('noMessage', function() {
                var result = {};
                var messageService = {};
                var messages = ControllerCommons.getResultMessageList(result, messageService);

                expect(messages).toEqual([]);
            });

            it('hasMessages', function() {
                var result = {messages: ['hej']};
                var messageService = {};
                var messages = ControllerCommons.getResultMessageList(result, messageService);

                expect(messages).toEqual(['hej']);
            });
        });
    });

});
