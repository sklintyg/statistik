 'use strict';


 function addColor(rawData){
     var color = ["#fbb10c", "#2ca2c6", "#11b73c", "#d165df", "#9c734d", "#008391", "#535353"];
     for (var i = 0; i < rawData.length; i++) {
         rawData[i].color = color[i];
     }
 }
 
 var dataDownloadFailed = function () {
     alert("Failed to download chart data");
 };

 var showHideDataTableDefault = "Dölj datatabell";
 var toggleTableVisibilityGeneric = function(event, $scope){
     var elem = $(event.target);
     var accordionGroup = $(elem.parents('.accordion-group')[0]);
     var accordionBody = $(accordionGroup.children('.accordion-body'));
     var wasTableVisible = accordionBody.hasClass("in");
     $scope.showHideDataTable = wasTableVisible ? "Visa datatabell" : "Dölj datatabell"; 
 };
 
 var exportTableDataGeneric = function() {
     var dt = $('#datatable');
     var csvData = table2CSV(dt);
     $.generateFile({
         filename : 'export.csv',
         content : csvData,
         script : 'fileDownload.jsp'
     });
 };
 
 var getChartCategories = function(ajaxResult) {
     return ajaxResult.rows.map(function(e) {
         return e.name;
     });
 };

 var getChartSeries = function(ajaxResult) {
     var dataSeries = [];
     var length = ajaxResult.headers.length;
     for ( var i = 0; i < length; i++) {
         var ds = {};
         ds.name = ajaxResult.headers[i];
         ds.data = [];
         dataSeries.push(ds);
     }

     var length = ajaxResult.rows.length;
     for ( var i = 0; i < length; i++) {
         var rowdata = ajaxResult.rows[i].data;
         var rowdatalength = rowdata.length;
         for ( var c = 0; c < rowdatalength; c++) {
             dataSeries[c].data.push(rowdata[c]);
         }
     }
     return dataSeries;
 };

/* Controllers */
statisticsApp.controller('OverviewCtrl', function ($scope, statisticsData) {
    var populatePageWithData = function(result){
        $scope.casesPerMonthMaleProportion = result.casesPerMonth.proportionMale;
        $scope.casesPerMonthFemaleProportion = result.casesPerMonth.proportionFemale;
        $scope.casesPerMonthAlteration = result.casesPerMonth.alteration;
        
        function paintDonutChart(containerId, chartData) {
            
            var chartOptions = {
                chart: {
                    renderTo : containerId,
                    type: 'pie',
                    backgroundColor: 'transparent',
                    height: 180,
                },
                exporting: {
                    enabled: false /* This removes the built in highchart export */           
                },
                title: {
                    text: ''
                },
                tooltip: {
                    backgroundColor: '#fff',
                    borderWidth: 2
                },
                credits: {
                    enabled: false
                },
                series: [{
                    name: 'Antal',
                    data: chartData,
                    innerSize: '40%',
                    dataLabels: {
                        formatter: function() {
                            return null;
                        },
                    }
                }]
            };
            new Highcharts.Chart(chartOptions);
        }

        paintDonutChart("diagnosisChart", extractDonutData(result.diagnosisGroups));
        $scope.diagnosisGroups = result.diagnosisGroups;
        paintDonutChart("ageChart", extractDonutData(result.ageGroups));
        $scope.ageGroups = result.ageGroups;
        paintDonutChart("degreeOfSickLeaveChart", extractDonutData(result.degreeOfSickLeaveGroups));
        $scope.degreeOfSickLeaveGroups = result.degreeOfSickLeaveGroups;
        
        paintBarChart("sickLeaveLengthChart", result.sickLeaveLength.chartData);
        $scope.longSickLeavesTotal = result.sickLeaveLength.longSickLeavesTotal;
        $scope.longSickLeavesAlteration = result.sickLeaveLength.longSickLeavesAlternation;

        addColor(result.perCounty);
        paintSickLeavePerCountyChart("sickLeavePerCountyChart", result.perCounty);
        $scope.sickLeavePerCountyGroups = result.perCounty;
    };
    
    function paintBarChart(containerId, chartData) {
        
        var chartOptions = {
                chart: {
                    renderTo : containerId,
                    type: 'column',
                    height: 185,
                    backgroundColor: 'transparent',
                },
                title: {
                    text: ''
                },
                plotOptions: {
                    series: {
                        color: '#11b73c'
                    }
                },
                xAxis: {
                    categories: chartData.map(function(e) { return e.name; }),
                    min: 0,
                    title: {
                        text: 'DAGAR'
                    },
                    labels: {
                        rotation: -45,
                        align: 'right',
                    }
                },
                yAxis: {
                    min: 0,
                    title: {
                        text: 'ANTAL',
                    }
                },
                exporting: {
                    enabled: false /* This removes the built in highchart export */           
                },
                legend: {
    	            align: 'top left',
    	            verticalAlign: 'top',
    	            x: 80,
    	            y: 0,
    	            borderWidth: 0,
    	            enabled: false,
    	        },
                tooltip: {
                    backgroundColor: '#fff',
                    borderWidth: 2
                },
                credits: {
                    enabled: false
                },
                series: [{
                    name: "Antal",
                    data: chartData.map(function(e) { return e.quantity; })
                }]
        };
        new Highcharts.Chart(chartOptions);
    }
    
    function paintSickLeavePerCountyChart(containerId, chartData) {

        var chartOptions = {
            chart : {
                renderTo : containerId,
                height : 185,
                width: 133,
                type : 'bubble',
                backgroundColor: 'transparent',
            },
            credits : {
                enabled : false
            },
            exporting : {
                enabled : false
            },
            title : {
                text : ''
            },
            legend : {
                enabled : false
            },
            xAxis : [ {
                min: 0,
                max: 100,
                minPadding: 0,
                maxPadding: 0,
                minorGridLineWidth : 0,
                labels : {
                    enabled : false
                },
                gridLineWidth : 0
            } ],
            yAxis : [ {
                min: 0,
                max: 100,
                minPadding: 0,
                maxPadding: 0,
                minorGridLineWidth : 0,
                labels : {
                    enabled : false
                },
                gridLineWidth : 0,
                title : ''
            } ],
            series : chartData.map(function(e) {
                    var coords = getCoordinates(e);
                    return {"data": [[coords.x, coords.y, e.quantity]], color: e.color }; 
                })
        };
        new Highcharts.Chart(chartOptions, function(chart) { // on complete
            chart.renderer.image('img/sweden_graph.png', 30, 10, 69, 160).add();
        });
    }
    
    function getCoordinates(perCountyObject){
        var county = perCountyObject.name.toLowerCase();
        if (contains(county, "stockholm")){
            return {"x": 55, "y": 28};
        } else if (contains(county, "västra götaland")){
            return {"x": 25, "y": 23};
        } else if (contains(county, "skåne")){
            return {"x": 29, "y": 5};
        } else if (contains(county, "östergötland")){
            return {"x": 47, "y": 20};
        } else if (contains(county, "uppsala")){
            return {"x": 57, "y": 34};
        } else {
            return {"x": 10, "y": 80}; //Default point should not match any part of sweden
        }
    }
    
	function contains(master, substring) {
		return master.indexOf(substring) != -1;
	}
	
    function extractDonutData(rawData){
        addColor(rawData);
        var donutData = [];
        for (var i = 0; i < rawData.length; i++) {
            donutData.push({
                name: rawData[i].name,
                y: rawData[i].quantity,
                color: rawData[i].color
            });
        }
        return donutData;
    }
    
    statisticsData.getOverview(populatePageWithData, dataDownloadFailed);
});

