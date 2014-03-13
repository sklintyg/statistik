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
        var color = [ "#fbb10c", "#2ca2c6", "#B0B0B0", "#12BC3A", "#9c734d", "#D35ABB", "#4A4A4A" ];
        var maleColor = [ "#008391", "#00CDE3" ];
        var femaleColor = [ "#EA8025", "#FFC18C" ];
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
    
    this.htmlsafe = function(string) {
        return string.replace(/&/g, '&amp;').replace(/</g, '&lt;');
    };
    
    this.isNumber = function(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    };
    
    this.makeThousandSeparated = function(input) {
        return ControllerCommons.isNumber(input) ? input.toString().split('').reverse().join('').match(/.{1,3}/g).join('\u00A0').split('').reverse().join('') : input;
    };
    
    this.map = function(arr, func) {
        var r=[];
        for(var i=0; i<arr.length; i++) {
            r.push(func(arr[i]));
        }
        return r;
    }
    
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
    }
    
    this.exportChart = function(chart, chartName, legendLayout) {
        var options = {filename: ControllerCommons.getFileName(chartName)};
        var chartOptions = { legend: { enabled: true } };
        if (legendLayout) {
            chartOptions.legend.layout = legendLayout;
        }
        chart.exportChart(options, chartOptions);
    }

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
                    align : 'right'
                },
                categories : ControllerCommons.map(chartCategories, function(name) {
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
                borderWidth : 2
            },
            credits : {
                enabled : false
            },
            series : chartSeries
        };
    };

};
