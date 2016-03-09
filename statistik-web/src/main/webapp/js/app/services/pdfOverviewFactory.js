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

            _generateOverview(headers, charts);
        }

        function _generateOverview(headers, charts) {
            var content = [];


            pdfFactory.factory.header(content, headers);

            angular.forEach(charts, function(chart) {
                if (angular.isArray(chart)) {
                    var columns = [];

                    columns.push(_getOverviewChart(chart[0]));
                    columns.push(_getOverviewChart(chart[1]));

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

            var columns = [];

            columns.push({
                image: pdfFactory.factory.chart(chart.chart, chart.width, chart.height),
                width: chart.displayWidth ? chart.displayWidth : chart.width
            });


            if (chart.table) {
                var table = _getTable(chart.table.header, chart.table.data);
                table.margin = [20, 20, 0, 0];
                columns.push(table);
            }

            content.push({ columns: columns});

            return content;
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

        //The public api of this factory
        return {
            printOverview: _printOverview
        };
    }]);
