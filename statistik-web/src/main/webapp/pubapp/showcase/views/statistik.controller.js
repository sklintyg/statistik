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

angular.module('showcase').controller('showcase.StatistikCtrl',
        [ '$scope', '$window', function($scope, $window) {
            'use strict';
            $scope.headerRows = [[{text:'Rubrik 1'},{text:"Rubrik 2"}]]; 
            $scope.rows = [{
            	  name: 'Rad 1',
            	  data: [1,2]
            	},{
            	  name: 'Rad 2',
            	  data: [3,4]
            	}];
            $scope.activeSjukskrivningslangdsFilters=['1 vecka', '2 veckor'];
            
            $scope.headerRows = [[{text:'Rubrik 1'},{text:"Rubrik 2"}, {text:"Rubrik 3"}]]; 
            $scope.rows = [{
            	  name: 'Rad 1',
            	  data: [1,2]
            	},{
            	  name: 'Rad 2',
            	  data: [3,4]
            	}];
            $scope.activeSjukskrivningslangdsFilters=['1 vecka', '2 veckor'];
            $scope.businesses = new Array();
            var data1 = {name : "Enhet 1"};
            var data2 = {name : "Enhet 2"};
            $scope.businesses = [];
            $scope.businesses.push(data1);
            $scope.businesses.push(data2);
            
            $scope.isCollapsed = true;
      

        } ]);
