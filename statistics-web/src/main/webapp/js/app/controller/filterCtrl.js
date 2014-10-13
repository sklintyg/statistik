'use strict';

angular.module('StatisticsApp').controller('filterCtrl', ['$scope', '$rootScope', 'statisticsData', 'businessFilter',
    function ($scope, $rootScope, statisticsData, businessFilter) {

        $scope.businessFilter = businessFilter;

        $scope.recursionhelper = {
            itemclick: function (item, itemRoot) {
                $scope.itemClicked(item, itemRoot);
            }
        };

        $scope.itemClicked = function (item, itemRoot) {
            businessFilter.itemClicked(item, itemRoot);
        };

        $scope.makeUnitSelection = function () {
            businessFilter.makeUnitSelection();
            $rootScope.$broadcast('filterChange', '');
        };

    }
]);
