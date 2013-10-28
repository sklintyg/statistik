 'use strict';

 app.pageHeaderCtrl = function ($scope, $rootScope) {

    $rootScope.$on('$routeChangeSuccess', function(angularEvent, next, current) {
        $scope.currentUrl = window.location.href;

        var d = new Date();
        var curr_date = d.getDate();
        var curr_month = d.getMonth() + 1; //Months are zero based
        var curr_year = d.getFullYear();
        $scope.currentTime = curr_year + "-" + curr_month + "-" + curr_date;

        $scope.viewHeader = next.params.verksamhetId ? "Verksamhetsstatistik" : "Nationell statistik";
    });

 };
