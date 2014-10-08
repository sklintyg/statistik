'use strict';

app.statisticsApp.factory('businessFilter', function(_) {
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

    businessFilter.getSelectedBusinesses = function (samePage) {
        if (!businessFilter.dataInitialized) {
            return null;
        }
        if (samePage || businessFilter.permanentFilter) {
            return businessFilter.selectedBusinesses;
        }
        return null;
    }

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
    }

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
    }

    businessFilter.loggedOut = function () {
        businessFilter.reset();
    }

    businessFilter.useSmallGUI = function () {
        return businessFilter.businesses.length <= 10;
    }

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
    }

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
    }

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
    }

    return businessFilter;
});
