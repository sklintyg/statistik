/*
 * Copyright (C) 2013 - 2016 Inera AB (http://www.inera.se)
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

angular.module('StatisticsApp.filter.directive')
    .directive('diagnosSelection', [function() {
        return {
            scope: {
                showDetailsOptions: '=',
                showDetailsOptions2: '=',
                showDetailsOptions3: '=',
                detailsOptions: '=',
                detailsOptions2: '=',
                detailsOptions3: '=',
                selectedDetailsOption: '=',
                selectedDetailsOption2: '=',
                selectedDetailsOption3: '='
            },
            restrict: 'E',
            templateUrl: 'js/app/shared/diagnosSelection/diagnosSelection.directive.html',
            controller: function($scope) {
                $scope.hideDiagnosCategorySelection = true;

                $scope.$watch('detailsOptions3.length', function(length) {
                    $scope.hideDiagnosCategorySelection = length === 0;
                });
            }
        };
    }]);