/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
    function ($filter, _, $timeout, messageService, sortableTableViewState, MAX_INIT_ROWS_TABLE, MAX_INIT_COLUMNS_TABLE, ObjectHelper) {
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
                $scope.tableVisible = getTableVisibility();
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
                        watchHeader();
                        watchRows();
                    });
                };

                $scope.doneRendering = function() {
                    $scope.doneLoading = true;
                };

                $scope.getHeaderTitle = function(header) {
                    return header.title;
                };

                $scope.haveToolTip = function(header) {
                    return ObjectHelper.isNotEmpty($scope.getHeaderTitle(header));
                };

                function getTableVisibility() {
                    if (!angular.isArray($scope.headerRows) || !angular.isArray($scope.rows)) {
                        return true;
                    }

                    if (maxRows !== null && $scope.rows.length > maxRows) {
                        return false;
                    }

                    if (maxColumns !== null && $scope.headerRows[0].length > (maxColumns/3) + 1) {
                        return false;
                    }

                    return true;
                }

                function watchHeader() {
                    if (!angular.isArray($scope.headerRows) || !angular.isArray($scope.rows)) {
                        return;
                    }
                    $scope.tableVisible = getTableVisibility();

                    $scope.headers = $scope.headerRows;
                }

                function watchRows() {
                    if (!angular.isArray($scope.headerRows) || !angular.isArray($scope.rows)) {
                        return;
                    }
                    $scope.tableVisible = getTableVisibility();

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
                        var sortedRows = _.sortBy(rows, function(row) {
                            return row.data[sortIndex].sort;
                        });

                        if (reverse) {
                            sortedRows = _.reverse(sortedRows);
                        }

                        $scope.rowsShown = sortedRows;
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
                            var sortValue = $filter('replaceEmpty')(d);

                            if (angular.isString(sortValue)) {
                                var parse = parseFloat(sortValue.replace(',', '.'));

                                sortValue = isNaN(parse) ? sortValue : parse;
                            }

                            data.push({
                                value: $filter('thousandseparated')(d),
                                sort: sortValue
                            });
                        });

                        rows.push({
                            name: row.name,
                            marked: row.marked,
                            data: data
                        });
                    });
                }
            }
        };
    });
