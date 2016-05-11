/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

describe('Controller: pageCtrl', function() {
    'use strict';

    beforeEach(function() {
        module('StatisticsApp');
        module('StatisticsApp.treeMultiSelector');
    });

    var ctrl;

    beforeEach(inject(function($rootScope, $controller, $window) {
        var scope = $rootScope.$new();
        ctrl = $controller('pageCtrl', {
            $scope: scope,
            $rootScope: $rootScope,
            $window: $window,
            $cookies: {},
            statisticsData: {},
            businessFilter: {}}
        );
    }));
    
    it('should select correct selected verksamhet from available verksamhets', function() {
        var verksamhet1 = { vardgivarId: 'id1', name: 'Verksamhet1' };
        var verksamhet2 = { vardgivarId: 'id2', name: 'Verksamhet2' };
        expect(ctrl.getSelectedVerksamhet('id1', [ verksamhet1, verksamhet2 ])).toEqual(verksamhet1);
    });

    it('should return default verksamhet if no verksamhet matches', function() {
        var verksamhet1 = { vardgivarId: 'id1', name: 'Verksamhet1' };
        var verksamhet2 = { vardgivarId: 'id2', name: 'Verksamhet2' };
        expect(ctrl.getSelectedVerksamhet('id3', [ verksamhet1, verksamhet2 ])).toEqual({ name: 'Okänd verksamhet'});
    });

});
