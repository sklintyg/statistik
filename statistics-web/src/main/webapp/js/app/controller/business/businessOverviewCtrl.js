 'use strict';

 app.businessOverviewCtrl = function ($scope, $timeout, statisticsData, $routeParams) {
 
    var dataReceived = function(result) {
        $scope.doneLoading = true;
        $timeout(function() {
            populatePageWithData(result);
        }, 1);
    };
     
    var populatePageWithData = function(result) {
        $scope.totalCasesPerMonth = result.casesPerMonth.totalCases;

        function paintSexProportionChart(containerId, male, female, period) {
            
            var chartOptions = {
                chart: {
                    renderTo : containerId,
                    backgroundColor: 'transparent',
                    height: 180
                },
                title: {
                    text: period,
                    verticalAlign: 'bottom'
                },
                plotOptions: {
                    pie: {
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: false,
                        },
                        showInLegend: true
                    }
                },
                legend: {
                    labelFormat: '{name} {y}%',
                    align: 'center',
                    verticalAlign: 'top'
                },
                series: [{
                    type: 'pie',
                    name: 'Könsfördelning',
                    data: [
                        {name: 'Kvinnor', y: female, color: "#EA8025"},
                        {name: 'Män', y: male, color: "#008391"}
                    ]
                }],
                exporting: {
                    enabled: false /* This removes the built in highchart export */
                },
                tooltip: {
                    backgroundColor: '#fff',
                    borderWidth: 2
                },
                credits: {
                    enabled: false
                }
            };
            new Highcharts.Chart(chartOptions);
        }

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

        paintSexProportionChart("sexProportionChartOld", result.casesPerMonth.proportionMaleOld, result.casesPerMonth.proportionFemaleOld, result.casesPerMonth.oldPeriod);
        paintSexProportionChart("sexProportionChartNew", result.casesPerMonth.proportionMaleNew, result.casesPerMonth.proportionFemaleNew, result.casesPerMonth.newPeriod);
        
        paintDonutChart("diagnosisChart", extractDonutData(result.diagnosisGroups));
        $scope.diagnosisGroups = result.diagnosisGroups;
        paintDonutChart("ageChart", extractDonutData(result.ageGroups));
        $scope.ageGroups = result.ageGroups;
        paintDonutChart("degreeOfSickLeaveChart", extractDonutData(result.degreeOfSickLeaveGroups));
        $scope.degreeOfSickLeaveGroups = result.degreeOfSickLeaveGroups;

        paintBarChart("sickLeaveLengthChart", result.sickLeaveLength.chartData);
        $scope.longSickLeavesTotal = result.sickLeaveLength.longSickLeavesTotal;
        $scope.longSickLeavesAlteration = result.sickLeaveLength.longSickLeavesAlternation;
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
                    categories: chartData.map(function(e) { return ControllerCommons.htmlsafe(e.name); }),
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

    function extractDonutData(rawData){
        ControllerCommons.addColor(rawData);
        var donutData = [];
        for (var i = 0; i < rawData.length; i++) {
            donutData.push({
                name: ControllerCommons.htmlsafe(rawData[i].name),
                y: rawData[i].quantity,
                color: rawData[i].color
            });
        }
        return donutData;
    }

    var getSelectedVerksamhet = function(selectedVerksamhetId, verksamhets) {
        for (var i = 0; i < verksamhets.length; i++) {
            if (verksamhets[i].id === selectedVerksamhetId) {
                return verksamhets[i];
            }
        }
        return {}; //Selected verksamhet not found
    }

    statisticsData.getBusinessOverview($routeParams.businessId, dataReceived, function() { $scope.dataLoadingError = true; });
    $scope.spinnerText = "Laddar information...";
    $scope.doneLoading = false;
    $scope.dataLoadingError = false;

    statisticsData.getLoginInfo(function(loginInfo){ 
        $scope.businesses = loginInfo.businesses;
        $scope.verksamhetName = getSelectedVerksamhet($routeParams.businessId, loginInfo.businesses).name;
        }, function() { $scope.dataLoadingError = true; });

};
