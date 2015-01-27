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

    this.addColor = function(rawData) {
        var color = [ "#E40E62", "#00AEEF", "#57843B", "#002B54", "#F9B02D", "#724E86", "#34655e", "#8A6B61" ];
        var maleColor = [ "#008391", "#90cad0" ];
        var femaleColor = [ "#EA8025", "#f6c08d" ];
        var colorSelector = 0;
        var maleColorSelector = 0;
        var femaleColorSelector = 0;
        for (var i = 0; i < rawData.length; i++) {
            if (rawData[i].sex === "Male") {
                rawData[i].color = maleColor[maleColorSelector++];
            } else if (rawData[i].sex === "Female") {
                rawData[i].color = femaleColor[femaleColorSelector++];
            } else {
                rawData[i].color = color[colorSelector++];
            }
        }
        return rawData;
    };
 
    var addBwColor = function(series, chartType) {
    	var patterns = [ '/img/print-patterns/1.png', '/img/print-patterns/2.png', '/img/print-patterns/3.png', '/img/print-patterns/4.png', '/img/print-patterns/9.png', '/img/print-patterns/6.png', '/img/print-patterns/7.png' ];
        var dashStyles = [ 'shortdashdotdot', 'dashdot', 'dot', 'longdash', 'shortdot', 'solid', 'shortdash', 'shortdashdot', 'dash', 'longdashdot', 'longdashdotdot' ];

        for (var i = 0; i < series.length; i++) {
            series[i].animation = false;
            if (chartType === "bar" || chartType === "area") {
                series[i].color = {
                        pattern: patterns[i % patterns.length],
                        width: 6,
                        height: 6
                    };
                series[i].fillColor = {
                        pattern: patterns[i % patterns.length],
                        width: 6,
                        height: 6
                };
            } else if (chartType === "line") {
                series[i].color = 'black';
                series[i].dashStyle = dashStyles[i % dashStyles.length];
            }
        }
        return series;
    };
    
    this.setupSeriesForDisplayType = function(setupForBwPrint, series, chartType) {
        return setupForBwPrint ? addBwColor(series, chartType) : ControllerCommons.addColor(series);
    };
    
    this.printAndCloseWindow = function($timeout, $window) {
        $( document ).ready( function(){
            $timeout(function() {
                $window.print();
                if ('afterprint' in window) {
                    $(window).on('afterprint', function(){$window.close();});
                } else {
                    $timeout(function() {
                        $window.close();
                    }, 100);
                }
            }, 3000);
          } );
    };

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

    this.getHighChartConfigBase = function(chartCategories, chartSeries) {
        return {
            chart : {
                renderTo : 'chart1',
                backgroundColor : null, //transparent
                plotBorderWidth: 1
            },
            title : {
                text : ''
            },
            legend : {
                align : 'top left',
                x : 80,
                y : 0,
                borderWidth : 0
            },
            xAxis : {
                labels : {
                    rotation : 310,
                    align : 'right',
                    style: {
                    	whiteSpace: 'pre',
                    	width: '120px'
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
        if (basedOnAlreadyInText) {
            return enhetsCount && enhetsCount != 1 ? " och " + enhetsCount + " enheter " : " ";
        }
        return enhetsCount && enhetsCount != 1 ? " baserat på " + enhetsCount + " enheter " : " ";
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

    this.print = function(bwPrint, rootScope, windowParam) {
        var printQuery = bwPrint ? "printBw=true" : "print=true";
        var prefixChar = rootScope.queryString ? "&" : "?";
        windowParam.open(windowParam.location + prefixChar + printQuery);
    }

};


