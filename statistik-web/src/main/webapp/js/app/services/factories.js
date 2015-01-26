'use strict';

angular.module('StatisticsApp').factory('statisticsData', function ($http, $rootScope) {
    var factory = {};

    var makeRequestNational = function (restFunctionName, successCallback, failureCallback) {
        $http.get("api/" + restFunctionName, {cache: true}).success(function (result) {
            try {
                successCallback(result);
            } catch (e) {
                failureCallback();
            }
        }).error(function (data, status, headers, config) {
            failureCallback();
        });
    };

    var makeRequestVerksamhet = function (restFunctionName, verksamhetId, successCallback, failureCallback) {
        var url = "api/verksamhet/" + verksamhetId + "/" + restFunctionName + $rootScope.queryString;
        $http.post(url, {}, {cache: true}).success(function (result) {
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

    factory.getOverview = function (successCallback, failureCallback) {
        makeRequestNational("getOverview", successCallback, failureCallback);
    };

    factory.getBusinessOverview = function (businessId, successCallback, failureCallback) {
        makeRequestVerksamhet("getOverview", businessId, successCallback, failureCallback);
    };

    factory.getNumberOfCasesPerMonth = function (successCallback, failureCallback) {
        makeRequestNational("getNumberOfCasesPerMonth", successCallback, failureCallback);
    };

    factory.getNumberOfCasesPerMonthVerksamhet = function (verksamhetId, successCallback, failureCallback) {
        makeRequestVerksamhet("getNumberOfCasesPerMonth", verksamhetId, successCallback, failureCallback);
    };

    factory.getDiagnosisGroupData = function (successCallback, failureCallback) {
        makeRequestNational("getDiagnoskapitelstatistik", successCallback, failureCallback);
    };

    factory.getDiagnosisGroupDataVerksamhet = function (verksamhetId, successCallback, failureCallback) {
        makeRequestVerksamhet("getDiagnoskapitelstatistik", verksamhetId, successCallback, failureCallback);
    };

    factory.getSubDiagnosisGroupData = function (successCallback, failureCallback, groupId) {
        makeRequestNational("getDiagnosavsnittstatistik/" + groupId, successCallback, failureCallback);
    };

    factory.getSubDiagnosisGroupDataVerksamhet = function (verksamhetId, successCallback, failureCallback, groupId) {
        makeRequestVerksamhet("getDiagnosavsnittstatistik/" + groupId, verksamhetId, successCallback, failureCallback);
    };

    factory.getDiagnosisKapitelAndAvsnitt = function (successCallback, failureCallback) {
        makeRequestNational("getDiagnosisKapitelAndAvsnitt", successCallback, failureCallback);
    };

    factory.getAgeGroups = function (successCallback, failureCallback) {
        makeRequestNational("getAgeGroupsStatistics", successCallback, failureCallback);
    };

    factory.getAgeGroupsVerksamhet = function (verksamhetId, successCallback, failureCallback) {
        makeRequestVerksamhet("getAgeGroupsStatistics", verksamhetId, successCallback, failureCallback);
    };

    factory.getSjukfallPerLakaresAlderOchKonVerksamhet = function (verksamhetId, successCallback, failureCallback) {
        makeRequestVerksamhet("getCasesPerDoctorAgeAndGenderStatistics", verksamhetId, successCallback, failureCallback);
    };

    factory.getAgeGroupsCurrentVerksamhet = function (verksamhetId, successCallback, failureCallback) {
        makeRequestVerksamhet("getAgeGroupsCurrentStatistics", verksamhetId, successCallback, failureCallback);
    };

    factory.getDegreeOfSickLeave = function (successCallback, failureCallback) {
        makeRequestNational("getDegreeOfSickLeaveStatistics", successCallback, failureCallback);
    };

    factory.getDegreeOfSickLeaveVerksamhet = function (verksamhetId, successCallback, failureCallback) {
        makeRequestVerksamhet("getDegreeOfSickLeaveStatistics", verksamhetId, successCallback, failureCallback);
    };

    factory.getNationalSickLeaveLengthData = function (successCallback, failureCallback) {
        makeRequestNational("getSickLeaveLengthData", successCallback, failureCallback);
    };

    factory.getSickLeaveLengthDataVerksamhet = function (verksamhetId, successCallback, failureCallback) {
        makeRequestVerksamhet("getSickLeaveLengthData", verksamhetId, successCallback, failureCallback);
    };

    factory.getSickLeaveLengthCurrentDataVerksamhet = function (verksamhetId, successCallback, failureCallback) {
        makeRequestVerksamhet("getSickLeaveLengthCurrentData", verksamhetId, successCallback, failureCallback);
    };

    factory.getLongSickLeavesDataVerksamhet = function (verksamhetId, successCallback, failureCallback) {
        makeRequestVerksamhet("getLongSickLeavesData", verksamhetId, successCallback, failureCallback);
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

    factory.getSjukfallPerBusinessVerksamhet = function (verksamhetId, successCallback, failureCallback) {
        makeRequestVerksamhet("getNumberOfCasesPerEnhet", verksamhetId, successCallback, failureCallback);
    };

    factory.getSjukfallPerLakareVerksamhet = function (verksamhetId, successCallback, failureCallback) {
        makeRequestVerksamhet("getNumberOfCasesPerLakare", verksamhetId, successCallback, failureCallback);
    };

    factory.getIcd10Structure = function (successCallback, failureCallback) {
        makeRequestNational("getIcd10Structure", successCallback, failureCallback);
    };

    factory.getFilterHash = function (diagnosIds, enhetsIds, verksamhetstyps, successCallback, failureCallback) {
        var diagnoser = _.reduce(_.values(diagnosIds), function (memo, val) { return memo.concat(val); }, []);
        var param = {"enheter": enhetsIds, "verksamhetstyper": verksamhetstyps, "diagnoser": diagnoser };
        $http.post("api/getFilterHash", param, {cache: true}).success(function (result) {
            try {
                successCallback(result);
            } catch (e) {
                throw new Error(e);
            }
        }).error(function () {
            failureCallback();
        });
    };

    factory.getFilterData = function (filterHash, successCallback, failureCallback) {
        makeRequestNational("getFilterData/" + filterHash, successCallback, failureCallback);
    };

    factory.getSjukfallPerLakarbefattningVerksamhet = function (verksamhetId, successCallback, failureCallback) {
        makeRequestVerksamhet("getNumberOfCasesPerLakarbefattning", verksamhetId, successCallback, failureCallback);
    };

    function arrayToQueryParam(diagnosisToCompare, paramName) {
        if (!diagnosisToCompare) {
            return "";
        }
        return _.reduce(diagnosisToCompare, function(memo, diagnosis, index){
            return memo + (index > 0 ? "&" : "") + paramName + "=" + diagnosis
        }, "");
    }

    factory.getCompareDiagnosisVerksamhet = function (verksamhetId, successCallback, failureCallback, diagnosisToCompare) {
        makeRequestVerksamhet("getJamforDiagnoserStatistik?" + arrayToQueryParam(diagnosisToCompare, "dx"), verksamhetId, successCallback, failureCallback);
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
