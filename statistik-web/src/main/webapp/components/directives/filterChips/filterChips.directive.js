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
    function(AppModel, _, ControllerCommons, $uibModal, $timeout, $window) {
        'use strict';
        return {
            scope: {
                businessFilter: '=',
                isCollapsed: '='
            },
            restrict: 'E',
            templateUrl: '/components/directives/filterChips/filterChips.html',
            link: function($scope, element) {
                $scope.haveChips = false;
                $scope.shownChips = [];
                $scope.allChips = [];
                $scope.numberOfChipsNotShown = 0;

                $scope.chips = {
                    diagnos: [],
                    enheter: [],
                    sjukskrivningslangd: [],
                    aldersgrupp: []
                };

                $scope.showAll = function() {
                    $uibModal.open({
                        animation: true,
                        templateUrl: '/components/directives/filterChips/modal/modal.html',
                        controller: 'FilterChipsModalCtrl',
                        size: 'lg',
                        resolve: {
                            chips: function () {
                                return $scope.chips;
                            },
                            removeChip: function() {
                                return $scope.removeChip;
                            }
                        }
                    });
                };

                $scope.$watchCollection('businessFilter.geographyBusinessIds', enhetsFilter);
                $scope.$watchCollection('businessFilter.geographyBusinessIdsSaved', enhetsFilter);

                $scope.$watchCollection('businessFilter.selectedDiagnoses', diagnosFilter);
                $scope.$watchCollection('businessFilter.diagnoserSaved', diagnosFilter);

                $scope.$watchCollection('businessFilter.selectedAldersgruppIds', aldersGruppsFilter);
                $scope.$watchCollection('businessFilter.aldersgruppSaved', aldersGruppsFilter);

                $scope.$watchCollection('businessFilter.selectedSjukskrivningslangdIds', sjukskrivningsLangdsFilter);
                $scope.$watchCollection('businessFilter.sjukskrivningslangdSaved', sjukskrivningsLangdsFilter);

                $scope.$watchCollection('allChips', calcMaxNumberOfChips);

                $scope.$watch('isCollapsed', function(value) {
                    if (!value) {
                        $scope.shownChips = $scope.allChips;
                        calcMaxNumberOfChips();
                    }
                });

                $($window).on('resize.doResize', _.debounce(function () {
                    if (!$scope.isCollapsed) {
                        $scope.$apply(function() {
                            $scope.shownChips = $scope.allChips;
                            calcMaxNumberOfChips();
                        });
                    }
                }, 100));

                $scope.$on('$destroy', function () {
                    $($window).off('resize.doResize'); //remove the handler added earlier
                });

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

                        $scope.businessFilter.selectGeographyBusiness($scope.businessFilter.geographyBusinessIds);
                        break;
                    }
                };

                function enhetsFilter() {
                    var filter = $scope.businessFilter.geographyBusinessIds;

                    $scope.chips.enheter.length = 0;
                    angular.forEach(filter, function(enhet) {

                        var text = _.findWhere($scope.businessFilter.businesses, {id: enhet});
                        $scope.chips.enheter.push({
                            type: 'enhet',
                            icon: 'fa-building-o',
                            id: enhet,
                            newFilter: $scope.businessFilter.geographyBusinessIdsSaved.indexOf(enhet) === -1,
                            text: text ? text.name : enhet
                        });
                    });

                    setHaveChips();
                }

                function diagnosFilter() {
                    var icd10 = $scope.businessFilter.icd10.untuched;

                    var filter = $scope.businessFilter.selectedDiagnoses;

                    filter = ControllerCommons.getDiagnosFilterInformationText(filter, icd10, true);

                    $scope.chips.diagnos.length = 0;
                    angular.forEach(filter, function(diagnos) {
                        $scope.chips.diagnos.push({
                            type: 'diagnos',
                            icon: 'fa-stethoscope',
                            id: diagnos.id,
                            newFilter: $scope.businessFilter.diagnoserSaved.indexOf(diagnos.id) === -1,
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
                            newFilter: $scope.businessFilter.sjukskrivningslangdSaved.indexOf(sjukskrivningslangd) === -1,
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
                            newFilter: $scope.businessFilter.aldersgruppSaved.indexOf(aldersGrupp) === -1,
                            text: AppModel.get().ageGroups[aldersGrupp]
                        });
                    });

                    setHaveChips();
                }

                function setHaveChips() {
                    $scope.allChips.length = 0;

                    angular.forEach($scope.chips, function(type) {
                        angular.forEach(type, function(chip) {
                            $scope.allChips.push(chip);
                        });
                    });

                    $scope.shownChips = $scope.allChips;
                    $scope.haveChips = $scope.allChips.length > 0;
                }

                function calcMaxNumberOfChips() {
                    $timeout(function() {
                        var filterElement = element.find('.filter-level .col-xs-12');
                        var width = filterElement.width();
                        var numberOfRows = 1;
                        var maxNumberOfRows = 2;
                        var totalChipWith = 0;
                        var numberOfChips = 0;
                        var showAllWidth = element.find('.show-all-filter').outerWidth(true);
                        showAllWidth = showAllWidth < 80 ? 80 : showAllWidth;

                        $scope.numberOfChipsNotShown = 0;

                        var chips = filterElement.find('.filter-chip:not(.ng-leave)');

                        chips.each(function() {
                            var chipWidth = $( this ).outerWidth(true);

                            var addChip = false;

                            if (totalChipWith + chipWidth < width) {
                                addChip = true;
                            } else if (numberOfRows < maxNumberOfRows) {
                                numberOfRows++;
                                addChip = true;

                                if (numberOfRows === maxNumberOfRows) {
                                    totalChipWith = showAllWidth;
                                } else {
                                    totalChipWith = 0;
                                }
                            }

                            if (addChip) {
                                totalChipWith += chipWidth;
                                numberOfChips++;
                            } else {
                                $scope.numberOfChipsNotShown += 1;
                            }
                        });

                        $scope.shownChips = $scope.allChips.slice(0, numberOfChips);
                    });
                }
            }
        };
    });