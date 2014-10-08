'use strict';

app.statisticsApp.factory('statisticsData', function ($http) {
    var factory = {};

    var makeRequestNational = function (restFunctionName, successCallback, failureCallback) {
        $http.get("api/" + restFunctionName).success(function (result) {
            try {
                successCallback(result);
            } catch (e) {
                failureCallback();
            }
        }).error(function (data, status, headers, config) {
            failureCallback();
        });
    };

    var makeRequestVerksamhet = function (restFunctionName, verksamhetId, enhetsIds, successCallback, failureCallback) {
        var enhetsIdString = getEnhetsIdString(enhetsIds)
        $http.get("api/verksamhet/" + verksamhetId + "/" + restFunctionName + enhetsIdString).success(function (result) {
            try {
                successCallback(result);
            } catch (e) {
                failureCallback();
            }
        }).error(function (data, status, headers, config) {
            if (status == 403) {
                window.location.replace("#/login");
            }
            failureCallback();
        });
    };

    var getEnhetsIdString = function (params) {
        var returnString = ""
        if (params) {
            returnString += "?ids="
            returnString += params.join()
        }
        return returnString
    }

    factory.getOverview = function (successCallback, failureCallback) {
        makeRequestNational("getOverview", successCallback, failureCallback);
    };

    factory.getBusinessOverview = function (businessId, params, successCallback, failureCallback) {
        makeRequestVerksamhet("getOverview", businessId, params, successCallback, failureCallback);
    };

    factory.getNumberOfCasesPerMonth = function (successCallback, failureCallback) {
        makeRequestNational("getNumberOfCasesPerMonth", successCallback, failureCallback);
    };

    factory.getNumberOfCasesPerMonthVerksamhet = function (verksamhetId, params, successCallback, failureCallback) {
        makeRequestVerksamhet("getNumberOfCasesPerMonth", verksamhetId, params, successCallback, failureCallback);
    };

    factory.getDiagnosisGroupData = function (successCallback, failureCallback) {
        makeRequestNational("getDiagnoskapitelstatistik", successCallback, failureCallback);
    };

    factory.getDiagnosisGroupDataVerksamhet = function (verksamhetId, params, successCallback, failureCallback) {
        makeRequestVerksamhet("getDiagnoskapitelstatistik", verksamhetId, params, successCallback, failureCallback);
    };

    factory.getSubDiagnosisGroupData = function (successCallback, failureCallback, groupId) {
        makeRequestNational("getDiagnosavsnittstatistik/" + groupId, successCallback, failureCallback);
    };

    factory.getSubDiagnosisGroupDataVerksamhet = function (verksamhetId, params, successCallback, failureCallback, groupId) {
        makeRequestVerksamhet("getDiagnosavsnittstatistik/" + groupId, verksamhetId, params, successCallback, failureCallback);
    };

    factory.getDiagnosisGroups = function (successCallback, failureCallback) {
        makeRequestNational("getDiagnoskapitel", successCallback, failureCallback);
    };

    factory.getAgeGroups = function (successCallback, failureCallback) {
        makeRequestNational("getAgeGroupsStatistics", successCallback, failureCallback);
    };

    factory.getAgeGroupsVerksamhet = function (verksamhetId, params, successCallback, failureCallback) {
        makeRequestVerksamhet("getAgeGroupsStatistics", verksamhetId, params, successCallback, failureCallback);
    };

    factory.getAgeGroupsCurrentVerksamhet = function (verksamhetId, params, successCallback, failureCallback) {
        makeRequestVerksamhet("getAgeGroupsCurrentStatistics", verksamhetId, params, successCallback, failureCallback);
    };

    factory.getDegreeOfSickLeave = function (successCallback, failureCallback) {
        makeRequestNational("getDegreeOfSickLeaveStatistics", successCallback, failureCallback);
    };

    factory.getDegreeOfSickLeaveVerksamhet = function (verksamhetId, params, successCallback, failureCallback) {
        makeRequestVerksamhet("getDegreeOfSickLeaveStatistics", verksamhetId, params, successCallback, failureCallback);
    };

    factory.getNationalSickLeaveLengthData = function (successCallback, failureCallback) {
        makeRequestNational("getSickLeaveLengthData", successCallback, failureCallback);
    };

    factory.getSickLeaveLengthDataVerksamhet = function (verksamhetId, params, successCallback, failureCallback) {
        makeRequestVerksamhet("getSickLeaveLengthData", verksamhetId, params, successCallback, failureCallback);
    };

    factory.getSickLeaveLengthCurrentDataVerksamhet = function (verksamhetId, params, successCallback, failureCallback) {
        makeRequestVerksamhet("getSickLeaveLengthCurrentData", verksamhetId, params, successCallback, failureCallback);
    };

    factory.getLongSickLeavesDataVerksamhet = function (verksamhetId, params, successCallback, failureCallback) {
        makeRequestVerksamhet("getLongSickLeavesData", verksamhetId, params, successCallback, failureCallback);
    };

    factory.getNationalCountyData = function (successCallback, failureCallback) {
        makeRequestNational("getCountyStatistics", successCallback, failureCallback);
    };

    factory.getNationalSjukfallPerSexData = function (successCallback, failureCallback) {
        makeRequestNational("getSjukfallPerSexStatistics", successCallback, failureCallback);
    };

    factory.getLoginInfo = function (successCallback, failureCallback) {
        makeRequestNational("login/getLoginInfo", successCallback, failureCallback);
    };

    return factory;
});

