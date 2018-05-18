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

angular.module('ibApp').factory('BestallningarProxy',
    function(ProxyTemplate) {
        'use strict';

        function _getBestallning(utredningsId) {
            var restPath = '/api/vardadmin/bestallningar/' + utredningsId;

            var config =  {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.getbestallning.title',
                    errorTextKey: 'server.error.getbestallning.text'
                }
            };

            return ProxyTemplate.getTemplate(restPath, config);
        }

        function _getBestallningarWithFilter(query) {
            var restPath = '/api/vardadmin/bestallningar';

            return ProxyTemplate.postTemplate(restPath, query, {});
        }

        function _getBestallningarFilterValues() {
            var restPath = '/api/vardadmin/bestallningar/list/filter';

            return ProxyTemplate.getTemplate(restPath, {});
        }

        function _getAvslutadeBestallningarWithFilter(query) {
            var restPath = '/api/vardadmin/bestallningar/avslutade';

            return ProxyTemplate.postTemplate(restPath, query, {});
        }

        function _getAvslutadeBestallningarFilterValues() {
            var restPath = '/api/vardadmin/bestallningar/avslutade/list/filter';

            return ProxyTemplate.getTemplate(restPath, {});
        }

        // Return public API for the service
        return {
            getBestallning: _getBestallning,
            getBestallningarWithFilter: _getBestallningarWithFilter,
            getBestallningarFilterValues : _getBestallningarFilterValues,
            getAvslutadeBestallningarWithFilter: _getAvslutadeBestallningarWithFilter,
            getAvslutadeBestallningarFilterValues : _getAvslutadeBestallningarFilterValues
        };
    });
