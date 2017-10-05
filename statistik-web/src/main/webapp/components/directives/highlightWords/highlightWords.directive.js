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

/* globals Map */

angular.module('StatisticsApp')
    .directive('highlightWords',
        /** @ngInject */
        function(_) {
        'use strict';

        var hightlightWords = _.debounce(function() {
            console.log('Startar highlight');
            var phrases = getPhrases();
            var keys = [];
            phrases.forEach(function(item, key) {
                keys.push(key);
            });
            $('.highlight-content').mark(keys, {
                element: 'span',
                exclude: ['svg *', '.highlight-words', 'datatable-body'],
                className: 'highlight-words',
                separateWordSearch: false,
                diacritics: false,
                accuracy: 'exactly',
                each: addTooltip(phrases),
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
                element.setAttribute('title', phrases.get(element.innerText));
            };
        }

        function getPhrases() {
            var phrases = new Map();
            phrases.set('sjukfall', 'Ett sjukfall omfattar en patients alla elektroniska läkarintyg som följer på varandra med max fem dagars uppehåll. Intygen måste även vara utfärdade av samma vårdgivare. Om det är mer än fem dagar mellan två intyg eller om två intyg är utfärdade av olika vårdgivare räknas det som två sjukfall.');
            phrases.set('inkomna meddelanden', 'Meddelanden som skickats elektroniskt från Försäkringskassan till hälso- och sjukvården. Ett meddelande rör alltid ett visst elektroniskt intyg som utfärdats av hälso- och sjukvården och som skickats till Försäkringskassan.');
            phrases.set('utfärdade intyg', 'Elektroniska intyg som har utfärdats och signerats av hälso- och sjukvården.');
            phrases.set('Okänd befattning', 'Innehåller sjukfall där läkaren inte går att slå upp i HSA-katalogen eller där läkaren inte har någon befattning angiven.');
            phrases.set('Ej läkarbefattning', 'Innehåller sjukfall där läkaren inte har någon läkarbefattning angiven i HSA men däremot andra slags befattningar.');
            phrases.set('Utan giltig ICD-10 kod', 'Innehåller sjukfall som inte har någon diagnoskod angiven eller där den angivna diagnoskoden inte finns i klassificeringssystemet för diagnoser, ICD-10-SE.');
            phrases.set('Okänt län', 'Innehåller de sjukfall där enheten som utfärdat intygen inte har något län angivet i HSA-katalogen.');
            return phrases;
        }

    });
