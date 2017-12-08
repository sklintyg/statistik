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

/* globals Highcharts */
angular.module('StatisticsApp').controller('doubleAreaChartsCtrl',
    /** @ngInject */
    function ($scope, $rootScope, $routeParams, $window, $timeout, statisticsData, config, messageService,
            $location, chartFactory, _, pdfFactory, ControllerCommons) {
        'use strict';

        var that = this;
        var chart1 = {};
        var chart2 = {};
        $scope.status = {
            isTableOpen: true,
            isChartCollapsed: false
        };

        var defaultChartType = 'area';
        var chartTypeInfo = ControllerCommons.getChartTypeInfo($routeParams, config, defaultChartType);
        $scope.activeChartType = chartTypeInfo.activeChartType;

        var isVerksamhet = ControllerCommons.isShowingVerksamhet($location);

        this.paintChart = function (containerId, yAxisTitle, yAxisTitleXPos, chartCategories, chartSeries, chartSpacingLeft, doneLoadingCallback, yAxisTitleUnit) {

            var chartConfigOptions = {
                categories: chartCategories,
                series: chartSeries,
                type: chartTypeInfo.activeHighchartType,
                doneLoadingCallback: doneLoadingCallback,
                overview: false,
                percentChart: chartTypeInfo.usePercentChart,
                stacked: chartTypeInfo.stacked,
                verticalLabel: false,
                labelMaxLength: null,
                unit: yAxisTitleUnit
            };

            var chartOptions = chartFactory.getHighChartConfigBase(chartConfigOptions);
            chartOptions.chart.renderTo = containerId;
            chartOptions.plotOptions.area.lineWidth = 1;
            chartOptions.plotOptions.area.lineColor = 'grey';
            chartOptions.legend.enabled = false;
            chartOptions.subtitle.text = (chartTypeInfo.usePercentChart ? 'Andel ' : 'Antal ') + yAxisTitle;
            return new Highcharts.Chart(chartOptions);
        };

        function updateChartsYAxisMaxValue() {
            // Reset values to default
            that.chart1.yAxis[0].setExtremes(0, null);
            that.chart2.yAxis[0].setExtremes(0, null);

            var yMax = Math.max(that.chart1.yAxis[0].getExtremes().max, that.chart2.yAxis[0].getExtremes().max);

            // Set both values to same max.
            that.chart1.yAxis[0].setExtremes(0, yMax);
            that.chart2.yAxis[0].setExtremes(0, yMax);
        }

        var updateChart = function (ajaxResult, doneLoadingCallback) {
            destroyChart(that.chart1);
            destroyChart(that.chart2);

            if (angular.isFunction(config.hideChart) && config.hideChart(ajaxResult)) {
                $scope.series = [];
                $scope.doneLoading = true;
                $scope.hideChart = true;
                $scope.resultMessageList = ControllerCommons.removeChartMessages($scope.resultMessageList);

                return;
            }

            var chartCategories = ajaxResult.femaleChart.categories;
            var chartSeriesMale = ajaxResult.maleChart.series;
            var chartSeriesFemale = ajaxResult.femaleChart.series;
            $scope.series = chartSeriesMale;
            $scope.hideChart = false;

            chartFactory.addColor(chartSeriesFemale);
            var yAxisTitleUnit = config.chartYAxisTitleUnit ? config.chartYAxisTitleUnit : 'sjukfall';
            that.chart1 = that.paintChart('chart1', yAxisTitleUnit + ' för kvinnor', 118, chartCategories, chartSeriesFemale, -100, function() {}, yAxisTitleUnit);

            chartFactory.addColor(chartSeriesMale);
            that.chart2 = that.paintChart('chart2', yAxisTitleUnit + ' för män', 97, chartCategories, chartSeriesMale, -80, doneLoadingCallback, yAxisTitleUnit);

            updateChartsYAxisMaxValue();
        };

        $scope.switchChartType = function (chartType) {
            ControllerCommons.rerouteWhenNeededForChartTypeChange($scope.activeChartType, chartType, config.exchangeableViews, $location, $routeParams);
        };

        $scope.showInLegend = function(index) {
            return chartFactory.showInLegend(that.chart1.series, index) && chartFactory.showInLegend(that.chart2.series, index);
        };

        var populatePageWithDataSuccess = function(result) {
            ControllerCommons.populateActiveFilters($scope, statisticsData, result.filter.diagnoser,
                result.allAvailableDxsSelectedInFilter,
                result.filter.filterhash, result.allAvailableEnhetsSelectedInFilter, result.filteredEnhets,
                result.filter.sjukskrivningslangd, result.allAvailableSjukskrivningslangdsSelectedInFilter,
                result.filter.aldersgrupp, result.allAvailableAgeGroupsSelectedInFilter);
            var messages = ControllerCommons.getResultMessageList(result, messageService);
            $scope.resultMessageList = ControllerCommons.removeFilterMessages(messages);
            $rootScope.$broadcast('resultMessagesChanged',  messages);

            $scope.subTitle = angular.isFunction(config.suffixTitle) ? config.suffixTitle($routeParams.kapitelId) : config.title;
            //Period should be on a separate row (INTYG-3288)
            $scope.subTitlePeriod = result.period;
            if (config.showDetailsOptions) {
                $scope.currentPeriod = result.period;
                statisticsData.getDiagnosisKapitelAndAvsnittAndKategori(populateDetailsOptions, function() {
                    $window.alert('Kunde inte ladda data');
                });
            }

            $timeout(function() {
                ControllerCommons.updateDataTable($scope, result.tableData);
                updateChart(result, function() {
                    $scope.doneLoading = true;
                });
                $timeout(function() {
                    $rootScope.$broadcast('pageDataPopulated');
                });
            }, 1);
        };

        var populatePageWithData = function (result) {
            ControllerCommons.checkNationalResultAndEnableExport($scope, result, isVerksamhet, false, populatePageWithDataSuccess);
        };

        function populateDetailsOptions(result) {
            var basePath = isVerksamhet ? '#/verksamhet/diagnosavsnitt' : '#/nationell/diagnosavsnitt';
            ControllerCommons.populateDetailsOptions(result, basePath, $scope, $routeParams, messageService, config);
        }

        $scope.chartContainers = [
            {id: 'chart1', name: 'diagram för kvinnor', gender: 'kvinnor'},
            {id: 'chart2', name: 'diagram för män', gender: 'män'}
        ];

        $scope.toggleSeriesVisibility = function (index) {
            chartFactory.toggleSeriesVisibility(that.chart1.series[index]);
            chartFactory.toggleSeriesVisibility(that.chart2.series[index]);

            updateChartsYAxisMaxValue();
        };

        function refreshVerksamhet() {
            statisticsData[config.dataFetcherVerksamhet](populatePageWithData, function () {
                $scope.dataLoadingError = true;
            }, ControllerCommons.getExtraPathParam($routeParams));
        }


        var isExistingDiagnosisHashValid = $routeParams.diagnosHash !== '-';
        if (isExistingDiagnosisHashValid) {
            $scope.spinnerText = 'Laddar information...';
            $scope.doneLoading = false;
            $scope.dataLoadingError = false;
            if (isVerksamhet) {
                $scope.exportTableUrl = config.exportTableUrlVerksamhet(ControllerCommons.getExtraPathParam($routeParams));
                refreshVerksamhet();
            } else {
                $scope.exportTableUrl = config.exportTableUrl($routeParams.kapitelId);
                statisticsData[config.dataFetcher](populatePageWithData, function () {
                    $scope.dataLoadingError = true;
                }, ControllerCommons.getMostSpecificGroupId($routeParams));
            }
            $scope.exportTableUrl = ControllerCommons.combineUrl($scope.exportTableUrl, $scope.queryString);
        } else {
            $scope.doneLoading = true;
        }

        $scope.routeParams = $routeParams;
        $scope.subTitle = config.title;
        $scope.chartFootnotes = angular.isFunction(config.chartFootnotes) ? config.chartFootnotes(isVerksamhet) : config.chartFootnotes;
        $scope.chartFootnotes = Array.isArray($scope.chartFootnotes) ? $scope.chartFootnotes : [];
        $scope.showDiagnosisSelector = config.showDiagnosisSelector;
        $scope.showDetailsOptions = config.showDetailsOptions;
        $scope.showDetailsOptions2 = config.showDetailsOptions2 && isVerksamhet;
        $scope.showDetailsOptions3 = config.showDetailsOptions3 && isVerksamhet;

        $scope.useSpecialPrintTable = true;

        $scope.exportChart = function (chartName, gender) {
            chartFactory.exportChart(that[chartName], $scope.viewHeader, $scope.subTitle, gender);
        };

        $scope.printPdf = function () {
            pdfFactory.print($scope, [that.chart1, that.chart2]);
        };

        function destroyChart(chartToDestroy) {
            if (chartToDestroy && typeof chartToDestroy.destroy === 'function') {
                chartToDestroy.destroy();
            }
        }

        $scope.$on('$destroy', function() {
            destroyChart(chart1);
            destroyChart(chart2);
        });

        return this;

    }
);

