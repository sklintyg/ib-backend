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

describe('ibUtredningStatus', function() {
    'use strict';

    var UtredningStatusService;

    beforeEach(angular.mock.module('htmlTemplates'));
    beforeEach(angular.mock.module('ibApp'));
    beforeEach(inject(['UtredningStatusService',
        function(_UtredningStatusService_) {
            UtredningStatusService = _UtredningStatusService_;
        }]));

    describe('Intyg status ', function() {

        it('without code should always be last', function() {

            var statuses = [{
                code: 'is-001',
                timestamp:'2018-02-27T16:22:59'
            },{
                timestamp:'2018-02-27T16:22:59'
            },{
                code: 'is-001',
                timestamp:'2018-02-27T16:22:59'
            }];

            UtredningStatusService.sortByStatusAndTimestamp(statuses);
            expect(statuses[0].code).toBe('is-001');
            expect(statuses[1].code).toBe('is-001');
            expect(statuses[2].code).toBeUndefined();
        });

    });
});
