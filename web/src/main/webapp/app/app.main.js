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

/* App Module */
var app = angular.module('StatisticsApp',
    ['ngRoute',
     'ngCookies',
     'ngSanitize',
     'ngAnimate',
     'ui.bootstrap',
     'underscore',
     'StatisticsApp.constants',
     'StatisticsApp.treeMultiSelector',
     'StatisticsApp.businessFilter',
     'dropzone',
     'ngStorage',
     'smoothScroll']);

app.run(
    /** @ngInject */
    function (AppService, $rootScope) {
    'use strict';

        $rootScope.isLoggedIn = false;
        $rootScope.pdfFontLoaded = false;

        AppService.get().then(function(data) {
            $rootScope.isLoggedIn = data.loggedIn;
        });
    }
);