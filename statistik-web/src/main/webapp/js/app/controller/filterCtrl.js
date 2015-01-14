'use strict';

angular.module('StatisticsApp').controller('filterCtrl', ['$scope', '$rootScope', 'statisticsData', 'businessFilter', '$timeout', 'messageService',
    function ($scope, $rootScope, statisticsData, businessFilter, $timeout, messageService) {

        $scope.businessFilter = businessFilter;
        function updateGeographyFilterSelectorDataButtonLabelText() {
            $scope.geographyFilterSelectorData.buttonLabelText = $scope.businessFilter.geographyBusinessIds.length + " av " + $scope.businessFilter.businesses.length + " valda";
        }

        $scope.$watch('businessFilter.geographyBusinessIds', function(newValue,oldValue,scope,d,e,f,g) {
            updateGeographyFilterSelectorDataButtonLabelText();
        });
        $scope.$watch('businessFilter.businesses', function(newValue,oldValue,scope,d,e,f,g) {
            updateGeographyFilterSelectorDataButtonLabelText();
        });
        $scope.$watch('businessFilter', function(newValue,oldValue,scope,d,e,f,g) {
            $scope.icd10 = newValue.icd10;
        });
        $scope.$watch('businessFilter.geography', function(newValue,oldValue,scope,d,e,f,g) {
            $scope.geography = newValue.geography;
        });

        $scope.geographyFilterSelectorData = {
            titleText:messageService.getProperty("lbl.filter.val-av-enheter", null, "", null, true),
            buttonLabelText:"Enheter",
            firstLevelLabelText:messageService.getProperty("lbl.filter.modal.lan", null, "", null, true),
            secondLevelLabelText:messageService.getProperty("lbl.filter.modal.kommuner", null, "", null, true),
            thirdLevelLabelText:messageService.getProperty("lbl.filter.modal.enheter", null, "", null, true)
        };

        $scope.diagnosisFilterSelectorData = {
            titleText:messageService.getProperty("lbl.filter.val-av-diagnoser", null, "", null, true),
            buttonLabelText:messageService.getProperty("lbl.filter.val-av-diagnoser-knapp", null, "", null, true),
            firstLevelLabelText:messageService.getProperty("lbl.filter.modal.kapitel", null, "", null, true),
            secondLevelLabelText:messageService.getProperty("lbl.filter.modal.avsnitt", null, "", null, true),
            thirdLevelLabelText:messageService.getProperty("lbl.filter.modal.kategorier", null, "", null, true)
        };

        $scope.makeUnitSelection = function () {
            $rootScope.$broadcast('filterChange', '');
        };

    }
]);
