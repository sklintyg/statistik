 'use strict';

 app.businessOverviewCtrl = function ($scope, statisticsData, $routeParams) {
    var populatePageWithData = function(result){
        $scope.doneLoading = true;
        $scope.$apply();

        $scope.casesPerMonthMaleProportion = result.casesPerMonth.proportionMale;
        $scope.casesPerMonthFemaleProportion = result.casesPerMonth.proportionFemale;
        $scope.casesPerMonthAlteration = result.casesPerMonth.alteration;

        function paintDonutChart(containerId, chartData) {

            var chartOptions = {
                chart: {
                    renderTo : containerId,
                    type: 'pie',
                    backgroundColor: 'transparent',
                    height: 180
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
                        }
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

        ControllerCommons.addColor(result.perCounty);
        paintSickLeavePerCountyChart("sickLeavePerCountyChart", result.perCounty);
        $scope.sickLeavePerCountyGroups = result.perCounty;
    };

    function paintBarChart(containerId, chartData) {

        var chartOptions = {
                chart: {
                    renderTo : containerId,
                    type: 'column',
                    height: 185,
                    backgroundColor: 'transparent'
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
                    categories: chartData.map(function(e) { return htmlsafe(e.name); }),
                    min: 0,
                    title: {
                        text: 'DAGAR'
                    },
                    labels: {
                        rotation: -45,
                        align: 'right'
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
    	            align: 'top left',
    	            verticalAlign: 'top',
    	            x: 80,
    	            y: 0,
    	            borderWidth: 0,
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
                type : 'bubble',
                backgroundColor: 'transparent'
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
			plotOptions: {
				bubble: {
					tooltip : {
						headerFormat: '{series.name}<br/>',
			            pointFormat: '<b>{point.z}</b>',
			            shared: true
			        }
				}
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
                    return {"data": [[coords.x, coords.y, e.quantity]], color: e.color, name: htmlsafe(e.name) };
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
        ControllerCommons.addColor(rawData);
        var donutData = [];
        for (var i = 0; i < rawData.length; i++) {
            donutData.push({
                name: htmlsafe(rawData[i].name),
                y: rawData[i].quantity,
                color: rawData[i].color
            });
        }
        return donutData;
    }

	function htmlsafe(string) {
		return string.replace(/&/g,'&amp;').replace(/</g,'&lt;');
	}

    statisticsData.getBusinessOverview($routeParams.businessId, populatePageWithData, function() { $scope.dataLoadingError = true; });
    $scope.spinnerText = "Laddar data...";
    $scope.doneLoading = false;
    $scope.dataLoadingError = false;

    statisticsData.getLoginInfo(function(loginInfo){ $scope.businesses = loginInfo.businesses; }, function() { $scope.dataLoadingError = true; });
    
};
