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

/* global JSON */

angular.module('StatisticsApp').factory('sessionCheckService',
    ['$http', '$log', '$interval', '$window', 'UserModel',
      function($http, $log, $interval, $window, UserModel) {
      'use strict';

      var pollPromise;

      var pollIntervalInSeconds = 60;

      // logout use when seconds remains (to be able to follow the normal logout flow)
      var logoutWhenSecondsLeft = pollIntervalInSeconds + 20;

      //one every minute
      var msPollingInterval = pollIntervalInSeconds * 1000;

      /*
       * stop regular polling
       */
      function _stopPolling() {
        if (pollPromise) {
          $log.debug('sessionCheckService -> Polling stopped');
          $interval.cancel(pollPromise);
          pollPromise = null;
        } else {
          $log.debug('sessionCheckService -> Polling already stopped');
        }
      }

      /*
       * get session status from server
       */
      function _getSessionInfo() {
        $log.debug('_getSessionInfo requesting session info =>');
        $http.get('/api/session-auth-check/ping').then(function(response) {
          $log.debug('<= _getSessionInfo success');
          if (response.data) {
            $log.debug('session status  = ' + JSON.stringify(response.data));
            if (response.data.authenticated === true && response.data.secondsUntilExpire < logoutWhenSecondsLeft) {
              $log.debug('Session is about to expire - redirecting to loggedout');
              _stopPolling();
              _logout();
            }
          } else {
            $log.debug('_getSessionInfo returned unexpected data:' + response.data);
          }

          //Schedule new polling
          _stopPolling();
          pollPromise = $interval(_getSessionInfo, msPollingInterval);
        }, function(response) {
          $log.error('_getSessionInfo error ' + response.status);

          _stopPolling();
          //Schedule a new check
          pollPromise = $interval(_getSessionInfo, msPollingInterval);
        });
      }

      /*
       * start regular polling of stats from server
       */
      function _startPolling() {
        if(pollPromise) {
          $log.debug('sessionCheckService -> Polling already started');
        } else {
          $log.debug('sessionCheckService -> Start polling');
          _getSessionInfo();
        }
      }

      function _logout() {
        if (UserModel.get().authenticationMethod !== 'FAKE') {
          // FAKE logouts will be handled upon next request

          // The RelayState is a mechanism to preserve the desired location after
          // SAML redirects/POSTs has occured
          $log.debug('SAML inactive logout');
          $window.location = '/saml/logout?RelayState=timeout';
        }
      }

      // Return public API for the service
      return {
        startPolling: _startPolling,
        stopPolling: _stopPolling
      };
    }]);
