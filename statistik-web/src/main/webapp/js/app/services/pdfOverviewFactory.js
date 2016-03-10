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
angular.module('StatisticsApp')
    .factory('pdfOverviewFactory', ['$window', 'pdfFactory', function($window, pdfFactory) {

        function _printOverview($scope, charts) {
            var headers = {
                header: $scope.viewHeader,
                subHeader: $scope.subTitle
            };

            if ($scope.verksamhetViewShowing) {
                headers.extraHeader = $scope.headerEnhetInfo;
            }

            _generateOverview(headers, charts);
        }

        function _generateOverview(headers, charts) {
            var content = [];

            pdfFactory.factory.header(content, headers);

            angular.forEach(charts, function(chart) {
                if (angular.isArray(chart)) {
                    var columns = [];

                    angular.forEach(chart, function(chartChild) {
                        columns.push(_getOverviewChart(chartChild));
                    });

                    content.push({
                        columns: columns
                    });
                }
                else {
                    content.push(_getOverviewChart(chart));
                }
            });

            pdfFactory.factory.create(content, 'overview');
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
                    ])
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

            if (chart.table) {
                var table = _getTable(chart.table.header, chart.table.data);
                table.margin = [20, 10, 0, 0];
                columns.push(table);
            }

            var columnsObject = {columns: columns};

            if (chart.pageBreak) {
                columnsObject.pageBreak = 'after';
            }

            content.push(columnsObject);

            return content;
        }

        function _getGenderImageContent(config) {
            var content = [];
            content.push({
                image: _getGenderImage()
            });

            content.push({
                absolutePosition: {x: 60, y: 185},
                text: config.female,
                style: 'genderImageText'
            });

            content.push({
                absolutePosition: {x: 150, y: 185},
                text: config.male,
                style: 'genderImageText'
            });

            return content;
        }

        function _getChartContent(config, chart) {
            return {
                image: pdfFactory.factory.chart(chart, config.width, config.height, false),
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
            return 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAALMAAACpCAYAAABgWQwOAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyRpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoTWFjaW50b3NoKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDo5MTNFRTMzMzIzQzgxMUUzQjZBRkZFMDkwMThEQTI0RSIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDo5MTNFRTMzNDIzQzgxMUUzQjZBRkZFMDkwMThEQTI0RSI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOjkxM0VFMzMxMjNDODExRTNCNkFGRkUwOTAxOERBMjRFIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOjkxM0VFMzMyMjNDODExRTNCNkFGRkUwOTAxOERBMjRFIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+oPKzsAAACkhJREFUeNrsnQ+M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';
        }

        //The public api of this factory
        return {
            printOverview: _printOverview
        };
    }]);
