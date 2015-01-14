'use strict';

angular.module('StatisticsApp').controller('directiveTmsCtrl', [ '$scope', function ($scope) {
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
        } else if (item.someSelected) {
            $scope.selectAll(item);
        } else {
            $scope.selectAll(item);
        }
        $scope.updateState($scope.menuOptions.subs);
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
        var searchText = text.toLowerCase();
        var mappingFunc = function (item) {
            if (item.subs) {
                _.each(item.subs, mappingFunc);
            }
            item.hide = $scope.isItemHidden(item, searchText);
        };
        _.each(items, mappingFunc);
        _.each(items, $scope.updateState);
        expandIfOnlyOneVisible(items);
    };

    $scope.isItemHidden = function (item, searchText) {
        if (item.name.toLowerCase().indexOf(searchText) >= 0) {
            return false;
        }
        if (!item.subs) {
            return true;
        }
        return _.all(item.subs, function (sub) {
            return $scope.isItemHidden(sub, searchText);
        });
    };

    $scope.updateCounters = function() {
        $scope.selectedTertiaryCount = selectedTertiaryCount();
        $scope.selectedSecondaryCount = selectedSecondaryCount();
        $scope.selectedLeavesCountAll = selectedLeavesCountAll();
    };

    $scope.updateState = function (item) {
        if (item.subs && item.subs.length != 0) {
            var someSelected = false;
            var allSelected = true;
            _.each(item.subs, function (sub) {
                if (!sub.hide) {
                    $scope.updateState(sub);
                    someSelected = someSelected || sub.someSelected || sub.allSelected;
                    allSelected = allSelected && sub.allSelected;
                }
            });
            if (allSelected) {
                item.allSelected = true;
                item.someSelected = false;
            } else {
                item.allSelected = false;
                item.someSelected = someSelected ? true : false;
            }
        }
        $scope.updateCounters();
    };

    $scope.openDialogClicked = function(){
        $scope.updateCounters();
    };

}]);
