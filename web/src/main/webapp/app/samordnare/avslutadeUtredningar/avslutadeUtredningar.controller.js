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

angular.module('ibApp')
    .controller('AvslutadeUtredningarCtrl',
        function($log, $scope, $state, ibAvslutadeUtredningarFilterModel, UtredningarProxy) {
            'use strict';

            $scope.busy = false;
            $scope.error = undefined;
            $scope.filter = ibAvslutadeUtredningarFilterModel.build();
            $scope.utredningar = [];
            $scope.utredningarTotal = 0;

            $scope.visaUtredning = function(utredningsId){
                $state.go('.visaUtredning', {utredningsId: utredningsId});
            };

            $scope.getAvslutadeUtredningarFiltered = function(appendResults) {

                $scope.busy = true;

                if (!appendResults) {
                    $scope.filter.currentPage = 0;
                }

                UtredningarProxy.getAvslutadeUtredningarWithFilter($scope.filter.convertToPayload()).then(function(data) {
                    if (appendResults) {
                        $scope.utredningar = $scope.utredningar.concat(data.utredningar);
                    }
                    else {
                        $scope.utredningar = data.utredningar;
                    }
                    $scope.utredningarTotal = data.totalCount;
                }, function(error) {
                    $scope.error = error;
                    $log.error(error);
                }).finally(function() { // jshint ignore:line
                    $scope.busy = false;
                });
            };

            $scope.getMore = function() {
                $scope.filter.currentPage++;
                $scope.getAvslutadeUtredningarFiltered(true);
            };

            $scope.getAvslutadeUtredningarFiltered();
        }
    );
