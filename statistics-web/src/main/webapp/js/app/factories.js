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

   factory.getNumberOfCasesPerMonthVerksamhet = function (verksamhetId, successCallback, failureCallback){
       $http.get("api/verksamhet/" + verksamhetId + "/getNumberOfCasesPerMonth").success(function(result) {
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
   
   factory.getDiagnosisGroupDataVerksamhet = function (verksamhetId, successCallback, failureCallback){
       $http.get("api/verksamhet/" + verksamhetId + "/getDiagnosisGroupStatistics").success(function(result) {
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
       $http.get("api/getDiagnosisSubGroupStatistics", {params: {'groupId':groupId}}).success(function(result) {
           try {
               successCallback(result);
           } catch (e) {
               failureCallback();
           }
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };
   
   factory.getSubDiagnosisGroupDataVerksamhet = function (verksamhetId, successCallback, failureCallback, groupId){
       $http.get("api/verksamhet/" + verksamhetId + "/getDiagnosisSubGroupStatistics/" + groupId).success(function(result) {
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

   factory.getAgeGroupsVerksamhet = function (verksamhetId, successCallback, failureCallback){
       $http.get("api/verksamhet/" + verksamhetId + "/getAgeGroupsStatistics").success(function(result) {
           try {
               successCallback(result);
           } catch (e) {
               failureCallback();
           }
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };
   
   factory.getAgeGroupsCurrentVerksamhet = function (verksamhetId, successCallback, failureCallback){
       $http.get("api/verksamhet/" + verksamhetId + "/getAgeGroupsCurrentStatistics").success(function(result) {
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

   factory.getDegreeOfSickLeaveVerksamhet = function (verksamhetId, successCallback, failureCallback){
       $http.get("api/verksamhet/" + verksamhetId + "/getDegreeOfSickLeaveStatistics").success(function(result) {
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
   
   factory.getSickLeaveLengthDataVerksamhet = function (verksamhetId, successCallback, failureCallback){
       $http.get("api/verksamhet/" + verksamhetId + "/getSickLeaveLengthData").success(function(result) {
           try {
               successCallback(result);
           } catch (e) {
               failureCallback();
           }
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };
   
   factory.getSickLeaveLengthHistoricalDataVerksamhet = function (verksamhetId, successCallback, failureCallback){
       $http.get("api/verksamhet/" + verksamhetId + "/getSickLeaveLengthHistoricalData").success(function(result) {
           try {
               successCallback(result);
           } catch (e) {
               failureCallback();
           }
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };
   
   factory.getLongSickLeavesDataVerksamhet = function (verksamhetId, successCallback, failureCallback){
       $http.get("api/verksamhet/" + verksamhetId + "/getLongSickLeavesData").success(function(result) {
           try {
               successCallback(result);
           } catch (e) {
               failureCallback();
           }
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };
   
   factory.getNationalCountyData = function (successCallback, failureCallback){
       $http.get("api/getCountyStatistics").success(function(result) {
           try {
               successCallback(result);
           } catch (e) {
               failureCallback();
           }
       }).error(function(data, status, headers, config) {
           failureCallback();
       });
   };
   
   factory.getNationalSjukfallPerSexData = function (successCallback, failureCallback){
       $http.get("api/getSjukfallPerSexStatistics").success(function(result) {
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
