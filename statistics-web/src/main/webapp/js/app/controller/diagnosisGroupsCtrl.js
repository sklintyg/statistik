 'use strict';

var diagnosisGroupsCtrl = function ($scope, $routeParams, $window, statisticsData, dataFetcher, showDetailsOptions) {
    var chart1, chart2;
    $scope.chartContainers = ["container1", "container2"];
    
    var paintChart = function(containerId, yAxisTitle, chartCategories, chartSeries) {
        var chartOptions = {
                
            chart : {
                type: 'area',
                renderTo : containerId,
            },
            title : {
                text : '',
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
                    verticalAlign : 'top',
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
        var topheaders = [{"text": "", "colspan" : "1"}];
        for ( var i = 0; i < ajaxResult.female.headers.length; i++) {
            topheaders.push({"text": ajaxResult.female.headers[i], "colspan" : "2"});
        }
        topheaders.push({"text": "", "colspan" : "1"});
        
        var subheaders = [{"text": "Period", "colspan" : "1"}];
        for ( var i = 0; i < ajaxResult.female.headers.length; i++) {
            subheaders.push({"text": "Kvinnor", "colspan" : "1"});
            subheaders.push({"text": "Män", "colspan" : "1"});
        }
        subheaders.push({"text": "Summering", "colspan" : "1"});
        
        $scope.headerrows = [topheaders, subheaders];
        
        var rows = [];
        for ( var i = 0; i < ajaxResult.female.rows.length; i++) {
            rows.push({"name":ajaxResult.female.rows[i].name, "data":appendSum(zipArrays(ajaxResult.female.rows[i].data, ajaxResult.male.rows[i].data))});
        }
        $scope.rows = rows;
    };

    var updateChart = function(ajaxResult) {
        var chartCategories = getChartCategories(ajaxResult.female);

        var chartSeriesFemale = getChartSeries(ajaxResult.female);
        var chartSeriesTopFemale = chartSeriesFemale.slice(0,6).concat(sumSeries(chartSeriesFemale.slice(6,chartSeriesFemale.length)));
        addColor(chartSeriesTopFemale);
        chart1 = paintChart('container1', 'Antal kvinnor', chartCategories, chartSeriesTopFemale);
        
        var chartSeriesMale = getChartSeries(ajaxResult.male);
        var chartSeriesTopMale = chartSeriesMale.slice(0,6).concat(sumSeries(chartSeriesMale.slice(6,chartSeriesMale.length)));
        addColor(chartSeriesTopMale);
        chart2 = paintChart('container2', 'Antal män', chartCategories, chartSeriesTopMale);
        
        var yMax = Math.max(chart1.yAxis[0].dataMax, chart2.yAxis[0].dataMax);
        chart1.yAxis[0].setExtremes(0,yMax);
        chart2.yAxis[0].setExtremes(0,yMax);
        
        $scope.series = chartSeriesTopMale;
    };

    function sumSeries(series){
        var sum = [];
        series.map(function(e) {
            for ( var i = 0; i < e.data.length; i++) {
                sum[i] = (sum[i] ? sum[i] : 0) + e.data[i];
            }
        });
        var summedSeries = {};
        summedSeries.name = "Övrigt";
        summedSeries.data = sum;
        return summedSeries;
    }
    
    $scope.toggleSeriesVisibility = function (index){
        var s1 = chart1.series[index];
        var s2 = chart2.series[index];
        if (s1.visible){
            s1.hide();
            s2.hide();
        } else {
            s1.show();
            s2.show();
        }
    };
    
    function zipArrays(d1, d2){
        var zipped = new Array();

        for(var i = 0; i < d1.length; i++)
        {
            zipped.push(d1[i]);
            zipped.push(d2[i]);
        }
        return zipped;
    }

    function appendSum(numberArray){
        var sum = 0;
        for ( var i = 0; i < numberArray.length; i++) {
            sum += numberArray[i];
        }
        return numberArray.concat([sum]);
    }
    
    $scope.exportTableData = exportTableDataGeneric;
    
    var populatePageWithData = function(result){
        updateDataTable($scope, result);
        updateChart(result);
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

    $scope.subTitle = "Antal sjukfall per diagnosgrupp";
    
    statisticsData[dataFetcher](populatePageWithData, dataDownloadFailed, $routeParams.groupId);
    
    $scope.showHideDataTable = showHideDataTableDefault;
    $scope.toggleTableVisibility = function(event){
        toggleTableVisibilityGeneric(event, $scope);
    };

    $scope.showDetailsOptions = showDetailsOptions;
    if (showDetailsOptions){
        statisticsData.getDiagnosisGroups(populateDetailsOptions, dataDownloadFailed);
    }

};

