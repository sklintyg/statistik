 'use strict';

 app.diagnosisGroupConfig = function() {
     var conf = {};
     conf.dataFetcher = "getDiagnosisGroupData";
     conf.dataFetcherVerksamhet = "getDiagnosisGroupDataVerksamhet";
     conf.exportTableUrl = function() { return "api/getDiagnosisGroupStatistics/csv"; };
     conf.exportTableUrlVerksamhet = function(verksamhetId) { return "api/verksamhet/" + verksamhetId + "/getDiagnosisGroupStatistics/csv"; };
     conf.showDetailsOptions = false;
     conf.title = function(period) { return "Antal sjukfall per diagnosgrupp " + period; };
     conf.chartFootnotes = ["För en given månad kan samma sjukfall visas fler än en gång i graf och tabell. Om ett sjukfall innehåller flera intyg under samma månad så hämtas diagnos från varje intyg. Om intygen har olika diagnosgrupper kommer sjukfallet finnas med en gång för varje diagnosgrupp för respektive månad. Exempel: Om ett sjukfall innehåller två intyg för maj månad, där intyg ett sätter diagnosen M54 och intyg två efter vidare utredning sätter diagnosen F32, så kommer sjukfallet både räknas med i gruppen för Muskuloskeleta sjukdomar (M00-M99) och i gruppen för Psykiska sjukdomar (F00-F99) i graf och tabell för maj månad."]; 
     return conf;
 };
 
 app.diagnosisSubGroupConfig = function() {
     var conf = {};
     conf.dataFetcher = "getSubDiagnosisGroupData";
     conf.dataFetcherVerksamhet = "getSubDiagnosisGroupDataVerksamhet";
     conf.exportTableUrl = function(subgroupId) { return "api/getDiagnosisSubGroupStatistics/" + subgroupId + "/csv"; };
     conf.exportTableUrlVerksamhet = function(verksamhetId, subgroupId) { return "api/verksamhet/" + verksamhetId + "/getDiagnosisSubGroupStatistics/" + subgroupId + "/csv"; };
     conf.showDetailsOptions = true;
     conf.title = function(period, name) { return "Antal sjukfall för " + name + " " + period; };
     conf.chartFootnotes = ["För en given månad kan samma sjukfall visas fler än en gång i graf och tabell. Om ett sjukfall innehåller flera intyg under samma månad så hämtas diagnos från varje intyg. Om intygen har olika diagnoser som faller inom samma diagnoskapitel, men olika diagnosavsnitt, kommer sjukfallet att finnas med en gång för varje diagnosavsnitt för respektive månad. Exempel: Om ett sjukfall innehåller två intyg för maj månad, där intyg ett sätter diagnosen F43 och intyg två efter vidare utredning sätter diagnosen F32, kommer sjukfallet både räknas med i avsnittet för Neurotiska syndrom (F40-F48) och i avsnittet för Förstämningssyndrom (F30-F39) i graf och tabell för maj månad."]; 
     return conf;
 };
 
 app.degreeOfSickLeaveConfig = function() {
     var conf = {};
     conf.dataFetcher = "getDegreeOfSickLeave";
     conf.dataFetcherVerksamhet = "getDegreeOfSickLeaveVerksamhet";
     conf.exportTableUrl = function() { return "api/getDegreeOfSickLeaveStatistics/csv"; };
     conf.exportTableUrlVerksamhet = function(verksamhetId) { return "api/verksamhet/" + verksamhetId + "/getDegreeOfSickLeaveStatistics/csv"; };
     conf.showDetailsOptions = false;
     conf.title = function(period) { return "Antal sjukfall per sjukskrivningsgrad " + period; };
     conf.tooltipHelpText ="Begreppet sjukskrivningsgrad beskriver hur många procent av en heltidsarbetstid (25 %, 50 %, 75 % eller 100 %) patienten rekommenderas sjukskrivning.";	 
     conf.chartFootnotes = ["För en given månad kan samma sjukfall visas fler än en gång i graf och tabell. Alla sjukskrivningsgrader hämtas från varje intyg. Om intyget innehåller flera sjukskrivningsgrader kommer sjukfallet att finnas med en gång för varje sjukskrivningsgrad för respektive månad. Exempel: Om ett intyg för maj månad först innehåller sjukskrivning med 50 % sjukskrivningsgrad och sedan övergår till 100% kommer sjukfallet visas både för 50% och 100% i graf och tabell för maj månad."]; 
     return conf;
 };
 
 app.doubleAreaChartsCtrl = function ($scope, $routeParams, $window, $timeout, statisticsData, config) {
     var that = this;
     var chart1 = {};
     var chart2 = {};
     var isVerksamhet = $routeParams.verksamhetId ? true : false;

     this.paintChart = function(containerId, yAxisTitle, yAxisTitleXPos, chartCategories, chartSeries) {
         var chartOptions = ControllerCommons.getHighChartConfigBase(chartCategories, chartSeries);
         chartOptions.chart.type = 'area';
         chartOptions.chart.marginTop = 27;
         chartOptions.chart.spacingLeft = -20;
         chartOptions.plotOptions.area.lineWidth = 1;
         chartOptions.plotOptions.area.lineColor = 'grey';
         chartOptions.chart.renderTo = containerId;
         chartOptions.legend.enabled = $routeParams.printBw || $routeParams.print;
         chartOptions.xAxis.title.text = "Period";
         chartOptions.tooltip.useHTML = true;
         chartOptions.yAxis.title.text = yAxisTitle;
         /*chartOptions.yAxis.title.rotation = 270;*/
         chartOptions.yAxis.title.x= yAxisTitleXPos;
         chartOptions.yAxis.title.y= -13;
         chartOptions.yAxis.title.align = 'high';
         chartOptions.yAxis.title.offset = 0;
         return new Highcharts.Chart(chartOptions);
     };

    var updateDataTable = function($scope, ajaxResult) {
        $scope.headerrows = ajaxResult.tableData.headers;
        $scope.rows = ajaxResult.tableData.rows;
    };

    //Expects the table to consist of two headers where the first header has a colspan of two
    var updatePrintDataTable = function($scope, ajaxResult) {
        var headers = ajaxResult.tableData.headers;
        var rows = ajaxResult.tableData.rows;
        var printTables = [];
        var totWidth = 0;
        var maxWidth = 500;
        var colWidth = 50;
        var currentDataColumn = 1;
        
        for (var c = 1; c < headers[0].length - 2;) {
            var printTable = {};
            printTable.headers = [];
            printTable.rows = [];

            var totWidth = colWidth * 2; //total width of table frame (first and last column which should be used on all print tables) 
            
            //Add headers for first column
            for (var i = 0; i < headers.length; i++) {
                printTable.headers[i] = [];
                printTable.headers[i].push(headers[i][0]);
            }
            
            //Add all row names (first column)
            for (var r = 0; r < rows.length; r++) {
                var row = {};
                row.name = rows[r].name;
                row.data = [];
                printTable.rows.push(row);
            }
            
            //Add columns until table is too wide or all columns has been added
            for (var i = 0; (c < headers[0].length - 2) && ((totWidth + colWidth) < maxWidth); i++) {
                printTable.headers[0].push(headers[0][c]);
                for (var s = 0; s < 2; s++){
                    printTable.headers[1].push(headers[1][currentDataColumn]);
                    for (var r = 0; r < rows.length; r++) {
                        printTable.rows[r].data.push(rows[r].data[currentDataColumn]);
                    }
                    currentDataColumn++;
                }
                c++;
                totWidth += colWidth;
            }

            //Add headers for last column (sum)
            for (var i = 0; i < headers.length; i++) {
                printTable.headers[i].push(headers[i][headers[i].length - 1]);
            }

            //Add all row sum (last column)
            for (var r = 0; r < rows.length; r++) {
                printTable.rows[r].data.push(rows[r].data[rows[r].data.length - 1]);
            }

            printTables.push(printTable);
        }
        $scope.printDataTables = printTables;
    };
    
    var updateChart = function(ajaxResult) {
        var chartCategories = ajaxResult.femaleChart.categories;

        var chartSeriesFemale = ajaxResult.femaleChart.series;
        ControllerCommons.setupSeriesForDisplayType($routeParams.printBw, chartSeriesFemale, "area");
        that.chart1 = that.paintChart('chart1', 'Antal kvinnor', 40, chartCategories, chartSeriesFemale);
        
        var chartSeriesMale = ajaxResult.maleChart.series;
        ControllerCommons.setupSeriesForDisplayType($routeParams.printBw, chartSeriesMale, "area");
        that.chart2 = that.paintChart('chart2', 'Antal män', 21, chartCategories, chartSeriesMale);
        
        var yMax = Math.max(that.chart1.yAxis[0].dataMax, that.chart2.yAxis[0].dataMax);
        that.chart1.yAxis[0].setExtremes(0,yMax);
        that.chart2.yAxis[0].setExtremes(0,yMax);
        
        $scope.series = chartSeriesMale;
    };

    var populatePageWithData = function(result){
        $scope.doneLoading = true;
        $scope.subTitle = config.title(result.period, $routeParams.groupId);
        if (config.showDetailsOptions) {
            $scope.currentPeriod = result.period;
            statisticsData.getDiagnosisGroups(populateDetailsOptions, function() { alert("Kunde inte ladda data"); });
        }

        $timeout(function() {
            updateDataTable($scope, result);
            updateChart(result);
            
            if ($routeParams.printBw || $routeParams.print) {
                ControllerCommons.printAndCloseWindow($timeout, $window);
            }
        }, 1);
        $timeout(function() {
            updatePrintDataTable($scope, result);
        }, 100);
    };
    
    var populateDetailsOptions = function(result){
        var basePath = isVerksamhet ? "#/verksamhet/" + $routeParams.verksamhetId + "/diagnoskapitel" : "#/nationell/diagnoskapitel";

        for ( var i = 0; i < result.length; i++) {
            if (result[i].id == $routeParams.groupId){
                $scope.selectedDetailsOption = result[i];
                break;
            }
        }
        $scope.subTitle = ($scope.selectedDetailsOption && $scope.selectedDetailsOption.name && $scope.selectedDetailsOption.id) ?  config.title($scope.currentPeriod, $scope.selectedDetailsOption.id + " " + $scope.selectedDetailsOption.name) : "";
        
        $scope.detailsOptions = ControllerCommons.map(result, function(e){
            e.url = basePath + "/" + e.id;
            return e;
        });
    };

    $scope.chartFootnotes = config.chartFootnotes;
    
    $scope.chartContainers = [ "chart1", "chart2" ];

    $scope.toggleSeriesVisibility = function(index) {
        var s1 = that.chart1.series[index];
        var s2 = that.chart2.series[index];
        if (s1.visible) {
            s1.hide();
            s2.hide();
        } else {
            s1.show();
            s2.show();
        }
    };

    $scope.popoverText = config.tooltipHelpText;
    $scope.popoverFootnotesText = config.chartFootnotes;

    if (isVerksamhet){
        $scope.exportTableUrl = config.exportTableUrlVerksamhet($routeParams.verksamhetId, $routeParams.groupId);
        statisticsData[config.dataFetcherVerksamhet]($routeParams.verksamhetId, populatePageWithData, function() { $scope.dataLoadingError = true; }, $routeParams.groupId);
    } else {
        $scope.exportTableUrl = config.exportTableUrl($routeParams.groupId);
        statisticsData[config.dataFetcher](populatePageWithData, function() { $scope.dataLoadingError = true; }, $routeParams.groupId);
    }
    
    $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;
    $scope.toggleTableVisibility = function(event) {
        ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
    };

    $scope.showDetailsOptions = config.showDetailsOptions;

    $scope.spinnerText = "Laddar information...";
    $scope.doneLoading = false;
    $scope.dataLoadingError = false;
    
    $scope.useSpecialPrintTable = true;

    $scope.exportChart = function(chartName) {
        that[chartName].exportChart({}, {legend: {enabled: true, layout: 'vertical'} });
    };

    $scope.print = function(bwPrint) {
        window.open($window.location + (bwPrint ? "?printBw=true" : "?print=true"));
    };
    
    return this;

};