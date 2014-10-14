'use strict';

angular.module('StatisticsApp').directive("navigationaware", function ($rootScope, $location) {
    
    var isActivePage = function(currentRoute, navLinkAttrs) {
        return currentRoute.controllerAs === navLinkAttrs.ctrlname || currentRoute.controller === navLinkAttrs.ctrlname;
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

angular.module('StatisticsApp').directive("spinner", function() {
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

angular.module('StatisticsApp').directive("dataerrorview", function() {
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

angular.module('StatisticsApp').directive('legendHeight', function() {
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

angular.module('StatisticsApp').directive('multiselectDropdown', function () {
    function multiselect_selected($el) {
        var ret = true;
        $('option', $el).each(function(element) {
            if (!!!$(this).prop('selected')) {
                ret = false;
            }
        });
        return ret;
    }

    return function(scope, element, attrs) {
        element.multiselect({
            numberDisplayed : 1,
            buttonText: function (options, select) {
                if (options.length == 0) {
                    return 'Inga valda' + ' <b class="caret"></b>';
                }
                if (multiselect_selected(select)) {
                    return 'Alla valda' + ' <b class="caret"></b>';
                }
                if (options.length > this.numberDisplayed) {
                    return options.length + ' valda' + ' <b class="caret"></b>';
                }
                else {
                    var selected = '';
                    options.each(function () {
                        var label = ($(this).attr('label') !== undefined) ? $(this).attr('label') : $(this).html();
                        selected += label + ', ';
                    });
                    return selected.substr(0, selected.length - 2) + ' <b class="caret"></b>';
                }
            },
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
        scope.$watchCollection(attrs.ngModel, function () {
            element.multiselect('refresh');
        });
    }
});

angular.module('StatisticsApp').directive('intermediate', function() {
    return function(scope, element, attrs) {
        scope.$watch(attrs.intermediate, function (newVal) {
            element[0].indeterminate = newVal;
        });
    }
});

angular.module('StatisticsApp').directive("submenu", function (recursionService) {
    return {
        restrict: "E",
        scope: { item: "=", itemroot: "=", depth: "=", recursionhelper: "=" },
        template:
            '<span class="glyphicon" ng-class="{glyphiconMinusSign: !item.hideSiblings, glyphiconPlusSign: item.hideSiblings}"/>' +
            '<span ng-click="item.hideSiblings = !item.hideSiblings" class="ellipsis-text">{{item.name}}</span>' +
            '<input type="checkbox" ng-checked="item.allSelected" intermediate="item.someSelected" ng-click="recursionhelper.itemclick(item, itemroot)"/>' +
            '<ul ng-init="item.hideSiblings=true" ng-show="item.subs && !item.hideSiblings" style="list-style-type: none;">' +
              '<li data-ng-init="depth=depth+1" data-ng-repeat="item in item.subs">' +
                '<submenu item="item" itemroot="itemroot" depth="depth" recursionhelper="recursionhelper" ng-hide="item.hide" ng-class="{leaf: !item.subs}" class="depth{{depth}}"></submenu>' +
              '</li>' +
            '</ul>',
        compile: recursionService.compile
    };
});

angular.module('common').directive('message',
    [ '$log', '$rootScope', 'messageService',
        function($log, $rootScope, messageService) {
            'use strict';

            return {
                restrict: 'EA',
                scope: {
                    'key': '@',
                    'param': '=',
                    'params': '='
                },
                replace: true,
                template: '<span ng-bind-html="resultValue"></span>',
                link: function(scope, element, attr) {
                    var result;
                    // observe changes to interpolated attribute
                    attr.$observe('key', function(interpolatedKey) {
                        var normalizedKey = angular.lowercase(interpolatedKey);
                        var useLanguage;
                        if (typeof attr.lang !== 'undefined') {
                            useLanguage = attr.lang;
                        } else {
                            useLanguage = $rootScope.lang;
                        }

                        result = messageService.getProperty(normalizedKey, null, attr.fallback, useLanguage,
                            (typeof attr.fallbackDefaultLang !== 'undefined'));

                        if (typeof scope.param !== 'undefined') {
                            $log.debug(scope.param);
                            result = result.replace('%0', scope.param);
                        } else {
                            if (typeof scope.params !== 'undefined') {
                                var myparams = scope.params;
                                for (var i = 0; i < myparams.length; i++) {
                                    result = result.replace('%' + i, myparams[i]);
                                }
                            }
                        }

                        // now get the value to display..
                        scope.resultValue = result;
                    });
                }
            };
        }]);
