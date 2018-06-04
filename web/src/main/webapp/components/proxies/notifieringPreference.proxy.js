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

angular.module('ibApp').factory('NotifieringPreferenceProxy',
    function(ProxyTemplate) {
        'use strict';
        var restPath = '/api/notifiering/preference';

        function _getNotifieringPreference() {


            var config =  {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.getvardeCCCCCnhetpreference.title',
                    errorTextKey: 'server.error.sssss.text'
                }
            };

            return ProxyTemplate.getTemplate(restPath, config);
        }

        function _setNotifieringPreference(notifieringPreference) {

            var config =  {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.setvaCCCCCCCCrdenhetpreference.title',
                    errorTextKey: 'server.error.setvardenhCCCCCCCetpreference.text'
                }
            };

            return ProxyTemplate.putTemplate(restPath, notifieringPreference, config);
        }


        // Return public API for the service
        return {
            getNotifieringPreference: _getNotifieringPreference,
            setNotifieringPreference: _setNotifieringPreference
        };
    });