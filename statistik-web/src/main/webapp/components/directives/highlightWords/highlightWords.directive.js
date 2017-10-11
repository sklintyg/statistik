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
    .directive('highlightWords',
        /** @ngInject */
        function(_, PHRASES_TO_HIGHLIGHT) {
        'use strict';

        var hightlightWords = _.debounce(function() {
            var keys = [];
            for (var key in PHRASES_TO_HIGHLIGHT) {
                if (PHRASES_TO_HIGHLIGHT.hasOwnProperty(key)) {
                    keys.push(key);
                }
            }

            $('.highlight-this-content').mark(keys, {
                element: 'span',
                exclude: ['svg *', '.highlight-words', 'datatable-body'],
                className: 'highlight-words',
                separateWordSearch: false,
                diacritics: false,
                accuracy: 'exactly',
                each: addTooltip(PHRASES_TO_HIGHLIGHT),
                done: initToolTop
            });
        }, 300);

        return {
            scope: {
            },
            restrict: 'E',
            link: function($scope) {
                $scope.hasRegistered = false;
                $scope.$watch(function() {
                    if ($scope.hasRegistered) {
                        return;
                    }
                    $scope.hasRegistered = true;
                    $scope.$$postDigest(function() {
                        $scope.hasRegistered = false;
                        hightlightWords();
                    });
                });
            }
        };

        function initToolTop() {
            $('[data-toggle="tooltip"]').tooltip({
                container: '#view'
            });
        }

        function addTooltip(phrases) {
            return function(element) {
                element.setAttribute('data-toggle', 'tooltip');
                element.setAttribute('data-placement', 'auto right');
                element.setAttribute('title', phrases[element.innerText]);
            };
        }

    });
