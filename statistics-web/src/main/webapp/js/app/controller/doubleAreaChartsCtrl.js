 'use strict';

 app.diagnosisGroupConfig = function() {
     var conf = {};
     conf.dataFetcher = "getDiagnosisGroupData";
     conf.dataFetcherVerksamhet = "getDiagnosisGroupDataVerksamhet";
     conf.showDetailsOptions = false;
     conf.title = "Antal sjukfall per diagnosgrupp";
     conf.chartFootnotes = ["Notera att samma sjukfall kan visas fler än en gång i grafen då ett sjukfall kan tilldelas flera diagnoser under samma månad", "Endast de sex vanligast förekommande diagnosgrupperna redovisas med namn. Övriga diagnosgrupper redovisas som övrigt."]; 
     return conf;
 }
 
 app.diagnosisSubGroupConfig = function() {
     var conf = {};
     conf.dataFetcher = "getSubDiagnosisGroupData";
     conf.dataFetcherVerksamhet = "getSubDiagnosisGroupDataVerksamhet";
     conf.showDetailsOptions = true;
     conf.title = "Antal sjukfall per enskilt diagnoskapitel";
     conf.chartFootnotes = ["Notera att samma sjukfall kan visas fler än en gång i grafen då ett sjukfall kan tilldelas flera diagnoser under samma månad", "Endast de sex vanligast förekommande undergrupper redovisas med namn. Övriga underdiagnoser redovisas som övrigt."]; 
     return conf;
 }
 
 app.degreeOfSickLeaveConfig = function() {
     var conf = {};
     conf.dataFetcher = "getDegreeOfSickLeave";
     conf.dataFetcherVerksamhet = "getDegreeOfSickLeaveVerksamhet";
     conf.showDetailsOptions = false;
     conf.title = "Antal sjukfall per sjukskrivningsgrad";
     conf.tooltipHelpTextTitle ="Vad innebär sjukskrivningsgrad?";
     conf.tooltipHelpText ="Begreppet sjukskrivningsgrad beskriver hur många procent av en heltidsarbetstid (25 %, 50 %, 75 % eller 100 %) patienten rekommenderas sjukskrivning.";	 
     conf.chartFootnotes = ["Notera att samma sjukfall kan visas fler än en gång i grafen då ett sjukfall kan ha olika sjukskrivningsgrad under samma månad."]; 
     return conf;
 }
 
 app.doubleAreaChartsCtrl = function ($scope, $routeParams, $window, $timeout, statisticsData, config) {
     var chart1, chart2;
     var that = this;
     var isVerksamhet = $routeParams.verksamhetId ? true : false;

     this.paintChart = function(containerId, yAxisTitle, chartCategories, chartSeries) {
         var chartOptions = ControllerCommons.getHighChartConfigBase(chartCategories, chartSeries);
         chartOptions.chart.type = 'area';
         chartOptions.chart.renderTo = containerId;
         chartOptions.legend.enabled = false;
         chartOptions.xAxis.title.text = "Period";
         chartOptions.yAxis.title.text = yAxisTitle;
         chartOptions.tooltip.useHTML = true;
         chartOptions.yAxis.title.rotation = 270;
         chartOptions.yAxis.title.x= 0;
         chartOptions.yAxis.title.y= 0;
         return new Highcharts.Chart(chartOptions);
     };

    var updateDataTable = function($scope, ajaxResult) {
        $scope.headerrows = ajaxResult.tableData.headers;
        $scope.rows = ajaxResult.tableData.rows;
    };

    var updateChart = function(ajaxResult) {
        var chartCategories = ajaxResult.femaleChart.categories;

        var chartSeriesFemale = ajaxResult.femaleChart.series;
        ControllerCommons.addColor(chartSeriesFemale);
        chart1 = that.paintChart('container1', 'Antal kvinnor', chartCategories, chartSeriesFemale);
        
        var chartSeriesMale = ajaxResult.maleChart.series;
        ControllerCommons.addColor(chartSeriesMale);
        chart2 = that.paintChart('container2', 'Antal män', chartCategories, chartSeriesMale);
        
        var yMax = Math.max(chart1.yAxis[0].dataMax, chart2.yAxis[0].dataMax);
        chart1.yAxis[0].setExtremes(0,yMax);
        chart2.yAxis[0].setExtremes(0,yMax);
        
        $scope.series = chartSeriesMale;
    };

    var populatePageWithData = function(result){
        $scope.doneLoading = true;
        $timeout(function() {
            updateDataTable($scope, result);
            updateChart(result);
        }, 1);
    };
    
    var populateDetailsOptions = function(result){
        var basePath = isVerksamhet ? "#/verksamhet/" + $routeParams.verksamhetId + "/underdiagnosgrupper" : "#/nationell/underdiagnosgrupper"
        
        for ( var i = 0; i < result.length; i++) {
            if (result[i].id == $routeParams.groupId){
                $scope.selectedDetailsOption = result[i];
                break;
            }
        }
        
        $scope.detailsOptions = result.map(function(e){
            e.url = basePath + "/" + e.id;
            return e;
        });
    };

    $scope.chartFootnotes = config.chartFootnotes;
    
    $scope.chartContainers = [ "container1", "container2" ];

    $scope.toggleSeriesVisibility = function(index) {
        var s1 = chart1.series[index];
        var s2 = chart2.series[index];
        if (s1.visible) {
            s1.hide();
            s2.hide();
        } else {
            s1.show();
            s2.show();
        }
    };

    $scope.exportTableData = ControllerCommons.exportTableDataGeneric;

    $scope.subTitle = config.title;
    $scope.popoverTextTitle = config.tooltipHelpTextTitle;
    $scope.popoverText = config.tooltipHelpText;

    if (isVerksamhet){
        statisticsData[config.dataFetcherVerksamhet]($routeParams.verksamhetId, populatePageWithData, function() { $scope.dataLoadingError = true; }, $routeParams.groupId);
    } else {
        statisticsData[config.dataFetcher](populatePageWithData, function() { $scope.dataLoadingError = true; }, $routeParams.groupId);
    }

    $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;
    $scope.toggleTableVisibility = function(event) {
        ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
    };

    $scope.showDetailsOptions = config.showDetailsOptions;
    if (config.showDetailsOptions) {
        statisticsData.getDiagnosisGroups(populateDetailsOptions, function() { alert("Kunde inte ladda data"); });
    }

    $scope.spinnerText = "Laddar information...";
    $scope.doneLoading = false;
    $scope.dataLoadingError = false;

     return this;

};

