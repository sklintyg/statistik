 'use strict';

 app.overviewCtrl = function ($scope, $timeout, statisticsData) {

     var dataReceived = function(result) {
         $scope.subTitle = "Utvecklingen i landet " + result.periodText;
         $scope.popoverTextPeriod = result.periodText;
         $scope.doneLoading = true;
         $timeout(function() {
             populatePageWithData(result);
         }, 1);
     };
     
     var populatePageWithData = function(result) {
        $scope.casesPerMonthMaleProportion = result.casesPerMonth.proportionMale;
        $scope.casesPerMonthFemaleProportion = result.casesPerMonth.proportionFemale;
        $scope.casesPerMonthAlteration = result.casesPerMonth.alteration;
        
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
            data: chartData.map(function(e) { return e.quantity; }),
            color: '#12BC3A'
        }];
        var categories = chartData.map(function(e) { return e.name; });
        var chartOptions = ControllerCommons.getHighChartConfigBase(categories, series);
        chartOptions.chart.type = 'column';
        chartOptions.chart.renderTo = containerId;
        chartOptions.chart.height = 185;
        chartOptions.xAxis.title = { text: 'DAGAR' };
        chartOptions.yAxis.title = { text: 'ANTAL' };
        chartOptions.legend.enabled = false;
        new Highcharts.Chart(chartOptions);
    }
    
    function paintSickLeavePerCountyChart(containerId, chartData) {
        var series = chartData.map(function(e) {
            var coords = getCoordinates(e);
            return {"data": [[coords.x, coords.y, e.quantity]], color: e.color, name: ControllerCommons.htmlsafe(e.name) };
        });

        var chartOptions = ControllerCommons.getHighChartConfigBase([], series);
        chartOptions.chart = {
            renderTo : containerId,
            height : 230,
            width: 188,
            type : 'bubble',
            backgroundColor: 'transparent'
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
            chart.renderer.image('img/sweden_graph.png', 43, 10, 88, 206).add();
        });
    }
    
    function getCoordinates(perCountyObject){
        var county = perCountyObject.name.toLowerCase();
        if (contains(county, "stockholm")){
            return {"x": 55, "y": 28};
        } else if (contains(county, "västra götaland")){
            return {"x": 25, "y": 23};
        } else if (contains(county, "skåne")){
            return {"x": 29, "y": 5};
        } else if (contains(county, "östergötland")){
            return {"x": 47, "y": 20};
        } else if (contains(county, "uppsala")){
            return {"x": 57, "y": 34};
        } else {
            return {"x": 10, "y": 80}; //Default point should not match any part of sweden
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
    $scope.popoverTextTitle = "Förklaring";
    $scope.popoverTextAmount = "Totala antalet sjukfall under perioden ";
    $scope.popoverTextChangeProcentage = "Procentsatsen visar förändringen av antalet sjukfall under perioden ";  
    $scope.popoverTextChangeProcentage2 = " jämfört med dess föregående tre månader.";
    $scope.popoverTextSexDistribution = "Könsfördelningen av totala antalet sjukfall under perioden ";
};
