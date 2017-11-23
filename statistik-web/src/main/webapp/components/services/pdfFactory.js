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

/* globals pdfMake, canvg, $ */
angular.module('StatisticsApp')
    .factory('pdfFactory',
        /** @ngInject */
        function($window, $timeout, thousandseparatedFilter, $location, _, MAX_ROWS_TABLE_PDF, messageService, $rootScope,
            ControllerCommons, sortableTableViewstate) {

            'use strict';

            function _getFileName(statisticsLevel) {
                return ControllerCommons.getExportFileName(statisticsLevel) + '.pdf';
            }

            function _print($scope, charts) {
                if (!charts || angular.equals({}, charts)) {
                    return;
                }
                $scope.generatingPdf = true;

                var headers = {
                    header: $scope.viewHeader,
                    subHeader: $scope.subTitle + ' ' + $scope.subTitlePeriod
                };
                var filename = _getFileName($scope.viewHeader);

                if (!angular.isArray(charts)) {
                    charts = [charts];
                }

                var pdfDoneCallback = function() {
                    $timeout(function() {
                        $scope.generatingPdf = false;
                    });
                };

                var table;
                if ($scope.useSpecialPrintTable) {

                    var isTableVisible = $scope.status.isTableOpen;
                    $scope.status.isTableOpen = true;

                    // Timeout needed for the table to be visible before proceeding.
                    $timeout(function() {
                        table = [];

                        var tableData = _updatePrintDataTable($scope.headerrows, sortableTableViewstate.getSortedRows());

                        angular.forEach(tableData, function(t) {
                            table.push({
                                header: t.headers,
                                data: formatTableData(t.rows),
                                hasMoreThanMaxRows: t.rows.length > MAX_ROWS_TABLE_PDF
                            });
                        });

                        $scope.status.isTableOpen = isTableVisible;

                        _generate(headers, table, charts, $scope.activeEnhetsFilters, $scope.activeDiagnosFilters, $scope.activeSjukskrivningslangdsFilters, $scope.activeAldersgruppFilters, filename, pdfDoneCallback);
                    });
                }
                else {
                    table = {
                        header: $scope.headerrows,
                        data: formatTableData(sortableTableViewstate.getSortedRows()),
                        hasMoreThanMaxRows: sortableTableViewstate.getSortedRows().length > MAX_ROWS_TABLE_PDF
                    };

                    _generate(headers, table, charts, $scope.activeEnhetsFilters, $scope.activeDiagnosFilters, $scope.activeSjukskrivningslangdsFilters, $scope.activeAldersgruppFilters, filename, pdfDoneCallback);
                }
            }

            function formatTableData(data) {
                var tableData = [];
                var tableRows = data.slice(0, MAX_ROWS_TABLE_PDF);
                angular.forEach(tableRows, function(row) {
                    var rowData = [];

                    angular.forEach(row.data, function(item) {
                        rowData.push(item.value);
                    });

                    row.data = rowData;

                    tableData.push(row);
                });
                return tableData;
            }

            function _generate(headers, table, charts, enhetsFilter, diagnosFilter, sjukskrivningslangdFilter, aldersgruppFilter, filename, pdfDoneCallback) {
                var content = [];

                _addHeader(content, headers);
                content.push(_convertChartsToImages(charts));
                _addListFilter(content, 'Sammanställning av enheter', enhetsFilter);
                _addListFilter(content, 'Sammanställning av diagnosfilter', diagnosFilter);
                _addListFilter(content, 'Sammanställning av sjukskrivningslängdsfilter', sjukskrivningslangdFilter);
                _addListFilter(content, 'Sammanställning av åldersgruppfilter', aldersgruppFilter);

                if (angular.isArray(table)) {
                    angular.forEach(table, function(t) {
                        if (t.hasMoreThanMaxRows) {
                            content.push({text: messageService.getProperty('table.warning.text'), style: 'tableWarning'});
                        }

                        var pdfTable = _getTable(t.header, t.data);
                        pdfTable.pageBreak = 'before';
                        content.push(pdfTable);

                    });
                } else {
                    if (table.hasMoreThanMaxRows) {
                        content.push({text: messageService.getProperty('table.warning.text'), style: 'tableWarning'});
                    }

                    content.push(_getTable(table.header, table.data));
                }

                _create(content, filename, pdfDoneCallback);
            }

            function _create(content, fileName, pdfDoneCallback) {
                _createPdf(content, fileName, pdfDoneCallback);
            }

            function _createPdf(content, fileName, pdfDoneCallback) {
                pdfMake.fonts = {
                    LiberationSans: {
                        normal: 'LiberationSans-Regular.ttf',
                        bold: 'LiberationSans-Bold.ttf',
                        italics: 'LiberationSans-Italic.ttf',
                        bolditalics: 'LiberationSans-BoldItalic.ttf'
                    }
                };

                var docDefinition = {
                    content: content,
                    footer: _getFooter,
                    pageSize: 'A4',
                    //pageOrientation: 'landscape',
                    styles: _getPdfStyle(),
                    defaultStyle: {
                        font: 'LiberationSans'
                    }
                };

                pdfMake.createPdf(docDefinition).getBase64(function(result) {

                    if (pdfDoneCallback) {
                        pdfDoneCallback();
                    }
                    var inputs = '<input type="hidden" name="pdf" value="'+result+'">';
                    inputs += '<input type="hidden" name="name" value="' + fileName + '">';
                    inputs += '<input type="hidden" name="url" value="' + $location.url() + '">';

                    $window.jQuery('<form action="/api/pdf/create" method="post">'+inputs+'</form>')
                        .appendTo('body').submit().remove();

                });
            }

            function _getPdfStyle() {
                return {
                    header: {
                        fontSize: 18,
                        bold: true,
                        margin: [0, 0, 0, 0]
                    },
                    subheader: {
                        fontSize: 14,
                        bold: true,
                        margin: [0, 5, 0, 10]
                    },
                    chartheader: {
                        fontSize: 12,
                        bold: true,
                        margin: [0, 5, 0, 10]
                    },
                    tableWarning: {
                        fontSize: 12,
                        bold: true,
                        margin: [0, 10, 0, 2]
                    },
                    table: {
                        fontSize: 9,
                        margin: [0, 10, 0, 5]
                    },
                    tableHeader: {
                        bold: true,
                        color: 'black'
                    },
                    tableBorder: {
                        margin: [0, 5, 0, 5]
                    },
                    footer: {
                        fontSize: 10,
                        margin: [0, 0, 20, 0]
                    },
                    filterheader: {
                        fontSize: 12,
                        bold: true,
                        margin: [0, 10, 0, 2]
                    },
                    chartDescHeader: {
                        fontSize: 16,
                        bold: true
                    },
                    chartDescText: {
                        margin: [15, 10, 15, 10]
                    },
                    genderImageText: {
                        fontSize: 20,
                        color: 'white',
                        bold: true
                    }
                };
            }

            function _addListFilter(content, rubrik, filter) {
                if (filter && filter.length > 0) {
                    content.push({text: rubrik, style: 'filterheader'});

                    content.push({
                        ul: filter.slice(0, filter.length)
                    });
                }
            }

            function _getFooter(currentPage, pageCount) {
                return {
                    alignment: 'right',
                    style: 'footer',
                    text: moment().format('YYYY-MM-DD') + '  Sida ' + currentPage.toString() + ' av ' + pageCount
                };
            }

            function _addHeader(content, headers) {
                content.push({ image: _getLogo(), width: 180});

                var headerText = [headers.header];

                content.push({ text: headerText, style: 'header'});
                content.push({ text: headers.subHeader, style: 'subheader' });
            }

            function _convertChartsToImages(charts) {
                var content = [];
                var width = 510;
                var chartWidth = 680;
                var chartHeight = 400;
                var boxHeight = 0;
                var numberOfCharts = charts.length;

                // Calculate legend height
                if (numberOfCharts > 1) {
                    var chart = charts[0];
                    var chartH = chart.options.chart.height;

                    var svg = _getSVG(chart, chartWidth, chartH, true);

                    var boxPosition = svg.indexOf('highcharts-legend-box');
                    var heightSearchString = 'height="';
                    var boxHeightStart = svg.indexOf(heightSearchString, boxPosition) + heightSearchString.length;
                    var boxHeightStop = svg.indexOf('"', boxHeightStart);
                    boxHeight = svg.substr(boxHeightStart, boxHeightStop - boxHeightStart);
                }

                angular.forEach(charts, function(chart, index) {
                    var showLegend = numberOfCharts === (index + 1);

                    var image = _getChart(chart, chartWidth, chartHeight, showLegend, false, boxHeight);

                    content.push({
                        image: image,
                        width: width
                    });
                });

                return content;
            }

            function _getSVG(chart, width, chartHeight, showLegend) {
                return chart.getSVG({
                    legend: {
                        enabled: showLegend,
                        itemStyle: {
                            width: '600px'
                        }
                    },
                    yAxis: { min: 0, max: chart.yAxis[0].max, endOnTick: false, tickInterval: chart.yAxis[0].tickInterval },
                    chart: {
                        height: chartHeight,
                        width: width
                    }
                });
            }

            function _getChart(chart, width, height, showLegend, useHeight, legendHeight) {
                var chartHeight;

                if (useHeight) {
                    chartHeight = height;
                } else {
                    chartHeight = chart.options.chart.height ? chart.options.chart.height : height;
                }

                var canvas = document.createElement('CANVAS');
                canvas.height = chartHeight;
                canvas.width = width;

                if (!angular.isDefined(showLegend)) {
                    showLegend = true;
                }

                // remove legend height from chart if not shown
                if (!showLegend && angular.isDefined(legendHeight)) {
                    chartHeight -= legendHeight;
                }

                var svg = _getSVG(chart, width, chartHeight, showLegend);

                canvg(canvas, svg);

                return canvas.toDataURL('image/png');
            }

            function _getTable(headerRows, data) {
                var body = [];
                var numberOfHeaderRows = headerRows.length;

                _addTableHeaders(body, headerRows);

                angular.forEach(data, function(row) {
                    var rowData = [row.name];

                    angular.forEach(row.data, function(item) {
                        rowData.push({
                            text: item+'',
                            alignment: 'right'
                        });
                    });

                    body.push(rowData);
                });

                return _getTableLayout(numberOfHeaderRows, body);
            }

            function _getTableLayout(numberOfHeaderRows, body) {
                return {
                    table: {
                        headerRows: numberOfHeaderRows,
                        body: body,
                        dontBreakRows: true
                    },
                    style: 'table',
                    layout: {
                        hLineWidth: function(/*i, node*/) {
                            return 0.5;
                        },
                        vLineWidth: function(/*i, node*/) {
                            return 0.5;
                        },
                        hLineColor: function(i) {
                            return (i === numberOfHeaderRows) ? 'gray' :'lightgray';
                        },
                        vLineColor: function(/*i, node*/) {
                            return 'lightgray';
                        }
                    }
                };
            }

            function _addTableHeaders(body, headerRows) {
                angular.forEach(headerRows, function(headerRow) {
                    var header = [];

                    angular.forEach(headerRow, function(item) {
                        header.push({
                            text: item.text,
                            colSpan: item.colspan,
                            style: 'tableHeader'
                        });

                        if (item.colspan > 1) {
                            for (var i = 1; i < item.colspan; i++) {
                                header.push({});
                            }
                        }
                    });

                    body.push(header);
                });
            }

            function _updatePrintDataTable(headers, rows) {
                var printTables = [], currentDataColumn = 1;
                var maxWidth = 530;

                var chartTable = $('#chart-data-table');

                chartTable.width(maxWidth);

                var topLevelHeaders = _.tail(headers[0]); //Take all headers but the first, the first is handled separately

                var firstColumnWidth = $('.stat-scroll-table .headcol').width();
                var remainingWidth = 0;
                var printTable;

                $('.stat-scroll-table .scrolling thead tr:first td').each(function(index, td) {
                    var columnWidth = $(td).width();

                    if (remainingWidth > columnWidth) {
                        remainingWidth -= columnWidth;
                    } else {
                        remainingWidth = maxWidth - firstColumnWidth;

                        // Create new table
                        printTable = createNewTable();
                        printTables.push(printTable);
                    }

                    // Add columns to table
                    var topHeader = addTopHeader(printTable, index);

                    //Add the sub level headers, the colspan of the top level header
                    //decides how many sub level columns we have.
                    _.each(_.range(topHeader.colspan), function() {
                        printTable.headers[1].push(headers[1][currentDataColumn]);

                        //Add the data that goes into the sublevel
                        _.each(rows, function(row, rowindex) {
                            printTable.rows[rowindex].data.push(row.data[currentDataColumn - 1]);
                        });

                        currentDataColumn++;
                    });
                });

                function addTopHeader(table, index) {
                    var topHeader = topLevelHeaders[index];

                    table.headers[0].push(topHeader);

                    return topHeader;
                }

                function createNewTable() {
                    var printTable = {headers: [], rows: []};

                    //Add headers for first column
                    _.each(headers, function(header, headerIndex) {
                        printTable.headers[headerIndex] = [header[0]];
                    });

                    //Add row name for each row
                    _.each(rows, function(row) {
                        printTable.rows.push({name: row.name, data: []});
                    });

                    return printTable;
                }

                chartTable.width('');

                return printTables;
            }

            function _getLogo() {
                /* jshint ignore:start */
                return 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAARsAAAA5CAYAAAAGPo+oAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyBpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMC1jMDYwIDYxLjEzNDc3NywgMjAxMC8wMi8xMi0xNzozMjowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNSBXaW5kb3dzIiB4bXBNTTpJbnN0YW5jZUlEPSJ4bXAuaWlkOjhFMTEzNzkzNUI2MTExRTNBOTA1OTgyM0U0QzlEREM0IiB4bXBNTTpEb2N1bWVudElEPSJ4bXAuZGlkOjhFMTEzNzk0NUI2MTExRTNBOTA1OTgyM0U0QzlEREM0Ij4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6OEUxMTM3OTE1QjYxMTFFM0E5MDU5ODIzRTRDOUREQzQiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6OEUxMTM3OTI1QjYxMTFFM0E5MDU5ODIzRTRDOUREQzQiLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz4VRLDUAAALn0lEQVR42uxd3W4buRU+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';
                /* jshint ignore:end */
            }

            //The public api of this factory
            return {
                create: _create,
                print: _print,
                getFileName: _getFileName,
                factory: {
                    create: _create,
                    footer: _getFooter,
                    header: _addHeader,
                    chart: _getChart,
                    table: _getTableLayout,
                    filter: _addListFilter
                }
            };
        }
);
