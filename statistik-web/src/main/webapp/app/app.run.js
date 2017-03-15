/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

    Highcharts.seriesTypes.line.prototype.drawLegendSymbol = Highcharts.seriesTypes.area.prototype.drawLegendSymbol;

    $rootScope.lang = 'sv';
    $rootScope.DEFAULT_LANG = 'sv';
    messageService.addResources(stMessages); // jshint ignore:line

    Highcharts.setOptions({
        lang: { thousandsSep: ' ' }
    });

    $rootScope.pageTitle = 'Statistiktjänsten';
    $rootScope.pageName = '';

    $rootScope.$on('$routeChangeSuccess', function (e, current) {
        function addToQueryString(name, value) {
            if (value) {
                $rootScope.queryString += !!$rootScope.queryString ? '&' : '?';
                $rootScope.queryString += name + '=' + value;
            }
        }

        if ($route.current.$$route) {
            if ($route.current.$$route.title) {
                $rootScope.pageName = messageService.getProperty($route.current.$$route.title, null, $route.current.$$route.title);
            } else {
                $rootScope.pageName = null;
            }

            $rootScope.pageTitle = ($rootScope.pageName ? $rootScope.pageName + ' | ' : '') + 'Statistiktjänsten';

            $rootScope.queryString = '';
            addToQueryString('vgid', current.params.vgid);
            addToQueryString('filter', current.params.filter);
            addToQueryString('landstingfilter', current.params.landstingfilter);

            $rootScope.verksamhetViewShowing = current.$$route.originalPath.indexOf('/verksamhet') === 0;
        }
    });
});
