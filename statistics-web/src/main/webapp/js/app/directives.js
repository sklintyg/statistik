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
                    var groupId = elem.closest(".navigation-group").attr('id');
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

app.statisticsApp.directive('legendHeight', function() {
    return function(scope, element, attrs) {
       if (scope.$last) {
          scope.$watch('seriesData', function(){
            var heights = ControllerCommons.map(element.parent().children(), function(e){return $(e).height();})
            var maxHeight = Math.max.apply(null, heights);
            element.parent().children().css('height', maxHeight);
          });
       }   
    };
});

app.statisticsApp.directive('multiselectDropdown', function() {
    return function(scope, element, attrs) {
        element.multiselect({
            numberDisplayed : 3,
            onChange: function (optionElement, checked) {
                optionElement.removeAttr('selected');
                if (checked) {
                    optionElement.prop('selected', 'selected');
                }
                element.change();
            }
        });

        // Watch for any changes to the length of our select element
        scope.$watch(function () {
            return element[0].length;
        }, function () {
            element.multiselect('rebuild');
        });

        // Watch for any changes from outside the directive and refresh
        //scope.$watch(attributes.ngModel, function () {
        //    element.multiselect('refresh');
        //});
    }
});
