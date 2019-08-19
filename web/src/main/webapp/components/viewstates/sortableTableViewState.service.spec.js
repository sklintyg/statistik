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

describe('viewstate: sortableTableViewState', function() {
  'use strict';

  beforeEach(module('StatisticsApp'));

  var sortableTableViewState;

  // Inject dependencies and mocks
  beforeEach(inject(function(_sortableTableViewState_) {
    sortableTableViewState = _sortableTableViewState_;
  }));

  describe('updateSortIndex', function() {
    it('set new index', function() {
      sortableTableViewState.updateSortIndex(1);

      expect(sortableTableViewState.getSortIndex()).toEqual(1);
      expect(sortableTableViewState.getSortReverse()).toBeTruthy();
    });

    it('set same index twice', function() {
      sortableTableViewState.updateSortIndex(1);
      expect(sortableTableViewState.getSortIndex()).toEqual(1);

      sortableTableViewState.updateSortIndex(1);

      expect(sortableTableViewState.getSortIndex()).toEqual(1);
      expect(sortableTableViewState.getSortReverse()).toBeFalsy();
    });

    it('set same index three times', function() {
      sortableTableViewState.updateSortIndex(1);
      expect(sortableTableViewState.getSortIndex()).toEqual(1);
      expect(sortableTableViewState.getSortReverse()).toBeTruthy();

      sortableTableViewState.updateSortIndex(1);

      expect(sortableTableViewState.getSortIndex()).toEqual(1);
      expect(sortableTableViewState.getSortReverse()).toBeFalsy();

      sortableTableViewState.updateSortIndex(1);
      expect(sortableTableViewState.getSortIndex()).toEqual(-1);
      expect(sortableTableViewState.getSortReverse()).toBeTruthy();
    });

  });

});
