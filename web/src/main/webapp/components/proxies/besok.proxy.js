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

angular.module('ibApp').factory('BesokProxy',
    function(ProxyTemplate) {
        'use strict';
        var besokRestPath = '/api/vardadmin/besok/';

        function _createBesok(besok) {
            var restPath = besokRestPath;

            return ProxyTemplate.putTemplate(restPath, besok, {});
        }

        function _createBesokAvvikelse(besok) {
            var restPath = besokRestPath + 'avvikelse';

            return ProxyTemplate.putTemplate(restPath, besok, {});
        }

        function _getProffessionsTyper() {
            var restPath = besokRestPath + 'professiontyper';

            return ProxyTemplate.getTemplate(restPath, {});
        }

        function _addArbetsdagar(datum, arbetsdagar) {
            var restPath = besokRestPath + 'addarbetsdagar';

            return ProxyTemplate.postTemplate(restPath, {datum: datum, arbetsdagar: arbetsdagar}, {});
        }

        // Return public API for the service
        return {
            createBesok: _createBesok,
            createBesokAvvikelse: _createBesokAvvikelse,
            getProffessionsTyper: _getProffessionsTyper,
            addArbetsdagar: _addArbetsdagar
        };
    });
