 'use strict';

 app.pageCtrl = function ($scope, $rootScope, $window, $cookies, statisticsData) {

    var getSelectedVerksamhet = function(selectedVerksamhetId, verksamhets) {
        for (var i = 0; i < verksamhets.length; i++) {
            if (verksamhets[i].id === selectedVerksamhetId) {
                return verksamhets[i];
            }
        }
        return {}; //Selected verksamhet not found
    };

    $rootScope.$on('$routeChangeSuccess', function(angularEvent, next, current) {
        var verksamhetId = next.params.verksamhetId;
        $scope.verksamhetIdParam = verksamhetId;

        $scope.currentUrl = window.location.href;

        var d = new Date();
        var curr_date = d.getDate();
        var curr_month = d.getMonth() + 1; //Months are zero based
        var curr_year = d.getFullYear();
        $scope.currentTime = curr_year + "-" + curr_month + "-" + curr_date;

        $scope.viewHeader = verksamhetId ? "Verksamhetsstatistik" : "Nationell statistik";
        
        $scope.businesses = [];
        if (isLoggedIn) {
            if (verksamhetId) {
                $scope.businessId = verksamhetId;
                $cookies.verksamhetId = verksamhetId;
            } else if ($cookies.verksamhetId) {
                $scope.businessId = $cookies.verksamhetId;
            }
            
            statisticsData.getLoginInfo(function(loginInfo){
                    $scope.businesses = loginInfo.businesses;
                    $scope.verksamhetName = getSelectedVerksamhet($scope.businessId, loginInfo.businesses).name;
                }, function() { $scope.dataLoadingError = true; });
        }
    });
    
    $scope.selectVerksamhet = function(verksamhetId) {
        $cookies.verksamhetId = verksamhetId;
        $scope.verksamhetName = getSelectedVerksamhet(verksamhetId, $scope.businesses).name;
        $window.location.href = $scope.currentUrl.replace(new RegExp($scope.businessId, 'g'), verksamhetId);
        $scope.businessId = verksamhetId;
    };
    
 };
