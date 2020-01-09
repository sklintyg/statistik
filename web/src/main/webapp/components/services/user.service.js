/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

angular.module('StatisticsApp').factory('UserService',
    /** @ngInject */
    function($log, $q, UserModel, statisticsData) {
      'use strict';

      function _saveSettings(settings) {

        var promise = $q.defer();

        var success = function(data) {
          UserModel.setSettings(data);
          promise.resolve(data);
        };

        var error = function(data, status) {
          $log.error('error ' + status);
          // Let calling code handle the error of no data response
          if (data === null) {
            promise.reject({errorCode: data, message: 'no response'});
          } else {
            promise.reject(data);
          }
        };

        statisticsData.saveUserSettings(settings).then(success, error);

        return promise.promise;
      }

      function _updateOneSetting(key, value) {

        var settings = angular.copy(UserModel.get().settings);

        settings[key] = value;

        return _saveSettings(settings);
      }

      return {
        saveSettings: _saveSettings,
        updateOneSetting: _updateOneSetting
      };
    }
);
