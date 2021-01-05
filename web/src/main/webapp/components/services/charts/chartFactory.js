/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/* globals Highcharts */
angular.module('StatisticsApp').factory('chartFactory',
    /** @ngInject */
    function(COLORS, CATEGORY_TO_HIDE, _, ControllerCommons, $window, $filter, $log, StaticData) {
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
        var textToFormat;
        var tooltip;

        var isObject = angular.isObject(value);

        if (isObject) {
          textToFormat = value.name;
          tooltip = value.tooltip;
        } else {
          textToFormat = value;
          tooltip = value;
        }

        var text = textToFormat.length > numberOfChars ? textToFormat.substring(0, numberOfChars) + '...' : textToFormat;

        var filteredText = $filter('highlightWords')(text);

        var skipDefaultToolTip = filteredText !== text;

        if (isObject && value.marked) {
          filteredText = '<b>' + filteredText + '</b>';
        }

        if (skipDefaultToolTip) {
          return filteredText;
        }

        return '<span data-original-title="' + tooltip + '" data-placement="auto right" data-toggle="tooltip">' + filteredText + '</span>';
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

      function _getTooltip(overview, percentChart, unit, chartType, usingAndel, maxWidth) {

        var formatter;

        if (chartType === 'bubble') {
          formatter = function() {
            var value = percentChart ?
                Highcharts.numberFormat(this.percentage, 0, ',') + ' %' :
                ControllerCommons.makeThousandSeparated(this.point.z);
            return '<b>' + value + '</b> ' + unit + ' för ' + this.series.name;
          };
        } else {
          formatter = function() {
            var value = percentChart ?
                Highcharts.numberFormat(this.percentage, 0, ',') + ' %' :
                ControllerCommons.makeThousandSeparated(this.y) + (usingAndel ? ' %' : '');

            var title = this.x ? this.x : this.point.name;

            if (angular.isObject(title)) {
              title = title.oldName ? title.oldName : title.name;
            }

            if (overview) {
              return '<b>' + value + '</b> ' + unit + ' för ' + title;
            }

            return title + ':<br><b>' + value + '</b> ' + unit + ' för ' + this.series.name;
          };
        }

        return {
          hideDelay: 500,
          backgroundColor: '#fff',
          borderWidth: 2,
          padding: 9,
          style: {
            whiteSpace: 'nowrap',
            width: '600px'
          },
          responsiveWidthPercentage: maxWidth || null,
          useHTML: false,
          outside: typeof (maxWidth) === 'undefined',
          formatter: formatter
        };
      }

      function onChartRender() {
        /* jshint ignore:start */
        this.tooltip.update({
          style: {
            width: Math.floor(0.01 * this.tooltip.options.responsiveWidthPercentage * this.chartWidth) + 'px'
          }
        });
        /* jshint ignore:end */
      }

      function processCategories(categories) {

        // Ta bort kategorienamnet om det bara finns Totalt
        if (categories.length === 1 && categories[0].name === CATEGORY_TO_HIDE) {
          return [{
            name: '',
            marked: false,
            oldName: categories[0].name
          }];
        }

        return _.map(categories, function(category) {
          var tooltip = category.tooltip ? category.tooltip : category.name;

          return {
            name: ControllerCommons.htmlsafe(category.name),
            tooltip: ControllerCommons.htmlsafe(tooltip),
            marked: category.marked
          };
        });
      }

      function _addChartEvent(config, eventName, callback) {
        if (typeof (config.chart.events) !== 'object') {
          config.chart.events = {};
        }
        config.chart.events[eventName] = callback;
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
          chart: {
            animation: false,
            renderTo: options.renderTo ? options.renderTo : 'chart1',
            type: options.type,
            backgroundColor: null, //transparent
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
          subtitle: {
            text: (options.percentChart ? 'Andel ' + options.unit : 'Antal ' + options.unit),
            align: 'left',
            style: {
              color: '#008391',
              fontWeight: 'bold',
              fontSize: '12px'
            },
            margin: 7
          },
          legend: {
            align: 'left',
            borderWidth: 0,
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
          xAxis: {
            labels: {
              rotation: options.verticalLabel ? -90 : 320,
              align: 'right',
              style: {
                whiteSpace: 'pre',
                width: (_getMaxLength(options.labelMaxLength) * 7) + 'px'
              },
              useHTML: true,
              formatter: labelFormatter(_getMaxLength(options.labelMaxLength), options.verticalLabel),
              step: 1
            },
            categories: processCategories(options.categories)
          },
          yAxis: {
            allowDecimals: false,
            min: 0,
            minRange: 0.1,
            title: {
              text: null
            },
            labels: {
              formatter: function() {
                return ControllerCommons.makeThousandSeparated(this.value) + (options.percentChart || options.usingAndel ? ' %' : '');
              }
            },
            plotLines: [{
              value: 0,
              width: 1,
              color: '#808080'
            }]
          },
          exporting: {
            enabled: false,
            fallbackToExportServer: false
          },
          plotOptions: {
            line: {
              animation: false,
              softThreshold: false,
              allowPointSelect: false,
              marker: {
                enabled: false,
                symbol: 'circle'
              },
              dataLabels: {
                enabled: false
              },
              events: {
                legendItemClick: function() { // This function removes interaction for plot and legend-items
                  return false;
                }
              },
              showInLegend: true,
              stacking: null
            },
            column: {
              animation: false,
              softThreshold: false,
              showInLegend: true,
              stacking: options.percentChart ? 'percent' : (options.stacked ? 'normal' : null)
            },
            series: {},
            area: {
              animation: false,
              lineColor: '#666666',
              lineWidth: 1,
              marker: {
                enabled: false,
                symbol: 'circle'
              },
              showInLegend: true,
              stacking: options.percentChart ? 'percent' : 'normal'
            },
            pie: {
              animation: false,
              dataLabels: {
                enabled: false
              },
              showInLegend: false
            }
          },
          tooltip: _getTooltip(options.overview, options.percentChart, options.unit, options.type, options.usingAndel,
              options.maxWidthPercentage),
          credits: {
            enabled: false
          },
          series: _.map(options.series, function(series) {
            //This enables the marker for series with single data points
            if (series.data.length === 1) {
              if (series.marker) {
                series.marker.enabled = true;
              } else {
                series.marker = {enabled: true};
              }
            }

            if (options.stacked && hasSexSet) {
              if (series.sex === null) {
                series.showInLegend = false;
                series.visible = false;
              }
            }

            return series;
          })
        };

        if (options.doneLoadingCallback) {
          _addChartEvent(config, 'load', options.doneLoadingCallback);
        }

        if (typeof (options.maxWidthPercentage) === 'number') {
          _addChartEvent(config, 'render', onChartRender);
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
          extendedChartOptions.legend = {enabled: true};
        }
        var yMax = chart.yAxis[0].max;
        var chartTickInterval = chart.yAxis[0].tickInterval;
        extendedChartOptions.yAxis = {min: 0, max: yMax, endOnTick: false, tickInterval: chartTickInterval};

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
          $log.error(e);
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

        return maleSeries && femaleSeries ? true : false;
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

      var addColor = function(rawData) {
        var colorSelector = 0, maleColorSelector = 0, femaleColorSelector = 0;

        var colors = COLORS.other,
            maleColor = COLORS.male,
            femaleColor = COLORS.female;

        _.each(rawData, function(data) {
          // continue if color is set
          if (data.color) {
            return;
          }

          if (data.sex === 'MALE') {
            data.color = maleColor[maleColorSelector++];
          } else if (data.sex === 'FEMALE') {
            data.color = femaleColor[femaleColorSelector++];
          } else {
            if (colorSelector === colors.length) {
              //Begin anew with colors array
              colorSelector = 0;
            }
            data.color = colors[colorSelector++];
          }

        });
        return rawData;
      };

      var setColorToTotalCasesSeries = function(series) {
        for (var i = 0; i < series.length; i++) {
          if (series[i].sex === null && series[i].name === 'Totalt') {
            series[i].color = COLORS.total;
            break;
          }
        }
      };

      var addCategoryIntygTooltip = function(categories) {

        var tooltips = StaticData.get().intygTooltips;

        return _.map(categories, function(category) {
          var name = tooltips[category.name];
          var tooltip = category.name;

          if (name) {
            category.tooltip = tooltip;
            category.name = name;
          }

          return category;
        });
      };

      //This is the public api accessible to customers of this factory
      return {
        addColor: addColor,
        setColorToTotalCasesSeries: setColorToTotalCasesSeries,
        getHighChartConfigBase: getHighChartConfigBase,
        exportChart: exportChart,
        showInLegend: showInLegend,
        toggleSeriesVisibility: toggleSeriesVisibility,
        getChartExportFileName: getChartExportFileName,
        addCategoryIntygTooltip: addCategoryIntygTooltip
      };
    });
