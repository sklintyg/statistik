 'use strict';
 
 app.casesPerCountyCtrl = function ($scope, $timeout, $routeParams, $window, statisticsData) {
    var chart = {};
    $scope.chartContainers = ["chart1"];
    
	var paintChart = function(chartCategories, chartSeries) {
	    var chartOptions = ControllerCommons.getHighChartConfigBase(chartCategories, chartSeries);
	    chartOptions.chart.type = 'column';
        chartOptions.xAxis.title.text = "Län";
		return new Highcharts.Chart(chartOptions);
	};

	var updateDataTable = function($scope, ajaxResult) {
		$scope.headerrows = ajaxResult.headers;
		$scope.rows = ajaxResult.rows;
	};

	var updateChart = function(ajaxResult) {
		chart = paintChart(ajaxResult.categories, ControllerCommons.setupSeriesForDisplayType($routeParams.printBw, ajaxResult.series, "bar"));
	};

    var populatePageWithData = function(result){
        $scope.subTitle = "Antal sjukfall per län " + result.period;
        $scope.doneLoading = true;
        $timeout(function() {
            updateDataTable($scope, result.tableData);
            updateChart(result.chartData);
            
            if ($routeParams.printBw || $routeParams.print) {
                ControllerCommons.printAndCloseWindow($timeout, $window);
            }
        }, 1);
    };
    
    statisticsData.getNationalCountyData(populatePageWithData, function() { $scope.dataLoadingError = true; });
    $scope.exportTableUrl = "api/getCountyStatistics/csv";
    
    $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;
    $scope.toggleTableVisibility = function(event){
        ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
    };
    
    $scope.spinnerText = "Laddar information...";
    $scope.doneLoading = false;
    $scope.dataLoadingError = false;
    
    $scope.exportChart = function() {
        chart.exportChart();
    };

    $scope.print = function(bwPrint) {
        window.open($window.location + (bwPrint ? "?printBw=true" : "?print=true"));
    };

};
