 'use strict';

 app.businessOverviewCtrl = function ($scope, $timeout, statisticsData, $routeParams) {

    $scope.baseUrl = "#/verksamhet/" + $routeParams.verksamhetId;  
     
    var dataReceived = function(result) {
    	$scope.subTitle = "Utveckling för verksamheten de senaste tre månaderna, " + result.periodText;
        $scope.popoverTextPeriod = result.periodText;
        $scope.doneLoading = true;
        $timeout(function() {
            populatePageWithData(result);
        }, 1);
    };
     
    var populatePageWithData = function(result) {
        $scope.totalCasesPerMonth = result.casesPerMonth.totalCases;

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
        chartOptions.chart.height = 185;
        chartOptions.xAxis.title = { text: 'Dagar' };
        chartOptions.xAxis.labels.format = '{value} dagar';
        chartOptions.tooltip.pointFormat = '{point.y} dagar';
        chartOptions.yAxis.title = { text: 'Antal' };
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
