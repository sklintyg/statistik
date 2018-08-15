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

/* globals pdfMake, canvg, $ */
angular.module('StatisticsApp')
    .factory('pdfFactory',
        /** @ngInject */
        function($window, $timeout, thousandseparatedFilter, $location, _, MAX_ROWS_TABLE_PDF, messageService, $rootScope,
            ControllerCommons, sortableTableViewState, filterViewState) {

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

                        var tableData = _updatePrintDataTable($scope.headerrows, sortableTableViewState.getSortedRows());

                        _.each(tableData, function(t) {
                            table.push({
                                header: t.headers,
                                data: formatTableData(t.rows),
                                hasMoreThanMaxRows: t.rows.length > MAX_ROWS_TABLE_PDF
                            });
                        });

                        $scope.status.isTableOpen = isTableVisible;

                        _generate(headers, table, charts, $scope.activeEnhetsFilters, $scope.activeEnhetsFiltersAllSelected, $scope.activeDiagnosFilters, $scope.activeSjukskrivningslangdsFilters,
                            $scope.activeAldersgruppFilters, $scope.activeIntygstypFilters, filename, pdfDoneCallback);
                    });
                }
                else {
                    table = {
                        header: $scope.headerrows,
                        data: formatTableData(sortableTableViewState.getSortedRows()),
                        hasMoreThanMaxRows: sortableTableViewState.getSortedRows().length > MAX_ROWS_TABLE_PDF
                    };

                    _generate(headers, table, charts, $scope.activeEnhetsFilters, $scope.activeEnhetsFiltersAllSelected, $scope.activeDiagnosFilters, $scope.activeSjukskrivningslangdsFilters,
                        $scope.activeAldersgruppFilters, $scope.activeIntygstypFilters, filename, pdfDoneCallback);
                }
            }

            function formatTableData(data) {
                var tableData = [];
                var tableRows = _.cloneDeep(data.slice(0, MAX_ROWS_TABLE_PDF));
                _.each(tableRows, function(row) {
                    var rowData = [];

                    _.each(row.data, function(item) {
                        rowData.push(item.value);
                    });

                    row.data = rowData;

                    tableData.push(row);
                });
                return tableData;
            }

            function _generate(headers, table, charts, enhetsFilter, enhetsFilterAllSelected, diagnosFilter, sjukskrivningslangdFilter, aldersgruppFilter, intygstyperFilter, filename, pdfDoneCallback) {
                var content = [];

                _addHeader(content, headers);
                content.push(_convertChartsToImages(charts));

                _addAllFilters(content, enhetsFilterAllSelected, enhetsFilter, diagnosFilter, sjukskrivningslangdFilter, aldersgruppFilter, intygstyperFilter);

                if (angular.isArray(table)) {
                    _.each(table, function(t) {
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

            function _addAllFilters(content, enhetsFilterAllSelected, enhetsFilter, diagnosFilter, sjukskrivningslangdFilter, aldersgruppFilter, intygstyperFilter) {
                if (enhetsFilterAllSelected) {
                    _addListFilter(content, messageService.getProperty('lbl.filter.pdf.enheteralla'), enhetsFilter);
                } else {
                    _addListFilter(content, messageService.getProperty('lbl.filter.pdf.enheter'), enhetsFilter);
                }

                _addListFilter(content, messageService.getProperty('lbl.filter.pdf.diagnoser'), diagnosFilter);

                if (filterViewState.get().sjukskrivningslangd) {
                    _addListFilter(content, messageService.getProperty('lbl.filter.pdf.sjukskrivningslangd'), sjukskrivningslangdFilter);
                }

                _addListFilter(content, messageService.getProperty('lbl.filter.pdf.aldersgrupp'), aldersgruppFilter);

                if (filterViewState.get().intygstyper) {
                    _addListFilter(content, messageService.getProperty('lbl.filter.pdf.intygstyper'),
                        intygstyperFilter);
                }
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
                content.push({ image: _getLogo(), width: 180, margin: [ 0, 0, 0, 10 ]});

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

                _.each(charts, function(chart, index) {
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

                _.each(data, function(row) {
                    var rowData = [row.name];

                    _.each(row.data, function(item) {
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
                _.each(headerRows, function(headerRow) {
                    var header = [];

                    _.each(headerRow, function(item) {
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
                return 'data:image/png;base64,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';
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
                    filter: _addListFilter,
                    allFilters: _addAllFilters
                }
            };
        }
);
