'use strict';

angular.module('StatisticsApp').directive("navigationaware", function ($rootScope) {

    var isActivePage = function(currentRoute, navLinkAttrs) {
        return currentRoute.controllerAs === navLinkAttrs.ctrlname || currentRoute.controller === navLinkAttrs.ctrlname;
    };
    
    return {
        restrict: "A",
        link: function ($scope, elem, $attrs) {
            $rootScope.$on('$routeChangeSuccess', function(angularEvent, current, previous) {
                elem.parent().removeClass("active");
                if (isActivePage(current, $attrs)){
                    elem.parent().addClass("active");
                    var groupId = elem.closest(".navigation-group").attr('id');
                    if (groupId) {
                        $rootScope.$broadcast('navigationUpdate', groupId);
                    }
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
                '<div ng-show="showError">' +
                    '<div ng-include="\'views/error/failedToFetchData.html\'"></div>' +
                '</div>' +
                '  <div ng-show="!showError">' +
                '    <div ng-transclude></div>' +
                '  </div>' +
            '</div>'
    };
});

angular.module('StatisticsApp').directive('legendHeight', function() {
    return function(scope, element) {
       if (scope.$last) {
          scope.$watch('seriesData', function(){
            var heights = _.map(element.parent().children(), function(e){return $(e).height();});
            var maxHeight = Math.max.apply(null, heights);
            element.parent().children().css('height', maxHeight);
          });
       }   
    };
});

angular.module('StatisticsApp').directive('multiselectDropdown', function (businessFilter) {
    function multiselectSize($el) {
        return $('option', $el).length;
    }

    return function(scope, element, attrs) {
        element.multiselect({
            buttonText: function (options, select) {
                return options.length + ' av ' + multiselectSize(select) + ' valda <b class="caret"></b>';
            },
            onChange: function (optionElement, checked) {
                optionElement.removeAttr('selected');
                if (checked) {
                    optionElement.prop('selected', 'selected');
                }
                businessFilter.filterChanged();
                element.change();
            },
            includeSelectAllOption: true,
            selectAllText: "Markera alla"
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
    };
});

angular.module('StatisticsApp').directive('intermediate', function() {
    return function(scope, element, attrs) {
        scope.$watch(attrs.intermediate, function (newVal) {
            element[0].indeterminate = newVal;
        });
    };
});

angular.module('StatisticsApp').directive('bindonce', function () {
    return {
        link:function (scope, elem, attr, ctrl) {
            elem.text(scope.$eval(attr.bindonce));
        }
    };
});

angular.module('StatisticsApp').directive('onFinishRender', function () {
    return {
        restrict: 'A',
        link: function (scope, element, attr) {
            if (scope.$last === true) {
                scope.$evalAsync(attr.onFinishRender);
            }
        }
    };
});

angular.module('StatisticsApp').directive("submenu", function (recursionService) {
    return {
        restrict: "E",
        scope: { item: "=", itemroot: "=", depth: "=", recursionhelper: "=" },
        template:
            '<span ng-click="recursionhelper.hideclick(item)" class="ellipsis-text"><span class="glyphicon" ng-class="{glyphiconMinusSign: !item.hideChildren, glyphiconPlusSign: item.hideChildren}"/><span bindonce="item.name"></span></span>' +
            '<input type="checkbox" ng-checked="item.allSelected" intermediate="item.someSelected" ng-click="recursionhelper.itemclick(item, itemroot)"/>' +
            '<ul ng-init="item.hideChildren=true" ng-show="item.subs && !item.hideChildren" style="list-style-type: none;">' +
              '<li data-ng-init="depth=depth+1" data-ng-repeat="item in item.subs">' +
                '<submenu item="item" itemroot="itemroot" depth="depth" recursionhelper="recursionhelper" ng-hide="item.hide" ng-class="{leaf: !item.subs}" class="depth{{depth}}"></submenu>' +
              '</li>' +
            '</ul>',
        compile: recursionService.compile
    };
});

angular.module('StatisticsApp').directive("filterButton", function () {
    return {
    	restrict: "E",
	    template:
	        '<button id="show-hide-filter-btn" type="button" class="btn btn-small pull-right" ng-class="{filterbtnactivefilter: filterIsActive}" ng-click="isFilterCollapsed = !isFilterCollapsed">' +
	        '<i class="glyphicon" ng-class="{glyphiconDownSign: isFilterCollapsed, glyphiconUpSign: !isFilterCollapsed}"></i> {{!isFilterCollapsed ? "Dölj filter" : "Visa filter"}}<span style="font-size: 12px; font-style: italic;"><br/>{{filterButtonIdText}}</span><span ng-show="filterIsActive" style="font-size: 12px; font-style: italic;"><br/>Val gjorda</span>' +
	        '</button>'
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
