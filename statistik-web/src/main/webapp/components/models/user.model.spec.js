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

describe('Model: UserModel', function () {
    'use strict';

    var UserModel;

    // load the controller's module
    beforeEach(module('StatisticsApp'));

    beforeEach(inject(function(_UserModel_) {
        UserModel = _UserModel_;
    }));

    it('get default values', function() {
        expect(UserModel.get().userName).toEqual('');
        expect(UserModel.get().userNameWithAccess).toEqual('');
        expect(UserModel.get().loggedInWithoutStatistikuppdrag).toBeTruthy();
        expect(UserModel.get().isDelprocessledare).toBeFalsy();
        expect(UserModel.get().isProcessledare).toBeFalsy();
        expect(UserModel.get().hasLandstingAccess).toBeFalsy();
        expect(UserModel.get().landstingAvailable).toBeFalsy();
        expect(UserModel.get().isLandstingAdmin).toBeFalsy();
        expect(UserModel.get().enableVerksamhetMenu).toBeFalsy();
    });

    it('payload without businesses', function() {

        UserModel.setLoginInfo({
            name: 'Test user',
            vgs: []
        });
        UserModel.setUserAccessInfo({
            businesses: [],
            vgInfo: {
                verksamhetschef: false,
                delprocessledare: false,
                processledare: true,
                landstingAdmin: false,
                landstingsvardgivare: false,
                landstingsvardgivareWithUpload: false
            }
        });

        expect(UserModel.get().userName).toEqual('Test user');
        expect(UserModel.get().userNameWithAccess).toEqual('Test user');
        expect(UserModel.get().loggedInWithoutStatistikuppdrag).toBeTruthy();
        expect(UserModel.get().isDelprocessledare).toBeFalsy();
        expect(UserModel.get().isProcessledare).toBeTruthy();
        expect(UserModel.get().hasLandstingAccess).toBeFalsy();
        expect(UserModel.get().landstingAvailable).toBeFalsy();
        expect(UserModel.get().isLandstingAdmin).toBeFalsy();
        expect(UserModel.get().enableVerksamhetMenu).toBeFalsy();
    });

    it('payload with businesses', function() {

        UserModel.setLoginInfo({
            name: 'Test user',
            vgs: ['VG1']
        });
        UserModel.setUserAccessInfo({
            businesses: [{
                id: 'VG1-ENHET-1'
            }],
            vgInfo: {
                verksamhetschef: true,
                delprocessledare: true,
                processledare: false,
                landstingAdmin: true,
                landstingsvardgivare: true,
                landstingsvardgivareWithUpload: false
            }
        });

        expect(UserModel.get().userName).toEqual('Test user');
        expect(UserModel.get().userNameWithAccess).toEqual('Test user');
        expect(UserModel.get().loggedInWithoutStatistikuppdrag).toBeFalsy();
        expect(UserModel.get().isDelprocessledare).toBeTruthy();
        expect(UserModel.get().isProcessledare).toBeFalsy();
        expect(UserModel.get().hasLandstingAccess).toBeTruthy();
        expect(UserModel.get().landstingAvailable).toBeFalsy();
        expect(UserModel.get().isLandstingAdmin).toBeTruthy();
        expect(UserModel.get().enableVerksamhetMenu).toBeTruthy();
    });
});
