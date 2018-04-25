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

xdescribe('ibHeaderUser Directive', function() {
    'use strict';

    var $initialScope;
    var directiveScope;
    var compile;
    var element;
    var $uibModal;
    var $q;
    var $window;
    var UserModel;
    var initialMockedUser = {
        'privatLakareAvtalGodkand': false,
        'namn': 'Åsa Andersson',
        'vardgivare': [ {
            'id': 'VG1',
            'namn': 'WebCert-Vårdgivare1',
            'vardenheter': [ {
                'id': 'VG1-VE1',
                'namn': 'WebCert-Enhet1'
            }, {
                'id': 'VG1-VE2',
                'namn': 'WebCert-Enhet2'
            } ]
        } ],
        'valdVardenhet': {
            'id': 'VG1-VE1',
            'namn': 'WebCert-Enhet1'
        },
        'valdVardgivare': {
            'id': 'VG1',
            'namn': 'WebCert-Vårdgivare2',
            'vardenheter': [ {
                'id': 'VG1-VE1',
                'namn': 'WebCert-Enhet1'
            }, {
                'id': 'VG1-VE2',
                'namn': 'WebCert-Enhet2'
            } ]
        },
        'authenticationMethod': 'FAKE',
        'features': {
            'HANTERA_INTYGSUTKAST': {
                'name': 'HANTERA_INTYGSUTKAST',
                'desc': 'Hantera intygsutkast',
                'global': true,
                'intygstyper': [ 'fk7263', 'ts-bas', 'ts-diabetes', 'luse', 'lisjp', 'luae_na', 'luae_fs', 'db', 'doi' ]
            }
        },
        'roles': {
            'LAKARE': {
                'name': 'LAKARE',
                'desc': 'Läkare'
            }
        },
        'authorities': {

            'NAVIGERING': {
                'name': 'NAVIGERING',
                'desc': 'Navigera i menyer, på logo, via tillbakaknappar',
                'intygstyper': [],
                'requestOrigins': [ {
                    'name': 'NORMAL',
                    'intygstyper': []
                }, {
                    'name': 'UTHOPP',
                    'intygstyper': []
                } ]
            },
            'ATKOMST_ANDRA_ENHETER': {
                'name': 'ATKOMST_ANDRA_ENHETER',
                'desc': 'Åtkomst andra vårdenheter',
                'intygstyper': [],
                'requestOrigins': [ {
                    'name': 'NORMAL',
                    'intygstyper': []
                }, {
                    'name': 'UTHOPP',
                    'intygstyper': []
                } ]
            }
        },
        'origin': 'NORMAL',
        'anvandarPreference': {},
        'sekretessMarkerad': false,
        'lakare': true,
        'privatLakare': false,
        'totaltAntalVardenheter': 2
    };

    var mockedModuleConfig = {
        PP_HOST: 'PP_HOST:9999',
        DASHBOARD_URL: 'DASHBOARD_URL'
    };

    function getSekretessLink() {
        return $(element).find('#wc-vardperson-sekretess-info-dialog--link');
    }

    function runDirective() {
        element = compile('<wc-header-user></wc-header-user>')($initialScope);
        $initialScope.$digest();

        directiveScope = element.isolateScope() || element();
    }

    beforeEach(module('htmlTemplates'));
    beforeEach(module('ibApp'));

    //mocks
    beforeEach(module(function($provide) {

        $provide.constant('moduleConfig', mockedModuleConfig);

        $window = {
            location: {
                href: null
            }
        };
        $provide.value('$window', $window);

    }));

    beforeEach(inject([ '$compile', '$rootScope', '$uibModal', '$window', '$q', 'common.UserModel',
            function($compile, $rootScope, _$uibModal_, _$window_, _$q_, _UserModel_) {
                compile = $compile;
                $initialScope = $rootScope.$new();
                $uibModal = _$uibModal_;
                $window = _$window_;
                $q = _$q_;
                UserModel = _UserModel_;

                //Set a fresh COPY of initial user model before each test, so tests don't affect each other.
                UserModel.setUser(angular.copy(initialMockedUser));

            } ]));

    describe('Verify user name and role', function() {
        it('Should show users name and role', function() {
            //Act
            runDirective();
            //Assert
            expect($(element).find('#wc-header-user-name').text()).toContain(UserModel.user.namn);
            expect($(element).find('#wc-header-user-role').text()).toContain(UserModel.user.role);
        });
    });

    describe('Verify PP user menu', function() {

        it('Should NOT show expand menu if user not PP', function() {
            //Arrange

            //Act
            runDirective();

            //Assert
            expect($(element).find('#expand-usermenu-btn').length).toBe(0);
        });

        it('Should show show expand menu if user not PP', function() {
            //Arrange
            UserModel.user.roles.PRIVATLAKARE = {};

            //Act
            runDirective();

            //Assert
            expect($(element).find('#expand-usermenu-btn').length).toBe(1);
        });

        it('should expand menu when clicked', function() {
            //Arrange
            UserModel.user.roles.PRIVATLAKARE = {};

            //Act
            runDirective();

            //Assert
            expect($(element).find('#expand-usermenu-btn').length).toBe(1);
            expect(directiveScope.menu.expanded).toBeFalsy();

            //Act again..Should be able to click expand button
            $(element).find('#expand-usermenu-btn').click();

            //Assert again..
            expect(directiveScope.menu.expanded).toBeTruthy();
            expect($(element).find('#editUserLink').length).toBe(1);

        });

        it('should change url when editUserLink is clicked', function() {
            //Arrange
            UserModel.user.roles.PRIVATLAKARE = {};

            //Act
            runDirective();
            $(element).find('#expand-usermenu-btn').click();

            //Assert
            expect(directiveScope.menu.expanded).toBeTruthy();
            expect($(element).find('#editUserLink').length).toBe(1);

            //then click link
            $(element).find('#editUserLink').click();

            //Assert
            expect($window.location.href).toContain(mockedModuleConfig.PP_HOST);
            expect($window.location.href).toContain(mockedModuleConfig.DASHBOARD_URL);

        });

    });

    describe('Verify sekretess info message', function() {

        it('Should not show sekretessmessage if not sekretessmarkerad', function() {
            //Arrange
            //Act
            runDirective();
            //Assert
            expect(getSekretessLink().length).toBe(0);
        });

        it('Should show sekretessmessage dialog if sekretessmarkerad', function() {
            //Arrange
            UserModel.user.sekretessMarkerad = true;
            UserModel.setAnvandarPreference('wc.vardperson.sekretess.approved', true);
            //return a modal instance with a result promise
            spyOn($uibModal, 'open').and.callFake(function() {
                return {
                    result: $q.defer().promise
                };
            });

            //Act
            runDirective();

            //Assert
            expect(getSekretessLink().text()).toContain('Sekretessmarkering');

            //trigger open dialog
            getSekretessLink().click();

            //Assert
            expect($uibModal.open).toHaveBeenCalledWith(jasmine.objectContaining({
                id: 'SekretessInfoMessage'
            }));

        });

        it('Should launch sekretess confirmation dialog dialog if sekretessmarkerad and not confirmed', function() {

            //One coould argue that testing the wcVardPersonSekretess directives beahviour here is out of scope, why not
            //make sure it's there and at least is triggered.

            //Arrange
            UserModel.user.sekretessMarkerad = true;

            //return a modal instance with a result promise
            spyOn($uibModal, 'open').and.callFake(function() {
                return {
                    result: $q.defer().promise
                };
            });

            //Act
            runDirective();

            //Assert
            // The embedded wcVardPersonSekretess directive should reacted on this, and lauch the confirmation dialog..
            expect($uibModal.open).toHaveBeenCalledWith(jasmine.objectContaining({
                id: 'SekretessConsentDialog'
            }));

        });
    });

});
