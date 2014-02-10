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

 if (!app){
     var app = {};
 }
 app.businessOverviewCtrl = function ($scope, $timeout, statisticsData, $routeParams, $window) {

    $scope.baseUrl = "#/verksamhet/" + $routeParams.verksamhetId;  
     
    var dataReceived = function(result) {
        if (result.casesPerMonth.totalCases === 0) {
            $window.location.href = $window.location.href.replace(/[^\/]*\/?$/, "nodata");
            return;
        }
    	$scope.subTitle = "Utveckling för verksamheten de senaste tre månaderna, " + result.periodText;
        $scope.popoverTextPeriod = result.periodText;
        $scope.doneLoading = true;
        $timeout(function() {
            populatePageWithData(result);
        }, 1);
    };
     
    var populatePageWithData = function(result) {
        
        function paintPerMonthAlternationChart(alteration) {
            var chartOptions = ControllerCommons.getHighChartConfigBase([], [ { data : [ [ 1 ] ] } ]);
            chartOptions.chart.renderTo = "alterationChart";
            chartOptions.chart.type = "pie";
            chartOptions.chart.height = 210;
            chartOptions.chart.marginTop = 20;
            chartOptions.chart.plotBorderWidth = 0;
            chartOptions.title = {
                    verticalAlign : 'middle',
                    floating : true,
                    text : alteration,
                    style : {
                        color: '#FFFFFF',
                        fontSize: '2em',
                        fontWeight: 'bold',
                        textAlign: 'center'
                    }
                };
            chartOptions.tooltip = { enabled: false };
            chartOptions.plotOptions.pie = {
                    colors : [ "#12BC3A" ],
                    animation : false,
                    borderWidth : 0,
                    dataLabels : { enabled : false },
                    states : { hover: {enabled: false} }
                };
            new Highcharts.Chart(chartOptions);
         }

        function paintSexProportionChart(containerId, male, female, period) {
            var series = [{
                type: 'pie',
                name: 'Könsfördelning',
                showInLegend: true,
                useHTML: true,
                data: [
                    {name: 'Kvinnor', y: female, color: "#EA8025"},
                    {name: 'Män', y: male, color: "#008391"}
                ]
            }];
            var chartOptions = ControllerCommons.getHighChartConfigBase([], series);
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
            new Highcharts.Chart(chartOptions);
        }

        function paintDonutChart(containerId, chartData, tooltipHeaderPrefix) {
            var chartOptions = ControllerCommons.getHighChartConfigBase([], []);
            chartOptions.chart.type = 'pie';
            chartOptions.chart.renderTo = containerId;
            chartOptions.chart.height = 180;
            chartOptions.chart.plotBorderWidth = 0;
            chartOptions.series = [{
                name: 'Antal',
                data: chartData,
                innerSize: '40%',
                dataLabels: {
                    formatter: function() {
                        return null;
                    }
                }
            }];
            chartOptions.tooltip.headerFormat = '<span style="font-size: 10px">' + (tooltipHeaderPrefix || "") + '{point.key}</span><br/>';
            new Highcharts.Chart(chartOptions);
        }

        paintPerMonthAlternationChart(result.casesPerMonth.totalCases);
        
        paintSexProportionChart("sexProportionChartOld", result.casesPerMonth.amountMaleOld, result.casesPerMonth.amountFemaleOld, result.casesPerMonth.oldPeriod);
        paintSexProportionChart("sexProportionChartNew", result.casesPerMonth.amountMaleNew, result.casesPerMonth.amountFemaleNew, result.casesPerMonth.newPeriod);
        
        paintDonutChart("diagnosisChart", extractDonutData(result.diagnosisGroups));
        $scope.diagnosisGroups = result.diagnosisGroups;
        paintDonutChart("ageChart", extractDonutData(result.ageGroups));
        $scope.ageGroups = result.ageGroups;
        paintDonutChart("degreeOfSickLeaveChart", extractDonutData(result.degreeOfSickLeaveGroups), "Sjukskrivningsgrad ");
        $scope.degreeOfSickLeaveGroups = result.degreeOfSickLeaveGroups;

        paintBarChart("sickLeaveLengthChart", result.sickLeaveLength.chartData);
        $scope.longSickLeavesTotal = result.sickLeaveLength.longSickLeavesTotal;
        $scope.longSickLeavesAlteration = result.sickLeaveLength.longSickLeavesAlternation;
    };

    function paintBarChart(containerId, chartData) {
        var series = [{
            name: "Antal",
            data: ControllerCommons.map(chartData, function(e) { return e.quantity; }),
            color: '#12BC3A'
        }];
        var categories = ControllerCommons.map(chartData, function(e) { return e.name; });
        var chartOptions = ControllerCommons.getHighChartConfigBase(categories, series);
        chartOptions.chart.type = 'column';
        chartOptions.chart.renderTo = containerId;
        chartOptions.chart.height = 210;
        chartOptions.xAxis.title = { text: 'Dagar' };
        chartOptions.xAxis.labels.format = '{value} dagar';
        chartOptions.tooltip.pointFormat = '{point.y} dagar';
        chartOptions.yAxis.title = { text: 'Antal' };
        chartOptions.yAxis.tickPixelInterval = 30,
        chartOptions.legend.enabled = false;
        new Highcharts.Chart(chartOptions);
    }

    function extractDonutData(rawData){
        ControllerCommons.addColor(rawData);
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

    statisticsData.getBusinessOverview($routeParams.verksamhetId, dataReceived, function() { $scope.dataLoadingError = true; });
    $scope.spinnerText = "Laddar information...";
    $scope.doneLoading = false;
    $scope.dataLoadingError = false;
    $scope.popoverTextAmount = "Totala antalet sjukfall under perioden ";
    $scope.popoverTextChangeProcentage = "Procentsatsen visar förändringen av antalet sjukfall under perioden ";  
    $scope.popoverTextChangeProcentage2 = " jämfört med dess föregående tre månader.";
    $scope.popoverTextSexDistribution = "Könsfördelningen av totala antalet sjukfall under perioden ";
    $scope.popoverTextSexDistribution2 = " jämfört med föregående tre månader.";

};
