'use strict';

angular.module('StatisticsApp').factory('statisticsData', function ($http) {
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

    var makeRequestVerksamhet = function (restFunctionName, verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback) {
        var param = getParamsAsJson(enhetsIds, diagnosIds);
        $http.post("api/verksamhet/" + verksamhetId + "/" + restFunctionName, param).success(function (result) {
            try {
                successCallback(result, enhetsIds, diagnosIds);
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

    var getParamsAsJson = function (enhetsIds, diagnosIds) {
        if (diagnosIds) {
            return {"enhets": enhetsIds, "kapitels": diagnosIds.kapitel, "avsnitts": diagnosIds.avsnitt, "kategoris": diagnosIds.kategorier};
        }
        return {"enhets": enhetsIds, "kapitels": null, "avsnitts": null, "kategoris": null};
    };

    factory.getOverview = function (successCallback, failureCallback) {
        makeRequestNational("getOverview", successCallback, failureCallback);
    };

    factory.getBusinessOverview = function (businessId, enhetsIds, diagnosIds, successCallback, failureCallback) {
        makeRequestVerksamhet("getOverview", businessId, enhetsIds, diagnosIds, successCallback, failureCallback);
    };

    factory.getNumberOfCasesPerMonth = function (successCallback, failureCallback) {
        makeRequestNational("getNumberOfCasesPerMonth", successCallback, failureCallback);
    };

    factory.getNumberOfCasesPerMonthVerksamhet = function (verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback) {
        makeRequestVerksamhet("getNumberOfCasesPerMonth", verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback);
    };

    factory.getDiagnosisGroupData = function (successCallback, failureCallback) {
        makeRequestNational("getDiagnoskapitelstatistik", successCallback, failureCallback);
    };

    factory.getDiagnosisGroupDataVerksamhet = function (verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback) {
        makeRequestVerksamhet("getDiagnoskapitelstatistik", verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback);
    };

    factory.getSubDiagnosisGroupData = function (successCallback, failureCallback, groupId) {
        makeRequestNational("getDiagnosavsnittstatistik/" + groupId, successCallback, failureCallback);
    };

    factory.getSubDiagnosisGroupDataVerksamhet = function (verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback, groupId) {
        makeRequestVerksamhet("getDiagnosavsnittstatistik/" + groupId, verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback);
    };

    factory.getDiagnosisKapitelAndAvsnitt = function (successCallback, failureCallback) {
        makeRequestNational("getDiagnosisKapitelAndAvsnitt", successCallback, failureCallback);
    };

    factory.getAgeGroups = function (successCallback, failureCallback) {
        makeRequestNational("getAgeGroupsStatistics", successCallback, failureCallback);
    };

    factory.getAgeGroupsVerksamhet = function (verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback) {
        makeRequestVerksamhet("getAgeGroupsStatistics", verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback);
    };

    factory.getSjukfallPerLakaresAlderOchKonVerksamhet = function (verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback) {
        makeRequestVerksamhet("getCasesPerDoctorAgeAndGenderStatistics", verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback);
    };

    factory.getAgeGroupsCurrentVerksamhet = function (verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback) {
        makeRequestVerksamhet("getAgeGroupsCurrentStatistics", verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback);
    };

    factory.getDegreeOfSickLeave = function (successCallback, failureCallback) {
        makeRequestNational("getDegreeOfSickLeaveStatistics", successCallback, failureCallback);
    };

    factory.getDegreeOfSickLeaveVerksamhet = function (verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback) {
        makeRequestVerksamhet("getDegreeOfSickLeaveStatistics", verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback);
    };

    factory.getNationalSickLeaveLengthData = function (successCallback, failureCallback) {
        makeRequestNational("getSickLeaveLengthData", successCallback, failureCallback);
    };

    factory.getSickLeaveLengthDataVerksamhet = function (verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback) {
        makeRequestVerksamhet("getSickLeaveLengthData", verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback);
    };

    factory.getSickLeaveLengthCurrentDataVerksamhet = function (verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback) {
        makeRequestVerksamhet("getSickLeaveLengthCurrentData", verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback);
    };

    factory.getLongSickLeavesDataVerksamhet = function (verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback) {
        makeRequestVerksamhet("getLongSickLeavesData", verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback);
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

    factory.getSjukfallPerBusinessVerksamhet = function (verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback) {
        makeRequestVerksamhet("getNumberOfCasesPerEnhet", verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback);
    };

    factory.getSjukfallPerLakareVerksamhet = function (verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback) {
        makeRequestVerksamhet("getNumberOfCasesPerLakare", verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback);
    };

    factory.getIcd10Structure = function (successCallback, failureCallback) {
        makeRequestNational("getIcd10Structure", successCallback, failureCallback);
    };

    factory.getSjukfallPerLakarbefattningVerksamhet = function (verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback) {
        makeRequestVerksamhet("getNumberOfCasesPerLakarbefattning", verksamhetId, enhetsIds, diagnosIds, successCallback, failureCallback);
    };

    return factory;
});

/* Manually compiles the element, fixing the recursion loop. */
angular.module('StatisticsApp').factory('recursionService', ['$compile', function ($compile) {
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
