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

angular.module('showcase').config(function($routeProvider) {
    'use strict';
    var templateRoot = '';

    $routeProvider.
    
    when('/', {
    	templateUrl: templateRoot + 'views/bootstrap.html',
        controller: 'showcase.BootstrapCtrl'
    }).
        when('/showcase', {
        	templateUrl: templateRoot + 'views/bootstrap.html',
            controller: 'showcase.BootstrapCtrl'
                   // templateUrl: templateRoot + 'header.html'
        }).

        when('/showcase.bootstrap', {
                    templateUrl: templateRoot + 'views/bootstrap.html',
                    controller: 'showcase.BootstrapCtrl'
        }).

        when('/showcase.statistik', {
                    templateUrl: templateRoot + 'views/statistik.html',
                    controller: 'showcase.StatistikCtrl'
        }).

        when('/showcase.navigation', {
                    templateUrl: templateRoot + 'views/navigation.html',
                    controller: 'showcase.NavigationCtrl'
        }).
        when('/nationell/aldersgrupper', {
            templateUrl: templateRoot + 'views/navigation.html',
            controller: 'showcase.NavigationCtrl'
        }).
        when('/nationell/diagnosavsnitt', {
            templateUrl: templateRoot + 'views/navigation.html',
            controller: 'showcase.NavigationCtrl'
        }).
        when('/nationell/diagnosgrupp', {
            templateUrl: templateRoot + 'views/navigation.html',
            controller: 'showcase.NavigationCtrl'
        }).
        when('/nationell/oversikt', {
            templateUrl: templateRoot + 'views/navigation.html',
            controller: 'showcase.NavigationCtrl'
        }).
        when('/nationell/sjukfallPerManad', {
            templateUrl: templateRoot + 'views/navigation.html',
            controller: 'showcase.NavigationCtrl'
        }).
        when('/nationell/sjukskrivningsgrad', {
            templateUrl: templateRoot + 'views/navigation.html',
            controller: 'showcase.NavigationCtrl'
        }).
        when('/nationell/sjukskrivningslangd', {
            templateUrl: templateRoot + 'views/navigation.html',
            controller: 'showcase.NavigationCtrl'
        }).
        when('/nationell/lan', {
            templateUrl: templateRoot + 'views/navigation.html',
            controller: 'showcase.NavigationCtrl'
        }).
        when('/nationell/andelSjukfallPerKon', {
            templateUrl: templateRoot + 'views/navigation.html',
            controller: 'showcase.NavigationCtrl'
        }).
        when('/om/tjansten', {
            templateUrl: templateRoot + 'views/navigation.html',
            controller: 'showcase.NavigationCtrl'
        }).
        when('/om/inloggning', {
            templateUrl: templateRoot + 'views/navigation.html',
            controller: 'showcase.NavigationCtrl'
        }).
        when('/om/vanligafragor', {
            templateUrl: templateRoot + 'views/navigation.html',
            controller: 'showcase.NavigationCtrl'
        }).
        when('/om/kontakt', {
            templateUrl: templateRoot + 'views/navigation.html',
            controller: 'showcase.NavigationCtrl'
        }).
        otherwise({redirectTo: templateRoot + 'views/bootstrap.html'
        });

});
