/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
'use strict';

angular.module('StatisticsApp.treeMultiSelector.controller', [])
    .controller('treeMultiSelectorCtrl', ['$scope', 'treeMultiSelectorUtil', '$timeout', function ($scope, treeMultiSelectorUtil, $timeout) {

        var self = this;

        $scope.clickedDone = function () {
            $scope.dialogOpen = false;
            $scope.doneClicked();
        };

        $scope.doneRenderingDialog = function () {
            $scope.$parent.doneLoading = true;
        };

        $scope.recursionhelper = {
            itemclick: function (item) {
                $scope.itemClicked(item);
            },

            hideclick: function (item) {
                $scope.hideClicked(item);
            }
        };

        function hasNoMenuOptionsOrSubs() {
            return !$scope.menuOptions || !$scope.menuOptions.subs || $scope.menuOptions.subs.length === 0;
        }

        function selectedTertiaryCount() {
            return hasNoMenuOptionsOrSubs() ? 0 : _.reduce($scope.menuOptions.subs, function (memo, sub) {
                return memo + (self.selectedLeavesCount(sub) > 0 ? 1 : 0);
            }, 0);
        }

        function selectedSecondaryCount() {
            return hasNoMenuOptionsOrSubs() ? 0 : _.reduce($scope.menuOptions.subs, function (acc, item) {
                var nodeSum = _.reduce(item.subs, function (memo, sub) {
                    return memo + (self.selectedLeavesCount(sub) > 0 ? 1 : 0);
                }, 0);
                return acc + nodeSum;
            }, 0);
        }

        function selectedLeavesCountAll() {
            return !$scope.menuOptions ? 0 : self.selectedLeavesCount($scope.menuOptions);
        }

        self.selectedLeavesCount = function selectedLeavesCount(node) {
            if (node.subs && node.subs.length > 0) {
                return _.reduce(node.subs, function (acc, item) {
                    return acc + self.selectedLeavesCount(item);
                }, 0);
            } else {
                return node.allSelected ? 1 : 0;
            }
        };

        $scope.itemClicked = function (item) {
            if (item.allSelected) {
                $scope.deselectAll(item);
            } else {
                $scope.selectAll(item);
            }
            $scope.updateCounters();
            $scope.updateState($scope.menuOptions);
        };

        $scope.hideClicked = function (item) {
            item.hideChildren = !item.hideChildren;
        };

        $scope.deselectAll = function (item) {
            item.allSelected = false;
            item.someSelected = false;
            if (item.subs) {
                _.each(item.subs, function (sub) {
                    $scope.deselectAll(sub);
                });
            }
        };

        $scope.selectAll = function (item) {
            item.allSelected = true;
            item.someSelected = false;
            if (item.subs) {
                _.each(item.subs, function (sub) {
                    $scope.selectAll(sub);
                });
            }
        };

        function expandIfOnlyOneVisible(items) {
            var visibleItems = _.reject(items, function (item) {
                return item.hide;
            });
            if (visibleItems.length === 1) {
                var item = visibleItems[0];
                item.hideChildren = false;
                expandIfOnlyOneVisible(item.subs);
            }
        }

        var currentFiltering = null;
        $scope.filterMenuItems = function (items, text) {
            if (currentFiltering !== null) {
                $timeout.cancel(currentFiltering);
            }
            currentFiltering = $timeout(function () {
                _.each(items, function (item) {
                    $scope.updateItemHiddenState(item, getIsMatchingFilterFunction(text));
                });
                expandIfOnlyOneVisible(items);
                $scope.$evalAsync();
                currentFiltering = null;
            }, 0, false);
        };

        function getIsMatchingFilterFunction(searchText) {
            var text = searchText.toLowerCase();
            return function isMatchingFilter(item) {
                return item.name.toLowerCase().indexOf(text) >= 0;
            };
        }

        $scope.updateItemHiddenState = function (item, shouldItemBeVisibleFunction) {
            var childVisibilityFunction = shouldItemBeVisibleFunction;
            var hide = true;
            if (shouldItemBeVisibleFunction(item)) {
                hide = false;
                childVisibilityFunction = function () {
                    return true;
                };
            }
            item.hide = !!_.reduce(item.subs, function (mem, sub) {
                return $scope.updateItemHiddenState(sub, childVisibilityFunction) && mem;
            }, hide);
            return item.hide;
        };

        $scope.updateCounters = function () {
            $scope.selectedTertiaryCount = selectedTertiaryCount();
            $scope.selectedSecondaryCount = selectedSecondaryCount();
            $scope.selectedLeavesCountAll = selectedLeavesCountAll();
        };

        $scope.updateState = function (item) {
            treeMultiSelectorUtil.updateSelectionState(item);
        };

        $scope.openDialogClicked = function () {
            $scope.$parent.doneLoading = false;
            $timeout(function () {
                $scope.dialogOpen = true;
                $scope.updateCounters();
                $timeout(function () {
                    $scope.$parent.doneLoading = true;
                }, 1, false);
            }, 1);
        };

        $scope.dialogOpen = false;

    }]);
