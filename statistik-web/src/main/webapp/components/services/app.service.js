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


angular.module('StatisticsApp').factory('AppService',
    /** @ngInject */
    function($http, $log, $q, AppModel) {
        'use strict';

        function _get() {

            var promise = $q.defer();

            var restPath = '/api/login/getAppSettings';
            $http.get(restPath).then(function(result) {
                var data = result.data;
                AppModel.set(data);
                promise.resolve(data);
            }, function(data, status) {
                $log.error('error ' + status);
                // Let calling code handle the error of no data response
                if (data === null) {
                    promise.reject({errorCode: data, message: 'no response'});
                } else {
                    promise.reject(data);
                }
            });

            return promise.promise;
        }

        return {
            get: _get
        };
    }
);
