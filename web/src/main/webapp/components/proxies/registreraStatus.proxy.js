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

angular.module('ibApp').factory('RegistreraStatusProxy',
    function(ProxyTemplate) {
        'use strict';

        function registerStatusProxy(restPath, requestData, utredningsId, errorMessageKey) {
            restPath = restPath.replace('{utredningsId}', utredningsId);

            return ProxyTemplate.putTemplate(restPath, requestData, {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.' + errorMessageKey + '.title',
                    errorTextKey: 'server.error.' + errorMessageKey + '.text'
                }
            });
        }

        function registerReceivedKomplettering(date, utredningsId) {
            return registerStatusProxy(
                '/api/vardadmin/bestallningar/{utredningsId}/komplettering/fragestallningmottagen',
                {
                    fragestallningMottagenDatum: date
                },
                utredningsId,
                'registerkompletteringreceived'
            );
        }

        function registerSentUtlatande(date, utredningsId) {
            return registerStatusProxy(
                '/api/vardadmin/bestallningar/{utredningsId}/sendutlatande',
                {
                    utlatandeSentDate: date
                },
                utredningsId,
                'registersentutlatande'
            );
        }

        function registerReceivedHandling(date, utredningsId) {
            return registerStatusProxy(
                '/api/vardadmin/handlingar/{utredningsId}',
                {
                    handlingarMottogsDatum: date
                },
                utredningsId,
                'registerreceived'
            );
        }

        function registerSentKomplettering(date, utredningsId) {
            return registerStatusProxy(
                '/api/vardadmin/bestallningar/{utredningsId}/komplettering/skickad',
                {
                    kompletteringSkickadDatum: date
                },
                utredningsId,
                'registerkompletteringreceived'
            );
        }

        function abortUtredning(utredningsId) {
            return registerStatusProxy(
                '/api/vardadmin/bestallningar/{utredningsId}/avsluta',
                {},
                utredningsId,
                'abortutredning'
            );
        }

        // Return public API for the service
        return {
            abortUtredning: abortUtredning,
            registerReceivedKomplettering: registerReceivedKomplettering,
            registerSentKomplettering: registerSentKomplettering,
            registerSentUtlatande: registerSentUtlatande,
            registerReceivedHandling: registerReceivedHandling
        };
    });
