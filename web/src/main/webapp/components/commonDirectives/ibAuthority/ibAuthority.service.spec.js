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

describe('ibAuthorityService', function() {
    'use strict';

    var authorityService;
    var UserModel;

    var testUser = {
        'hsaId':'ib-user-1',
        'namn':'Simona Samordnare',
        'titel':'',
        'authenticationScheme':'urn:inera:intygsbestallning:siths:fake',
        'roles':{
            'FMU_SAMORDNARE':{
                'name':'FMU_SAMORDNARE',
                'desc':'FMU Samordnare',
                'privileges':[
                    {
                        'name':'LISTA_UTREDNINGAR',
                        'desc':'Lista utredningar',
                        'intygstyper':[],
                        'requestOrigins':[]
                    },
                    {
                        'name':'VISA_UTREDNING',
                        'desc':'Visa utredning',
                        'intygstyper':[],
                        'requestOrigins':[]
                    },
                    {
                        'name':'HANTERA_VARDENHETER_FOR_VARDGIVARE',
                        'desc':'Hantera vårdenheter för vårdgivare',
                        'intygstyper':[],
                        'requestOrigins':[]
                    },
                    {
                        'name':'AVVISA_EXTERNFORFRAGAN',
                        'desc':'Avvisa Externförfrågningar',
                        'intygstyper':[],
                        'requestOrigins':[]
                    }
                ]
            }
        },
        'features':{},
        'authoritiesTree':[
            {
                'id':'IFV1239877878-1041',
                'name':'WebCert-Vårdgivare1',
                'vardenheter':[],
                'type':'VG',
                'samordnare':true
            }
        ],
        'currentRole':{
            'name':'FMU_SAMORDNARE',
            'desc':'FMU Samordnare',
            'privileges':[
                {
                    'name':'LISTA_UTREDNINGAR',
                    'desc':'Lista utredningar',
                    'intygstyper':[],
                    'requestOrigins':[]
                },
                {
                    'name':'VISA_UTREDNING',
                    'desc':'Visa utredning',
                    'intygstyper':[],
                    'requestOrigins':[]
                },
                {
                    'name':'HANTERA_VARDENHETER_FOR_VARDGIVARE',
                    'desc':'Hantera vårdenheter för vårdgivare',
                    'intygstyper':[],
                    'requestOrigins':[]
                },
                {
                    'name':'AVVISA_EXTERNFORFRAGAN',
                    'desc':'Avvisa Externförfrågningar',
                    'intygstyper':[],
                    'requestOrigins':[]
                }
            ]
        },
        'currentlyLoggedInAt':{
            'id':'IFV1239877878-1041',
            'name':'WebCert-Vårdgivare1',
            'vardenheter':[],
            'type':'VG',
            'samordnare':true
        }
    };

    beforeEach(module('ibApp'));

    beforeEach(angular.mock.inject(['ibAuthorityService', 'UserModel',
        function(_authorityService_, _UserModel_) {
            authorityService = _authorityService_;
            UserModel = _UserModel_;
            UserModel.set(testUser);
        }
    ]));

    describe('#AuthorityService - role checking', function() {

        it ('should be false when user does not have role', function () {
            expect(authorityService.isAuthorityActive({role:'DUMMY_ROLE'})).toBeFalsy();
        });

        it ('should be true when user have role', function () {
            expect(authorityService.isAuthorityActive({role:'FMU_SAMORDNARE'})).toBeTruthy();
        });
    });

    describe('#AuthorityService - privilege checking', function() {

        it ('should be false when user does not have privilege', function () {
            expect(authorityService.isAuthorityActive({authority:'DUMMY_PREVILEDGE'})).toBeFalsy();
        });
        it ('should be true when user does have privilege', function () {
            expect(authorityService.isAuthorityActive({authority:'LISTA_UTREDNINGAR'})).toBeTruthy();
        });

    });

});
