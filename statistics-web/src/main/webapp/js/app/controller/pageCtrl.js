 'use strict';

 app.pageCtrl = function ($scope, $rootScope, $window, statisticsData) {

    var getSelectedVerksamhet = function(selectedVerksamhetId, verksamhets) {
        for (var i = 0; i < verksamhets.length; i++) {
            if (verksamhets[i].id === selectedVerksamhetId) {
                return verksamhets[i];
            }
        }
        return {}; //Selected verksamhet not found
    }

    $rootScope.$on('$routeChangeSuccess', function(angularEvent, next, current) {
        var verksamhetId = next.params.verksamhetId;
        $scope.selectedVerksamhetId = verksamhetId;
        
        $scope.currentUrl = window.location.href;

        var d = new Date();
        var curr_date = d.getDate();
        var curr_month = d.getMonth() + 1; //Months are zero based
        var curr_year = d.getFullYear();
        $scope.currentTime = curr_year + "-" + curr_month + "-" + curr_date;

        $scope.viewHeader = verksamhetId ? "Verksamhetsstatistik" : "Nationell statistik";
        
        $scope.businesses = [];
        $scope.verksamhetName = "";
        if (verksamhetId) {
            statisticsData.getLoginInfo(function(loginInfo){
                    $scope.businesses = loginInfo.businesses;
                    $scope.verksamhetName = getSelectedVerksamhet(verksamhetId, loginInfo.businesses).name;
                }, function() { $scope.dataLoadingError = true; });
        }
    });
    
    $scope.selectVerksamhet = function(verksamhetId) {
        $window.location.replace($scope.currentUrl.replace(new RegExp($scope.selectedVerksamhetId, 'g'), verksamhetId));
    }

 };