angular.module('StatisticsApp').diagnosisGroupConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';


    var conf = {};
    conf.dataFetcher = 'getDiagnosisGroupData';
    conf.dataFetcherVerksamhet = 'getDiagnosisGroupDataVerksamhet';
    conf.exportTableUrl = function () {
        return 'api/getDiagnoskapitelstatistik?format=xlsx';
    };
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getDiagnoskapitelstatistik?format=xlsx';
    };
    conf.showDetailsOptions = false;
    conf.suffixTitle = function (suffix) {
        return this.title + ' ' + (suffix || '');
    };
    conf.title = messageService.getProperty('title.diagnosisgroup');
    conf.chartFootnotes = ['help.nationell.diagnosisgroup'];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/diagnosgrupp', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/diagnosgrupptvarsnitt', active: false}];
    return conf;
};

angular.module('StatisticsApp').diagnosisSubGroupConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcher = 'getSubDiagnosisGroupData';
    conf.dataFetcherVerksamhet = 'getSubDiagnosisGroupDataVerksamhet';
    conf.exportTableUrl = function (subgroupId) {
        return 'api/getDiagnosavsnittstatistik/' + subgroupId + '?format=xlsx';
    };
    conf.exportTableUrlVerksamhet = function (subgroupId) {
        return 'api/verksamhet/getDiagnosavsnittstatistik/' + subgroupId + '?format=xlsx';
    };
    conf.showDetailsOptions = true;
    conf.showDetailsOptions2 = true;
    conf.showDetailsOptions3 = true;
    conf.suffixTitle = function (suffix) {
        return this.title + ' ' + (suffix || '');
    };
    conf.title = messageService.getProperty('title.diagnosgroup');
    conf.pageHelpTextShowDetailOptions = 'help.diagnosissubgroup.showdetailoptions';
    conf.chartFootnotes = function(isVerksamhet) {
        if (isVerksamhet) {
            return ['help.verksamhet.diagnosgroup'];
        }

        return ['help.nationell.diagnosgroup'];
    };
    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/diagnosavsnitt', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/diagnosavsnitttvarsnitt', active: false}];

    return conf;
};

