/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
    function ($scope, $rootScope, $window, $timeout, statisticsData, $routeParams, COUNTY_COORDS, chartFactory,
        messageService, pdfOverviewFactory, thousandseparatedFilter, ControllerCommons, _) {
        'use strict';

        var self = this;

        var perMonthAlterationChart = {}, sickLeavePerCountyChart = {},
            ageDonutChart = {}, diagnosisDonutChart = {}, degreeOfSickLeaveChart = {}, sickLeaveLengthChart = {};

        var setTooltipText = function (result) {
            $scope.popoverText = 'Statistiktjänsten är en webbtjänst som visar samlad statistik för sjukskrivning som ordinerats av läkare. ' +
                                    'Tjänsten visar statistik för alla elektroniska läkarintyg. ' +
                                    'Statistiken är uppdelad i nationell statistik som är tillgänglig för alla, ' +
                                    ' och verksamhetsstatistik som bara går att se med särskild behörighet inom hälso- och sjukvården.';
            $scope.popoverTextAmount = 'Totala antalet sjukfall under perioden ' + result.periodText;
            $scope.popoverTextSexDistribution = 'Andel kvinnor och andel män av det totala antalet sjukfall under perioden ' + result.periodText;
            $scope.popoverTextChangeProcentageThisMonth = 'Diagrammet visar hur antalet sjukfall förändrats mellan perioden ' + result.periodText;
            $scope.popoverTextChangeProcentagePreviousMonth = ' och föregående period ' + result.casesPerMonth.previousPeriodText;
            $scope.popoverTextChangeCurrentVSPrevious = 'Spalten förändring visar skillnaden i antal sjukfall mellan perioden ' +
                                                result.periodText + '  och föregående period ' + result.casesPerMonth.previousPeriodText;
            $scope.popoverTextDiagnosisGroups1 = 'Diagrammet visar antal sjukfall inom de vanligast förekommande diagnosgrupperna under ' +
                                                result.periodText + '.';
            $scope.popoverTextAgeGroups1 = 'Diagrammet visar de åldersgrupper som har flest sjukfall under ' + result.periodText + '.';
            $scope.popoverTextDegreeOfSickLeave1 = 'Diagrammet visar antalet sjukfall per sjukskrivningsgrad under perioden  ' +
                                                result.periodText + '.';
            $scope.popoverTextDegreeOfSickLeave2 = 'Flytta markören i cirkeln för att se antalet sjukfall per sjukskrivningsgrad.';
            $scope.popoverTextSickLeaveLength1 = 'Diagrammet visar antal sjukfall per sjukskrivningslängd under perioden ' + result.periodText + '.';
            $scope.popoverTextSickLeaveLength2 = 'Ställ markören i respektive stapel för att se antalet sjukfall.';
            $scope.popoverTextPerCountyDescription1 = 'Kartan visar de län med flest antal sjukfall under ' + result.periodText + '.';

            $scope.diagnosTooltipContent = '<div class="popover-content">' + $scope.popoverTextDiagnosisGroups1 + '</div>' +
                                            '<div class="popover-content">' + $scope.popoverTextChangeCurrentVSPrevious + '</div>';
        };

        var dataReceived = function (result) {
            $scope.subTitle = 'Utvecklingen i Sverige de senaste tre månaderna, ' + result.periodText;
            setTooltipText(result);
            $scope.doneLoading = true;
            $timeout(function () {
                populatePageWithData(result);
            }, 1);
        };

        function paintPerMonthAlternationChart(alteration) {
            var chartOptions = chartFactory.getHighChartConfigBase([], [
                { data: [
                    [ 1 ]
                ] }
            ]);
            chartOptions.chart.renderTo = 'alterationChart';
            chartOptions.chart.type = 'pie';
            chartOptions.chart.height = 210;
            chartOptions.chart.marginTop = 20;
            chartOptions.chart.plotBorderWidth = 0;
            chartOptions.subtitle = null;
            chartOptions.title = {
                verticalAlign: 'middle',
                align: 'center',
                y: 15,
                floating: true,
                text: alteration + ' %',
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
                colors: [ '#57843B' ],
                animation: false,
                borderWidth: 0,
                dataLabels: { enabled: false },
                states: { hover: {enabled: false} }
            };
            return new Highcharts.Chart(chartOptions);
        }

        var paintDonutChart = function (containerId, chartData, tooltipHeaderPrefix, tooltipHeaderSuffix) {
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
            var chartOptions = chartFactory.getHighChartConfigBase([], series);

            chartOptions.chart.type = 'pie';
            chartOptions.chart.renderTo = containerId;
            chartOptions.chart.height = 180;
            chartOptions.subtitle.text = null;
            chartOptions.chart.plotBorderWidth = 0;
            chartOptions.tooltip.headerFormat = '<span style="font-size: 10px">' + (tooltipHeaderPrefix || '') + '{point.key}'  +
                                                        (tooltipHeaderSuffix || '') + '</span><br/>';

            return new Highcharts.Chart(chartOptions);
        };

        var updateCharts = function (result) {

            $scope.casesPerMonthMaleProportion = result.casesPerMonth.proportionMale;
            $scope.casesPerMonthFemaleProportion = result.casesPerMonth.proportionFemale;

            chartFactory.addColor(result.casesPerMonth.alteration);
            perMonthAlterationChart = paintPerMonthAlternationChart(result.casesPerMonth.alteration);

            var diagnosisDonutData = extractDonutData(result.diagnosisGroups);
            chartFactory.addColor(diagnosisDonutData);
            diagnosisDonutChart = paintDonutChart('diagnosisChart', diagnosisDonutData);
            $scope.diagnosisGroups = result.diagnosisGroups;

            var ageGroupsDonutData = extractDonutData(result.ageGroups);
            chartFactory.addColor(ageGroupsDonutData);
            ageDonutChart = paintDonutChart('ageChart', ageGroupsDonutData);
            $scope.ageGroups = result.ageGroups;

            var degreeOfSickLeaveDonutData = extractDonutData(result.degreeOfSickLeaveGroups);
            chartFactory.addColor(degreeOfSickLeaveDonutData);
            degreeOfSickLeaveChart = paintDonutChart('degreeOfSickLeaveChart', degreeOfSickLeaveDonutData, null, ' %');
            $scope.degreeOfSickLeaveGroups = result.degreeOfSickLeaveGroups;

            chartFactory.addColor(result.sickLeaveLength.chartData);
            sickLeaveLengthChart = paintBarChart('sickLeaveLengthChart', result.sickLeaveLength.chartData);

            $scope.longSickLeavesTotal = result.sickLeaveLength.longSickLeavesTotal;
            $scope.longSickLeavesAlteration = result.sickLeaveLength.longSickLeavesAlternation;

            chartFactory.addColor(result.perCounty);
            sickLeavePerCountyChart = paintSickLeavePerCountyChart('sickLeavePerCountyChart', result.perCounty);
            $scope.sickLeavePerCountyGroups = result.perCounty;
        };

        function populatePageWithData(result) {
            $scope.resultMessage = ControllerCommons.getResultMessage(result, messageService);
            $timeout(function () {
                updateCharts(result);
            }, 1);
        }

        function paintBarChart(containerId, chartData) {
            var series = [
                {
                    name: 'Antal',
                    data: _.map(chartData, function (e) {
                        return e.quantity;
                    }),
                    color: '#57843B'
                }
            ];
            var categories = _.map(chartData, function (e) {
                return {name: e.name};
            });
            var chartOptions = chartFactory.getHighChartConfigBase(categories, series);
            chartOptions.chart.type = 'column';
            chartOptions.chart.renderTo = containerId;
            chartOptions.chart.height = 240;
            chartOptions.xAxis.title = { text: 'Sjukskrivningslängd', style : chartOptions.xAxis.title.style };
            chartOptions.subtitle.text = null;
            chartOptions.yAxis.title = {text: 'Antal', style : chartOptions.subtitle.style };
            
            chartOptions.yAxis.tickPixelInterval = 30;
            chartOptions.legend.enabled = false;

            return new Highcharts.Chart(chartOptions);
        }

        function paintSickLeavePerCountyChart(containerId, chartData, hideImage) {
            var series = _.map(chartData, function (e) {
                var coords = self.getCoordinates(e);
                return {'data': [
                    [coords.x, coords.y, e.quantity]
                ], color: e.color, name: ControllerCommons.htmlsafe(e.name) };
            });

            var chartOptions = chartFactory.getHighChartConfigBase([], series);
            chartOptions.chart = {
                renderTo: containerId,
                height: 350,
                width: 188,
                type: 'bubble',
                backgroundColor: null //Transparent
            };

            chartOptions.legend.enabled = false;
            chartOptions.plotOptions = {
                bubble: {
                    tooltip: {
                        headerFormat: '{series.name}<br/>',
                        pointFormat: 'Antal: <b>{point.z}</b>',
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

            return new Highcharts.Chart(chartOptions, function (chart) { // on complete
                if (!hideImage) {
                    chart.renderer.image('assets/images/sverige.png', 20, 10, 127, 300).add();
                }
            });
        }

        self.getCoordinates = function getCoordinates(perCountyObject) {
            var result = _.find(COUNTY_COORDS, function(c) {
                if(contains(perCountyObject.name.toLowerCase(), c.name)) {
                    return c;
                }
            });

            return result ? result.xy : _.find(COUNTY_COORDS, function(c) { return c.name === 'DEFAULT';}).xy;
        };

        function contains(master, substring) {
            return master.indexOf(substring) !== -1;
        }

        function extractDonutData(rawData) {
            chartFactory.addColor(rawData);

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
        $scope.spinnerText = 'Laddar information...';
        $scope.doneLoading = false;
        $scope.dataLoadingError = false;

        $scope.printPdf = function () {
            var charts = [];

            var topCharts = [];


            topCharts.push({
                title: messageService.getProperty('national.widget.header.konsfordelning'),
                male: $scope.casesPerMonthMaleProportion + ' %',
                female: $scope.casesPerMonthFemaleProportion + ' %',
                genderImage: true
            });

            topCharts.push({
                chart: perMonthAlterationChart,
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
                    data: ControllerCommons.formatOverViewTablePDF(thousandseparatedFilter, $scope.degreeOfSickLeaveGroups, ' %')
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

            var sickLeavePerCountyChart2 = paintSickLeavePerCountyChart('sickLeavePerCountyChartPrint', $scope.sickLeavePerCountyGroups, true);

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
            if(perMonthAlterationChart && typeof perMonthAlterationChart.destroy === 'function') {
                perMonthAlterationChart.destroy();
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
            
            if(sickLeavePerCountyChart && typeof sickLeavePerCountyChart.destroy === 'function') {
                sickLeavePerCountyChart.destroy();
            }
        });
    }
);
