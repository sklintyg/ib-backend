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
angular.module('ibApp').directive('ibUtredningForfragan', function($log, $uibModal, VardgivareProxy, UtredningarProxy) {
    'use strict';

    return {
        restrict: 'E',
        scope: {
            utredning: '='
        },
        templateUrl: '/app/samordnare/visaUtredning/ibUtredningForfragan/ibUtredningForfragan.directive.html',
        link: function($scope) {

            $scope.skickaForfragan = function(){

                // If an InternForfragan already exists, show as selected and disabled checkboxes
                var internForfraganCreatedVardenheter = {};
                angular.forEach($scope.utredning.internForfraganList, function(internForfragan) {
                    internForfraganCreatedVardenheter[internForfragan.vardenhetHsaId] = true;
                });

                var utredning = $scope.utredning;

                var skickaForfraganModal = $uibModal.open({
                    templateUrl: '/app/samordnare/visaUtredning/ibUtredningForfragan/skickaForfragan.modal.html',
                    size: 'md',
                    controller: function($scope) {

                        $scope.vm = {
                            loading: true,
                            hasVardenheter: false,
                            vardenheter: [],
                            disabledVardenheter: internForfraganCreatedVardenheter,
                            selectedVardenheter: angular.copy(internForfraganCreatedVardenheter),
                            createInternForfraganInProgress: false,
                            vardenheterError: false,
                            vardenheterValidationError: false
                        };

                        VardgivareProxy.getVardenheter().then(function(vardenheter) {
                            $scope.vm.vardenheter = vardenheter;
                            angular.forEach(vardenheter, function(vardenheterList) {
                               if (vardenheterList.length > 0) {
                                   $scope.vm.hasVardenheter = true;
                               }
                            });
                        }, function(error) {
                            $scope.vm.vardenheterError = 'skicka.forfragan.vardenheter.error';
                            $log.error('failed to load vardenheter for vardgivare!' + error);
                        }).finally(function() { // jshint ignore:line
                            $scope.vm.loading = false;
                        });

                        $scope.vardenheterChanged = function() {
                            $scope.vm.vardenheterValidationError = false;
                        };

                        $scope.skicka = function() {
                            $scope.vm.createInternForfraganInProgress = true;

                            // ng-model for checkboxes requires an object with true/false for each value
                            // 1. convert it into an array containing only true values
                            // 2. filter out already created internforfragan (they show as selected in the checkboxlist)
                            var selectedVardenheterArray = [];
                            angular.forEach($scope.vm.selectedVardenheter, function(value, key) {
                                if (value && !internForfraganCreatedVardenheter[key]) {
                                    selectedVardenheterArray.push(key);
                                }
                            });

                            if (selectedVardenheterArray.length === 0) {
                                $scope.vm.vardenheterValidationError = true;
                            }
                            else {
                                UtredningarProxy.createInternForfragan(utredning.utredningsId, {
                                    'vardenheter': selectedVardenheterArray,
                                    'kommentar': $scope.vm.meddelande
                                }).then(function(data) {
                                    utredning.internForfraganList = data.internForfraganList;
                                    utredning.handelseList = data.handelseList;
                                    skickaForfraganModal.close();
                                }, function(error) {
                                    $log.error('failed to create InternForfragan!' + error);
                                }).finally(function() { // jshint ignore:line
                                    $scope.vm.createInternForfraganInProgress = false;
                                });
                            }
                        };

                    }
                });
            };

        }
    };
});
