/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

(function() {
  'use strict';

  describe('helper ObjectHelper', function() {
    var ObjectHelper;

    beforeEach(module('StatisticsApp'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function(_ObjectHelper_) {
      ObjectHelper = _ObjectHelper_;
    }));

    describe('isNotEmpty', function() {
      it('is empty', function() {
        expect(ObjectHelper.isNotEmpty(null)).toBeFalsy();
        expect(ObjectHelper.isNotEmpty(undefined)).toBeFalsy();
      });

      it('is not empty', function() {
        expect(ObjectHelper.isNotEmpty('')).toBeTruthy();
        expect(ObjectHelper.isNotEmpty(true)).toBeTruthy();
      });
    });

    describe('isEmpty', function() {
      it('is empty', function() {
        expect(ObjectHelper.isEmpty(null)).toBeTruthy();
        expect(ObjectHelper.isEmpty(undefined)).toBeTruthy();
      });

      it('is not empty', function() {
        expect(ObjectHelper.isEmpty('')).toBeFalsy();
        expect(ObjectHelper.isEmpty(true)).toBeFalsy();
      });
    });
  });
})();
