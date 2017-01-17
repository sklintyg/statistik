/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
angular.module('StatisticsApp.treeMultiSelector.controller', [])
    .controller('treeMultiSelectorCtrl', ['$scope', '$element', 'treeMultiSelectorUtil', '$timeout', '_', function ($scope, $element, treeMultiSelectorUtil, $timeout, _) {
        'use strict';

        var self = this;

        $scope.value = {
            multiMenuFilter: null
        };

        $scope.showLabel = !$scope.hideLabel;

        $scope.transitionsSupported = supportsTransitions();

        $scope.clickedDone = function () {
            $scope.dialogOpen = false;
            $scope.doneClicked();
        };

        $scope.doneRenderingDialog = function () {
            $scope.$parent.doneLoading = true;
            $scope.doneLoading = true;
        };

        $scope.recursionhelper = {
            itemclick: function (item, $event) {
                $scope.itemClicked(item);
                $event.stopPropagation();
            },

            hideclick: function (item, $event) {
                $scope.hideClicked(item);
                $event.stopPropagation();
            }
        };

        function hasNoMenuOptionsOrSubs() {
            return !$scope.menuOptions || !$scope.menuOptions.subs || $scope.menuOptions.subs.length === 0;
        }

        function selectedTertiaryCount() {
            return hasNoMenuOptionsOrSubs() ? 0 : _.reduce($scope.menuOptions.subs, function (memo, sub) {
                return memo + (self.selectedLeavesCount(sub) > 0 ? 1 : 0);
            }, 0);
        }

        function selectedSecondaryCount() {
            return hasNoMenuOptionsOrSubs() ? 0 : _.reduce($scope.menuOptions.subs, function (acc, item) {
                var nodeSum = _.reduce(item.subs, function (memo, sub) {
                    return memo + (self.selectedLeavesCount(sub) > 0 ? 1 : 0);
                }, 0);
                return acc + nodeSum;
            }, 0);
        }

        function selectedLeavesCountAll() {
            return !$scope.menuOptions ? 0 : self.selectedLeavesCount($scope.menuOptions);
        }

        self.selectedLeavesCount = function selectedLeavesCount(node) {
            if (node.subs && node.subs.length > 0) {
                return _.reduce(node.subs, function (acc, item) {
                    return acc + self.selectedLeavesCount(item);
                }, 0);
            } else {
                return node.allSelected ? 1 : 0;
            }
        };

        $scope.itemClicked = function (item) {
            if (item.allSelected) {
                $scope.deselectAll(item);
            } else {
                $scope.selectAll(item);
            }

            updateSelections();
        };

        function updateSelections() {
            $scope.updateCounters();
            $scope.updateState($scope.menuOptions);
            $scope.$emit('selectionsChanged');
        }

        $scope.$on('updateSelections', function() {
            updateSelections();
        });

        $scope.hideClicked = function (item) {
            item.showChildren = !item.showChildren;
        };

        $scope.deselectAll = function (item) {
            item.allSelected = false;
            item.someSelected = false;
            if (item.subs) {
                _.each(item.subs, function (sub) {
                    $scope.deselectAll(sub);
                });
            }
        };

        $scope.selectAll = function (item) {
            item.allSelected = true;
            item.someSelected = false;
            if (item.subs) {
                _.each(item.subs, function (sub) {
                    $scope.selectAll(sub);
                });
            }
        };

        function expandIfOnlyOneVisible(items) {
            var visibleItems = _.reject(items, function (item) {
                return item.hide;
            });
            if (visibleItems.length === 1) {
                var item = visibleItems[0];
                item.showChildren = true;
                expandIfOnlyOneVisible(item.subs);
            }
        }

        var currentFiltering = null;
        $scope.filterMenuItems = function (items, text) {
            if (currentFiltering !== null) {
                $timeout.cancel(currentFiltering);
            }
            currentFiltering = $timeout(function () {
                _.each(items, function (item) {
                    $scope.updateItemHiddenState(item, getIsMatchingFilterFunction(text));
                });
                expandIfOnlyOneVisible(items);
                $scope.$evalAsync();
                currentFiltering = null;
            }, 0, false);
        };

        function getIsMatchingFilterFunction(searchText) {
            var text = searchText.toLowerCase();
            return function isMatchingFilter(item) {
                return item.name.toLowerCase().indexOf(text) >= 0;
            };
        }

        $scope.updateItemHiddenState = function (item, shouldItemBeVisibleFunction) {
            var childVisibilityFunction = shouldItemBeVisibleFunction;
            var hide = true;
            if (shouldItemBeVisibleFunction(item)) {
                hide = false;
                childVisibilityFunction = function () {
                    return true;
                };
            }
            item.hide = !!_.reduce(item.subs, function (mem, sub) {
                return $scope.updateItemHiddenState(sub, childVisibilityFunction) && mem;
            }, hide);
            return item.hide;
        };

        $scope.updateCounters = function () {
            $scope.selectedTertiaryCount = selectedTertiaryCount();
            $scope.selectedSecondaryCount = selectedSecondaryCount();
            $scope.selectedLeavesCountAll = selectedLeavesCountAll();
        };

        $scope.updateState = function (item) {
            treeMultiSelectorUtil.updateSelectionState(item);
        };

        $scope.openDialogClicked = function () {
            $scope.doneLoading = false;
            if (angular.isFunction($scope.onOpen)) {
                $scope.onOpen();
            }

            $timeout(function () {
                if ($scope.dialogOpen) {
                    $scope.doneLoading = true;
                }
                $scope.dialogOpen = true;
                $scope.updateCounters();
                resetFilter();
                $timeout(function () {
                    $scope.$parent.doneLoading = true;
                }, 1, false);
            }, 1);
        };

        function resetFilter() {
            $scope.value.multiMenuFilter = '';
            _.each($scope.menuOptions.subs, function (item) {
                $scope.updateItemHiddenState(item, function () {
                    return true;
                });
            });
        }

        $scope.$watch('value.multiMenuFilter', function(value) {
            if (value !== null) {
                $scope.filterMenuItems($scope.menuOptions.subs, value);
            }
        });

        $scope.dialogOpen = false;

        //http://stackoverflow.com/questions/7264899/detect-css-transitions-using-javascript-and-without-modernizr
        function supportsTransitions() {
            var b = document.body || document.documentElement,
                s = b.style,
                p = 'transition';

            if (typeof s[p] === 'string') { return true; }

            // Tests for vendor specific prop
            var v = ['Moz', 'webkit', 'Webkit', 'Khtml', 'O', 'ms'];
            p = p.charAt(0).toUpperCase() + p.substr(1);

            for (var i=0; i<v.length; i++) {
                if (typeof s[v[i] + p] === 'string') { return true; }
            }

            return false;
        }
        //Manually resize modal window to make room for sidebar (if used)
        if ($scope.sidebarMenuExpand) {
                $scope.$watch('sidebarState.collapsed', function(newValue) {
                    var modalDialog = angular.element($element[0].getElementsByClassName('modal-dialog')[0]);
                    if (newValue === true) {
                        modalDialog.css('width', '600px');
                    } else {
                        modalDialog.css('width', '900px');
                    }
                }, true);
        }
    }]);
