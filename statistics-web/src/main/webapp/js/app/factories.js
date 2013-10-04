'use strict';

app.statisticsApp.factory('statisticsData', function($http){
   var factory = {};
   
   factory.getOverview = function (successCallback, failureCallback){
       $http.get("api/getOverview").success(function(result) {
           successCallback(result);
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };
   
   factory.getNumberOfCasesPerMonth = function (successCallback, failureCallback){
       $http.get("api/getNumberOfCasesPerMonth").success(function(result) {
           successCallback(result);
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };

   factory.getDiagnosisGroupData = function (successCallback, failureCallback){
       $http.get("api/getDiagnosisGroupStatistics").success(function(result) {
           successCallback(result);
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };
   
   factory.getSubDiagnosisGroupData = function (successCallback, failureCallback, groupId){
       $http.get("api/getDiagnosisSubGroupStatistics",{params: {'groupId':groupId}}).success(function(result) {
           successCallback(result);
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };
   
   factory.getDiagnosisGroups = function (successCallback, failureCallback){
       $http.get("api/getDiagnosisGroups").success(function(result) {
           successCallback(result);
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };

   factory.getAgeGroups = function (successCallback, failureCallback){
       $http.get("api/getAgeGroupsStatistics").success(function(result) {
           successCallback(result);
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };

   return factory;
});