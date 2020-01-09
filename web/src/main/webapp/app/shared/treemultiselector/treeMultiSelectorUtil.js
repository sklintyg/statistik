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
angular.module('StatisticsApp.treeMultiSelector.util', ['underscore'])
.factory('treeMultiSelectorUtil', ['_',
  function(_) {
    'use strict';

    var externalApi = {};

    externalApi.updateSelectionState = function(item) {
      if (item.subs && item.subs.length !== 0) {
        var someSelected = false;
        var allSelected = true;
        _.each(item.subs, function(sub) {
          externalApi.updateSelectionState(sub);
          someSelected = someSelected || sub.someSelected || sub.allSelected;
          allSelected = allSelected && sub.allSelected;
        });
        if (allSelected) {
          item.allSelected = true;
          item.someSelected = false;
        } else {
          item.allSelected = false;
          item.someSelected = !!someSelected;
        }
      }
    };

    return externalApi;

  }]);
