angular.module('StatisticsApp').directive('statCookieBanner',
    [function() {
        'use strict';

        return {
            restrict: 'E',
            scope: {},
            templateUrl: 'js/app/components/directives/statCookieBanner/statCookieBanner.html',
            controller: function($scope, $timeout, $localStorage) {
                $scope.isOpen = false;
                $scope.showDetails = false;


                if (!$localStorage.cookieBannerShown) {
                    $timeout(function() {
                        $scope.isOpen = true;
                    }, 500);
                }

                $scope.onCookieConsentClick = function() {
                    $scope.isOpen = false;

                    $localStorage.cookieBannerShown = true;
                };
            }
        };
    }]);