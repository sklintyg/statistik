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

angular.module('StatisticsApp').directive('serviceBanner', function() {
  'use strict';

  return {
    restrict: 'E',
    scope: {
      banners: '<'
    },
    templateUrl: '/components/directives/serviceBanner/serviceBanner.directive.html',
    controller: /** @ngInject */
    function($scope) {

      function getClass(severity) {
        switch (severity) {
        case 'ERROR':
          return 'alert-danger';
        case 'WARN':
          return 'alert-warning';
        case 'INFO':
          return 'alert-info';
        }
      }

      $scope.$watch('banners', function() {
        var banners = [];

        angular.forEach($scope.banners, function(banner) {
          banners.push({
            bannerClass: getClass(banner.severity),
            message: banner.message
          });
        });

        $scope.processedBanners = banners;
      });
    }
  };
});
