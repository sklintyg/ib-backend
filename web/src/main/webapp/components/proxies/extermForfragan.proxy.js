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

angular.module('ibApp').factory('ExternForfraganProxy',
    function(ProxyTemplate) {
        'use strict';

        function _acceptExternforfragan(utredningsId, requestBody) {
            var restPath = '/api/externforfragningar/' + utredningsId + '/accept';

            var config = {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.acceptexternforfragan.title',
                    errorTextKey: 'server.error.acceptexternforfragan.text',
                    errorHsaTextKey: 'server.error.acceptexternforfragan.hsa.text',
                    errorMyndighetTextKey: 'server.error.acceptexternforfragan.myndighet.text'
                }
            };

            return ProxyTemplate.postTemplate(restPath, requestBody, config);
        }

        function _avvisaExternforfragan(utredningsId, requestBody) {
            var restPath = '/api/externforfragningar/' + utredningsId + '/avvisa';

            var config = {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.acceptexternforfragan.title',
                    errorTextKey: 'server.error.acceptexternforfragan.text',
                    errorMyndighetTextKey: 'server.error.acceptexternforfragan.myndighet.text'
                }
            };

            return ProxyTemplate.postTemplate(restPath, requestBody, config);
        }

        // Return public API for the service
        return {
            acceptExternForfragan: _acceptExternforfragan,
            avvisaExternForfragan: _avvisaExternforfragan
        };
    });