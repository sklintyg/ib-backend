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
        function($log, $scope, $uibModalInstance, BesokProxy, DateUtilsService, dialogModel, redovisaBesokService) {
            'use strict';

            // Setup view model
            $scope.vm = {
                besokList: [],
                showSaveErrorMessage: false,
                deltarItems: [
                    {
                        id: 'DELTAGIT',
                        label: 'Deltog'
                    },
                    {
                        id: 'EJ_DELTAGIT',
                        label: 'Deltog ej'
                    }
                ],
                inProgress: false
            };

            // Transform utredning besokList to local besokList view model
            $scope.vm.besokList = dialogModel.bestallning.besokList.
                filter(function(besok) {
                    return redovisaBesokService.shouldBesokBeRedovisat(besok);
                }).
                map(function(besok) {
                    var genomfort = besok.besokStatus.id === 'GENOMFORT';

                    var disabledDeltarItems = {
                        'DELTAGIT': genomfort,
                        'EJ_DELTAGIT': genomfort
                    };

                    return {
                        besokId: besok.besokId,
                        besokStatus: besok.besokStatus.id,
                        besokDatum: besok.besokDatum,
                        besokStartTid: besok.besokStartTid,
                        besokSlutTid: besok.besokSlutTid,
                        professionLabel: besok.profession.label,
                        tolkStatus: {
                            selected: besok.tolkStatus === null ? null : besok.tolkStatus.id
                        },
                        disabledDeltarItems: disabledDeltarItems,
                        genomfort: genomfort,
                        originalGenomfort: genomfort
                    };
                });

            $scope.validate = function() {
                $scope.vm.besokList.forEach(function(besok) {
                    besok.invalid = false;
                    if(!besok.tolkStatus && besok.genomfort) {
                        besok.invalid = true;
                    }
                });
            };

            $scope.send = function () {

                // Create DTO
                var redovisaBesokDto = {
                    utredningId: dialogModel.bestallning.utredningsId,
                    redovisaBesokList: []
                };

                // Transform besokList vm for backend
                redovisaBesokDto.redovisaBesokList = $scope.vm.besokList.
                    filter(function(item) {
                        return !item.originalGenomfort;
                    })
                    .map(function(item) {
                        return {
                            besokId: item.besokId,
                            tolkStatus: item.tolkStatus.selected,
                            genomfort: item.genomfort
                        };
                    });

                $scope.vm.showValidationErrorMessage = false;

                // Validate that any rows are 'genomfort' at all
                // Validate that if tolk has been booked, tolk and genomfort must be set for it to be a valid row
                if (redovisaBesokDto.redovisaBesokList.filter(function(item) {
                        return redovisaBesokService.isBesokRedovisningValid(item);
                    }).length === 0) {
                    $scope.vm.showValidationErrorMessage = true;
                    return;
                }

                // Send
                $scope.vm.showSaveErrorMessage = false;
                $scope.vm.inProgress = true;
                BesokProxy.redovisaBesok(redovisaBesokDto.utredningId, redovisaBesokDto).then(function() {
                    $scope.vm.inProgress = false;
                    $uibModalInstance.close();
                    $scope.vm.showSaveErrorMessage = false;
                }, function(error) {
                    $scope.vm.inProgress = false;
                    $scope.vm.showSaveErrorMessage = true;
                    if (error.errorEntityId) {
                        $scope.vm.saveErrorBesok = dialogModel.bestallning.besokList
                            .filter(function(besok) { return besok.besokId === error.errorEntityId; });
                        if ($scope.vm.saveErrorBesok.length === 1) {
                            $scope.vm.saveErrorBesok = $scope.vm.saveErrorBesok[0];
                        }
                        else {
                            $scope.vm.saveErrorBesok = null;
                        }
                    }
                });
            };
        });
