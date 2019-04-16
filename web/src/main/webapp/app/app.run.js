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

/* globals Highcharts */
angular.module('StatisticsApp').run(

    /** @ngInject */
    function ($rootScope, $route, $http, messageService, dynamicLinkService) {
    'use strict';

    Highcharts.seriesTypes.line.prototype.drawLegendSymbol = Highcharts.seriesTypes.area.prototype.drawLegendSymbol;

    $rootScope.lang = 'sv';
    $rootScope.DEFAULT_LANG = 'sv';
    messageService.addResources(stMessages); // jshint ignore:line
        
    $http.get('/api/links').then(function(links) {
        dynamicLinkService.addLinks(links.data);
        messageService.addLinks(links.data);
    });

    Highcharts.setOptions({
        lang: { thousandsSep: ' ' }
    });

    $rootScope.pageTitle = 'Intygsstatistik';
    $rootScope.pageName = '';

    $rootScope.$on('$routeChangeSuccess', function (e, current) {
        function addToQueryString(name, value) {
            if (value) {
                $rootScope.queryString += !!$rootScope.queryString ? '&' : '?';
                $rootScope.queryString += name + '=' + value;
            }
        }

        if ($route.current.$$route) {
            var title = $route.current.$$route.title;
            if (title) {
                $rootScope.pageName = messageService.getProperty(title, null, title);
            } else {
                $rootScope.pageName = null;
            }

            $rootScope.pageTitle = ($rootScope.pageName ? $rootScope.pageName + ' | ' : '') + 'Intygsstatistik';

            $rootScope.queryString = '';
            addToQueryString('vgid', current.params.vgid);
            addToQueryString('filter', current.params.filter);
            addToQueryString('regionfilter', current.params.regionfilter);
            addToQueryString('codelevel', current.params.codelevel);

            $rootScope.verksamhetViewShowing = current.$$route.originalPath.indexOf('/verksamhet') === 0;
        }
    });
});
