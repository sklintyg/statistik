angular.module('StatisticsApp.treeMultiSelector.directive', [])
    .directive("treeMultiSelector", function () {
        'use strict';
        return {
            restrict: 'EA',
            scope: {
                menuOptions: '=', //Each item in the array has properties "name (for label) and "subs" (for sub items)
                doneClicked: '=', //The function to call when the selection is accepted by the user
                textData: '='
            },
            controller: 'treeMultiSelectorCtrl',
            templateUrl: 'js/app/shared/treemultiselector/treeMultiSelectorView.html'
        };
    });