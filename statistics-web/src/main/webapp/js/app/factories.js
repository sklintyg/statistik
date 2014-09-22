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
        var enhetsIdString = getParamString(enhetsIds)
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

    var getParamString = function (params) {
        var returnString = ""
        if (params && params.length > 0) {
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
    var businesses = [];
    var selectedBusinesses = [];
    var businessService = {};
    var geography = { subs: [] };
    var geographyInitialized = false;

    businessService.setBusinesses = function (newItems) {
        businesses = newItems;
    };

    businessService.getBusinesses = function () {
        return businesses;
    };

    businessService.setSelectedBusinesses = function (newItems) {
        selectedBusinesses = newItems;
    };

    businessService.getSelectedBusinesses = function () {
        return selectedBusinesses;
    };

    businessService.populateGeography = function (businesses) {
        if (!geographyInitialized) {
            for (var i = 0; i < businesses.length; i++) {
                var business = businesses[i];

                var county = $.grep(geography.subs, function (element) {
                    return element.id === business.lansId;
                });
                if (county.length === 0) {
                    county = { id: business.lansId, name: business.lansName, subs: [] };
                    geography.subs.push(county);
                } else {
                    county = county[0];
                }

                var munip = $.grep(county.subs, function (element) {
                    return element.id === business.kommunId;
                });
                if (munip.length === 0) {
                    munip = { id: business.kommunId, name: business.kommunName, subs: [] };
                    county.subs.push(munip);
                } else {
                    munip = munip[0];
                }

                munip.subs.push(business);
            }
            geographyInitialized = true;
        }
    };

    businessService.getGeography = function () {
        return geography;
    }

    businessService.resetGeography = function () {
        geography = { subs: [] };
        geographyInitialized = false;
    }

    return businessService;
});
