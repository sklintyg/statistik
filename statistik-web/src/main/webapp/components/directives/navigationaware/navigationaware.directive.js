/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

angular.module('StatisticsApp').directive('navigationaware',
    /** @ngInject */
    function ($rootScope, $route) {
        'use strict';
        var isActivePage = function(currentRoute, navLinkAttrs) {
            return currentRoute.controllerAs === navLinkAttrs.ctrlname || currentRoute.controller === navLinkAttrs.ctrlname;
        };

        return {
            restrict: 'A',
            link: function ($scope, elem, $attrs) {

                init($route.current);

                function init(current) {
                    elem.parent().removeClass('active');
                    if (isActivePage(current, $attrs)){
                        elem.parent().addClass('active');
                        var groupId = elem.closest('.navigation-group').attr('id');
                        if (groupId) {
                            $rootScope.$broadcast('navigationUpdate', groupId);
                        }
                    }
                }

                $rootScope.$on('$routeChangeSuccess', function(angularEvent, current) {
                    init(current);
                });
            }
        };
    });
