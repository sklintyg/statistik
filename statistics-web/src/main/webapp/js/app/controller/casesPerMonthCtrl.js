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
				labels: {
					formatter: function() {
						return this.value
					}
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
		$scope.headerrows = [[{ "text" : "Period", "colspan" : "1" }].concat(ajaxResult.headers[0])];
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
    
    $scope.spinnerText = "Laddar information...";
    $scope.doneLoading = false;
    $scope.dataLoadingError = false;
    $scope.popoverTextTitle = "Vad innebär sjukfall?";
    $scope.popoverText = "Ett sjukfall omfattar alla de läkarintyg (FK 7263) som rör en viss patient och vars giltighet följer på varandra med max 5 dagars uppehåll. Exempel: Om intyg 1 gäller till och med 14 augusti och intyg 2 gäller från och med17 augusti ses de båda intygen som samma sjukfall. Men om intyg 2 istället varit giltigt från och med 21 augusti räknas intygen som två skilda sjukfall.";
};