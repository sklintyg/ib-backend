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

describe('Service: RedovisaBesok', function() {
    'use strict';

    // load the controller's module
    beforeEach(angular.mock.module('ibApp'));

    var redovisaBesokService;

    beforeEach(inject(function(_redovisaBesokService_) {
        redovisaBesokService = _redovisaBesokService_;
    }));

    it('should redovisa besok if besokTime has passed and it has status BOKAT, OMBOKAT or GENOMFORT', function() {

        var besokTemplate = {
            besokDatum: '2018-01-01',
            besokStartTid: '10:10',
            besokStatus: {
                id: 'BOKAT'
            }
        };

        var testBesok = angular.copy(besokTemplate);
        expect(redovisaBesokService.shouldBesokBeRedovisat(testBesok)).toBeTruthy();

        // Future dates are not allowed
        testBesok = angular.copy(besokTemplate);
        testBesok.besokDatum = '2999-01-01';
        expect(redovisaBesokService.shouldBesokBeRedovisat(testBesok)).toBeFalsy();

        // Invalid statuses aren't allowed
        testBesok = angular.copy(besokTemplate);
        testBesok.besokStatus.id = 'EJBOKAD';
        expect(redovisaBesokService.shouldBesokBeRedovisat(testBesok)).toBeFalsy();
    });

    it('should accept besokredovisningar that are marked as genomfort and either have no tolk or have tolk EJ_/DELTAGIT', function() {

        var besokTemplate = {
            tolkStatus: 'DELTAGIT',
            genomfort: true
        };

        var testBesok = angular.copy(besokTemplate);
        expect(redovisaBesokService.isBesokRedovisningValid(testBesok)).toBeTruthy();

        testBesok = angular.copy(besokTemplate);
        testBesok.tolkStatus = 'EJDELTAGIT';
        expect(redovisaBesokService.isBesokRedovisningValid(testBesok)).toBeTruthy();

        testBesok = angular.copy(besokTemplate);
        testBesok.tolkStatus = 'EJBOKAD';
        expect(redovisaBesokService.isBesokRedovisningValid(testBesok)).toBeTruthy();

        // BOKAT not valid status for tolk
        testBesok = angular.copy(besokTemplate);
        testBesok.tolkStatus = 'BOKAT';
        expect(redovisaBesokService.isBesokRedovisningValid(testBesok)).toBeFalsy();

        // must be genomfort to be allowed to redovisa
        testBesok = angular.copy(besokTemplate);
        testBesok.genomfort = false;
        expect(redovisaBesokService.isBesokRedovisningValid(testBesok)).toBeFalsy();

        // two wrongs should not make a right
        testBesok = angular.copy(besokTemplate);
        testBesok.tolkStatus = 'BOKAT';
        testBesok.genomfort = false;
        expect(redovisaBesokService.isBesokRedovisningValid(testBesok)).toBeFalsy();
    });

});
