'use strict';

app.businessLandingPageCtrl = function ($window, statisticsData) {

    var selectDefaultBusiness = function(loginInfo) { 
        $window.location="#/verksamhet/" + loginInfo.businesses[0].id + "/oversikt";
    }
    
    statisticsData.getLoginInfo(selectDefaultBusiness, function() { $scope.dataLoadingError = true; });

}
