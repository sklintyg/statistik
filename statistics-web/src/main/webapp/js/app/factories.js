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

app.statisticsApp.factory('businessFilter', function() {
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
            for (var i = 0; i < businessService.businesses.length; i++) {
                businessService.selectedBusinesses.push(businessService.businesses[i].id);
            }
            for (var i = 0; i < businessService.verksamhetsTyper.length; i++) {
                businessService.verksamhetsTypIds.push(businessService.verksamhetsTyper[i].id);
            }
        }
    }

    businessService.loggedIn = function (businesses) {
        if (!businessService.dataInitialized) {
            businessService.businesses = businesses;
            if (businessService.useSmallGUI()) {
                for (var i = 0; i < businesses.length; i++) {
                    businessService.geographyBusinessIds.push(businesses[i].id);
                }
            } else {
                businessService.populateGeography(businesses);
            }
            businessService.populateVerksamhetsTyper(businesses);
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
        for (var i = 0; i < businesses.length; i++) {
            var business = businesses[i];

            var county = $.grep(businessService.geography.subs, function (element) {
                return element.id === business.lansId;
            });
            if (county.length === 0) {
                county = { id: business.lansId, name: business.lansName, subs: [], allSelected : true, someSelected: false };
                businessService.geography.subs.push(county);
            } else {
                county = county[0];
            }

            var munip = $.grep(county.subs, function (element) {
                return element.id === business.kommunId;
            });
            if (munip.length === 0) {
                munip = { id: business.kommunId, name: business.kommunName, subs: [], allSelected : true, someSelected: false };
                county.subs.push(munip);
            } else {
                munip = munip[0];
            }

            business.allSelected = true;
            business.someSelected = false;
            munip.subs.push(business);
        }
    };

    businessService.populateVerksamhetsTyper = function (businesses) {
        var verksamhetsTypSet = {};
        for (var i = 0; i < businesses.length; i++) {
            var business = businesses[i];
            for (var j = 0; j < business.verksamhetsTyper.length; j++) {
                var verksamhetsTyp = business.verksamhetsTyper[j];
                verksamhetsTypSet[verksamhetsTyp.id] = verksamhetsTyp;
            }
        }
        businessService.verksamhetsTyper = [];
        var id;
        for (id in verksamhetsTypSet) {
            if (verksamhetsTypSet.hasOwnProperty(id)) {
                businessService.verksamhetsTyper.push(verksamhetsTypSet[id]);
                businessService.verksamhetsTypIds.push(id);
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
