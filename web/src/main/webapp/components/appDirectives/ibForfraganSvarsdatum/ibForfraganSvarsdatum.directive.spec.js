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

describe('Directive: IbForfraganSvarsdatum', function() {
    'use strict';

    // load the controller's module
    beforeEach(module('ibApp', function($provide) {

        var config = {
            forfraganPaminnelseDagar: 2
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

    function compileDirective(status, svarsdatum) {

        element = $compile('<ib-forfragan-svarsdatum status="status" svarsdatum="svarsdatum" />')($scope);
        $scope.status = status;
        $scope.svarsdatum = svarsdatum;
        $scope.$digest();

        return element.isolateScope() || element.scope();
    }

    it('Should should show warning slutdatum passed', function() {
        var scope = compileDirective('INKOMMEN', '2018-04-18');

        expect(scope.warningSvarsdatumPassed).toBeTruthy();
        expect(scope.warningSvarsdatumPaminnelsePassed).toBeFalsy();

        scope = compileDirective('INKOMMEN', '2015-04-18');

        expect(scope.warningSvarsdatumPassed).toBeTruthy();
        expect(scope.warningSvarsdatumPaminnelsePassed).toBeFalsy();
    });

    it('Should should show warning slutdatum paminnelse', function() {
        var scope = compileDirective('INKOMMEN', '2018-04-19');

        expect(scope.warningSvarsdatumPassed).toBeFalsy();
        expect(scope.warningSvarsdatumPaminnelsePassed).toBeTruthy();

        scope = compileDirective('INKOMMEN', '2018-04-20');

        expect(scope.warningSvarsdatumPassed).toBeFalsy();
        expect(scope.warningSvarsdatumPaminnelsePassed).toBeTruthy();
    });

    it('Should should not show any warning if date >= forfraganPaminnelseDagar', function() {
        var scope = compileDirective('INKOMMEN', '2018-04-21');

        expect(scope.warningSvarsdatumPassed).toBeFalsy();
        expect(scope.warningSvarsdatumPaminnelsePassed).toBeFalsy();
    });

    it('Should should not show any warning if fas is REDOVISA_TOLK', function() {
        var scope = compileDirective('AVVISAD', '2018-04-18');

        expect(scope.warningSvarsdatumPassed).toBeFalsy();
        expect(scope.warningSvarsdatumPaminnelsePassed).toBeFalsy();

        scope = compileDirective('AVVISAD', '2018-04-20');

        expect(scope.warningSvarsdatumPassed).toBeFalsy();
        expect(scope.warningSvarsdatumPaminnelsePassed).toBeFalsy();
    });


});
