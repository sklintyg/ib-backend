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
angular.module('ibApp').factory('StatPollService',
    ['$http', '$log', '$rootScope', '$interval', 'UserModel', 'ObjectHelper', function($http, $log, $rootScope, $interval, UserModel, ObjectHelper) {
        'use strict';

        var intervalPromise;
        var msPollingInterval = 10 * 1000;


        //Default stat result
        var latestData = {};


        /*
         * Start regular polling of stats from server
         */
        function _startPolling() {
            _stopPolling();
            _getStats();
            $log.debug('statPollService -> Started polling' );
        }

        /*
         * Stop regular polling of stats from server
         */
        function _stopPolling(reason) {
            if (intervalPromise) {
                $interval.cancel(intervalPromise);
                $log.debug('statService -> Stop polling ' + (ObjectHelper.isDefined(reason) ? '(reason: ' + reason + ')': ''));
            }
        }
        /*
         * Executes a forced poll right now
         */
        function _refreshNow(reason) {
            $log.debug('statService -> refreshNow requested ' + (ObjectHelper.isDefined(reason) ? '(reason: ' + reason + ')': ''));
            _startPolling();
        }

        /*
         * Executes a forced poll right now, eg when user changed active unit/role
         */
        function _getLatestData() {
            return latestData;
        }

        /*
         * Actually fetch stats from server
         */
        function _getStats() {

            var apiPath = '/api/stats/' + (UserModel.get().currentlyLoggedInAt.type === 'VE' ? 'vardadmin' : 'samordnare');


            $log.debug('_getStats requesting info from ' + apiPath + ' =>');
            $http.get(apiPath).then(function(response) {
                $log.debug('<= _getStats response');
                latestData = response.data;
                $rootScope.$broadcast('statService.stat-update', latestData);

            }, function(errorResponse) {
                $log.error('_getStats error ' + errorResponse);
            }).finally(function() { // jshint ignore:line
                _stopPolling();
                intervalPromise = $interval(_getStats, msPollingInterval);
                $log.debug('statService -> Scheduled next polling');

            });
        }



        // Return public API for the service
        return {
            startPolling: _startPolling,
            stopPolling: _stopPolling,
            refreshNow: _refreshNow,
            getLatestData: _getLatestData
        };
    }]);
