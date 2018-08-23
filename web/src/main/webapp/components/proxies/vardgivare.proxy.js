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

angular.module('ibApp').factory('VardgivareProxy',
    function(ProxyTemplate) {
        'use strict';

        var endpointBaseUrl = '/api/samordnare/vardgivare/vardenheter';

        function _getVardenheter() {
            return ProxyTemplate.getTemplate(endpointBaseUrl, {});
        }

        function _getVardenheterWithFilter(query) {
            return ProxyTemplate.postTemplate(endpointBaseUrl, query, {});
        }

        function _updateRegiForm(vardenhetHsaId, regiForm) {
            var payload = {
                regiForm: regiForm
            };

            return ProxyTemplate.putTemplate(endpointBaseUrl + '/' + vardenhetHsaId, payload);
        }

        function _deleteVardenhet(vardenhetHsaId) {
            return ProxyTemplate.deleteTemplate(endpointBaseUrl + '/' + vardenhetHsaId);
        }

        function _findVardenhetByHsaId(vardenhetHsaId) {
            return ProxyTemplate.getTemplate(endpointBaseUrl + '/' + vardenhetHsaId, {});
        }

        function _addVardenhet(vardenhetHsaId, regiForm) {
            var payload = {
                regiForm: regiForm
            };

            return ProxyTemplate.postTemplate(endpointBaseUrl + '/' + vardenhetHsaId, payload);

        }

        // Return public API for the service
        return {
            getVardenheter: _getVardenheter,
            getVardenheterWithFilter: _getVardenheterWithFilter,
            updateRegiForm: _updateRegiForm,
            deleteVardenhet: _deleteVardenhet,
            findVardenhetByHsaId: _findVardenhetByHsaId,
            addVardenhet: _addVardenhet
        };
    });
