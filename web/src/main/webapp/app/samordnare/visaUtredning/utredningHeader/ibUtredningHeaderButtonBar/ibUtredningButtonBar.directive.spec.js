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

describe('ibUtredningButtonBar', function() {
    'use strict';

    var $rootScope;
    var $scope;
    var $controller;
    var $compile;
    var element;
    beforeEach(angular.mock.module('htmlTemplates'));
    beforeEach(angular.mock.module('ibApp'));
    beforeEach(angular.mock.module(function($provide) {
    }));

    beforeEach(angular.mock.inject([
        '$rootScope',
        function(_$rootScope_) {
        $rootScope = _$rootScope_;
        $scope = _$rootScope_.$new();

        // Instantiate directive.
        // gotcha: Controller and link functions will execute.
        element = $compile('<ib-utredning-button-bar view-state="viewState"></ib-utredning-button-bar>')($scope);
        $rootScope.$digest();

        // Grab controller instance
        $controller = element.controller('ibUtredningButtonBar');

        // Grab scope. Depends on type of scope.
        // See angular.element documentation.
        $scope = element.isolateScope();
    }]));

    describe('header show button logic', function() {

        beforeEach(function() {});

        describe('skicka button', function() {
            it('should show skicka button if intyg is not sent, revoked and patient is alive', function() {
                expect($scope.showSkickaButton()).toBeTruthy();
            });
        });
    });
});
