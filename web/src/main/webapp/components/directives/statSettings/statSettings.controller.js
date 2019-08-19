/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

angular.module('StatisticsApp').controller('StatSettingsCtrl',
    function($scope, $uibModalInstance, messageService, statisticsData, UserModel, _) {
      'use strict';

      $scope.saving = false;
      $scope.settings = [];

      var oldSettings = UserModel.get().settings;

      addSetting('showMessagesPerLakare');

      $scope.cancel = function() {
        $uibModalInstance.dismiss();
      };

      $scope.save = function() {
        $scope.saving = true;

        var toSave = getSettings();

        statisticsData.saveUserSettings(toSave)
        .then(
            function(newSettings) {
              UserModel.setSettings(newSettings);
              $scope.saving = false;
              $uibModalInstance.close();
            },
            function() {
              $scope.saving = false;
            }
        );
      };

      function getSettings() {
        var newSettings = {};

        _.each($scope.settings, function(value) {
          newSettings[value.property] = value.value;
        });

        return newSettings;
      }

      function addSetting(property) {
        $scope.settings.push({
          id: 'setting-' + property.toLowerCase(),
          property: property,
          title: 'settings.modal.' + property + '.title',
          description: 'settings.modal.' + property + '.description',
          help: messageService.getProperty('settings.modal.' + property + '.help'),
          value: oldSettings[property]
        });
      }
    }
);
