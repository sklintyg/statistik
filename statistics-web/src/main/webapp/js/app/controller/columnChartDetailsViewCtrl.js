 'use strict';

 app.nationalSickLeaveLengthConfig = function() {
     var conf = {};
     conf.dataFetcher = "getNationalSickLeaveLengthData",
     conf.dataFetcherVerksamhet = "getNationalSickLeaveLengthDataVerksamhet",
     conf.title = function(){return "Antal sjukfall baserat på sjukskrivningslängd";}
     return conf;
 }

 app.nationalSickLeaveLengthHistoricalConfig = function() {
     var conf = {};
     conf.dataFetcherVerksamhet = "getNationalSickLeaveLengthHistoricalDataVerksamhet",
     conf.title = function(monthsIncluded){return "Antal pågående samt avslutade sjukfall baserat på sjukskrivningslängd senaste " + monthsIncluded + " månaderna";}
     return conf;
 }
 
 app.nationalAgeGroupConfig = function() {
     var conf = {};
     conf.dataFetcher = "getAgeGroups",
     conf.dataFetcherVerksamhet = "getAgeGroupsVerksamhet",
     conf.title = function(){return "Antal pågående sjukfall baserat på patientens ålder";}
     return conf;
 }
 
 app.nationalAgeGroupHistoricalConfig = function() {
     var conf = {};
     conf.dataFetcherVerksamhet = "getAgeGroupsHistoricalVerksamhet",
     conf.title = function(monthsIncluded){return "Antal pågående samt avslutade sjukfall baserat på patientens ålder senaste " + monthsIncluded + " månaderna";}
         return conf;
 }
 
 app.columnChartDetailsViewCtrl = function ($scope, $routeParams, statisticsData, config) {
     var isVerksamhet = $routeParams.verksamhetId ? true : false;

    $scope.chartContainers = ["container"];
    
	var paintChart = function(chartCategories, chartSeries) {
		var chartOptions = {
			chart : {
				renderTo : 'container',
                type: 'column'
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
                labels : { rotation : 310 },
                categories : chartCategories.map(function(name) { return ControllerCommons.htmlsafe(name); })
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
		$scope.headerrows = ajaxResult.headers;
		$scope.rows = ajaxResult.rows;
	};

	var updateChart = function(ajaxResult) {
		paintChart(ajaxResult.categories, ControllerCommons.addColor(ajaxResult.series));
	};

    $scope.exportTableData = ControllerCommons.exportTableDataGeneric;
    
    var populatePageWithData = function(result){
        updateDataTable($scope, result.tableData);
        updateChart(result.chartData);
        $scope.subTitle = config.title(result.monthsIncluded);
        $scope.doneLoading = true;
    };

    if (isVerksamhet){
        statisticsData[config.dataFetcherVerksamhet]($routeParams.verksamhetId, populatePageWithData, function() { $scope.dataLoadingError = true; });
    } else {
        statisticsData[config.dataFetcher](populatePageWithData, function() { $scope.dataLoadingError = true; });
    }
    
    $scope.showHideDataTable = ControllerCommons.showHideDataTableDefault;
    $scope.toggleTableVisibility = function(event){
        ControllerCommons.toggleTableVisibilityGeneric(event, $scope);
    };
    
    $scope.spinnerText = "Laddar information...";
    $scope.doneLoading = false;
    $scope.dataLoadingError = false;
};
