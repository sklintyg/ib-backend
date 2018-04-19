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

describe('Directive: IbUtredningSlutdatumFas', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('ibApp', function($provide) {

        var config = {
            utredningPaminnelseDagar: 5
        };

        $provide.value('APP_CONFIG', config);
    }));
    beforeEach(module('htmlTemplates'));

    var $compile;
    var $scope;
    var element;

    // Store references to $scope and $compile
    // so they are available to all tests in this describe block
    beforeEach(inject(function(_$compile_, $rootScope) {
        $compile = _$compile_;
        $scope = $rootScope.$new();

        var today = moment('2018-04-19').toDate();
        jasmine.clock().mockDate(today);
    }));

    afterEach(function() {
        jasmine.clock().uninstall();
    });

    function compileDirective(fas, slutdatum) {

        element = $compile('<ib-utredning-slutdatum-fas fas="fas" slutdatum="slutdatum" />')($scope);
        $scope.fas = fas;
        $scope.slutdatum = slutdatum;
        $scope.$digest();

        return element.isolateScope() || element.scope();
    }

    it('Should should show warning slutdatum passed', function() {
        var scope = compileDirective('', '2018-04-18');

        expect(scope.warningSlutDatumPassed).toBeTruthy();
        expect(scope.warningSlutDatumPaminnelsePassed).toBeFalsy();

        scope = compileDirective('', '2015-04-18');

        expect(scope.warningSlutDatumPassed).toBeTruthy();
        expect(scope.warningSlutDatumPaminnelsePassed).toBeFalsy();
    });

    it('Should should show warning slutdatum paminnelse', function() {
        var scope = compileDirective('', '2018-04-19');

        expect(scope.warningSlutDatumPassed).toBeFalsy();
        expect(scope.warningSlutDatumPaminnelsePassed).toBeTruthy();

        scope = compileDirective('', '2018-04-20');

        expect(scope.warningSlutDatumPassed).toBeFalsy();
        expect(scope.warningSlutDatumPaminnelsePassed).toBeTruthy();

        scope = compileDirective('', '2018-04-23');

        expect(scope.warningSlutDatumPassed).toBeFalsy();
        expect(scope.warningSlutDatumPaminnelsePassed).toBeTruthy();
    });

    it('Should should not show any warning if date >= utredningPaminnelseDagar', function() {
        var scope = compileDirective('', '2018-04-24');

        expect(scope.warningSlutDatumPassed).toBeFalsy();
        expect(scope.warningSlutDatumPaminnelsePassed).toBeFalsy();
    });

    it('Should should not show any warning if fas is REDOVISA_TOLK', function() {
        var scope = compileDirective('REDOVISA_TOLK', '2018-04-18');

        expect(scope.warningSlutDatumPassed).toBeFalsy();
        expect(scope.warningSlutDatumPaminnelsePassed).toBeFalsy();

        scope = compileDirective('REDOVISA_TOLK', '2018-04-23');

        expect(scope.warningSlutDatumPassed).toBeFalsy();
        expect(scope.warningSlutDatumPaminnelsePassed).toBeFalsy();
    });


});
