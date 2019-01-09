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

describe('helper: ArrayHelper', function() {
    'use strict';

    beforeEach(module('StatisticsApp'));

    var ArrayHelper;

    // Inject dependencies and mocks
    beforeEach(inject(function(_ArrayHelper_) {
        ArrayHelper = _ArrayHelper_;
    }));

    describe('isDifferent', function() {
        it('same empty', function() {
            var arrayOne = [];
            var arrayTwo = [];

            expect(ArrayHelper.isDifferent(arrayOne, arrayTwo)).toBeFalsy();
        });

        it('same content', function() {
            var arrayOne = ['test'];
            var arrayTwo = ['test'];

            expect(ArrayHelper.isDifferent(arrayOne, arrayTwo)).toBeFalsy();
        });

        it('different lenght', function() {
            var arrayOne = ['test'];
            var arrayTwo = [];

            expect(ArrayHelper.isDifferent(arrayOne, arrayTwo)).toBeTruthy();
        });

        it('different content', function() {
            var arrayOne = ['test'];
            var arrayTwo = ['other'];

            expect(ArrayHelper.isDifferent(arrayOne, arrayTwo)).toBeTruthy();
        });
    });

    describe('sortSwedish', function() {
        it('empty', function() {
            var unsortedArray = [];
            var sortedArray = ArrayHelper.sortSwedish(unsortedArray, 'text');

            var expectedArray = [];

            expect(sortedArray).toEqual(expectedArray);
        });

        it('one value', function() {
            var unsortedArray = [{
                text: 'hej'
            }];
            var sortedArray = ArrayHelper.sortSwedish(unsortedArray, 'text');

            var expectedArray = [{
                text: 'hej'
            }];

            expect(sortedArray).toEqual(expectedArray);
        });

        it('same value', function() {
            var unsortedArray = [{
                text: 'hej'
            }, {
                text: 'hej'
            }];
            var sortedArray = ArrayHelper.sortSwedish(unsortedArray, 'text');

            var expectedArray = [{
                text: 'hej'
            }, {
                text: 'hej'
            }];

            expect(sortedArray).toEqual(expectedArray);
        });

        it('sorted', function() {
            var unsortedArray = [{
                text: 'Ä'
            }, {
                text: 'hej'
            }];
            var sortedArray = ArrayHelper.sortSwedish(unsortedArray, 'text');

            var expectedArray = [{
                text: 'hej'
            },{
                text: 'Ä'
            }];

            expect(sortedArray).toEqual(expectedArray);
        });

        it('sorted 2', function() {
            var unsortedArray = [{
                text: 'ÅÄö'
            }, {
                text: 'ÅÄÖ'
            }];
            var sortedArray = ArrayHelper.sortSwedish(unsortedArray, 'text');

            var expectedArray = [{
                text: 'ÅÄÖ'
            },{
                text: 'ÅÄö'
            }];

            expect(sortedArray).toEqual(expectedArray);
        });

        it('sorted different length', function() {
            var unsortedArray = [{
                text: 'aaaa'
            }, {
                text: 'aaa'
            }];
            var sortedArray = ArrayHelper.sortSwedish(unsortedArray, 'text');

            var expectedArray = [{
                text: 'aaa'
            },{
                text: 'aaaa'
            }];

            expect(sortedArray).toEqual(expectedArray);
        });

        it('empty text', function() {
            var unsortedArray = [{
                text: 'a'
            }, {
                text: ''
            }];
            var sortedArray = ArrayHelper.sortSwedish(unsortedArray, 'text');

            var expectedArray = [{
                text: ''
            },{
                text: 'a'
            }];

            expect(sortedArray).toEqual(expectedArray);
        });

        it('missing text', function() {
            var unsortedArray = [{
                text: 'Ä'
            }, {

            }];
            var sortedArray = ArrayHelper.sortSwedish(unsortedArray, 'text');

            var expectedArray = [{
                text: 'Ä'
            },{

            }];

            expect(sortedArray).toEqual(expectedArray);
        });

        it('always last', function() {
            var unsortedArray = [{
                text: 'abc'
            }, {
                text: 'fgh'
            }];
            var sortedArray = ArrayHelper.sortSwedish(unsortedArray, 'text', 'abc');

            var expectedArray = [{
                text: 'fgh'
            },{
                text: 'abc'
            }];

            expect(sortedArray).toEqual(expectedArray);
        });
    });
});
