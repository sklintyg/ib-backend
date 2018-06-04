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
angular.module('ibApp').directive('ibBestallningButtonBar',
    function ($uibModal, $state) {
        'use strict';

        return {
            restrict: 'E',
            scope: {
                bestallning: '=',
                bestallningVm: '='
            },
            templateUrl: '/app/vardadmin/visaBestallning/bestallningHeader/ibBestallningHeaderButtonBar/ibBestallningButtonBar.directive.html',
            link: function ($scope) {

                $scope.vm = {
                    acceptInProgress: false
                };

                $scope.status = function () {
                    if ($scope.bestallning.status.id === 'BESTALLNING_MOTTAGEN' ||
                        $scope.bestallning.status.id === 'VANTAR_PA_HANDLINGAR' ||
                        $scope.bestallning.status.id === 'UPPDATERA_BESTALLNING') {
                        return false;
                    }
                    return true;
                };

                $scope.correctFasId = function () {
                    if ($scope.bestallning !== undefined && $scope.bestallning.fas.id === 'UTREDNING') {
                        return true;
                    }
                    return false;
                };

                $scope.registerReceivedAction = function () {
                    var modalInstance = $uibModal.open({
                        templateUrl: '/app/vardadmin/visaBestallning/bestallningHeader/ibBestallningHeaderButtonBar/registreraMottagenHandling/' +
                        'registreraMottagenHandling.modal.html',
                        size: 'md',
                        controller: 'RegistreraMottagenHandlingModalCtrl',
                        resolve: {
                            bestallning: $scope.bestallning
                        }
                    });
                    //angular > 1.5 warns if promise rejection is not handled (e.g backdrop-click == rejection)
                    modalInstance.result.catch(function () {}); //jshint ignore:line

                    modalInstance.result.then(function() {
                        $state.reload();
                    }, function() {

                    });
                };
            }
        };
    });
