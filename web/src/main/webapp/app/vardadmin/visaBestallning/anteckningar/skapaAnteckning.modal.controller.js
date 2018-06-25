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
    .controller('SkapaAnteckningModalCtrl',
        function ($scope, $uibModalInstance, SkapaAnteckningProxy, $stateParams, $state) {
            'use strict';
            
            $scope.note = '';
            
            $scope.vm = {
                busySaving: false
            };
            
            $scope.anteckningIsEmpty = function() {
                return ($scope.note.replace(/\s/g,'') == '');
            };
            
            $scope.save = function () {
                $scope.vm.busySaving = true;
                
                SkapaAnteckningProxy.skapaAnteckning($stateParams.utredningsId, {text: $scope.note})
                    .then(function () {
                        $uibModalInstance.close();
                        $state.reload();
                    }).finally(function () { // jshint ignore:line
                        $scope.vm.busySaving = false;
                    });

            };
        }
    );
