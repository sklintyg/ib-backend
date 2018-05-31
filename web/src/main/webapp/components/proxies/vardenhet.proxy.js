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

angular.module('ibApp').factory('VardenhetProxy',
    function(ProxyTemplate) {
        'use strict';

        function _getVardenhetKontaktPreference() {
            var restPath = '/api/vardenhet/preference';

            var config =  {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.getvardenhetpreference.title',
                    errorTextKey: 'server.error.getvardenhetpreference.text'
                }
            };

            return ProxyTemplate.getTemplate(restPath, config);
        }

        function _setVardenhetKontaktPreference(vardenhetPreferenceRequest) {
            var restPath = '/api/vardenhet/preference';

            var config =  {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.setvardenhetpreference.title',
                    errorTextKey: 'server.error.setvardenhetpreference.text'
                }
            };

            return ProxyTemplate.putTemplate(restPath, vardenhetPreferenceRequest, config);
        }

        function _getVardenhetSvarPreference() {
            var restPath = '/api/vardenhet/preference/svar';

            var config =  {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.getvardenhetpreference.title',
                    errorTextKey: 'server.error.getvardenhetpreference.text'
                }
            };

            return ProxyTemplate.getTemplate(restPath, config);
        }

        function _setVardenhetSvarPreference(vardenhetSvarPreferenceRequest) {
            var restPath = '/api/vardenhet/preference/svar';

            var config =  {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.setvardenhetpreference.title',
                    errorTextKey: 'server.error.setvardenhetpreference.text'
                }
            };

            return ProxyTemplate.putTemplate(restPath, vardenhetSvarPreferenceRequest, config);
        }

        function _getHsaInfo() {
            var restPath = '/api/vardenhet/fromhsa';

            var config =  {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.gethsainfo.title',
                    errorTextKey: 'server.error.gethsainfo.text'
                }
            };

            return ProxyTemplate.getTemplate(restPath, config);
        }
        // Return public API for the service
        return {
            getVardenhetKontaktPreference: _getVardenhetKontaktPreference,
            setVardenhetKontaktPreference: _setVardenhetKontaktPreference,
            getVardenhetSvarPreference: _getVardenhetSvarPreference,
            setVardenhetSvarPreference: _setVardenhetSvarPreference,
            getHsaInfo: _getHsaInfo
        };
    });
