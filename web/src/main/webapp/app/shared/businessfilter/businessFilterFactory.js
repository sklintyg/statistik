/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

angular.module('StatisticsApp.filterFactory.factory', []);

angular.module('StatisticsApp.filterFactory.factory')
.factory('businessFilterFactory',
    /** @ngInject */
    function(statisticsData, _, treeMultiSelectorUtil, moment, AppModel, StaticDataService, StaticData, ControllerCommons, $q, ObjectHelper,
        ArrayHelper) {
      'use strict';

      return createBusinessFilter(statisticsData, _, treeMultiSelectorUtil, moment, AppModel, StaticDataService, StaticData,
          ControllerCommons, $q, ObjectHelper, ArrayHelper);
    }
);

angular.module('StatisticsApp.filterFactory.factory')
.factory('regionFilterFactory',
    /** @ngInject */
    function(statisticsData, _, treeMultiSelectorUtil, moment, AppModel, StaticDataService, StaticData, ControllerCommons, $q, ObjectHelper,
        ArrayHelper) {
      'use strict';

      return createBusinessFilter(statisticsData, _, treeMultiSelectorUtil, moment, AppModel, StaticDataService, StaticData,
          ControllerCommons, $q, ObjectHelper, ArrayHelper);
    }
);

function createBusinessFilter(statisticsData, _, treeMultiSelectorUtil, moment, AppModel, StaticDataService, StaticData, ControllerCommons,
    $q, ObjectHelper, ArrayHelper) {
  'use strict';

  var loadingFilter = false;

  //The businessFilter object holds all methods and properties that are part of the public API
  var businessFilter = {};

  //Immediately called function that initializes the businessFilter the first time around
  (function() {
    businessFilter.dataInitialized = false;

    businessFilter.businesses = [];

    businessFilter.geographyBusinessIdsSaved = [];
    businessFilter.geography = {subs: []};
    businessFilter.geographyBusinessIds = [];

    businessFilter.verksamhetsTyper = [];

    businessFilter.sjukskrivningslangd = [];
    businessFilter.sjukskrivningslangdSaved = [];
    businessFilter.selectedSjukskrivningslangdIds = [];

    businessFilter.aldersgrupp = [];
    businessFilter.aldersgruppSaved = [];
    businessFilter.selectedAldersgruppIds = [];

    businessFilter.intygstyper = [];
    businessFilter.intygstyperSaved = [];
    businessFilter.selectedIntygstyperIds = [];

    businessFilter.diagnoserSaved = [];
    businessFilter.selectedDiagnoses = [];
    businessFilter.icd10 = {subs: []};

    //Init the datepicker components
    businessFilter.fromDate = null;
    businessFilter.fromDateSaved = null;
    businessFilter.toDate = null;
    businessFilter.toDateSaved = null;

    businessFilter.useDefaultPeriod = true;

    //Flag that defines if user has selected any filter values or not.
    businessFilter.hasUserSelection = false;
  }());

  businessFilter.updateHasUserSelection = function() {
    //If any filter parts have values set by user - filter is considered active.
    businessFilter.hasUserSelection = !businessFilter.useDefaultPeriod || businessFilter.aldersgruppSaved.length > 0 ||
        businessFilter.sjukskrivningslangdSaved.length > 0 || businessFilter.diagnoserSaved.length > 0 ||
        businessFilter.geographyBusinessIdsSaved.length > 0 || businessFilter.intygstyperSaved.length > 0;
  };

  businessFilter.selectDiagnoses = function(diagnoses) {
    businessFilter.selectByAttribute(businessFilter.icd10, diagnoses, 'numericalId');
    businessFilter.selectedDiagnoses = diagnoses;
    treeMultiSelectorUtil.updateSelectionState(businessFilter.icd10);
  };

  businessFilter.selectGeographyBusiness = function(businessIds) {
    businessFilter.selectByAttribute(businessFilter.geography, businessIds, 'id');
    businessFilter.geographyBusinessIds = businessIds;
    treeMultiSelectorUtil.updateSelectionState(businessFilter.geography);
  };

  businessFilter.filterChanged = function() {
    var fromDateChanged = businessFilter.fromDateSaved !== businessFilter.fromDate;
    var toDateChanged = businessFilter.toDateSaved !== businessFilter.toDate;
    var aldersGruppChanged = ArrayHelper.isDifferent(businessFilter.aldersgruppSaved, businessFilter.selectedAldersgruppIds);
    var sjukskrivningslangdChanged = ArrayHelper.isDifferent(businessFilter.sjukskrivningslangdSaved,
        businessFilter.selectedSjukskrivningslangdIds);
    var diagnoserChanged = ArrayHelper.isDifferent(businessFilter.diagnoserSaved, businessFilter.selectedDiagnoses);
    var geographyBusinessChanged = ArrayHelper.isDifferent(businessFilter.geographyBusinessIdsSaved, businessFilter.geographyBusinessIds);
    var intygstyperChanged = ArrayHelper.isDifferent(businessFilter.intygstyperSaved, businessFilter.selectedIntygstyperIds);

    return fromDateChanged || toDateChanged || aldersGruppChanged || sjukskrivningslangdChanged ||
        diagnoserChanged || geographyBusinessChanged || intygstyperChanged;
  };

  businessFilter.resetSelections = function() {
    businessFilter.geographyBusinessIds.length = 0;
    businessFilter.geographyBusinessIdsSaved.length = 0;
    businessFilter.selectedSjukskrivningslangdIds.length = 0;
    businessFilter.sjukskrivningslangdSaved.length = 0;
    businessFilter.selectedIntygstyperIds.length = 0;
    businessFilter.intygstyperSaved.length = 0;
    businessFilter.selectedAldersgruppIds.length = 0;
    businessFilter.aldersgruppSaved.length = 0;
    businessFilter.selectedDiagnoses.length = 0;
    businessFilter.diagnoserSaved.length = 0;
    businessFilter.deselectAll(businessFilter.geography);
    businessFilter.deselectAll(businessFilter.icd10);

    //Reset datepickers
    businessFilter.toDate = null;
    businessFilter.toDateSaved = null;
    businessFilter.fromDate = null;
    businessFilter.fromDateSaved = null;

    businessFilter.useDefaultPeriod = true;
    businessFilter.updateHasUserSelection();
  };

  businessFilter.setIcd10Structure = function(diagnoses) {
    businessFilter.icd10.subs = diagnoses;
    businessFilter.updateDiagnoses();
  };

  function setPreselectedFilter(filterData) {
    if (businessFilter.icd10.subs.length > 0) {
      var diagnoser = filterData.diagnoser ? filterData.diagnoser : [];

      businessFilter.diagnoserSaved = _.cloneDeep(diagnoser);
      businessFilter.selectDiagnoses(diagnoser);
    }

    businessFilter.selectedSjukskrivningslangdIds = filterData.sjukskrivningslangd ? filterData.sjukskrivningslangd : [];
    businessFilter.sjukskrivningslangdSaved = _.cloneDeep(businessFilter.selectedSjukskrivningslangdIds);
    businessFilter.selectedIntygstyperIds = filterData.sjukskrivningslangd ? filterData.intygstyper : [];
    businessFilter.intygstyperSaved = _.cloneDeep(businessFilter.selectedIntygstyperIds);
    businessFilter.selectedAldersgruppIds =  filterData.aldersgrupp ? filterData.aldersgrupp : [];
    businessFilter.aldersgruppSaved =_.cloneDeep(businessFilter.selectedAldersgruppIds);

    var enheter = filterData.enheter ? filterData.enheter : [];
    businessFilter.geographyBusinessIdsSaved = _.cloneDeep(enheter);
    businessFilter.selectGeographyBusiness(enheter);
    businessFilter.toDate = filterData.toDate ? moment(filterData.toDate).utc().toDate() : null;
    businessFilter.toDateSaved = businessFilter.toDate;
    businessFilter.fromDate = filterData.fromDate ? moment(filterData.fromDate).utc().toDate() : null;
    businessFilter.fromDateSaved = businessFilter.fromDate;
    businessFilter.useDefaultPeriod = filterData.useDefaultPeriod;
    businessFilter.updateHasUserSelection();
  }

  businessFilter.populateIcd10Structure = function() {
    businessFilter.setIcd10Structure(StaticData.get().icd10Structure);
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

      statisticsData.getFilterData(preSelectedFilter, function(filterData) {
        deferred.resolve(filterData);
      }, function() {
        throw new Error('Could not parse filter');
      });
    } else {
      deferred.resolve(null);
    }

    return deferred.promise;
  }

  function populateStaticData() {
    if (businessFilter.icd10.subs.length > 0) {
      var deferred = $q.defer();
      deferred.resolve(true);
      return deferred.promise;
    } else {
      return StaticDataService.get();
    }
  }

  businessFilter.setup = function(businesses, preSelectedFilter) {
    populateStaticData().then(function() {
      businessFilter.businesses = businesses;
      if (businessFilter.showBusinessFilter()) {
        businessFilter.populateGeography(businesses);
        businessFilter.populateVerksamhetsTyper(businesses);
        businessFilter.businesses = ArrayHelper.sortSwedish(businesses, 'name', 'Okän');
      }
      businessFilter.populateSjukskrivningsLangd();
      businessFilter.populateIntygstyper();
      businessFilter.populateAldersgrupp();
      businessFilter.populateIcd10Structure();
      businessFilter.dataInitialized = true;
      businessFilter.selectPreselectedFilter(preSelectedFilter);
    });
  };

  businessFilter.showBusinessFilter = function() {
    return businessFilter.businesses.length > 1;
  };

  businessFilter.populateGeography = function(businesses) {
    var distributedCareUnitIds = businessFilter.getDistributedCareUnitIds(businesses);

    businessFilter.geography = {subs: []};
    _.each(businesses, function(business) {
      var county = _.find(businessFilter.geography.subs, {name: business.lansName});
      if (!county) {
        county = {
          id: business.lansId,
          numericalId: business.lansId + 'county',
          name: business.lansName,
          visibleName: business.lansName,
          subs: []
        };
        businessFilter.geography.subs.push(county);
      }

      var munip = _.find(county.subs, {name: business.kommunName});
      if (!munip) {
        munip = {
          id: business.kommunId,
          numericalId: business.kommunId + 'munip',
          name: business.kommunName,
          visibleName: business.kommunName,
          subs: []
        };
        county.subs.push(munip);
      }

      business.numericalId = business.id;
      business.visibleName = business.name;

      var existingVardenhetInFilter = _.find(munip.subs, {id: business.vardenhet ? business.id : business.vardenhetId});

      if (business.vardenhet && !existingVardenhetInFilter) {
        munip.subs.push({
          id: business.id,
          numericalId: business.id + 'vardenhet',
          name: business.name,
          visibleName: business.visibleName,
          distributedCareUnit: distributedCareUnitIds.indexOf(business.id) > -1,
          subs: []
        });
      } else if (business.vardenhet && existingVardenhetInFilter) {
        existingVardenhetInFilter.name = business.name;
        existingVardenhetInFilter.visibleName = business.visibleName;
      } else if (!business.vardenhet && existingVardenhetInFilter) {
        existingVardenhetInFilter.subs.push(business);
      } else {
        munip.subs.push({
          id: business.vardenhetId,
          numericalId: business.vardenhetId + 'vardenhet',
          name: businessFilter.getCareUnitName(business.vardenhetId, businesses), //business.vardenhetId,
          visibleName: businessFilter.getCareUnitNameAndMunip(business.vardenhetId, businesses), //business.vardenhetId,
          distributedCareUnit: distributedCareUnitIds.indexOf(business.vardenhetId) > -1,
          subs: [business]
        });
      }
    });

    businessFilter.sortItemsAlphabetically(businessFilter);
  };

  businessFilter.sortItemsAlphabetically = function(businessFilter) {
    businessFilter.geography.subs = ArrayHelper.sortSwedish(businessFilter.geography.subs, 'name', 'Okän');
    _.each(businessFilter.geography.subs, function(county) {
      county.subs = ArrayHelper.sortSwedish(county.subs, 'name', 'Okän');
      _.each(county.subs, function(munip) {
        munip.subs = ArrayHelper.sortSwedish(munip.subs, 'name', 'Okän');
        _.each(munip.subs, function(careUnit) {
          careUnit.subs = ArrayHelper.sortSwedish(careUnit.subs, 'name', 'Okän');
        });
      });
    });
  };

  businessFilter.getDistributedCareUnitIds = function(businesses) {
    var distributedCareUnitIds = [];
    var allCareUnits = _.filter(businesses, {vardenhet: true});
    _.each(allCareUnits, function(careUnit) {
      var unitsInCareUnit = _.filter(businesses, {vardenhet: false} && {vardenhetId: careUnit.id});
      _.each(unitsInCareUnit, function(unit){
        if (unit.kommunId !== careUnit.kommunId && distributedCareUnitIds.indexOf(careUnit.id) < 0) {
          distributedCareUnitIds.push(careUnit.id);
        }
      });
    });
    return distributedCareUnitIds;
  };

  businessFilter.getCareUnitName = function(businessId, businesses) {
    var careUnit = _.find(businesses, {id: businessId});
    if (careUnit) {
      return careUnit.visibleName;
    }
    return businessId;
  };

  businessFilter.getCareUnitNameAndMunip = function(businessId, businesses) {
    var careUnit = _.find(businesses, {id: businessId});
    if (careUnit) {
      return careUnit.visibleName  + ' (' + careUnit.kommunName + ')';
    }
    return businessId;
  };

  businessFilter.populateSjukskrivningsLangd = function() {
    businessFilter.sjukskrivningslangd = StaticData.get().sjukskrivningLengths;
  };

  businessFilter.populateIntygstyper = function() {
    businessFilter.intygstyper = StaticData.get().intygTypes;
  };

  businessFilter.populateAldersgrupp = function() {
    businessFilter.aldersgrupp = StaticData.get().aldersgrupps;
  };

  businessFilter.populateVerksamhetsTyper = function(businesses) {
    var verksamhetsTypSet = {};
    _.each(businesses, function(business) {
      _.each(business.verksamhetsTyper, function(verksamhetsTyp) {
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
    businessFilter.verksamhetsTyper = ArrayHelper.sortSwedish(_.values(verksamhetsTypSet), 'name', 'Okän');
  };

  businessFilter.selectAll = function(item) {
    businessFilter.selectAllWithValue(item, true);
  };

  businessFilter.deselectAll = function(item) {
    businessFilter.selectAllWithValue(item, false);
  };

  businessFilter.selectAllWithValue = function(item, selected) {
    if (treeMultiSelectorUtil.isCareUnit(item)) {
      item.isSelected = selected;
    }
    item.allSelected = selected;
    item.someSelected = false;
    if (item.subs) {
      _.each(item.subs, function(sub) {
        businessFilter.selectAllWithValue(sub, selected);
      });
    }
  };

  businessFilter.selectByAttribute = function(item, listOfIdsToSelect, attribute) {
    if (_.some(listOfIdsToSelect, function(val) {
      return item[attribute] === val;
    })) {
      if (treeMultiSelectorUtil.isCareUnit(item)) {
        item.isSelected = true;
      }
      businessFilter.selectAll(item);
    } else {
      item.allSelected = false;
      item.someSelected = false;
      if (item.subs) {
        _.each(item.subs, function(sub) {
          businessFilter.selectByAttribute(sub, listOfIdsToSelect, attribute);
        });
      }
    }
  };

  businessFilter.collectGeographyIds = function(node) {
    if (node.subs) {
      if (node.allSelected || node.someSelected || node.isSelected) {
        return _.reduce(node.subs, function(acc, item) {
          return acc.concat(businessFilter.collectGeographyIds(item));
        }, node.isSelected ? [node.id] : []);
      } else {
        return [];
      }
    } else {
      return node.allSelected ? [node.id] : [];
    }
  };

  businessFilter.getSelectedLeaves = function(node) {
    if (node.subs && node.subs.length !== 0) {
      return _.reduce(node.subs, function(memo, item) {
        return memo.concat(businessFilter.getSelectedLeaves(item));
      }, []);
    } else {
      return node.allSelected ? [node] : [];
    }
  };

  businessFilter.collectSummary = function(node, acc) {
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
          _.each(node.subs, function(subItem) {
            businessFilter.collectSummary(subItem, acc);
          });
        }
      } else if (node.someSelected) {
        _.each(node.subs, function(subItem) {
          businessFilter.collectSummary(subItem, acc);
        });
      }
    } else { // Kategori
      if (node.allSelected) {
        acc.push(node.numericalId);
      }
    }
  };

  businessFilter.updateGeography = function() {
    businessFilter.geographyBusinessIds = businessFilter.collectGeographyIds(businessFilter.geography);
  };

  businessFilter.updateDiagnoses = function() {
    businessFilter.selectedDiagnoses = [];
    businessFilter.collectSummary(businessFilter.icd10, businessFilter.selectedDiagnoses);
  };

  businessFilter.setSelectedGeography = function() {
    businessFilter.selectGeographyBusiness(businessFilter.geographyBusinessIds);
  };

  businessFilter.setSelectedDiagnoses = function() {
    businessFilter.selectDiagnoses(businessFilter.selectedDiagnoses);
  };

  return businessFilter;
}