angular.module('StatisticsApp').degreeOfSickLeaveConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcher = 'getDegreeOfSickLeave';
    conf.dataFetcherVerksamhet = 'getDegreeOfSickLeaveVerksamhet';
    conf.exportTableUrl = function () {
        return 'api/getDegreeOfSickLeaveStatistics?format=xlsx';
    };
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getDegreeOfSickLeaveStatistics?format=xlsx';
    };
    conf.showDetailsOptions = false;
    conf.suffixTitle = function (suffix) {
        return this.title + ' ' + (suffix || '');
    };
    conf.title = messageService.getProperty('title.degreeofsickleave');
    conf.chartFootnotes = ['help.nationell.degreeofsickleave'];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukskrivningsgrad', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukskrivningsgradtvarsnitt', active: false}];

    return conf;
};

angular.module('StatisticsApp').casesPerBusinessTimeSeriesConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSjukfallPerBusinessTimeSeriesVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getNumberOfCasesPerEnhetTimeSeries?format=xlsx';
    };
    conf.suffixTitle = function (suffix) {
        return this.title + ' ' + (suffix || '');
    };
    conf.title = messageService.getProperty('title.vardenhet');
    conf.chartFootnotes = function(isVerksamhet, isLandsting) {
        if (isLandsting) {
            return ['help.landsting.vardenhet'];
        }

        return ['help.verksamhet.vardenhet'];
    };

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukfallperenhettidsserie', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukfallperenhet', active: false}];

    return conf;
};

