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

describe('Model: UserModel', function() {
    'use strict';

    // Load the module and mock away everything that is not necessary.
    beforeEach(module('ibApp'));

    var UserModel;
    var testJsonData = {
        'hsaId': 'ib-user-3',
        'namn': 'Harald Alltsson',
        'titel': '',
        'authenticationScheme': 'urn:inera:intygsbestallning:siths:fake',

        'authoritiesTree': [ {
            'id': 'kronoberg',
            'name': 'Landstinget Kronoberg',
            'vardenheter': [],
            'type': 'VG',
            'samordnare': true
        }, {
            'id': 'ostergotland',
            'name': 'Landstinget Östergötland',
            'vardenheter': [ {
                'id': 'linkoping',
                'name': 'Linköpings Universitetssjukhus',
                'type': 'VE',
                'parentName': 'Landstinget Östergötland',
                'parentId': 'ostergotland'
            } ],
            'type': 'VG',
            'samordnare': false
        }, {
            'id': 'IFV1239877878-1041',
            'name': 'WebCert-Vårdgivare1',
            'vardenheter': [ {
                'id': 'IFV1239877878-1042',
                'name': 'WebCert-Enhet1',
                'type': 'VE',
                'parentName': 'WebCert-Vårdgivare1',
                'parentId': 'IFV1239877878-1041'
            } ],
            'type': 'VG',
            'samordnare': true
        }, {
            'id': 'IFV1239877878-1043',
            'name': 'WebCert-Vårdgivare2',
            'vardenheter': [ {
                'id': 'IFV1239877878-104D',
                'name': 'WebCert-Enhet3',
                'type': 'VE',
                'parentName': 'WebCert-Vårdgivare2',
                'parentId': 'IFV1239877878-1043'
            } ],
            'type': 'VG',
            'samordnare': false
        } ],
        'currentRole': {
            'name': 'FMU_VARDADMIN',
            'desc': 'FMU Vårdadministratör'
        },
        'currentlyLoggedInAt': {
            'id': 'IFV1239877878-1042',
            'name': 'WebCert-Enhet1',
            'type': 'VE',
            'parentName': 'WebCert-Vårdgivare1',
            'parentId': 'IFV1239877878-1041'
        }
    };

    // Initialize the controller and a mock scope
    beforeEach(inject(function(_UserModel_) {
        UserModel = _UserModel_;
    }));

    describe('set', function() {
        it('should set name correctly', function() {
            UserModel.set(testJsonData);
            expect(UserModel.get().namn).toEqual('Harald Alltsson');

        });
    });
    describe('selectableSystemroles count', function() {
        it('should report selectableSystemroles correctly', function() {
            UserModel.set(testJsonData);
            expect(UserModel.getSelectableSystemRolesCount()).toEqual(5);
            UserModel.get().authoritiesTree[0].samordnare = false;
            expect(UserModel.getSelectableSystemRolesCount()).toEqual(4);
            UserModel.get().authoritiesTree[1].vardenheter = [];
            expect(UserModel.getSelectableSystemRolesCount()).toEqual(3);
        });
    });
});
