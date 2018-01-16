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

angular.module('StatisticsApp').directive('statScrollTable',
    /** @ngInject */
    function ($filter, _, $timeout, messageService, sortableTableViewState, MAX_INIT_ROWS_TABLE, MAX_INIT_COLUMNS_TABLE) {
        'use strict';

        return {
            restrict: 'E',
            scope: {
                rows: '=',
                headerRows: '='
            },
            templateUrl: '/components/directives/statScrollTable/statScrollTable.html',
            link: function($scope, element) {

                $scope.sortableTableVS = sortableTableViewState;
                $scope.sortableTableVS.reset();


                $scope.$watch('rows', watchRows);
                $scope.$watch('headerRows', watchHeader);


                $scope.rowsShown = [];
                $scope.fixedHeader = [];
                $scope.scrollHeader = [];
                $scope.tableVisible = true;
                $scope.doneLoading = true;
                $scope.message = messageService.getProperty('info.hidden-table');
                $scope.messageSeverity = 'INFO';

                var rows = [];
                var maxRows = MAX_INIT_ROWS_TABLE;
                var maxColumns = MAX_INIT_COLUMNS_TABLE;

                $scope.sortByColumn = function(columnIndex, shouldSort) {

                    if (!shouldSort) {
                        return;
                    }

                    // Update viewstate
                    sortableTableViewState.updateSortIndex(columnIndex);

                    // Do sort
                    sortRows();
                };

                $scope.showTable = function() {
                    $scope.tableVisible = true;
                    $scope.doneLoading = false;
                    maxRows = null;
                    maxColumns = null;

                    $timeout(function() {
                        watchHeader($scope.headerRows);
                        watchRows($scope.rows);
                    });
                };

                $scope.doneRendering = function() {
                    $scope.doneLoading = true;
                };

                function checkHeaderAndRowLength(rows, headers) {
                    if (!angular.isArray(headers) || !angular.isArray(rows)) {
                        return false;
                    }

                    if (maxRows !== null && rows.length > maxRows) {
                        $scope.tableVisible = false;
                        return false;
                    }

                    if (maxColumns !== null && headers[0].length > (maxColumns/3) + 1) {
                        $scope.tableVisible = false;
                        return false;
                    }

                    return true;
                }

                function watchHeader(headerRows) {
                    if (!checkHeaderAndRowLength($scope.rows, headerRows)) {
                        return;
                    }

                    $scope.headers = headerRows;
                }

                function watchRows(rows) {
                    if (!checkHeaderAndRowLength(rows, $scope.headerRows)) {
                        return;
                    }

                    setWidth();
                    processData();

                    // Do sort
                    sortRows();
                }

                function setWidth() {
                    var container = element.find('#table-column-measureContainer');

                    var maxWidth = _.chain($scope.rows)
                        .map(function(row) {
                            return {
                                row: row,
                                length: row.name.length
                            };
                        })
                        .sortBy('length')
                        .takeRight(5)
                        .map(function(row) {
                            return getColumnWidth(container, row.row);
                        })
                        .max()
                        .value();

                    // Add padding
                    maxWidth += 7;

                    element.find('.table-headers').css('width', maxWidth + 'px');
                }

                function getColumnWidth(container, row) {

                    var extraClass = row.marked ? 'bold' : '';
                    //Temporary add, measure and remove the chip's html equivalent.
                    var elem = $('<span class="headcol-name ' + extraClass + '">' + row.name + '</span>');
                    container.append(elem);
                    var chipWidth = elem.outerWidth(true);
                    elem.remove();
                    return chipWidth;
                }

                function sortRows() {
                    $('.stat-scroll-table .stat-table-row').off( 'mouseenter' );
                    $('.stat-scroll-table .stat-table-row').off( 'mouseleave' );

                    var sortIndex = sortableTableViewState.getSortIndex() - 1;
                    var reverse = sortableTableViewState.getSortReverse();

                    if (sortIndex < 0) {
                        $scope.rowsShown = rows;
                    } else {
                        $scope.rowsShown = _.sortBy(rows, function(row) {
                            var x = reverse ? -1 : 1;
                            return x * row.data[sortIndex].sort;
                        });
                    }

                    // Update viewstate
                    $scope.sortableTableVS.updateSortedRows($scope.rowsShown);

                    $timeout(function() {
                        $('.stat-scroll-table .stat-table-row').hover( function() {
                            var el = $(this);
                            var position = el.index() + 1;

                            $('.stat-scroll-table .stat-table-row:nth-child(' + position + ')').addClass('tr-hover');
                        }, function() {
                            var el = $(this);
                            var position = el.index() + 1;

                            $('.stat-scroll-table .stat-table-row:nth-child(' + position + ')').removeClass('tr-hover');
                        });
                    });
                }

                function processData() {
                    rows = [];
                    _.each($scope.rows, function(row) {
                        var data = [];

                        _.each(row.data, function(d) {
                            data.push({
                                value: $filter('thousandseparated')(d),
                                sort: $filter('replaceEmpty')(d)
                            });
                        });

                        rows.push({
                            name: row.name,
                            data: data
                        });
                    });
                }
            }
        };
    });
