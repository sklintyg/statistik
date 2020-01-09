/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

describe('viewstate: FilterViewState', function() {
  'use strict';

  beforeEach(module('StatisticsApp'));

  var filterViewState;
  var _;

  // Inject dependencies and mocks
  beforeEach(inject(function(_filterViewState_, ___) {
    filterViewState = _filterViewState_;
    _ = ___;
  }));

  describe('set state', function() {
    it('undefined', function() {

      var oldState = _.cloneDeep(filterViewState.get());

      filterViewState.set(undefined);

      var state = filterViewState.get();

      expect(state).toEqual(oldState);
    });

    it('empty', function() {
      var oldState = _.cloneDeep(filterViewState.get());
      var newState = {};

      filterViewState.set(newState);

      var state = filterViewState.get();

      expect(state).toEqual(oldState);
    });

    it('only intygstyper', function() {
      var newState = {
        intygstyper: true
      };

      filterViewState.set(newState);

      var state = filterViewState.get();

      expect(state).toEqual({
        intygstyper: true,
        sjukskrivningslangd: true,
        messages: []
      });
    });

    it('only sjukskrivningslangd', function() {
      var newState = {
        sjukskrivningslangd: false
      };

      filterViewState.set(newState);

      var state = filterViewState.get();

      expect(state).toEqual({
        intygstyper: false,
        sjukskrivningslangd: false,
        messages: []
      });
    });

    it('both', function() {
      var newState = {
        sjukskrivningslangd: false,
        intygstyper: true
      };

      filterViewState.set(newState);

      var state = filterViewState.get();

      expect(state).toEqual({
        intygstyper: true,
        sjukskrivningslangd: false,
        messages: []
      });
    });

    it('dont change messages', function() {
      filterViewState.setMessages([{
        type: 'FILTER',
        text: 'test'
      }]);

      var newState = {
        sjukskrivningslangd: false,
        intygstyper: true
      };

      filterViewState.set(newState);

      var state = filterViewState.get();

      expect(state).toEqual({
        intygstyper: true,
        sjukskrivningslangd: false,
        messages: [{
          type: 'FILTER',
          text: 'test'
        }]
      });
    });
  });

  describe('setMessages', function() {
    it('normal', function() {
      filterViewState.setMessages([{
        type: 'FILTER',
        text: 'test'
      }]);

      var state = filterViewState.get();

      expect(state).toEqual({
        intygstyper: false,
        sjukskrivningslangd: true,
        messages: [{
          type: 'FILTER',
          text: 'test'
        }]
      });
    });

    it('no filter message', function() {
      filterViewState.setMessages([{
        type: 'UNSET',
        text: 'test'
      }]);

      var state = filterViewState.get();

      expect(state.messages).toEqual([]);
    });

    it('both', function() {
      filterViewState.setMessages([{
        type: 'UNSET',
        text: 'test'
      }, {
        type: 'FILTER',
        text: 'filter'
      }]);

      var state = filterViewState.get();

      expect(state.messages).toEqual([{
        type: 'FILTER',
        text: 'filter'
      }]);
    });
  });

});
