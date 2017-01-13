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


angular.module('StatisticsApp').factory('ControllerCommons',
    /** @ngInject */
    function(_, $cacheFactory, UserModel, $filter) {
        'use strict';

        var that = this;

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
            if (that.isNumber(input)) {
                var splittedOnDot = input.toString().split('\.');
                var integerPartThousandSeparated = splittedOnDot[0].split('').reverse().join('')
                                                    .match(/.{1,3}/g).join('\u00A0').split('').reverse().join('');
                if (splittedOnDot.length === 1) {
                    return integerPartThousandSeparated;
                }
                return integerPartThousandSeparated + ',' + splittedOnDot[1];
            }
            return input;
        };

        this.getFileName = function(chartName) {
            var d = new Date();

            var year = '' + d.getFullYear();
            var month = d.getMonth() < 9 ? '0' + (d.getMonth() + 1) : '' + (d.getMonth() + 1);
            var day = d.getDate() < 10 ? '0' + d.getDate() : '' + d.getDate();
            var date = year + month + day;

            var hour = d.getHours() < 10 ? '0' + d.getHours() : '' + d.getHours();
            var minute = d.getMinutes() < 10 ? '0' + d.getMinutes() : '' + d.getMinutes();
            var second = d.getSeconds() < 10 ? '0' + d.getSeconds() : '' + d.getSeconds();
            var time = hour + minute + second;

            return String(chartName).replace(/\s+/g, '_') + '_' + date + '_' + time;
        };

        function icdStructureAsArray(icdStructure) {
            return _.map(icdStructure, function (icd) {
                return icdStructureAsArray(icd.subItems).concat(icd);
            });
        }

        this.getDiagnosFilterInformationText = function(diagnosFilterIds, icdStructure, asObject) {
            var icdStructureAsFlatArray = _.compose(_.flattenDeep, icdStructureAsArray)(icdStructure);
            return _.map(diagnosFilterIds, function(diagnosId){
                var icdItem = _.find(icdStructureAsFlatArray, function(icd){
                    return icd.numericalId === parseInt(diagnosId, 10);
                });

                var text = icdItem.id + ' ' + icdItem.name;

                if (asObject) {
                    return {
                        id: diagnosId,
                        text: text
                    };
                }
                return text;
            });
        };

        this.populateActiveFilters = function(scope, statisticsData, diagnosIds, isAllAvailableDxsSelectedInFilter, filterHash,
                                                isAllAvailableEnhetsSelectedInFilter, filteredEnhets, filteredSjukskrivningslangd, isAllAvailableSjukskrivningslangdsSelectedInFilter, filteredAldersgrupp, isAllAvailableAgeGroupsSelectedInFilter) {
            that.populateActiveDiagnosFilter(scope, statisticsData, diagnosIds, isAllAvailableDxsSelectedInFilter);
            that.populateActiveEnhetsFilter(scope, filteredEnhets, isAllAvailableEnhetsSelectedInFilter);
            that.populateActiveSjukskrivningslangdFilter(scope, filterHash, filteredSjukskrivningslangd, isAllAvailableSjukskrivningslangdsSelectedInFilter);
            that.populateActiveAldersgruppFilter(scope, filterHash, filteredAldersgrupp, isAllAvailableAgeGroupsSelectedInFilter);
        };

        this.populateActiveDiagnosFilter = function(scope, statisticsData, diagnosIds, isAllAvailableDxsSelectedInFilter) {
            if (isAllAvailableDxsSelectedInFilter) {
                return;
            }
            if (!diagnosIds) {
                return;
            }
            if (diagnosIds.length === 0) {
                scope.activeDiagnosFilters = null;
                return;
            }
            statisticsData.getIcd10Structure(function (diagnoses) {
                scope.activeDiagnosFilters = diagnoses ? that.getDiagnosFilterInformationText(diagnosIds, diagnoses) : null;
            }, function () {
                scope.activeDiagnosFilters = diagnosIds;
            });
        };

        this.populateActiveEnhetsFilter = function(scope, enhetNames, isAllAvailableEnhetsSelectedInFilter) {
            if (UserModel.get().isProcessledare && isAllAvailableEnhetsSelectedInFilter) {
                scope.activeEnhetsFilters = ['Samtliga enheter inom vårdgivaren ' + scope.vgName];
            } else {
                scope.activeEnhetsFilters = enhetNames;
            }
        };

        this.populateActiveSjukskrivningslangdFilter = function(scope, filterHash, sjukskrivningslangds, isAllAvailableSjukskrivningslangdsSelectedInFilter) {
            scope.activeSjukskrivningslangdsFilters = null;
            if (isAllAvailableSjukskrivningslangdsSelectedInFilter) {
                return;
            }
            if (sjukskrivningslangds && sjukskrivningslangds.length > 0) {
                scope.activeSjukskrivningslangdsFilters = sjukskrivningslangds;
            }
        };

        this.populateActiveAldersgruppFilter = function(scope, filterHash, aldersgrupp, isAllAvailableAgeGroupsSelectedInFilter) {
            scope.activeAldersgruppFilters = null;
            if (isAllAvailableAgeGroupsSelectedInFilter) {
                return;
            }
            if (aldersgrupp && aldersgrupp.length > 0) {
                scope.activeAldersgruppFilters = aldersgrupp;
            }
        };

        this.setupDiagnosisSelector = function(diagnosisTreeFilter, $routeParams, $scope, messageService, $timeout, statisticsData, $location) {
            //Initiate the diagnosisTree if we need to.
            diagnosisTreeFilter.setup($routeParams);
            $scope.diagnosisTreeFilter = diagnosisTreeFilter;

            $scope.diagnosisSelectorData = {
                titleText: messageService.getProperty('comparediagnoses.lbl.val-av-diagnoser', null, '', null, true),
                buttonLabelText: messageService.getProperty('lbl.filter.val-av-diagnoser-knapp', null, '', null, true),
                firstLevelLabelText: messageService.getProperty('lbl.filter.modal.kapitel', null, '', null, true),
                secondLevelLabelText: messageService.getProperty('lbl.filter.modal.avsnitt', null, '', null, true),
                thirdLevelLabelText: messageService.getProperty('lbl.filter.modal.kategorier', null, '', null, true)
            };

            $scope.diagnosisSelected = function () {
                that.diagnosisToCompareSelected(diagnosisTreeFilter, $timeout, $scope, statisticsData, $location);
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
                var newPath = path.replace(/\/[^\/]+$/gm, '/' + selectionHash);

                if (path === newPath) {
                    $timeout(function () {
                        $scope.doneLoading = true;
                    }, 1);
                } else {
                    $location.path(newPath);
                }
            };

            var error = function () {
                throw new Error('Failed to get filter hash value');
            };

            statisticsData.getFilterHash(params).then(success, error);
        };

        this.isShowingVerksamhet = function($location) {
            return $location.path().indexOf('/verksamhet/') === 0;
        };

        this.isShowingLandsting = function($location) {
            return $location.path().indexOf('/landsting/') === 0;
        };

        this.isShowingProtectedPage = function(location) {
            return this.isShowingVerksamhet(location) || this.isShowingLandsting(location);
        };

        this.createQueryStringOfQueryParams = function (queryParams) {
            return !_.isEmpty(queryParams) ? _.map(queryParams, function (value, key) {
                return key + '=' + value;
            }).join('&') : '';
        };

        this.getExtraPathParam = function(routeParams) {
            return routeParams.diagnosHash ? routeParams.diagnosHash : that.getMostSpecificGroupId(routeParams);
        };

        this.getMostSpecificGroupId = function(routeParams) {
            return routeParams.kategoriId ? routeParams.kategoriId : (routeParams.avsnittId ? routeParams.avsnittId : routeParams.kapitelId);
        };

        this.populateDetailsOptions = function (result, basePath, $scope, $routeParams, messageService, config) {
            var kapitels = result.kapitels;
            var avsnitts = result.avsnitts[$routeParams.kapitelId];
            var kategoris = result.kategoris[$routeParams.avsnittId];

            setSelectedOptions($scope, $routeParams, kapitels, avsnitts, kategoris);

            $scope.detailsOptions = _.map(kapitels, function (e) {
                e.url = basePath + '/kapitel/' + e.id;
                return e;
            });
            $scope.detailsOptions2 = _.map(avsnitts, function (e) {
                e.url = basePath + '/kapitel/' + $routeParams.kapitelId + '/avsnitt/' + e.id;
                return e;
            });
            $scope.detailsOptions3 = _.map(kategoris, function (e) {
                e.url = basePath + '/kapitel/' + $routeParams.kapitelId + '/avsnitt/' + $routeParams.avsnittId + '/kategori/' + e.id;
                return e;
            });

            //Add default option for detailsOptions2
            var defaultId = messageService.getProperty('lbl.valj-annat-diagnosavsnitt', null, '', null, true);
            $scope.detailsOptions2.unshift({'id': defaultId, 'name':'', 'url':basePath + '/kapitel/' + $routeParams.kapitelId});
            if (!$scope.selectedDetailsOption2) {
                $scope.selectedDetailsOption2 = $scope.detailsOptions2[0];
            }

            if ($routeParams.avsnittId) {
                //Add default option for detailsOptions3
                var defaultIdKategori = messageService.getProperty('lbl.valj-annan-diagnoskategori', null, '', null, true);
                $scope.detailsOptions3.unshift({
                    'id': defaultIdKategori,
                    'name': '',
                    'url': basePath + '/kapitel/' + $routeParams.kapitelId + '/avsnitt/' + $routeParams.avsnittId
                });
                if (!$scope.selectedDetailsOption3) {
                    $scope.selectedDetailsOption3 = $scope.detailsOptions3[0];
                }
            }

            $scope.subTitle = getSubtitle($scope.selectedDetailsOption, $scope.selectedDetailsOption2,
                                            $scope.selectedDetailsOption3, config);
        };

        function setSelectedOptions($scope, $routeParams, kapitels, avsnitts, kategoris) {
            var i;
            for (i = 0; i < kapitels.length; i++) {
                if (kapitels[i].id === $routeParams.kapitelId) {
                    $scope.selectedDetailsOption = kapitels[i];
                    break;
                }
            }

            for (i = 0; i < avsnitts.length; i++) {
                if (avsnitts[i].id === $routeParams.avsnittId) {
                    $scope.selectedDetailsOption2 = avsnitts[i];
                    break;
                }
            }

            if (kategoris) {
                for (i = 0; i < kategoris.length; i++) {
                    if (kategoris[i].id === $routeParams.kategoriId) {
                        $scope.selectedDetailsOption3 = kategoris[i];
                        break;
                    }
                }
            }
        }

        function getDiagnosPathPart($routeParams) {
            var path = '';
            if ($routeParams.kapitelId) {
                path += '/kapitel/' + $routeParams.kapitelId;

                if ($routeParams.avsnittId) {
                    path += '/avsnitt/' + $routeParams.avsnittId;

                    if ($routeParams.kategoriId) {
                        path += '/kategori/' + $routeParams.kategoriId;
                    }
                }
            }

            return path;
        }

        function getSubtitle(selectedOption1, selectedOption2, selectedOption3, config) {
            if ((selectedOption3 && selectedOption3.name && selectedOption3.id)) {
                return config.suffixTitle(selectedOption3.id + ' ' + selectedOption3.name);
            }
            if ((selectedOption2 && selectedOption2.name && selectedOption2.id)) {
                return config.suffixTitle(selectedOption2.id + ' ' + selectedOption2.name);
            }
            if (selectedOption1 && selectedOption1.name && selectedOption1.id) {
                return config.suffixTitle(selectedOption1.id + ' ' + selectedOption1.name);
            }
            return '';
        }

        this.createDiagnosHashPathOrAlternativePath = function($routeParams){
            return ($routeParams.diagnosHash ? '/' + $routeParams.diagnosHash : getDiagnosPathPart($routeParams));
        };

        this.getResultMessageList = function(result, messageService) {
            if (angular.isArray(result.messages) && result.messages.length > 0) {
                return result.messages;
            }

            if (result.empty) {
                return this.getEmptyResponseMessage(messageService, result);
            }

            return [];
        };

        this.getEmptyResponseMessage = function(messageService) {
            return [ {type: 'UNSET', severity: 'WARN', message: messageService.getProperty('info.emptyresponse', null, '', null, true)} ];
        };

        this.removeFilterMessages = function(messages) {
          return $filter('filter')(messages, function(message) {
              return message && message.type !== 'FILTER';
          });
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

        this.checkNationalResultAndEnableExport = function($scope, result, verksamhet, landsting, success) {
            $scope.errorPageUrl = null;
            if (result === '' && !verksamhet && !landsting) {
                $scope.dataLoadingError = true;
                $scope.errorPageUrl = '/app/views/error/statisticNotDone.html';

                $cacheFactory.get('$http').removeAll();
            } else {
                $scope.exportEnabled = true;
                success(result);
            }
        };

        function getStatisticsTypeForChartType(chartType) {
            if (chartType === 'column') {
                return 'Tvärsnitt';
            }
            return 'Tidsserie';
        }

        this.rerouteWhenNeededForChartTypeChange = function(currentChartType, chartType, exchangeableViews, location, routeParams) {
            if (chartType !== currentChartType) {
                var statisticsTypeForChartType = getStatisticsTypeForChartType(chartType);
                var newStatisticsType = _.find(exchangeableViews, function(exchangeableView) {
                    return exchangeableView.description === statisticsTypeForChartType;
                });
                var newPath = newStatisticsType.state + that.createDiagnosHashPathOrAlternativePath(routeParams);
                location.search('chartType', chartType).path(newPath);
            }
        };

        this.getChartTypeInfo = function($routeParams, config, defaultChartType) {
            var activeChartType = $routeParams.chartType || config.defaultChartType || defaultChartType;
            var usePercentChart = activeChartType === 'percentarea' || config.percentChart;
            var activeHighchartType = config.highchartType ? config.highchartType : (usePercentChart ? 'area' : activeChartType);
            var stacked = activeHighchartType === 'area' || usePercentChart;
            return {activeChartType: activeChartType, usePercentChart: usePercentChart, activeHighchartType: activeHighchartType, stacked: stacked};
        };

        return this;
});


