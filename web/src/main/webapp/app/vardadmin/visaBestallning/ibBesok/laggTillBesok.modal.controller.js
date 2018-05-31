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
    .controller('LaggTillBesokModalCtrl',
        function($log, $scope, BesokProxy, utredningsId) {
            'use strict';

            var chooseOption = {
                id: undefined,
                label: 'Välj i listan'
            };

            $scope.professionList = [chooseOption];

            BesokProxy.getProffessionsTyper().then(function(result) {
                $scope.professionList = $scope.professionList.concat(result);
            }, function(error) {
                $log.error(error);
            });

            $scope.investigativePersonnel = 'Namn på utredande vårdpersonal';

            $scope.interpreter = {
                id: 'none',
                state: 'Bokat'
            };

            $scope.besok = {
                utredningId: utredningsId,
                utredandeVardPersonalNamn: '',
                profession: undefined,
                tolkStatus: undefined,
                kallelseForm: '',
                kallelseDatum: '',
                besokDatum: new Date(),
                besokStartTid: '',
                besokSlutTid: ''
            };

            $scope.send = function () {
                //console.log($scope.besok);
                BesokProxy.createBesok($scope.besok);
            };


        });
