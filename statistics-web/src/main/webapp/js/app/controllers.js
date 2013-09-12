'use strict';

/* Controllers */
statisticsApp.controller('OverviewCtrl', [ '$scope', '$http', 
    function OverviewCtrl($scope, $http) {
		$http.get("api/getOverview").success(function(result) {
			$scope.casesPerMonthMaleProportion = result.casesPerMonth.proportionMale;
			$scope.casesPerMonthFemaleProportion = result.casesPerMonth.proportionFemale;
			$scope.casesPerMonthAlteration = result.casesPerMonth.alteration;
		}).error(function(data, status, headers, config) {
			alert("Failed to download overview data")
		});
	} ]);

statisticsApp.controller('CasesPerMonthCtrl', [ '$scope', '$http', 
   function ($scope, $http) {
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
			legend : {
				align : 'left',
				verticalAlign : 'top',
				borderWidth : 0,
				useHTML : true,
				floating : true,
				x: 0,
	            y: 0,
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
					floating: true
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
		        crosshairs: true
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
    
	$http.get("api/getNumberOfCasesPerMonth").success(function(result) {
		updateDataTable($scope, result);
		updateChart(result);
	}).error(function(data, status, headers, config) {
		alert("Failed to download chart data")
	});
} ]);


