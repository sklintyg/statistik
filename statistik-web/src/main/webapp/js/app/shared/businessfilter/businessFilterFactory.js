/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
'use strict';

angular.module('StatisticsApp.businessFilter.factory', [])
    .factory('businessFilter', ['statisticsData', '_', 'treeMultiSelectorUtil',
        function (statisticsData, _, treeMultiSelectorUtil) {

            //The businessFilter object holds all methods and properties that are part of the public API
            var businessFilter = {};

            //Immediately called function that initializes the businessFilter the first time around
            (function(){
                businessFilter.dataInitialized = false;

                businessFilter.businesses = [];
                businessFilter.selectedBusinesses = [];

                businessFilter.geography = {subs: []};
                businessFilter.geographyBusinessIds = [];

                businessFilter.verksamhetsTyper = [];
                businessFilter.verksamhetsTypIds = [];

                businessFilter.icd10 = {subs: []};
                businessFilter.selectedDiagnoses;

                //Init the datepicker components
                businessFilter.fromDate = null;
                businessFilter.toDate = null;

                businessFilter.useDefaultPeriod = true;
            }());

            businessFilter.selectDiagnoses = function (diagnoses) {
                businessFilter.selectByAttribute(businessFilter.icd10, diagnoses, "numericalId");
                businessFilter.selectedDiagnoses = diagnoses;
                treeMultiSelectorUtil.updateSelectionState(businessFilter.icd10);
            };

            businessFilter.selectGeographyBusiness = function (businessIds) {
                businessFilter.selectByAttribute(businessFilter.geography, businessIds, "id");
                businessFilter.geographyBusinessIds = businessIds;
                treeMultiSelectorUtil.updateSelectionState(businessFilter.geography);
            };

            businessFilter.resetSelections = function () {
                businessFilter.selectedBusinesses.length = 0;
                businessFilter.geographyBusinessIds.length = 0;
                businessFilter.verksamhetsTypIds.length = 0;
                _.each(businessFilter.businesses, function (business) {
                    businessFilter.selectedBusinesses.push(business.id);
                    businessFilter.geographyBusinessIds.push(business.id);
                });
                _.each(businessFilter.verksamhetsTyper, function (verksamhetsTyp) {
                    businessFilter.verksamhetsTypIds.push(verksamhetsTyp.id);
                });
                businessFilter.selectAll(businessFilter.geography, true);
                businessFilter.selectAll(businessFilter.icd10, true);
                businessFilter.updateDiagnoses();

                //Reset datepickers
                businessFilter.toDate = null;
                businessFilter.fromDate = null;

                businessFilter.useDefaultPeriod = true;
            };

            var isSet = function (value) {
                return typeof value !== "undefined" && value !== null;
            };

            function sortSwedish(arrayToSort, propertyName, alwaysLast) {
                var swedishAlphabet = "0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZzÅåÄäÖö";
                return arrayToSort.sort(function (first, second) {
                    if (first[propertyName] === second[propertyName]) {
                        return 0;
                    }
                    if (isSet(alwaysLast) && first[propertyName].indexOf(alwaysLast) > -1) {
                        return 1;
                    }
                    if (isSet(alwaysLast) && second[propertyName].indexOf(alwaysLast) > -1) {
                        return -1;
                    }
                    for (var i = 0; true; i++) {
                        if (first[propertyName].length <= i) {
                            return -1;
                        }
                        if (second[propertyName].length <= i) {
                            return 1;
                        }
                        var posFirst = swedishAlphabet.indexOf(first[propertyName][i]);
                        var posSecond = swedishAlphabet.indexOf(second[propertyName][i]);
                        if (posFirst !== posSecond) {
                            return posFirst - posSecond;
                        }
                    }
                });
            }

            businessFilter.setupDiagnosisTreeForSelectionModal = function (diagnoses) {
                _.each(diagnoses, function (kapitel) {
                    kapitel.typ = 'kapitel';
                    kapitel.subs = kapitel.subItems;
                    kapitel.name = kapitel.id + " " + kapitel.name;
                    _.each(kapitel.subItems, function (avsnitt) {
                        avsnitt.typ = 'avsnitt';
                        avsnitt.subs = avsnitt.subItems;
                        avsnitt.name = avsnitt.id + " " + avsnitt.name;
                        _.each(avsnitt.subItems, function (kategori) {
                            kategori.name = kategori.id + " " + kategori.name;
                        });
                    });
                });
            };

            businessFilter.setIcd10Structure = function (diagnoses) {
                businessFilter.setupDiagnosisTreeForSelectionModal(diagnoses);
                businessFilter.icd10.subs = diagnoses;
                businessFilter.selectAll(businessFilter.icd10, true);
                businessFilter.updateDiagnoses();
            };

            function setPreselectedFilter(filterData) {
                businessFilter.selectDiagnoses(filterData.diagnoser);
                businessFilter.verksamhetsTypIds = filterData.verksamhetstyper;
                businessFilter.selectGeographyBusiness(filterData.enheter);
                businessFilter.toDate = Date.parse(filterData.toDate);
                businessFilter.fromDate = Date.parse(filterData.fromDate);
                businessFilter.useDefaultPeriod = filterData.useDefaultPeriod;

                businessFilter.filterChanged();
            }

            function updateIcd10StructureOnce(successCallback) {
                if (businessFilter.icd10.subs.length > 0) {
                    successCallback();
                } else {
                    statisticsData.getIcd10Structure(function (result) {
                            businessFilter.setIcd10Structure(result);
                            successCallback();
                        },
                        function () {
                            throw new Error("Failed to fetch ICD10 structure tree from server");
                        });
                }
            }

            businessFilter.setup = function (businesses, preSelectedFilter) {
                updateIcd10StructureOnce(function () {
                    if (!businessFilter.dataInitialized) {
                        businessFilter.businesses = sortSwedish(businesses, "name", "Okän");
                        if (businessFilter.numberOfBusinesses() === "large") {
                            businessFilter.populateGeography(businesses);
                        }
                        businessFilter.populateVerksamhetsTyper(businesses);
                        businessFilter.resetSelections();
                        businessFilter.dataInitialized = true;
                    }
                    if (preSelectedFilter) {
                        statisticsData.getFilterData(preSelectedFilter, function (filterData) {
                            setPreselectedFilter(filterData);
                        }, function () {
                            throw new Error("Could not parse filter");
                        });
                    }
                });
            };

            businessFilter.numberOfBusinesses = function () {
                if (businessFilter.businesses.length <= 1) {
                    return "small";
                }
                if (businessFilter.businesses.length <= 10) {
                    return "medium";
                }
                return "large";
            };

            businessFilter.populateGeography = function (businesses) {
                businessFilter.geography = {subs: []};
                _.each(businesses, function (business) {
                    var county = _.findWhere(businessFilter.geography.subs, {id: business.lansId});
                    if (!county) {
                        county = {id: business.lansId, name: business.lansName, subs: []};
                        businessFilter.geography.subs.push(county);
                    }

                    var munip = _.findWhere(county.subs, {id: business.kommunId});
                    if (!munip) {
                        munip = {id: business.kommunId, name: business.kommunName, subs: []};
                        county.subs.push(munip);
                    }

                    munip.subs.push(business);
                });
                businessFilter.geography.subs = sortSwedish(businessFilter.geography.subs, "name", "Okän");
                _.each(businessFilter.geography.subs, function (county) {
                    county.subs = sortSwedish(county.subs, "name", "Okän");
                });
            };

            businessFilter.populateVerksamhetsTyper = function (businesses) {
                var verksamhetsTypSet = {};
                _.each(businesses, function (business) {
                    _.each(business.verksamhetsTyper, function (verksamhetsTyp) {
                        var previousType = verksamhetsTypSet[verksamhetsTyp.id];
                        if (!previousType) {
                            verksamhetsTypSet[verksamhetsTyp.id] = {
                                id: verksamhetsTyp.id,
                                name: verksamhetsTyp.name,
                                units: [business]
                            };
                        } else {
                            var newUnitList = previousType.units;
                            newUnitList.push(business);
                            verksamhetsTypSet[verksamhetsTyp.id] = {
                                id: verksamhetsTyp.id,
                                name: verksamhetsTyp.name,
                                units: newUnitList
                            };
                        }
                    });
                });
                businessFilter.verksamhetsTyper = sortSwedish(_.values(verksamhetsTypSet), "name");
            };

            businessFilter.selectAll = function (item) {
                item.allSelected = true;
                item.someSelected = false;
                if (item.subs) {
                    _.each(item.subs, function (sub) {
                        businessFilter.selectAll(sub);
                    });
                }
            };

            businessFilter.selectByAttribute = function (item, listOfIdsToSelect, attribute) {
                if (_.any(listOfIdsToSelect, function (val) {
                        return item[attribute] === val;
                    })) {
                    businessFilter.selectAll(item);
                } else {
                    item.allSelected = false;
                    item.someSelected = false;
                    if (item.subs) {
                        _.each(item.subs, function (sub) {
                            businessFilter.selectByAttribute(sub, listOfIdsToSelect, attribute);
                        });
                    }
                }
            };

            businessFilter.collectGeographyIds = function (node) {
                if (node.subs) {
                    if (node.allSelected || node.someSelected) {
                        return _.reduce(node.subs, function (acc, item) {
                            return acc.concat(businessFilter.collectGeographyIds(item));
                        }, []);
                    } else {
                        return [];
                    }
                } else {
                    return node.allSelected ? [node.id] : [];
                }
            };

            businessFilter.getSelectedLeaves = function (node) {
                if (node.subs && node.subs.length !== 0) {
                    return _.reduce(node.subs, function (memo, item) {
                        return memo.concat(businessFilter.getSelectedLeaves(item));
                    }, []);
                } else {
                    return node.allSelected ? [node] : [];
                }
            };

            businessFilter.collectSummary = function (node, acc) {
                if (node.subs) { // Diagnoses, Kapitel or Avsnitt
                    if (node.allSelected) {
                        if (node.typ === 'kapitel') {
                            acc.push(node.numericalId);
                        } else if (node.typ === 'avsnitt') {
                            acc.push(node.numericalId);
                        } else { // root node
                            _.each(node.subs, function (subItem) {
                                businessFilter.collectSummary(subItem, acc);
                            });
                        }
                    } else if (node.someSelected) {
                        _.each(node.subs, function (subItem) {
                            businessFilter.collectSummary(subItem, acc);
                        });
                    }
                } else { // Kategori
                    if (node.allSelected) {
                        acc.push(node.numericalId);
                    }
                }
            };

            businessFilter.updateGeography = function () {
                businessFilter.geographyBusinessIds = businessFilter.collectGeographyIds(businessFilter.geography);
                businessFilter.filterChanged();
            };

            businessFilter.updateDiagnoses = function () {
                businessFilter.selectedDiagnoses = [];
                businessFilter.collectSummary(businessFilter.icd10, businessFilter.selectedDiagnoses);
            };

            businessFilter.collectVerksamhetsIds = function () {
                var matchingBusinesses = _.filter(businessFilter.businesses, function (business) {
                    var typer = business.verksamhetsTyper;
                    return typer === null || typer.length === 0 || _.any(typer, function (verksamhetsTyp) {
                            return _.contains(businessFilter.verksamhetsTypIds, verksamhetsTyp.id);
                        });
                });
                return _.pluck(matchingBusinesses, 'id');
            };

            businessFilter.filterChanged = function () {
                var verksamhetsBusinessIds = businessFilter.collectVerksamhetsIds();
                businessFilter.selectedBusinesses = _.intersection(businessFilter.geographyBusinessIds, verksamhetsBusinessIds);
            };

            return businessFilter;
        }
    ]);
