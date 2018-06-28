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

angular.module('ibApp')
    .controller('SkickaForfraganModalCtrl',
        function($scope, $state, $uibModalInstance, $log, utredning, veModel, UtredningarProxy) {
            'use strict';

            // If an InternForfragan already exists, show as selected and disabled checkboxes
            var internForfraganCreatedVardenheter = {};
            angular.forEach(utredning.internForfraganList, function(internForfragan) {
                internForfraganCreatedVardenheter[internForfragan.vardenhetHsaId] = true;
            });

            $scope.vm = {
                hasVardenheter: veModel.hasVardenheter,
                vardenheter: veModel.vardenheter,
                disabledVardenheter: internForfraganCreatedVardenheter,
                selectedVardenheter: angular.copy(internForfraganCreatedVardenheter),
                vardenheterValidationError: false,
                inProgress: false
            };

            $scope.vardenheterChanged = function() {
                $scope.vm.vardenheterValidationError = false;
            };

            function toTrueValueArray(obj) {
                var arr = [];
                for (var key in obj) {
                    if (obj.hasOwnProperty(key) && obj[key]) {
                        arr.push(obj[key]);
                    }
                }

                return arr;
            }

            $scope.skickaEnabled = function() {

                var selectedArray = toTrueValueArray($scope.vm.selectedVardenheter);
                var disabledArray = toTrueValueArray($scope.vm.disabledVardenheter); // these can never be false so same function can be used

                // the disabled are those which are already sent so if more are selected we should enable the button to send the newly selected ones
                return selectedArray.length > disabledArray.length;
            };

            $scope.skicka = function() {
                $scope.vm.inProgress = true;

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
                        $uibModalInstance.close();
                        $state.reload();
                    }, function(error) {
                        $log.error('failed to create InternForfragan!' + error);
                    }).finally(function() { // jshint ignore:line
                        $scope.vm.inProgress = false;
                    });
                }
            };

        }
    );
