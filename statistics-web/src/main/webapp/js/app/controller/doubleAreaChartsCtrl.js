 'use strict';

 app.diagnosisGroupConfig = function() {
     var conf = {};
     conf.dataFetcher = "getDiagnosisGroupData";
     conf.dataFetcherVerksamhet = "getDiagnosisGroupDataVerksamhet";
     conf.showDetailsOptions = false;
     conf.title = "Antal sjukfall per diagnosgrupp";
     conf.chartFootnotes = ["Notera att för en given månad så kan samma sjukfall visas fler än en gång i graf och tabell. Om ett sjukfall innehåller flera intyg i samma månad så hämtas diagnos från varje intyg. Om det är olika diagnosgrupper kommer sjukfallet finnas med en gång för varje diagnosgrupp för respektive månad. Exempel: om ett sjukfall innehåller två intyg för maj månad där det första sätter diagnosen M54 och det andra intyget sätter diagnosen F32 kommer sjukfallet räknas med i gruppen för Muskuloskeleta sjukdomar (M00-M99) samt för Psykiska sjukdomar (F00-F99) i graf och tabell för maj månad."]; 
     return conf;
 }
 
 app.diagnosisSubGroupConfig = function() {
     var conf = {};
     conf.dataFetcher = "getSubDiagnosisGroupData";
     conf.dataFetcherVerksamhet = "getSubDiagnosisGroupDataVerksamhet";
     conf.showDetailsOptions = true;
     conf.detailsOptionsTitlePrefix = "Antal sjukfall för";
     conf.title = "";
     conf.chartFootnotes = ["Notera att för en given månad så kan samma sjukfall visas fler än en gång i graf och tabell. Om ett sjukfall innehåller flera intyg i samma månad så hämtas diagnos från varje intyg. Om det är olika diagnoser som faller inom samma diagnoskapitel men olika diagnosavsnitt kommer sjukfallet finnas med en gång för varje diagnosavsnitt för respektive månad. Exempel: om ett sjukfall innehåller två intyg för maj månad där det första sätter diagnosen XXX och det andra intyget sätter diagnosen YYY(diagnosavsnitt YY ba) kommer sjukfallet räknas med i avsnittet för XXX samt för XXXi graf och tabell för maj månad.", "Endast de sex vanligast förekommande diagnosgrupperna redovisas med namn. Övriga diagnosgrupper redovisas som övrigt."]; 
     return conf;
 }
 
 app.degreeOfSickLeaveConfig = function() {
     var conf = {};
     conf.dataFetcher = "getDegreeOfSickLeave";
     conf.dataFetcherVerksamhet = "getDegreeOfSickLeaveVerksamhet";
     conf.showDetailsOptions = false;
     conf.title = "Antal sjukfall per sjukskrivningsgrad";
     conf.tooltipHelpText ="Begreppet sjukskrivningsgrad beskriver hur många procent av en heltidsarbetstid (25 %, 50 %, 75 % eller 100 %) patienten rekommenderas sjukskrivning.";	 
     conf.chartFootnotes = ["Notera att för en given månad så kan samma sjukfall visas fler än en gång i graf och tabell. Alla sjukskrivningsgrader hämtas från varje intyg. Om det finns flera sjukskrivningsgrader kommer sjukfallet finnas med en gång för varje sjukskrivningsgrad för respektive månad. Exempel: om ett intyg innehåller sjukskrivning för maj månad som först är 50% sjukskrivningsgrad och sedan övergår till 100% kommer sjukfallet visas både för 50% och 100% i graf och tabell för maj månad."]; 
     return conf;
 }
 
 app.doubleAreaChartsCtrl = function ($scope, $routeParams, $window, $timeout, statisticsData, config) {
     var that = this;
     var chart1 = {};
     var chart2 = {};
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
                row.name = rows[r].name
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
        that.chart1 = that.paintChart('chart1', 'Antal kvinnor', chartCategories, chartSeriesFemale);
        
        var chartSeriesMale = ajaxResult.maleChart.series;
        ControllerCommons.setupSeriesForDisplayType($routeParams.printBw, chartSeriesMale, "area");
        that.chart2 = that.paintChart('chart2', 'Antal män', chartCategories, chartSeriesMale);
        
        var yMax = Math.max(that.chart1.yAxis[0].dataMax, that.chart2.yAxis[0].dataMax);
        that.chart1.yAxis[0].setExtremes(0,yMax);
        that.chart2.yAxis[0].setExtremes(0,yMax);
        
        $scope.series = chartSeriesMale;
    };

    var populatePageWithData = function(result){
        $scope.doneLoading = true;
        $timeout(function() {
            updateDataTable($scope, result);
            updateChart(result);
            
            if ($routeParams.printBw) {
                ControllerCommons.printAndCloseWindow($timeout, $window);
            }
        }, 1);
        $timeout(function() {
            updatePrintDataTable($scope, result);
        }, 100);
    };
    
    var populateDetailsOptions = function(result){
        var basePath = isVerksamhet ? "#/verksamhet/" + $routeParams.verksamhetId + "/diagnoskapitel" : "#/nationell/diagnoskapitel"

        for ( var i = 0; i < result.length; i++) {
            if (result[i].id == $routeParams.groupId){
                $scope.selectedDetailsOption = result[i];
                break;
            }
        }
        $scope.subTitle = config.detailsOptionsTitlePrefix + (($scope.selectedDetailsOption && $scope.selectedDetailsOption.name && $scope.selectedDetailsOption.id) ?  " " + $scope.selectedDetailsOption.id + " " + $scope.selectedDetailsOption.name : "");
        
        $scope.detailsOptions = result.map(function(e){
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

    $scope.exportTableData = ControllerCommons.exportTableDataGeneric;

    $scope.subTitle = config.title;
    $scope.popoverText = config.tooltipHelpText;
    $scope.popoverFootnotesText = config.chartFootnotes;

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
    
    $scope.useSpecialPrintTable = true;

    $scope.exportChart = function(chartName) {
        that[chartName].exportChart({}, {legend: {enabled: true, layout: 'vertical'} });
    };

    $scope.bwPrint = function() {
        window.open($window.location + "?printBw=true");
    }
    
    return this;

};

