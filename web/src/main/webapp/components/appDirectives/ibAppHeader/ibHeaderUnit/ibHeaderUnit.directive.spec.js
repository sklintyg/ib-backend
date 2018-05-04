/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

describe('Directive: IbHeaderUnit', function() {
    'use strict';

    var initialMockedUser = {
        'hsaId': 'ib-user-3',
        'namn': 'Harald Alltsson',
        'currentRole': {'name': 'FMU_VARDADMIN', 'desc': 'FMU Vårdadministratör' },
        'currentlyLoggedInAt': {
            'id': 'IFV1239877878-104D',
            'name': 'WebCert-Enhet3',
            'type': 'VE',
            'parentName': 'WebCert-Vårdgivare2',
            'parentId': 'IFV1239877878-1043'
        }
    };



    // load the controller's module
    beforeEach(module('ibApp', function() {}));
    beforeEach(module('htmlTemplates'));

    var $compile;
    var $scope;
    var element;
    var StatPollService;
    var UserModel;

    // Store references to $scope and $compile
    // so they are available to all tests in this describe block
    beforeEach(inject(function(_$compile_, $rootScope, _UserModel_, _StatPollService_) {
        $compile = _$compile_;
        $scope = $rootScope.$new();
        StatPollService = _StatPollService_;

        spyOn(StatPollService, 'startPolling').and.callFake(function() {
        });

        //Set a fresh COPY of initial user model before each test, so tests don't affect each other.
        UserModel = _UserModel_;
        UserModel.set(angular.copy(initialMockedUser));
    }));
    afterEach(function() {
        expect(StatPollService.startPolling).toHaveBeenCalled();
    });

    function compileDirective() {

        element = $compile('<ib-header-unit/>')($scope);
        $scope.$digest();

        return element.isolateScope() || element.scope();
    }

    it('Should unit and parent unit name correctly', function() {
        compileDirective();

        //Assert
        expect($(element).find('#ib-header-unit-name').text()).toContain(initialMockedUser.currentlyLoggedInAt.name);
        expect($(element).find('#ib-header-unit-parent-name').text()).toContain(initialMockedUser.currentlyLoggedInAt.parentName);

    });
    it('Should only show parent unit name if samordnare on vg', function() {
        UserModel.set({
            currentlyLoggedInAt: {
                id: 'kronoberg',
                name: 'Landstinget Kronoberg',
                vardenheter: [],
                type: 'VG',
                samordnare: true
            }
        });

        compileDirective();


        //Assert
        expect($(element).find('#ib-header-unit-name').text()).toContain('Landstinget Kronoberg');
        expect($(element).find('#ib-header-unit-parent-name').length).toBe(0);

    });




});
