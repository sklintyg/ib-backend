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
angular.module('ibApp').directive('ibUtredningButtonBar',
    function($uibModal, ExternForfraganProxy) {
    'use strict';

    return {
        restrict: 'E',
        scope: {
            utredning: '=',
            utredningVm: '='
        },
        templateUrl: '/app/samordnare/visaUtredning/utredningHeader/ibUtredningHeaderButtonBar/ibUtredningButtonBar.directive.html',
        link: function($scope) {

            $scope.vm = {
                acceptInProgress: false
            };

            function findDirektTilldeladInternForfragan() {
                return $scope.utredning.internForfraganList.filter(function(internForfragan) {
                    if (internForfragan.status.id === 'DIREKTTILLDELAD') {
                        return true;
                    }
                });
            }

            $scope.accepteraBtnDisabled = function() {
                var hasDirektTilldeladInternForfragan = findDirektTilldeladInternForfragan().length > 0;
                return $scope.utredning.status.id === 'TILLDELAD_VANTAR_PA_BESTALLNING' ||
                    (!hasDirektTilldeladInternForfragan && !$scope.utredningVm.selectedInternforfragan);
            };

            $scope.avvisaBtnDisabled = function() {
                return $scope.utredning.status.id === 'TILLDELAD_VANTAR_PA_BESTALLNING';
            };

            $scope.acceptera = function() {
                $scope.vm.acceptInProgress = true;
                var vardenhetHsaId;
                if ($scope.utredningVm.selectedInternforfragan) {
                    vardenhetHsaId = $scope.utredningVm.selectedInternforfragan.vardenhetHsaId;
                }
                else {
                    vardenhetHsaId = findDirektTilldeladInternForfragan()[0].vardenhetHsaId;
                }
                ExternForfraganProxy.acceptExternForfragan($scope.utredning.utredningsId, vardenhetHsaId)
                    .then(function(data) {
                        angular.copy(data, $scope.utredning);
                    }).finally(function() { // jshint ignore:line
                        $scope.vm.acceptInProgress = false;
                    });
            };
            $scope.avvisa = function() {
                var modalInstance = $uibModal.open({
                    templateUrl: '/app/samordnare/visaUtredning/utredningHeader/ibUtredningHeaderButtonBar/avvisaForfraganModal/' +
                                 'avvisaForfragan.modal.html',
                    size: 'md',
                    controller: 'AvvisaForfraganModalCtrl',
                    resolve: {
                        utredning: $scope.utredning
                    }
                });
                //angular > 1.5 warns if promise rejection is not handled (e.g backdrop-click == rejection)
                modalInstance.result.catch(function () {}); //jshint ignore:line
            };
        }
    };
});
