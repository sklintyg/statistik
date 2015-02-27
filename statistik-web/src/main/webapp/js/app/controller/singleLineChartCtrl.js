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

angular.module('StatisticsApp').controller('singleLineChartCtrl', [ '$scope', '$rootScope', '$routeParams', '$timeout', '$window', 'statisticsData', 'businessFilter', 'config', 'printFactory',
    function ($scope, $rootScope, $routeParams, $timeout, $window, statisticsData, businessFilter, config, printFactory) {
        var chart;
        $scope.chartContainers = [
            {id: "chart1", name: "diagram"}
        ];
        var isVerksamhet = $routeParams.verksamhetId ? true : false;

        var paintChart = function (chartCategories, chartSeries) {
            var chartOptions = ControllerCommons.getHighChartConfigBase(chartCategories, chartSeries);
            chartOptions.chart.type = 'line';

            //Set the chart.width to a fixed width when we are about the print.
            //It will prevent the chart from overflowing the printed page.
            //Maybe there is some better way around this since this is not very responsive.
            if($routeParams.printBw || $routeParams.print) {
                chartOptions.chart.width = 768;
            }

            chartOptions.chart.marginLeft = 70;
            chartOptions.chart.marginTop = 27;
            chartOptions.legend.enabled = $routeParams.printBw || $routeParams.print;
            chartOptions.xAxis.title.text = "Period";
            chartOptions.yAxis.title.text = "Antal sjukfall";
            chartOptions.yAxis.title.x = 30;
            chartOptions.yAxis.title.y = -13;
            chartOptions.yAxis.title.align = 'high';
            chartOptions.yAxis.title.offset = 0;
            chartOptions.text = "#008391";
            chartOptions.tooltip.text = "#000";
            return new Highcharts.Chart(chartOptions);
        };

        var setColorToTotalCasesSeries = function (series) {
            for (var i = 0; i < series.length; i++) {
                if (series[i].name === "Antal sjukfall totalt") {
                    series[i].color = "#5d5d5d";
                    break;
                }
            }
        };

        var updateChart = function (ajaxResult) {
            $scope.series = printFactory.setupSeriesForDisplayType($routeParams.printBw, ajaxResult.series, "line");
            setColorToTotalCasesSeries($scope.series);
            chart = paintChart(ajaxResult.categories, $scope.series);
        };

        var populatePageWithData = function (result) {
            ControllerCommons.populateActiveDiagnosFilter($scope, statisticsData, result.filter.diagnoser, $routeParams.printBw || $routeParams.print);
            $scope.subTitle = config.title(result.period, result.filter.enheter ? result.filter.enheter.length : null);
            $timeout(function () {
                ControllerCommons.updateDataTable($scope, result.tableData);
                updateChart(result.chartData);
                $scope.doneLoading = true;

                if ($routeParams.printBw || $routeParams.print) {
                    printFactory.printAndCloseWindow($timeout, $window);
                }
            }, 1);
        };

        $scope.toggleSeriesVisibility = function (index) {
            var series = chart.series[index];
            if (series.visible) {
                series.hide();
            } else {
                series.show();
            }
        };

        $scope.exportChart = function () {
            ControllerCommons.exportChart(chart, $scope.pageName, $scope.subTitle, $scope.activeDiagnosFilters);
        };

        function refreshVerksamhet() {
            statisticsData[config.dataFetcherVerksamhet]($routeParams.verksamhetId, populatePageWithData, function () {
                $scope.dataLoadingError = true;
            });
        }

        if (isVerksamhet) {
            $scope.exportTableUrl = config.exportTableUrlVerksamhet($routeParams.verksamhetId);
            refreshVerksamhet();
        } else {
            $scope.exportTableUrl = config.exportTableUrl;
            statisticsData[config.dataFetcher](populatePageWithData, function () {
                $scope.dataLoadingError = true;
            });
        }

        $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;
        $scope.toggleTableVisibility = function (event) {
            ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
        };

        $scope.spinnerText = "Laddar information...";
        $scope.doneLoading = false;
        $scope.dataLoadingError = false;
        $scope.popoverText = config.showPageHelpTooltip ? "Ett sjukfall innehåller en patients alla läkarintyg om intygen följer varandra med max fem dagars uppehåll. Läkarintygen måste också vara utfärdade av samma vårdgivare. Om det är fler än fem dagar mellan intygen räknas det nya intyget som ett nytt sjukfall." : "";

        $scope.print = function (bwPrint) {
            printFactory.print(bwPrint, $rootScope, $window);
        };

    }
]);

angular.module('StatisticsApp').casesPerMonthConfig = function () {
    var conf = {};
    conf.dataFetcher = "getNumberOfCasesPerMonth";
    conf.dataFetcherVerksamhet = "getNumberOfCasesPerMonthVerksamhet";
    conf.exportTableUrl = "api/getNumberOfCasesPerMonth/csv";
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getNumberOfCasesPerMonth/csv";
    };
    conf.title = function (months, enhetsCount) {
        return "Antal sjukfall per månad" + ControllerCommons.getEnhetCountText(enhetsCount, false) + months;
    };
    conf.showPageHelpTooltip = true;
    return conf;
};

angular.module('StatisticsApp').longSickLeavesConfig = function () {
    var conf = {};
    conf.dataFetcherVerksamhet = "getLongSickLeavesDataVerksamhet";
    conf.exportTableUrlVerksamhet = function (verksamhetId) {
        return "api/verksamhet/" + verksamhetId + "/getLongSickLeavesData/csv";
    };
    conf.title = function (months, enhetsCount) {
        return "Antal långa sjukfall - mer än 90 dagar" + ControllerCommons.getEnhetCountText(enhetsCount, false) + months;
    };
    conf.showPageHelpTooltip = false;
    return conf;
};
