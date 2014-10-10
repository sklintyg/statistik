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

 app.overviewCtrl = function ($scope, $timeout, statisticsData) {

     var setTooltipText = function(result) {
         $scope.popoverText = "Nationella statistiktjänsten är en webbtjänst som visar samlad statistik för ordinerad sjukskrivning i alla elektroniska läkarintyg. Tjänsten visar nationell statistik som är tillgänglig för alla och verksamhetsstatistik som kräver särskild behörighet för att se.";
         $scope.popoverTextAmount = "Totala antalet sjukfall under perioden " + result.periodText;
         $scope.popoverTextChangeProcentage = "Förändring visar den procentuella förändringen mellan perioden " + result.periodText + " och föregående period " + result.casesPerMonth.previousPeriodText;
         $scope.popoverTextSexDistribution = "Könsfördelningen av totala antalet sjukfall under perioden " + result.periodText;
     };

     var dataReceived = function(result) {
         $scope.subTitle = "Utvecklingen i landet de senaste tre månaderna, " + result.periodText;
         setTooltipText(result);
         $scope.doneLoading = true;
         $timeout(function() {
             populatePageWithData(result);
         }, 1);
     };
     
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
                text : alteration + '%',
                style : {
                	fontFamily: 'Helvetica, Arial, sans-serif',
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
     
     var populatePageWithData = function(result) {
        $scope.casesPerMonthMaleProportion = result.casesPerMonth.proportionMale;
        $scope.casesPerMonthFemaleProportion = result.casesPerMonth.proportionFemale;
        paintPerMonthAlternationChart(result.casesPerMonth.alteration);
        
        function paintDonutChart(containerId, chartData) {
            var series = [{
                name: 'Antal',
                data: chartData,
                innerSize: '40%',
                dataLabels: {
                    formatter: function() {
                        return null;
                    }
                }
            }];
            var chartOptions = ControllerCommons.getHighChartConfigBase([], series);
            chartOptions.chart.type = 'pie';
            chartOptions.chart.renderTo = containerId;
            chartOptions.chart.height = 180;
            chartOptions.chart.plotBorderWidth = 0;
            new Highcharts.Chart(chartOptions);
        }

        paintDonutChart("diagnosisChart", extractDonutData(result.diagnosisGroups));
        $scope.diagnosisGroups = result.diagnosisGroups;
        paintDonutChart("ageChart", extractDonutData(result.ageGroups));
        $scope.ageGroups = result.ageGroups;
        paintDonutChart("degreeOfSickLeaveChart", extractDonutData(result.degreeOfSickLeaveGroups));
        $scope.degreeOfSickLeaveGroups = result.degreeOfSickLeaveGroups;
        
        paintBarChart("sickLeaveLengthChart", result.sickLeaveLength.chartData);
        $scope.longSickLeavesTotal = result.sickLeaveLength.longSickLeavesTotal;
        $scope.longSickLeavesAlteration = result.sickLeaveLength.longSickLeavesAlternation;

        ControllerCommons.addColor(result.perCounty);
        paintSickLeavePerCountyChart("sickLeavePerCountyChart", result.perCounty);
        $scope.sickLeavePerCountyGroups = result.perCounty;
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
        chartOptions.chart.height = 240;
        chartOptions.xAxis.title = { text: 'Sjukskrivningslängd' };
        chartOptions.yAxis.title = { text: 'Antal' };
        chartOptions.yAxis.tickPixelInterval = 30,
        chartOptions.legend.enabled = false;
        new Highcharts.Chart(chartOptions);
    }
    
    function paintSickLeavePerCountyChart(containerId, chartData) {
        var series = ControllerCommons.map(chartData, function(e) {
            var coords = getCoordinates(e);
            return {"data": [[coords.x, coords.y, e.quantity]], color: e.color, name: ControllerCommons.htmlsafe(e.name) };
        });

        var chartOptions = ControllerCommons.getHighChartConfigBase([], series);
        chartOptions.chart = {
            renderTo : containerId,
            height : 320,
            width: 188,
            type : 'bubble',
            backgroundColor: null //Transparent
        };
        chartOptions.legend.enabled = false;

        chartOptions.plotOptions = {
            bubble: {
                tooltip : {
                    headerFormat: '{series.name}<br/>',
                    pointFormat: '<b>{point.z}</b>',
                    shared: true
                }
            }
        };
        chartOptions.xAxis = {
            min: 0,
            max: 100,
            minPadding: 0,
            maxPadding: 0,
            minorGridLineWidth : 0,
            labels : {
                enabled : false
            },
            gridLineWidth : 0
        };
        chartOptions.yAxis = {
            min: 0,
            max: 100,
            minPadding: 0,
            maxPadding: 0,
            minorGridLineWidth : 0,
            labels : {
                enabled : false
            },
            gridLineWidth : 0,
            title : ''
        };
        new Highcharts.Chart(chartOptions, function(chart) { // on complete
            chart.renderer.image('img/sverige.png', 20, 10, 127, 300).add();
        });
    }
    
    function getCoordinates(perCountyObject){
        var county = perCountyObject.name.toLowerCase();
        if (contains(county, "blekinge")){
            return {"x": 34, "y": 6};
        } else if (contains(county, "dalarna")){
            return {"x": 36, "y": 43};
        } else if (contains(county, "halland")){
            return {"x": 27, "y": 16};
        } else if (contains(county, "kalmar")){
            return {"x": 44, "y": 16};
        } else if (contains(county, "kronoberg")){
            return {"x": 34, "y": 13};
        } else if (contains(county, "gotland")){
            return {"x": 55, "y": 19};
        } else if (contains(county, "gävleborg")){
            return {"x": 45, "y": 47};
        } else if (contains(county, "jämtland")){
            return {"x": 36, "y": 60};
        } else if (contains(county, "jönköping")){
            return {"x": 33, "y": 19};
        } else if (contains(county, "norrbotten")){
            return {"x": 55, "y": 89};
        } else if (contains(county, "skåne")){
            return {"x": 30, "y": 7};
        } else if (contains(county, "stockholm")){
            return {"x": 53, "y": 33};
        } else if (contains(county, "södermanland")){
            return {"x": 46, "y": 28};
        } else if (contains(county, "uppsala")){
            return {"x": 50, "y": 38};
        } else if (contains(county, "värmland")){
            return {"x": 29, "y": 35};
        } else if (contains(county, "västerbotten")){
            return {"x": 51, "y": 72};
        } else if (contains(county, "västernorrland")){
            return {"x": 49, "y": 62};
        } else if (contains(county, "västmanland")){
            return {"x": 45, "y": 34};
        } else if (contains(county, "västra götaland")){
            return {"x": 14, "y": 18};
        } else if (contains(county, "örebro")){
            return {"x": 31, "y": 28};
        } else if (contains(county, "östergötland")){
            return {"x": 37, "y": 20};
        } else {
            return {"x": 12, "y": 84}; //Default point should not match any part of sweden
        }
    }
    
	function contains(master, substring) {
		return master.indexOf(substring) != -1;
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
    
    statisticsData.getOverview(dataReceived, function() { $scope.dataLoadingError = true; });
    $scope.spinnerText = "Laddar information...";
    $scope.doneLoading = false;
    $scope.dataLoadingError = false;

};
