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

angular.module('StatisticsApp').factory('UserModel', ['_',
  function(_) {
    'use strict';

    var data = {};

    _reset();

    function _reset() {
      data.userName = '';
      data.userNameWithAccess = '';
      data.vgs = [];
      data.settings = {};
      data.authenticationMethod = '';
      _resetUserAccessInfo();
      return data;
    }

    function _resetUserAccessInfo() {
      data.loggedInWithoutStatistikuppdrag = true;
      data.isDelprocessledare = false;
      data.isProcessledare = false;
      data.hasRegionAccess = false;
      data.regionAvailable = false;
      data.isRegionAdmin = false;
      data.enableVerksamhetMenu = false;
      data.businesses = [];
      return data;
    }

    return {
      reset: _reset,
      resetUserAccessInfo: _resetUserAccessInfo,

      setLoginInfo: function(loginInfo) {
        data.userName = loginInfo.name;
        data.userNameWithAccess = loginInfo.name;
        data.vgs = _.map(loginInfo.vgs, function(vg) {
          return {id: vg.hsaId, name: vg.name};
        });
        data.settings = loginInfo.userSettings;
        data.authenticationMethod = loginInfo.authenticationMethod;
      },
      setUserAccessInfo: function(userAccessInfo) {
        data.businesses = userAccessInfo.businesses;
        data.loggedInWithoutStatistikuppdrag = !(userAccessInfo.businesses && userAccessInfo.businesses.length >= 1);
        data.isDelprocessledare = userAccessInfo.vgInfo.delprocessledare;
        data.isProcessledare = userAccessInfo.vgInfo.processledare;
        data.hasRegionAccess = userAccessInfo.vgInfo.regionsvardgivare;
        data.regionAvailable = userAccessInfo.vgInfo.regionsvardgivareWithUpload;
        data.isRegionAdmin = userAccessInfo.vgInfo.regionAdmin;
        data.enableVerksamhetMenu = userAccessInfo.businesses && userAccessInfo.businesses.length >= 1;
      },
      setSettings: function(settings) {
        data.settings = settings;
      },
      get: function() {
        return data;
      }
    };

  }
]);
