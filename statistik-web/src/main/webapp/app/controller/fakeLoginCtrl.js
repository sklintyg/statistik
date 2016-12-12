/*
 * Copyright (C) 2013 - 2014 Inera AB (http://www.inera.se)
 *
 *     This file is part of Inera Statistics (http://code.google.com/p/inera-statistics/).
 *
 *     Inera Statistics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Inera Statistics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *     You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

angular.module('StatisticsApp').controller('fakeLoginCtrl',
    /** @ngInject */
    function($scope) {
        'use strict';

        $scope.selectedPerson = '0';
        $scope.fakeUserContext = '';

        $scope.selectPerson = function() {
            $scope.fakeUserContext = angular.toJson($scope.identities[$scope.selectedPerson], 2);
        };

        $scope.reset = function() {
            $scope.selectedPerson = '0';
            $scope.selectPerson(0);
        };

        $scope.identities = [
            {
                'fornamn': 'Anna',
                'efternamn': 'Modig',
                'hsaId': 'HSA-AM',
                'vardgivarId': ['vg1','TSTNMT2321000156-105M']
            },
            {
                'fornamn': 'Anna',
                'efternamn': 'Urmodig',
                'hsaId': 'HSA-AM1',
                'enhetId': 'SE162321000255-O19466',
                'vardgivarId': 'SE162321000255-O00001',
                'vardgivarniva': 'false'
            },
            {
                'fornamn': 'Anna',
                'efternamn': 'Vemodig',
                'hsaId': 'HSA-AM2',
                'enhetId': 'SE162321000255-O11635',
                'vardgivarId': 'SE162321000255-O00001',
                'vardgivarniva': 'false'
            },
            {
                'fornamn': 'Vgr',
                'efternamn': 'User1',
                'hsaId': 'test1',
                'enhetId': 'SE2321000131-E000000000431',
                'vardgivarId': 'SE2321000131-E000000000001',
                'vardgivarniva': 'true'
            },
            {
                'fornamn': 'Capio',
                'efternamn': 'User1',
                'hsaId': 'capio-user-1',
                'enhetId': 'SE162321000255-O16010',
                'vardgivarId': 'SE162321000255-O19774',
                'vardgivarniva': 'true'
            },
            {
                'fornamn': 'Leonie',
                'efternamn': 'Koehl',
                'hsaId': 'TSTNMT2321000156-103F',
                'enhetId': 'TSTNMT2321000156-1039',
                'vardgivarId': 'TSTNMT2321000156-1002',
                'vardgivarniva': 'true'
            },
            {
                'fornamn': 'Erik',
                'efternamn': 'Nilsson',
                'hsaId': 'TSTNMT2321000156-105H',
                'enhetId': 'TSTNMT2321000156-105F',
                'vardgivarId': 'TSTNMT2321000156-1002',
                'vardgivarniva': 'true'
            },
            {
                'fornamn': 'Susanne',
                'efternamn': 'Karlsson',
                'hsaId': 'TSTNMT2321000156-105J',
                'enhetId': 'TSTNMT2321000156-105F',
                'vardgivarId': 'TSTNMT2321000156-1002',
                'vardgivarniva': 'true'
            },
            {
                'fornamn': 'Pelle',
                'efternamn': 'Uppdragslös',
                'hsaId': 'pelle-uppdragslos',
                'enhetId': 'TSTNMT2321000156-105F',
                'vardgivarId': 'TSTNMT2321000156-1002',
                'vardgivarniva': 'true'
            },
            {
                'fornamn': 'Hans',
                'efternamn': 'Hosplösa',
                'hsaId': 'hans-hosplosa',
                'enhetId': 'TSTNMT2321000156-105F',
                'vardgivarId': 'TSTNMT2321000156-1002',
                'vardgivarniva': 'true'
            },
            {
                'fornamn': 'Emma',
                'efternamn': 'Nilsson',
                'hsaId': 'TSTNMT2321000156-105R',
                'enhetId': 'TSTNMT2321000156-105N',
                'vardgivarId': 'TSTNMT2321000156-105M',
                'vardgivarniva': 'true'
            },
            {
                'fornamn': 'Anders',
                'efternamn': 'Karlsson',
                'hsaId': 'TSTNMT2321000156-105S',
                'enhetId': 'TSTNMT2321000156-105N',
                'vardgivarId': 'TSTNMT2321000156-105M',
                'vardgivarniva': 'true'
            },
            {
                'fornamn': 'Ingrid',
                'efternamn': 'Nilsson Olsson',
                'hsaId': 'TSTNMT2321000156-105T',
                'enhetId': 'TSTNMT2321000156-105P',
                'vardgivarId': 'TSTNMT2321000156-105M',
                'vardgivarniva': 'true'
            },
            {
                'fornamn': 'Nils',
                'efternamn': 'Ericsson',
                'hsaId': 'TSTNMT2321000156-105V',
                'enhetId': 'TSTNMT2321000156-105N',
                'vardgivarId': 'TSTNMT2321000156-105M',
                'vardgivarniva': 'true'
            },
            {
                'fornamn': 'Kerstin',
                'efternamn': 'Johansson',
                'hsaId': 'TSTNMT2321000156-105W',
                'enhetId': 'TSTNMT2321000156-105P',
                'vardgivarId': 'TSTNMT2321000156-105M',
                'vardgivarniva': 'true'
            },
            {
                'fornamn': 'Elin',
                'efternamn': 'Johansson',
                'hsaId': 'TSTNMT2321000156-1067',
                'enhetId': 'TSTNMT2321000156-1062',
                'vardgivarId': 'TSTNMT2321000156-1061',
                'vardgivarniva': 'true'
            },
            {
                'fornamn': 'Lars',
                'efternamn': 'Olsson',
                'hsaId': 'TSTNMT2321000156-1066',
                'enhetId': 'TSTNMT2321000156-1062',
                'vardgivarId': 'TSTNMT2321000156-1061',
                'vardgivarniva': 'true'
            },
            {
                'fornamn': 'Rolf',
                'efternamn': 'Rollöse',
                'hsaId': 'TSTNMT2321000156-105V',
                'enhetId': 'TSTNMT2321000156-105N',
                'vardgivarId': 'TSTNMT2321000156-105M',
                'vardgivarniva': 'true'
            },
            {
                'fornamn': 'Ralf',
                'efternamn': 'Enhetlöse',
                'hsaId': 'TSTNMT2321000156-998V',
                'enhetId': 'TSTNMT2321000156-105N',
                'vardgivarId': 'TSTNMT2321000156-105M',
                'vardgivarniva': 'true'
            }];

         $scope.selectPerson(0);

    }
);
