'use strict';

app.businessLandingPageCtrl = function ($window, $cookies, statisticsData) {

    var getFirstAvailableVerksamhetId = function() {
        statisticsData.getLoginInfo(function(loginInfo) {
                return loginInfo.businesses[0].id;
            }, function() {
                $scope.dataLoadingError = true;
            });
    }

    var defaultVerksamhetId = $cookies.verksamhetId || getFirstAvailableVerksamhetId();
    $window.location="#/verksamhet/" + defaultVerksamhetId + "/oversikt";
    

}
