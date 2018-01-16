/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
    function(COLORS, _, ControllerCommons, $window, $filter) {
    'use strict';

        var labelFormatter = function(maxWidth, sameLengthOnAll) {

            return function() {
                //If the label is more than 30 characters then cut the text and add ellipsis
                var numberOfChars = maxWidth;

                if (!sameLengthOnAll && this.isFirst) {
                    numberOfChars = maxWidth - 10;
                }

                return _formatter(this.value, numberOfChars);
            };
        };

        function _formatter(value, numberOfChars) {
            var text = value.length > numberOfChars ? value.substring(0, numberOfChars) + '...' : value;

            return '<span title="' + value + '">' + $filter('highlightWords')(text) + '</span>';
        }

        function _getMaxLength(maxLength) {
            return maxLength ? maxLength : 30;
        }

        function getTextWidth(container, text) {
            //Temporary add, measure and remove the chip's html equivalent.
            var elem = $('<span class="temp-highcharts-label">' + text + '</span>');
            container.append(elem);
            var width = elem.outerWidth(true);
            elem.remove();
            return width;
        }

        function _getCategoryLength(chartCategories, maxLength) {
            var categoryLength = 0;
            var container = $('.collapsible-panel-body');
            var labelLength = _getMaxLength(maxLength);

            angular.forEach(chartCategories, function(category) {
                var length = getTextWidth(container, _formatter(category.name, labelLength));

                if (categoryLength < length) {
                    categoryLength = length;
                }
            });

            return categoryLength;
        }

        function _getLabelHeight(chartCategories, verticalLabel, maxLength) {
            if (verticalLabel) {
                return _getCategoryLength(chartCategories, maxLength);
            }

            return 40;
        }

        function _getTooltip(overview, percentChart, unit, chartType) {

            var formatter;

            if (chartType === 'bubble') {
                formatter = function() {
                    var value = percentChart ?
                        Highcharts.numberFormat(this.percentage, 0, ',') + ' %' :
                        ControllerCommons.makeThousandSeparated(this.point.z);

                    return '<p class="tooltip-style">' +
                        '<nobr><b>' + value + '</b></nobr> ' + unit + ' för ' + this.series.name  +
                        '</p>';
                };
            } else {
                formatter = function() {
                    var value = percentChart ?
                        Highcharts.numberFormat(this.percentage, 0, ',') + ' %' :
                        ControllerCommons.makeThousandSeparated(this.y);

                    var title = this.x ? this.x : this.point.name;

                    if (overview) {
                        return '<p class="tooltip-style">' +
                            '<nobr><b>' + value + '</b></nobr> ' + unit + ' för ' + title +
                            '</p>';
                    }

                    return '<p class="tooltip-style">' +
                        '<span class="title">' + title + ':</span><br>' +
                        '<nobr><b>' + value + '</b></nobr> ' + unit + ' för ' + this.series.name +
                        '</p>';
                };
            }


            return {
                backgroundColor : '#fff',
                borderWidth : 2,
                padding: 1,
                style: {},
                useHTML: true,
                formatter: formatter
            };
        }

        /**
         * Hämtar en config för ett highcharts diagram
         *
         *
         * Options består av:
         *
         * categories: array,
         * series: array,
         * type: string,
         * doneLoadingCallback: function,
         * percentChart: boolean,
         * stacked: boolean,
         * verticalLabel: boolean,
         * labelMaxLength: number,
         * overview: boolean,
         * renderTo: string
         * unit: string
         *
         *
         * @param options
         * @returns {}  // chart object
         */
        var getHighChartConfigBase = function(options) {

            var hasSexSet = isSexSetOnChartSeries(options.series);
            var labelHeight = _getLabelHeight(options.categories, options.verticalLabel, options.labelMaxLength);

            var config = {
                chart : {
                    animation: false,
                    renderTo : options.renderTo ? options.renderTo : 'chart1',
                    type: options.type,
                    backgroundColor : null, //transparent
                    plotBorderWidth: 1,
                    marginLeft: options.overview ? null : 80,
                    height: 360 + labelHeight,
                    marginBottom: options.verticalLabel ? labelHeight + 25 : null
                },
                title: {
                    text: null,
                    style: {
                        fontSize: '16px'
                    }
                },
                subtitle : {
                    text : (options.percentChart ? 'Andel ' + options.unit : 'Antal ' + options.unit),
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
                    symbolRadius: 0,
                    y: options.verticalLabel ? 15 : 0,
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
                        rotation : options.verticalLabel ? -90 : 320,
                        align : 'right',
                        style: {
                            whiteSpace: 'pre',
                            width: '200px'
                        },
                        useHTML: true,
                        formatter: labelFormatter(_getMaxLength(options.labelMaxLength), options.verticalLabel),
                        step: 1
                    },
                    categories : _.map(options.categories, function(category) {
                        var name = ControllerCommons.htmlsafe(category.name);
                        return category.marked ? '<b>' + name + '</b>' : name;
                    })
                },
                yAxis : {
                    allowDecimals : false,
                    min : 0,
                    minRange : 0.1,
                    title : {
                        text : null
                    },
                    labels : {
                        formatter : function() {
                            return ControllerCommons.makeThousandSeparated(this.value) + (options.percentChart ? ' %' : '');
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
                    fallbackToExportServer: false
                },
                plotOptions : {
                    line : {
                        animation: false,
                        softThreshold: false,
                        allowPointSelect : false,
                        marker : {
                            enabled : false,
                            symbol : 'circle'
                        },
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
                        animation: false,
                        softThreshold: false,
                        showInLegend : true,
                        stacking: options.percentChart ? 'percent' : (options.stacked ? 'normal' : null)
                    },
                    series: {
                    },
                    area : {
                        animation: false,
                        lineColor : '#666666',
                        lineWidth : 1,
                        marker : {
                            enabled : false,
                            symbol : 'circle'
                        },
                        showInLegend : true,
                        stacking: options.percentChart ? 'percent' : 'normal'
                    },
                    pie : {
                        animation: false,
                        dataLabels : {
                            enabled : false
                        },
                        showInLegend : false
                    }
                },
                tooltip : _getTooltip(options.overview, options.percentChart, options.unit, options.type),
                credits : {
                    enabled : false
                },
                series : _.map(options.series, function (series) {
                    //This enables the marker for series with single data points
                    if (series.data.length === 1) {
                        if (series.marker) {
                            series.marker.enabled = true;
                        } else {
                            series.marker = {enabled: true};
                        }
                    }

                    if (options.stacked && hasSexSet) {
                        if(series.sex === null) {
                            series.showInLegend = false;
                            series.visible = false;
                        }
                    }

                    return series;
                })
            };

            if (options.doneLoadingCallback) {
                config.chart.events = { load: options.doneLoadingCallback };
            }

            return config;
        };

        var getChartExportFileName = function(statisticsLevel, gender) {
            return ControllerCommons.getExportFileName(statisticsLevel, gender);
        };

        var exportChart = function(chart, statisticsLevel, title, gender) {
            if (!chart || angular.equals({}, chart)) {
                return;
            }

            var chartHeight = chart.options.chart.height ? chart.options.chart.height : 400;
            var options = {filename: getChartExportFileName(statisticsLevel, gender)};
            var extendedChartOptions = {};
            if (chart.series.length <= 10) {
                extendedChartOptions.legend = { enabled: true };
            }
            var yMax = chart.yAxis[0].max;
            var chartTickInterval = chart.yAxis[0].tickInterval;
            extendedChartOptions.yAxis = { min: 0, max: yMax, endOnTick: false, tickInterval: chartTickInterval };

            extendedChartOptions.chart = {
                height: chartHeight,
                width: 600,
                marginLeft: 90
            };

            if (title) {
                extendedChartOptions.title = {
                    text: title
                };
                extendedChartOptions.chart.backgroundColor = '#FFFFFF';
            }
            try {
                chart.exportChartLocal(options, extendedChartOptions);
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
            if (series && series.length > index) {
                return series[index].options.showInLegend;
            }

            return false;
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
                // continue if color is set
                if (data.color) {
                    return;
                }

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
            toggleSeriesVisibility: toggleSeriesVisibility,
            getChartExportFileName: getChartExportFileName
        };
    });
