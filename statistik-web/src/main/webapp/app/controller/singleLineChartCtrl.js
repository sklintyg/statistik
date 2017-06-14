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
angular.module('StatisticsApp').controller('singleLineChartCtrl',
    /** @ngInject */
    function ($scope, $rootScope, $routeParams, $timeout, $window, $filter, statisticsData, config, $location,
        messageService, chartFactory, pdfFactory, _, ControllerCommons) {
        'use strict';

        var chart;

        var defaultChartType = 'line';
        var chartTypeInfo = ControllerCommons.getChartTypeInfo($routeParams, config, defaultChartType);
        $scope.activeChartType = chartTypeInfo.activeChartType;
        $scope.status = {
            isTableOpen: true,
            isChartCollapsed: false
        };

        $scope.chartContainers = [
            {id: 'chart1', name: 'diagram'}
        ];

        var isVerksamhet = ControllerCommons.isShowingVerksamhet($location);
        var isLandsting = ControllerCommons.isShowingLandsting($location);

        var paintChart = function (chartCategories, chartSeries, doneLoadingCallback) {
            var chartOptions = chartFactory.getHighChartConfigBase(chartCategories, chartSeries, doneLoadingCallback, false, chartTypeInfo.usePercentChart, chartTypeInfo.stacked);
            chartOptions.chart.type = chartTypeInfo.activeHighchartType;
            chartOptions.legend.enabled = false;
            chartOptions.subtitle.text = 'Antal sjukfall';
            if (config.chartYAxisTitle) {
                chartOptions.subtitle.text = config.chartYAxisTitle;
            }
            chartOptions.text = '#008391';
            chartOptions.tooltip.text = '#000';
            return new Highcharts.Chart(chartOptions);
        };

        var updateChart = function (ajaxResult, doneLoadingCallback) {
            $scope.series = chartFactory.addColor(ajaxResult.series);
            chartFactory.setColorToTotalCasesSeries($scope.series);
            chart = paintChart(ajaxResult.categories, $scope.series, doneLoadingCallback);
        };

        $scope.switchChartType = function (chartType) {
            ControllerCommons.rerouteWhenNeededForChartTypeChange($scope.activeChartType, chartType, config.exchangeableViews, $location, $routeParams);
        };

        $scope.showInLegend = function(index) {
            return chartFactory.showInLegend(chart.series, index);
        };

        var populatePageWithDataSuccess = function (result) {
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
            if (angular.isFunction(config.chartFootnotesExtra)) {
                var footnotesExtra = config.chartFootnotesExtra(result, isVerksamhet, isLandsting, $filter);
                if (footnotesExtra) {
                    $scope.chartFootnotes.push(footnotesExtra);
                }
            }
            $timeout(function() {
                ControllerCommons.updateDataTable($scope, result.tableData);
                updateChart(result.chartData, function() {
                    $scope.doneLoading = true;
                });
                $timeout(function() {
                    $rootScope.$broadcast('pageDataPopulated');
                });
            }, 1);
        };

        var populatePageWithData = function (result) {
            ControllerCommons.checkNationalResultAndEnableExport($scope, result, isVerksamhet, isLandsting, populatePageWithDataSuccess);
        };

        $scope.toggleSeriesVisibility = function (index) {
            chartFactory.toggleSeriesVisibility(chart.series[index]);
        };


        $scope.exportChart = function () {
            chartFactory.exportChart(chart, $scope.viewHeader, $scope.subTitle);
        };

        function refreshVerksamhet() {
            statisticsData[config.dataFetcherVerksamhet](populatePageWithData, function () {
                $scope.dataLoadingError = true;
            });
        }

        function refreshLandsting() {
            statisticsData[config.dataFetcherLandsting](populatePageWithData, function () {
                $scope.dataLoadingError = true;
            });
        }

        if (isVerksamhet) {
            $scope.exportTableUrl = config.exportTableUrlVerksamhet();
            refreshVerksamhet();
        } else if (isLandsting) {
            $scope.exportTableUrl = config.exportTableUrlLandsting();
            refreshLandsting();
        } else {
            $scope.exportTableUrl = config.exportTableUrl;
            statisticsData[config.dataFetcher](populatePageWithData, function () {
                $scope.dataLoadingError = true;
            });
        }
        $scope.exportTableUrl = ControllerCommons.combineUrl($scope.exportTableUrl, $scope.queryString);

        $scope.subTitle = config.title;
        $scope.chartFootnotes = angular.isFunction(config.chartFootnotes) ? config.chartFootnotes(isVerksamhet) : config.chartFootnotes;
        $scope.chartFootnotes = Array.isArray($scope.chartFootnotes) ? $scope.chartFootnotes : [];
        $scope.spinnerText = 'Laddar information...';
        $scope.doneLoading = false;
        $scope.dataLoadingError = false;

        $scope.printPdf = function () {
            pdfFactory.print($scope, chart);
        };

        $scope.$on('$destroy', function() {
            if(chart && typeof chart.destroy === 'function') {
                chart.destroy();
            }
        });

    }
);

