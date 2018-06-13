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

angular.module('ibApp').factory('InternForfraganProxy', function(ProxyTemplate) {
    'use strict';

    var basePath = '/api/vardadmin/internforfragningar';

    function _getForfragningar(query) {
        var restPath = basePath;

        return ProxyTemplate.postTemplate(restPath, query, {});
    }
    function _getInternForfragning(utredningsId) {
        var restPath = basePath + '/' + utredningsId;

        return ProxyTemplate.getTemplate(restPath, {});
    }

    function _getForfragningarFilterValues() {
        var restPath = basePath + '/list/filter';

        return ProxyTemplate.getTemplate(restPath, {});
    }
    function _accepteraInternForfragan(utredningsId, payload) {
        var config = {
            errorMessageConfig: {
                errorTitleKey: 'server.error.accepterainternforfragan.title',
                errorTextKey: 'server.error.accepterainternforfragan.text'
            }
        };
        var restPath = basePath + '/' + utredningsId + '/besvara';
        return ProxyTemplate.postTemplate(restPath, payload, config);
    }
    function _avbojInternForfragan(utredningsId, payload) {
        var config = {
            errorMessageConfig: {
                errorTitleKey: 'server.error.avbojinternforfragan.title',
                errorTextKey: 'server.error.avbojinternforfragan.text'
            }
        };
        var restPath = basePath + '/' + utredningsId + '/besvara';
        return ProxyTemplate.postTemplate(restPath, payload, config);
    }

    // Return public API for the service
    return {
        getForfragningar: _getForfragningar,
        getForfragningarFilterValues: _getForfragningarFilterValues,
        getInternForfragning: _getInternForfragning,
        accepteraInternForfragan: _accepteraInternForfragan,
        avbojInternForfragan: _avbojInternForfragan
    };
});
