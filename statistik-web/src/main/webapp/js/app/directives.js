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
        template:
            '<span ng-click="recursionhelper.hideclick(item)" class="ellipsis-text"><span class="glyphicon" ng-class="{glyphiconMinusSign: !item.hideChildren, glyphiconPlusSign: item.hideChildren}"/>{{item.name}}</span>' +
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
	        '<button id="show-hide-filter-btn" type="button" class="btn btn-small pull-right" data-toggle="collapse" data-target="#statistics-filter-container" ng-click="filter.open = !filter.open">' +
	        '<i class="glyphicon" ng-class="{glyphiconDownSign: !filter.open, glyphiconUpSign: filter.open}"></i> {{filter.open ? "Dölj filter" : "Visa filter"}}' +
	        '</button>'
    };
});

angular.module('StatisticsApp').directive("treeMultiSelector", ['$compile', '$templateCache', 'messageService', function ($compile, $templateCache, messageService) {
    return {
        restrict: 'EA',
        scope: {
            menuOptions: '=', //Each item in the array has properties "name (for label) and "subs" (for sub items)
            doneClicked: '=', //The function to call when the selection is accepted by the user
            textData: '='
        },
        controller: function ($scope, $element, $attrs, $transclude) {
            $scope.clickedDone = function(){
                $scope.doneClicked();
            };

            $scope.recursionhelper = {
                itemclick: function (item) {
                    $scope.itemClicked(item);
                },

                hideclick: function (item) {
                    $scope.hideClicked(item);
                }
            };

            function selectedTertiaryCount() {
                if (!$scope.menuOptions || !$scope.menuOptions.subs || $scope.menuOptions.subs.length == 0) {
                    return 0;
                }
                return _.reduce($scope.menuOptions.subs, function (memo, sub) {
                    return memo + ($scope.selectedLeavesCount(sub) > 0 ? 1 : 0);
                }, 0);
            }

            function selectedSecondaryCount() {
                if (!$scope.menuOptions || !$scope.menuOptions.subs || $scope.menuOptions.subs.length == 0) {
                    return 0;
                }
                return _.reduce($scope.menuOptions.subs, function (acc, item) {
                    var nodeSum = _.reduce(item.subs, function (memo, sub) {
                        return memo + ($scope.selectedLeavesCount(sub) > 0 ? 1 : 0);
                    }, 0);
                    return acc + nodeSum;
                }, 0);
            }

            function selectedLeavesCountAll() {
                if (!$scope.menuOptions) {
                    return 0;
                }
                return $scope.selectedLeavesCount($scope.menuOptions);
            }

            $scope.selectedLeavesCount = function (node) {
                if (node.subs && node.subs.length > 0) {
                    return _.reduce(node.subs, function (acc, item) {
                        return acc + $scope.selectedLeavesCount(item);
                    }, 0);
                } else {
                    return node.allSelected ? 1 : 0;
                }
            };

            $scope.itemClicked = function (item) {
                if (item.allSelected) {
                    $scope.deselectAll(item);
                } else if (item.someSelected) {
                    $scope.selectAll(item);
                } else {
                    $scope.selectAll(item);
                }
                $scope.updateState($scope.menuOptions.subs);
            };

            $scope.hideClicked = function (item) {
                item.hideChildren = !item.hideChildren;
            };

            $scope.deselectAll = function (item) {
                if (!item.hide) {
                    item.allSelected = false;
                    item.someSelected = false;
                    if (item.subs) {
                        _.each(item.subs, function (sub) {
                            $scope.deselectAll(sub);
                        });
                    }
                }
            };

            $scope.selectAll = function (item, selectHidden) {
                if (!item.hide || selectHidden) {
                    item.allSelected = true;
                    item.someSelected = false;
                    if (item.subs) {
                        _.each(item.subs, function (sub) {
                            $scope.selectAll(sub, selectHidden);
                        });
                    }
                }
            };

            function expandIfOnlyOneVisible(items) {
                var visibleItems = _.reject(items, function (item) {
                    return item.hide;
                });
                if (visibleItems.length === 1) {
                    var item = visibleItems[0];
                    item.hideChildren = false;
                    $scope.updateState(item);
                    expandIfOnlyOneVisible(item.subs);
                }
            }

            $scope.filterMenuItems = function (items, text) {
                var searchText = text.toLowerCase();
                var mappingFunc = function (item) {
                    if (item.subs) {
                        _.each(item.subs, mappingFunc);
                    }
                    item.hide = $scope.isItemHidden(item, searchText);
                };
                _.each(items, mappingFunc);
                _.each(items, $scope.updateState);
                expandIfOnlyOneVisible(items);
            };

            $scope.isItemHidden = function (item, searchText) {
                if (item.name.toLowerCase().indexOf(searchText) >= 0) {
                    return false;
                }
                if (!item.subs) {
                    return true;
                }
                return _.all(item.subs, function (sub) {
                    return $scope.isItemHidden(sub, searchText);
                });
            };

            $scope.updateCounters = function() {
                $scope.selectedTertiaryCount = selectedTertiaryCount();
                $scope.selectedSecondaryCount = selectedSecondaryCount();
                $scope.selectedLeavesCountAll = selectedLeavesCountAll();
            };

            $scope.updateState = function (item) {
                if (item.subs && item.subs.length != 0) {
                    var someSelected = false;
                    var allSelected = true;
                    _.each(item.subs, function (sub) {
                        if (!sub.hide) {
                            $scope.updateState(sub);
                            someSelected = someSelected || sub.someSelected || sub.allSelected;
                            allSelected = allSelected && sub.allSelected;
                        }
                    });
                    if (allSelected) {
                        item.allSelected = true;
                        item.someSelected = false;
                    } else {
                        item.allSelected = false;
                        item.someSelected = someSelected ? true : false;
                    }
                }
                $scope.updateCounters();
            };

            $scope.openDialogClicked = function(){
                $scope.updateCounters();
            };

        },
        templateUrl: 'views/treeMultiSelector.html'
    };
}]);

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

                        element.html('<span>' + result + '</span>')
                    }

                    attr.$observe('key', function(interpolatedKey) {
                        updateMessage(interpolatedKey);
                    });

                    updateMessage(attr.key);
                }
            };
        }]);
