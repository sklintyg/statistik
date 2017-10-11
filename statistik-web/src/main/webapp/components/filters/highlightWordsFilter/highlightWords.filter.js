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

/* This filter will tag words (or phrases) that will later be marked by the highlightWords directive.*/
angular.module('StatisticsApp').filter('highlightWords',
    /** @ngInject */
    function(PHRASES_TO_HIGHLIGHT) {
        'use strict';

        return function(text) {
            var highlightedText = text;

            for (var key in PHRASES_TO_HIGHLIGHT) {
                if (PHRASES_TO_HIGHLIGHT.hasOwnProperty(key)) {
                    highlightedText = highlightedText.replace(new RegExp('(' + key + ')', 'gi'), '<span class="highlight-this-content">$1</span>');
                }
            }

            return highlightedText;
        };

    });
