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
    .factory('diagnosisTreeFilter',
        /** @ngInject */
        function (statisticsData, _, treeMultiSelectorUtil, $window) {
            'use strict';

            var diagnosisTreeFilter = {};

            diagnosisTreeFilter.diagnosisOptionsTree = {subs: []};
            diagnosisTreeFilter.showCodeLevel = false;

            diagnosisTreeFilter.selectAll = function (item) {
                item.allSelected = true;
                item.someSelected = false;
                if (item.subs) {
                    _.each(item.subs, function (sub) {
                        diagnosisTreeFilter.selectAll(sub);
                    });
                }
            };

            diagnosisTreeFilter.deselectAll = function (item) {
                item.allSelected = false;
                item.someSelected = false;
                if (item.subs) {
                    _.each(item.subs, function (sub) {
                        diagnosisTreeFilter.deselectAll(sub);
                    });
                }
            };

            diagnosisTreeFilter.selectByAttribute = function (item, listOfIdsToSelect, attribute) {
                if (_.some(listOfIdsToSelect, function(val) { return item[attribute] === val; })) {
                    diagnosisTreeFilter.selectAll(item);
                } else {
                    item.allSelected = false;
                    item.someSelected = false;
                    if (item.subs) {
                        _.each(item.subs, function (sub) {
                            diagnosisTreeFilter.selectByAttribute(sub, listOfIdsToSelect, attribute);
                        });
                    }
                }
            };

           diagnosisTreeFilter.selectDiagnoses = function selectDiagnoses(diagnoses) {
                diagnosisTreeFilter.selectByAttribute(diagnosisTreeFilter.diagnosisOptionsTree, diagnoses, 'numericalId');
                treeMultiSelectorUtil.updateSelectionState(diagnosisTreeFilter.diagnosisOptionsTree);
            };

            diagnosisTreeFilter.setPreselectedFilter = function(filterData) {
                diagnosisTreeFilter.selectDiagnoses(filterData.diagnoser);
            };

            //This could be a common utility method
            diagnosisTreeFilter.setupDiagnosisTreeForSelectionModal = function(diagnoses, showCodeLevel) {
                _.each(diagnoses, function (kapitel) {
                    kapitel.typ = 'kapitel';
                    kapitel.subs = kapitel.subItems;
                    kapitel.name = kapitel.id + ' ' + kapitel.name;
                    _.each(kapitel.subItems, function (avsnitt) {
                        avsnitt.typ = 'avsnitt';
                        avsnitt.subs = avsnitt.subItems;
                        avsnitt.name = avsnitt.id + ' ' + avsnitt.name;
                        _.each(avsnitt.subItems, function (kategori) {
                            kategori.name = kategori.id + ' ' + kategori.name;

                            if (showCodeLevel) {
                                kategori.typ = 'kategori';
                                kategori.subs = kategori.subItems;
                                _.each(kategori.subItems, function(kod) {
                                    kod.name = kod.id + ' ' + kod.name;
                                });
                            }
                        });
                    });
                });
            };

            //This should be a utility method
            diagnosisTreeFilter.getSelectedLeaves = function(node) {
                if (node.subs && node.subs.length !== 0) {
                    return _.reduce(node.subs, function (memo, item) {
                        return memo.concat(diagnosisTreeFilter.getSelectedLeaves(item));
                    }, []);
                } else {
                    return node.allSelected ? [node] : [];
                }
            };

            diagnosisTreeFilter.getSelectedDiagnosis = function() {
                if (!diagnosisTreeFilter.diagnosisOptionsTree) {
                    return null;
                }
                return _.map(diagnosisTreeFilter.getSelectedLeaves(diagnosisTreeFilter.diagnosisOptionsTree), function(it){
                    return it.numericalId;
                });
            };

            diagnosisTreeFilter.resetSelections = function() {
                diagnosisTreeFilter.deselectAll(diagnosisTreeFilter.diagnosisOptionsTree);
            };

            var diagnosHashExists = function diagnosHashExists(routeParams) {
                return routeParams.diagnosHash !== '-';
            };

            var populateTreeMultiSelectWithPrefilteredData = function populateTreeMultiSelectWithPrefilteredData(routeParams) {
                statisticsData.getFilterData(routeParams.diagnosHash, function(filterData) {
                    diagnosisTreeFilter.setPreselectedFilter(filterData);
                }, function(){ throw new Error('Could not parse filter'); });
            };

            var hasDiagnosisOptionsTreeAnySubs = function hasDiagnosisOptionsTreeAnySubs() {
                return diagnosisTreeFilter.diagnosisOptionsTree.subs.length > 0;
            };

            var firstTimeInitiationOfDiagnosisTree = function firstTimeInitiation(routeParams, showCodeLevel) {
                //Get icd10 structure and populate the diagnosisOptionsTree
                statisticsData.getIcd10Structure(function (diagnosisTree) {
                    diagnosisTreeFilter.setupDiagnosisTreeForSelectionModal(diagnosisTree, showCodeLevel);
                    diagnosisTreeFilter.diagnosisOptionsTree = {subs: diagnosisTree};

                    //If we do have a filter hash already then we very much want to apply it.
                    if(diagnosHashExists(routeParams)) {
                        populateTreeMultiSelectWithPrefilteredData(routeParams);
                    }
                }, function () {
                    $window.alert('Failed to fetch ICD10 structure tree from server');
                });
            };

            /*
             *   This initiates or resets the treemultiselect with diagnoses
             *    every time it is needed.
             */
            diagnosisTreeFilter.setup = function(routeParams, showCodeLevel) {
                if (!hasDiagnosisOptionsTreeAnySubs() || diagnosisTreeFilter.showCodeLevel !== showCodeLevel) {
                    firstTimeInitiationOfDiagnosisTree(routeParams, showCodeLevel);
                } else if(hasDiagnosisOptionsTreeAnySubs() && !diagnosHashExists(routeParams)) {
                    diagnosisTreeFilter.resetSelections();
                } else if(hasDiagnosisOptionsTreeAnySubs() && diagnosHashExists(routeParams)) {
                    diagnosisTreeFilter.resetSelections();
                    populateTreeMultiSelectWithPrefilteredData(routeParams);
                }

                diagnosisTreeFilter.showCodeLevel = showCodeLevel;
            };

            return diagnosisTreeFilter;
        }
    );
