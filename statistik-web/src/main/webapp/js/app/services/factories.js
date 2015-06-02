'use strict';

angular.module('StatisticsApp').factory('statisticsData', ['$http', '$rootScope', '$q', function ($http, $rootScope, $q) {
    var factory = {};

    var makeRequestNational = function (restFunctionName, successCallback, failureCallback, cached) {
        var finalCached = (typeof cached !== 'undefined') ? cached : true;

        $http.get("api/" + restFunctionName, {cache: finalCached}).success(function (result) {
            try {
                successCallback(result);
            } catch (e) {
                failureCallback();
            }
        }).error(function (data, status, headers, config) {
            failureCallback();
        });
    };

    var makeRequestVerksamhet = function (restFunctionName, successCallback, failureCallback) {
        var url = "api/verksamhet/" + restFunctionName + $rootScope.queryString;
        $http.get(url, {}, {cache: false}).success(function (result) {
            try {
                successCallback(result);
            } catch (e) {
                failureCallback();
            }
        }).error(function (data, status, headers, config) {
            if (status === 403) {
                window.location.replace("#/login");
            }
            if (status === 503) {
                window.location.replace("#/serverbusy");
            }
            failureCallback();
        });
    };

    factory.getOverview = function (successCallback, failureCallback) {
        makeRequestNational("getOverview", successCallback, failureCallback);
    };

    factory.getBusinessOverview = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getOverview", successCallback, failureCallback);
    };

    factory.getNumberOfCasesPerMonth = function (successCallback, failureCallback) {
        makeRequestNational("getNumberOfCasesPerMonth", successCallback, failureCallback);
    };

    factory.getNumberOfCasesPerMonthVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getNumberOfCasesPerMonth", successCallback, failureCallback);
    };

    factory.getNumberOfCasesPerMonthLandsting = function (successCallback, failureCallback) {
        makeRequestVerksamhet("landsting/getNumberOfCasesPerMonthLandsting", successCallback, failureCallback);
    };

    factory.getNumberOfCasesPerMonthTvarsnittVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getNumberOfCasesPerMonthTvarsnitt", successCallback, failureCallback);
    };

    factory.getDiagnosisGroupData = function (successCallback, failureCallback) {
        makeRequestNational("getDiagnoskapitelstatistik", successCallback, failureCallback);
    };

    factory.getDiagnosisGroupDataVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getDiagnoskapitelstatistik", successCallback, failureCallback);
    };

    factory.getDiagnosisGroupTvarsnittVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getDiagnosGruppTvarsnitt", successCallback, failureCallback);
    };

    factory.getSubDiagnosisGroupData = function (successCallback, failureCallback, groupId) {
        makeRequestNational("getDiagnosavsnittstatistik/" + groupId, successCallback, failureCallback);
    };

    factory.getSubDiagnosisGroupDataVerksamhet = function (successCallback, failureCallback, groupId) {
        makeRequestVerksamhet("getDiagnosavsnittstatistik/" + groupId, successCallback, failureCallback);
    };

    factory.getSubDiagnosisGroupTvarsnittVerksamhet = function (successCallback, failureCallback, groupId) {
        makeRequestVerksamhet("getDiagnosavsnittTvarsnitt/" + groupId, successCallback, failureCallback);
    };

    factory.getDiagnosisKapitelAndAvsnittAndKod = function (successCallback, failureCallback) {
        makeRequestNational("getDiagnosisKapitelAndAvsnittAndKod", successCallback, failureCallback);
    };

    factory.getAgeGroups = function (successCallback, failureCallback) {
        makeRequestNational("getAgeGroupsStatistics", successCallback, failureCallback);
    };

    factory.getAgeGroupsVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getAgeGroupsStatistics", successCallback, failureCallback);
    };

    factory.getAgeGroupsTimeSeriesVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getAgeGroupsStatisticsAsTimeSeries", successCallback, failureCallback);
    };

    factory.getSjukfallPerLakaresAlderOchKonVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getCasesPerDoctorAgeAndGenderStatistics", successCallback, failureCallback);
    };

    factory.getSjukfallPerLakaresAlderOchKonTidsserieVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getCasesPerDoctorAgeAndGenderTimeSeriesStatistics", successCallback, failureCallback);
    };

    factory.getDegreeOfSickLeave = function (successCallback, failureCallback) {
        makeRequestNational("getDegreeOfSickLeaveStatistics", successCallback, failureCallback);
    };

    factory.getDegreeOfSickLeaveVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getDegreeOfSickLeaveStatistics", successCallback, failureCallback);
    };

    factory.getDegreeOfSickLeaveTvarsnittVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getDegreeOfSickLeaveTvarsnitt", successCallback, failureCallback);
    };

    factory.getNationalSickLeaveLengthData = function (successCallback, failureCallback) {
        makeRequestNational("getSickLeaveLengthData", successCallback, failureCallback);
    };

    factory.getSickLeaveLengthDataVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getSickLeaveLengthData", successCallback, failureCallback);
    };

    factory.getSickLeaveLengthTimeSeriesDataVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getSickLeaveLengthTimeSeries", successCallback, failureCallback);
    };

    factory.getLongSickLeavesDataVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getLongSickLeavesData", successCallback, failureCallback);
    };

    factory.getLongSickLeavesTvarsnittVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getLongSickLeavesTvarsnitt", successCallback, failureCallback);
    };

    factory.getNationalCountyData = function (successCallback, failureCallback) {
        makeRequestNational("getCountyStatistics", successCallback, failureCallback);
    };

    factory.getNationalSjukfallPerSexData = function (successCallback, failureCallback) {
        makeRequestNational("getSjukfallPerSexStatistics", successCallback, failureCallback);
    };

    factory.getLoginInfo = function (successCallback, failureCallback) {
        makeRequestNational("login/getLoginInfo", successCallback, failureCallback, false);
    };

    factory.getSjukfallPerBusinessVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getNumberOfCasesPerEnhet", successCallback, failureCallback);
    };

    factory.getSjukfallPerBusinessLandsting = function (successCallback, failureCallback) {
        makeRequestVerksamhet("landsting/getNumberOfCasesPerEnhetLandsting", successCallback, failureCallback);
    };

    factory.getSjukfallPerBusinessTimeSeriesVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getNumberOfCasesPerEnhetTimeSeries", successCallback, failureCallback);
    };

    factory.getSjukfallPerPatientsPerBusinessLandsting = function (successCallback, failureCallback) {
        makeRequestVerksamhet("landsting/getNumberOfCasesPerPatientsPerEnhetLandsting", successCallback, failureCallback);
    };

    factory.getSjukfallPerLakareVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getNumberOfCasesPerLakare", successCallback, failureCallback);
    };

    factory.getSjukfallPerLakareSomTidsserieVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getSjukfallPerLakareSomTidsserie", successCallback, failureCallback);
    };

    factory.getIcd10Structure = function (successCallback, failureCallback) {
        makeRequestNational("getIcd10Structure", successCallback, failureCallback);
    };

    factory.getFilterHash = function (params) {
        var deferred = $q.defer();

        var concatDiagnoser = function (diagnosIds) {
            return _.reduce(_.values(diagnosIds), function (memo, val) {
                return memo.concat(val);
            }, []);
        };

        var param = {
            "enheter": params.enheter || null,
            "verksamhetstyper": params.verksamhetstyper || null,
            "diagnoser": concatDiagnoser(params.diagnoser),
            "fromDate": params.fromDate || null,
            "toDate": params.toDate || null,
            "useDefaultPeriod": !!params.useDefaultPeriod
        };

        $http.post("api/filter", param,
            {
                cache: true
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function (data, status, headers) {
                deferred.reject(data);
            });

        return deferred.promise;
    };

    factory.getFilterData = function (filterHash, successCallback, failureCallback) {
        makeRequestNational("filter/" + filterHash, successCallback, failureCallback);
    };

    factory.getSjukfallPerLakarbefattningVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getNumberOfCasesPerLakarbefattning", successCallback, failureCallback);
    };

    factory.getSjukfallPerLakarbefattningTidsserieVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet("getNumberOfCasesPerLakarbefattningSomTidsserie", successCallback, failureCallback);
    };

    factory.getCompareDiagnosisVerksamhet = function (successCallback, failureCallback, diagnosisToCompare) {
        makeRequestVerksamhet("getJamforDiagnoserStatistik/" + diagnosisToCompare, successCallback, failureCallback);
    };

    factory.getCompareDiagnosisTimeSeriesVerksamhet = function (successCallback, failureCallback, diagnosisToCompare) {
        makeRequestVerksamhet("getJamforDiagnoserStatistikTidsserie/" + diagnosisToCompare, successCallback, failureCallback);
    };

    factory.getLastLandstingUpdateInfo = function (successCallback, failureCallback) {
        makeRequestVerksamhet("landsting/lastUpdateInfo", successCallback, failureCallback);
    };

    return factory;
}]);

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
