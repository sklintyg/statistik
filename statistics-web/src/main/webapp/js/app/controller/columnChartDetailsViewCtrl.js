 'use strict';

 app.nationalSickLeaveLengthConfig = function() {
     var conf = {};
     conf.dataFetcher = "getNationalSickLeaveLengthData",
     conf.dataFetcherVerksamhet = "getSickLeaveLengthDataVerksamhet",
     conf.title = function(monthsIncluded){return "Antal pågående samt avslutade sjukfall baserat på sjukskrivningslängd senaste " + monthsIncluded + " månaderna";}
     conf.chartXAxisTitle = "Sjukskrivningslängd";
     return conf;
 }

 app.nationalSickLeaveLengthCurrentConfig = function() {
     var conf = {};
     conf.dataFetcherVerksamhet = "getSickLeaveLengthCurrentDataVerksamhet",
     conf.title = function(){return "Antal sjukfall baserat på sjukskrivningslängd";}
     conf.chartXAxisTitle = "Sjukskrivningslängd";
     return conf;
 }
 
 app.nationalAgeGroupConfig = function() {
     var conf = {};
     conf.dataFetcher = "getAgeGroups",
     conf.dataFetcherVerksamhet = "getAgeGroupsVerksamhet",
     conf.title = function(monthsIncluded){return "Antal pågående samt avslutade sjukfall baserat på patientens ålder senaste " + monthsIncluded + " månaderna";}
     conf.chartXAxisTitle = "Åldersgrupp";
     return conf;
 }
 
 app.nationalAgeGroupCurrentConfig = function() {
     var conf = {};
     conf.dataFetcherVerksamhet = "getAgeGroupsCurrentVerksamhet",
     conf.title = function(){return "Antal pågående sjukfall baserat på patientens ålder";}
     conf.chartXAxisTitle = "Åldersgrupp";
     return conf;
 }
 
 app.casesPerSexConfig = function() {
     var conf = {};
     conf.dataFetcher = "getNationalSjukfallPerSexData",
     conf.title = function(){return "Andel sjukfall per kön per län det senaste året";};
     conf.yAxisTitle = "Andel";
     conf.percentChart = true;
     conf.chartXAxisTitle = "Län";
     return conf;
 }

 app.columnChartDetailsViewCtrl = function ($scope, $routeParams, $timeout, $window, statisticsData, config) {
    var isVerksamhet = $routeParams.verksamhetId ? true : false;
    var chart = {};

    $scope.chartContainers = ["chart1"];
    
	var paintChart = function(chartCategories, chartSeries) {
        var chartOptions = ControllerCommons.getHighChartConfigBase(chartCategories, chartSeries);
        chartOptions.chart.type = 'column';
        chartOptions.xAxis.title.text = config.chartXAxisTitle;
        chartOptions.yAxis.title.text = config.percentChart ? "Andel" : 'Antal';
        chartOptions.yAxis.labels.formatter = function() { return ControllerCommons.makeThousandSeparated(this.value) + (config.percentChart ? "%" : "") };
        chartOptions.plotOptions.column.stacking = config.percentChart ? 'percent' : 'normal';
		return new Highcharts.Chart(chartOptions);
	};

	var updateDataTable = function($scope, ajaxResult) {
		$scope.headerrows = ajaxResult.headers;
		$scope.rows = ajaxResult.rows;
	};

	var updateChart = function(ajaxResult) {
		chart = paintChart(ajaxResult.categories, ControllerCommons.setupSeriesForDisplayType($routeParams.printBw, ajaxResult.series, "bar"));
	};

    $scope.exportTableData = ControllerCommons.exportTableDataGeneric;
    
    var populatePageWithData = function(result){
        $scope.subTitle = config.title(result.monthsIncluded);
        $scope.doneLoading = true;
        $timeout(function() {
            updateDataTable($scope, result.tableData);
            updateChart(result.chartData);
            
            if ($routeParams.printBw || $routeParams.print) {
                ControllerCommons.printAndCloseWindow($timeout, $window);
            }
        }, 1);
    };
    
    if (isVerksamhet){
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

    $scope.exportChart = function() {
        chart.exportChart();
    };
    
    $scope.print = function(bwPrint) {
        window.open($window.location + (bwPrint ? "?printBw=true" : "?print=true"));
    }
    
};
