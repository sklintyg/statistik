/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

angular.module('StatisticsApp').directive('message',
    /** @ngInject */
    function($log, $rootScope, $filter) {
      'use strict';

      return {
        restrict: 'EA',
        replace: true,
        scope: {
          'key': '@',
          'param': '=',
          'params': '='
        },
        link: function(scope, element, attr) {
          var result;

          // observe changes to interpolated attribute
          function updateMessage(interpolatedKey) {
            var params = typeof scope.param !== 'undefined' ? [scope.param] : scope.params;
            result = $filter('messageFilter')(interpolatedKey, attr.fallback, attr.fallbackDefaultLang, params, attr.lang,
                attr.disableHighlightWords);
            element.html('<span>' + result + '</span>');
          }

          attr.$observe('key', function(interpolatedKey) {
            updateMessage(interpolatedKey);
          });

          updateMessage(attr.key);
        }
      };
    });
