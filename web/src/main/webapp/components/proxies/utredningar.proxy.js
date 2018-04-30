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
    function($http, $log, $q,
        networkConfig) {
        'use strict';

        function _getUtredningar() {
            var promise = $q.defer();

            var restPath = '/api/utredningar';

            var config =  {
                timeout: networkConfig.defaultTimeout
            };

            $http.get(restPath, config).then(function(response) {
                $log.debug(restPath + ' - success');

                if (typeof response !== 'undefined') {
                    promise.resolve(response.data);
                } else {
                    $log.debug('JSON response syntax error. Rejected.');
                    promise.reject(null);
                }
            }, function(response) {
                $log.error('error ' + response.status);
                // Let calling code handle the error of no data response
                promise.reject(response.data);
            });

            return promise.promise;
        }

        function _getUtredningarWithFilter(query) {
            var promise = $q.defer();

            var restPath = '/api/utredningar';

            var config =  {
                timeout: networkConfig.defaultTimeout
            };

            $http.post(restPath, query, config).then(function(response) {
                $log.debug(restPath + ' - success');

                if (typeof response !== 'undefined') {
                    promise.resolve(response.data);
                } else {
                    $log.debug('JSON response syntax error. Rejected.');
                    promise.reject(null);
                }
            }, function(response) {
                $log.error('error ' + response.status);
                // Let calling code handle the error of no data response
                promise.reject(response.data);
            });

            return promise.promise;
        }

        // Return public API for the service
        return {
            getUtredningar: _getUtredningar,
            getUtredningarWithFilter: _getUtredningarWithFilter
        };
    });
