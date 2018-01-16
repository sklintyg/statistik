/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

describe('Filter: Thousandseparated', function () {
    'use strict';

    // load the controller's module
    beforeEach(module('StatisticsApp'));

    it('has a thousandseparated filter', inject(function($filter) {
        expect($filter('thousandseparated')).not.toBeNull();
    }));

    it('should return the same value', inject(function (thousandseparatedFilter) {
        expect(thousandseparatedFilter('1')).toEqual('1');
        expect(thousandseparatedFilter('200')).toEqual('200');
    }));

    it('should return value with ,', inject(function (thousandseparatedFilter) {
        expect(thousandseparatedFilter('1.22')).toEqual('1,22');
        expect(thousandseparatedFilter('200.123')).toEqual('200,123');
    }));

    it('should return thousand separated value', inject(function (thousandseparatedFilter) {
        expect(thousandseparatedFilter('1000')).toEqual('1\u00A0000');
        expect(thousandseparatedFilter('2000000')).toEqual('2\u00A0000\u00A0000');
    }));

    it('should return thousand separated value with ,', inject(function (thousandseparatedFilter) {
        expect(thousandseparatedFilter('1000.20')).toEqual('1\u00A0000,20');
        expect(thousandseparatedFilter('2000000.123')).toEqual('2\u00A0000\u00A0000,123');
    }));
});
