/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

'use strict';

var ControllerCommons = new function(){

    this.updateDataTable = function (scope, tableData) {
        scope.headerrows = tableData.headers;
        if (scope.headerrows.length > 1) {
            scope.headerrows[0].centerAlign = true;
        }
        scope.rows = tableData.rows;
    };
    
    this.htmlsafe = function(string) {
        return string.replace(/&/g, '&amp;').replace(/</g, '&lt;');
    };
    
    this.isNumber = function(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    };
    
    this.makeThousandSeparated = function(input) {
        return ControllerCommons.isNumber(input) ? input.toString().split('').reverse().join('').match(/.{1,3}/g).join('\u00A0').split('').reverse().join('') : input;
    };
    
    this.getFileName = function(chartName) {
        var d = new Date();

        var year = "" + d.getFullYear();
        var month = d.getMonth() < 9 ? "0" + (d.getMonth() + 1) : "" + (d.getMonth() + 1);
        var day = d.getDate() < 10 ? "0" + d.getDate() : "" + d.getDate();
        var date = year + month + day;
        
        var hour = d.getHours() < 10 ? "0" + d.getHours() : "" + d.getHours();
        var minute = d.getMinutes() < 10 ? "0" + d.getMinutes() : "" + d.getMinutes();
        var second = d.getSeconds() < 10 ? "0" + d.getSeconds() : "" + d.getSeconds();
        var time = hour + minute + second;

        return String(chartName).replace(/\s+/g, "_") + "_" + date + "_" + time;
    };
    
    this.exportChart = function(chart, chartName, title, diagnosFilters, legendLayout) {
        var options = {filename: ControllerCommons.getFileName(chartName)};
        var extendedChartOptions = { legend: { enabled: true } };
        var chartHeight = 400;
        extendedChartOptions.chart = {};
        extendedChartOptions.chart.height = chartHeight;
        extendedChartOptions.chart.width = 600;
        if (legendLayout) {
            extendedChartOptions.legend.layout = legendLayout;
        }
        if (title) {
            extendedChartOptions.title = {
                text: title,
                margin: 30
            };
            extendedChartOptions.subtitle = {
                text: " "
            };
            extendedChartOptions.chart.marginTop = null;
            extendedChartOptions.chart.backgroundColor = "#FFFFFF";
            extendedChartOptions.chart.spacingLeft = 0;
        }
        extendedChartOptions.subtitle = {
            text: " "
        };
        if (diagnosFilters) {
            var fontSize = 12;
            var fontSizeHeader = 15;
            extendedChartOptions.chart.spacingBottom = (diagnosFilters.length + 2) * fontSize + fontSizeHeader + diagnosFilters.length * 2;
            extendedChartOptions.chart.height = chartHeight + extendedChartOptions.chart.spacingBottom;
            extendedChartOptions.chart.events = {
                load: function () {
                    this.renderer.text('Sammanställning av diagnosfilter', 10, chartHeight + fontSize / 2 + fontSizeHeader)
                        .css({
                            color: '#008391',
                            fontSize: fontSizeHeader + 'px'
                        })
                        .add();
                    var arrayLength = diagnosFilters.length;
                    for (var i = 0; i < arrayLength; i++) {
                        this.renderer.text(diagnosFilters[i], 10, chartHeight + (2 + i) * fontSize + fontSizeHeader + i * 2)
                            .css({
                                color: '#008391',
                                fontSize: fontSize + 'px'
                            })
                            .add();
                    }
                }
            };
        }
        chart.exportChart(options, extendedChartOptions);
    };

    this.getHighChartConfigBase = function(chartCategories, chartSeries, doneLoadingCallback) {
        return {
            chart : {
                renderTo : 'chart1',
                backgroundColor : null, //transparent
                plotBorderWidth: 1,
                events: { load: doneLoadingCallback }
    },
            title : {
                text : ''
            },
            legend : {
                align : 'top left',
                x : 120,
                y : 0,
                borderWidth : 0
            },
            xAxis : {
                labels : {
                    rotation : 320,
                    align : 'right',
                    style: {
                    	whiteSpace: 'pre',
                    	width: '200px'
                    },
                    formatter: function() {
                        //If the label is more than 30 characters then cut the text and add ellipsis
                        return this.value.length > 30 ? this.value.substring(0,30) + "..." : this.value;
                    }
                },
                categories : _.map(chartCategories, function(name) {
                    return ControllerCommons.htmlsafe(name);
                }),
                title: { 
                	align: 'high',
                	style: {
                    	color: '#008391'
                    }
                }
            },
            yAxis : {
                allowDecimals : false,
                min : 0,
                title : {
                    text : 'Antal sjukfall',
                    align : 'high',
                    verticalAlign : 'top',
                    rotation : 0,
                    floating : true,
                    x : -10,
                    y : 5,
                    style: {
                    	color: '#008391'
                    }
                },
                labels : {
                    formatter : function() {
                        return ControllerCommons.makeThousandSeparated(this.value);
                    }
                },
                plotLines : [ {
                    value : 0,
                    width : 1,
                    color : '#808080'
                } ]
            },
            exporting : {
                enabled : false,
                url: highchartsExportUrl
            },
            plotOptions : {
                line : {
                    allowPointSelect : false,
                    marker : {
                        enabled : false,
                        symbol : 'circle'
                    },
                    cursor : 'pointer',
                    dataLabels : {
                        enabled : false
                    },
                    events : {
                        legendItemClick : function() { // This function removes interaction for plot and legend-items
                            return false;
                        }
                    },
                    showInLegend : true
                },
                column : {
                    stacking : 'normal'
                },
                area : {
                    stacking : 'normal',
                    lineColor : '#666666',
                    lineWidth : 1,
                    marker : {
                        enabled : false,
                        symbol : 'circle'
                    }
                },
                pie : {
                    cursor : 'pointer',
                    dataLabels : {
                        enabled : false
                    },
                    showInLegend : false
                }
            },
            tooltip : {
                backgroundColor : '#fff',
                borderWidth : 2,
                style: {
                	color: '#333333',
                	fontSize: '12px',
                	padding: '8px'
                }
            },
            credits : {
                enabled : false
            },
            series : chartSeries
        };
    };

    this.getEnhetCountText = function(enhetsCount, basedOnAlreadyInText) {
        'use strict';
        var singleEnhet = enhetsCount === 1;
        if (basedOnAlreadyInText) {
            return enhetsCount ? " och " + enhetsCount + " enhet" + (singleEnhet ? "" : "er") + " " : " ";
        }
        return enhetsCount ? " baserat på " + enhetsCount + " enhet" + (singleEnhet ? "" : "er") + " " : " ";
    };

    function icdStructureAsArray(icdStructure) {
        return _.map(icdStructure, function (icd) {
            return icdStructureAsArray(icd.subItems).concat(icd);
        });
    }

    this.getDiagnosFilterInformationText = function(diagnosFilterIds, icdStructure) {
        var icdStructureAsFlatArray = _.compose(_.flatten, icdStructureAsArray)(icdStructure);
        return _.map(diagnosFilterIds, function(diagnosId){
            var icdItem = _.find(icdStructureAsFlatArray, function(icd){
                return icd.numericalId == diagnosId;
            });
            return icdItem.id + " " + icdItem.name;
        });
    };

    this.populateActiveDiagnosFilter = function(scope, statisticsData, diagnosIds, isPrint) {
        if (!diagnosIds) {
            return;
        }
        statisticsData.getIcd10Structure(function (diagnoses) {
            scope.activeDiagnosFilters = diagnoses ? ControllerCommons.getDiagnosFilterInformationText(diagnosIds, diagnoses) : null;
            scope.activeDiagnosFiltersForPrint = isPrint ? scope.activeDiagnosFilters : null;
        }, function () {
            scope.activeDiagnosFilters = diagnosIds;
            scope.activeDiagnosFiltersForPrint = isPrint ? scope.activeDiagnosFilters : null;
        });
    };

    this.setupDiagnosisSelector = function(diagnosisTreeFilter, $routeParams, $scope, messageService, $timeout, statisticsData, $location) {
        //Initiate the diagnosisTree if we need to.
        diagnosisTreeFilter.setup($routeParams);
        $scope.diagnosisTreeFilter = diagnosisTreeFilter;

        $scope.diagnosisSelectorData = {
            titleText: messageService.getProperty("comparediagnoses.lbl.val-av-diagnoser", null, "", null, true),
            buttonLabelText: messageService.getProperty("lbl.filter.val-av-diagnoser-knapp", null, "", null, true),
            firstLevelLabelText: messageService.getProperty("lbl.filter.modal.kapitel", null, "", null, true),
            secondLevelLabelText: messageService.getProperty("lbl.filter.modal.avsnitt", null, "", null, true),
            thirdLevelLabelText: messageService.getProperty("lbl.filter.modal.kategorier", null, "", null, true)
        };

        $scope.diagnosisSelected = function () {
            ControllerCommons.diagnosisToCompareSelected(diagnosisTreeFilter, $timeout, $scope, statisticsData, $location);
        };
    };

    this.diagnosisToCompareSelected = function(diagnosisTreeFilter, $timeout, $scope, statisticsData, $location) {
        var diagnoses = diagnosisTreeFilter.getSelectedDiagnosis();

        $timeout(function () {
            //Ugly fix from http://stackoverflow.com/questions/20827282/cant-dismiss-modal-and-change-page-location
            $('#cancelModal').modal('hide');
            $('.modal-backdrop').remove();
            $('body').removeClass('modal-open');
        }, 1);

        $timeout(function () {
            $scope.doneLoading = false;
        }, 1);

        statisticsData.getFilterHash(diagnoses, null, null, function (selectionHash) {
            var path = $location.path();
            var newPath = path.replace(/\/[^\/]+$/gm, "/" + selectionHash);
            $location.path(newPath);
        }, function () {
            throw new Error("Failed to get filter hash value");
        });
    };

    this.isShowingVerksamhet = function($location) {
        return $location.path().indexOf("/verksamhet/") === 0;
    }

};


