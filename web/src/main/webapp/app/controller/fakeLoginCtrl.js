/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

angular.module('StatisticsApp').controller('fakeLoginCtrl',
    /** @ngInject */
    function($scope, $rootScope, _, $http) {
      'use strict';

      $rootScope.hideNavigationTabs = true;

      $scope.selectedPerson = '0';
      $scope.fakeUserContext = '';

      $scope.selectPerson = function() {
        var copy = _.cloneDeep($scope.identities[$scope.selectedPerson]);
        delete copy.description;
        $scope.fakeUserContext = angular.toJson(copy, 2);
      };

      $scope.reset = function() {
        $scope.selectedPerson = '0';
        $scope.selectPerson(0);
      };

      // Add / modify identities here. Note that the 'description' isn't part of the FakeCredentials
      // and are discarded before sending the user context to the /fake endpoint.
      $scope.identities = [
        {
          'fornamn': 'Arnold',
          'efternamn': 'Johansson',
          'hsaId': 'TSTNMT2321000156-1079',
          'vardgivarIdSomProcessLedare': ['SE2321000214-E000002'],
          'vardgivarniva': 'true', 'description': 'Processledare'
        },
        {
          'fornamn': 'Arnold',
          'efternamn': 'Johansson B',
          'hsaId': 'TSTNMT2321000156-1079B',
          'vardgivarIdSomProcessLedare': ['TSE2321000214-E000002'],
          'vardgivarniva': 'false', 'description': 'Ej Processledare'
        },
        {
          'fornamn': 'Anna',
          'efternamn': 'Modig',
          'hsaId': 'HSA-AM',
          'vardgivarIdSomProcessLedare': ['vg1'],
          'vardgivarniva': 'true', 'description': 'Processledare vg1 och Rehabstöd-Vårdgivare1'
        },
        {
          'fornamn': 'Anna',
          'efternamn': 'Urmodig',
          'hsaId': 'HSA-AM1',
          'vardgivarIdSomProcessLedare': ['SE162321000255-O00001'],
          'vardgivarniva': 'false', 'description': 'Storregionen - ej processledare'
        },
        {
          'fornamn': 'Anna',
          'efternamn': 'Vemodig',
          'hsaId': 'HSA-AM2',
          'vardgivarIdSomProcessLedare': ['SE162321000255-O00001'],
          'vardgivarniva': 'false', 'description': 'SE162321000255-O11635 - ej processledare'
        },
        {
          'fornamn': 'Vgr',
          'efternamn': 'User1',
          'hsaId': 'test1',
          'vardgivarIdSomProcessLedare': ['SE2321000131-E000000000001'],
          'vardgivarniva': 'true', 'description': 'Storregionen'
        },
        {
          'fornamn': 'Capio',
          'efternamn': 'User1',
          'hsaId': 'capio-user-1',
          'vardgivarIdSomProcessLedare': ['SE162321000255-O19774'],
          'vardgivarniva': 'true', 'description': 'Capio Citykliniken Lund'
        },
        {
          'fornamn': 'Leonie',
          'efternamn': 'Koehl',
          'hsaId': 'TSTNMT2321000156-103F',
          'vardgivarIdSomProcessLedare': ['TSTNMT2321000156-1002'],
          'vardgivarniva': 'true', 'description': 'Enhet 1 nmt_vg1'
        },
        {
          'fornamn': 'Erik',
          'efternamn': 'Nilsson',
          'hsaId': 'TSTNMT2321000156-105H',
          'vardgivarIdSomProcessLedare': ['TSTNMT2321000156-1002'],
          'vardgivarniva': 'true', 'description': 'Enhet 2 nmt_vg1'
        },
        {
          'fornamn': 'Susanne',
          'efternamn': 'Karlsson',
          'hsaId': 'TSTNMT2321000156-105J',
          'vardgivarIdSomProcessLedare': ['TSTNMT2321000156-1002'],
          'vardgivarniva': 'true', 'description': 'Enhet 2 nmt_vg1'
        },
        {
          'fornamn': 'Pelle',
          'efternamn': 'Uppdragslös',
          'hsaId': 'pelle-uppdragslos',
          'vardgivarIdSomProcessLedare': ['TSTNMT2321000156-1002'],
          'vardgivarniva': 'true', 'description': 'Enhet 2 nmt_vg1'
        },
        {
          'fornamn': 'Hans',
          'efternamn': 'Hosplösa',
          'hsaId': 'hans-hosplosa',
          'vardgivarIdSomProcessLedare': ['TSTNMT2321000156-1002'],
          'vardgivarniva': 'true', 'description': 'Enhet 2 nmt_vg1'
        },
        {
          'fornamn': 'Emma',
          'efternamn': 'Nilsson',
          'hsaId': 'TSTNMT2321000156-105R',
          'vardgivarIdSomProcessLedare': ['TSTNMT2321000156-105M'],
          'vardgivarniva': 'true', 'description': 'Enhet 1 Rehabstöd-Vårdgivare1'
        },
        {
          'fornamn': 'Anders',
          'efternamn': 'Karlsson',
          'hsaId': 'TSTNMT2321000156-105S',
          'vardgivarIdSomProcessLedare': ['TSTNMT2321000156-105M'],
          'vardgivarniva': 'true', 'description': 'Enhet 1 Rehabstöd-Vårdgivare1'
        },
        {
          'fornamn': 'Ingrid',
          'efternamn': 'Nilsson Olsson',
          'hsaId': 'TSTNMT2321000156-105T',
          'vardgivarIdSomProcessLedare': ['TSTNMT2321000156-105M'],
          'vardgivarniva': 'true', 'description': 'Enhet 1 Rehabstöd-Vårdgivare1'
        },
        {
          'fornamn': 'Nils',
          'efternamn': 'Ericsson',
          'hsaId': 'TSTNMT2321000156-105V',
          'vardgivarIdSomProcessLedare': ['TSTNMT2321000156-105M'],
          'vardgivarniva': 'true', 'description': 'Enhet 1 Rehabstöd-Vårdgivare1'
        },
        {
          'fornamn': 'Kerstin',
          'efternamn': 'Johansson',
          'hsaId': 'TSTNMT2321000156-105W',
          'vardgivarIdSomProcessLedare': ['TSTNMT2321000156-105M'],
          'vardgivarniva': 'true', 'description': 'Enhet 1 Rehabstöd-Vårdgivare1'
        },
        {
          'fornamn': 'Elin',
          'efternamn': 'Johansson',
          'hsaId': 'TSTNMT2321000156-1067',
          'vardgivarIdSomProcessLedare': ['TSTNMT2321000156-1061'],
          'vardgivarniva': 'true', 'description': 'Capio Citykliniken Lund'
        },
        {
          'fornamn': 'Lars',
          'efternamn': 'Olsson',
          'hsaId': 'TSTNMT2321000156-1066',
          'vardgivarIdSomProcessLedare': ['TSTNMT2321000156-1061'],
          'vardgivarniva': 'true', 'description': 'Capio Citykliniken Lund'
        },
        {
          'fornamn': 'Rolf',
          'efternamn': 'Rollöse',
          'hsaId': 'TSTNMT2321000156-105V',
          'vardgivarIdSomProcessLedare': ['TSTNMT2321000156-105M'],
          'vardgivarniva': 'true', 'description': 'Enhet 1 Rehabstöd-Vårdgivare1'
        },
        {
          'fornamn': 'Ralf',
          'efternamn': 'Enhetlöse',
          'hsaId': 'TSTNMT2321000156-998V',
          'vardgivarIdSomProcessLedare': ['TSTNMT2321000156-105M'],
          'vardgivarniva': 'true', 'description': 'Enhet 1 Rehabstöd-Vårdgivare1'
        }];

      $scope.selectPerson(0);

      var iAdateFormat = 'YYYY-MM-DDTHH:mm:ss';
      $scope.IA = {

        banner: {
          message: '',
          priority: 'HOG',
          application: 'INTYGSSTATISTIK',
          createdAt: moment().format(iAdateFormat),
          displayFrom: moment().subtract(1, 'days').format(iAdateFormat),
          displayTo: moment().add(7, 'days').format(iAdateFormat)
        }
      };

      $scope.clearIACache = function(evt) {
        evt.preventDefault();
        $http({
          url: '/services/api/ia-api/cache',
          method: 'DELETE',
          transformResponse: undefined
        }).then(
            function success() {
              $scope.IA.latestEvent = 'IA-cache tömd!';
            }, function error(response) {
              $scope.IA.latestEvent = 'Fel vid tömning av cache' + response.data;
            });
      };

      $scope.createIABanner = function() {
        $http({
          url: '/services/api/ia-api/banner',
          method: 'PUT',
          data: $scope.IA.banner
        }).then(
            function success() {
              $scope.IA.banner.message = '';
              $scope.IA.latestEvent = 'Banner skapad!';
            }, function error(response) {
              $scope.IA.latestEvent = 'Fel vid skapande av banner' + response.data;
            });
      };

    }
);
