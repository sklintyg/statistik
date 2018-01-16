/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

angular.module('StatisticsApp').factory('filterViewState',
    /** @ngInject */
    function() {
        'use strict';

        var state = {};

        function _reset() {
            state.intygstyper = false;
            state.sjukskrivningslangd = true;
        }

        function _get() {
            return state;
        }

        function _set(newSate) {
            _reset();

            if (!angular.isDefined(newSate)) {
                return;
            }

            if (angular.isDefined(newSate.intygstyper)) {
                state.intygstyper = newSate.intygstyper;
            }

            if (angular.isDefined(newSate.sjukskrivningslangd)) {
                state.sjukskrivningslangd = newSate.sjukskrivningslangd;
            }
        }

        _reset();

        // Return public API for the service
        return {
            get: _get,
            set: _set
        };
    }
);
