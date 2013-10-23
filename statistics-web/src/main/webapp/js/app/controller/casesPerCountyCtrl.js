 'use strict';
 
 app.casesPerCountyCtrl = function ($scope, $timeout, statisticsData) {

    $scope.chartContainers = ["container"];
    
	var paintChart = function(chartCategories, chartSeries) {
	    var chartOptions = ControllerCommons.getHighChartConfigBase(chartCategories, chartSeries);
	    chartOptions.chart.type = 'column';
		new Highcharts.Chart(chartOptions);
	};

	var updateDataTable = function($scope, ajaxResult) {
		$scope.headerrows = ajaxResult.headers;
		$scope.rows = ajaxResult.rows;
	};

	var updateChart = function(ajaxResult) {
		paintChart(ajaxResult.categories, ControllerCommons.addColor(ajaxResult.series));
	};

    $scope.exportTableData = ControllerCommons.exportTableDataGeneric;
    
    var populatePageWithData = function(result){
        $scope.subTitle = "Antal sjukfall per län de senaste " + result.monthsIncluded + " månaderna";
        $scope.doneLoading = true;
        $timeout(function() {
            updateDataTable($scope, result.tableData);
            updateChart(result.chartData);
        }, 1);
    };
    
    statisticsData.getNationalCountyData(populatePageWithData, function() { $scope.dataLoadingError = true; });
    
    $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;
    $scope.toggleTableVisibility = function(event){
        ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
    };
    
    $scope.spinnerText = "Laddar information...";
    $scope.doneLoading = false;
    $scope.dataLoadingError = false;
};
