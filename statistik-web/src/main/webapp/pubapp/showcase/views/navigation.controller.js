angular.module('showcase').controller('showcase.NavigationCtrl',
    ['$scope', '$window', '$localStorage',
        function($scope, $window, $localStorage) {
            'use strict';
            
            $scope.headerRows = [[{text:'Rubrik 1'},{text:"Rubrik 2"}]]; 
            $scope.rows = [{
            	  name: 'Rad 1',
            	  data: [1,2]
            	},{
            	  name: 'Rad 2',
            	  data: [3,4]
            	}];
            $scope.activeSjukskrivningslangdsFilters=['1 vecka', '2 veckor'];
            $scope.businesses = new Array();
            var data = {name : "businessname"};
            $scope.businesses = [];
            $scope.businesses.push(data);

            $scope.showCookieBanner = false;
            $scope.doShowCookieBanner = function() {
                $localStorage.cookieBannerShown = false;
                $scope.showCookieBanner = !$scope.showCookieBanner;
            };
            
            $scope.today = new Date();
 
            $scope.isCollapsed = true;
        }]);
