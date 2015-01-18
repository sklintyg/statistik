'use strict';

angular.module('StatisticsApp').factory('businessFilter', ['statisticsData', '_',
    function (statisticsData, _) {
        var businessFilter = {};
        businessFilter.reset = function () {
            businessFilter.dataInitialized = false;
            businessFilter.permanentFilter = true;

            businessFilter.businesses = [];
            businessFilter.selectedBusinesses = [];

            businessFilter.geography = {subs: []};
            businessFilter.geographyBusinessIds = [];

            businessFilter.verksamhetsTyper = [];
            businessFilter.verksamhetsTypIds = [];

            businessFilter.icd10 = {subs: []};
            businessFilter.selectedDiagnoses;
        };
        businessFilter.reset();

        businessFilter.getSelectedBusinesses = function (samePage) {
            if (!businessFilter.dataInitialized) {
                return null;
            }
            if (samePage || businessFilter.permanentFilter) {
                return businessFilter.selectedBusinesses;
            }
            return null;
        };

        businessFilter.getSelectedDiagnoses = function (samePage) {
            if (!businessFilter.dataInitialized) {
                return null;
            }
            if (samePage || businessFilter.permanentFilter) {
                return businessFilter.selectedDiagnoses;
            }
            return null;
        };

        businessFilter.resetSelections = function (force) {
            if (!businessFilter.permanentFilter || force) {
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
            }
        };

        var isSet = function (value) {
            return typeof value !== "undefined" && value != null;
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
                        return -1
                    }
                    if (second[propertyName].length <= i) {
                        return 1
                    }
                    var posFirst = swedishAlphabet.indexOf(first[propertyName][i]);
                    var posSecond = swedishAlphabet.indexOf(second[propertyName][i]);
                    if (posFirst != posSecond) {
                        return posFirst - posSecond;
                    }
                }
            });
        }

        businessFilter.setupDiagnosisTreeForSelectionModal = function(diagnoses) {
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

        businessFilter.loggedIn = function (businesses) {
            if (!businessFilter.dataInitialized) {
                statisticsData.getIcd10Structure(businessFilter.setIcd10Structure,
                    function () { alert("Failed to fetch ICD10 structure tree from server") });
                businessFilter.businesses = sortSwedish(businesses, "name", "Okän");
                if (businessFilter.numberOfBusinesses() === "large") {
                    businessFilter.populateGeography(businesses);
                }
                businessFilter.populateVerksamhetsTyper(businesses);
                businessFilter.resetSelections(true);
                businessFilter.dataInitialized = true;
            }
        };

        businessFilter.loggedOut = function () {
            businessFilter.reset();
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
                var county = _.findWhere(businessFilter.geography.subs, { id: business.lansId });
                if (!county) {
                    county = { id: business.lansId, name: business.lansName, subs: [] };
                    businessFilter.geography.subs.push(county);
                }

                var munip = _.findWhere(county.subs, { id: business.kommunId });
                if (!munip) {
                    munip = { id: business.kommunId, name: business.kommunName, subs: [] };
                    county.subs.push(munip);
                }

                munip.subs.push(business);
            });
            businessFilter.geography.subs = sortSwedish(businessFilter.geography.subs, "name", "Okän");
            _.each(businessFilter.geography.subs, function(county) {
                county.subs = sortSwedish(county.subs, "name", "Okän")
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
                            name: verksamhetsTyp.name.concat(" (1 enhet)"),
                            units: [business]
                        };
                    } else {
                        var newUnitList = previousType.units;
                        newUnitList.push(business);
                        verksamhetsTypSet[verksamhetsTyp.id] = {
                            id: verksamhetsTyp.id,
                            name: verksamhetsTyp.name.concat(" (", newUnitList.length.toString(), " enheter)"),
                            units: newUnitList
                        };
                    }
                });
            });
            businessFilter.verksamhetsTyper = sortSwedish(_.values(verksamhetsTypSet), "name");
        };

        businessFilter.selectAll = function (item, selectHidden) {
            if (!item.hide || selectHidden) {
                item.allSelected = true;
                item.someSelected = false;
                if (item.subs) {
                    _.each(item.subs, function (sub) {
                        businessFilter.selectAll(sub, selectHidden);
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
            if (node.subs && node.subs.length != 0) {
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
                        acc.kapitel.push(node.numericalId);
                    } else if (node.typ === 'avsnitt') {
                        acc.avsnitt.push(node.numericalId);
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
                    acc.kategorier.push(node.numericalId);
                }
            }
        };

        businessFilter.updateGeography = function () {
            businessFilter.geographyBusinessIds = businessFilter.collectGeographyIds(businessFilter.geography);
            businessFilter.filterChanged();
        };

        businessFilter.updateDiagnoses = function () {
            businessFilter.selectedDiagnoses = {
                kapitel: [],
                avsnitt: [],
                kategorier: []
            };
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
