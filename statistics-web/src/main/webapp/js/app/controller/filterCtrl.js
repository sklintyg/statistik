'use strict';

angular.module('StatisticsApp').controller('filterCtrl', ['$scope', '$rootScope', 'statisticsData', 'businessFilter', '_',
    function ($scope, $rootScope, statisticsData, businessFilter, _) {

        $scope.businessFilter = businessFilter;

        $scope.recursionhelper = {
            itemclick: function (item, itemRoot) {
                $scope.itemClicked(item, itemRoot);
            }
        };

        $scope.itemClicked = function (item, itemRoot) {
            if (item.allSelected) {
                businessFilter.deselectAll(item);
            } else if (item.someSelected) {
                businessFilter.selectAll(item);
            } else {
                businessFilter.selectAll(item);
            }
            businessFilter.updateState(itemRoot);
        };

        $scope.makeUnitSelection = function () {
            businessFilter.makeUnitSelection();
            $rootScope.$broadcast('filterChange', '');
        };

    }
]);
