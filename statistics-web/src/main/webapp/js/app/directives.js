'use strict';

statisticsApp.directive("navigationaware", function ($rootScope, $location) {
    
    var matchingPath = function(navigationHref, currentPath){
        return navigationHref.length >= currentPath.length && navigationHref.substr(navigationHref.length - currentPath.length) == currentPath;
    }
    
    return {
        restrict: "A",
        link: function ($scope, elem, $attrs) {
            $rootScope.$on('$routeChangeSuccess', function() {
                elem.parent().removeClass("active");
                if (matchingPath($attrs.ngHref, $location.$$url)){
                    elem.parent().addClass("active");
                }
            });
        }
    };
});
