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
    function(AppModel, _, ControllerCommons) {
        'use strict';
        return {
            scope: {
                businessFilter: '=',
                businessFilterSaved: '='
            },
            restrict: 'E',
            templateUrl: '/components/directives/filterChips/filterChips.html',
            link: function($scope) {
                $scope.haveChips = false;

                $scope.chips = {
                    diagnos: [],
                    enheter: [],
                    sjukskrivningslangd: [],
                    aldersgrupp: []
                };

                //$scope.$watchCollection('businessFilter.geographyBusinessIds', enhetsFilter);
                //$scope.$watchCollection('businessFilterSaved.geographyBusinessIds', enhetsFilter);

                $scope.$watchCollection('businessFilter.selectedDiagnoses', diagnosFilter);
                $scope.$watchCollection('businessFilterSaved.selectedDiagnoses', diagnosFilter);

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
                    case 'diagnos':
                        _.pull($scope.businessFilter.selectedDiagnoses, chip.id);

                        $scope.businessFilter.selectDiagnoses($scope.businessFilter.selectedDiagnoses);
                        break;
                    case 'enhet':
                        _.pull($scope.businessFilter.geographyBusinessIds, chip.id);
                        break;
                    }
                };

                /*function enhetsFilter() {
                    //that.getDiagnosFilterInformationText(diagnosIds, diagnoses) : null;

                    var filter = $scope.businessFilter.geographyBusinessIds;

                    $scope.chips.enheter.length = 0;
                    angular.forEach(filter, function(enhet) {
                        $scope.chips.enheter.push({
                            type: 'enhet',
                            icon: 'fa-building-o',
                            id: enhet,
                            newFilter: $scope.businessFilterSaved.geographyBusinessIds.indexOf(enhet) === -1,
                            text: enhet
                        });
                    });

                    setHaveChips();
                }*/

                function diagnosFilter() {
                    var icd10 = $scope.businessFilterSaved.icd10.untuched;

                    var filter = $scope.businessFilter.selectedDiagnoses;

                    filter = ControllerCommons.getDiagnosFilterInformationText(filter, icd10, true);

                    $scope.chips.diagnos.length = 0;
                    angular.forEach(filter, function(diagnos) {
                        $scope.chips.diagnos.push({
                            type: 'diagnos',
                            icon: 'fa-stethoscope',
                            id: diagnos.id,
                            newFilter: $scope.businessFilterSaved.selectedDiagnoses.indexOf(diagnos.id) === -1,
                            text: diagnos.text
                        });
                    });

                    setHaveChips();
                }

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

                    setHaveChips();
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

                    setHaveChips();
                }

                function setHaveChips() {
                    $scope.haveChips = $scope.chips.aldersgrupp.length > 0 || $scope.chips.sjukskrivningslangd.length > 0 ||
                        $scope.chips.enheter.length > 0 || $scope.chips.diagnos.length > 0;
                }
            }
        };
    });