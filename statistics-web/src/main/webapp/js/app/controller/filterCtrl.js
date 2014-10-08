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
                if (!sub.hide) {
                    updateState(sub);
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

    $scope.collectGeographyIds = function (node) {
        if (node.subs) {
            if (node.allSelected || node.someSelected ) {
                return _.reduce(node.subs, function (acc, item) {
                    return acc.concat($scope.collectGeographyIds(item));
                }, []);
            }
            return [];
        } else {
           return node.allSelected ? [node.id] : [];
        }
    }

    $scope.collectVerksamhetsIds = function () {
        var matchingBusinesses = _.filter(businessFilter.businesses, function (business) {
            return _.any(business.verksamhetsTyper, function (verksamhetsTyp) {
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
