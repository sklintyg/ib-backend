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

angular.module('ibApp').factory('InternForfraganProxy',
    function(ProxyTemplate) {
        'use strict';

        function _getForfragningar(query) {
            var restPath = '/api/internforfragningar';

            return ProxyTemplate.postTemplate(restPath, query, {});
        }
        function _getInternForfragning(utredningsId) {
            var restPath = '/api/internforfragningar/' + utredningsId;

            return ProxyTemplate.getTemplate(restPath, {});
        }

        function _getForfragningarFilterValues() {
            var restPath = '/api/internforfragningar/list/filter';

            return ProxyTemplate.getTemplate(restPath, {});
        }

        // Return public API for the service
        return {
            getForfragningar: _getForfragningar,
            getForfragningarFilterValues: _getForfragningarFilterValues,
            getInternForfragning: _getInternForfragning
        };
    });
