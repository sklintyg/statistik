/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
    var moment;
    var $route;

    // Inject dependencies and mocks
    beforeEach(inject(function(_ControllerCommons_, _moment_, _$route_) {
        ControllerCommons = _ControllerCommons_;
        moment = _moment_;
        $route = _$route_;
    }));

    describe('updateDataTable', function() {
        it('empty', function() {
            var scope = {};
            var tableData = {};

            ControllerCommons.updateDataTable(scope, tableData);

            var expected = {
                headerrows: undefined,
                rows: undefined
            };

            expect(scope.rows).toEqual(expected.rows);
            expect(scope.headerrows).toEqual(expected.headerrows);
        });

        it('one headerrow', function() {
            var scope = {};
            var tableData = {
                headers: [{
                    name: 'header'
                }],
                rows: [{
                    name: '1'
                },{
                    name: '2'
                }]
            };

            ControllerCommons.updateDataTable(scope, tableData);

            var expected = {
                headerrows: [{
                    name: 'header'
                }],
                rows: [{
                    name: '1'
                },{
                    name: '2'
                }]

            };

            expect(scope.rows).toEqual(expected.rows);
            expect(scope.headerrows).toEqual(expected.headerrows);
        });

        it('two headerrows', function() {
            var scope = {};
            var tableData = {
                headers: [{
                    name: 'header'
                }, {
                    name: 'header2'
                }],
                rows: [{
                    name: '1'
                },{
                    name: '2'
                }]
            };

            ControllerCommons.updateDataTable(scope, tableData);

            var expected = {
                headerrows: [{
                    name: 'header',
                    centerAlign: true
                }, {
                    name: 'header2'
                }],
                rows: [{
                    name: '1'
                },{
                    name: '2'
                }]

            };

            expect(scope.rows).toEqual(expected.rows);
            expect(scope.headerrows).toEqual(expected.headerrows);
        });
    });

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

    describe('removeMessages', function() {

        it('empty', function() {
            var message = [];
            var result = ControllerCommons.removeFilterMessages(message);

            expect(result).toEqual([]);
        });

        it('remove one message', function() {
            var message = [{
                message: 'test',
                type: 'UNSET'
            }, {
                message: 'filter',
                type: 'FILTER'
            }];

            var expected = [{
                message: 'test',
                type: 'UNSET'
            }];

            var result = ControllerCommons.removeFilterMessages(message);

            expect(result).toEqual(expected);
        });
    });

    describe('removeChartMessages', function() {

        it('empty', function() {
            var message = [];
            var result = ControllerCommons.removeChartMessages(message);

            expect(result).toEqual([]);
        });

        it('remove one message', function() {
            var message = [{
                message: 'test',
                type: 'CHART'
            }, {
                message: 'filter',
                type: 'FILTER'
            }];

            var expected = [{
                message: 'filter',
                type: 'FILTER'
            }];

            var result = ControllerCommons.removeChartMessages(message);

            expect(result).toEqual(expected);
        });
    });

    describe('isShowing', function() {

        var path = '';
        var location = {
            path: function() {
                return path;
            }
        };

        it('showing', function() {
            path = '/om/about';

            expect(ControllerCommons.isShowing(location, 'om')).toBeTruthy();
        });

        it('showing', function() {
            path = '/about';

            expect(ControllerCommons.isShowing(location, 'om')).toBeFalsy();
        });

        it('landsting', function() {
            path = '/landsting/';

            expect(ControllerCommons.isShowingLandsting(location)).toBeTruthy();
        });

        it('landsting false', function() {
            path = '/nationell/';

            expect(ControllerCommons.isShowingLandsting(location)).toBeFalsy();
        });

        it('verksamhet', function() {
            path = '/verksamhet/sjukfall?vgid=1';

            expect(ControllerCommons.isShowingVerksamhet(location)).toBeTruthy();
        });

        it('nationell', function() {
            path = '/nationell/';

            expect(ControllerCommons.isShowingNationell(location)).toBeTruthy();
        });

        it('not protected', function() {
            path = '/nationell/oversikt';

            expect(ControllerCommons.isShowingProtectedPage(location)).toBeFalsy();
        });

        it('protected', function() {
            path = '/verksamhet/sjukfall';

            expect(ControllerCommons.isShowingProtectedPage(location)).toBeTruthy();
        });
    });

    describe('getExportFileName', function() {
       it('empty gender & title', function() {
           $route.current = {
               title: ''
           };
           var statisticsLevel = 'base';
           var gender = '';
           var result = ControllerCommons.getExportFileName(statisticsLevel, gender);

           var date = moment().format('YYMMDD_HHmmss');

           expect(result).toEqual('base__' + date);
       });

        it('empty title', function() {

            $route.current = {
                title: ''
            };
            var statisticsLevel = 'base';
            var gender = 'män';
            var result = ControllerCommons.getExportFileName(statisticsLevel, gender);

            var date = moment().format('YYMMDD_HHmmss');

            expect(result).toEqual('base__man_' + date);
        });

        it('empty gender', function() {

            $route.current = {
                title: 'testar'
            };
            var statisticsLevel = 'base';
            var gender = '';
            var result = ControllerCommons.getExportFileName(statisticsLevel, gender);

            var date = moment().format('YYMMDD_HHmmss');

            expect(result).toEqual('base_testar_' + date);
        });

        it('all included', function() {

            $route.current = {
                title: 'testar'
            };
            var statisticsLevel = 'base';
            var gender = 'man';
            var result = ControllerCommons.getExportFileName(statisticsLevel, gender);

            var date = moment().format('YYMMDD_HHmmss');

            expect(result).toEqual('base_testar_man_' + date);
        });

        it('forbidden chars removed', function() {

            $route.current = {
                title: 'testar'
            };
            var statisticsLevel = 'ÅåÄäÖö.,=';
            var gender = 'man';
            var result = ControllerCommons.getExportFileName(statisticsLevel, gender);

            var date = moment().format('YYMMDD_HHmmss');

            expect(result).toEqual('AaAaOo._testar_man_' + date);
        });

    });

    describe('combineUrl', function() {
        it('empty query', function() {
            var urlBase = 'http://test.com';
            var queryString = '';

            var expected = 'http://test.com';

            expect(ControllerCommons.combineUrl(urlBase, queryString)).toEqual(expected);
        });

        it('query with ?', function() {
            var urlBase = 'http://test.com';
            var queryString = '?hej=hej';

            var expected = 'http://test.com?hej=hej';

            expect(ControllerCommons.combineUrl(urlBase, queryString)).toEqual(expected);
        });

        it('base with ?', function() {
            var urlBase = 'http://test.com?';
            var queryString = 'hej=hej';

            var expected = 'http://test.com?hej=hej';

            expect(ControllerCommons.combineUrl(urlBase, queryString)).toEqual(expected);
        });

        it('both with ?', function() {
            var urlBase = 'http://test.com?old=old';
            var queryString = '?hej=hej';

            var expected = 'http://test.com?old=old&hej=hej';

            expect(ControllerCommons.combineUrl(urlBase, queryString)).toEqual(expected);
        });
    });

    describe('formatOverViewTablePDF', function() {

        var filter = function(value) {
            return value;
        };

        it('empty', function() {
            var rows = [];

            var tableData = ControllerCommons.formatOverViewTablePDF(filter, rows);

            var expected = [];

            expect(tableData).toEqual(expected);
        });

        it('one row', function() {
            var rows = [{
                color: '#fff',
                name: 'test',
                quantity: '1',
                alternation: '50'
            }];

            var tableData = ControllerCommons.formatOverViewTablePDF(filter, rows);

            var expected = [['#fff', 'test', '1', '50 %']];

            expect(tableData).toEqual(expected);
        });
    });

});
