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
angular.module('ibApp').directive('ibUtredningForfragan', function($log, $uibModal, VardgivareProxy) {
    'use strict';

    return {
        restrict: 'E',
        scope: {
            utredning: '='
        },
        templateUrl: '/app/samordnare/visaUtredning/ibUtredningForfragan/ibUtredningForfragan.directive.html',
        link: function($scope) {

            $scope.skickaForfragan = function(){
                $uibModal.open({
                    templateUrl: '/app/samordnare/visaUtredning/ibUtredningForfragan/skickaForfragan.modal.html',
                    size: 'md',
                    controller: function($scope) {

                        $scope.vm = {
                            loading: true,
                            hasVardenheter: false,
                            vardenheter: []
                        };

                        VardgivareProxy.getVardenheter().then(function(vardenheter) {
                            $scope.vm.vardenheter = vardenheter;
                            angular.forEach(vardenheter, function(vardenheterList) {
                               if (vardenheterList.length > 0) {
                                   $scope.vm.hasVardenheter = true;
                               }
                            });
                        }, function(error) {
                            $log.error('failed to load vardenheter for vardgivare!' + error);
                        }).finally(function() { // jshint ignore:line
                            $scope.vm.loading = false;
                        });

                    }
                });
            };

        }
    };
});
