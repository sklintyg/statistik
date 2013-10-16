'use strict';

app.statisticsApp.factory('statisticsData', function($http){
   var factory = {};

    factory.getOverview = function (successCallback, failureCallback){
        $http.get("api/getOverview").success(function(result) {
            try {
                successCallback(result);
            } catch (e) {
                failureCallback();
            }
        }).error(function(data, status, headers, config) {
                failureCallback();
            });
    };

    factory.getBusinessOverview = function (businessId, successCallback, failureCallback){
        $http.get("api/verksamhet/" + businessId + "/getOverview/").success(function(result) {
            successCallback(result);
        }).error(function(data, status, headers, config) {
                if(status == 403) {
                    window.location.replace("/login.jsp");
                }
                failureCallback();
            });
    };

    factory.getNumberOfCasesPerMonth = function (successCallback, failureCallback){
       $http.get("api/getNumberOfCasesPerMonth").success(function(result) {
           try {
               successCallback(result);
           } catch (e) {
               failureCallback();
           }
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };

   factory.getDiagnosisGroupData = function (successCallback, failureCallback){
       $http.get("api/getDiagnosisGroupStatistics").success(function(result) {
           try {
               successCallback(result);
           } catch (e) {
               failureCallback();
           }
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };
   
   factory.getSubDiagnosisGroupData = function (successCallback, failureCallback, groupId){
       $http.get("api/getDiagnosisSubGroupStatistics",{params: {'groupId':groupId}}).success(function(result) {
           try {
               successCallback(result);
           } catch (e) {
               failureCallback();
           }
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };
   
   factory.getDiagnosisGroups = function (successCallback, failureCallback){
       $http.get("api/getDiagnosisGroups").success(function(result) {
           try {
               successCallback(result);
           } catch (e) {
               failureCallback();
           }
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };

   factory.getAgeGroups = function (successCallback, failureCallback){
       $http.get("api/getAgeGroupsStatistics").success(function(result) {
           try {
               successCallback(result);
           } catch (e) {
               failureCallback();
           }
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };

   factory.getDegreeOfSickLeave = function (successCallback, failureCallback){
       $http.get("api/getDegreeOfSickLeaveStatistics").success(function(result) {
           successCallback(result);
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };

   factory.getNationalSickLeaveLengthData = function (successCallback, failureCallback){
       $http.get("api/getSickLeaveLengthData").success(function(result) {
           try {
               successCallback(result);
           } catch (e) {
               failureCallback();
           }
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };
   
   factory.getLoginInfo = function (successCallback, failureCallback){
       $http.get("api/login/getLoginInfo").success(function(result) {
           successCallback(result);
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };
   
   return factory;
});
