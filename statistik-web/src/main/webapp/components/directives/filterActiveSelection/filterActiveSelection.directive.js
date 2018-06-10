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
    .directive('filterActiveSelection',
    /** @ngInject */
    function(StaticData, _, $uibModal, moment) {
        'use strict';
        return {
            scope: {
                businessFilter: '='
            },
            restrict: 'E',
            templateUrl: '/components/directives/filterActiveSelection/filterActiveSelection.html',
            link: function($scope) {

                $scope.selections = {
                    date: [],
                    enheter: [],
                    diagnos: [],
                    sjukskrivningslangd: [],
                    aldersgrupp: [],
                    intygstyper: []
                };

                $scope.showAll = function() {
                    createSelections();

                    $uibModal.open({
                        animation: true,
                        templateUrl: '/components/directives/filterActiveSelection/modal/modal.html',
                        controller: 'FilterActiveSelectionModalCtrl',
                        size: 'lg',
                        resolve: {
                            selections: function () {
                                return $scope.selections;
                            }
                        }
                    });
                };

                function createSelections() {
                    dateFilter();
                    enhetsFilter();
                    diagnosFilter();
                    aldersGruppsFilter();
                    sjukskrivningsLangdsFilter();
                    intygstyperFilter();
                }

                function dateFilter() {
                    $scope.selections.date = [];

                    if (!$scope.businessFilter.useDefaultPeriod) {

                        var fromDate = moment($scope.businessFilter.fromDateSaved).format('YYYY-MM');
                        var toDate = moment($scope.businessFilter.toDateSaved).format('YYYY-MM');

                        $scope.selections.date.push({
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
                            id: enhet,
                            text: text ? text.name : enhet
                        });
                    });

                    $scope.selections.enheter = _.sortBy(enheter, 'id');
                }

                function diagnosFilter() {
                    var filter = $scope.businessFilter.diagnoserSaved;

                    filter = StaticData.getDiagnosFilterInformationText(filter, true);

                    var diagnoser = [];
                    _.each(filter, function(diagnos) {
                        diagnoser.push({
                            id: diagnos.id,
                            text: diagnos.text
                        });
                    });

                    $scope.selections.diagnos = _.sortBy(diagnoser, 'id');
                }

                function sjukskrivningsLangdsFilter() {
                    var filter = $scope.businessFilter.sjukskrivningslangdSaved;
                    filter = _.uniq(filter);

                    var sjukskrivningslangder = [];
                    _.each(filter, function(sjukskrivningslangd) {
                        sjukskrivningslangder.push({
                            id: sjukskrivningslangd,
                            text: StaticData.get().sjukskrivningLengthsObject[sjukskrivningslangd]
                        });
                    });

                    $scope.selections.sjukskrivningslangd = _.sortBy(sjukskrivningslangder, 'id');
                }

                function aldersGruppsFilter() {
                    var filter = $scope.businessFilter.aldersgruppSaved;
                    filter = _.uniq(filter);

                    var aldersgrupper = [];
                    _.each(filter, function(aldersGrupp) {
                        aldersgrupper.push({
                            id: aldersGrupp,
                            text: StaticData.get().ageGroups[aldersGrupp]
                        });
                    });

                    $scope.selections.aldersgrupp = _.sortBy(aldersgrupper, 'id');
                }

                function intygstyperFilter() {
                    var filter = $scope.businessFilter.intygstyperSaved;
                    filter = _.uniq(filter);

                    var intygstyper = [];
                    _.each(filter, function(intygstyp) {
                        intygstyper.push({
                            id: intygstyp,
                            text: StaticData.get().intygTypesObject[intygstyp]
                        });
                    });

                    $scope.selections.intygstyper = _.sortBy(intygstyper, 'id');
                }
            }
        };
    });
