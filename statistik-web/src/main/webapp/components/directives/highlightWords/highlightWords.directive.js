/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
/*
* This directive will add the actual highlight of words and the matching tooltip.
* The words to highlight should already be tagged from the highlightWords filter.
* This directive is required since angular will not process the html added from a
* filter and it must therefore be added from this directive instead.
*/
angular.module('StatisticsApp')
    .directive('highlightWords',
        /** @ngInject */
        function(_, PHRASES_TO_HIGHLIGHT) {
        'use strict';

        var hightlightWords = _.debounce(function() {
            $('.highlight-this-content').each(function(index, element){
                $(element).addClass('highlight-words');
                element.setAttribute('data-toggle', 'tooltip');
                element.setAttribute('data-placement', 'auto right');
                element.setAttribute('title', PHRASES_TO_HIGHLIGHT[element.innerText]);
            });
            initToolTip();
        }, 200);

        return {
            scope: {
            },
            restrict: 'E',
            link: function($scope) {
                $scope.hasRegistered = false;
                $scope.$watch(function() {
                    if ($scope.hasRegistered) {
                        return;
                    }
                    $scope.hasRegistered = true;
                    $scope.$$postDigest(function() {
                        $scope.hasRegistered = false;
                        hightlightWords();
                    });
                });
            }
        };

        function initToolTip() {
            $('[data-toggle="tooltip"]').tooltip({
                container: '#view'
            });
        }

    });
