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

 app.casesPerMonthConfig = function() {
     var conf = {};
     conf.dataFetcher = "getNumberOfCasesPerMonth";
     conf.dataFetcherVerksamhet = "getNumberOfCasesPerMonthVerksamhet";
     conf.exportTableUrl = "api/getNumberOfCasesPerMonth/csv";
     conf.exportTableUrlVerksamhet = function(verksamhetId) { return "api/verksamhet/" + verksamhetId + "/getNumberOfCasesPerMonth/csv"; };
     conf.title = function(months){return "Antal sjukfall per månad " + months;};
     conf.showPageHelpTooltip = true;
     return conf;
 };
 
 app.longSickLeavesConfig = function() {
     var conf = {};
     conf.dataFetcherVerksamhet = "getLongSickLeavesDataVerksamhet";
     conf.exportTableUrlVerksamhet = function(verksamhetId) { return "api/verksamhet/" + verksamhetId + "/getLongSickLeavesData/csv"; };
     conf.title = function(months){return "Antal långa sjukfall - mer än 90 dagar " + months;};
     conf.showPageHelpTooltip = false;
     return conf;
 };
 
 app.singleLineChartCtrl = function ($scope, $routeParams, $timeout, $window, statisticsData, config) {
    var chart;
    $scope.chartContainers = [{id: "chart1", name: "Diagram"}];
    
	var paintChart = function(chartCategories, chartSeries) {
        var chartOptions = ControllerCommons.getHighChartConfigBase(chartCategories, chartSeries);
        chartOptions.chart.type = 'line';
        chartOptions.chart.marginLeft = 60;
        chartOptions.chart.marginTop = 27;
        chartOptions.legend.enabled = $routeParams.printBw || $routeParams.print;
        chartOptions.xAxis.title.text = "Period";
        chartOptions.yAxis.title.text = "Antal sjukfall";
        chartOptions.yAxis.title.x= 30;
        chartOptions.yAxis.title.y= -13;
        chartOptions.yAxis.title.align = 'high';
        chartOptions.yAxis.title.offset = 0;
        chartOptions.text = "#008391";
        return new Highcharts.Chart(chartOptions);
	};

	var updateDataTable = function($scope, ajaxResult) {
		$scope.headerrows = ajaxResult.headers;
		$scope.rows = ajaxResult.rows;
	};
	
	var setColorToTotalCasesSeries = function(series) {
	    for(var i = 0; i < series.length; i++) {
	        if (series[i].name === "Antal sjukfall totalt") {
	            series[i].color = "#B0B0B0";
	            break;
	        }
	    }
	};

    var updateChart = function(ajaxResult) {
	    $scope.series = ControllerCommons.setupSeriesForDisplayType($routeParams.printBw, ajaxResult.series, "line");
	    setColorToTotalCasesSeries($scope.series);
	    chart = paintChart(ajaxResult.categories, $scope.series);
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
    
    $scope.toggleSeriesVisibility = function(index) {
        var series = chart.series[index];
        if (series.visible) {
            series.hide();
        } else {
            series.show();
        }
    };
    
    $scope.exportChart = function() {
        ControllerCommons.exportChart(chart, $scope.pageName);
    };

    if ($routeParams.verksamhetId){
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
    $scope.popoverText = config.showPageHelpTooltip ? "Ett sjukfall omfattar alla de läkarintyg (FK&nbsp;7263) som rör en viss patient och vars giltighet följer på varandra med max 5 dagars uppehåll. Exempel: Om intyg 1 gäller till och med 14 augusti och intyg 2 gäller från och med 17 augusti ses de båda intygen som samma sjukfall. Men om intyg 2 istället varit giltigt från och med 21 augusti räknas intygen som två skilda sjukfall." : "";

    $scope.print = function(bwPrint) {
        window.open($window.location + (bwPrint ? "?printBw=true" : "?print=true"));
    };
    
};
