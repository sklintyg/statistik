'use strict';

angular.module('StatisticsApp').factory('treeMultiSelectUtil', ['_',
    function (_) {

        var externalApi = {};

        externalApi.updateSelectionState = function(item) {
            if (item.subs && item.subs.length != 0) {
                var someSelected = false;
                var allSelected = true;
                _.each(item.subs, function (sub) {
                    if (!sub.hide) {
                        externalApi.updateSelectionState(sub);
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
        };

        return externalApi;

    }]);
