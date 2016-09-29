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

/* global Mark */

angular.module('StatisticsApp')
    .directive('highlightWords',
        /** @ngInject */
        function() {
        'use strict';
        return {
            scope: {
            },
            restrict: 'E',
            link: function($scope) {
                var hasRegistered = false;
                $scope.$watch(function() {
                    if (hasRegistered) {
                        return;
                    }
                    hasRegistered = true;
                    $scope.$$postDigest(function() {
                        hasRegistered = false;
                        hightlightWords();
                    });
                });
            }
        };
        
        function hightlightWords() {
            var context = document.querySelector('.highlight-content');
            var instance = new Mark(context);

            angular.forEach(getPhrases(), function(phrase) {
                instance.mark(phrase.phrase, {
                    exclude: ['svg *'],
                    className: 'highlight-words',
                    separateWordSearch: false,
                    accuracy: 'exactly',
                    each: addTooltip(phrase),
                    done: initToolTop
                });
            });
        }

        function initToolTop() {
            $('[data-toggle="tooltip"]').tooltip({
                container: 'body'
            });
        }

        function addTooltip(phrase) {
            return function(element) {
                element.setAttribute('data-toggle', 'tooltip');
                element.setAttribute('data-placement', 'right');
                element.setAttribute('title', phrase.text);
            };
        }

        function getPhrases() {
            return [
                {
                    phrase: 'sjukfall',
                    text: 'Ett sjukfall omfattar en patients alla elektroniska läkarintyg som följer på varandra med max fem dagars uppehåll. Intygen måste även vara utfärdade av samma vårdgivare. Om det är mer än fem dagar mellan två intyg eller om två intyg är utfärdade av olika vårdgivare räknas det som två sjukfall.'
                },
                {
                    phrase: 'inkomna meddelanden',
                    text: 'Meddelanden som skickats elektroniskt från Försäkringskassan till hälso- och sjukvården. Ett meddelande rör alltid ett visst elektroniskt intyg som utfärdats av hälso- och sjukvården och som skickats till Försäkringskassan.'
                },
                {
                    phrase: 'utfärdade intyg',
                    text: 'Elektroniska intyg som har utfärdats och signerats av hälso- och sjukvården.'
                }
            ];

        }
    });