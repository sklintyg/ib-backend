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
    function($window, ProxyTemplate) {
        'use strict';

        var basePath = '/api/vardadmin/bestallningar';

        function _getBestallning(utredningsId) {
            var restPath = basePath + '/' + utredningsId;

            var config =  {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.getbestallning.title',
                    errorTextKey: 'server.error.getbestallning.text'
                }
            };

            return ProxyTemplate.getTemplate(restPath, config);
        }

        function _getBestallningarWithFilter(query) {
            var restPath = basePath;

            return ProxyTemplate.postTemplate(restPath, query, {});
        }

        function _getBestallningarFilterValues() {
            var restPath = basePath + '/list/filter';

            return ProxyTemplate.getTemplate(restPath, {});
        }

        function _getAvslutadeBestallningarWithFilter(query) {
            var restPath = basePath + '/avslutade';

            return ProxyTemplate.postTemplate(restPath, query, {});
        }

        function _getAvslutadeBestallningarFilterValues() {
            var restPath = basePath + '/avslutade/list/filter';

            return ProxyTemplate.getTemplate(restPath, {});
        }

        function _saveFakturerad(utredningId, fakturaId) {
            var restPath = basePath + '/' + utredningId + '/faktura';

            var request = {
                fakturaId: fakturaId
            };

            return ProxyTemplate.postTemplate(restPath, request, {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.savefaktura.title',
                    errorTextKey: 'server.error.savefaktura.text'
            }});
        }

        function _addInput(name, item) {
            return '<input type="hidden" name="' + name + '" value="' + item + '" />';
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

        // Return public API for the service
        return {
            getBestallning: _getBestallning,
            getBestallningarWithFilter: _getBestallningarWithFilter,
            getBestallningarFilterValues : _getBestallningarFilterValues,
            getAvslutadeBestallningarWithFilter: _getAvslutadeBestallningarWithFilter,
            getAvslutadeBestallningarFilterValues : _getAvslutadeBestallningarFilterValues,
            saveFakturerad: _saveFakturerad,
            excelReport: _excelReport,
            excelReportAvslutade: _excelReportAvslutade
        };
    });
