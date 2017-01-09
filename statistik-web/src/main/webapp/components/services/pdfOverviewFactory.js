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

angular.module('StatisticsApp')
    .factory('pdfOverviewFactory',
        /** @ngInject */
        function($window, pdfFactory, $timeout) {
        'use strict';

        function _printOverview($scope, charts) {
            $scope.generatingPdf = true;
            var headers = {
                header: $scope.viewHeader,
                subHeader: $scope.subTitle  + ' ' + $scope.subTitlePeriod
            };

            var pdfDoneCallback = function() {
                $timeout(function() {
                    $scope.generatingPdf = false;
                });
            };

            _generateOverview(headers, charts, $scope.activeEnhetsFilters, $scope.activeDiagnosFilters, $scope.activeSjukskrivningslangdsFilters, $scope.activeAldersgruppFilters, pdfDoneCallback);
        }

        function _generateOverview(headers, charts, enhetsFilter, diagnosFilter, sjukskrivningslangdFilter, aldersgruppFilter,  pdfDoneCallback) {
            var content = [];

            pdfFactory.factory.header(content, headers);

            pdfFactory.factory.filter(content, 'Sammanställning av diagnosfilter', diagnosFilter);
            pdfFactory.factory.filter(content, 'Sammanställning av enheter', enhetsFilter);
            pdfFactory.factory.filter(content, 'Sammanställning av sjukskrivningslängdsfilter', sjukskrivningslangdFilter);
            pdfFactory.factory.filter(content, 'Sammanställning av åldersgruppfilter', aldersgruppFilter);

            angular.forEach(charts, function(chart) {
                if (angular.isArray(chart)) {
                    var columns = [];

                    angular.forEach(chart, function(chartChild) {
                        columns.push(_addBorder(_getOverviewChart(chartChild), chartChild));
                    });

                    content.push({
                        columns: columns
                    });
                }
                else {
                    content.push(_addBorder(_getOverviewChart(chart), chart));
                }
            });

            pdfFactory.factory.create(content, 'overview', pdfDoneCallback);
        }

        function _addBorder(content, chart) {
            var value = {
                table: {
                    dontBreakRows: 1,
                    headerRows: 1,
                    body: [[{
                        stack: content,
                        margin: [5, 5, 5, 5]
                    }]]
                },
                style: 'tableBorder',
                layout: {
                    hLineWidth: function(/*i, node*/) {
                        return 1;
                    },
                    vLineWidth: function(/*i, node*/) {
                        return 1;
                    },
                    hLineColor: function(/*i, node*/) {
                        return 'lightgray';
                    },
                    vLineColor: function(/*i, node*/) {
                        return 'lightgray';
                    }
                }
            };

            if (chart.displayWidth) {
                value.width = chart.displayWidth + 50;
            }

            if (chart.pageBreak) {
                value.pageBreak = 'after';
            }


            return value;
        }

        function _getOverviewChart(chart) {

            var content = [];
            content.push({text: chart.title, style: 'chartheader'});

            if (chart.chartDescription) {
                var chartDescriptions = [];

                angular.forEach(chart.chartDescription, function(descirption) {
                    chartDescriptions.push([
                        {text: descirption.header, style: 'chartDescHeader', alignment: 'center'},
                        {text: descirption.text, style: 'chartDescText', alignment: 'center'}
                    ]);
                });

                content.push({columns: chartDescriptions});
            }

            var columns = [];

            if (chart.chart) {
                if (angular.isArray(chart.chart)) {
                    angular.forEach(chart.chart, function(c) {
                        columns.push(_getChartContent(chart, c));
                    });
                }
                else {
                    columns.push(_getChartContent(chart, chart.chart));
                }
            }

            if (chart.genderImage) {
                columns.push(_getGenderImageContent(chart));
            }

            if (chart.countryChart) {
                columns.push(_getSwedenImageChart(chart));
            }

            if (chart.table) {
                var table = _getTable(chart.table.header, chart.table.data);
                table.margin = [20, 10, 0, 0];
                columns.push(table);
            }

            var columnsObject = {columns: columns};

            content.push(columnsObject);

            return content;
        }

        function _getSwedenImageChart(config) {
            var content = [];
            content.push({
                image: _getSwedenImage(),
                margin: [20, 0, 0, 0]
            });

            var chart = _getChartContent(config, config.countryChart);
            chart.absolutePosition =  {x: 10, y: 25};
            content.push(chart);

            return content;
        }

        function _getGenderImageContent(config) {
            var content = [];
            content.push({
                image: _getGenderImage()
            });

            content.push({
                absolutePosition: {x: 35, y: 65},
                text: config.female,
                style: 'genderImageText'
            });

            content.push({
                absolutePosition: {x: 125, y: 65},
                text: config.male,
                style: 'genderImageText'
            });

            return content;
        }

        function _getChartContent(config, chart) {
            return {
                image: pdfFactory.factory.chart(chart, config.width, config.height, config.showLegend || false),
                width: config.displayWidth ? config.displayWidth : config.width
            };
        }

        function _getTable(headerRows, data) {
            var body = [];
            var numberOfHeaderRows = 1;

            _addTableHeaders(body, headerRows);

            angular.forEach(data, function(row) {
                var rowData = [];

                var first = true;

                angular.forEach(row, function(item) {

                    rowData.push({
                        text: first ? ' ': item +'',
                        fillColor: first ? item : '',
                        alignment: 'right'
                    });

                    first = false;
                });

                body.push(rowData);
            });

            return pdfFactory.factory.table(numberOfHeaderRows, body);
        }

        function _addTableHeaders(body, headerRows) {
            var header = [];

            angular.forEach(headerRows, function(headerRow) {
                header.push({
                    text: headerRow,
                    style: 'tableHeader'
                });
            });

            body.push(header);
        }

        function _getGenderImage() {
            /* jshint ignore:start */
            return 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAALMAAACpCAYAAABgWQwOAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyRpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoTWFjaW50b3NoKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDo5MTNFRTMzMzIzQzgxMUUzQjZBRkZFMDkwMThEQTI0RSIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDo5MTNFRTMzNDIzQzgxMUUzQjZBRkZFMDkwMThEQTI0RSI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOjkxM0VFMzMxMjNDODExRTNCNkFGRkUwOTAxOERBMjRFIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOjkxM0VFMzMyMjNDODExRTNCNkFGRkUwOTAxOERBMjRFIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+oPKzsAAACkhJREFUeNrsnQ+M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';
            /* jshint ignore:end */
        }

        function _getSwedenImage() {
            /* jshint ignore:start */
            return 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAH8AAAEsCAYAAADq0Gz+AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyZpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMDE0IDc5LjE1Njc5NywgMjAxNC8wOC8yMC0wOTo1MzowMiAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTQgTWFjaW50b3NoIiB4bXBNTTpJbnN0YW5jZUlEPSJ4bXAuaWlkOjQ1RjlCMzZGNDdFNjExRTQ5RTREQjk1ODZDRDM2RDBEIiB4bXBNTTpEb2N1bWVudElEPSJ4bXAuZGlkOjQ1RjlCMzcwNDdFNjExRTQ5RTREQjk1ODZDRDM2RDBEIj4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6NDVGOUIzNkQ0N0U2MTFFNDlFNERCOTU4NkNEMzZEMEQiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6NDVGOUIzNkU0N0U2MTFFNDlFNERCOTU4NkNEMzZEMEQiLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz6dYL21AAAVoklEQVR42uyd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';
            /* jshint ignore:end */
        }

        //The public api of this factory
        return {
            printOverview: _printOverview
        };
    }
);
