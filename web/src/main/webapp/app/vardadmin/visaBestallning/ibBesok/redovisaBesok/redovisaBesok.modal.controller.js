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
    .controller('RedovisaBesokModalCtrl',
        function($log, $scope, $uibModalInstance, BesokProxy, DateUtilsService, dialogModel, ObjectHelper) {
            'use strict';

            // Setup view model
            $scope.vm = {
                besokList: [],
                showSaveErrorMessage: false,
                deltarItems: [
                    {
                        id: 'DELTOG',
                        label: 'Deltog'
                    },
                    {
                        id: null,
                        label: 'Deltog ej'
                    }
                ],
                inProgress: false
            }

            // Transform utredning besokList to local besokList view model
            $scope.vm.besokList = dialogModel.bestallning.besokList.
                filter(function(besok) {
                    var besokStartTime = moment(besok.besokDatum + ' ' + besok.besokStartTid);
                    var isStartTimeExpired = moment().isAfter(besokStartTime);
                    return (isStartTimeExpired && (besok.besokStatus.id == 'BOKAT' ||
                            besok.besokStatus.id == 'OMBOKAT' ||
                            besok.besokStatus.id == 'GENOMFORT'));
                }).
                map(function(besok) {
                    var genomfort = besok.besokStatus.id == 'GENOMFORT';

                    var disabledDeltarItems = {
                        'DELTOG' : genomfort,
                        null : genomfort
                    };

                    return {
                        besokId: besok.besokId,
                        besokStatus: besok.besokStatus.id,
                        besokDatum: besok.besokDatum,
                        besokStartTid: besok.besokStartTid,
                        besokSlutTid: besok.besokSlutTid,
                        professionLabel: besok.profession.label,
                        tolkDeltog: besok.tolkStatus,
                        disabledDeltarItems: disabledDeltarItems,
                        genomfort: genomfort
                    }
                });

            $scope.validate = function() {
                $scope.vm.besokList.forEach(function(besok) {
                    besok.invalid = false;
                    if(!besok.tolkDeltog && besok.genomfort)
                        besok.invalid = true;
                });
            };

            $scope.send = function () {

                // Create DTO
                var redovisaBesokDto = {
                    utredningId: dialogModel.bestallning.utredningsId,
                    redovisaBesokList: []
                }

                // Transform besokList vm for backend
                redovisaBesokDto.redovisaBesokList = $scope.vm.besokList.map(function(item) {
                    return {
                        besokId: item.besokId,
                        tolkDeltog: item.tolkDeltog.id,
                        genomfort: item.genomfort
                    }
                });

                // Send
                $scope.vm.showSaveErrorMessage = false;
                $scope.vm.inProgress = true;
                BesokProxy.redovisaBesok(redovisaBesokDto).then(function() {
                    $scope.vm.inProgress = false;
                    $uibModalInstance.close();
                    $scope.vm.showSaveErrorMessage = false;
                }, function() {
                    $scope.vm.inProgress = false;
                    $scope.vm.showSaveErrorMessage = true;
                });
            };
        });
