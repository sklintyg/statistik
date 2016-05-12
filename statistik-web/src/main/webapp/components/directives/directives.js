angular.module('StatisticsApp').directive('navigationaware', function ($rootScope) {
    'use strict';

    var isActivePage = function(currentRoute, navLinkAttrs) {
        return currentRoute.controllerAs === navLinkAttrs.ctrlname || currentRoute.controller === navLinkAttrs.ctrlname;
    };
    
    return {
        restrict: 'A',
        link: function ($scope, elem, $attrs) {
            $rootScope.$on('$routeChangeSuccess', function(angularEvent, current) {
                elem.parent().removeClass('active');
                if (isActivePage(current, $attrs)){
                    elem.parent().addClass('active');
                    var groupId = elem.closest('.navigation-group').attr('id');
                    if (groupId) {
                        $rootScope.$broadcast('navigationUpdate', groupId);
                    }
                }
            });
        }
    };
});

angular.module('StatisticsApp').directive('spinner', function() {
    'use strict';

    return {
        restrict : 'A',
        transclude : true,
        replace : true,
        scope : {
          label: '@',
          showSpinner: '=',
          showContent: '='
        },
        template :
            '<div>' +
           '  <div ng-show="showSpinner" class="spinner">' +
           '    <div class="cssspinner"></div>' +
           '    <p id="loading-message">' +
           '      <strong><span>{{ label }}</span></strong>' +
           '    </p>' +
           '  </div>' +
           '    <div ng-transclude></div>' +
           '</div>'
    };
});

angular.module('StatisticsApp').directive('dataerrorview', function() {
    'use strict';

    return {
        restrict : 'A',
        transclude : true,
        replace : true,
        scope : {
            errorPageUrl: '=',
            showError: '='
        },
        template :
            '<div>'+
                '<div ng-show="showError">' +
                    '<div ng-include="\'app/views/error/failedToFetchData.html\'"></div>' +
                '</div>' +
                '  <div ng-show="!showError">' +
                '    <div ng-transclude></div>' +
                '  </div>' +
            '</div>'
    };
});

angular.module('StatisticsApp').directive('legendHeight', ['_', function(_) {
    'use strict';

    return function(scope, element) {
       if (scope.$last) {
          scope.$watch('seriesData', function(){
            var heights = _.map(element.parent().children(), function(e){return $(e).height();});
            var maxHeight = Math.max.apply(null, heights);
            element.parent().children().css('height', maxHeight);
          });
       }   
    };
}]);

angular.module('StatisticsApp').directive('intermediate', function() {
    'use strict';

    return function(scope, element, attrs) {
        scope.$watch(attrs.intermediate, function (newVal) {
            element[0].indeterminate = newVal;
        });
    };
});

angular.module('StatisticsApp').directive('bindonce', function () {
    'use strict';

    return {
        link:function (scope, elem, attr) {
            elem.text(scope.$eval(attr.bindonce));
        }
    };
});

angular.module('StatisticsApp').directive('onFinishRender', function () {
    'use strict';

    return {
        restrict: 'A',
        link: function (scope, element, attr) {
            if (scope.$last === true) {
                scope.$evalAsync(attr.onFinishRender);
            }
        }
    };
});

angular.module('StatisticsApp').directive('sortableTable', function () {
    'use strict';

    return {
        link: function(scope, elm) {
            scope.$on('pageDataPopulated', function () {
                elm.tablesorter({sortReset: true, sortInitialOrder: 'desc'});
            });
        }
    };
});

angular.module('StatisticsApp').directive('submenu', function (recursionService) {
    'use strict';

    return {
        restrict: 'E',
        scope: { item: '=', itemroot: '=', depth: '=', recursionhelper: '=' },
        template:
            '<span ng-click="recursionhelper.hideclick(item)" class="ellipsis-text">' +
            '<span class="glyphicon" ng-class=""{glyphiconMinusSign: !item.hideChildren, glyphiconPlusSign: item.hideChildren}"/>' +
            '<span bindonce="item.name"></span></span>' +
            '<input type="checkbox" ng-checked="item.allSelected" intermediate="item.someSelected" ng-click="recursionhelper.itemclick(item)"/>' +
            '<ul ng-init="item.hideChildren=true" ng-show="item.subs && !item.hideChildren" style="list-style-type: none;">' +
              '<li data-ng-init="depth=depth+1" data-ng-repeat="item in item.subs">' +
                '<submenu item="item" itemroot="itemroot" depth="depth" recursionhelper="recursionhelper" ' +
                'ng-hide="item.hide" ng-class="{leaf: !item.subs}" class="depth{{depth}}"></submenu>' +
              '</li>' +
            '</ul>',
        compile: recursionService.compile
    };
});

angular.module('StatisticsApp').directive('message',
    [ '$log', '$rootScope', 'messageService',
        function($log, $rootScope, messageService) {
            'use strict';

            return {
                restrict: 'EA',
                scope: false,
                replace: true,
                link: function(scope, element, attr) {
                    var result;
                    // observe changes to interpolated attribute
                    function updateMessage(interpolatedKey) {
                        var normalizedKey = angular.lowercase(interpolatedKey);
                        var useLanguage;
                        if (typeof attr.lang !== 'undefined') {
                            useLanguage = attr.lang;
                        } else {
                            useLanguage = $rootScope.lang;
                        }

                        result = messageService.getProperty(normalizedKey, null, attr.fallback, useLanguage,
                            (typeof attr.fallbackDefaultLang !== 'undefined'));

                        if (typeof attr.param !== 'undefined') {
                            $log.debug(attr.param);
                            result = result.replace('%0', attr.param);
                        } else {
                            if (typeof attr.params !== 'undefined') {
                                var myparams = attr.params;
                                for (var i = 0; i < myparams.length; i++) {
                                    result = result.replace('%' + i, myparams[i]);
                                }
                            }
                        }

                        element.html('<span>' + result + '</span>');
                    }

                    attr.$observe('key', function(interpolatedKey) {
                        updateMessage(interpolatedKey);
                    });

                    updateMessage(attr.key);
                }
            };
        }]);

angular.module('StatisticsApp').directive('confirmClick', ['$window',
    function($window){
        'use strict';

        return {
            link: function (scope, element, attr) {
                var msg = attr.confirmMessage || 'Är du säker?';
                var clickAction = attr.confirmedClickAction;
                element.bind('click',function () {
                    if ($window.confirm(msg) ) {
                        scope.$eval(clickAction);
                    }
                });
            }
        };
    }]);

