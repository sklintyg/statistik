'use strict';

angular.module('StatisticsApp').controller('directiveTmsCtrl', [ '$scope', 'treeMultiSelectUtil', function ($scope, treeMultiSelectUtil) {
    $scope.clickedDone = function(){
        $scope.doneClicked();
    };

    $scope.recursionhelper = {
        itemclick: function (item) {
            $scope.itemClicked(item);
        },

        hideclick: function (item) {
            $scope.hideClicked(item);
        }
    };

    function selectedTertiaryCount() {
        if (!$scope.menuOptions || !$scope.menuOptions.subs || $scope.menuOptions.subs.length == 0) {
            return 0;
        }
        return _.reduce($scope.menuOptions.subs, function (memo, sub) {
            return memo + ($scope.selectedLeavesCount(sub) > 0 ? 1 : 0);
        }, 0);
    }

    function selectedSecondaryCount() {
        if (!$scope.menuOptions || !$scope.menuOptions.subs || $scope.menuOptions.subs.length == 0) {
            return 0;
        }
        return _.reduce($scope.menuOptions.subs, function (acc, item) {
            var nodeSum = _.reduce(item.subs, function (memo, sub) {
                return memo + ($scope.selectedLeavesCount(sub) > 0 ? 1 : 0);
            }, 0);
            return acc + nodeSum;
        }, 0);
    }

    function selectedLeavesCountAll() {
        if (!$scope.menuOptions) {
            return 0;
        }
        return $scope.selectedLeavesCount($scope.menuOptions);
    }

    $scope.selectedLeavesCount = function (node) {
        if (node.subs && node.subs.length > 0) {
            return _.reduce(node.subs, function (acc, item) {
                return acc + $scope.selectedLeavesCount(item);
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
        $scope.updateState($scope.menuOptions);
    };

    $scope.hideClicked = function (item) {
        item.hideChildren = !item.hideChildren;
    };

    $scope.deselectAll = function (item) {
        if (!item.hide) {
            item.allSelected = false;
            item.someSelected = false;
            if (item.subs) {
                _.each(item.subs, function (sub) {
                    $scope.deselectAll(sub);
                });
            }
        }
    };

    $scope.selectAll = function (item, selectHidden) {
        if (!item.hide || selectHidden) {
            item.allSelected = true;
            item.someSelected = false;
            if (item.subs) {
                _.each(item.subs, function (sub) {
                    $scope.selectAll(sub, selectHidden);
                });
            }
        }
    };

    function expandIfOnlyOneVisible(items) {
        var visibleItems = _.reject(items, function (item) {
            return item.hide;
        });
        if (visibleItems.length === 1) {
            var item = visibleItems[0];
            item.hideChildren = false;
            $scope.updateState(item);
            expandIfOnlyOneVisible(item.subs);
        }
    }

    $scope.filterMenuItems = function (items, text) {
        _.each(items, function(item) {$scope.updateItemHiddenState(item, getIsMatchingFilterFunction(text))});
        _.each(items, $scope.updateState);
        expandIfOnlyOneVisible(items);
    };

    function getIsMatchingFilterFunction(searchText) {
        var text = searchText.toLowerCase();
        return function isMatchingFilter(item) {
            return item.name.toLowerCase().indexOf(text) >= 0;
        }
    }

    $scope.updateItemHiddenState = function (item, shouldItemBeVisibleFunction) {
        var childVisibilityFunction = shouldItemBeVisibleFunction;
        var hide = true;
        if (shouldItemBeVisibleFunction(item)) {
            hide = false;
            childVisibilityFunction = function () { return true; }
        }
        item.hide = !!_.reduce(item.subs, function (mem, sub) {
            return $scope.updateItemHiddenState(sub, childVisibilityFunction) && mem;
        }, hide);
        return item.hide;
    };

    $scope.updateCounters = function() {
        $scope.selectedTertiaryCount = selectedTertiaryCount();
        $scope.selectedSecondaryCount = selectedSecondaryCount();
        $scope.selectedLeavesCountAll = selectedLeavesCountAll();
    };

    $scope.updateState = function (item) {
        treeMultiSelectUtil.updateSelectionState(item);
        $scope.updateCounters();
    };

    $scope.openDialogClicked = function(){
        $scope.updateCounters();
    };

}]);
