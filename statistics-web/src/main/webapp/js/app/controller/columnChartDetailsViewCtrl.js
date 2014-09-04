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

 app.nationalSickLeaveLengthConfig = function() {
     var conf = {};
     conf.dataFetcher = "getNationalSickLeaveLengthData",
     conf.dataFetcherVerksamhet = "getSickLeaveLengthDataVerksamhet",
     conf.exportTableUrl = "api/getSickLeaveLengthData/csv";
     conf.exportTableUrlVerksamhet = function(verksamhetId) { return "api/verksamhet/" + verksamhetId + "/getSickLeaveLengthData/csv" };
     conf.title = function(period){return "Antal sjukfall baserat på sjukskrivningslängd " + period;}
     conf.chartXAxisTitle = "Sjukskrivningslängd";
     return conf;
 };

 app.nationalSickLeaveLengthCurrentConfig = function() {
     var conf = {};
     conf.dataFetcherVerksamhet = "getSickLeaveLengthCurrentDataVerksamhet",
     conf.exportTableUrlVerksamhet = function(verksamhetId) { return "api/verksamhet/" + verksamhetId + "/getSickLeaveLengthCurrentData/csv"; };
     conf.title = function(month){return "Antal pågående sjukfall för " + month + " baserat på sjukskrivningslängd";}
     conf.chartXAxisTitle = "Sjukskrivningslängd";
     conf.pageHelpText = "Vad innebär pågående sjukfall?<br/>Denna rapport syftar till att visa så aktuell information om sjukfallen möjligt. Alla sjukfall som pågår någon gång under aktuell månad hämtas. Rapporten kan inte ta hänsyn till vilken dag det är i månaden. I slutet på månaden kommer fortfarande sjukfall som avslutats under månadens gång visas som pågående."
     return conf;
 };
 
 app.nationalAgeGroupConfig = function() {
     var conf = {};
     conf.dataFetcher = "getAgeGroups",
     conf.dataFetcherVerksamhet = "getAgeGroupsVerksamhet",
     conf.exportTableUrl = "api/getAgeGroupsStatistics/csv";
     conf.exportTableUrlVerksamhet = function(verksamhetId) { return "api/verksamhet/" + verksamhetId + "/getAgeGroupsStatistics/csv"; };
     conf.title = function(period){return "Antal sjukfall baserat på patientens ålder " + period;}
     conf.chartXAxisTitle = "Åldersgrupp";
     return conf;
 };
 
 app.nationalAgeGroupCurrentConfig = function() {
     var conf = {};
     conf.dataFetcherVerksamhet = "getAgeGroupsCurrentVerksamhet",
     conf.exportTableUrlVerksamhet = function(verksamhetId) { return "api/verksamhet/" + verksamhetId + "/getAgeGroupsCurrentStatistics/csv"; };
     conf.title = function(month){return "Antal pågående sjukfall för " + month + " baserat på patientens ålder";}
     conf.chartXAxisTitle = "Åldersgrupp";
     conf.pageHelpText = "Vad innebär pågående sjukfall?<br/>Denna rapport syftar till att visa så aktuell information om sjukfallen möjligt. Alla sjukfall som pågår någon gång under aktuell månad hämtas. Rapporten kan inte ta hänsyn till vilken dag det är i månaden. I slutet på månaden kommer fortfarande sjukfall som avslutats under månadens gång visas som pågående."
     return conf;
 };
 
 app.casesPerSexConfig = function() {
     var conf = {};
     conf.dataFetcher = "getNationalSjukfallPerSexData",
     conf.exportTableUrl = "api/getSjukfallPerSexStatistics/csv";
     conf.title = function(period){return "Andel sjukfall per kön per län " + period;};
     conf.percentChart = true;
     conf.chartXAxisTitle = "Län";
     return conf;
 };

 app.columnChartDetailsViewCtrl = function ($scope, $routeParams, $timeout, $window, statisticsData, config) {
    var isVerksamhet = $routeParams.verksamhetId ? true : false;
    var chart = {};

    $scope.chartContainers = [{id: "chart1", name: "diagram"}];
    
	var paintChart = function(chartCategories, chartSeries) {
        var chartOptions = ControllerCommons.getHighChartConfigBase(chartCategories, chartSeries);
        chartOptions.chart.type = 'column';
        chartOptions.chart.marginLeft = 60;
        chartOptions.chart.marginTop = 27;
        chartOptions.legend.enabled = $routeParams.printBw || $routeParams.print;
        chartOptions.xAxis.title.text = config.chartXAxisTitle;
        chartOptions.yAxis.title.text = config.percentChart ? "Andel sjukfall i %" : 'Antal sjukfall';
        chartOptions.yAxis.title.x= config.percentChart ? 60 : 30;
        chartOptions.yAxis.title.y= -13;
        chartOptions.yAxis.title.align = 'high';
        chartOptions.yAxis.title.offset = 0;
        chartOptions.yAxis.labels.formatter = function() { 
        	return ControllerCommons.makeThousandSeparated(this.value) + (config.percentChart ? "%" : ""); 
        };
        chartOptions.plotOptions.column.stacking = config.percentChart ? 'percent' : 'normal';
		return new Highcharts.Chart(chartOptions);
	};

	var updateDataTable = function($scope, ajaxResult) {
		$scope.headerrows = ajaxResult.headers;
		$scope.rows = ajaxResult.rows;
	};

	var updateChart = function(ajaxResult) {
	    $scope.series = ControllerCommons.setupSeriesForDisplayType($routeParams.printBw, ajaxResult.series, "bar");
		chart = paintChart(ajaxResult.categories, $scope.series);
	};
	
    $scope.toggleSeriesVisibility = function(index) {
        var series = chart.series[index];
        if (series.visible) {
            series.hide();
        } else {
            series.show();
        }
    };

    var populatePageWithData = function(result){
        $scope.subTitle = config.title(result.period);
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
        statisticsData[config.dataFetcherVerksamhet]($routeParams.verksamhetId, $scope.selectedBusinesses, populatePageWithData, function() { $scope.dataLoadingError = true; });
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
        ControllerCommons.exportChart(chart, $scope.pageName);
    };
    
    $scope.print = function(bwPrint) {
        window.open($window.location + (bwPrint ? "?printBw=true" : "?print=true"));
    };
    
};
