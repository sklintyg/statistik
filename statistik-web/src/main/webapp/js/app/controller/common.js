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

var ControllerCommons = new function(){

    this.updateDataTable = function (scope, tableData) {
        scope.headerrows = tableData.headers;
        if (scope.headerrows.length > 1) {
            scope.headerrows[0].centerAlign = true;
        }
        scope.rows = tableData.rows;
    };

    this.htmlsafe = function(string) {
        return string.replace(/&/g, '&amp;').replace(/</g, '&lt;');
    };

    this.isNumber = function(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    };

    this.makeThousandSeparated = function(input) {
        if (ControllerCommons.isNumber(input)) {
            var splittedOnDot = input.toString().split('\.');
            var integerPartThousandSeparated = splittedOnDot[0].split('').reverse().join('').match(/.{1,3}/g).join('\u00A0').split('').reverse().join('');
            if (splittedOnDot.length === 1) {
                return integerPartThousandSeparated;
            }
            return integerPartThousandSeparated + "," + splittedOnDot[1];
        }
        return input;
    };

    this.getFileName = function(chartName) {
        var d = new Date();

        var year = "" + d.getFullYear();
        var month = d.getMonth() < 9 ? "0" + (d.getMonth() + 1) : "" + (d.getMonth() + 1);
        var day = d.getDate() < 10 ? "0" + d.getDate() : "" + d.getDate();
        var date = year + month + day;

        var hour = d.getHours() < 10 ? "0" + d.getHours() : "" + d.getHours();
        var minute = d.getMinutes() < 10 ? "0" + d.getMinutes() : "" + d.getMinutes();
        var second = d.getSeconds() < 10 ? "0" + d.getSeconds() : "" + d.getSeconds();
        var time = hour + minute + second;

        return String(chartName).replace(/\s+/g, "_") + "_" + date + "_" + time;
    };

    this.getEnhetCountText = function(enhetsCount, basedOnAlreadyInText) {
        if (enhetsCount === 1) {
            return " ";
        }
        if (basedOnAlreadyInText) {
            return enhetsCount ? " och " + enhetsCount + " enheter" + " " : " ";
        }
        return enhetsCount ? " baserat på " + enhetsCount + " enheter" + " " : " ";
    };

    function icdStructureAsArray(icdStructure) {
        return _.map(icdStructure, function (icd) {
            return icdStructureAsArray(icd.subItems).concat(icd);
        });
    }

    this.getDiagnosFilterInformationText = function(diagnosFilterIds, icdStructure) {
        var icdStructureAsFlatArray = _.compose(_.flattenDeep, icdStructureAsArray)(icdStructure);
        return _.map(diagnosFilterIds, function(diagnosId){
            var icdItem = _.find(icdStructureAsFlatArray, function(icd){
                return icd.numericalId === parseInt(diagnosId, 10);
            });
            return icdItem.id + " " + icdItem.name;
        });
    };

    this.populateActiveFilters = function(scope, statisticsData, diagnosIds, isPrint, isAllAvailableDxsSelectedInFilter, filterHash, isAllAvailableEnhetsSelectedInFilter, filteredEnhets) {
        this.populateActiveDiagnosFilter(scope, statisticsData, diagnosIds, isPrint, isAllAvailableDxsSelectedInFilter);
        this.populateActiveEnhetsFilter(scope, filterHash, isPrint, isAllAvailableEnhetsSelectedInFilter, filteredEnhets);
    };

    this.populateActiveDiagnosFilter = function(scope, statisticsData, diagnosIds, isPrint, isAllAvailableDxsSelectedInFilter) {
        if (isAllAvailableDxsSelectedInFilter) {
            return;
        }
        if (!diagnosIds) {
            return;
        }
        if (diagnosIds.length === 0) {
            scope.activeDiagnosFilters = [""];
            scope.activeDiagnosFiltersForPrint = isPrint ? scope.activeDiagnosFilters : null;
            return;
        }
        statisticsData.getIcd10Structure(function (diagnoses) {
            scope.activeDiagnosFilters = diagnoses ? ControllerCommons.getDiagnosFilterInformationText(diagnosIds, diagnoses) : null;
            scope.activeDiagnosFiltersForPrint = isPrint ? scope.activeDiagnosFilters : null;
        }, function () {
            scope.activeDiagnosFilters = diagnosIds;
            scope.activeDiagnosFiltersForPrint = isPrint ? scope.activeDiagnosFilters : null;
        });
    };

    this.populateActiveEnhetsFilter = function(scope, filterHash, isPrint, isAllAvailableEnhetsSelectedInFilter, enhetNames) {
        if (isAllAvailableEnhetsSelectedInFilter) {
            scope.headerEnhetInfo = scope.verksamhetName;
            return;
        }
        if (!filterHash) {
            scope.headerEnhetInfo = scope.verksamhetName;
            return;
        }
        if (enhetNames.length === 1) {
            scope.headerEnhetInfo = enhetNames[0];
        } else {
            scope.headerEnhetInfo = "";
            scope.activeEnhetsFilters = enhetNames.length > 1 ? enhetNames : [""];
            scope.activeEnhetsFiltersForPrint = isPrint ? scope.activeEnhetsFilters : null;
        }
    };

    this.setupDiagnosisSelector = function(diagnosisTreeFilter, $routeParams, $scope, messageService, $timeout, statisticsData, $location) {
        //Initiate the diagnosisTree if we need to.
        diagnosisTreeFilter.setup($routeParams);
        $scope.diagnosisTreeFilter = diagnosisTreeFilter;

        $scope.diagnosisSelectorData = {
            titleText: messageService.getProperty("comparediagnoses.lbl.val-av-diagnoser", null, "", null, true),
            buttonLabelText: messageService.getProperty("lbl.filter.val-av-diagnoser-knapp", null, "", null, true),
            firstLevelLabelText: messageService.getProperty("lbl.filter.modal.kapitel", null, "", null, true),
            secondLevelLabelText: messageService.getProperty("lbl.filter.modal.avsnitt", null, "", null, true),
            thirdLevelLabelText: messageService.getProperty("lbl.filter.modal.kategorier", null, "", null, true)
        };

        $scope.diagnosisSelected = function () {
            ControllerCommons.diagnosisToCompareSelected(diagnosisTreeFilter, $timeout, $scope, statisticsData, $location);
        };
    };

    this.diagnosisToCompareSelected = function(diagnosisTreeFilter, $timeout, $scope, statisticsData, $location) {
        var diagnoses = diagnosisTreeFilter.getSelectedDiagnosis();

        $timeout(function () {
            //Ugly fix from http://stackoverflow.com/questions/20827282/cant-dismiss-modal-and-change-page-location
            $('#cancelModal').modal('hide');
            $('.modal-backdrop').remove();
            $('body').removeClass('modal-open');
        }, 1);

        $timeout(function () {
            $scope.doneLoading = false;
        }, 1);

        var params = {
            diagnoser: diagnoses
        };

        var success = function (selectionHash) {
            var path = $location.path();
            var newPath = path.replace(/\/[^\/]+$/gm, "/" + selectionHash);

            if (path === newPath) {
                $timeout(function () {
                    $scope.doneLoading = true;
                }, 1);
            } else {
                $location.path(newPath);
            }
        };

        var error = function () {
            throw new Error("Failed to get filter hash value");
        };

        statisticsData.getFilterHash(params).then(success, error);
    };

    this.isShowingVerksamhet = function($location) {
        return $location.path().indexOf("/verksamhet/") === 0;
    };

    this.isShowingLandsting = function($location) {
        return $location.path().indexOf("/landsting/") === 0;
    };

    this.createQueryStringOfQueryParams = function (queryParams) {
        return !_.isEmpty(queryParams) ? _.map(queryParams, function (value, key) {
            return key + "=" + value;
        }).join('&') : '';
    };

    this.getExtraPathParam = function(routeParams) {
        return routeParams.diagnosHash ? routeParams.diagnosHash : ControllerCommons.getMostSpecificGroupId(routeParams);
    };

    this.getMostSpecificGroupId = function(routeParams) {
        return routeParams.kategoriId ? routeParams.kategoriId : (routeParams.avsnittId ? routeParams.avsnittId : routeParams.kapitelId);
    };

    this.populateDetailsOptions = function (result, basePath, $scope, $routeParams, messageService, config) {
        var kapitels = result.kapitels;
        for (var i = 0; i < kapitels.length; i++) {
            if (kapitels[i].id === $routeParams.kapitelId) {
                $scope.selectedDetailsOption = kapitels[i];
                break;
            }
        }
        var avsnitts = result.avsnitts[$routeParams.kapitelId];
        for (var i = 0; i < avsnitts.length; i++) {
            if (avsnitts[i].id === $routeParams.avsnittId) {
                $scope.selectedDetailsOption2 = avsnitts[i];
                break;
            }
        }
        var kategoris = result.kategoris[$routeParams.avsnittId];
        if (kategoris) {
            for (var i = 0; i < kategoris.length; i++) {
                if (kategoris[i].id === $routeParams.kategoriId) {
                    $scope.selectedDetailsOption3 = kategoris[i];
                    break;
                }
            }
        }

        $scope.detailsOptions = _.map(kapitels, function (e) {
            e.url = basePath + "/kapitel/" + e.id;
            return e;
        });
        $scope.detailsOptions2 = _.map(avsnitts, function (e) {
            e.url = basePath + "/kapitel/" + $routeParams.kapitelId + "/avsnitt/" + e.id;
            return e;
        });
        $scope.detailsOptions3 = _.map(kategoris, function (e) {
            e.url = basePath + "/kapitel/" + $routeParams.kapitelId + "/avsnitt/" + $routeParams.avsnittId + "/kategori/" + e.id;
            return e;
        });

        //Add default option for detailsOptions2
        var defaultId = messageService.getProperty("lbl.valj-annat-diagnosavsnitt", null, "", null, true);
        $scope.detailsOptions2.unshift({"id": defaultId, "name":"", "url":basePath + "/kapitel/" + $routeParams.kapitelId});
        if (!$scope.selectedDetailsOption2) {
            $scope.selectedDetailsOption2 = $scope.detailsOptions2[0];
        }

        if ($routeParams.avsnittId) {
            //Add default option for detailsOptions3
            var defaultIdKategori = messageService.getProperty("lbl.valj-annan-diagnoskategori", null, "", null, true);
            $scope.detailsOptions3.unshift({
                "id": defaultIdKategori,
                "name": "",
                "url": basePath + "/kapitel/" + $routeParams.kapitelId + "/avsnitt/" + $routeParams.avsnittId
            });
            if (!$scope.selectedDetailsOption3) {
                $scope.selectedDetailsOption3 = $scope.detailsOptions3[0];
            }
        }
        
        $scope.subTitle = getSubtitle($scope.currentPeriod, $scope.selectedDetailsOption, $scope.selectedDetailsOption2, $scope.selectedDetailsOption3, $scope, config);
    };

    function getDiagnosPathPart($routeParams) {
        return $routeParams.kapitelId ? "/kapitel/" + $routeParams.kapitelId + ($routeParams.avsnittId ? "/avsnitt/" + $routeParams.avsnittId + ($routeParams.kategoriId ? "/kategori/" + $routeParams.kategoriId : "") : "") : "";
    }

    function getSubtitle(period, selectedOption1, selectedOption2, selectedOption3, $scope, config) {
        if ((selectedOption3 && selectedOption3.name && selectedOption3.id)) {
            return config.title(period, $scope.enhetsCount, selectedOption3.id + " " + selectedOption3.name);
        }
        if ((selectedOption2 && selectedOption2.name && selectedOption2.id)) {
            return config.title(period, $scope.enhetsCount, selectedOption2.id + " " + selectedOption2.name);
        }
        if (selectedOption1 && selectedOption1.name && selectedOption1.id) {
            return config.title(period, $scope.enhetsCount, selectedOption1.id + " " + selectedOption1.name);
        }
        return "";
    }

    this.createDiagnosHashPathOrAlternativePath = function($routeParams){
        return ($routeParams.diagnosHash ? "/" + $routeParams.diagnosHash : getDiagnosPathPart($routeParams));
    };

    this.updateExchangeableViewsUrl = function(isVerksamhet, config, $location, $scope, $routeParams) {
        if (isVerksamhet && config.exchangeableViews) {
            //If we have a diagnosisHash then added to the next route before anything else
            _.each(config.exchangeableViews, function (view) {
                view.state = view.state + ControllerCommons.createDiagnosHashPathOrAlternativePath($routeParams);
            });
            var queryParamsString = ControllerCommons.createQueryStringOfQueryParams($location.search());
            //Add queryParams if any
            if (queryParamsString) {
                _.each(config.exchangeableViews, function (view) {
                    view.state = view.state + "?" + queryParamsString;
                });
            }
            $scope.exchangeableViews = config.exchangeableViews;

        }
    };

    this.getResultMessage = function(result, messageService) {
        return result.message ? result.message : (result.empty ? messageService.getProperty("info.emptyreponse", null, "", null, true) : "");
    };

    this.formatOverViewTablePDF = function(thousandseparatedFilter, data, nameSuffix) {
        var tableData = [];

        if (!nameSuffix) {
            nameSuffix = '';
        }

        angular.forEach(data, function(row) {
            tableData.push([row.color, row.name + nameSuffix, thousandseparatedFilter(row.quantity), row.alternation + ' %']);
        });

        return tableData;
    };

};


