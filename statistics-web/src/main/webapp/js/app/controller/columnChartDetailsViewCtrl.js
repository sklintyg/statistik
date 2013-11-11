 'use strict';

 app.nationalSickLeaveLengthConfig = function() {
     var conf = {};
     conf.dataFetcher = "getNationalSickLeaveLengthData",
     conf.dataFetcherVerksamhet = "getSickLeaveLengthDataVerksamhet",
     conf.exportTableUrl = "api/getSickLeaveLengthData/csv";
     conf.exportTableUrlVerksamhet = function(verksamhetId) { return "api/verksamhet/" + verksamhetId + "/getSickLeaveLengthData/csv" };
     conf.title = function(monthsIncluded){return "Antal pågående samt avslutade sjukfall baserat på sjukskrivningslängd senaste " + monthsIncluded + " månaderna";}
     conf.chartXAxisTitle = "Sjukskrivningslängd";
     return conf;
 }

 app.nationalSickLeaveLengthCurrentConfig = function() {
     var conf = {};
     conf.dataFetcherVerksamhet = "getSickLeaveLengthCurrentDataVerksamhet",
     conf.exportTableUrlVerksamhet = function(verksamhetId) { return "api/verksamhet/" + verksamhetId + "/getSickLeaveLengthCurrentData/csv"; };
     conf.title = function(){return "Antal sjukfall baserat på sjukskrivningslängd";}
     conf.chartXAxisTitle = "Sjukskrivningslängd";
     conf.pageHelpText = "Vad innebär pågående sjukfall?<br/>Denna rapport syftar till att visa så aktuell information om sjukfallen möjligt. Alla sjukfall som pågår någon gång under aktuell månad hämtas. Rapporten kan inte ta hänsyn till vilken dag det är i månaden. I slutet på månaden kommer fortfarande sjukfall som avslutats under månadens gång visas som pågående."
     return conf;
 }
 
 app.nationalAgeGroupConfig = function() {
     var conf = {};
     conf.dataFetcher = "getAgeGroups",
     conf.dataFetcherVerksamhet = "getAgeGroupsVerksamhet",
     conf.exportTableUrl = "api/getAgeGroupsStatistics/csv";
     conf.exportTableUrlVerksamhet = function(verksamhetId) { return "api/verksamhet/" + verksamhetId + "/getAgeGroupsStatistics/csv"; };
     conf.title = function(monthsIncluded){return "Antal pågående samt avslutade sjukfall baserat på patientens ålder senaste " + monthsIncluded + " månaderna";}
     conf.chartXAxisTitle = "Åldersgrupp";
     return conf;
 }
 
 app.nationalAgeGroupCurrentConfig = function() {
     var conf = {};
     conf.dataFetcherVerksamhet = "getAgeGroupsCurrentVerksamhet",
     conf.exportTableUrlVerksamhet = function(verksamhetId) { return "api/verksamhet/" + verksamhetId + "/getAgeGroupsCurrentStatistics/csv"; };
     conf.title = function(){return "Antal pågående sjukfall baserat på patientens ålder";}
     conf.chartXAxisTitle = "Åldersgrupp";
     conf.pageHelpText = "Vad innebär pågående sjukfall?<br/>Denna rapport syftar till att visa så aktuell information om sjukfallen möjligt. Alla sjukfall som pågår någon gång under aktuell månad hämtas. Rapporten kan inte ta hänsyn till vilken dag det är i månaden. I slutet på månaden kommer fortfarande sjukfall som avslutats under månadens gång visas som pågående."
     return conf;
 }
 
 app.casesPerSexConfig = function() {
     var conf = {};
     conf.dataFetcher = "getNationalSjukfallPerSexData",
     conf.exportTableUrl = "api/getSjukfallPerSexStatistics/csv";
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
        $scope.exportTableUrl = config.exportTableUrlVerksamhet($routeParams.verksamhetId);
        statisticsData[config.dataFetcherVerksamhet]($routeParams.verksamhetId, populatePageWithData, function() { $scope.dataLoadingError = true; });
    } else {
        $scope.exportTableUrl = config.exportTableUrl;
        statisticsData[config.dataFetcher](populatePageWithData, function() { $scope.dataLoadingError = true; });
    }
    
    $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;
    $scope.toggleTableVisibility = function(event){
        ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
    };
    
    $scope.spinnerText = "Laddar information...";
    $scope.doneLoading = false;
    $scope.dataLoadingError = false;
    
    $scope.popoverText = config.pageHelpText;

    $scope.exportChart = function() {
        chart.exportChart();
    };
    
    $scope.print = function(bwPrint) {
        window.open($window.location + (bwPrint ? "?printBw=true" : "?print=true"));
    }
    
};