app.statisticsApp.factory('businessFilter', function(_) {
    var businessService = {};

    businessService.reset = function () {
        businessService.dataInitialized = false;
        businessService.permanentFilter = false;

        businessService.businesses = [];
        businessService.selectedBusinesses = [];

        businessService.geography = { subs: [] };
        businessService.geographyBusinessIds = [];

        businessService.verksamhetsTyper = [];
        businessService.verksamhetsTypIds = [];
    }
    businessService.reset();

    businessService.getSelectedBusinesses = function (samePage) {
        if (!businessService.dataInitialized) {
            return null;
        }
        if (samePage || businessService.permanentFilter) {
            return businessService.selectedBusinesses;
        }
        return null;
    }

    businessService.resetSelections = function() {
        if (!businessService.permanentFilter) {
            businessService.selectedBusinesses.length = 0;
            businessService.verksamhetsTypIds.length = 0;
            _.each(businessService.businesses, function (business) {
                businessService.selectedBusinesses.push(business.id);
            });
            _.each(businessService.verksamhetsTyper, function (verksamhetsTyp) {
                businessService.verksamhetsTypIds.push(verksamhetsTyp.id);
            });
            businessService.selectAll(businessService.geography, true);
        }
    }

    businessService.loggedIn = function (businesses) {
        if (!businessService.dataInitialized) {
            businessService.businesses = businesses;
            if (!businessService.useSmallGUI()) {
                businessService.populateGeography(businesses);
            }
            businessService.populateVerksamhetsTyper(businesses);
            businessService.resetSelections();
            businessService.dataInitialized = true;
        }
    }

    businessService.loggedOut = function () {
        businessService.reset();
    }

    businessService.useSmallGUI = function () {
        return businessService.businesses.length <= 10;
    }

    businessService.populateGeography = function (businesses) {
        _.each(businesses, function (business) {
            var county = _.findWhere(businessService.geography.subs, { id: business.lansId });
            if (!county) {
                county = { id: business.lansId, name: business.lansName, subs: [] };
                businessService.geography.subs.push(county);
            }

            var munip = _.findWhere(county.subs, { id: business.kommunId });
            if (!munip) {
                munip = { id: business.kommunId, name: business.kommunName, subs: [] };
                county.subs.push(munip);
            }

            munip.subs.push(business);
        });
    };

    businessService.populateVerksamhetsTyper = function (businesses) {
        var verksamhetsTypSet = {};
        _.each(businesses, function (business) {
            _.each(business.verksamhetsTyper, function (verksamhetsTyp) {
                verksamhetsTypSet[verksamhetsTyp.id] = verksamhetsTyp;
            });
        });
        businessService.verksamhetsTyper = _.values(verksamhetsTypSet);
    }

    businessService.deselectAll = function (item) {
        if (!item.hide) {
            item.allSelected = false;
            item.someSelected = false;
            if (item.subs) {
                _.each(item.subs, function (sub) {
                    businessService.deselectAll(sub);
                });
            }
        }
    }

    businessService.selectAll = function (item, selectHidden) {
        if (!item.hide || selectHidden) {
            item.allSelected = true;
            item.someSelected = false;
            if (item.subs) {
                _.each(item.subs, function (sub) {
                    businessService.selectAll(sub, selectHidden);
                });
            }
        }
    }

    return businessService;
});

/* Manually compiles the element, fixing the recursion loop. */
app.statisticsApp.factory('recursionService', ['$compile', function ($compile) {
    return {
        compile: function (element, link) {
            // Normalize the link parameter
            if (angular.isFunction(link)) {
                link = { post: link };
            }

            // Break the recursion loop by removing the contents
            var contents = element.contents().remove();
            var compiledContents;
            return {
                pre: (link && link.pre) ? link.pre : null,
                /* Compiles and re-adds the contents */
                post: function (scope, element) {
                    // Compile the contents
                    if (!compiledContents) {
                        compiledContents = $compile(contents);
                    }
                    // Re-add the compiled contents to the element
                    compiledContents(scope, function (clone) {
                        element.append(clone);
                    });

                    // Call the post-linking function, if any
                    if (link && link.post) {
                        link.post.apply(null, arguments);
                    }
                }
            };
        }
    };
}]);
