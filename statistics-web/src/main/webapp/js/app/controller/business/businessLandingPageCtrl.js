'use strict';

app.businessLandingPageCtrl = function ($window, $cookies, statisticsData) {

    statisticsData.getLoginInfo(function(loginInfo) {
            var defaultVerksamhetId = loginInfo.businesses[0].id;
            if ($cookies.verksamhetId) {
                for (var i = 0; i < loginInfo.businesses.length; i++) {
                    if (loginInfo.businesses[i].id === $cookies.verksamhetId) {
                        defaultVerksamhetId = loginInfo.businesses[i].id;
                        break;
                    }
                }
            }
            $window.location.replace("#/verksamhet/" + defaultVerksamhetId + "/oversikt");
        }, function() {
            $scope.dataLoadingError = true;
        });

}
