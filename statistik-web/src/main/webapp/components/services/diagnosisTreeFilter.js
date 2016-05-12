angular.module('StatisticsApp')
    .factory('diagnosisTreeFilter', ['statisticsData', '_', 'treeMultiSelectorUtil', '$window',
        function (statisticsData, _, treeMultiSelectUtil, $window) {
            'use strict';

            var diagnosisTreeFilter = {};

            diagnosisTreeFilter.diagnosisOptionsTree = {subs: []};

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
                if (_.any(listOfIdsToSelect, function(val) { return item[attribute] === val; })) {
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
                treeMultiSelectUtil.updateSelectionState(diagnosisTreeFilter.diagnosisOptionsTree);
            };

            diagnosisTreeFilter.setPreselectedFilter = function(filterData) {
                diagnosisTreeFilter.selectDiagnoses(filterData.diagnoser);
            };

            //This could be a common utility method
            diagnosisTreeFilter.setupDiagnosisTreeForSelectionModal = function(diagnoses) {
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

            var firstTimeInitiationOfDiagnosisTree = function firstTimeInitiation(routeParams) {
                //Get icd10 structure and populate the diagnosisOptionsTree
                statisticsData.getIcd10Structure(function (diagnosisTree) {
                    diagnosisTreeFilter.setupDiagnosisTreeForSelectionModal(diagnosisTree);
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
            diagnosisTreeFilter.setup = function(routeParams) {
                if (!hasDiagnosisOptionsTreeAnySubs()) {
                    firstTimeInitiationOfDiagnosisTree(routeParams);
                } else if(hasDiagnosisOptionsTreeAnySubs() && !diagnosHashExists(routeParams)) {
                    diagnosisTreeFilter.resetSelections();
                } else if(hasDiagnosisOptionsTreeAnySubs() && diagnosHashExists(routeParams)) {
                    diagnosisTreeFilter.resetSelections();
                    populateTreeMultiSelectWithPrefilteredData(routeParams);
                }
            };

            return diagnosisTreeFilter;
        }]);