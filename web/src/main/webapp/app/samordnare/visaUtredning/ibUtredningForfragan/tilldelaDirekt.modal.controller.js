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
    .controller('TilldelaDirektModalCtrl',
        function($scope, $uibModalInstance, $log, $state, utredning, veModel, UtredningarProxy) {
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
                selectedVardenhet: {},
                vardenheterValidationError: false,
                inProgress: false
            };

            $scope.vardenheterChanged = function() {
                $scope.vm.vardenheterValidationError = false;
            };

            $scope.skicka = function() {
                $scope.vm.inProgress = true;

                if ($scope.vm.selectedVardenhet.selected === null) {
                    $scope.vm.vardenheterValidationError = true;
                }
                else {
                    UtredningarProxy.tilldelaDirekt(utredning.utredningsId, {
                        'vardenhet': $scope.vm.selectedVardenhet.selected,
                        'kommentar': $scope.vm.meddelande
                    }).then(function(data) {
                        angular.copy(data, utredning);
                        $uibModalInstance.close();
                    }, function(error) {
                        $log.error('failed to tilldelda direkt!' + error);
                    }).finally(function() { // jshint ignore:line
                        $scope.vm.inProgress = false;
                        $state.reload();
                    });
                }
            };

        }
    );
