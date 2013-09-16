 'use strict';

/* Controllers */
statisticsApp.controller('OverviewCtrl', function ($scope, statisticsData) {
    var populatePageWithData = function(result){
        $scope.casesPerMonthMaleProportion = result.casesPerMonth.proportionMale;
        $scope.casesPerMonthFemaleProportion = result.casesPerMonth.proportionFemale;
        $scope.casesPerMonthAlteration = result.casesPerMonth.alteration;
        
        var paintDonutChart = function(containerId, chartData) {
            
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
        var color = ["#fbb10c", "#2ca2c6", "#11b73c", "#d165df", "#9c734d", "#008391", "#535353"];
        var diagnosisData = [];
        for (var i = 0; i < result.diagnosisGroups.length; i++) {
            result.diagnosisGroups[i].color = color[i];
            // add browser data
            diagnosisData.push({
                y: result.diagnosisGroups[i].quantity,
                color: result.diagnosisGroups[i].color
            });
        }
        paintDonutChart("diagnosisChart", diagnosisData);
        $scope.diagnosisGroups = result.diagnosisGroups;
    };
    
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
});


