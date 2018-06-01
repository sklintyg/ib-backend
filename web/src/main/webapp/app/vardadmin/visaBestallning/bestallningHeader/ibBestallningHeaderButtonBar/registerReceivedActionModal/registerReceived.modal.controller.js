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
    .controller('RegisterReceivedModalCtrl',
        function() {
            'use strict';
/*
            $scope.vm = {
                kommentar: '',
                inProgress: false
            };

            $scope.avvisa = function() {
                $scope.vm.inProgress = true;

                if ($scope.vm.kommentar.length === 0) {
                    $scope.vm.kommentarValidationError = true;
                }
                else {
                    ExternForfraganProxy.avvisaExternForfragan(utredning.utredningsId, $scope.vm.kommentar)
                        .then(function(data) {
                            angular.copy(data, utredning);
                            $uibModalInstance.close();
                        }, function(error) {
                            $log.error('failed to avvisa ExternForfragan!' + error);
                        }).finally(function() { // jshint ignore:line
                            $scope.vm.inProgress = false;
                        });
                }
            };
*/
        }

    );
