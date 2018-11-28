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


/* globals Highcharts */
angular.module('StatisticsApp').controller('overviewCtrl',
    /** @ngInject */
    function ($scope, $rootScope, $window, $timeout, statisticsData, $routeParams, CoordinateService, chartFactory,
        messageService, pdfOverviewFactory, thousandseparatedFilter, ControllerCommons, _, COLORS) {
        'use strict';

        $scope.diagnosisDonutChartOptions = null;
        $scope.ageDonutChartOptions = null;
        $scope.degreeOfSickLeaveChartOptions = null;


        var sickLeaveLengthChart = {};

        var setTooltipText = function (result) {

            var popoverTextChangeCurrentVSPrevious = '<br><br>Spalten förändring visar skillnaden i antal sjukfall mellan perioden ' +
                result.periodText + ' och samma tremånadersperiod föregående år.';

            $scope.popoverText = 'Intygsstatistik är en webbtjänst som visar samlad statistik för sjukskrivning som ordinerats av läkare. ' +
                                    'Tjänsten visar statistik för alla elektroniska läkarintyg. ' +
                                    'Statistiken är uppdelad i nationell statistik som är tillgänglig för alla, ' +
                                    ' och verksamhetsstatistik som bara går att se med särskild behörighet inom hälso- och sjukvården.';
            $scope.popoverTextSexDistribution = 'Andel kvinnor och andel män av det totala antalet sjukfall under perioden ' + result.periodText + '.';
            $scope.popoverTextChangeProcentage = 'Diagrammet visar hur antalet sjukfall förändrats mellan perioden ' + result.periodText +
                                                    ' och samma tremånadersperiod föregående år.';

            $scope.popoverTextDiagnosisGroups = 'Diagrammet visar antal sjukfall inom olika diagnosgrupper under ' +
                                                result.periodText + '.' + popoverTextChangeCurrentVSPrevious;
            $scope.popoverTextAgeGroups = 'Diagrammet visar antal sjukfall inom olika åldersgrupper under månad ' + result.periodText + '.' +
                popoverTextChangeCurrentVSPrevious;
            $scope.popoverTextDegreeOfSickLeave = 'Diagrammet visar antalet sjukfall per sjukskrivningsgrad under perioden  ' +
                                                result.periodText + '.' + popoverTextChangeCurrentVSPrevious;

            $scope.popoverTextSickLeaveLength = 'Diagrammet visar antal sjukfall per sjukskrivningslängd under perioden ' + result.periodText + '.' +
                                                    '<br><br>Ställ markören i respektive stapel för att se antalet sjukfall.';
            $scope.popoverTextPerCountyDescription = 'Kartan visar de län med flest antal sjukfall per 1000 invånare under ' + result.periodText + '.' +
                popoverTextChangeCurrentVSPrevious;
        };

        var dataReceivedSuccess = function(result) {
            $scope.subTitlePeriod = result.periodText;
            setTooltipText(result);
            $scope.statisticNotDone = false;
            $scope.doneLoading = true;
            $timeout(function() {
                populatePageWithData(result);
            }, 1);
        };

        var dataReceived = function (result) {
            ControllerCommons.checkNationalResultAndEnableExport($scope, result, false, false, dataReceivedSuccess);
        };

        function paintPerMonthAlternationChart(alteration) {

            var series = [
                { data: [
                    [ 1 ]
                ] }
            ];

            var chartConfigOptions = {
                categories: [],
                series: series,
                type: 'pie',
                overview: true,
                unit: 'sjukfall'
            };

            var chartOptions = chartFactory.getHighChartConfigBase(chartConfigOptions);
            chartOptions.chart.height = 210;
            chartOptions.chart.width = 180;
            chartOptions.chart.margin = [20, 0, 0, 0];
            chartOptions.chart.spacing = [10, 0, 0, 0];
            chartOptions.chart.plotBorderWidth = 0;
            chartOptions.subtitle = null;
            chartOptions.title = {
                verticalAlign: 'middle',
                align: 'center',
                y: 15,
                floating: true,
                text: (alteration > 0 ? '+' : '') + alteration + ' %',
                style: {
                    fontFamily: 'Helvetica, Arial, sans-serif',
                    color: '#FFFFFF',
                    fontSize: '2em',
                    fontWeight: 'bold',
                    textAlign: 'center'
                }
            };
            chartOptions.tooltip = { enabled: false };
            chartOptions.plotOptions.pie = {
                colors: [ COLORS.overview ],
                animation: false,
                borderWidth: 0,
                dataLabels: { enabled: false },
                states: { hover: {enabled: false} }
            };
            return {
                options: chartOptions
            };
        }

        var paintDonutChart = function (chartData) {
            var series = [
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

            var chartConfigOptions = {
                categories: [],
                series: series,
                type: 'pie',
                overview: true,
                unit: 'sjukfall'
            };

            var chartOptions = chartFactory.getHighChartConfigBase(chartConfigOptions);

            chartOptions.chart.height = 180;
            chartOptions.chart.width = 180;
            chartOptions.subtitle.text = null;
            chartOptions.chart.plotBorderWidth = 0;

            return {
                options: chartOptions
            };
        };

        var updateCharts = function (result) {

            $scope.casesPerMonthMaleProportion = result.casesPerMonth.proportionMale;
            $scope.casesPerMonthFemaleProportion = result.casesPerMonth.proportionFemale;

            chartFactory.addColor(result.casesPerMonth.alteration);
            $scope.alterationChartOptions = paintPerMonthAlternationChart(result.casesPerMonth.alteration);

            chartFactory.addColor(result.diagnosisGroups);
            var diagnosisDonutData = extractDonutData(result.diagnosisGroups);
            $scope.diagnosisDonutChartOptions = paintDonutChart(diagnosisDonutData);
            $scope.diagnosisGroups = result.diagnosisGroups;

            chartFactory.addColor(result.ageGroups);
            var ageGroupsDonutData = extractDonutData(result.ageGroups);
            $scope.ageDonutChartOptions = paintDonutChart(ageGroupsDonutData);
            $scope.ageGroups = result.ageGroups;

            chartFactory.addColor(result.degreeOfSickLeaveGroups);
            var degreeOfSickLeaveDonutData = extractDonutData(result.degreeOfSickLeaveGroups);
            $scope.degreeOfSickLeaveChartOptions = paintDonutChart(degreeOfSickLeaveDonutData);
            $scope.degreeOfSickLeaveGroups = result.degreeOfSickLeaveGroups;

            chartFactory.addColor(result.sickLeaveLength.chartData);
            sickLeaveLengthChart = paintBarChart('sickLeaveLengthChart', result.sickLeaveLength.chartData);

            $scope.longSickLeavesTotal = result.sickLeaveLength.longSickLeavesTotal;
            $scope.longSickLeavesAlteration = result.sickLeaveLength.longSickLeavesAlternation;

            chartFactory.addColor(result.perCounty);
            $scope.sickLeavePerCountyChartOptions = paintSickLeavePerCountyChart(result.perCounty);
            $scope.sickLeavePerCountyGroups = result.perCounty;
        };

        function populatePageWithData(result) {
            $scope.resultMessageList = ControllerCommons.getResultMessageList(result, messageService);
            $timeout(function () {
                updateCharts(result);
            }, 100);
        }

        function paintBarChart(containerId, chartData) {
            var series = [
                {
                    name: 'Antal',
                    data: _.map(chartData, function (e) {
                        return e.quantity;
                    }),
                    color: COLORS.overview
                }
            ];
            var categories = _.map(chartData, function (e) {
                return {name: e.name};
            });

            var chartConfigOptions = {
                categories: categories,
                series: series,
                type: 'column',
                overview: true,
                renderTo: containerId,
                unit: 'sjukfall'
            };

            var chartOptions = chartFactory.getHighChartConfigBase(chartConfigOptions);
            chartOptions.chart.height = 240;
            chartOptions.subtitle.text = null;
            chartOptions.yAxis.title = {text: 'Antal', style : chartOptions.subtitle.style };

            chartOptions.yAxis.tickPixelInterval = 30;
            chartOptions.legend.enabled = false;

            return new Highcharts.Chart(chartOptions);
        }

        function paintSickLeavePerCountyChart(chartData, hideImage) {
            var series = _.map(chartData, function (e) {
                var coords = CoordinateService.getCoordinates(e);

                if (!coords) {
                    return {
                        data: []
                    };
                }

                return {
                    'data': [
                        [coords.x, coords.y, e.quantity]
                    ],
                    color: e.color,
                    name: ControllerCommons.htmlsafe(e.name)
                };
            });

            var chartConfigOptions = {
                categories: [],
                series: series,
                type: 'bubble',
                overview: true,
                unit: 'sjukfall per miljon invånare'
            };

            var chartOptions = chartFactory.getHighChartConfigBase(chartConfigOptions);
            chartOptions.chart.height = 350;
            chartOptions.chart.width = 180;
            chartOptions.chart.plotBorderWidth = 0;
            chartOptions.chart.style = {
                zIndex: 1
            };

            chartOptions.legend.enabled = false;
            chartOptions.plotOptions = {
                bubble: {
                    animation: false,
                    tooltip: {
                        shared: true
                    }
                }
            };

            chartOptions.xAxis = {
                min: 0,
                max: 100,
                minPadding: 0,
                maxPadding: 0,
                minorGridLineWidth: 0,
                labels: {
                    enabled: false
                },
                gridLineWidth: 0,
                visible: false
            };
            chartOptions.subtitle.text = null;
            chartOptions.yAxis = {
                min: 0,
                max: 100,
                minPadding: 0,
                maxPadding: 0,
                minorGridLineWidth: 0,
                labels: {
                    enabled: false
                },
                gridLineWidth: 0,
                title: '',
                visible: false
            };

            return {
                options: chartOptions,
                onComplete: function (chart) { // on complete
                    if (!hideImage) {
                        chart.renderer.image('assets/images/sverige.png', 20, 10, 127, 300).add();
                    }
                }
            };
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

        statisticsData.getOverview(dataReceived, function () {
            $scope.dataLoadingError = true;
        });
        $scope.subTitle = messageService.getProperty('national.overview-header2');
        $scope.spinnerText = 'Laddar information...';
        $scope.doneLoading = false;
        $scope.dataLoadingError = false;
        $scope.chartFootnotes = ['help.nationell.overview'];

        $scope.printPdf = function () {
            var charts = [];

            var topCharts = [];
            var diagnosisDonutChart = $('#diagnosisChart').highcharts();
            var ageDonutChart = $('#ageChart').highcharts();
            var degreeOfSickLeaveChart = $('#degreeOfSickLeaveChart').highcharts();
            var alterationChart = $('#alterationChart').highcharts();
            //var sickLeavePerCountyChart = $('#sickLeavePerCountyChart').highcharts();

            topCharts.push({
                title: messageService.getProperty('national.widget.header.konsfordelning'),
                male: $scope.casesPerMonthMaleProportion + ' %',
                female: $scope.casesPerMonthFemaleProportion + ' %',
                genderImage: true
            });

            topCharts.push({
                chart: alterationChart,
                title: messageService.getProperty('national.widget.header.forandring'),
                width: 300,
                height: 300,
                displayWidth: 150
            });

            charts.push(topCharts);

            charts.push({
                chart: diagnosisDonutChart,
                title: messageService.getProperty('national.widget.header.fordelning-diagnosgrupper'),
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
                title: messageService.getProperty('national.widget.header.fordelning-aldersgrupper'),
                width: 300,
                height: 300,
                displayWidth: 150,
                pageBreak: true,
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
                chart: degreeOfSickLeaveChart,
                title: messageService.getProperty('national.widget.header.fordelning-sjukskrivningsgrad'),
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

            charts.push({
                chart: sickLeaveLengthChart,
                title: messageService.getProperty('national.widget.header.fordelning-sjukskrivningslangd'),
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

            var options = paintSickLeavePerCountyChart($scope.sickLeavePerCountyGroups, true);
            var sickLeavePerCountyChart2 =Highcharts.chart('sickLeavePerCountyChartPrint', options.options);

            charts.push({
                countryChart: sickLeavePerCountyChart2,
                title: messageService.getProperty('national.widget.header.fordelning-lan'),
                height: 350,
                width: 188,
                displayWidth: 188,
                table: {
                    header: ['',
                        messageService.getProperty('overview.widget.table.column.lan'),
                        messageService.getProperty('overview.widget.table.column.antal'),
                        messageService.getProperty('overview.widget.table.column.forandring')
                    ],
                    data: ControllerCommons.formatOverViewTablePDF(thousandseparatedFilter, $scope.sickLeavePerCountyGroups)
                }
            });


            pdfOverviewFactory.printOverview($scope, charts);


            sickLeavePerCountyChart2.destroy();
        };

        $scope.$on('$destroy', function() {
            if(sickLeaveLengthChart && typeof sickLeaveLengthChart.destroy === 'function') {
                sickLeaveLengthChart.destroy();
            }
        });
    }
);
