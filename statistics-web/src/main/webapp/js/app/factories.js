'use strict';

statisticsApp.factory('statisticsData', function($http){
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
   return factory;
});