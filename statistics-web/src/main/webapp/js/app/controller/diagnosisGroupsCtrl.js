 'use strict';

 app.diagnosisGroupsCtrl = function ($scope, $routeParams, $window, statisticsData, dataFetcher, showDetailsOptions) {
    var chart1, chart2;
     var that = this;

     this.paintChart = function(containerId, yAxisTitle, chartCategories, chartSeries) {
         var chartOptions = {

             chart : {
                 type: 'area',
                 renderTo : containerId
             },
             title : {
                 text : ''
             },
             legend: {
                 enabled: false
             },
             xAxis : {
                 labels: {
                     rotation: 310
                 },
                 categories : chartCategories
             },
             yAxis : {
                 title : {
                     text : yAxisTitle,
                     align : 'high',
                     verticalAlign : 'top'
                 },
                 plotLines : [ {
                     value : 0,
                     width : 1,
                     color : '#808080'
                 } ]
             },
             exporting: {
                 enabled: false /* This removes the built in highchart export */
             },
             plotOptions: {
                 area: {
                     stacking: 'normal',
                     lineColor: '#666666',
                     lineWidth: 1,
                     marker: {
                         enabled: false,
                         symbol: 'circle'
                     }
                 }
             },
             tooltip: {
                 /*crosshairs: true*/ // True if crosshair. Not specified in design document for Statistiktjänsten 1.0.
             },
             credits: {
                 enabled: false
             },
             series : chartSeries
         };
         return new Highcharts.Chart(chartOptions);
     };

     this.getTopHeaders = function (ajaxResult) {
         var topheaders = [
             {"text": "", "colspan": "1"}
         ];
         for (var i = 0; i < ajaxResult.femaleTable.headers.length; i++) {
             topheaders.push({"text": ajaxResult.femaleTable.headers[i], "colspan": "2"});
         }
         topheaders.push({"text": "", "colspan": "1"});
         return topheaders;
     };

     this.getSubHeaders = function(ajaxResult) {
         var subheaders = [
             {"text": "Period", "colspan": "1"}
         ];
         for (var i = 0; i < ajaxResult.femaleTable.headers.length; i++) {
             subheaders.push({"text": "Kvinnor", "colspan": "1"});
             subheaders.push({"text": "Män", "colspan": "1"});
         }
         subheaders.push({"text": "Summering", "colspan": "1"});
         return subheaders;
     };

     this.getRows = function(ajaxResult) {
         var rows = [];
         for (var i = 0; i < ajaxResult.femaleTable.rows.length; i++) {
             rows.push({"name": ajaxResult.femaleTable.rows[i].name, "data": that.appendSum(that.zipArrays(ajaxResult.femaleTable.rows[i].data, ajaxResult.maleTable.rows[i].data))});
         }
         return rows;
     };

     this.zipArrays = function (d1, d2){
         var zipped = new Array();

         for(var i = 0; i < d1.length; i++)
         {
             zipped.push(d1[i]);
             zipped.push(d2[i]);
         }
         return zipped;
     };

     this.appendSum = function (numberArray){
         var sum = 0;
         for ( var i = 0; i < numberArray.length; i++) {
             sum += numberArray[i];
         }
         return numberArray.concat([sum]);
     };

    var updateDataTable = function($scope, ajaxResult) {
        var topheaders = that.getTopHeaders(ajaxResult);
        var subheaders = that.getSubHeaders(ajaxResult);
        $scope.headerrows = [topheaders, subheaders];
        $scope.rows = that.getRows(ajaxResult);
    };

    var updateChart = function(ajaxResult) {
        var chartCategories = ajaxResult.femaleChart.headers;

        var chartSeriesFemale = ajaxResult.femaleChart.rows;
        ControllerCommons.addColor(chartSeriesFemale);
        chart1 = that.paintChart('container1', 'Antal kvinnor', chartCategories, chartSeriesFemale);
        
        var chartSeriesMale = ajaxResult.maleChart.rows;
        ControllerCommons.addColor(chartSeriesMale);
        chart2 = that.paintChart('container2', 'Antal män', chartCategories, chartSeriesMale);
        
        var yMax = Math.max(chart1.yAxis[0].dataMax, chart2.yAxis[0].dataMax);
        chart1.yAxis[0].setExtremes(0,yMax);
        chart2.yAxis[0].setExtremes(0,yMax);
        
        $scope.series = chartSeriesMale;
    };

    var populatePageWithData = function(result){
        updateDataTable($scope, result);
        updateChart(result);
        $scope.doneLoading = true;
    };
    
    var populateDetailsOptions = function(result){
        $scope.detailsOption = result[0];
        for ( var i = 0; i < result.length; i++) {
            if (result[i].id == $routeParams.groupId){
                $scope.detailsOption = result[i];
                break;
            }
        }
        if (!$scope.detailsOption){
            // Selected sub diagnos group not found, redirect to default sub driagnos group
            $window.location="#/underdiagnosgrupper";
        }
        $scope.$watch(function(){return $scope.detailsOption;}, function() {
            if ($scope.detailsOption.id != $routeParams.groupId){
                $window.location="#/underdiagnosgrupper/" + $scope.detailsOption.id;
            }
        });
        $scope.detailsOptions = result;
    };

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

    $scope.subTitle = "Antal sjukfall per diagnosgrupp";

    statisticsData[dataFetcher](populatePageWithData,
            ControllerCommons.dataDownloadFailed, $routeParams.groupId);

    $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;
    $scope.toggleTableVisibility = function(event) {
        ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
    };

    $scope.showDetailsOptions = showDetailsOptions;
    if (showDetailsOptions) {
        statisticsData.getDiagnosisGroups(populateDetailsOptions,
                ControllerCommons.dataDownloadFailed);
    }

    $scope.spinnerText = "Laddar data...";
    $scope.doneLoading = false;

     return this;

};

