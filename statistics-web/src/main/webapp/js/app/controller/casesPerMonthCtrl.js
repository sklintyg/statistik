 'use strict';

 app.casesPerMonthCtrl = function ($scope, statisticsData) {

    $scope.chartContainers = ["container"];
    
	var paintChart = function(chartCategories, chartSeries) {
		var chartOptions = {
				
			chart : {
				renderTo : 'container'
			},
            title : {
                text : ''
            },
			legend: {
	            align: 'top left',
	            verticalAlign: 'top',
	            x: 80,
	            y: 0,
	            borderWidth: 0
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
		var chartCategories = ControllerCommons.getChartCategories(ajaxResult);
		var chartSeries = ControllerCommons.getChartSeries(ajaxResult);
		chartSeries.pop();
		paintChart(chartCategories, chartSeries);
	};

    $scope.exportTableData = ControllerCommons.exportTableDataGeneric;
    
    var populatePageWithData = function(result){
        updateDataTable($scope, result);
        updateChart(result);
        $scope.subTitle = "Antal sjukfall per månad " + result.rows[0].name + " - " + result.rows[result.rows.length-1].name;
        $scope.doneLoading = true;
    };

    statisticsData.getNumberOfCasesPerMonth(populatePageWithData, function() { $scope.dataLoadingError = true; });
    
    $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;
    $scope.toggleTableVisibility = function(event){
        ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
    };
    
    $scope.spinnerText = "Laddar data...";
    $scope.doneLoading = false;
    $scope.dataLoadingError = false;
};
