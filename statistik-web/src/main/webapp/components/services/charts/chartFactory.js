/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of statistik (https://github.com/sklintyg/statistik).
 *
 * statistik is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * statistik is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/* globals Highcharts */
angular.module('StatisticsApp').factory('chartFactory',
    /** @ngInject */
    function(COLORS, _, ControllerCommons, $window, AppModel) {
    'use strict';

        var labelFormatter = function(maxWidth) {

            return function() {
                //If the label is more than 30 characters then cut the text and add ellipsis
                var numberOfChars = maxWidth;

                if (this.isFirst) {
                    numberOfChars = maxWidth-10;
                }

                return this.value.length > numberOfChars ? '<span title="'+this.value +'">' + this.value.substring(0, numberOfChars) + '...</span>' : this.value;
            };
        };

        var getHighChartConfigBase = function(chartCategories, chartSeries, doneLoadingCallback, overview, percentChart, stacked) {

            var hasSexSet = isSexSetOnChartSeries(chartSeries);

            var options = {
                chart : {
                    renderTo : 'chart1',
                    backgroundColor : null, //transparent
                    plotBorderWidth: 1,
                    marginLeft: overview ? null : 80
                },
                title: {
                    text: null,
                    style: {
                        fontSize: '16px'
                    }
                },
                subtitle : {
                    text : (percentChart ? 'Andel sjukfall' : 'Antal sjukfall'),
                    align: 'left',
                    style: {
                        color: '#008391',
                        fontWeight: 'bold',
                        fontSize: '12px'
                    },
                    margin: 7
                },
                legend : {
                    align : 'left',
                    borderWidth : 0,
                    itemStyle: {
                        color: '#008391',
                        fontWeight: 'bold',
                        textOverflow: 'ellipsis',
                        overflow: 'hidden',
                        width: '400px'
                    }
                },
                xAxis : {
                    labels : {
                        rotation : 320,
                        align : 'right',
                        style: {
                            whiteSpace: 'pre',
                            width: '200px'
                        },
                        useHTML: false,
                        formatter: labelFormatter(30),
                        step: 1
                    },
                    categories : _.map(chartCategories, function(category) {
                        var name = ControllerCommons.htmlsafe(category.name);
                        return category.marked ? '<b>' + name + '</b>' : name;
                    }),
                    title: {
                        align: 'high',
                        style: {
                            color: '#008391',
                            fontWeight: 'bold'
                        }
                    }
                },
                yAxis : {
                    allowDecimals : false,
                    min : 0,
                    title : {
                        text : null
                    },
                    labels : {
                        formatter : function() {
                            return ControllerCommons.makeThousandSeparated(this.value) + (percentChart ? ' %' : '');
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
                    url: AppModel.get().highchartsExportUrl
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
                        showInLegend : true,
                        stacking: null
                    },
                    column : {
                        showInLegend : true,
                        stacking: percentChart ? 'percent' : (stacked ? 'normal' : null)
                    },
                    series: {
                    },
                    area : {
                        lineColor : '#666666',
                        lineWidth : 1,
                        marker : {
                            enabled : false,
                            symbol : 'circle'
                        },
                        showInLegend : true,
                        stacking: percentChart ? 'percent' : 'normal'
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
                    },
                    pointFormatter: function() {
                        var value = percentChart ?
                        Highcharts.numberFormat(this.percentage, 0, ',') + ' %' :
                            ControllerCommons.makeThousandSeparated(this.y);
                        return this.series.name + ': <nobr><b>' + value + '</b></nobr><br/>';
                    }
                },
                credits : {
                    enabled : false
                },
                series : _.map(chartSeries, function (series) {
                    //This enables the marker for series with single data points
                    if (series.data.length === 1) {
                        if (series.marker) {
                            series.marker.enabled = true;
                        } else {
                            series.marker = {enabled: true};
                        }
                    }

                    if (stacked && hasSexSet) {
                        if(series.sex === null) {
                            series.showInLegend = false;
                            series.visible = false;
                        }
                    }

                    return series;
                })
            };

            if (doneLoadingCallback) {
                options.chart.events = { load: doneLoadingCallback };
            }

            return options;
        };

        var exportChart = function(chart, chartName, title) {
            if (!chart || angular.equals({}, chart)) {
                return;
            }

            var options = {filename: ControllerCommons.getFileName(chartName)};
            var extendedChartOptions = {};
            if (chart.series.length <= 10) {
                extendedChartOptions.legend = { enabled: true };
                var yMax = chart.yAxis[0].max;
                var chartTickInterval = chart.yAxis[0].tickInterval;
                extendedChartOptions.yAxis = { min: 0, max: yMax, endOnTick: false, tickInterval: chartTickInterval };
            }
            extendedChartOptions.chart = {
                height: 400,
                width: 600,
                marginLeft: 90,
                xAxis : {
                    labels: {
                        formatter: labelFormatter(25)
                    }
                }
            };

            if (title) {
                extendedChartOptions.title = {
                    text: title
                };
                extendedChartOptions.chart.backgroundColor = '#FFFFFF';
            }
            try {
                chart.exportChart(options, extendedChartOptions);
            } catch (e) {
                $window.alert('Diagrammet kunde inte exporteras. Testa att applicera ett filter för att minska datamängden och försök igen.');
            }
        };

        function isSexSetOnChartSeries(chartSeries) {
            var maleSeries = _.find(chartSeries, function(series) {
                return series.sex === 'MALE';
            });

            var femaleSeries = _.find(chartSeries, function(series) {
                return series.sex === 'FEMALE';
            });

            return maleSeries && femaleSeries? true : false;
        }

        var showInLegend = function(series, index) {
            return series[index].options.showInLegend;
        };

        var toggleSeriesVisibility = function toggleSeriesVisibility(series) {
            if (series.visible) {
                series.hide();
            } else {
                series.show();
            }
        };

        var addColor = function (rawData) {
            var colorSelector = 0, maleColorSelector = 0, femaleColorSelector = 0;

            var colors = COLORS.other,
                maleColor = COLORS.male,
                femaleColor = COLORS.female;

            _.each(rawData, function (data) {
                if (data.sex === 'MALE') {
                    data.color = maleColor[maleColorSelector++];
                } else if (data.sex === 'FEMALE') {
                    data.color = femaleColor[femaleColorSelector++];
                } else {
                    if(colorSelector === colors.length) {
                        //Begin anew with colors array
                        colorSelector = 0;
                    }
                    data.color = colors[colorSelector++];
                }

            });
            return rawData;
        };

        var setColorToTotalCasesSeries = function (series) {
            for (var i = 0; i < series.length; i++) {
                if (series[i].sex === null) {
                    series[i].color = COLORS.total;
                    break;
                }
            }
        };

        //This is the public api accessible to customers of this factory
        return {
            addColor: addColor,
            setColorToTotalCasesSeries: setColorToTotalCasesSeries,
            getHighChartConfigBase: getHighChartConfigBase,
            exportChart: exportChart,
            showInLegend: showInLegend,
            toggleSeriesVisibility: toggleSeriesVisibility
        };
    });
