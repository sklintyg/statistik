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

angular.module('StatisticsApp').controller('casesPerCountyCtrl', ['$scope', '$rootScope', '$timeout', '$routeParams', '$window', 'statisticsData', 'messageService', 'chartFactory', 'pdfFactory',
    function ($scope, $rootScope, $timeout, $routeParams, $window, statisticsData, messageService, chartFactory, pdfFactory) {

        var chart = {};
        $scope.chartContainers = [
            {id: "chart1", name: "diagram"}
        ];

        var paintChart = function (chartCategories, chartSeries, doneLoadingCallback) {
            var chartOptions = chartFactory.getHighChartConfigBase(chartCategories, chartSeries, doneLoadingCallback);
            chartOptions.chart.type = 'column';
            chartOptions.legend.enabled = false;
            chartOptions.xAxis.title.text = "Län";
            return new Highcharts.Chart(chartOptions);
        };

        var updateChart = function (ajaxResult, doneLoadingCallback) {
            $scope.series = chartFactory.addColor(ajaxResult.series);
            chart = paintChart(ajaxResult.categories, $scope.series, doneLoadingCallback);
        };

        $scope.toggleSeriesVisibility = function (index) {
            chartFactory.toggleSeriesVisibility(chart.series[index]);
        };

        var populatePageWithData = function (result, enhetsIds, diagnosIds) {
            ControllerCommons.populateActiveFilters($scope, statisticsData, diagnosIds, result.allAvailableDxsSelectedInFilter, result.filter.filterhash, result.allAvailableEnhetsSelectedInFilter, result.filteredEnhets);
            $scope.resultMessage = ControllerCommons.getResultMessage(result, messageService);
            var enhetsCount = enhetsIds ? enhetsIds.length : null;
            $scope.subTitle = "Antal sjukfall per län" + ControllerCommons.getEnhetCountText(enhetsCount, false) + result.period;
            $timeout(function () {
                ControllerCommons.updateDataTable($scope, result.tableData);
                updateChart(result.chartData, function() { $scope.doneLoading = true; });
                $timeout(function () {
                    $rootScope.$broadcast('pageDataPopulated');
                });
            }, 1);
        };

        statisticsData.getNationalCountyData(populatePageWithData, function () {
            $scope.dataLoadingError = true;
        });
        $scope.exportTableUrl = "api/getCountyStatistics/csv";

        $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;
        $scope.toggleTableVisibility = function (event) {
            ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
        };

        $scope.spinnerText = "Laddar information...";
        $scope.doneLoading = false;
        $scope.dataLoadingError = false;
        $scope.chartFootnotes = [messageService.getProperty('info.lan.information', null, "", null, true)];
        $scope.exportChart = function () {
            chartFactory.exportChart(chart, $scope.pageName, $scope.subTitle);
        };

        $scope.printPdf = function () {
            pdfFactory.print($scope, chart);
        };

        $scope.$on('$destroy', function() {
            if(chart && typeof chart.destroy === 'function') {
                chart.destroy();
            }
        });

        $scope.showInLegend = function(index) {
            return true;
        };
    }
]);
