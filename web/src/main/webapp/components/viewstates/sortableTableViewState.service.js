/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

angular.module('StatisticsApp').service('sortableTableViewState',

    function() {
      'use strict';

      var state = {};

      function _reset() {
        state.sortIndex = -1;
        state.sortReverse = true;
        state.sortedRows = [];
      }

      function _getSortIndex() {
        return state.sortIndex;
      }

      function _getSortReverse() {
        return state.sortReverse;
      }

      function _getSortedRows() {
        return state.sortedRows;
      }

      function _updateSortIndex(index) {
        if (state.sortIndex === index) {
          if (!state.sortReverse) {
            state.sortReverse = true;
            state.sortIndex = -1;
          } else {
            state.sortReverse = false;
          }
        } else {
          state.sortIndex = index;
          state.sortReverse = true;
        }
      }

      function _updateSortedRows(rows) {
        state.sortedRows = rows;
      }

      _reset();

      // Return public API for the service
      return {
        reset: _reset,
        getSortIndex: _getSortIndex,
        getSortReverse: _getSortReverse,
        getSortedRows: _getSortedRows,
        updateSortIndex: _updateSortIndex,
        updateSortedRows: _updateSortedRows
      };

    }
);
