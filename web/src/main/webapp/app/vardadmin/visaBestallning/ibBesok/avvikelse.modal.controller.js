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
    .controller('AvvikelseModalCtrl',
        function($log, $scope, $uibModalInstance, BesokProxy, DateUtilsService, dialogModel) {
            'use strict';

            $scope.orsakList = [
                {
                    id: 'PATIENT',
                    label: 'Invånaren'
                },
                {
                    id: 'VARDEN',
                    label: 'Vården'
                }
            ];

            $scope.maxDate = moment().format('YYYY-MM-DD');

            $scope.avvikelse = {
                orsakatAv: 'PATIENT',
                beskrivning: undefined,
                datum: new Date(),
                tid: new Date(),
                invanareUteblev: false
            };

            $scope.showSaveErrorMessage = false;

            $scope.send = function () {

                $scope.showSaveErrorMessage = false;
                var avvikelseDto = angular.copy($scope.avvikelse);

                avvikelseDto.tid = DateUtilsService.formatTime(avvikelseDto.tid);

                BesokProxy.createBesokAvvikelse(dialogModel.utredningId, dialogModel.besokId, avvikelseDto).then(function() {
                    $uibModalInstance.close();
                    $scope.showSaveErrorMessage = false;
                }, function() {
                    //show felmeddelande
                    $scope.showSaveErrorMessage = true;
                });
                
            };

        });
