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

angular.module('StatisticsApp').controller('businessOverviewCtrl', ['$scope', '$rootScope', '$window', '$timeout', 'statisticsData', 'businessFilter', '$routeParams', 'printFactory',
function ($scope, $rootScope, $window, $timeout, statisticsData, businessFilter, $routeParams, printFactory) {

    var perMonthAlterationChart = {}, newSexProportionChart = {}, oldSexProportionChart = {},
        ageDonutChart = {}, diagnosisDonutChart = {}, degreeOfSickLeaveChart = {}, sickLeaveLengthChart = {};
    $scope.baseUrl = "#/verksamhet";

    var dataReceived = function (result) {
        $scope.subTitle = "Utveckling för verksamheten de senaste tre månaderna, " + result.periodText;
        $scope.popoverTextAmount = "Totala antalet sjukfall under perioden " + result.periodText;
        $scope.popoverTextChangeProcentage = "Procentsatsen visar förändringen av antalet sjukfall under perioden " + result.periodText;
        $scope.popoverTextSexDistribution = "Könsfördelningen av totala antalet sjukfall under perioden " + result.periodText;
        $scope.popoverTextDiagnosisGroups1 = "Diagrammet visar antal sjukfall inom de vanligast förekommande diagnosgrupperna under " + result.periodText + ".";
        $scope.popoverTextChangeCurrentVSPrevious = "Spalten förändring visar skillnaden i antal sjukfall mellan perioden " + result.periodText;
        $scope.popoverTextAgeGroups1 = "Diagrammet visar de åldersgrupper som har flest sjukfall under " + result.periodText + ".";
        $scope.popoverTextSickLeaveLength1 = "Diagrammet visar antal sjukfall per sjukskrivningslängd under perioden " + result.periodText + ".";
        $scope.popoverTextSickLeaveLength2 = "Ställ markören i respektive stapel för att se antalet sjukfall.";
        $scope.popoverTextDegreeOfSickLeave1 = "Diagrammet visar antalet sjukfall per sjukskrivningsgrad under perioden  " + result.periodText + ".";
        $scope.popoverPreviousMonths = " jämfört med föregående tre månader.";
        $scope.popoverTextPeriod = result.periodText;
        $scope.doneLoading = true;
        if (result.casesPerMonth.totalCases === 0) {
            $scope.showEmptyDataView = true;
        } else {
            $scope.showEmptyDataView = false;
            $timeout(function () {
                populatePageWithData(result);
            }, 1);
        }
    };

    var paintPerMonthAlternationChart = function (alteration) {
        var chartOptions, color = "#57843B", patternPath;
        if ($routeParams.printBw) {
            patternPath = window.location.pathname + 'img/print-patterns/1.png';
            //Override the default color with a black and white pattern
            color = {
                pattern: patternPath,
                width: 6,
                height: 6
            };
        }

        chartOptions = ControllerCommons.getHighChartConfigBase([], [
            {
                data: [
                    [1]
                ]
            }
        ]);

        chartOptions.chart.renderTo = "alterationChart";
        chartOptions.chart.type = "pie";
        chartOptions.chart.height = 210;
        chartOptions.chart.marginTop = 20;
        chartOptions.chart.plotBorderWidth = 0;
        chartOptions.title = {
            verticalAlign: 'middle',
            floating: true,
            text: alteration,
            style: {
                fontFamily: 'Helvetica, Arial, sans-serif',
                color: '#FFFFFF',
                fontSize: '2em',
                fontWeight: 'bold',
                textAlign: 'center'
            }
        };
        chartOptions.tooltip = {enabled: false};
        chartOptions.plotOptions.pie = {
            colors: [color],
            animation: false,
            borderWidth: 0,
            dataLabels: {enabled: false},
            states: {hover: {enabled: false}}
        };

        return new Highcharts.Chart(chartOptions);
    };

    var paintSexProportionChart = function(containerId, male, female, period) {
        var femaleColor = "#EA8025", maleColor = "#008391", chartOptions, patternPathFemale, patternPathMale;

        if ($routeParams.printBw) {
            patternPathMale = window.location.pathname + 'img/print-patterns/5.png';
            patternPathFemale = window.location.pathname + 'img/print-patterns/7.png';

            //Override the default color with a black and white pattern
            femaleColor = {
                pattern: patternPathFemale,
                width: 6,
                height: 6
            };

            maleColor = {
                pattern: patternPathMale,
                width: 6,
                height: 6
            };
        }

        var series = [
            {
                type: 'pie',
                name: 'Könsfördelning',
                showInLegend: true,
                useHTML: true,
                data: [
                    {name: 'Kvinnor', y: female, color: femaleColor},
                    {name: 'Män', y: male, color: maleColor}
                ]
            }
        ];

        chartOptions = ControllerCommons.getHighChartConfigBase([], series);
        chartOptions.chart.type = 'pie';
        chartOptions.chart.renderTo = containerId;
        chartOptions.chart.height = 220;
        chartOptions.chart.plotBorderWidth = 0;
        chartOptions.title = {
            text: period,
            verticalAlign: 'bottom',
            x: -3,
            y: 5,
            style: {
                color: '#274b6d',
                fontWeight: 'bold',
                fontSize: '12px'
            }

        };
        chartOptions.legend = {
            labelFormat: '{name} {percentage:.0f}% (antal: {y})',
            align: 'top left',
            verticalAlign: 'top',
            borderWidth: 0,
            useHTML: true,
            layout: 'vertical'
        };
        chartOptions.tooltip.pointFormat = '{point.percentage:.0f}% (antal: {point.y})';

        return new Highcharts.Chart(chartOptions);
    };

    var paintDonutChart = function(containerId, chartData, tooltipHeaderPrefix) {
        var chartOptions = ControllerCommons.getHighChartConfigBase([], []);
        chartOptions.chart.type = 'pie';
        chartOptions.chart.renderTo = containerId;
        chartOptions.chart.height = 180;
        chartOptions.chart.plotBorderWidth = 0;
        chartOptions.plotOptions.pie.showInLegend = $routeParams.printBw || $routeParams.print;
        chartOptions.series = [
            {
                name: 'Antal',
                data: chartData,
                innerSize: '40%',
                dataLabels: {
                    formatter: function () {
                        return null;
                    }
                }
            }
        ];
        chartOptions.tooltip.headerFormat = '<span style="font-size: 10px">' + (tooltipHeaderPrefix || "") + '{point.key}</span><br/>';
        return new Highcharts.Chart(chartOptions);
    };

    var updateCharts = function (result) {

        printFactory.setupSeriesForDisplayType($routeParams.printBw, result.casesPerMonth.totalCases, "pie");
        perMonthAlterationChart = paintPerMonthAlternationChart(result.casesPerMonth.totalCases);

        printFactory.setupSeriesForDisplayType($routeParams.printBw, result.casesPerMonth.amountMaleOld, "pie");
        oldSexProportionChart = paintSexProportionChart("sexProportionChartOld", result.casesPerMonth.amountMaleOld, result.casesPerMonth.amountFemaleOld, result.casesPerMonth.oldPeriod);

        printFactory.setupSeriesForDisplayType($routeParams.printBw, result.casesPerMonth.amountFemaleNew, "pie");
        newSexProportionChart = paintSexProportionChart("sexProportionChartNew", result.casesPerMonth.amountMaleNew, result.casesPerMonth.amountFemaleNew, result.casesPerMonth.newPeriod);

        var diagnosisDonutData = extractDonutData(result.diagnosisGroups);
        printFactory.setupSeriesForDisplayType($routeParams.printBw, diagnosisDonutData, "pie");
        diagnosisDonutChart = paintDonutChart("diagnosisChart", diagnosisDonutData);
        $scope.diagnosisGroups = result.diagnosisGroups;

        var ageGroupsDonutData = extractDonutData(result.ageGroups);
        printFactory.setupSeriesForDisplayType($routeParams.printBw, ageGroupsDonutData, "pie");
        ageDonutChart = paintDonutChart("ageChart", ageGroupsDonutData);
        $scope.ageGroups = result.ageGroups;

        var degreeOfSickLeaveDonutData = extractDonutData(result.degreeOfSickLeaveGroups);
        printFactory.setupSeriesForDisplayType($routeParams.printBw, degreeOfSickLeaveDonutData, "pie");
        degreeOfSickLeaveChart = paintDonutChart("degreeOfSickLeaveChart", degreeOfSickLeaveDonutData);

        $scope.degreeOfSickLeaveGroups = result.degreeOfSickLeaveGroups;

        printFactory.setupSeriesForDisplayType($routeParams.printBw, result.sickLeaveLength.chartData, "bar");
        sickLeaveLengthChart = paintBarChart("sickLeaveLengthChart", result.sickLeaveLength.chartData);

        $scope.longSickLeavesTotal = result.sickLeaveLength.longSickLeavesTotal;
        $scope.longSickLeavesAlteration = result.sickLeaveLength.longSickLeavesAlternation;
    };

    var populatePageWithData = function (result) {
        $timeout(function () {
            updateCharts(result);

            if ($routeParams.printBw || $routeParams.print) {
                printFactory.printAndCloseWindow($timeout, $window);
            }
        }, 1);
    };

    function paintBarChart(containerId, chartData, tooltipHeaderPrefix) {
        var color = '#57843B', chartOptions, patternPath;

        if ($routeParams.printBw) {
            patternPath = window.location.pathname + 'img/print-patterns/8.png';

            //Override the default color with a black and white pattern
            color = {
                pattern: patternPath,
                width: 6,
                height: 6
            };
        }

        var series = [
            {
                name: "Antal",
                data: _.map(chartData, function (e) {
                    return e.quantity;
                }),
                color: color
            }
        ];

        var categories = _.map(chartData, function (e) {
            return e.name;
        });

        chartOptions = ControllerCommons.getHighChartConfigBase(categories, series);
        chartOptions.chart.type = 'column';
        chartOptions.chart.renderTo = containerId;
        chartOptions.chart.height = 240;
        chartOptions.xAxis.title = { text: 'Sjukskrivningslängd' };
        chartOptions.xAxis.labels.format = '{value}';
        chartOptions.yAxis.title = { text: 'Antal' };
        chartOptions.tooltip.headerFormat = '<span style="font-size: 10px">' + (tooltipHeaderPrefix || "") + '{point.key}</span><br/>';
        chartOptions.yAxis.tickPixelInterval = 30,
            chartOptions.legend.enabled = false;
        return new Highcharts.Chart(chartOptions);
    }

    function extractDonutData(rawData) {
        printFactory.addColor(rawData);
        var donutData = [];
        for (var i = 0; i < rawData.length; i++) {
            donutData.push({
                name: ControllerCommons.htmlsafe(rawData[i].name),
                y: rawData[i].quantity,
                color: rawData[i].color
            });
        }
        return donutData;
    }

    function refresh() {
        statisticsData.getBusinessOverview(dataReceived, function () {
            $scope.dataLoadingError = true;
        });
    }

    refresh();
    $scope.spinnerText = "Laddar information...";
    $scope.doneLoading = false;
    $scope.dataLoadingError = false;

    $scope.print = function (bwPrint) {
        printFactory.print(bwPrint, $rootScope, $window);
    };

    $scope.$on('$destroy', function() {
        if(typeof perMonthAlterationChart.destroy === 'function') {
            perMonthAlterationChart.destroy();
        }

        if(typeof newSexProportionChart.destroy === 'function') {
            newSexProportionChart.destroy();
        }

        if(typeof oldSexProportionChart.destroy === 'function') {
            oldSexProportionChart.destroy();
        }

        if(typeof ageDonutChart.destroy === 'function') {
            ageDonutChart.destroy();
        }

        if(typeof diagnosisDonutChart.destroy === 'function') {
            diagnosisDonutChart.destroy();
        }

        if(typeof degreeOfSickLeaveChart.destroy === 'function') {
            degreeOfSickLeaveChart.destroy();
        }

        if(typeof sickLeaveLengthChart.destroy === 'function') {
            sickLeaveLengthChart.destroy();
        }
    });
}]);
