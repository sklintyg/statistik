'use strict';

angular.module('StatisticsApp').factory('businessFilter', function(_) {
    var businessFilter = {};

    businessFilter.reset = function () {
        businessFilter.dataInitialized = false;
        businessFilter.permanentFilter = false;

        businessFilter.businesses = [];
        businessFilter.selectedBusinesses = [];

        businessFilter.geography = { subs: [] };
        businessFilter.geographyBusinessIds = [];

        businessFilter.verksamhetsTyper = [];
        businessFilter.verksamhetsTypIds = [];
    }
    businessFilter.reset();

    businessFilter.itemClicked = function (item, itemRoot) {
        if (item.allSelected) {
            businessFilter.deselectAll(item);
        } else if (item.someSelected) {
            businessFilter.selectAll(item);
        } else {
            businessFilter.selectAll(item);
        }
        businessFilter.updateState(itemRoot);
    };

    businessFilter.getSelectedBusinesses = function (samePage) {
        if (!businessFilter.dataInitialized) {
            return null;
        }
        if (samePage || businessFilter.permanentFilter) {
            return businessFilter.selectedBusinesses;
        }
        return null;
    };

    businessFilter.resetSelections = function() {
        if (!businessFilter.permanentFilter) {
            businessFilter.selectedBusinesses.length = 0;
            businessFilter.verksamhetsTypIds.length = 0;
            _.each(businessFilter.businesses, function (business) {
                businessFilter.selectedBusinesses.push(business.id);
            });
            _.each(businessFilter.verksamhetsTyper, function (verksamhetsTyp) {
                businessFilter.verksamhetsTypIds.push(verksamhetsTyp.id);
            });
            businessFilter.selectAll(businessFilter.geography, true);
        }
    };

    businessFilter.loggedIn = function (businesses) {
        if (!businessFilter.dataInitialized) {
            businessFilter.businesses = businesses;
            if (!businessFilter.useSmallGUI()) {
                businessFilter.populateGeography(businesses);
            }
            businessFilter.populateVerksamhetsTyper(businesses);
            businessFilter.resetSelections();
            businessFilter.dataInitialized = true;
        }
    };

    businessFilter.loggedOut = function () {
        businessFilter.reset();
    };

    businessFilter.useSmallGUI = function () {
        return businessFilter.businesses.length <= 10;
    };

    businessFilter.populateGeography = function (businesses) {
        _.each(businesses, function (business) {
            var county = _.findWhere(businessFilter.geography.subs, { id: business.lansId });
            if (!county) {
                county = { id: business.lansId, name: business.lansName, subs: [] };
                businessFilter.geography.subs.push(county);
            }

            var munip = _.findWhere(county.subs, { id: business.kommunId });
            if (!munip) {
                munip = { id: business.kommunId, name: business.kommunName, subs: [] };
                county.subs.push(munip);
            }

            munip.subs.push(business);
        });
    };

    businessFilter.populateVerksamhetsTyper = function (businesses) {
        var verksamhetsTypSet = {};
        _.each(businesses, function (business) {
            _.each(business.verksamhetsTyper, function (verksamhetsTyp) {
                verksamhetsTypSet[verksamhetsTyp.id] = verksamhetsTyp;
            });
        });
        businessFilter.verksamhetsTyper = _.values(verksamhetsTypSet);
    };

    businessFilter.deselectAll = function (item) {
        if (!item.hide) {
            item.allSelected = false;
            item.someSelected = false;
            if (item.subs) {
                _.each(item.subs, function (sub) {
                    businessFilter.deselectAll(sub);
                });
            }
        }
    };

    businessFilter.selectAll = function (item, selectHidden) {
        if (!item.hide || selectHidden) {
            item.allSelected = true;
            item.someSelected = false;
            if (item.subs) {
                _.each(item.subs, function (sub) {
                    businessFilter.selectAll(sub, selectHidden);
                });
            }
        }
    };

    businessFilter.selectedTertiaryCount = function (node) {
        return _.reduce(node.subs, function (memo, sub) {
            return memo + (businessFilter.selectedLeavesCount(sub) > 0 ? 1 : 0)
        }, 0);
    };

    businessFilter.selectedSecondaryCount = function (node) {
        return _.reduce(node.subs, function (acc, item) {
            var nodeSum = _.reduce(item.subs, function (memo, sub) {
                return memo + (businessFilter.selectedLeavesCount(sub) > 0 ? 1 : 0)
            }, 0);
            return acc + nodeSum;
        }, 0);
    };

    businessFilter.selectedLeavesCount = function (node) {
        if (node.subs) {
            return _.reduce(node.subs, function (acc, item) {
                return acc + businessFilter.selectedLeavesCount(item);
            }, 0);
        } else {
            return node.allSelected ? 1 : 0;
        }
    };

    businessFilter.updateState = function (item) {
        if (item.subs) {
            var someSelected = false;
            var allSelected = true;
            _.each(item.subs, function (sub) {
                if (!sub.hide) {
                    businessFilter.updateState(sub);
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
    };

    businessFilter.filterMenuItems = function (items, text) {
        var searchText = text.toLowerCase();
        var mappingFunc = function (item) {
            if (item.subs) {
                _.each(item.subs, mappingFunc);
            }
            item.hide = businessFilter.isItemHidden(item, searchText);
        };
        _.each(items, mappingFunc);
        _.each(items, businessFilter.updateState);
    };

    businessFilter.isItemHidden = function (item, searchText) {
        if (item.name.toLowerCase().indexOf(searchText) >= 0) {
            return false;
        }
        if (!item.subs) {
            return true;
        }
        return _.all(item.subs, function (sub) {
            return businessFilter.isItemHidden(sub, searchText);
        });
    };

    businessFilter.collectGeographyIds = function (node) {
        if (node.subs) {
            if (node.allSelected || node.someSelected ) {
                return _.reduce(node.subs, function (acc, item) {
                    return acc.concat(businessFilter.collectGeographyIds(item));
                }, []);
            }
            return [];
        } else {
            return node.allSelected ? [node.id] : [];
        }
    };

    businessFilter.collectVerksamhetsIds = function () {
        var matchingBusinesses = _.filter(businessFilter.businesses, function (business) {
            return _.any(business.verksamhetsTyper, function (verksamhetsTyp) {
                return _.contains(businessFilter.verksamhetsTypIds, verksamhetsTyp.id);
            });
        });
        return _.pluck(matchingBusinesses, 'id');
    };

    businessFilter.makeUnitSelection = function () {
        var geographyBusinessIds = businessFilter.useSmallGUI() ? businessFilter.geographyBusinessIds : businessFilter.collectGeographyIds(businessFilter.geography);
        var verksamhetsBusinessIds = businessFilter.collectVerksamhetsIds();
        businessFilter.selectedBusinesses = _.intersection(geographyBusinessIds, verksamhetsBusinessIds);
    };

    return businessFilter;
});
