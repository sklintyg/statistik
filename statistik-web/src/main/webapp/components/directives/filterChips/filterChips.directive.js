/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

angular.module('StatisticsApp')
    .directive('filterChips',
    /** @ngInject */
    function(AppModel, _) {
        'use strict';
        return {
            scope: {
                businessFilter: '=',
                businessFilterSaved: '='
            },
            restrict: 'E',
            templateUrl: '/components/directives/filterChips/filterChips.html',
            link: function($scope) {
                $scope.chips = {
                    sjukskrivningslangd: [],
                    aldersgrupp: []
                };

                $scope.$watchCollection('businessFilter.selectedAldersgruppIds', aldersGruppsFilter);
                $scope.$watchCollection('businessFilterSaved.selectedAldersgruppIds', aldersGruppsFilter);

                $scope.$watchCollection('businessFilter.selectedSjukskrivningslangdIds', sjukskrivningsLangdsFilter);
                $scope.$watchCollection('businessFilterSaved.selectedSjukskrivningslangdIds', sjukskrivningsLangdsFilter);

                $scope.removeChip = function(chip) {
                    switch(chip.type) {
                    case 'sjukskrivningslangd':
                        _.pull($scope.businessFilter.selectedSjukskrivningslangdIds, chip.id);
                        break;
                    case 'aldersgrupp':
                        _.pull($scope.businessFilter.selectedAldersgruppIds, chip.id);
                        break;
                    }
                };

                function sjukskrivningsLangdsFilter() {
                    var filter = $scope.businessFilter.selectedSjukskrivningslangdIds;

                    $scope.chips.sjukskrivningslangd.length = 0;
                    angular.forEach(filter, function(sjukskrivningslangd) {
                        $scope.chips.sjukskrivningslangd.push({
                            type: 'sjukskrivningslangd',
                            icon: 'fa-calendar',
                            id: sjukskrivningslangd,
                            newFilter: $scope.businessFilterSaved.selectedSjukskrivningslangdIds.indexOf(sjukskrivningslangd) === -1,
                            text: AppModel.get().sjukskrivningLengthsObject[sjukskrivningslangd]
                        });
                    });
                }

                function aldersGruppsFilter() {
                    var filter = $scope.businessFilter.selectedAldersgruppIds;

                    $scope.chips.aldersgrupp.length = 0;
                    angular.forEach(filter, function(aldersGrupp) {
                        $scope.chips.aldersgrupp.push({
                            type: 'aldersgrupp',
                            id: aldersGrupp,
                            icon: 'fa-users',
                            newFilter: $scope.businessFilterSaved.selectedAldersgruppIds.indexOf(aldersGrupp) === -1,
                            text: AppModel.get().ageGroups[aldersGrupp]
                        });
                    });
                }
            }
        };
    });