/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
    function ($filter, _) {
        'use strict';

        return {
            restrict: 'E',
            scope: {
                rows: '=',
                headerRows: '='
            },
            templateUrl: '/components/directives/statScrollTable/statScrollTable.html',
            link: function($scope, element) {
                $scope.$watch('rows', watchRows);

                $scope.sortIndex = -1;
                $scope.sortReverse = true;
                $scope.rowsShown = [];
                var rows = [];

                $scope.sortByColumn = function(columnIndex, shouldSort) {

                    if (!shouldSort) {
                        return;
                    }

                    if ($scope.sortIndex === columnIndex) {
                        if (!$scope.sortReverse) {
                            $scope.sortReverse = true;
                            $scope.sortIndex = -1;
                        } else {
                            $scope.sortReverse = false;
                        }
                    }
                    else {
                        $scope.sortIndex = columnIndex;
                        $scope.sortReverse = true;
                    }

                    sortRows();
                };

                function watchRows(rows) {
                    if (angular.isUndefined(rows)) {
                        return;
                    }

                    setWidth();
                    processData();
                    sortRows();
                }

                function setWidth() {
                    var container = element.find('#table-column-measureContainer');
                    var widths = [];

                    angular.forEach($scope.rows, function(row) {
                        widths.push(getColumnWidth(container, row));
                    });

                    var maxWidth = Math.max.apply( null, widths);

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
                    var sortIndex = $scope.sortIndex - 1;
                    var reverse = $scope.sortReverse;

                    if (sortIndex < 0) {
                        $scope.rowsShown = rows;
                    } else {
                        $scope.rowsShown = _.sortBy(rows, function(row) {
                            var x = reverse ? -1 : 1;
                            return x * row.data[sortIndex].sort;
                        });
                    }
                }

                function processData() {
                    rows = [];
                    angular.forEach($scope.rows, function(row) {
                        var data = [];

                        angular.forEach(row.data, function(d) {
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