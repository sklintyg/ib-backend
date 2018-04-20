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

xdescribe('wcHeaderUnit Directive', function() {
    'use strict';

    var $initialScope;
    var directiveScope;
    var compile;
    var element;
    var $uibModal;
    var UserModel;
    var statService;
    var $httpBackend;
    var $state;
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
    var testStatResponse = {
        'fragaSvarValdEnhet': 12,
        'fragaSvarAndraEnheter': 2,
        'intygAndraEnheter': 2,
        'intygValdEnhet': 10,
        'vardgivare': [ {
            'namn': 'Landstinget Västmanland',
            'id': 'VG1',
            'vardenheter': [ {
                'namn': 'Vårdcentrum i Väst',
                'id': 'VG1-VE1',
                'fragaSvar': 11,
                'intyg': 0
            }, {
                'namn': 'Vårdcentrum i Väst - Akuten',
                'id': 'VG1-VE2',
                'fragaSvar': 0,
                'intyg': 0
            } ]
        } ]
    };

    function runDirective() {
        element = compile('<wc-header-unit></wc-header-unit>')($initialScope);
        $initialScope.$digest();

        $initialScope.$broadcast('statService.stat-update', testStatResponse);
        $initialScope.$digest();
        directiveScope = element.isolateScope() || element();
    }

    beforeEach(module('htmlTemplates'));
    beforeEach(module('ibApp'));

    beforeEach(inject([ '$compile', '$rootScope', '$httpBackend', '$state', '$uibModal', 'common.statService', 'common.UserModel',
            function($compile, $rootScope, _$httpBackend_, _$state_, _$uibModal_, _statService_, _UserModel_) {
                compile = $compile;
                $httpBackend = _$httpBackend_;
                $state = _$state_;
                $initialScope = $rootScope.$new();
                $uibModal = _$uibModal_;
                UserModel = _UserModel_;
                statService = _statService_;

                spyOn(statService, 'startPolling').and.callFake(function() {
                });
                spyOn(statService, 'getLatestData').and.returnValue(testStatResponse);
                spyOn(statService, 'refreshStat').and.callFake(function() {
                });
                spyOn($state, 'go').and.callFake(function() {
                });

                //Set a fresh COPY of initial user model before each test, so tests don't affect each other.
                UserModel.setUser(angular.copy(initialMockedUser));

            } ]));

    afterEach(function() {
        expect(statService.startPolling).toHaveBeenCalled();
    });

    describe('Verify ability to change unit', function() {

        it('Should NOT show expand menu if only 1 unit to choose from', function() {
            //Arrange
            UserModel.user.totaltAntalVardenheter = 1;

            //Act
            runDirective();

            //Assert
            expect($(element).find('#expand-unitmenu-btn').length).toBe(0);
        });

        it('Should NOT show expand menu even if several available units but no privilege', function() {
            //Arrange
            UserModel.user.totaltAntalVardenheter = 2;
            UserModel.user.authorities.ATKOMST_ANDRA_ENHETER = undefined;

            //Act
            runDirective();

            //Assert
            expect($(element).find('#expand-unitmenu-btn').length).toBe(0);
        });

        it('Should show expand menu if several available units and correct privilege', function() {
            //Arrange
            UserModel.user.totaltAntalVardenheter = 2;

            //Act
            runDirective();

            //Assert
            expect($(element).find('#expand-unitmenu-btn').length).toBe(1);
        });

        it('should expand menu when clicked', function() {
            //Arrange
            UserModel.user.totaltAntalVardenheter = 2;

            //Act
            runDirective();

            //Assert
            expect($(element).find('#expand-unitmenu-btn').length).toBe(1);
            expect(directiveScope.menu.expanded).toBeFalsy();

            //Act again..
            var eventSpy = jasmine.createSpyObj([ 'stopPropagation' ]);
            directiveScope.toggleMenu(eventSpy);
            directiveScope.$digest();

            //Assert again..
            expect(directiveScope.menu.expanded).toBeTruthy();
            expect($(element).find('#wc-care-unit-clinic-selector-link').length).toBe(1);

        });

        it('should open dialog when wc-care-unit-clinic-selector-link is clicked', function() {
            //Arrange
            UserModel.user.totaltAntalVardenheter = 2;

            //Act
            runDirective();
            var eventSpy = jasmine.createSpyObj([ 'stopPropagation' ]);
            directiveScope.toggleMenu(eventSpy);
            directiveScope.$digest();

            //Assert
            expect(directiveScope.menu.expanded).toBeTruthy();
            expect($(element).find('#wc-care-unit-clinic-selector-link').length).toBe(1);

            spyOn($uibModal, 'open').and.callThrough();

            //open dialog
            $(element).find('#wc-care-unit-clinic-selector-link').click();

            expect($uibModal.open).toHaveBeenCalledWith(jasmine.objectContaining({
                id: 'wcChangeActiveUnitDialog'
            }));

        });

        it('should change active unit when selecting other unit in dialog', function() {
            //Arrange
            UserModel.user.totaltAntalVardenheter = 2;
            var afterChangeResponse = angular.copy(initialMockedUser);
            afterChangeResponse.valdVardenhet = {
                'id': 'VG1-VE2',
                'namn': 'WebCert-Enhet2'
            };

            $httpBackend.expectPOST('/api/anvandare/andraenhet').respond(200, afterChangeResponse);

            //Act
            runDirective();
            var eventSpy = jasmine.createSpyObj([ 'stopPropagation' ]);
            directiveScope.toggleMenu(eventSpy);
            directiveScope.$digest();

            //Assert
            expect(directiveScope.menu.expanded).toBeTruthy();
            expect($(element).find('#wc-care-unit-clinic-selector-link').length).toBe(1);

            spyOn($uibModal, 'open').and.callThrough();

            //open dialog
            $(element).find('#wc-care-unit-clinic-selector-link').click();

            expect($uibModal.open).toHaveBeenCalledWith(jasmine.objectContaining({
                id: 'wcChangeActiveUnitDialog'
            }));

            //Click on VG1-VE2
            expect($(document).find('#select-active-unit-VG1-VE2-modal').length).toBe(1);

            $(document).find('#select-active-unit-VG1-VE2-modal').click();
            $httpBackend.flush();

            expect(directiveScope.getUser().valdVardenhet.id).toBe('VG1-VE2');
            expect(statService.refreshStat).toHaveBeenCalled();
            expect($state.go).toHaveBeenCalled();


        });

    });

    describe('Verify scope methods', function() {

        it('Should show showEnhetName when not PP', function() {
            //Arrange

            //Act
            runDirective();

            //Assert
            expect(directiveScope.showEnhetName()).toBeTruthy();
            expect($(element).find('.ve-name').length).toBe(1);
            expect($(element).find('.ve-name').text()).toContain(initialMockedUser.valdVardenhet.namn);
        });

        it('Should not show showEnhetName when PP', function() {
            //Arrange
            UserModel.user.roles = {
                'PRIVATLAKARE': {}
            };

            //Act
            runDirective();

            //Assert
            expect(directiveScope.showEnhetName()).toBeFalsy();
            expect($(element).find('.ve-name').length).toBe(0);
        });

        it('Should not showInactiveUnitStatus when parameter missing', function() {
            //Arrange

            //Act
            runDirective();

            //Assert
            expect(directiveScope.showInactiveUnitStatus()).toBeFalsy();
            expect($(element).find('.status-row').text()).not.toContain('inaktiv');
        });

        it('Should showInactiveUnitStatus when parameter present', function() {
            //Arrange
            UserModel.user.parameters = {
                'inactiveUnit': true
            };

            //Act
            runDirective();

            //Assert
            expect(directiveScope.showInactiveUnitStatus()).toBeTruthy();
            expect($(element).find('.status-row').text()).toContain('inaktiv');
        });

    });

    describe('Verify other units stats methods', function() {

        it('Should show other-units-stats when stats > 0', function() {
            //Arrange
            //Act
            runDirective();

            //Assert
            expect(directiveScope.showStatsStatus()).toBeTruthy();
            expect($(element).find('.status-row').text()).toContain('4 ej hanterade');
        });

        it('Should not show other-units-stats when stats < 1', function() {
            //Arrange
            //Act
            runDirective();

            directiveScope.stat.intygAndraEnheter = 0;
            directiveScope.stat.fragaSvarAndraEnheter = 0;
            directiveScope.$digest();

            //Assert
            expect(directiveScope.showStatsStatus()).toBeFalsy();
            expect($(element).find('.status-row').text()).not.toContain('ej hanterade');
        });

        it('Should not show other-units-stats when djupintegrerad', function() {
            //Arrange
            UserModel.user.origin = 'DJUPINTEGRATION';

            //Act
            runDirective();

            //Assert
            expect(directiveScope.showStatsStatus()).toBeFalsy();
            expect($(element).find('.status-row').text()).not.toContain('ej hanterade');
        });
    });

});
