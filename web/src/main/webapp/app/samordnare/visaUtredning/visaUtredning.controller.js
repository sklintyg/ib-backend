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

angular.module('ibApp')
    .controller('VisaUtredningCtrl',
        function($log, $scope, $stateParams, UtredningarProxy) {
            'use strict';

            var config = {
                tabs: [],
                intygContext: {
                    type: 'fk7263',
                    id: null,
                }
            };

            config.tabs.push({
                id: 'wc-arende-panel-tab',
                title: 'common.supportpanel.arende.title',
                tooltip: 'common.supportpanel.arende.tooltip',
                config: {
                    intygContext: config.intygContext
                }
            });

            config.tabs.push({
                id: 'wc-help-tips-panel-tab',
                title: 'common.supportpanel.help.title',
                tooltip: 'common.supportpanel.help.tooltip',
                config: {
                    intygContext: config.intygContext
                }
            });

            $scope.config = config;

            /*
behovTolk
:
false
besvarasSenastDatum
:
"2018-04-25"
handlaggareEpost
:
"epost@inera.se"
handlaggareNamn
:
"Hanna Handläggarsson"
handlaggareTelefonnummer
:
"031-9999999"
inkomDatum
:
"2018-04-11"
invanarePersonId
:
null
status
:
"TODO"
tolkSprak
:
null
utredningsId
:
"utredning-bootstrap-1"
utredningsTyp
:
"AFU"
vardgivareHsaId
:
"IFV1239877878-1041"

             */

            UtredningarProxy.getUtredning($stateParams.utredningsId).then(function(utredning) {
                $scope.utredning = utredning;
            }, function(error) {
                $log.error(error);
            });
        }
    );
