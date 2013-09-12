'use strict';

angular.module('StatisticsApp').directive("navigationaware", ['$rootScope', '$location', function ($rootScope, $location) {
    return {
        restrict: "A",
        link: function ($scope, elem, $attrs) {
            String.prototype.endsWith = function (s) {
                return this.length >= s.length && this.substr(this.length - s.length) == s;
            }
            
            $rootScope.$on('$routeChangeSuccess', function() {
                elem.parent().removeClass("active");
                if ($attrs.ngHref.endsWith($location.$$path)){
                    elem.parent().addClass("active");
                    console.log("Matching:" + $location.$$path);
                }
              });
        }
    };
}]);
