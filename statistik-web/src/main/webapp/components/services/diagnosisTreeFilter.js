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
    .factory('diagnosisTreeFilter',
        /** @ngInject */
        function (statisticsData, _, treeMultiSelectorUtil, StaticFilterData, StaticFilterDataService) {
            'use strict';

            var diagnosisTreeFilter = {};
            var preselectedFilter = {diagnoser: []};

            diagnosisTreeFilter.diagnosisOptionsTree = {subs: []};
            diagnosisTreeFilter.level = 3;

            diagnosisTreeFilter.levels = {
                chapter: 1,
                section: 2,
                category: 3,
                code: 4
            };

            diagnosisTreeFilter.selectAll = function(item) {
                item.allSelected = true;
                item.someSelected = false;
                if (item.subs) {
                    _.each(item.subs, function (sub) {
                        diagnosisTreeFilter.selectAll(sub);
                    });
                }
            };

            diagnosisTreeFilter.deselectAll = function(item) {
                item.allSelected = false;
                item.someSelected = false;
                if (item.subs) {
                    _.each(item.subs, function (sub) {
                        diagnosisTreeFilter.deselectAll(sub);
                    });
                }
            };

            diagnosisTreeFilter.selectByAttribute = function(item, listOfIdsToSelect, attribute) {
                if (listOfIdsToSelect.indexOf(item[attribute]) > -1) {
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

            diagnosisTreeFilter.selectDiagnoses = function(diagnoses) {
                diagnosisTreeFilter.selectByAttribute(diagnosisTreeFilter.diagnosisOptionsTree, diagnoses, 'numericalId');
                treeMultiSelectorUtil.updateSelectionState(diagnosisTreeFilter.diagnosisOptionsTree);
            };

            diagnosisTreeFilter.setPreselectedFilter = function() {
                diagnosisTreeFilter.selectDiagnoses(preselectedFilter.diagnoser);
            };

            //This should be a utility method
            diagnosisTreeFilter.getSelectedLeaves = function(node, level) {
                if (!node.subs || node.subs.length === 0 || level > diagnosisTreeFilter.level) {
                    return node.allSelected ? [node] : [];
                } else {
                    return _.reduce(node.subs, function (memo, item) {
                        return memo.concat(diagnosisTreeFilter.getSelectedLeaves(item, level + 1));
                    }, []);
                }
            };

            diagnosisTreeFilter.getSelectedDiagnosis = function() {
                if (!diagnosisTreeFilter.diagnosisOptionsTree) {
                    return null;
                }
                return _.map(diagnosisTreeFilter.getSelectedLeaves(diagnosisTreeFilter.diagnosisOptionsTree, 1), function(it){
                    return it.numericalId;
                });
            };

            diagnosisTreeFilter.resetSelections = function() {
                diagnosisTreeFilter.deselectAll(diagnosisTreeFilter.diagnosisOptionsTree);
                preselectedFilter = {diagnoser: []};
            };

            var diagnosHashExists = function(diagnosHash) {
                return diagnosHash !== '-';
            };

            var populateTreeMultiSelectWithPrefilteredData = function(diagnosHash) {
                statisticsData.getFilterData(diagnosHash, function(filterData) {
                    preselectedFilter = filterData;
                    diagnosisTreeFilter.setPreselectedFilter();
                }, function() {
                    throw new Error('Could not parse filter');
                });
            };

            var hasDiagnosisOptionsTreeAnySubs = function() {
                return diagnosisTreeFilter.diagnosisOptionsTree.subs.length > 0;
            };

            var firstTimeInitiationOfDiagnosisTree = function(diagnosHash) {
                StaticFilterDataService.get().then(function() {
                    var diagnosisTree = StaticFilterData.get().icd10Structure;
                    diagnosisTreeFilter.diagnosisOptionsTree = {subs: diagnosisTree};

                    //If we do have a filter hash already then we very much want to apply it.
                    if(diagnosHashExists(diagnosHash)) {
                        populateTreeMultiSelectWithPrefilteredData(diagnosHash);
                    }
                });
            };

            /*
             *   This initiates or resets the treemultiselect with diagnoses
             *    every time it is needed.
             */
            diagnosisTreeFilter.setup = function(diagnosHash, level) {
                diagnosisTreeFilter.level = level;

                if (!hasDiagnosisOptionsTreeAnySubs()) {
                    firstTimeInitiationOfDiagnosisTree(diagnosHash);
                } else if(!diagnosHashExists(diagnosHash)) {
                    diagnosisTreeFilter.resetSelections();
                } else {
                    diagnosisTreeFilter.resetSelections();
                    populateTreeMultiSelectWithPrefilteredData(diagnosHash);
                }
            };

            diagnosisTreeFilter.urlLevelToCorrectLevel = function(urlLevel) {
                var level;

                switch (urlLevel) {
                case 1:
                    level = diagnosisTreeFilter.levels.code;
                    break;
                case 2:
                    level = diagnosisTreeFilter.levels.section;
                    break;
                case 3:
                    level = diagnosisTreeFilter.levels.chapter;
                    break;
                default:
                    level = diagnosisTreeFilter.levels.category;
                }

                return level;
            };

            diagnosisTreeFilter.levelToUrlLevel = function(level) {
                var urlLevel;

                switch (level) {
                case diagnosisTreeFilter.levels.code:
                    urlLevel = 1;
                    break;
                case diagnosisTreeFilter.levels.section:
                    urlLevel = 2;
                    break;
                case diagnosisTreeFilter.levels.chapter:
                    urlLevel = 3;
                    break;
                default:
                    urlLevel = 0;
                }

                return urlLevel;
            };

            return diagnosisTreeFilter;
        }
    );
