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

angular.module('ibApp').factory('ibVardgivareService',
    function($log, VardgivareProxy) {
        'use strict';

        var model = {
            vardenheter: [],
            hasVardenheter: false,
            hasEgnaVardenheter: false,
            error: undefined,
            loading: true
        };

        function _getVardenheter() {

            model.loading = true;
            model.error = undefined;

            VardgivareProxy.getVardenheter().then(function(vardenheter) {
                model.vardenheter = vardenheter;
                angular.forEach(vardenheter, function(vardenheterList) {
                    if (vardenheterList.length > 0) {
                        model.hasVardenheter = true;
                    }
                });
                if (vardenheter.egetLandsting.length > 0) {
                    model.hasEgnaVardenheter = true;
                }
            }, function(error) {
                model.error = 'skicka.forfragan.vardenheter.error';
                $log.error('failed to load vardenheter for vardgivare!' + error);
            }).finally(function() { // jshint ignore:line
                model.loading = false;
            });
        }

        return {
            model: model,
            getVardenheter: _getVardenheter
        };

    });
