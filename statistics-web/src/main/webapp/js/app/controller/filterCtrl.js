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
        for (var i = 0; i < item.subs.length; i++) {
            if (!isItemHidden(item.subs[i], searchText)) {
                return false;
            }
        }
        return true;
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

    $scope.selectedTertiaryCount = function (node) {
        var c = 0;
        _.each(node.subs, function (item) {
            if ($scope.selectedLeavesCount(item) > 0) {
                c++;
            }
        });
        return c;
    };

    $scope.selectedSecondaryCount = function (node) {
        var c = 0;
        _.each(node.subs, function (item) {
            _.each(item.subs, function (item) {
                if ($scope.selectedLeavesCount(item) > 0) {
                    c++;
                }
            });
        });
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
