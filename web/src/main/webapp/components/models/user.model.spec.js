/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

describe('Model: UserModel', function() {
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
    expect(UserModel.get().hasRegionAccess).toBeFalsy();
    expect(UserModel.get().regionAvailable).toBeFalsy();
    expect(UserModel.get().isRegionAdmin).toBeFalsy();
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
        regionAdmin: false,
        regionsvardgivare: false,
        regionsvardgivareWithUpload: false
      }
    });

    expect(UserModel.get().userName).toEqual('Test user');
    expect(UserModel.get().userNameWithAccess).toEqual('Test user');
    expect(UserModel.get().loggedInWithoutStatistikuppdrag).toBeTruthy();
    expect(UserModel.get().isDelprocessledare).toBeFalsy();
    expect(UserModel.get().isProcessledare).toBeTruthy();
    expect(UserModel.get().hasRegionAccess).toBeFalsy();
    expect(UserModel.get().regionAvailable).toBeFalsy();
    expect(UserModel.get().isRegionAdmin).toBeFalsy();
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
        regionAdmin: true,
        regionsvardgivare: true,
        regionsvardgivareWithUpload: false
      }
    });

    expect(UserModel.get().userName).toEqual('Test user');
    expect(UserModel.get().userNameWithAccess).toEqual('Test user');
    expect(UserModel.get().loggedInWithoutStatistikuppdrag).toBeFalsy();
    expect(UserModel.get().isDelprocessledare).toBeTruthy();
    expect(UserModel.get().isProcessledare).toBeFalsy();
    expect(UserModel.get().hasRegionAccess).toBeTruthy();
    expect(UserModel.get().regionAvailable).toBeFalsy();
    expect(UserModel.get().isRegionAdmin).toBeTruthy();
    expect(UserModel.get().enableVerksamhetMenu).toBeTruthy();
  });
});
