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


angular.module('StatisticsApp').factory('CoordinateService',
    /** @ngInject */
    function(COUNTY_COORDS, _) {
        'use strict';

        function _getCoordinates(perCountyObject) {
            var name = perCountyObject.name.toLowerCase();

            var result = _.find(COUNTY_COORDS, function(c) {
                if (contains(name, c.name)) {
                    return c;
                }
            });

            if (!result) {
               return null;
            }

            return result.xy;
        }

        function contains(master, substring) {
            return master.indexOf(substring) !== -1;
        }

        return {
            getCoordinates: _getCoordinates
        };
    }
);
