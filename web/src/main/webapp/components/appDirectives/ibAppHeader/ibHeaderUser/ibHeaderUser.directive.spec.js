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

describe('Directive: IbHeaderUser', function() {
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

    // Store references to $scope and $compile
    // so they are available to all tests in this describe block
    beforeEach(inject(function(_$compile_, $rootScope, UserModel) {
        $compile = _$compile_;
        $scope = $rootScope.$new();

        //Set a fresh COPY of initial user model before each test, so tests don't affect each other.
        UserModel.set(angular.copy(initialMockedUser));
    }));


    function compileDirective() {

        element = $compile('<ib-header-user/>')($scope);
        $scope.$digest();

        return element.isolateScope() || element.scope();
    }

    it('Should name and role description correctly', function() {
        compileDirective();

        //Assert
        expect($(element).find('#ib-header-user-name').text()).toContain(initialMockedUser.namn);
        expect($(element).find('#ib-header-user-role').text()).toContain(initialMockedUser.currentRole.desc);

    });




});
