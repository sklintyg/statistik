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

var paintChart = function(chartCategories, chartSeries) {
	$(function() {
		$('#container').highcharts({
			title : {
				text : result.title,
				x : -20
			// center
			},
			xAxis : {
				categories : chartCategories
			},
			yAxis : {
				title : {
					text : ''
				},
				plotLines : [ {
					value : 0,
					width : 1,
					color : '#808080'
				} ]
			},
			legend : {
				layout : 'vertical',
				align : 'right',
				verticalAlign : 'middle',
				borderWidth : 0
			},
			series : chartSeries
		});
	});
}

var updateDataTable = function($scope, ajaxResult) {
	$scope.headers = ajaxResult.headers;
	$scope.rows = ajaxResult.rows;
}

var updateChart = function(ajaxResult) {
	var chartCategories = getChartCategories(result);
	var chartSeries = getChartSeries(result);
	paintChart(chartCategories, chartSeries);
}

function ChartDataLoader($scope, $http) {
	$http.get("api/getNumberOfCasesPerMonth").success(function(result) {
		updateDataTable($scope, result);
		updateChart(result);
	}).error(function(data, status, headers, config) {
		alert("Failed to download chart data")
	});

};
