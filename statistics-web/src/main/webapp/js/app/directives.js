'use strict';

app.statisticsApp.directive("navigationaware", function ($rootScope, $location) {
    
    var matchingPath = function(navigationHref, currentPath){
        return currentPath.indexOf(navigationHref) >= 0;
    };
    
    return {
        restrict: "A",
        link: function ($scope, elem, $attrs) {
            $rootScope.$on('$routeChangeSuccess', function() {
                elem.parent().removeClass("active");
                if (matchingPath($attrs.ngHref, $location.$$absUrl)){
                    elem.parent().addClass("active");
                }
            });
        }
    };
});
