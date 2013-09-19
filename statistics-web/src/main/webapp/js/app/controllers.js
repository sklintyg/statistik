 'use strict';

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
                    height: 185,
                    margin: [10, 10, 10, 10]
                    
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
                        text: 'ANTAL'
                    }
                },
                exporting: {
                    enabled: false /* This removes the built in highchart export */           
                },
                legend: {
                    enabled: false
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
                type : 'bubble'
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
            chart.renderer.image('/img/sweden_graph.png', 30, 10, 69, 160).add();
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
    
    function addColor(rawData){
        var color = ["#fbb10c", "#2ca2c6", "#11b73c", "#d165df", "#9c734d", "#008391", "#535353"];
        for (var i = 0; i < rawData.length; i++) {
            rawData[i].color = color[i];
        }
    }
    
    var dataDownloadFailed = function () {
        alert("Failed to download overview data");
    }
    
    statisticsData.getOverview(populatePageWithData, dataDownloadFailed);
});

statisticsApp.controller('CasesPerMonthCtrl', function ($scope, statisticsData) {
    
	var getChartCategories = function(ajaxResult) {
		return ajaxResult.rows.map(function(e) {
			return e.name;
		});
	}

	var getChartSeries = function(ajaxResult) {
		var dataSeries = [];
		var length = ajaxResult.headers.length;
		for ( var i = 0; i < length; i++) {
			var ds = [];
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
	}

	var paintChart = function(chartCategories, chartSeries, chartTitle) {
		var chartOptions = {
				
			chart : {
				renderTo : 'container',
			},
			title : {
				text : chartTitle,
				x : -20,
			// center
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
	}

	var updateDataTable = function($scope, ajaxResult) {
		$scope.headers = ajaxResult.headers;
		$scope.rows = ajaxResult.rows;
	}

	var updateChart = function(ajaxResult) {
		var chartCategories = getChartCategories(ajaxResult);
		var chartSeries = getChartSeries(ajaxResult);
		paintChart(chartCategories, chartSeries, ajaxResult.title);
	}

    $scope.exportTableData = function() {
    	var dt = $('#datatable');
    	var csvData = table2CSV(dt);
        $.generateFile({
            filename : 'export.csv',
            content : csvData,
            script : 'fileDownload.jsp'
        });
    };	
    
    var populatePageWithData = function(result){
        updateDataTable($scope, result);
        updateChart(result);
        $scope.startDate = result.rows[0].name;
        $scope.endDate = result.rows[result.rows.length-1].name;
    }

    var dataDownloadFailed = function () {
        alert("Failed to download chart data");
    }

    statisticsData.getNumberOfCasesPerMonth(populatePageWithData, dataDownloadFailed);
    
    $scope.showHideDataTable = "Dölj datatabell";
    $scope.toggleTableVisibility = function(event){
        var elem = $(event.target);
        var accordionGroup = $(elem.parents('.accordion-group')[0]);
        var accordionBody = $(accordionGroup.children('.accordion-body'));
        var wasTableVisible = accordionBody.hasClass("in");
        $scope.showHideDataTable = wasTableVisible ? "Visa datatabell" : "Dölj datatabell"; 
    }
});


