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

/* globals Highcharts */
angular.module('StatisticsApp').controller('businessOverviewCtrl',

    /** @ngInject */
function ($scope, $rootScope, $window, $timeout, statisticsData, $routeParams, chartFactory,
    messageService, pdfOverviewFactory, thousandseparatedFilter, ControllerCommons, _) {
    'use strict';

    var perMonthAlterationChart = {}, newSexProportionChart = {}, oldSexProportionChart = {},
        ageDonutChart = {}, diagnosisDonutChart = {}, degreeOfSickLeaveChart = {}, sickLeaveLengthChart = {};
    $scope.baseUrl = '#/verksamhet';

    var dataReceived = function (result) {
        var popoverPreviousMonths = ' jämfört med föregående tre månader.';
        var popoverTextChangeCurrentVSPrevious = '<br><br>Spalten förändring visar skillnaden i antal sjukfall mellan perioden ' +
            result.periodText + popoverPreviousMonths;

        $scope.subTitlePeriod = result.periodText;
        $scope.popoverTextAmount = 'Totala antalet sjukfall under perioden ' + result.periodText + ',';
        $scope.popoverTextChangeProcentage = 'Procentsatsen visar förändringen av antalet sjukfall under perioden ' + result.periodText;
        $scope.popoverTextSexDistribution = 'Könsfördelningen av totala antalet sjukfall under perioden ' + result.periodText + popoverPreviousMonths;
        $scope.popoverTextDiagnosisGroups = 'Diagrammet visar antal sjukfall inom de vanligast förekommande diagnosgrupperna under ' +
                                                result.periodText + '.' + popoverTextChangeCurrentVSPrevious;
        $scope.popoverTextAgeGroups = 'Diagrammet visar de åldersgrupper som har flest sjukfall under ' + result.periodText + '.' +
            popoverTextChangeCurrentVSPrevious;
        $scope.popoverTextSickLeaveLength = 'Diagrammet visar antal sjukfall per sjukskrivningslängd under perioden ' + result.periodText + '.' +
                '<br><br>Ställ markören i respektive stapel för att se antalet sjukfall.';
        $scope.popoverTextDegreeOfSickLeave = 'Diagrammet visar antalet sjukfall per sjukskrivningsgrad under perioden  ' + result.periodText + '.' +
            popoverTextChangeCurrentVSPrevious;
        $scope.popoverTextPeriod = result.periodText;
        $scope.doneLoading = true;

        var messages = ControllerCommons.getResultMessageList(result, messageService);
        $scope.resultMessageList = ControllerCommons.removeFilterMessages(messages);
        $rootScope.$broadcast('resultMessagesChanged',  messages);

        if (result.empty) {
            $scope.showEmptyDataView = true;
        } else {
            $scope.showEmptyDataView = false;
            $timeout(function () {
                populatePageWithData(result);
            }, 1);
        }
    };

    var paintPerMonthAlternationChart = function (alteration) {
        var chartOptions, color = '#57843B';

        chartOptions = chartFactory.getHighChartConfigBase([], [
            {
                data: [
                    [1]
                ]
            }
        ], null, true);

        chartOptions.chart.renderTo = 'alterationChart';
        chartOptions.chart.type = 'pie';
        chartOptions.chart.height = 210;
        chartOptions.chart.marginTop = 20;
        chartOptions.chart.plotBorderWidth = 0;
        chartOptions.subtitle = null;
        chartOptions.title = {
            verticalAlign: 'middle',
            floating: true,
            align: 'center',
            y: 15,
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
        var femaleColor = '#EA8025', maleColor = '#008391', chartOptions;

        var series = [
            {
                type: 'pie',
                name: 'Könsfördelning',
                showInLegend: true,
                data: [
                    {name: 'Kvinnor', y: female, color: femaleColor},
                    {name: 'Män', y: male, color: maleColor}
                ]
            }
        ];

        chartOptions = chartFactory.getHighChartConfigBase([], series, null, true);
        chartOptions.chart.type = 'pie';
        chartOptions.chart.renderTo = containerId;
        chartOptions.chart.height = 220;
        chartOptions.chart.plotBorderWidth = 0;
        chartOptions.subtitle = null;
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
            labelFormat: '{name} {percentage:.0f} % (antal: {y})',
            verticalAlign: 'top',
            borderWidth: 0,
            useHTML: true,
            layout: 'vertical',
            itemStyle: {
                color: '#008391',
                fontWeight: 'bold'
            }

        };
        chartOptions.tooltip.pointFormat = '{point.percentage:.0f} % (antal: {point.y})';

        return new Highcharts.Chart(chartOptions);
    };

    var paintDonutChart = function(containerId, chartData) {
        var chartOptions = chartFactory.getHighChartConfigBase([], [], null, true);
        chartOptions.chart.type = 'pie';
        chartOptions.chart.renderTo = containerId;
        chartOptions.chart.height = 180;
        chartOptions.chart.plotBorderWidth = 0;
        chartOptions.subtitle = null;
        chartOptions.plotOptions.pie.showInLegend = false;
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
        chartOptions.tooltip.headerFormat = '<span style="font-size: 10px">{point.key}</span><br/>';
        return new Highcharts.Chart(chartOptions);
    };

    var updateCharts = function (result) {

        chartFactory.addColor(result.casesPerMonth.totalCases);
        perMonthAlterationChart = paintPerMonthAlternationChart(result.casesPerMonth.totalCases);

        chartFactory.addColor(result.casesPerMonth.amountMaleOld);
        oldSexProportionChart = paintSexProportionChart('sexProportionChartOld', result.casesPerMonth.amountMaleOld,
                                    result.casesPerMonth.amountFemaleOld, result.casesPerMonth.oldPeriod);

        chartFactory.addColor(result.casesPerMonth.amountFemaleNew);
        newSexProportionChart = paintSexProportionChart('sexProportionChartNew', result.casesPerMonth.amountMaleNew,
                                    result.casesPerMonth.amountFemaleNew, result.casesPerMonth.newPeriod);

        chartFactory.addColor(result.diagnosisGroups);
        var diagnosisDonutData = extractDonutData(result.diagnosisGroups);
        diagnosisDonutChart = paintDonutChart('diagnosisChart', diagnosisDonutData);
        $scope.diagnosisGroups = result.diagnosisGroups;

        chartFactory.addColor(result.ageGroups);
        var ageGroupsDonutData = extractDonutData(result.ageGroups);
        ageDonutChart = paintDonutChart('ageChart', ageGroupsDonutData);
        $scope.ageGroups = result.ageGroups;

        chartFactory.addColor(result.degreeOfSickLeaveGroups);
        var degreeOfSickLeaveDonutData = extractDonutData(result.degreeOfSickLeaveGroups);
        degreeOfSickLeaveChart = paintDonutChart('degreeOfSickLeaveChart', degreeOfSickLeaveDonutData);

        $scope.degreeOfSickLeaveGroups = result.degreeOfSickLeaveGroups;

        chartFactory.addColor(result.sickLeaveLength.chartData);
        sickLeaveLengthChart = paintBarChart('sickLeaveLengthChart', result.sickLeaveLength.chartData);

        $scope.longSickLeavesTotal = result.sickLeaveLength.longSickLeavesTotal;
        $scope.longSickLeavesAlteration = result.sickLeaveLength.longSickLeavesAlternation;
    };

    function populatePageWithData(result) {
        ControllerCommons.populateActiveFilters($scope, statisticsData, result.filter.diagnoser, result.allAvailableDxsSelectedInFilter,
                                result.filter.filterhash, result.allAvailableEnhetsSelectedInFilter, result.filteredEnhets,
                                result.filter.sjukskrivningslangd, result.allAvailableSjukskrivningslangdsSelectedInFilter,
                                result.filter.aldersgrupp, result.allAvailableAgeGroupsSelectedInFilter);
        $timeout(function () {
            updateCharts(result);
        }, 1);
    }

    function paintBarChart(containerId, chartData) {
        var color = '#57843B', chartOptions;

        var series = [
            {
                name: 'Antal',
                data: _.map(chartData, function (e) {
                    return e.quantity;
                }),
                color: color
            }
        ];

        var categories = _.map(chartData, function (e) {
            return {name: e.name};
        });

        chartOptions = chartFactory.getHighChartConfigBase(categories, series, null, true, false, false, false, undefined, 'column');
        chartOptions.chart.type = 'column';
        chartOptions.chart.renderTo = containerId;
        chartOptions.chart.height = 240;
        chartOptions.xAxis.labels.format = '{value}';
        chartOptions.subtitle.text = null;
        chartOptions.yAxis.title = { text: 'Antal', style : chartOptions.subtitle.style  };
        chartOptions.tooltip.headerFormat = '<span style="font-size: 10px">{point.key}</span><br/>';
        chartOptions.yAxis.tickPixelInterval = 30;
        chartOptions.legend.enabled = false;
        return new Highcharts.Chart(chartOptions);
    }

    function extractDonutData(rawData) {
        return _.map(rawData, function(data) {
            return {
                name: ControllerCommons.htmlsafe(data.name),
                y: data.quantity,
                color: data.color
            };
        });
    }

    function refresh() {
        statisticsData.getBusinessOverview(dataReceived, function () {
            $scope.dataLoadingError = true;
        });
    }

    refresh();
    $scope.subTitle = 'Utveckling för verksamheten de senaste tre månaderna, ';
    $scope.spinnerText = 'Laddar information...';
    $scope.doneLoading = false;
    $scope.dataLoadingError = false;

    $scope.printPdf = function () {
        var charts = [];

        charts.push([{
                chart: perMonthAlterationChart,
                title: messageService.getProperty('business.widget.header.total-antal'),
                width: 300,
                height: 300,
                displayWidth: 150
            },
            {
                chart: [oldSexProportionChart, newSexProportionChart],
                title: messageService.getProperty('business.widget.header.konsfordelning-sjukfall'),
                width: 300,
                showLegend: true,
                height: 300,
                displayWidth: 150
            }
        ]);

        charts.push({
            chart: diagnosisDonutChart,
            title: messageService.getProperty('business.widget.header.fordelning-diagnosgrupper'),
            width: 300,
            height: 300,
            displayWidth: 150,
            table: {
                header: ['',
                    messageService.getProperty('overview.widget.table.column.diagnosgrupp'),
                    messageService.getProperty('overview.widget.table.column.antal'),
                    messageService.getProperty('overview.widget.table.column.forandring')
                ],
                data: ControllerCommons.formatOverViewTablePDF(thousandseparatedFilter, $scope.diagnosisGroups)
            }
        });

        charts.push({
            chart: ageDonutChart,
            title: messageService.getProperty('business.widget.header.fordelning-aldersgrupper'),
            width: 300,
            height: 300,
            displayWidth: 150,
            table: {
                header: ['',
                    messageService.getProperty('overview.widget.table.column.aldersgrupp'),
                    messageService.getProperty('overview.widget.table.column.antal'),
                    messageService.getProperty('overview.widget.table.column.forandring')
                ],
                data: ControllerCommons.formatOverViewTablePDF(thousandseparatedFilter, $scope.ageGroups)
            }
        });

        charts.push({
            chart: sickLeaveLengthChart,
            title: messageService.getProperty('business.widget.header.fordelning-sjukskrivningslangd'),
            width: 680,
            height: 300,
            displayWidth: 510,
            chartDescription: [
                {
                    header: thousandseparatedFilter($scope.longSickLeavesTotal),
                    text: messageService.getProperty('overview.widget.fordelning-sjukskrivningslangd.overgar-90')
                },
                {
                    header: $scope.longSickLeavesAlteration + ' %',
                    text: messageService.getProperty('overview.widget.fordelning-sjukskrivningslangd.overgar-90-3-manader')
                }
            ]
        });

        charts.push({
            chart: degreeOfSickLeaveChart,
            title: messageService.getProperty('business.widget.header.fordelning-sjukskrivningsgrad'),
            width: 300,
            height: 300,
            displayWidth: 150,
            table: {
                header: ['',
                    messageService.getProperty('overview.widget.table.column.sjukskrivningsgrad'),
                    messageService.getProperty('overview.widget.table.column.antal'),
                    messageService.getProperty('overview.widget.table.column.forandring')
                ],
                data: ControllerCommons.formatOverViewTablePDF(thousandseparatedFilter, $scope.degreeOfSickLeaveGroups)
            }
        });


        pdfOverviewFactory.printOverview($scope, charts);
    };

    $scope.$on('$destroy', function() {
        if(perMonthAlterationChart && typeof perMonthAlterationChart.destroy === 'function') {
            perMonthAlterationChart.destroy();
        }

        if(newSexProportionChart && typeof newSexProportionChart.destroy === 'function') {
            newSexProportionChart.destroy();
        }

        if(oldSexProportionChart && typeof oldSexProportionChart.destroy === 'function') {
            oldSexProportionChart.destroy();
        }

        if(ageDonutChart && typeof ageDonutChart.destroy === 'function') {
            ageDonutChart.destroy();
        }

        if(diagnosisDonutChart && typeof diagnosisDonutChart.destroy === 'function') {
            diagnosisDonutChart.destroy();
        }

        if(degreeOfSickLeaveChart && typeof degreeOfSickLeaveChart.destroy === 'function') {
            degreeOfSickLeaveChart.destroy();
        }

        if(sickLeaveLengthChart && typeof sickLeaveLengthChart.destroy === 'function') {
            sickLeaveLengthChart.destroy();
        }
    });
});
