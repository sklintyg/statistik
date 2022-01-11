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
angular.module('StatisticsApp.treeMultiSelector.util', ['underscore'])
.factory('treeMultiSelectorUtil', ['_',
  function(_) {
    'use strict';

    var externalApi = {};

    externalApi.updateSelectionState = function(item) {
      externalApi.updateCareUnits(item);
      externalApi.updateSelectionStates(item);
    };

    externalApi.updateSelectionStates = function(item) {
      if (item.subs && item.subs.length !== 0) {
        var someSelected = false;
        var allSelected = true;
        _.each(item.subs, function(sub) {
          externalApi.updateSelectionStates(sub);
          someSelected = someSelected || sub.someSelected || sub.allSelected || (externalApi.isCareUnit(sub) ? sub.isSelected : false);
          allSelected = allSelected && (externalApi.isCareUnitWithNoUnits(sub) ? true : sub.allSelected) &&
              (externalApi.isCareUnit(sub) ? sub.isSelected : true);
        });
        if (!externalApi.isDistributedCareUnit(item)) {
          if (allSelected) {
            item.allSelected = true;
            item.someSelected = false;
          } else {
            if (item.isSelected && !allSelected) {
              item.isSelected = false;
            }
            item.allSelected = false;
            item.someSelected = !!someSelected;
          }
        }
      }
    };

    externalApi.updateCareUnits = function(item) {
      if (!item.subs || item.subs.length === 0 || !externalApi.isCounty(item.subs[0])) {
        return;
      }

      var distributedCareUnits = externalApi.getDistributedCareUnits(item);
      var sortedDistributedUnits = externalApi.getDistributedUnits(distributedCareUnits);

      _.each(distributedCareUnits, function(careUnit) {
        var someSelected = false;
        var allSelected = true;
        _.each(_.find(sortedDistributedUnits, {careUnitId: careUnit.id}).units, function(unit) {
          someSelected = someSelected || unit.someSelected || unit.allSelected;
          allSelected = allSelected && unit.allSelected;
        });

        careUnit.allSelected = allSelected;
        careUnit.someSelected = allSelected ? false : someSelected;
        if (careUnit.isSelected && !allSelected) {
          careUnit.isSelected = false;
        }
      });
    };

    externalApi.getDistributedCareUnits = function(item) {
      var distributedCareUnits = [];
      _.each(item.subs, function(county) {
        _.each(county.subs, function(munip) {
          distributedCareUnits =  _.concat(distributedCareUnits, _.filter(munip.subs, {distributedCareUnit: true}));
        });
      });
      return distributedCareUnits;
    };

    externalApi.getDistributedUnits = function(distributedCareUnits) {
      var sortedDistributedUnits = [];
      _.each(distributedCareUnits, function(careUnit) {
        var existingCareUnit = _.find(sortedDistributedUnits, {careUnitId: careUnit.id});
        if (existingCareUnit) {
          existingCareUnit.units = _.concat(existingCareUnit.units, careUnit.subs);
        } else {
          sortedDistributedUnits.push({careUnitId: careUnit.id, units: careUnit.subs});
        }
      });
      return sortedDistributedUnits;
    };

    externalApi.isCareUnit = function(item) {
      return item.numericalId && typeof item.numericalId === 'string' && item.numericalId.indexOf('vardenhet') > -1;
    };

    externalApi.isCounty = function(item) {
      return item.numericalId && typeof item.numericalId === 'string' && item.numericalId.indexOf('county') > -1;
    };

    externalApi.isDistributedCareUnit = function(item) {
      return externalApi.isCareUnit(item) && item.distributedCareUnit;
    };

    externalApi.isCareUnitWithNoUnits = function(item) {
      return externalApi.isCareUnit(item) &&  item.subs.length === 0;
    };

    return externalApi;
  }]);
