/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
describe('Filter: ReplaceEmpty', function () {
    'use strict';

    // load the controller's module
    beforeEach(module('StatisticsApp'));

    it('has a replaceEmpty filter', inject(function($filter) {
        expect($filter('replaceEmpty')).not.toBeNull();
    }));

    it('should replace - with -1', inject(function (replaceEmptyFilter) {
        expect(replaceEmptyFilter('-')).toEqual(-1);
    }));

    it('should return value with ,', inject(function (replaceEmptyFilter) {
        expect(replaceEmptyFilter(1)).toEqual(1);
        expect(replaceEmptyFilter(1.2)).toEqual(1.2);
        expect(replaceEmptyFilter('12,23')).toEqual('12,23');
        expect(replaceEmptyFilter('123 123')).toEqual('123 123');
        expect(replaceEmptyFilter('hej89')).toEqual('hej89');
        expect(replaceEmptyFilter('hej')).toEqual('hej');
    }));
});
