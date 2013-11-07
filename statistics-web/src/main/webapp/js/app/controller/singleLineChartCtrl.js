 'use strict';

 app.casesPerMonthConfig = function() {
     var conf = {};
     conf.dataFetcher = "getNumberOfCasesPerMonth";
     conf.dataFetcherVerksamhet = "getNumberOfCasesPerMonthVerksamhet";
     conf.title = function(months){return "Antal sjukfall per månad " + months;};
     conf.showPageHelpTooltip = true;
     return conf;
 }
 
 app.longSickLeavesConfig = function() {
     var conf = {};
     conf.dataFetcherVerksamhet = "getLongSickLeavesDataVerksamhet";
     conf.title = function(){return "Antal långa sjukfall - mer än 90 dagar";};
     conf.showPageHelpTooltip = false;
     return conf;
 }
 
 app.singleLineChartCtrl = function ($scope, $routeParams, $timeout, statisticsData, config) {
    var chart;
    $scope.chartContainers = ["chart1"];
    
	var paintChart = function(chartCategories, chartSeries) {
        var chartOptions = ControllerCommons.getHighChartConfigBase(chartCategories, chartSeries);
        chartOptions.chart.type = 'line';
        chartOptions.xAxis.title.text = "Period";
        return new Highcharts.Chart(chartOptions);
	};

	var updateDataTable = function($scope, ajaxResult) {
		$scope.headerrows = [[{ "text" : "Period", "colspan" : "1" }].concat(ajaxResult.headers[0])];
		$scope.rows = ajaxResult.rows;
	};

    var updateChart = function(ajaxResult) {
	    $scope.series = ControllerCommons.addColor(ajaxResult.series);
	    chart = paintChart(ajaxResult.categories, $scope.series);
    };

    $scope.exportTableData = ControllerCommons.exportTableDataGeneric;
    
    var populatePageWithData = function(result){
        $scope.subTitle = config.title(result.tableData.rows[0].name + " - " + result.tableData.rows[result.tableData.rows.length-1].name);
        $scope.doneLoading = true;
        $timeout(function() {
            updateDataTable($scope, result.tableData);
            updateChart(result.chartData);
        }, 1);
    };
    
    $scope.toggleSeriesVisibility = function(index) {
        var series = chart.series[index];
        if (series.visible) {
            series.hide();
        } else {
            series.show();
        }
    };
    
    $scope.exportChart = function() {
        chart.exportChart();
    };

    if ($routeParams.verksamhetId){
        statisticsData[config.dataFetcherVerksamhet]($routeParams.verksamhetId, populatePageWithData, function() { $scope.dataLoadingError = true; });
    } else {
        statisticsData[config.dataFetcher](populatePageWithData, function() { $scope.dataLoadingError = true; });
    }
    
    $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;
    $scope.toggleTableVisibility = function(event){
        ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
    };
    
    $scope.spinnerText = "Laddar information...";
    $scope.doneLoading = false;
    $scope.dataLoadingError = false;
    $scope.popoverTextTitle = "Vad innebär sjukfall?";
    $scope.popoverText = config.showPageHelpTooltip ? "Ett sjukfall omfattar alla de läkarintyg (FK&nbsp;7263) som rör en viss patient och vars giltighet följer på varandra med max 5 dagars uppehåll. Exempel: Om intyg 1 gäller till och med 14 augusti och intyg 2 gäller från och med 17 augusti ses de båda intygen som samma sjukfall. Men om intyg 2 istället varit giltigt från och med 21 augusti räknas intygen som två skilda sjukfall." : "";
};