'use strict';

app.businessLandingPageCtrl = function ($scope, $window, $cookies, statisticsData) {

    statisticsData.getLoginInfo(function(loginInfo) {
            if (loginInfo.businesses.length < 1) {
                $window.location.href = "#/login";
            }
            var defaultVerksamhetId = loginInfo.businesses[0].id;
            if ($cookies.verksamhetId) {
                for (var i = 0; i < loginInfo.businesses.length; i++) {
                    if (loginInfo.businesses[i].id === $cookies.verksamhetId) {
                        defaultVerksamhetId = loginInfo.businesses[i].id;
                        break;
                    }
                }
            }
            $window.location.href = "#/verksamhet/" + defaultVerksamhetId + "/oversikt";
        }, function() {
            $scope.dataLoadingError = true;
        });

}
