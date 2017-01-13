/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

angular.module('StatisticsApp').factory('statisticsData',

    /** @ngInject */
    function ($http, $rootScope, $q, $location, _, $log, $cacheFactory, $cookies, AppModel) {
    'use strict';

    var factory = {};

    var pendingAbortableRequest;

    function newAbortable() {
        if (pendingAbortableRequest) {
            pendingAbortableRequest.resolve();
        }
        pendingAbortableRequest = $q.defer();
        return pendingAbortableRequest.promise;
    }

    var makeRequestNational = function (restFunctionName, successCallback, failureCallback, cached, notAbortable) {
        var finalCached = (typeof cached !== 'undefined') ? cached : true;
        var timeoutFunction = notAbortable ? null : newAbortable();
        $http.get('api/' + restFunctionName, {cache: finalCached, timeout: timeoutFunction}).success(function (result) {
            try {
                successCallback(result);
            } catch (e) {
                $cacheFactory.get('$http').removeAll();
                failureCallback();
            }
        }).error(function (/*data, status, headers, config*/) {
            failureCallback();
        });
    };

    var makeRequestVerksamhet = function (restFunctionName, successCallback, failureCallback, httpMethod, notAbortable) {
        var url = 'api/verksamhet/' + restFunctionName + $rootScope.queryString;
        makeRequest(url, successCallback, failureCallback, httpMethod, notAbortable);
    };

    var makeRequestLandsting = function (restFunctionName, successCallback, failureCallback, httpMethod, notAbortable) {
        var url = 'api/landsting/' + restFunctionName + $rootScope.queryString;
        makeRequest(url, successCallback, failureCallback, httpMethod, notAbortable);
    };

    function makeRequest(url, successCallback, failureCallback, httpMethod, notAbortable) {
        httpMethod = httpMethod ? httpMethod : 'get';
        var timeoutFunction = notAbortable ? null : newAbortable();

        var config = {cache: false, timeout: timeoutFunction};
        var arg1 = httpMethod === 'get' ? config : {};

        $http[httpMethod](url, arg1, config).success(function (result) {
            $cookies.remove('statUrl');
            try {
                successCallback(result);
            } catch (e) {
                failureCallback();
            }
        }).error(function (data, status/*, headers, config*/) {
            if (status === 403) {
                var url = AppModel.get().loginUrl;
                $cookies.put('statUrl', '/#' + $location.url());
                $rootScope.loginTimedOut = $rootScope.isLoggedIn;
                $rootScope.isLoggedIn = false;
                $location.path(url ? url : '/login');
            }
            if (status === 503) {
                $location.path('/serverbusy');
            }
            failureCallback();
        });
    }

    factory.getOverview = function (successCallback, failureCallback) {
        makeRequestNational('getOverview', successCallback, failureCallback);
    };

    factory.getBusinessOverview = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getOverview', successCallback, failureCallback);
    };

    factory.getNumberOfCasesPerMonth = function (successCallback, failureCallback) {
        makeRequestNational('getNumberOfCasesPerMonth', successCallback, failureCallback);
    };

    factory.getNumberOfMeddelandenPerMonth = function (successCallback, failureCallback) {
        makeRequestNational('getNumberOfMeddelandenPerMonth', successCallback, failureCallback);
    };

    factory.getNumberOfIntygPerMonthVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getTotalNumberOfIntygPerMonth', successCallback, failureCallback);
    };

    factory.getNumberOfIntygPerMonthTvarsnittVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getTotalNumberOfIntygTvarsnitt', successCallback, failureCallback);
    };

    factory.getIntygPerTypePerMonthVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getIntygPerTypePerMonth', successCallback, failureCallback);
    };

    factory.getIntygPerTypeTvarsnittVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getIntygPerTypeTvarsnitt', successCallback, failureCallback);
    };

    factory.getNumberOfCasesPerMonthVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getNumberOfCasesPerMonth', successCallback, failureCallback);
    };

    factory.getNumberOfCasesPerMonthLandsting = function (successCallback, failureCallback) {
        makeRequestLandsting('getNumberOfCasesPerMonthLandsting', successCallback, failureCallback);
    };

    factory.getNumberOfCasesPerMonthTvarsnittVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getNumberOfCasesPerMonthTvarsnitt', successCallback, failureCallback);
    };

    factory.getDiagnosisGroupData = function (successCallback, failureCallback) {
        makeRequestNational('getDiagnoskapitelstatistik', successCallback, failureCallback);
    };

    factory.getDiagnosisGroupDataVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getDiagnoskapitelstatistik', successCallback, failureCallback);
    };

    factory.getDiagnosisGroupTvarsnittVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getDiagnosGruppTvarsnitt', successCallback, failureCallback);
    };

    factory.getSubDiagnosisGroupData = function (successCallback, failureCallback, groupId) {
        makeRequestNational('getDiagnosavsnittstatistik/' + groupId, successCallback, failureCallback);
    };

    factory.getSubDiagnosisGroupDataVerksamhet = function (successCallback, failureCallback, groupId) {
        makeRequestVerksamhet('getDiagnosavsnittstatistik/' + groupId, successCallback, failureCallback);
    };

    factory.getSubDiagnosisGroupTvarsnittVerksamhet = function (successCallback, failureCallback, groupId) {
        makeRequestVerksamhet('getDiagnosavsnittTvarsnitt/' + groupId, successCallback, failureCallback);
    };

    factory.getDiagnosisKapitelAndAvsnittAndKategori = function (successCallback, failureCallback) {
        makeRequestNational('getDiagnosisKapitelAndAvsnittAndKategori', successCallback, failureCallback, true, true);
    };

    factory.getAgeGroups = function (successCallback, failureCallback) {
        makeRequestNational('getAgeGroupsStatistics', successCallback, failureCallback);
    };

    factory.getAgeGroupsVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getAgeGroupsStatistics', successCallback, failureCallback);
    };

    factory.getAgeGroupsTimeSeriesVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getAgeGroupsStatisticsAsTimeSeries', successCallback, failureCallback);
    };

    factory.getSjukfallPerLakaresAlderOchKonVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getCasesPerDoctorAgeAndGenderStatistics', successCallback, failureCallback);
    };

    factory.getSjukfallPerLakaresAlderOchKonTidsserieVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getCasesPerDoctorAgeAndGenderTimeSeriesStatistics', successCallback, failureCallback);
    };

    factory.getDegreeOfSickLeave = function (successCallback, failureCallback) {
        makeRequestNational('getDegreeOfSickLeaveStatistics', successCallback, failureCallback);
    };

    factory.getDegreeOfSickLeaveVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getDegreeOfSickLeaveStatistics', successCallback, failureCallback);
    };

    factory.getDegreeOfSickLeaveTvarsnittVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getDegreeOfSickLeaveTvarsnitt', successCallback, failureCallback);
    };

    factory.getDifferentieratIntygandeVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getDifferentieratIntygandeStatistics', successCallback, failureCallback);
    };

    factory.getDifferentieratIntygandeTvarsnittVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getDifferentieratIntygandeTvarsnitt', successCallback, failureCallback);
    };

    factory.getNationalSickLeaveLengthData = function (successCallback, failureCallback) {
        makeRequestNational('getSickLeaveLengthData', successCallback, failureCallback);
    };

    factory.getSickLeaveLengthDataVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getSickLeaveLengthData', successCallback, failureCallback);
    };

    factory.getSickLeaveLengthTimeSeriesDataVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getSickLeaveLengthTimeSeries', successCallback, failureCallback);
    };

    factory.getLongSickLeavesDataVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getLongSickLeavesData', successCallback, failureCallback);
    };

    factory.getLongSickLeavesTvarsnittVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getLongSickLeavesTvarsnitt', successCallback, failureCallback);
    };

    factory.getNationalCountyData = function (successCallback, failureCallback) {
        makeRequestNational('getCountyStatistics', successCallback, failureCallback);
    };

    factory.getNationalSjukfallPerSexData = function (successCallback, failureCallback) {
        makeRequestNational('getSjukfallPerSexStatistics', successCallback, failureCallback);
    };

    factory.getLoginInfo = function (successCallback, failureCallback) {
        makeRequestNational('login/getLoginInfo', successCallback, failureCallback, false, true);
    };

    factory.getUserAccessInfo = function (vgId, successCallback, failureCallback) {
        makeRequestNational('login/getUserAccessInfo/' + vgId, successCallback, failureCallback, false, true);
    };

    factory.getSjukfallPerBusinessVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getNumberOfCasesPerEnhet', successCallback, failureCallback);
    };

    factory.getSjukfallPerBusinessLandsting = function (successCallback, failureCallback) {
        makeRequestLandsting('getNumberOfCasesPerEnhetLandsting', successCallback, failureCallback);
    };

    factory.getSjukfallPerBusinessTimeSeriesVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getNumberOfCasesPerEnhetTimeSeries', successCallback, failureCallback);
    };

    factory.getNumberOfMeddelandenPerMonthVerksamhet  = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getNumberOfMeddelandenPerMonth', successCallback, failureCallback);
    };

    factory.getNumberOfMeddelandenPerMonthTvarsnittVerksamhet  = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getNumberOfMeddelandenPerMonthTvarsnitt', successCallback, failureCallback);
    };

    factory.getSjukfallPerPatientsPerBusinessLandsting = function (successCallback, failureCallback) {
        makeRequestLandsting('getNumberOfCasesPerPatientsPerEnhetLandsting', successCallback, failureCallback);
    };

    factory.getSjukfallPerLakareVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getNumberOfCasesPerLakare', successCallback, failureCallback);
    };

    factory.getSjukfallPerLakareSomTidsserieVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getSjukfallPerLakareSomTidsserie', successCallback, failureCallback);
    };

    factory.getIcd10Structure = function (successCallback, failureCallback) {
        makeRequestNational('getIcd10Structure', successCallback, failureCallback, true, true);
    };

    factory.getFilterHash = function (params) {
        var deferred = $q.defer();

        var concatDiagnoser = function (diagnosIds) {
            return _.reduce(_.values(diagnosIds), function (memo, val) {
                return memo.concat(val);
            }, []);
        };

        var param = {
            'enheter': params.enheter || null,
            'verksamhetstyper': params.verksamhetstyper || null,
            'sjukskrivningslangd': params.sjukskrivningslangd || null,
            'aldersgrupp': params.aldersgrupp || null,
            'diagnoser': concatDiagnoser(params.diagnoser),
            'fromDate': params.fromDate || null,
            'toDate': params.toDate || null,
            'useDefaultPeriod': !!params.useDefaultPeriod
        };

        $http.post('api/filter', param,
            {
                cache: true
            }).success(function (data) {
                deferred.resolve(data);
            }).error(function (data/*, status, headers*/) {
                deferred.reject(data);
            });

        return deferred.promise;
    };

    factory.getFilterData = function (filterHash, successCallback, failureCallback) {
        makeRequestNational('filter/' + filterHash, successCallback, failureCallback, true, true);
    };

    factory.getSjukfallPerLakarbefattningVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getNumberOfCasesPerLakarbefattning', successCallback, failureCallback);
    };

    factory.getSjukfallPerLakarbefattningTidsserieVerksamhet = function (successCallback, failureCallback) {
        makeRequestVerksamhet('getNumberOfCasesPerLakarbefattningSomTidsserie', successCallback, failureCallback);
    };

    factory.getCompareDiagnosisVerksamhet = function (successCallback, failureCallback, diagnosisToCompare) {
        makeRequestVerksamhet('getJamforDiagnoserStatistik/' + diagnosisToCompare, successCallback, failureCallback);
    };

    factory.getCompareDiagnosisTimeSeriesVerksamhet = function (successCallback, failureCallback, diagnosisToCompare) {
        makeRequestVerksamhet('getJamforDiagnoserStatistikTidsserie/' + diagnosisToCompare, successCallback, failureCallback);
    };

    factory.getLastLandstingUpdateInfo = function (successCallback, failureCallback) {
        makeRequestLandsting('lastUpdateInfo', successCallback, failureCallback, 'get', true);
    };

    factory.getLandstingFilterInfo = function (successCallback, failureCallback) {
        makeRequestLandsting('landstingFilterInfo', successCallback, failureCallback, 'get', true);
    };

    factory.clearLandstingEnhets = function (successCallback, failureCallback) {
        makeRequestLandsting('landstingEnhets', successCallback, failureCallback, 'delete', true);
    };

    factory.logOnServer = function (message) {
        $http.post('api/logging/log', {message: message, url: $location.url()}, {cache: false})
        .success(function (/*result*/) {
        }).error(function (/*data, status, headers, config*/) {
            $log.log('Could not log to server: ' + message);
        });
    };

    return factory;
});
