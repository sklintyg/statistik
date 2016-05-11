/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

angular.module('StatisticsApp').factory('UserModel',
    function() {
        'use strict';

        var data = {};

        function _reset() {
            data.name = null;
            data.role = null;
            data.authenticationScheme = null;
            data.fakeSchemeId = 'urn:inera:rehabstod:siths:fake';
            data.valdVardenhet = null;
            data.valdVardgivare = null;
            data.vardgivare = null;
            data.totaltAntalVardenheter = 0;
            data.isLakare = false;
            data.urval = null;

            data.loggedIn = false;
            return data;
        }

        return {
            reset: _reset,
            init: function() {
                return _reset();
            },

            set: function(user) {
                _reset();
                data.name = user.namn;
                data.authenticationScheme = user.authenticationScheme;
                data.valdVardenhet = user.valdVardenhet;
                data.valdVardgivare = user.valdVardgivare;
                data.vardgivare = user.vardgivare;
                data.totaltAntalVardenheter = user.totaltAntalVardenheter;
                data.loggedIn = true;
                data.urval = user.urval;
            },
            get: function() {
                return data;
            }
        };
    }
);