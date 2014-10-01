'use strict';

app.filterCtrl = function ($scope, $rootScope, statisticsData, businessFilter) {

    $scope.selectedGeography = {
        selectedBusinessIds : businessFilter.geographyBusinessIds
    }

    $scope.selectedVerksamhet = {
        selectedVerksamhetsTypIds : businessFilter.verksamhetsTypIds
    }

    $scope.geography = function () {
        return businessFilter.geography;
    }

    $scope.businesses = function () {
        return businessFilter.businesses;
    }

    $scope.verksamhetsTyper = function () {
        return businessFilter.verksamhetsTyper;
    }

    $scope.useSmallGUI = function () {
        return businessFilter.useSmallGUI();
    }

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

    $scope.selectedTertiaryCount = function (node) {
        var c = 0;
        ControllerCommons.map(node.subs, function (item) {
            if ($scope.selectedLeavesCount(item) > 0) {
                c++;
            }
            ;
        });
        return c;
    };

    $scope.selectedSecondaryCount = function (node) {
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

    $scope.collectGeographyIds = function (node) {
        var returnList = [];
        if (node.subs) {
            if (node.allSelected || node.someSelected ) {
                ControllerCommons.map(node.subs, function (item) {
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
        var selectedBusinessSet = {};
        for (var i = 0; i < businessFilter.verksamhetsTypIds.length; i++) {
            var selectedVerksamhetsId = businessFilter.verksamhetsTypIds[i];
            for (var j = 0; j < businessFilter.businesses.length; j++) {
                var business = businessFilter.businesses[j];
                for (var k = 0; k < business.verksamhetsTyper.length; k++) {
                    var verksamhetsTyp = business.verksamhetsTyper[k];
                    if (verksamhetsTyp.id === selectedVerksamhetsId) {
                        selectedBusinessSet[business.id] = business;
                    }
                }
            }
        }
        var selectedBusinessIds = [];
        var id;
        for (id in selectedBusinessSet) {
            if (selectedBusinessSet.hasOwnProperty(id)) {
                selectedBusinessIds.push(id);
            }
        }
        return selectedBusinessIds;
    }

    $scope.findCut = function (businessIds1, businessIds2) {
        var returnIds = [];
        for (var i = 0; i < businessIds1.length; i++) {
            var id = businessIds1[i];
            if (businessIds2.indexOf(id) >= 0) {
                returnIds.push(id);
            }
        }
        return returnIds;
    }

    $scope.makeUnitSelection = function () {
        var geographyBusinessIds;
        if (businessFilter.useSmallGUI()) {
            geographyBusinessIds = businessFilter.geographyBusinessIds;
        } else {
            geographyBusinessIds = $scope.collectGeographyIds(businessFilter.geography);
        }
        var verksamhetsBusinessIds = $scope.collectVerksamhetsIds();
        businessFilter.selectedBusinesses = $scope.findCut(geographyBusinessIds, verksamhetsBusinessIds);
        $rootScope.$broadcast('filterChange', '');
    }

    selectAll(businessFilter.geography);
    updateState(businessFilter.geography);
};