angular.module('StatisticsApp').casesPerMonthConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcher = 'getNumberOfCasesPerMonth';
    conf.dataFetcherVerksamhet = 'getNumberOfCasesPerMonthVerksamhet';
    conf.dataFetcherLandsting = 'getNumberOfCasesPerMonthLandsting';
    conf.exportTableUrl = 'api/getNumberOfCasesPerMonth?format=xlsx';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getNumberOfCasesPerMonth?format=xlsx';
    };
    conf.exportTableUrlLandsting = function () {
        return 'api/landsting/getNumberOfCasesPerMonthLandsting?format=xlsx';
    };
    conf.title = messageService.getProperty('title.sickleave');
    conf.chartFootnotesExtra = function(result, isVerksamhet, isLandsting, $filter) {
        if (isLandsting) {
            return $filter('messageFilter')('help.landsting.sjukfall-totalt', '', '', [result.fileUploadDate], '');
        }
    };

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/sjukfallPerManad', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/sjukfallPerManadTvarsnitt', active: false}];

    return conf;
};

angular.module('StatisticsApp').longSickLeavesConfig =
    /** @ngInject */
    function (messageService) {
    'use strict';

    var conf = {};
    conf.dataFetcherVerksamhet = 'getLongSickLeavesDataVerksamhet';
    conf.exportTableUrlVerksamhet = function () {
        return 'api/verksamhet/getLongSickLeavesData?format=xlsx';
    };
    conf.title = messageService.getProperty('title.sickleavelength90');

    conf.exchangeableViews = [
        {description: 'Tidsserie', state: '/verksamhet/langasjukskrivningar', active: true},
        {description: 'Tvärsnitt', state: '/verksamhet/langasjukskrivningartvarsnitt', active: false}];

    return conf;
};

angular.module('StatisticsApp').meddelandenPerMonthConfig =
    /** @ngInject */
        function (messageService) {
        'use strict';

        var conf = {};
        conf.dataFetcher = 'getNumberOfMeddelandenPerMonth';
        conf.dataFetcherVerksamhet = 'getNumberOfMeddelandenPerMonthVerksamhet';
        conf.exportTableUrl = 'api/getNumberOfMeddelandenPerMonth?format=xlsx';
        conf.chartYAxisTitle = 'Antal meddelanden';
        conf.exportTableUrlVerksamhet = function () {
            return 'api/verksamhet/getNumberOfMeddelandenPerMonth?format=xlsx';
        };
        conf.title = messageService.getProperty('title.meddelanden');

        conf.exchangeableViews = [
            {description: 'Tidsserie', state: '/verksamhet/meddelanden', active: true},
            {description: 'Tvärsnitt', state: '/verksamhet/meddelandenTvarsnitt', active: false}];

        return conf;
    };

angular.module('StatisticsApp').intygPerMonthConfig =
    /** @ngInject */
        function (messageService) {
        'use strict';

        var conf = {};
        conf.dataFetcherVerksamhet = 'getNumberOfIntygPerMonthVerksamhet';
        conf.chartYAxisTitle = 'Antal intyg';
        conf.exportTableUrlVerksamhet = function () {
            return 'api/verksamhet/getNumberOfIntygPerMonth?format=xlsx';
        };
        conf.title = messageService.getProperty('title.intyg');

        conf.exchangeableViews = [
            {description: 'Tidsserie', state: '/verksamhet/intyg', active: true},
            {description: 'Tvärsnitt', state: '/verksamhet/intygTvarsnitt', active: false}];

        return conf;
    };
