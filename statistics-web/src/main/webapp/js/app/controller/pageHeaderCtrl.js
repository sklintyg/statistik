 'use strict';

 app.pageHeaderCtrl = function ($scope, $rootScope) {

    $rootScope.$on('$routeChangeSuccess', function(angularEvent, next, current) {
        $scope.currentUrl = window.location.href;
        $scope.currentTime = new Date().toLocaleFormat("%Y-%m-%d");
    });

 };
