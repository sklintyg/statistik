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

angular.module('StatisticsApp').controller('casesPerCountyCtrl', ['$scope', '$timeout', '$routeParams', '$window', 'statisticsData', 'messageService',
    function ($scope, $timeout, $routeParams, $window, statisticsData, messageService) {

        var chart = {};
        $scope.chartContainers = [
            {id: "chart1", name: "diagram"}
        ];

        var paintChart = function (chartCategories, chartSeries) {
            var chartOptions = ControllerCommons.getHighChartConfigBase(chartCategories, chartSeries);
            chartOptions.chart.type = 'column';
            chartOptions.chart.marginTop = 27;
            chartOptions.chart.marginLeft = 60;
            chartOptions.yAxis.title.x = 30;
            chartOptions.yAxis.title.y = -13;
            chartOptions.yAxis.title.align = 'high';
            chartOptions.yAxis.title.offset = 0;
            chartOptions.legend.enabled = $routeParams.printBw || $routeParams.print;
            chartOptions.xAxis.title.text = "Län";
            return new Highcharts.Chart(chartOptions);
        };

        var updateChart = function (ajaxResult) {
            $scope.series = ControllerCommons.setupSeriesForDisplayType($routeParams.printBw, ajaxResult.series, "bar");
            chart = paintChart(ajaxResult.categories, $scope.series);
        };

        $scope.toggleSeriesVisibility = function (index) {
            var series = chart.series[index];
            if (series.visible) {
                series.hide();
            } else {
                series.show();
            }
        };

        var populatePageWithData = function (result) {
            $scope.subTitle = "Antal sjukfall per län " + result.period;
            $scope.doneLoading = true;
            $timeout(function () {
                ControllerCommons.updateDataTable($scope, result.tableData);
                updateChart(result.chartData);

                if ($routeParams.printBw || $routeParams.print) {
                    ControllerCommons.printAndCloseWindow($timeout, $window);
                }
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
        //$scope.popoverText = messageService.getProperty('info.lan.information', null, "", null, true);
        $scope.chartFootnotes = [messageService.getProperty('info.lan.information')];
        $scope.exportChart = function () {
            ControllerCommons.exportChart(chart, $scope.pageName);
        };

        $scope.print = function (bwPrint) {
            window.open($window.location + (bwPrint ? "?printBw=true" : "?print=true"));
        };

    }
]);
