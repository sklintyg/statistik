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

        _reset();

        function _reset() {
            data.userName = '';
            data.userNameWithAccess = '';
            data.loggedInWithoutStatistikuppdrag = true;
            data.isDelprocessledare = false;
            data.isProcessledare = false;
            data.hasLandstingAccess = false;
            data.landstingAvailable = false;
            data.isLandstingAdmin = false;
            data.enableVerksamhetMenu = false;
            data.businesses = [];
            return data;
        }

        return {
            reset: _reset,

            set: function(loginInfo) {
                _reset();
                data.userName = loginInfo.name;
                data.userNameWithAccess = loginInfo.name;
                data.loggedInWithoutStatistikuppdrag = !(loginInfo.businesses && loginInfo.businesses.length >= 1);
                data.isDelprocessledare = loginInfo.delprocessledare;
                data.isProcessledare = loginInfo.processledare;
                data.hasLandstingAccess = loginInfo.landstingsvardgivare;
                data.landstingAvailable = loginInfo.landstingsvardgivareWithUpload;
                data.isLandstingAdmin = loginInfo.landstingAdmin;
                data.enableVerksamhetMenu = loginInfo.businesses && loginInfo.businesses.length >= 1;
                data.businesses = loginInfo.businesses;
            },
            get: function() {
                return data;
            }
        };
    }
);
