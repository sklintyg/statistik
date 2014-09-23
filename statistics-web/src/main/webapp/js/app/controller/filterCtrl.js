'use strict';

app.filterCtrl = function ($scope, $rootScope, statisticsData, businessFilter) {

    $scope.selectedBusinesses = businessFilter.selectedBusinesses;

    $scope.geography = businessFilter.getGeography();

    $scope.businesses = businessFilter.getBusinesses;

    $scope.recursionhelper = {
        itemclick: function (item, itemRoot) {
            $scope.itemClicked(item, itemRoot);
        }
    };

    $scope.itemClicked = function (item, itemRoot) {
        if (item.allSelected) {
            deselectAll(item);
        } else if (item.someSelected) {
            selectAll(item);
        } else {
            selectAll(item);
        }
        updateState(itemRoot);
    }

    function deselectAll(item) {
        if (!item.hide) {
            item.allSelected = false;
            item.someSelected = false;
            if (item.subs) {
                for (var i = 0; i < item.subs.length; i++) {
                    deselectAll(item.subs[i]);
                }
            }
        }
    }

    function selectAll(item) {
        if (!item.hide) {
            item.allSelected = true;
            item.someSelected = false;
            if (item.subs) {
                for (var i = 0; i < item.subs.length; i++) {
                    selectAll(item.subs[i]);
                }
            }
        }
    }

    function updateState(item) {
        if (item.subs) {
            var someSelected = false;
            var allSelected = true;
            for (var i = 0; i < item.subs.length; i++) {
                var currItem = item.subs[i];
                if (!currItem.hide) {
                    updateState(currItem);
                    someSelected = someSelected || currItem.someSelected || currItem.allSelected;
                    allSelected = allSelected && currItem.allSelected;
                }
            }
            if (allSelected) {
                item.allSelected = true;
                item.someSelected = false;
            } else {
                item.allSelected = false;
                item.someSelected = someSelected ? true : false;
            }
        }
    }

    function isItemHidden(item, searchText) {
        if (item.name.toLowerCase().indexOf(searchText) >= 0) {
            return false;
        }
        if (!item.subs) {
            return true;
        }
        for (var i = 0; i < item.subs.length; i++) {
            if (!isItemHidden(item.subs[i], searchText)) {
                return false;
            }
        }
        return true;
    }

    $scope.filterMenuItems = function (items, text) {
        var searchText = text.toLowerCase();
        var mappingFunc = function (item) {
            if (item.subs) {
                ControllerCommons.map(item.subs, mappingFunc);
            }
            item.hide = isItemHidden(item, searchText);
        };
        ControllerCommons.map(items, mappingFunc);
        ControllerCommons.map(items, updateState);
    }

    $scope.selectedLeavesCount = function (node) {
        var c = 0;
        if (node.subs) {
            ControllerCommons.map(node.subs, function (item) {
                c += $scope.selectedLeavesCount(item);
            });
        } else {
            c += node.allSelected ? 1 : 0;
        }
        return c;
    };

    $scope.selectedKapitelCount = function (node) {
        var c = 0;
        ControllerCommons.map(node.subs, function (item) {
            if ($scope.selectedLeavesCount(item) > 0) {
                c++;
            }
            ;
        });
        return c;
    };

    $scope.selectedAvsnittCount = function (node) {
        var c = 0;
        ControllerCommons.map(node.subs, function (item) {
            ControllerCommons.map(item.subs, function (item) {
                if ($scope.selectedLeavesCount(item) > 0) {
                    c++;
                }
                ;
            });
        });
        return c;
    };

    $scope.collectSelectedItems = function (node) {
        var returnList = [];
        if (node.subs) {
            if (node.allSelected || node.someSelected ) {
                ControllerCommons.map(node.subs, function (item) {
                    returnList = Array.concat(returnList, $scope.collectSelectedItems(item));
                });
            }
        } else {
            if (node.allSelected) {
                returnList = [node];
            }
        }
        return returnList;
    }

    $scope.makeUnitSelection = function () {
        if ($scope.businesses() >= 10) {
            businessFilter.setSelectedBusinesses($scope.collectSelectedIds($scope.geography));
        } else {
            businessFilter.setSelectedBusinesses($scope.selectedBusinesses);
        }
        $rootScope.$broadcast('filterChange', businessFilter.getSelectedBusinesses().length);
    }

};
