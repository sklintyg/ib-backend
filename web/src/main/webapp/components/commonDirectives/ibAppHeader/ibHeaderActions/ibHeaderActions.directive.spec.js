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
//must be global.
var iid_Invoke; // jshint ignore:line
xdescribe('ibHeaderHelp Directive', function() {
    'use strict';
    var $initialScope;
    var directiveScope;
    var compile;
    var element;
    var $uibModal;
    var $q;
    var $window;
    var authorityService;
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
        PP_HOST: 'PP_HOST:9998',
        DASHBOARD_URL: 'DASHBOARD_URL'
    };

    function compileDirective(user) {
        $initialScope.testUserArgument = user;
        element = compile('<ib-header-help user="testUserArgument"></ib-header-help>')($initialScope);
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

    function getAboutLink() {
        return $(element).find('#aboutLink');
    }
    function getLogoutLink() {
        return $(element).find('#logoutLink');
    }

    function getCreateAccountLink() {
        return $(element).find('#createAccountLink');
    }

    beforeEach(inject([ '$compile', '$rootScope', '$uibModal', '$window', '$q', 'common.authorityService',
            function($compile, $rootScope, _$uibModal_, _$window_, _$q_, _authorityService_) {
                compile = $compile;
                $initialScope = $rootScope.$new();
                $uibModal = _$uibModal_;
                $window = _$window_;
                $q = _$q_;
                authorityService = _authorityService_;
            } ]));

    describe('verify link display', function() {
        it('Should only show create account link when not logged in', function() {
            //Arrange
            //Act
            compileDirective(undefined);

            //Assert
            expect(directiveScope.vm.showAbout).toBe(false);
            expect(getAboutLink().length).toBe(0);

            expect(getLogoutLink().length).toBe(0);
            expect(directiveScope.vm.showLogout).toBe(false);

            expect(directiveScope.vm.showCreateAccount).toBe(true);
            expect(getCreateAccountLink().length).toBe(1);

        });

        it('Should show about and logout for normal user having NAVIGERING', function() {
            //Arrange
            spyOn(authorityService, 'isAuthorityActive').and.callFake(function() {
                return true;
            });
            //Act
            compileDirective(initialMockedUser);

            //Assert
            expect(getAboutLink().length).toBe(1);
            expect(directiveScope.vm.showAbout).toBe(true);

            expect(getLogoutLink().length).toBe(1);
            expect(directiveScope.vm.showLogout).toBe(true);

            expect(getCreateAccountLink().length).toBe(0);
            expect(directiveScope.vm.showCreateAccount).toBe(false);
            expect(authorityService.isAuthorityActive).toHaveBeenCalledWith(jasmine.objectContaining({
                authority: 'NAVIGERING'
            }));

        });

        it('Should not show logout if not having NAVIGERING privilege', function() {
            //Arrange
            spyOn(authorityService, 'isAuthorityActive').and.callFake(function() {
                return false;
            });
            //Act
            compileDirective(initialMockedUser);

            //Assert
            expect(getAboutLink().length).toBe(1);
            expect(directiveScope.vm.showAbout).toBe(true);

            expect(getLogoutLink().length).toBe(0);
            expect(directiveScope.vm.showLogout).toBe(false);

            expect(getCreateAccountLink().length).toBe(0);
            expect(directiveScope.vm.showCreateAccount).toBe(false);
            expect(authorityService.isAuthorityActive).toHaveBeenCalledWith(jasmine.objectContaining({
                authority: 'NAVIGERING'
            }));

        });

    });

    describe('verify link click actions', function() {
        it('should navigate to privatlakarportalen when clicking createAccountlink', function() {
            //Arrange
            //Act
            compileDirective(undefined);

            //Assert
            expect(directiveScope.vm.showCreateAccount).toBe(true);
            expect(getCreateAccountLink().length).toBe(1);

            getCreateAccountLink().click();

            //Assert
            expect($window.location.href).toContain(mockedModuleConfig.PP_HOST);
        });

        it('should navigate to logout url when clicking logout', function() {
            //Arrange
            iid_Invoke = jasmine.createSpy('invoke'); // jshint ignore:line

            spyOn(authorityService, 'isAuthorityActive').and.callFake(function() {
                return true;
            });

            //Act
            compileDirective(initialMockedUser);

            //Assert
            expect(getLogoutLink().length).toBe(1);
            expect(directiveScope.vm.showLogout).toBe(true);

            getLogoutLink().click();

            //Assert
            expect($window.location).toContain('/saml/logout/');

        });

    });

});