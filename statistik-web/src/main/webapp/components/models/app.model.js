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

angular.module('StatisticsApp').factory('AppModel',
    function() {
        'use strict';

        var data = {};

        function _reset() {
            data.isLoggedIn = false;
            data.loginUrl = '';
            data.loginVisible = false;
            data.highchartsExportUrl = '';
            return data;
        }

        return {
            reset: _reset,
            init: function() {
                return _reset();
            },
            set: function(app) {
                _reset();
                data.isLoggedIn = app.isLoggedIn;
                data.loginUrl = app.loginUrl;
                data.loginVisible = app.loginVisible;
                data.highchartsExportUrl = app.highchartsExportUrl;
            },
            get: function() {
                return data;
            }
        };
    }
);