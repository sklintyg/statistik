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
    function ($timeout, TABLE_CONFIG) {
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

                var maxRows = TABLE_CONFIG.maxRows;
                $scope.moreThanMax = false;

                function watchRows(rows) {
                    $scope.moreThanMax = false;

                    if (angular.isUndefined(rows)) {
                        return;
                    }

                    //if (rows.length > maxRows) {
                    //    $scope.rowsShown = rows.slice(0, maxRows);
                    //    $scope.moreThanMax = true;
                    //} else {
                        $scope.rowsShown = rows;
                    //                    }

                    // Wait for table to render
                    $timeout(setWidth);
                }

                function setWidth() {
                    var columns = element.find('.headcol-name');

                    var maxWidth = Math.max.apply( null, columns.map( function () {
                        return $( this ).outerWidth( true );
                    }).get() );

                    // Add padding
                    maxWidth += 7;

                    element.find('.table-headers').css('width', maxWidth + 'px');
                    //element.find('.scrolling').css('margin-left', maxWidth + 'px');
                }
            }
        };
    });