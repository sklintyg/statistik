/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
/**
 * Monitoring log in backend.
 */
angular.module('StatisticsApp').factory('monitoringLogService',
    /** @ngInject */
    function($http, $cookies) {
        'use strict';

        function post(request) {
            var csrfToken = $cookies.get('XSRF-TOKEN') || undefined;
            if (csrfToken) {
            $http.post('/api/logging/monitorlog', request, {
                    headers: {
                        'X-XSRF-TOKEN': csrfToken
                    },
                });
            }
        }

        function isDefined(input) {
            return input !== undefined && input !== '';
        }

        function _screenResolution(width, height) {
            if (isDefined(width) && isDefined(height)) {
                post({
                    'event': 'SCREEN_RESOLUTION',
                    'info': {
                        'width': width,
                        'height': height
                    }
                });
            }
        }


        return {
            screenResolution: _screenResolution
        };

    });