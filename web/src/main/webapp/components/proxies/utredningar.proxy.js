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
    function($window, ProxyTemplate) {
        'use strict';

        var basePath = '/api/samordnare/utredningar';

        function _getUtredning(utredningsId) {
            var restPath = basePath + '/' + utredningsId;
            return ProxyTemplate.getTemplate(restPath);
        }

        function _getUtredningar() {
            var restPath = basePath;

            return ProxyTemplate.getTemplate(restPath);
        }

        function _getUtredningarWithFilter(query) {
            var restPath = basePath;

            return ProxyTemplate.postTemplate(restPath, query);
        }

        function _getAvslutadeUtredningarWithFilter(query) {
            var restPath = basePath + '/avslutade';

            return ProxyTemplate.postTemplate(restPath, query);
        }

        function _createInternForfragan(utredningsId, requestBody) {
            var restPath = basePath + '/' + utredningsId + '/createinternforfragan';

            return ProxyTemplate.postTemplate(restPath, requestBody);
        }

        function _tillDelaDirekt(utredningsId, requestBody) {
            var restPath = basePath + '/' + utredningsId + '/tilldeladirekt';

            return ProxyTemplate.postTemplate(restPath, requestBody);
        }

        function _saveBetaldVeId(utredningId, betaldVeId) {
            var restPath = basePath + '/' + utredningId + '/betald-ve-id';

            var request = {
                betalningId: betaldVeId
            };

            return ProxyTemplate.postTemplate(restPath, request);
        }

        function _saveFakturaFkId(utredningId, fakturaFkId) {
            var restPath = basePath + '/' + utredningId + '/faktura-fk-id';

            var request = {
                fakturaFkId: fakturaFkId
            };

            return ProxyTemplate.postTemplate(restPath, request);
        }

        function _saveBetaldFkId(utredningId, betaldFkId) {
            var restPath = basePath + '/' + utredningId + '/betald-fk-id';

            var request = {
                utbetalningId: betaldFkId
            };

            return ProxyTemplate.postTemplate(restPath, request);
        }

        function _excelReport(query) {
            var restPath = basePath + '/xlsx';

            var inputs = '';
            angular.forEach(query, function(value, key) {
                if (value !== null && value !== undefined) {
                    inputs += _addInput(key, value);
                }
            });

            //send request
            $window.jQuery('<form action="' + restPath + '" target="_blank" method="post">' + inputs + '</form>')
                .appendTo('body').submit().remove();
        }

        function _excelReportAvslutade(query) {
            var restPath = basePath + '/avslutade/xlsx';

            var inputs = '';
            angular.forEach(query, function(value, key) {
                if (value !== null && value !== undefined) {
                    inputs += _addInput(key, value);
                }
            });

            //send request
            $window.jQuery('<form action="' + restPath + '" target="_blank" method="post">' + inputs + '</form>')
                .appendTo('body').submit().remove();
        }

        function _addInput(name, item) {
            return '<input type="hidden" name="' + name + '" value="' + item + '" />';
        }

        // Return public API for the service
        return {
            getUtredning: _getUtredning,
            getUtredningar: _getUtredningar,
            getUtredningarWithFilter: _getUtredningarWithFilter,
            getAvslutadeUtredningarWithFilter: _getAvslutadeUtredningarWithFilter,
            createInternForfragan: _createInternForfragan,
            tilldelaDirekt: _tillDelaDirekt,
            saveBetaldVeId: _saveBetaldVeId,
            saveBetaldFkId: _saveBetaldFkId,
            saveFakturaFkId: _saveFakturaFkId,
            excelReport: _excelReport,
            excelReportAvslutade: _excelReportAvslutade
        };
    });
