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

angular.module('StatisticsApp').filter('helpTooltip',
    /** @ngInject */
    function($sce) {
        'use strict';

        return function(text) {
            if (angular.isString(text)) {
                var phrases = getPhrases();
                angular.forEach(phrases, function(phrase) {
                    text = (' ' + text + ' ').replace(new RegExp('\\W(' + phrase.phrase + ')\\W', 'gi'),
                        ' <span class="help-tooltip" uib-tooltip="'+phrase.text+'" tooltip-placement="right">$1</span> ');
                });
            }

            return $sce.trustAsHtml(text);
        };


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