angular.module('StatisticsApp').compareDiagnosisTimeSeriesConfig =
    /** @ngInject */
    function (messageService, MAX_SELECTED_DXS) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getCompareDiagnosisTimeSeriesVerksamhet';
    conf.exportTableUrlVerksamhet = function (diagnosisHash) {
        return 'api/verksamhet/getJamforDiagnoserStatistikTidsserie/' + diagnosisHash + '?format=xlsx';
    };
    conf.suffixTitle = function (suffix) {
        return this.title + ' ' + (suffix || '');
    };
    conf.title = messageService.getProperty('title.diagnoscompare');
    conf.showDiagnosisSelector = true;
    conf.hideChart = function(data) {
        return data.maleChart.series.length > MAX_SELECTED_DXS;
    };

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/jamforDiagnoserTidsserie', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/jamforDiagnoser', active: false}];
    return conf;
};

angular.module('StatisticsApp').nationalAgeGroupTimeSeriesConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getAgeGroupsTimeSeriesVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getAgeGroupsStatisticsAsTimeSeries?format=xlsx';
    };
    conf.suffixTitle = function (suffix) {
        return this.title + ' ' + (suffix || '');
    };
    conf.title = messageService.getProperty('title.agegroup');
    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/aldersgrupperTidsserie', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/aldersgrupper', active: false}];
    return conf;
};

angular.module('StatisticsApp').sickLeaveLengthTimeSeriesConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSickLeaveLengthTimeSeriesDataVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getSickLeaveLengthTimeSeries?format=xlsx';
    };
    conf.suffixTitle = function (suffix) {
        return this.title + ' ' + (suffix || '');
    };
    conf.title = messageService.getProperty('title.sickleavelength');
    conf.chartFootnotes = function(isVerksamhet) {
        var text = ['help.nationell.sickleavelength'];

        if (isVerksamhet) {
            text.push('help.verksamhet.sickleavelength');
        }
        return text;
    };

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukskrivningslangdTidsserie', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukskrivningslangd', active: false}];
    return conf;
};

angular.module('StatisticsApp').casesPerLakarbefattningTidsserieConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSjukfallPerLakarbefattningTidsserieVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getNumberOfCasesPerLakarbefattningSomTidsserie?format=xlsx';
    };
    conf.suffixTitle = function (suffix) {
        return this.title + ' ' + (suffix || '');
    };
    conf.title = messageService.getProperty('title.lakare-befattning');
    conf.chartFootnotes = ['help.verksamhet.lakare-befattning'];
    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukfallperlakarbefattningtidsserie', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukfallperlakarbefattning', active: false}];
    return conf;
};

angular.module('StatisticsApp').casesPerLakareTimeSeriesConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSjukfallPerLakareSomTidsserieVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getSjukfallPerLakareSomTidsserie?format=xlsx';
    };
    conf.suffixTitle = function (suffix) {
        return this.title + ' ' + (suffix || '');
    };
    conf.title = messageService.getProperty('title.lakare');
    conf.chartFootnotes = ['help.verksamhet.lakare'];
    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukfallperlakaretidsserie', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukfallperlakare', active: false}];
    return conf;
};

angular.module('StatisticsApp').casesPerLakaresAlderOchKonTidsserieConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getSjukfallPerLakaresAlderOchKonTidsserieVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getCasesPerDoctorAgeAndGenderTimeSeriesStatistics?format=xlsx';
    };
    conf.suffixTitle = function (suffix) {
        return this.title + ' ' + (suffix || '');
    };
    conf.title = messageService.getProperty('title.lakaregender');
    conf.chartFootnotes = ['help.verksamhet.lakaregender'];

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukfallperlakaresalderochkontidsserie', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukfallperlakaresalderochkon', active: false}];
    return conf;
};

angular.module('StatisticsApp').intygPerTypePerMonthConfig =
    /** @ngInject */
        function (messageService) {
        'use strict';

        var conf = {};
        conf.dataFetcherVerksamhet = 'getIntygPerTypePerMonthVerksamhet';
        conf.chartYAxisTitleUnit = 'intyg';
        conf.exportTableUrlVerksamhet = function () {
            return 'api/verksamhet/getIntygPerTypePerMonth?format=xlsx';
        };
        conf.suffixTitle = function (suffix) {
            return this.title + ' ' + (suffix || '');
        };
        conf.title = messageService.getProperty('title.intygstyp');

        conf.exchangeableViews = [
            {description: 'Tidsserie', state: '/verksamhet/intygPerTyp', active: true},
            {description: 'Tvärsnitt', state: '/verksamhet/intygPerTypTvarsnitt', active: false}];

        return conf;
    };

