/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
    function(StaticFilterDataService, StaticFilterData, _, ControllerCommons, $uibModal, $timeout, $window) {
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
                    enheter: [],
                    diagnos: [],
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

                StaticFilterDataService.get().then(function() { //Make sure StaticFilterData is populated
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
                            calcMaxNumberOfChips();
                        }
                    });
                });
                $($window).on('resize.doResize', _.debounce(function () {
                    if (!$scope.isCollapsed) {
                        $scope.$apply(function() {
                            calcMaxNumberOfChips();
                        });
                    }
                }, 100));

                $scope.$on('$destroy', function () {
                    $($window).off('resize.doResize'); //remove the handler added earlier
                });

                //Watch width of potential window scrollbar
                $scope.$watch(
                    function() {
                        return $window.innerWidth - $window.document.body.clientWidth;
                    },
                    function(newVal, oldVal) {
                        if (newVal > oldVal) {
                            //Scollbar must just have appeared - recalculate to compensate for this
                            calcMaxNumberOfChips();
                        }
                });


                $scope.removeChip = function(chip) {

                    switch(chip.type) {
                    case 'sjukskrivningslangd':
                        if (chip.removedFilter) {
                            $scope.businessFilter.selectedSjukskrivningslangdIds.push(chip.id);
                        } else {
                            _.pull($scope.businessFilter.selectedSjukskrivningslangdIds, chip.id);
                        }
                        break;
                    case 'aldersgrupp':
                        if (chip.removedFilter) {
                            $scope.businessFilter.selectedAldersgruppIds.push(chip.id);
                        } else {
                            _.pull($scope.businessFilter.selectedAldersgruppIds, chip.id);
                        }
                        break;
                    case 'diagnos':
                        if (chip.removedFilter) {
                            $scope.businessFilter.selectedDiagnoses.push(chip.id);
                        } else {
                            _.pull($scope.businessFilter.selectedDiagnoses, chip.id);
                        }
                        break;
                    case 'enhet':
                        if (chip.removedFilter) {
                            $scope.businessFilter.geographyBusinessIds.push(chip.id);
                        } else {
                            _.pull($scope.businessFilter.geographyBusinessIds, chip.id);
                        }
                        break;
                    }
                };

                function enhetsFilter() {
                    var filter = $scope.businessFilter.geographyBusinessIds;
                    filter = _.uniq(filter);

                    var enheter = [];

                    angular.forEach(filter, function(enhet) {
                        var text = _.find($scope.businessFilter.businesses, {id: enhet});
                        enheter.push({
                            type: 'enhet',
                            icon: 'fa-building-o',
                            id: enhet,
                            newFilter: $scope.businessFilter.geographyBusinessIdsSaved.indexOf(enhet) === -1,
                            removedFilter: false,
                            text: text ? text.name : enhet
                        });
                    });

                    var left =  _.difference($scope.businessFilter.geographyBusinessIdsSaved, $scope.businessFilter.geographyBusinessIds);

                    angular.forEach(left, function(enhet) {
                        var text = _.find($scope.businessFilter.businesses, {id: enhet});
                        enheter.push({
                            type: 'enhet',
                            icon: 'fa-building-o',
                            id: enhet,
                            newFilter: false,
                            removedFilter: true,
                            text: text ? text.name : enhet
                        });
                    });

                    $scope.chips.enheter = _.sortBy(enheter, function(n) {
                        return n.id;
                    });

                    setHaveChips();
                }

                function diagnosFilter() {
                    var icd10 = $scope.businessFilter.icd10.untuched;

                    var filter = $scope.businessFilter.selectedDiagnoses;

                    filter = ControllerCommons.getDiagnosFilterInformationText(filter, icd10, true);

                    var diagnoser = [];
                    angular.forEach(filter, function(diagnos) {
                        diagnoser.push({
                            type: 'diagnos',
                            icon: 'fa-stethoscope',
                            id: diagnos.id,
                            newFilter: $scope.businessFilter.diagnoserSaved.indexOf(diagnos.id) === -1,
                            removedFilter: false,
                            text: diagnos.text
                        });
                    });

                    var left =  _.difference($scope.businessFilter.diagnoserSaved, $scope.businessFilter.selectedDiagnoses);

                    left = ControllerCommons.getDiagnosFilterInformationText(left, icd10, true);

                    angular.forEach(left, function(diagnos) {
                        diagnoser.push({
                            type: 'diagnos',
                            icon: 'fa-stethoscope',
                            id: diagnos.id,
                            newFilter: false,
                            removedFilter: true,
                            text: diagnos.text
                        });
                    });

                    $scope.chips.diagnos = _.sortBy(diagnoser, function(n) {
                        return n.id;
                    });

                    setHaveChips();
                }

                function sjukskrivningsLangdsFilter() {
                    var filter = $scope.businessFilter.selectedSjukskrivningslangdIds;
                    filter = _.uniq(filter);

                    var sjukskrivningslangder = [];
                    angular.forEach(filter, function(sjukskrivningslangd) {
                        sjukskrivningslangder.push({
                            type: 'sjukskrivningslangd',
                            icon: 'fa-calendar',
                            id: sjukskrivningslangd,
                            newFilter: $scope.businessFilter.sjukskrivningslangdSaved.indexOf(sjukskrivningslangd) === -1,
                            removedFilter: false,
                            text: StaticFilterData.get().sjukskrivningLengthsObject[sjukskrivningslangd]
                        });
                    });

                    var left =  _.difference($scope.businessFilter.sjukskrivningslangdSaved, $scope.businessFilter.selectedSjukskrivningslangdIds);

                    angular.forEach(left, function(sjukskrivningslangd) {
                        sjukskrivningslangder.push({
                            type: 'sjukskrivningslangd',
                            icon: 'fa-calendar',
                            id: sjukskrivningslangd,
                            newFilter: false,
                            removedFilter: true,
                            text: StaticFilterData.get().sjukskrivningLengthsObject[sjukskrivningslangd]
                        });
                    });

                    $scope.chips.sjukskrivningslangd = _.sortBy(sjukskrivningslangder, function(n) {
                        return n.id;
                    });

                    setHaveChips();
                }

                function aldersGruppsFilter() {
                    var filter = $scope.businessFilter.selectedAldersgruppIds;
                    filter = _.uniq(filter);

                    var aldersgrupper = [];
                    angular.forEach(filter, function(aldersGrupp) {
                        aldersgrupper.push({
                            type: 'aldersgrupp',
                            id: aldersGrupp,
                            icon: 'fa-users',
                            newFilter: $scope.businessFilter.aldersgruppSaved.indexOf(aldersGrupp) === -1,
                            removedFilter: false,
                            text: StaticFilterData.get().ageGroups[aldersGrupp]
                        });
                    });

                    var left =  _.difference($scope.businessFilter.aldersgruppSaved, $scope.businessFilter.selectedAldersgruppIds);

                    angular.forEach(left, function(aldersGrupp) {
                        aldersgrupper.push({
                            type: 'aldersgrupp',
                            id: aldersGrupp,
                            icon: 'fa-users',
                            newFilter: false,
                            removedFilter: true,
                            text: StaticFilterData.get().ageGroups[aldersGrupp]
                        });
                    });

                    $scope.chips.aldersgrupp = _.sortBy(aldersgrupper, function(n) {
                        return n.id;
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

                    $scope.haveChips = $scope.allChips.length > 0;
                }

                function calcMaxNumberOfChips() {
                    $timeout(function() {
                        var filterElement = element.find('.filter-level .col-xs-12');
                        var width = filterElement.width();
                        var allChips = $scope.allChips.length;
                        var numberOfChips = getNrChipsThatFits2Rows($scope.allChips, width);
                        if (numberOfChips < allChips) {
                            $scope.numberOfChipsNotShown = allChips - numberOfChips;
                        } else {
                            $scope.numberOfChipsNotShown = 0;
                        }

                        $scope.shownChips = $scope.allChips.slice(0, numberOfChips);
                    });
                }

                function getNrChipsThatFits2Rows(chips, availableWidth) {

                    var chipCount = getNrChipsThatFitsRow(chips, 0, availableWidth);
                    if (chipCount < chips.length) {
                        chipCount += getNrChipsThatFitsRow(chips, chipCount, availableWidth);
                    }

                    if (chipCount<chips.length) {
                        //Make room for "x fler filter" text that will appear
                        return chipCount -1;
                    }
                    return chipCount;
                }
                function getNrChipsThatFitsRow(chips, startFrom, availableWidth) {

                    var usedWidth = 0;
                    var measureContainer = $('#chip-measureContainer');

                    for (var i = startFrom; i < chips.length; i++) {

                        var chipWidth = getChipWidth(measureContainer, chips[i]);
                        usedWidth += chipWidth;

                        //Will this chip also fit?
                        if ((availableWidth - usedWidth) <= 0) {
                            //No, this chip won't fit. return how many that fits row 1 and at which chip wer'e at.
                            return i - startFrom;
                        }
                    }

                    //If we get this far - all chips requested should fit row.
                    return chips.length - startFrom;
                }

                function getChipWidth(container, chip) {
                    //Temporary add, measure and remove the chip's html equivalent.
                    var elem = $('<button class="filter-chip">' +
                        '<span class="default-text"><i class="fa ' + chip.icon + '"></i> ' +
                        chip.text + '</span>' +
                        '</button>');
                    container.append(elem);
                    var chipWidth = elem.outerWidth(true);
                    elem.remove();
                    return chipWidth;
                }

            }
        };
    });
