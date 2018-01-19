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
    function(StaticFilterData, _, $uibModal, moment) {
        'use strict';
        return {
            scope: {
                businessFilter: '='
            },
            restrict: 'E',
            templateUrl: '/components/directives/filterChips/filterChips.html',
            link: function($scope) {

                $scope.chips = {
                    date: [],
                    enheter: [],
                    diagnos: [],
                    sjukskrivningslangd: [],
                    aldersgrupp: [],
                    intygstyper: []
                };

                $scope.showAll = function() {
                    createChips();

                    $uibModal.open({
                        animation: true,
                        templateUrl: '/components/directives/filterChips/modal/modal.html',
                        controller: 'FilterChipsModalCtrl',
                        size: 'lg',
                        resolve: {
                            chips: function () {
                                return $scope.chips;
                            }
                        }
                    });
                };

                function createChips() {
                    dateFilter();
                    enhetsFilter();
                    diagnosFilter();
                    aldersGruppsFilter();
                    sjukskrivningsLangdsFilter();
                    intygstyperFilter();
                }

                function dateFilter() {
                    $scope.chips.date = [];

                    if (!$scope.businessFilter.useDefaultPeriod) {

                        var fromDate = moment($scope.businessFilter.fromDate).format('YYYY-MM');
                        var toDate = moment($scope.businessFilter.toDate).format('YYYY-MM');

                        $scope.chips.date.push({
                            text: fromDate + ' - ' + toDate
                        });
                    }
                }

                function enhetsFilter() {
                    var filter = $scope.businessFilter.geographyBusinessIdsSaved;
                    filter = _.uniq(filter);

                    var enheter = [];

                    _.each(filter, function(enhet) {
                        var text = _.find($scope.businessFilter.businesses, {id: enhet});
                        enheter.push({
                            text: text ? text.name : enhet
                        });
                    });

                    $scope.chips.enheter = _.sortBy(enheter, function(n) {
                        return n.id;
                    });
                }

                function diagnosFilter() {
                    var filter = $scope.businessFilter.diagnoserSaved;

                    filter = StaticFilterData.getDiagnosFilterInformationText(filter, true);

                    var diagnoser = [];
                    _.each(filter, function(diagnos) {
                        diagnoser.push({
                            text: diagnos.text
                        });
                    });

                    $scope.chips.diagnos = _.sortBy(diagnoser, function(n) {
                        return n.id;
                    });
                }

                function sjukskrivningsLangdsFilter() {
                    var filter = $scope.businessFilter.sjukskrivningslangdSaved;
                    filter = _.uniq(filter);

                    var sjukskrivningslangder = [];
                    _.each(filter, function(sjukskrivningslangd) {
                        sjukskrivningslangder.push({
                            text: StaticFilterData.get().sjukskrivningLengthsObject[sjukskrivningslangd]
                        });
                    });

                    $scope.chips.sjukskrivningslangd = _.sortBy(sjukskrivningslangder, function(n) {
                        return n.id;
                    });
                }

                function aldersGruppsFilter() {
                    var filter = $scope.businessFilter.aldersgruppSaved;
                    filter = _.uniq(filter);

                    var aldersgrupper = [];
                    _.each(filter, function(aldersGrupp) {
                        aldersgrupper.push({
                            text: StaticFilterData.get().ageGroups[aldersGrupp]
                        });
                    });

                    $scope.chips.aldersgrupp = _.sortBy(aldersgrupper, function(n) {
                        return n.id;
                    });
                }

                function intygstyperFilter() {
                    var filter = $scope.businessFilter.intygstyperSaved;
                    filter = _.uniq(filter);

                    var intygstyper = [];
                    _.each(filter, function(intygstyp) {
                        intygstyper.push({
                            text: StaticFilterData.get().intygTypesObject[intygstyp]
                        });
                    });

                    $scope.chips.intygstyper = _.sortBy(intygstyper, function(n) {
                        return n.id;
                    });
                }
            }
        };
    });
