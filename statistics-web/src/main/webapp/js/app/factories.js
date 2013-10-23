'use strict';

app.statisticsApp.factory('statisticsData', function($http){
   var factory = {};

   var makeRequestNational = function(restFunctionName, successCallback, failureCallback){
       $http.get("api/" + restFunctionName).success(function(result) {
           try {
               successCallback(result);
           } catch (e) {
               failureCallback();
           }
       }).error(function(data, status, headers, config) {
               failureCallback();
           });
   };
   
   var makeRequestVerksamhet = function(restFunctionName, verksamhetId, successCallback, failureCallback){
       $http.get("api/verksamhet/" + verksamhetId + "/" + restFunctionName).success(function(result) {
           try {
               successCallback(result);
           } catch (e) {
               failureCallback();
           }
       }).error(function(data, status, headers, config) {
           if(status == 403) {
               window.location.replace("/login.jsp");
           }
           failureCallback();
       });
   };
   
   factory.getOverview = function (successCallback, failureCallback){
       makeRequestNational("getOverview", successCallback, failureCallback);
   };

   factory.getBusinessOverview = function (businessId, successCallback, failureCallback){
       makeRequestVerksamhet("getOverview", businessId, successCallback, failureCallback);
   };

   factory.getNumberOfCasesPerMonth = function (successCallback, failureCallback){
       makeRequestNational("getNumberOfCasesPerMonth", successCallback, failureCallback);
   };

   factory.getNumberOfCasesPerMonthVerksamhet = function (verksamhetId, successCallback, failureCallback){
       makeRequestVerksamhet("getNumberOfCasesPerMonth", verksamhetId, successCallback, failureCallback);
   };
   
   factory.getDiagnosisGroupData = function (successCallback, failureCallback){
       makeRequestNational("getDiagnosisGroupStatistics", successCallback, failureCallback);
   };
   
   factory.getDiagnosisGroupDataVerksamhet = function (verksamhetId, successCallback, failureCallback){
       makeRequestVerksamhet("getDiagnosisGroupStatistics", verksamhetId, successCallback, failureCallback);
   };
   
   factory.getSubDiagnosisGroupData = function (successCallback, failureCallback, groupId){
       makeRequestNational("getDiagnosisSubGroupStatistics/" + groupId, successCallback, failureCallback);
   };
   
   factory.getSubDiagnosisGroupDataVerksamhet = function (verksamhetId, successCallback, failureCallback, groupId){
       makeRequestVerksamhet("getDiagnosisSubGroupStatistics/" + groupId, verksamhetId, successCallback, failureCallback);
   };
   
   factory.getDiagnosisGroups = function (successCallback, failureCallback){
       makeRequestNational("getDiagnosisGroups", successCallback, failureCallback);
   };

   factory.getAgeGroups = function (successCallback, failureCallback){
       makeRequestNational("getAgeGroupsStatistics", successCallback, failureCallback);
   };

   factory.getAgeGroupsVerksamhet = function (verksamhetId, successCallback, failureCallback){
       makeRequestVerksamhet("getAgeGroupsStatistics", verksamhetId, successCallback, failureCallback);
   };
   
   factory.getAgeGroupsCurrentVerksamhet = function (verksamhetId, successCallback, failureCallback){
       makeRequestVerksamhet("getAgeGroupsCurrentStatistics", verksamhetId, successCallback, failureCallback);
   };
   
   factory.getDegreeOfSickLeave = function (successCallback, failureCallback){
       makeRequestNational("getDegreeOfSickLeaveStatistics", successCallback, failureCallback);
   };

   factory.getDegreeOfSickLeaveVerksamhet = function (verksamhetId, successCallback, failureCallback){
       makeRequestVerksamhet("getDegreeOfSickLeaveStatistics", verksamhetId, successCallback, failureCallback);
   };
   
   factory.getNationalSickLeaveLengthData = function (successCallback, failureCallback){
       makeRequestNational("getSickLeaveLengthData", successCallback, failureCallback);
   };
   
   factory.getSickLeaveLengthDataVerksamhet = function (verksamhetId, successCallback, failureCallback){
       makeRequestVerksamhet("getSickLeaveLengthData", verksamhetId, successCallback, failureCallback);
   };
   
   factory.getSickLeaveLengthCurrentDataVerksamhet = function (verksamhetId, successCallback, failureCallback){
       makeRequestVerksamhet("getSickLeaveLengthCurrentData", verksamhetId, successCallback, failureCallback);
   };
   
   factory.getLongSickLeavesDataVerksamhet = function (verksamhetId, successCallback, failureCallback){
       makeRequestVerksamhet("getLongSickLeavesData", verksamhetId, successCallback, failureCallback);
   };
   
   factory.getNationalCountyData = function (successCallback, failureCallback){
       makeRequestNational("getCountyStatistics", successCallback, failureCallback);
   };
   
   factory.getNationalSjukfallPerSexData = function (successCallback, failureCallback){
       makeRequestNational("getSjukfallPerSexStatistics", successCallback, failureCallback);
   };
   
   factory.getLoginInfo = function (successCallback, failureCallback){
       makeRequestNational("login/getLoginInfo", successCallback, failureCallback);
   };
   
   return factory;
});
