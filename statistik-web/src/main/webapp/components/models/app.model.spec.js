/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

describe('Model: AppModel', function () {
    'use strict';

    var AppModel;

    // load the controller's module
    beforeEach(module('StatisticsApp'));

    beforeEach(inject(function(_AppModel_) {
        AppModel = _AppModel_;
    }));

    it('get default values', function() {
        expect(AppModel.get().isLoggedIn).toBeFalsy();
        expect(AppModel.get().loginUrl).toEqual('');
        expect(AppModel.get().loginVisible).toBeFalsy();
    });

    it('payload', function() {

        AppModel.set({
            loggedIn: true,
            loginUrl: 'loginUrl',
            loginVisible: true
        });

        expect(AppModel.get().isLoggedIn).toBeTruthy();
        expect(AppModel.get().loginUrl).toEqual('loginUrl');
        expect(AppModel.get().loginVisible).toBeTruthy();
    });

});
