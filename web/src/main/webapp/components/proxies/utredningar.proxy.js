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

angular.module('ibApp').factory('UtredningarProxy',
    function(ProxyTemplate) {
        'use strict';

        var basePath = '/api/samordnare/utredningar';

        function _getUtredning(utredningsId) {
            var restPath = basePath + '/' + utredningsId;

            var config =  {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.getutredning.title',
                    errorTextKey: 'server.error.getutredning.text'
                }
            };

            return ProxyTemplate.getTemplate(restPath, config);
        }

        function _getUtredningar() {
            var restPath = basePath;

            return ProxyTemplate.getTemplate(restPath, {});
        }

        function _getUtredningarWithFilter(query) {
            var restPath = basePath;

            return ProxyTemplate.postTemplate(restPath, query, {});
        }

        function _getAvslutadeUtredningarWithFilter(query) {
            var restPath = basePath + '/avslutade';

            return ProxyTemplate.postTemplate(restPath, query, {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.listavslutadeutredningar.title',
                    errorTextKey: 'server.error.listavslutadeutredningar.text'
                }
            });
        }

        function _createInternForfragan(utredningsId, requestBody) {
            var restPath = basePath + '/' + utredningsId + '/createinternforfragan';

            var config =  {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.createinternforfragan.title',
                    errorTextKey: 'server.error.createinternforfragan.text'
                }
            };

            return ProxyTemplate.postTemplate(restPath, requestBody, config);
        }

        function _tillDelaDirekt(utredningsId, requestBody) {
            var restPath = basePath + '/' + utredningsId + '/tilldeladirekt';

            var config = {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.tilldeladirekt.title',
                    errorTextKey: 'server.error.tilldeladirekt.text'
                }
            };

            return ProxyTemplate.postTemplate(restPath, requestBody, config);
        }

        // Return public API for the service
        return {
            getUtredning: _getUtredning,
            getUtredningar: _getUtredningar,
            getUtredningarWithFilter: _getUtredningarWithFilter,
            getAvslutadeUtredningarWithFilter: _getAvslutadeUtredningarWithFilter,
            createInternForfragan: _createInternForfragan,
            tilldelaDirekt: _tillDelaDirekt
        };
    });
