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

angular.module('StatisticsApp.treeMultiSelector.modal', ['ui.bootstrap', 'underscore']).controller('TreeMultiSelectorModalCtrl',
    function($scope, $uibModalInstance, treeMultiSelectorUtil, $timeout, _, directiveScope) {
      'use strict';

      function init() {
        $scope.doneLoading = false;

        $scope.value = {
          multiMenuFilter: ''
        };
        if (directiveScope.runFilterMenuOnInit) {
          $scope.value.multiMenuFilter = null;
        }

        $scope.depth = 0;
        $scope.directiveScope = directiveScope;

        $scope.showLabel = !directiveScope.hideLabel;

        $timeout(function() {
          $scope.updateCounters();
          updateDoneDisabled();
          resetFilter();
          $scope.setupVisibleSubs(directiveScope.menuOptions);

          $scope.doneLoading = true;
        });
      }

      $scope.cancel = function() {
        $uibModalInstance.dismiss();
      };

      $scope.clickedDone = function() {
        $uibModalInstance.close();
      };

      $scope.setupVisibleSubs = function(menuOption) {
        if (menuOption && menuOption.subs) {
          menuOption.visibleSubs = menuOption.subs;
          menuOption.showChildren = false;
          _.each(menuOption.subs, function(sub) {
            $scope.setupVisibleSubs(sub);
          });
        }
      };

      function updateDoneDisabled() {
        var totalSelections = $scope.selectedPrimaryCounter + $scope.selectedSecondaryCounter +
            $scope.selectedTertiaryCounter + $scope.selectedQuaternaryCounter;
        $scope.doneDisabled = directiveScope.minSelections && (directiveScope.minSelections > totalSelections);
      }

      $scope.$on('selectionsChanged', function() {
        updateDoneDisabled();
      });

      $scope.doneRenderingDialog = function() {
        $scope.doneLoading = true;
      };

      $scope.recursionhelper = {
        itemclick: function(item, $event) {
          $scope.itemClicked(item);
          $event.stopPropagation();
        },

        hideclick: function(item, $event) {
          $scope.hideClicked(item);
          $event.stopPropagation();
        }
      };

      function hasNoMenuOptionsOrSubs() {
        return !directiveScope.menuOptions || !directiveScope.menuOptions.subs || directiveScope.menuOptions.subs.length === 0;
      }

      $scope.countSelectedByLevel = function() {
        return countSelectedLevels(directiveScope.menuOptions, 0);
      };

      function sumSelectedLevels(selectedLevels1, selectedLevels2) {
        for (var key in selectedLevels2) {
          if (Object.prototype.hasOwnProperty.call(selectedLevels2, key)) {
            var val = selectedLevels2[key];
            selectedLevels1[key] = selectedLevels1[key] ? selectedLevels1[key] + val : val;
          }
        }
        return selectedLevels1;
      }

      function countSelectedLevels(menuOptions, level) {
        if (!menuOptions) {
          return {};
        }
        if (!menuOptions.subs || menuOptions.subs.length === 0) {
          var resp = {};
          resp[level] = menuOptions.allSelected ? 1 : 0;
          return resp;
        }
        var startVal = {};
        startVal[level] = menuOptions.allSelected ? 1 : 0;
        return hasNoMenuOptionsOrSubs() ? 0 : _.reduce(menuOptions.subs, function(acc, sub) {
          if (!(sub.allSelected || sub.someSelected)) {
            return acc;
          }
          return sumSelectedLevels(acc, countSelectedLevels(sub, level + 1));
        }, startVal);
      }

      $scope.itemClicked = function(item) {
        if (item.allSelected) {
          $scope.deselectAll(item);
        } else {
          $scope.selectAll(item);
        }

        updateSelections();
      };

      function updateSelections() {
        $scope.updateState(directiveScope.menuOptions);
        $scope.updateCounters();
      }

      $scope.hideClicked = function(item) {
        item.showChildren = !item.showChildren;
      };

      $scope.deselectAll = function(item) {
        item.allSelected = false;
        item.someSelected = false;
        if (item.subs) {
          _.each(item.subs, function(sub) {
            $scope.deselectAll(sub);
          });
        }
      };

      $scope.selectAll = function(item) {
        item.allSelected = true;
        item.someSelected = false;
        if (item.subs) {
          _.each(item.subs, function(sub) {
            $scope.selectAll(sub);
          });
        }
      };

      function expandIfOnlyOneVisible(item) {
        if (item.visibleSubs && item.visibleSubs.length === 1) {
          var itemSub = item.visibleSubs[0];
          itemSub.showChildren = true;
          expandIfOnlyOneVisible(itemSub);
        }
      }

      function collapseAll(item) {
        for (var i = 0; item && item.visibleSubs && i < item.visibleSubs.length; i++) {
          var visibleSub = item.visibleSubs[i];
          if (visibleSub.showChildren) {
            collapseAll(visibleSub);
          }
          visibleSub.showChildren = false;
        }
      }

      $scope.filterMenuItems = function(items, text, restartFiltering) {
        var matchFunction = getIsMatchingFilterFunction(text);
        var filteredItems = $scope.filterItems(items, matchFunction, restartFiltering);
        collapseAll(filteredItems);
        expandIfOnlyOneVisible(filteredItems);
        return filteredItems;
      };

      function escapeRegExp(str) {
        return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, '\\$&');
      }

      function getIsMatchingFilterFunction(searchText) {
        if (!searchText || searchText.length === 0) {
          return function() {
            return true;
          };
        }

        var text = searchText.toLowerCase();
        if (directiveScope.ignoreCharsInSearch) {
          var re = new RegExp('([' + directiveScope.ignoreCharsInSearch + '])', 'g');
          text = text.replace(re, '');
        }
        text = escapeRegExp(text);

        var regExp = new RegExp(text, 'i');

        return function isMatchingFilter(item) {
          return regExp.test(item.visibleName);
        };
      }

      $scope.filterItems = function(item, shouldItemBeVisibleFunction, restartFiltering) {
        var isItemVisible = shouldItemBeVisibleFunction(item);
        var visibleFunction = isItemVisible ? function() {
          return true;
        } : shouldItemBeVisibleFunction;
        var subs = restartFiltering ? item.subs : item.visibleSubs;
        if (subs && subs.length > 0) {
          var filteredSubs = _.reduce(subs, function(mem, sub) {
            var filteredSub = $scope.filterItems(sub, visibleFunction, restartFiltering);
            if (filteredSub !== null) {
              mem.push(filteredSub);
            }
            return mem;
          }, []);
          item.visibleSubs = filteredSubs;
          if (filteredSubs.length > 0) {
            return item;
          }
        }

        return isItemVisible ? item : null;
      };

      function valueOr(val, orElse) {
        return val ? val : orElse;
      }

      $scope.updateCounters = function() {
        $timeout(function() {
          $scope.updateCountersNow();
        }, 1);
      };

      //The actual update without the delay. Extracted from 'updateCounters' for testing.
      $scope.updateCountersNow = function() {
        if (directiveScope.hideDepthCounters) {
            return;
        }
        var levelCount = $scope.countSelectedByLevel();
        $scope.selectedPrimaryCounter = valueOr(levelCount[1], 0);
        $scope.selectedSecondaryCounter = valueOr(levelCount[2], 0);
        $scope.selectedTertiaryCounter = valueOr(levelCount[3], 0);
        $scope.selectedQuaternaryCounter = valueOr(levelCount[4], 0);
        $scope.$emit('selectionsChanged');

        var compareValue;
        var depthLabel;

        if (directiveScope.maxDepth === 1) {
          compareValue = $scope.selectedPrimaryCounter;
          depthLabel = directiveScope.textData.firstLevelLabelText;
        } else if (directiveScope.maxDepth === 2) {
          compareValue = $scope.selectedSecondaryCounter;
          depthLabel = directiveScope.textData.secondLevelLabelText;
        } else if (directiveScope.maxDepth === 3) {
          compareValue = $scope.selectedTertiaryCounter;
          depthLabel = directiveScope.textData.thirdLevelLabelText;
        } else {
          compareValue = $scope.selectedQuaternaryCounter;
          depthLabel = directiveScope.textData.leavesLevelLabelText;
        }

        $scope.showMaxWarning = compareValue > directiveScope.maxSelections;
        $scope.depthLabel = depthLabel.toLowerCase();
      };

      $scope.updateState = function(item) {
        treeMultiSelectorUtil.updateSelectionState(item);
      };

      function resetFilter() {
        $scope.value.multiMenuFilter = '';
        // $scope.visibleMenuOptions = directiveScope.menuOptions;

        if (!hasNoMenuOptionsOrSubs()) {
          _.each(directiveScope.menuOptions.subs, function(item) {
            $scope.filterItems(item, function() {
              return true;
            }, true);
          });
        }
      }

      $scope.$watch('value.multiMenuFilter', function(value, oldValue) {
        if (value !== oldValue) {
          var restartFiltering = value.indexOf(oldValue) < 0; //Continue with existing filtering or restart using all items
          directiveScope.menuOptions = $scope.filterMenuItems(directiveScope.menuOptions, value, restartFiltering);
        }
      });

      $scope.selectVerksamhetsTyp = function(verksamhetsTyp) {
        var checked = true;

        _.each(verksamhetsTyp.units, function(businesse) {
          if (checked || businesse.verksamhetsTyper.length === 1) {
            businesse.allSelected = checked;
          } else {
            var shouldUncheck = true;
            _.each(directiveScope.verksamhetsTyper, function(type) {
              if (type !== verksamhetsTyp && type.checked &&
                  type.units.indexOf(businesse) > -1) {
                shouldUncheck = false;
              }
            });
            if (shouldUncheck) {
              businesse.allSelected = false;
            }
          }
        });

        verksamhetsTyp.checked = checked;

        updateSelections();
      };

      init();
    }
);
