'use strict';

app.filterCtrl = function ($scope, $rootScope, statisticsData, businessFilter, _) {

    $scope.businessFilter = businessFilter;

    $scope.recursionhelper = {
        itemclick: function (item, itemRoot) {
            $scope.itemClicked(item, itemRoot);
        }
    };

    $scope.itemClicked = function (item, itemRoot) {
        if (item.allSelected) {
            businessFilter.deselectAll(item);
        } else if (item.someSelected) {
            businessFilter.selectAll(item);
        } else {
            businessFilter.selectAll(item);
        }
        updateState(itemRoot);
    }

    function isItemHidden(item, searchText) {
        if (item.name.toLowerCase().indexOf(searchText) >= 0) {
            return false;
        }
        if (!item.subs) {
            return true;
        }
        _.each(item.subs, function (sub) {
            if (!isItemHidden(sub, searchText)) {
                return false;
            }
        });
        return true;
    }

    function updateState(item) {
        if (item.subs) {
            var someSelected = false;
            var allSelected = true;
            _.each(item.subs, function (sub) {
                var currItem = sub;
                if (!currItem.hide) {
                    updateState(currItem);
                    someSelected = someSelected || currItem.someSelected || currItem.allSelected;
                    allSelected = allSelected && currItem.allSelected;
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
    }

    $scope.filterMenuItems = function (items, text) {
        var searchText = text.toLowerCase();
        var mappingFunc = function (item) {
            if (item.subs) {
                _.each(item.subs, mappingFunc);
            }
            item.hide = isItemHidden(item, searchText);
        };
        _.each(items, mappingFunc);
        _.each(items, businessFilter.updateState);
    }

    $scope.selectedTertiaryCount = function (node) {
        return _.reduce(node.subs, function (memo, sub) {
            return memo + ($scope.selectedLeavesCount(sub) > 0 ? 1 : 0)
        }, 0);
    };

    $scope.selectedSecondaryCount = function (node) {
        var c = 0;
        _.each(node.subs, function (item) {
            c += _.reduce(item.subs, function (memo, sub) {
                return memo + ($scope.selectedLeavesCount(sub) > 0 ? 1 : 0)
            }, 0);
        });
        return c;
    };

    $scope.selectedLeavesCount = function (node) {
        var c = 0;
        if (node.subs) {
            _.each(node.subs, function (item) {
                c += $scope.selectedLeavesCount(item);
            });
        } else {
            c += node.allSelected ? 1 : 0;
        }
        return c;
    };

    $scope.collectGeographyIds = function (node) {
        var returnList = [];
        if (node.subs) {
            if (node.allSelected || node.someSelected ) {
                _.each(node.subs, function (item) {
                    returnList = Array.concat(returnList, $scope.collectGeographyIds(item));
                });
            }
        } else {
            if (node.allSelected) {
                returnList = [node.id];
            }
        }
        return returnList;
    }

    $scope.collectVerksamhetsIds = function () {
        var matchingBusinesses = _.filter(businessFilter.businesses, function (business) {
            return _.some(business.verksamhetsTyper, function (verksamhetsTyp) {
                return _.contains(businessFilter.verksamhetsTypIds, verksamhetsTyp.id);
            });
        });
        return _.pluck(matchingBusinesses, 'id');
    }

    $scope.makeUnitSelection = function () {
        var geographyBusinessIds = businessFilter.useSmallGUI() ? businessFilter.geographyBusinessIds : $scope.collectGeographyIds(businessFilter.geography);
        var verksamhetsBusinessIds = $scope.collectVerksamhetsIds();
        businessFilter.selectedBusinesses = _.intersection(geographyBusinessIds, verksamhetsBusinessIds);
        $rootScope.$broadcast('filterChange', '');
    }

};
