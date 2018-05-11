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
    .controller('HanteraEnheterCtrl',
        function($scope, $log, ibEnhetFilterModel, VardgivareProxy) {
            'use strict';

            $scope.filter = ibEnhetFilterModel.build();


            $scope.onEditRow = function (row) {
                $log.debug('Edit! ' + row);
            };
            $scope.onDeleteRow = function (row) {
                $log.debug('Delete! ' + row);
            };

            $scope.getVardenheterWithFilter = function(appendResults) {

                if (!appendResults) {
                    $scope.filter.currentPage = 0;
                }

                VardgivareProxy.getVardenheterWithFilter($scope.filter.convertToPayload()).then(function(data) {
                    if (appendResults) {
                        $scope.vardenheter = $scope.vardenheter.concat(data.vardenheter);
                    }
                    else {
                         $scope.vardenheter = data.vardenheter;
                    }
                    $scope.vardenheterTotal = data.totalCount;
                }, function(error) {
                    $log.error(error);
                });
            };

            $scope.getMore = function() {
                $scope.filter.currentPage++;
                $scope.getVardenheterWithFilter(true);
            };

            $scope.getVardenheterWithFilter();

        }
    );