statisticsApp.controller('CasesPerMonthCtrl', function ($scope, statisticsData) {

    $scope.chartContainers = ["container"];
    
	var paintChart = function(chartCategories, chartSeries) {
		var chartOptions = {
				
			chart : {
				renderTo : 'container',
			},
            title : {
                text : '',
            },
			legend: {
	            align: 'top left',
	            verticalAlign: 'top',
	            x: 80,
	            y: 0,
	            borderWidth: 0,
	        },	
			xAxis : {
				labels: {
                	rotation: 310
                },
				categories : chartCategories
			},
			yAxis : {
				title : {
					text : 'Antal',
					align : 'high',
					verticalAlign : 'top',
					rotation : 0,
					floating: true,
					x: 30,
		            y: -10
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
            	line: {
                    allowPointSelect: false,
                	marker: {
						enabled: false,
						symbol: 'circle'
                	},
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: false
                    },
                    events: {
                    	legendItemClick: function () { // This function removes interaction for plot and legend-items
                        	return false;
                    	}
                	},
                    showInLegend: true
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
		new Highcharts.Chart(chartOptions);
	};

	var updateDataTable = function($scope, ajaxResult) {
		$scope.headerrows = [[{ "text" : "Period", "colspan" : "1" }].concat(ajaxResult.headers.map(function(e) {
            return { "text" : e, "colspan" : "1" };
        }))];
		$scope.rows = ajaxResult.rows;
	};

	var updateChart = function(ajaxResult) {
		var chartCategories = getChartCategories(ajaxResult);
		var chartSeries = getChartSeries(ajaxResult);
		paintChart(chartCategories, chartSeries);
	};

    $scope.exportTableData = exportTableDataGeneric;
    
    var populatePageWithData = function(result){
        updateDataTable($scope, result);
        updateChart(result);
        $scope.subTitle = "Antal sjukfall per månad " + result.rows[0].name + " - " + result.rows[result.rows.length-1].name;
    };

    statisticsData.getNumberOfCasesPerMonth(populatePageWithData, dataDownloadFailed);
    
    $scope.showHideDataTable = showHideDataTableDefault;
    $scope.toggleTableVisibility = function(event){
        toggleTableVisibilityGeneric(event, $scope);
    };
});




statisticsApp.controller('DiagnosisGroupsCtrl', function ($scope, $routeParams, $window, statisticsData, dataFetcher, showDetailsOptions) {
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

});

