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

angular.module('ibApp').factory('VardgivareProxy',
    function($http, $log, $q, networkConfig) {
        'use strict';

        var endpointBaseUrl = '/api/vardgivare/vardenheter';

        function _getVardenheter() {
            var promise = $q.defer();

            var config =  {
                timeout: networkConfig.defaultTimeout
            };

            $http.get(endpointBaseUrl, config).then(function(response) {
                if (typeof response !== 'undefined') {
                    promise.resolve(response.data);
                } else {
                    $log.debug('JSON response syntax error. Rejected.');
                    promise.reject(null);
                }
            }, function(response) {
                $log.error('error ' + response.status);
                promise.reject(response.data);
            });

            return promise.promise;
        }

        function _getVardenheterWithFilter(query) {
            var promise = $q.defer();

            var config =  {
                timeout: networkConfig.defaultTimeout
            };

            $http.post(endpointBaseUrl, query, config).then(function(response) {

                if (typeof response !== 'undefined') {
                    promise.resolve(response.data);
                } else {
                    $log.debug('JSON response syntax error. Rejected.');
                    promise.reject(null);
                }
            }, function(response) {
                $log.error('error ' + response.status);
                promise.reject(response.data);
            });

            return promise.promise;
        }

        function _updateRegiForm(vardenhetHsaId, regiForm) {
            var promise = $q.defer();

            var payload = {
                regiForm: regiForm
            };

            var config =  {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.updateregiform.title',
                    errorTextKey: 'server.error.updateregiform.text'
                },
                timeout: networkConfig.defaultTimeout
            };

            $http.put(endpointBaseUrl + '/' + vardenhetHsaId, payload, config).then(function(response) {
                if (typeof response !== 'undefined') {
                    promise.resolve(response.data);
                } else {
                    $log.debug('JSON response syntax error. Rejected.');
                    promise.reject(null);
                }
            }, function(response) {
                $log.error('error ' + response.status);
                promise.reject(response.data);
            });

            return promise.promise;
        }

        function _deleteVardenhet(vardenhetHsaId) {
            var promise = $q.defer();

            var config =  {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.deletevardenhet.title',
                    errorTextKey: 'server.error.deletevardenhet.text'
                },
                timeout: networkConfig.defaultTimeout
            };

            $http.delete(endpointBaseUrl + '/' + vardenhetHsaId, config).then(function(response) { // jshint ignore:line
                if (typeof response !== 'undefined') {
                    promise.resolve(response.data);
                } else {
                    $log.debug('JSON response syntax error. Rejected.');
                    promise.reject(null);
                }
            }, function(response) {
                $log.error('error ' + response.status);
                promise.reject(response.data);
            });

            return promise.promise;
        }

        function _findVardenhetByHsaId(vardenhetHsaId) {
            var promise = $q.defer();


            var config =  {
                timeout: networkConfig.defaultTimeout
            };

                $http.get(endpointBaseUrl + '/' + vardenhetHsaId, config).then(function(response) {
                    if (typeof response !== 'undefined') {
                        promise.resolve(response.data);
                    } else {
                        $log.debug('JSON response syntax error. Rejected.');
                        promise.reject(null);
                    }
                }, function(response) {
                    $log.error('error ' + response.status);
                    promise.reject(response.data);
                });

            return promise.promise;
        }

        function _addVardenhet(vardenhetHsaId, regiForm) {
            var promise = $q.defer();

            var payload = {
                regiForm: regiForm
            };

            var config =  {
                errorMessageConfig: {
                    errorTitleKey: 'server.error.addvardenhet.title',
                    errorTextKey: 'server.error.addvardenhet.text'
                },
                timeout: networkConfig.defaultTimeout
            };

            $http.post(endpointBaseUrl + '/' + vardenhetHsaId, payload, config).then(function(response) {
                if (typeof response !== 'undefined') {
                    promise.resolve(response.data);
                } else {
                    $log.debug('JSON response syntax error. Rejected.');
                    promise.reject(null);
                }
            }, function(response) {
                $log.error('error ' + response.status);
                promise.reject(response.data);
            });

            return promise.promise;
        }

        // Return public API for the service
        return {
            getVardenheter: _getVardenheter,
            getVardenheterWithFilter: _getVardenheterWithFilter,
            updateRegiForm: _updateRegiForm,
            deleteVardenhet: _deleteVardenhet,
            findVardenhetByHsaId: _findVardenhetByHsaId,
            addVardenhet: _addVardenhet
        };
    });
