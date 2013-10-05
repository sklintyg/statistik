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
				 min : 0,
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
            // Selected sub diagnosis group not found, redirect to default sub diagnosis group
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

    statisticsData[dataFetcher](populatePageWithData, function() { $scope.dataLoadingError = true; }, $routeParams.groupId);

    $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;
    $scope.toggleTableVisibility = function(event) {
        ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
    };

    $scope.showDetailsOptions = showDetailsOptions;
    if (showDetailsOptions) {
        statisticsData.getDiagnosisGroups(populateDetailsOptions, function() { alert("Kunde inte ladda data"); });
    }

    $scope.spinnerText = "Laddar data...";
    $scope.doneLoading = false;
    $scope.dataLoadingError = false;

     return this;

};

