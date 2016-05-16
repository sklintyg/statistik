/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

/* globals Highcharts */
angular.module('StatisticsApp').run(

    /** @ngInject */
    function ($rootScope, $route, messageService) {
    'use strict';

    $rootScope.lang = 'sv';
    $rootScope.DEFAULT_LANG = 'sv';
    messageService.addResources(stMessages); // jshint ignore:line

    Highcharts.setOptions({
        lang: { thousandsSep: ' ' }
    });

    $rootScope.pageTitle = 'Statistiktjänsten';
    $rootScope.pageName = '';

    $rootScope.$on('$routeChangeSuccess', function (e, current) {
        if ($route.current.$$route) {
            $rootScope.pageName = $route.current.$$route.title;
            $rootScope.pageTitle = ($rootScope.pageName ? $rootScope.pageName + ' | ' : '') + 'Statistiktjänsten';
            $rootScope.queryString = current.params.filter ? '?filter=' + current.params.filter : '';
            if (current.params.landstingfilter) {
                $rootScope.queryString += $rootScope.queryString.length > 0 ? '&' : '?';
                $rootScope.queryString += 'landstingfilter=' + current.params.landstingfilter;
            }
            $rootScope.verksamhetViewShowing = current.$$route.originalPath.indexOf('/verksamhet') === 0;
        }
    });
});