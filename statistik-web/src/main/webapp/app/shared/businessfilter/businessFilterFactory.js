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

angular.module('StatisticsApp.filterFactory.factory', []);

angular.module('StatisticsApp.filterFactory.factory')
    .factory('businessFilterFactory',
        /** @ngInject */
        function (statisticsData, _, treeMultiSelectorUtil, moment, AppModel, StaticFilterDataService, StaticFilterData, ControllerCommons, $q) {
            'use strict';

            return createBusinessFilter(statisticsData, _, treeMultiSelectorUtil, moment, AppModel, StaticFilterDataService, StaticFilterData, ControllerCommons, $q);
        }
    );

angular.module('StatisticsApp.filterFactory.factory')
    .factory('landstingFilterFactory',
        /** @ngInject */
        function (statisticsData, _, treeMultiSelectorUtil, moment, AppModel, StaticFilterDataService, StaticFilterData, ControllerCommons, $q) {
            'use strict';

            return createBusinessFilter(statisticsData, _, treeMultiSelectorUtil, moment, AppModel, StaticFilterDataService, StaticFilterData, ControllerCommons, $q);
        }
    );


function createBusinessFilter(statisticsData, _, treeMultiSelectorUtil, moment, AppModel, StaticFilterDataService, StaticFilterData, ControllerCommons, $q) {
    'use strict';

    var loadingFilter = false;

    //The businessFilter object holds all methods and properties that are part of the public API
    var businessFilter = {};

    //Immediately called function that initializes the businessFilter the first time around
    (function(){
        businessFilter.dataInitialized = false;

        businessFilter.businesses = [];

        businessFilter.geographyBusinessIdsSaved = [];
        businessFilter.geography = {subs: []};
        businessFilter.geographyBusinessIds = [];

        businessFilter.verksamhetsTyper = [];
        businessFilter.selectedVerksamhetTypIds = [];

        businessFilter.sjukskrivningslangd = [];
        businessFilter.sjukskrivningslangdSaved = [];
        businessFilter.selectedSjukskrivningslangdIds = [];

        businessFilter.aldersgrupp = [];
        businessFilter.aldersgruppSaved = [];
        businessFilter.selectedAldersgruppIds = [];

        businessFilter.diagnoserSaved = [];
        businessFilter.selectedDiagnoses = [];
        businessFilter.icd10 = {subs: []};

        //Init the datepicker components
        businessFilter.fromDate = null;
        businessFilter.toDate = null;

        businessFilter.useDefaultPeriod = true;

        //Flag that defines if user has selected any filter values or not.
        businessFilter.hasUserSelection = false;
    }());

    businessFilter.updateHasUserSelection = function() {
        //If any filter parts have values set by user - filter is considered active.
        businessFilter.hasUserSelection = !businessFilter.useDefaultPeriod || businessFilter.aldersgruppSaved.length > 0 ||
            businessFilter.sjukskrivningslangdSaved.length > 0 || businessFilter.diagnoserSaved.length > 0 ||
            businessFilter.geographyBusinessIdsSaved.length > 0;
    };

    businessFilter.selectDiagnoses = function (diagnoses) {
        businessFilter.selectByAttribute(businessFilter.icd10, diagnoses, 'numericalId');
        businessFilter.selectedDiagnoses = diagnoses;
        treeMultiSelectorUtil.updateSelectionState(businessFilter.icd10);
    };

    businessFilter.selectGeographyBusiness = function (businessIds) {
        businessFilter.selectByAttribute(businessFilter.geography, businessIds, 'id');
        businessFilter.geographyBusinessIds = businessIds;
        treeMultiSelectorUtil.updateSelectionState(businessFilter.geography);
    };

    businessFilter.updateSelectionVerksamhetsTyper = function(verksamhetsTyper) {
        angular.forEach(verksamhetsTyper, function(verksamhetsTyp) {
            var selected = true;

            angular.forEach(verksamhetsTyp.units, function(businesse) {
                selected = selected && businesse.allSelected;
            });

            if (!selected) {
                verksamhetsTyp.checked = selected;
            }
        });
    };

    businessFilter.deselectVerksamhetsTyper = function(verksamhetsTyper) {
        angular.forEach(verksamhetsTyper, function(verksamhetsTyp) {
            verksamhetsTyp.checked = false;
        });
    };

    businessFilter.resetSelections = function () {
        businessFilter.geographyBusinessIds.length = 0;
        businessFilter.geographyBusinessIdsSaved.length = 0;
        businessFilter.selectedVerksamhetTypIds.length = 0;
        businessFilter.selectedSjukskrivningslangdIds.length = 0;
        businessFilter.sjukskrivningslangdSaved.length = 0;
        businessFilter.selectedAldersgruppIds.length = 0;
        businessFilter.aldersgruppSaved.length = 0;
        businessFilter.selectedDiagnoses.length = 0;
        businessFilter.diagnoserSaved.length = 0;
        businessFilter.deselectAll(businessFilter.geography);
        businessFilter.deselectAll(businessFilter.icd10);
        businessFilter.updateSelectionVerksamhetsTyper(businessFilter.verksamhetsTyper);

        //Reset datepickers
        businessFilter.toDate = null;
        businessFilter.fromDate = null;

        businessFilter.useDefaultPeriod = true;
        businessFilter.updateHasUserSelection();
    };

    var isSet = function (value) {
        return typeof value !== 'undefined' && value !== null;
    };

    function sortSwedish(arrayToSort, propertyName, alwaysLast) {
        var swedishAlphabet = '0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZzÅåÄäÖö';
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

    businessFilter.setIcd10Structure = function (diagnoses) {
        businessFilter.icd10.subs = diagnoses;
        businessFilter.updateDiagnoses();
    };

    function setPreselectedFilter(filterData) {
        if (businessFilter.icd10.subs.length > 0) {
            businessFilter.diagnoserSaved = _.cloneDeep(filterData.diagnoser);
            businessFilter.selectDiagnoses(filterData.diagnoser);
        }
        businessFilter.selectedVerksamhetTypIds = _.uniqWith(_.map(filterData.verksamhetstyper, function(verksamhetstyp) {
            return _.find(businessFilter.verksamhetsTyper, function(verksamhet) { return _.includes(verksamhet.ids, verksamhetstyp); }).id;
        }));
        businessFilter.selectedSjukskrivningslangdIds = filterData.sjukskrivningslangd;
        businessFilter.sjukskrivningslangdSaved = _.cloneDeep(filterData.sjukskrivningslangd);
        businessFilter.selectedAldersgruppIds = filterData.aldersgrupp;
        businessFilter.aldersgruppSaved = _.cloneDeep(filterData.aldersgrupp);
        businessFilter.geographyBusinessIdsSaved = _.cloneDeep(filterData.enheter);
        businessFilter.selectGeographyBusiness(filterData.enheter);
        businessFilter.toDate = filterData.toDate ? moment(filterData.toDate).utc().toDate() : null;
        businessFilter.fromDate = filterData.fromDate ? moment(filterData.fromDate).utc().toDate() : null;
        businessFilter.useDefaultPeriod = filterData.useDefaultPeriod;
        businessFilter.updateHasUserSelection();
    }

    businessFilter.populateIcd10Structure = function() {
        businessFilter.setIcd10Structure(StaticFilterData.get().icd10Structure);
    };

    businessFilter.selectPreselectedFilter = function(preSelectedFilter) {

        if (loadingFilter) {
            return;
        }

        var filterData;

        var loadFilter = loadFilterData(preSelectedFilter).then(function(data) {
            filterData = data;
        });

        $q.all([loadFilter]).then(function() {
            if (filterData) {
                setPreselectedFilter(filterData);
            } else {
                businessFilter.resetSelections();
            }
        }).finally(function() {
            loadingFilter = false;
        });
    };

    function loadFilterData(preSelectedFilter) {
        var deferred = $q.defer();

        if (preSelectedFilter) {

            statisticsData.getFilterData(preSelectedFilter, function (filterData) {
                deferred.resolve(filterData);
            }, function () {
                throw new Error('Could not parse filter');
            });
        } else {
            deferred.resolve(null);
        }

        return deferred.promise;
    }

    function populateStaticFilterData() {
        if (businessFilter.icd10.subs.length > 0) {
            var deferred = $q.defer();
            deferred.resolve(true);
            return deferred.promise;
        } else {
            return StaticFilterDataService.get();
        }
    }

    businessFilter.setup = function (businesses, preSelectedFilter) {
        populateStaticFilterData().then(function() {
            businessFilter.businesses = businesses;
            if (businessFilter.showBusinessFilter()) {
                businessFilter.populateGeography(businesses);
                businessFilter.populateVerksamhetsTyper(businesses);
                businessFilter.businesses = sortSwedish(businesses, 'name', 'Okän');
            }
            businessFilter.populateSjukskrivningsLangd();
            businessFilter.populateAldersgrupp();
            businessFilter.populateIcd10Structure();
            businessFilter.dataInitialized = true;
            businessFilter.selectPreselectedFilter(preSelectedFilter);
        });
    };

    businessFilter.showBusinessFilter = function () {
        return businessFilter.businesses.length > 1;
    };

    businessFilter.populateGeography = function (businesses) {
        businessFilter.geography = {subs: []};
        _.each(businesses, function (business) {
            var county = _.find(businessFilter.geography.subs, {name: business.lansName});
            if (!county) {
                county = {
                    id: business.lansId,
                    numericalId: business.lansId + 'county',
                    name: business.lansName,
                    visibleName: business.lansName,
                    subs: []};
                businessFilter.geography.subs.push(county);
            }

            var munip = _.find(county.subs, {name: business.kommunName});
            if (!munip) {
                munip = {
                    id: business.kommunId,
                    numericalId: business.kommunId + 'munip',
                    name: business.kommunName,
                    visibleName: business.kommunName,
                    subs: []};
                county.subs.push(munip);
            }

            business.numericalId = business.id;
            business.visibleName = business.name;
            munip.subs.push(business);
        });
        businessFilter.geography.subs = sortSwedish(businessFilter.geography.subs, 'name', 'Okän');
        _.each(businessFilter.geography.subs, function (county) {
            county.subs = sortSwedish(county.subs, 'name', 'Okän');
        });
    };

    businessFilter.populateSjukskrivningsLangd = function() {
        businessFilter.sjukskrivningslangd = StaticFilterData.get().sjukskrivningLengths;
    };

    businessFilter.populateAldersgrupp = function() {
        businessFilter.aldersgrupp = StaticFilterData.get().aldersgrupps;
    };

    businessFilter.populateVerksamhetsTyper = function (businesses) {
        var verksamhetsTypSet = {};
        _.each(businesses, function (business) {
            _.each(business.verksamhetsTyper, function (verksamhetsTyp) {
                var previousType = verksamhetsTypSet[verksamhetsTyp.name];
                if (!previousType) {
                    verksamhetsTypSet[verksamhetsTyp.name] = {
                        ids: [verksamhetsTyp.id],
                        id: verksamhetsTyp.name,
                        name: verksamhetsTyp.name,
                        units: [business]
                    };
                } else {
                    var newUnitList = previousType.units;
                    newUnitList.push(business);
                    var newIdList = previousType.ids;
                    newIdList.push(verksamhetsTyp.id);
                    verksamhetsTypSet[verksamhetsTyp.name] = {
                        ids: newIdList,
                        id: verksamhetsTyp.name,
                        name: verksamhetsTyp.name,
                        units: newUnitList
                    };
                }
            });
        });
        businessFilter.verksamhetsTyper = sortSwedish(_.values(verksamhetsTypSet), 'name', 'Okän');
    };

    businessFilter.selectAll = function (item) {
        businessFilter.selectAllWithValue(item, true);
    };

    businessFilter.deselectAll = function (item) {
        businessFilter.selectAllWithValue(item, false);
    };

    businessFilter.selectAllWithValue = function (item, selected) {
        item.allSelected = selected;
        item.someSelected = false;
        if (item.subs) {
            _.each(item.subs, function (sub) {
                businessFilter.selectAllWithValue(sub, selected);
            });
        }
    };

    businessFilter.selectByAttribute = function (item, listOfIdsToSelect, attribute) {
        if (_.some(listOfIdsToSelect, function (val) {
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
                } else if (node.typ === 'kategori') {
                    acc.push(node.numericalId);
                } else if (node.typ === 'kod') {
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
    };

    businessFilter.updateDiagnoses = function () {
        businessFilter.selectedDiagnoses = [];
        businessFilter.collectSummary(businessFilter.icd10, businessFilter.selectedDiagnoses);
    };

    businessFilter.collectVerksamhetsIds = function () {
        function getSelectedVerksamhets() {
            if (businessFilter.selectedVerksamhetTypIds.length === 0) {
                return businessFilter.verksamhetsTyper;
            }
            return _.filter(businessFilter.verksamhetsTyper, function(verksamhetstyp) {
                return _.includes(businessFilter.selectedVerksamhetTypIds, verksamhetstyp.id);
            });
        }

        var selectedVerksamhettyps = getSelectedVerksamhets();
        var selectedUnitsFromVerksamhetstyps = _.map(selectedVerksamhettyps, function (verksamhetstyp) {
            return verksamhetstyp.units;
        });
        var selectedUnitsFromVerksamhetstypsFlattened = _.flatten(selectedUnitsFromVerksamhetstyps);
        return _.map(selectedUnitsFromVerksamhetstypsFlattened, 'id');
    };

    businessFilter.setSelectedGeography = function() {
        businessFilter.selectGeographyBusiness(businessFilter.geographyBusinessIds);
    };

    businessFilter.setSelectedDiagnoses = function() {
        businessFilter.selectDiagnoses(businessFilter.selectedDiagnoses);
    };

    return businessFilter;
}
