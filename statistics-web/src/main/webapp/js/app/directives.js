'use strict';

app.statisticsApp.directive("navigationaware", function ($rootScope, $location) {
    
    var isActivePage = function(currentRoute, navLinkAttrs) {
        return currentRoute.controllerAs === navLinkAttrs.ctrlname;
    }
    
    return {
        restrict: "A",
        link: function ($scope, elem, $attrs) {
            $rootScope.$on('$routeChangeSuccess', function(angularEvent, current, previous) {
                elem.parent().removeClass("active");
                if (isActivePage(current, $attrs)){
                    elem.parent().addClass("active");
                    var groupId = elem.closest(".accordion-body").attr('id');
                    $rootScope.$broadcast('navigationUpdate', groupId);
                }
            });
        }
    };
});

app.statisticsApp.directive("spinner", function() {
    return {
        restrict : "A",
        transclude : true,
        replace : true,
        scope : {
          label: "@",
          showSpinner: "=",
          showContent: "="
        },
        template :
            '<div>'
           +'  <div ng-show="showSpinner" class="spinner">'
           +'    <img aria-labelledby="loading-message" src="/img/ajax-loader.gif"/>'
           +'    <p id="loading-message">'
           +'      <strong><span>{{ label }}</span></strong>'
           +'    </p>'
           +'  </div>'
           +'  <div ng-show="showContent">'
           +'    <div ng-transclude></div>'
           +'  </div>'
           +'</div>'
    }
});

app.statisticsApp.directive("dataerrorview", function() {
    return {
        restrict : "A",
        transclude : true,
        replace : true,
        scope : {
            errorPageUrl: "=",
            showError: "="
        },
        template :
            '<div>'+
                '<div ng-show="showError">'
                    +'<div ng-include="\'views/error/failedToFetchData.html\'"></div>'
                +'</div>'
                +'  <div ng-show="!showError">'
                +'    <div ng-transclude></div>'
                +'  </div>'
            +'</div>'
    }
});