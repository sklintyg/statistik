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

(function() {
    'use strict';

    // --- end test hooks

    // Globally configure jquery not to cache ajax requests.
    // Our other angular $http service driven requests have their own solution (using an interceptor)

    $.ajaxSetup({cache: false});
    
    var statApp = angular.module('StatisticsApp',
    	    ['ngRoute',
    	     'ngCookies',
    	     'ngSanitize',
    	     'ngAnimate',
    	     'ngMockE2E',
    	     'ui.bootstrap',
    	     'underscore',
    	     'StatisticsApp.constants',
    	     'dropzone',
    	     'ngStorage']);

    var app = angular.module('showcase',
        ['ui.bootstrap', 'ngRoute', 'ngCookies', 'ngSanitize', 'ngAnimate', 'ngMockE2E', 'StatisticsApp']);

    app.value('networkConfig', {
        defaultTimeout: 30000 // test: 1000
    });

    app.value('APP_CONFIG', {
        diagnosKapitelList: [{
            id: 'Id1',
            name: 'Val 1'
        },{
            id: 'Id2',
            name: 'Val 2'
        },{
            id: 'Id3',
            name: 'Val 3'
        }]
    });
    
    // Inject language resources
    app.run(['$rootScope', '$httpBackend',
        function($rootScope, $httpBackend) {
            $rootScope.lang = 'sv';
            $rootScope.DEFAULT_LANG = 'sv';

            //Kanske vi kan (i resp controller) sätta upp 'when' mockning så att direktiven kan köra som i en sandbox
            // (Se exempel i arendehantering.controller.js)?
            // Detta kanske gör det möjligt att kunna laborera med ett direktivs alla funktioner som även kräver backendkommunikation.
            $httpBackend.whenGET(/^\/api\/*/).respond(200);
            $httpBackend.whenPOST(/^\/api\/*/).respond(200);
            $httpBackend.whenPUT(/^\/api\/*/).respond(200);

            $httpBackend.whenGET(/^\/moduleapi\/*/).respond(200);
            $httpBackend.whenPOST(/^\/moduleapi\/*/).respond(200);
           // $httpBackend.whenPUT(/^\/moduleapi\/*/).respond(200);

            //Ev. templates skall få hämtas på riktigt
            $httpBackend.whenGET(/^.+\.html/).passThrough();

        }]);

    // Inject language resources
    statApp.run(['$rootScope', 'messageService', 'UserModel',
        function($rootScope, messageService) {

            stMessages.sv['showcase.tooltip'] = 'här visas  tooltiptexten'; // jshint ignore:line

            messageService.addResources(stMessages);// jshint ignore:line

            $rootScope.lang = 'sv';
            $rootScope.DEFAULT_LANG = 'sv';

        }]);

}());
