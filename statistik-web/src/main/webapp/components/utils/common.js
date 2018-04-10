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


angular.module('StatisticsApp').factory('ControllerCommons',
    /** @ngInject */
    function(_, $cacheFactory, UserModel, $filter, $route, StaticFilterData) {
        'use strict';

        var that = this;

        this.updateDataTable = function (scope, tableData) {
            scope.headerrows = tableData.headers;
            if (scope.headerrows && scope.headerrows.length > 1) {
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

        this.populateActiveFilters = function(scope, statisticsData, filterHash, diagnosIds, isAllAvailableDxsSelectedInFilter,
                                                filteredEnhets, isAllAvailableEnhetsSelectedInFilter, filteredSjukskrivningslangd, isAllAvailableSjukskrivningslangdsSelectedInFilter,
                                                filteredAldersgrupp, isAllAvailableAgeGroupsSelectedInFilter, filteredIntygstyp, isAllAvailableIntygTypesSelectedInFilter) {
            that.populateActiveDiagnosFilter(scope, statisticsData, diagnosIds, isAllAvailableDxsSelectedInFilter);
            that.populateActiveEnhetsFilter(scope, filteredEnhets, isAllAvailableEnhetsSelectedInFilter);
            that.populateActiveSjukskrivningslangdFilter(scope, filterHash, filteredSjukskrivningslangd, isAllAvailableSjukskrivningslangdsSelectedInFilter);
            that.populateActiveAldersgruppFilter(scope, filterHash, filteredAldersgrupp, isAllAvailableAgeGroupsSelectedInFilter);
            that.populateActiveIntygstypFilter(scope, filterHash, filteredIntygstyp, isAllAvailableIntygTypesSelectedInFilter);
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
            var dxsTexts = StaticFilterData.getDiagnosFilterInformationText(diagnosIds);
            var foundInfoTextForAllDxs = dxsTexts && diagnosIds && dxsTexts.length === diagnosIds.length;
            scope.activeDiagnosFilters = foundInfoTextForAllDxs ? dxsTexts : diagnosIds;
        };

        this.populateActiveEnhetsFilter = function(scope, enhetNames, isAllAvailableEnhetsSelectedInFilter) {
            if (UserModel.get().isProcessledare && isAllAvailableEnhetsSelectedInFilter) {
                scope.activeEnhetsFilters = ['Samtliga enheter inom vårdgivaren ' + scope.vgName];
            } else {
                scope.activeEnhetsFilters = enhetNames;
            }

            scope.activeEnhetsFiltersAllSelected = isAllAvailableEnhetsSelectedInFilter;
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

        this.populateActiveIntygstypFilter = function(scope, filterHash, intygstyp, isAllAvailableIntygsTypesSelectedInFilter) {
            scope.activeIntygstypFilters = null;
            if (isAllAvailableIntygsTypesSelectedInFilter) {
                return;
            }
            if (intygstyp && intygstyp.length > 0) {
                scope.activeIntygstypFilters = intygstyp;
            }
        };

        this.isShowingVerksamhet = function($location) {
            return this.isShowing($location, 'verksamhet');
        };

        this.isShowingLandsting = function($location) {
            return this.isShowing($location, 'landsting');
        };

        this.isShowingNationell = function($location) {
            return this.isShowing($location, 'nationell');
        };

        this.isShowing = function($location, prefix) {
            return $location.path().indexOf('/' + prefix + '/') === 0;
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
            return filterMessages(messages, 'FILTER');
        };

        this.removeChartMessages= function(messages) {
            return filterMessages(messages, 'CHART');
        };

        function filterMessages(messages, type) {
            return $filter('filter')(messages, function(message) {
                return message && message.type !== type;
            });
        }

        this.formatOverViewTablePDF = function(thousandseparatedFilter, data) {
            var tableData = [];

            _.each(data, function(row) {
                tableData.push([row.color, row.name, thousandseparatedFilter(row.quantity), row.alternation + ' %']);
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
            if (chartType === 'column' || chartType === 'stackedcolumn') {
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
            var stacked = activeHighchartType === 'area' || activeChartType === 'stackedcolumn' || usePercentChart;
            return {activeChartType: activeChartType, usePercentChart: usePercentChart, activeHighchartType: activeHighchartType, stacked: stacked};
        };

        this.getExportFileName = function(statisticsLevel, gender) {
            var reportName = $filter('messageFilter')($route.current.title, $route.current.title, undefined, undefined, undefined, true);
            var genderString = gender ? gender + '_' : '';
            var name = statisticsLevel + '_' + reportName + '_' + genderString + moment().format('YYMMDD_HHmmss');
            return name.replace(/Å/g, 'A').replace(/Ä/g, 'A').replace(/Ö/g, 'O')
                .replace(/å/g, 'a').replace(/ä/g, 'a').replace(/ö/g, 'o')
                .replace(/[^A-Za-z0-9._]/g, '');
        };

        this.combineUrl = function(urlBase, queryString) {
            //Make sure query params are not started twice with a '?'
            var fixedQueryString = urlBase.indexOf('?') > 0 ? queryString.replace('?', '&') : queryString;
            return urlBase + fixedQueryString;
        };

        return this;
});


