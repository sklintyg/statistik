angular.module('showcase').controller('showcase.NavigationCtrl',
    ['$scope', '$window', '$localStorage',
        function($scope, $window, $localStorage) {
            'use strict';
            
            $scope.showCookieBanner = false;
            $scope.doShowCookieBanner = function() {
                $localStorage.cookieBannerShown = false;
                $scope.showCookieBanner = !$scope.showCookieBanner;
            };
            
            $scope.today = new Date();
 
            $scope.isCollapsed = true;
        }]);
