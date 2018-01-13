/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
    function(StaticFilterDataService, StaticFilterData, _, $uibModal) {
        'use strict';
        return {
            scope: {
                businessFilter: '='
            },
            restrict: 'E',
            templateUrl: '/components/directives/filterChips/filterChips.html',
            link: function($scope) {
                $scope.haveChips = false;
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
                    $scope.$watchCollection('businessFilter.geographyBusinessIdsSaved', enhetsFilter);

                    $scope.$watchCollection('businessFilter.diagnoserSaved', diagnosFilter);

                    $scope.$watchCollection('businessFilter.aldersgruppSaved', aldersGruppsFilter);

                    $scope.$watchCollection('businessFilter.sjukskrivningslangdSaved', sjukskrivningsLangdsFilter);
                });

                function enhetsFilter() {
                    var filter = $scope.businessFilter.geographyBusinessIdsSaved;
                    filter = _.uniq(filter);

                    var enheter = [];

                    _.each(filter, function(enhet) {
                        var text = _.find($scope.businessFilter.businesses, {id: enhet});
                        enheter.push({
                            type: 'enhet',
                            icon: 'fa-building-o',
                            id: enhet,
                            text: text ? text.name : enhet
                        });
                    });

                    $scope.chips.enheter = _.sortBy(enheter, function(n) {
                        return n.id;
                    });

                    setHaveChips();
                }

                function diagnosFilter() {
                    var filter = $scope.businessFilter.diagnoserSaved;

                    filter = StaticFilterData.getDiagnosFilterInformationText(filter, true);

                    var diagnoser = [];
                    _.each(filter, function(diagnos) {
                        diagnoser.push({
                            type: 'diagnos',
                            icon: 'fa-stethoscope',
                            id: diagnos.id,
                            text: diagnos.text
                        });
                    });

                    $scope.chips.diagnos = _.sortBy(diagnoser, function(n) {
                        return n.id;
                    });

                    setHaveChips();
                }

                function sjukskrivningsLangdsFilter() {
                    var filter = $scope.businessFilter.sjukskrivningslangdSaved;
                    filter = _.uniq(filter);

                    var sjukskrivningslangder = [];
                    _.each(filter, function(sjukskrivningslangd) {
                        sjukskrivningslangder.push({
                            type: 'sjukskrivningslangd',
                            icon: 'fa-calendar',
                            id: sjukskrivningslangd,
                            text: StaticFilterData.get().sjukskrivningLengthsObject[sjukskrivningslangd]
                        });
                    });

                    $scope.chips.sjukskrivningslangd = _.sortBy(sjukskrivningslangder, function(n) {
                        return n.id;
                    });

                    setHaveChips();
                }

                function aldersGruppsFilter() {
                    var filter = $scope.businessFilter.aldersgruppSaved;
                    filter = _.uniq(filter);

                    var aldersgrupper = [];
                    _.each(filter, function(aldersGrupp) {
                        aldersgrupper.push({
                            type: 'aldersgrupp',
                            id: aldersGrupp,
                            icon: 'fa-users',
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

                    _.each($scope.chips, function(type) {
                        _.each(type, function(chip) {
                            $scope.allChips.push(chip);
                        });
                    });

                    $scope.haveChips = $scope.allChips.length > 0;
                }
            }
        };
    });
