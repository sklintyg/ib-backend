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
angular.module('ibApp').directive('ibUtredningHandelser', function($log, $timeout, ibHandelseFilterModel) {
    'use strict';

    return {
        restrict: 'E',
        scope: {
            utredning: '='
        },
        templateUrl: '/app/samordnare/visaUtredning/ibUtredningHandelser/ibUtredningHandelser.directive.html',
        link: function($scope) {

            $scope.onChange = function() {
                $timeout(function() {
                    $scope.getHandelserFiltered();
                });
            };

            $scope.filter = ibHandelseFilterModel.build();

            $scope.getHandelserFiltered = function(appendResults) {

                if (!appendResults) {
                    $scope.filter.currentPage = 0;
                }

/*                HandelserProxy.getForfragningar($scope.filter.convertToPayload()).then(function(data) {
                    if (appendResults) {
                        $scope.handelser = $scope.handelser.concat(data.handelser);
                    }
                    else {
                        $scope.handelser = data.handelser;
                    }
                    $scope.handelserTotal = data.totalCount;
                }, function(error) {
                    $log.error(error);
                });
                */
            };
            $scope.getHandelserFiltered();
        }
    };
});
