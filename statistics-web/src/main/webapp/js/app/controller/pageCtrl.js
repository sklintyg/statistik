 /*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

'use strict';

 app.pageCtrl = function ($scope, $rootScope, $window, $cookies, statisticsData, businessFilter) {

    var getSelectedVerksamhet = function(selectedVerksamhetId, verksamhets) {
        for (var i = 0; i < verksamhets.length; i++) {
            if (verksamhets[i].vardgivarId === selectedVerksamhetId) {
                return verksamhets[i];
            }
        }
        return {name: "Okänd verksamhet"}; //Selected verksamhet not found
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
        
        if (isLoggedIn) {
            if (verksamhetId) {
                $scope.businessId = verksamhetId;
                $cookies.verksamhetId = verksamhetId;
            } else if ($cookies.verksamhetId) {
                $scope.businessId = $cookies.verksamhetId;
            }
            
            statisticsData.getLoginInfo(function(loginInfo){
                businessFilter.businesses = loginInfo.businesses;
                businessFilter.populateGeography(loginInfo.businesses);
                var v = getSelectedVerksamhet($scope.businessId, loginInfo.businesses);
                $scope.verksamhetName = loginInfo.vgView ? ("- "+ v.vardgivarName + (loginInfo.fullVgAccess ? "(alla enheter)": "(vissa enheter)")): v.name;
                $scope.userName = loginInfo.name;
                $scope.isVgView = loginInfo.vgView;
                $scope.isFullVgAccess = loginInfo.fullVgAccess;
                $scope.userNameWithAccess = loginInfo.name;
            }, function() { $scope.dataLoadingError = true; });
        } else {
            businessFilter.resetGeography();
        }
    });
    
    $scope.isLoggedIn = isLoggedIn;
    
    $scope.loginClicked = function(url) {
        $window.location.href = "#/" + url;
    }
    
 };
