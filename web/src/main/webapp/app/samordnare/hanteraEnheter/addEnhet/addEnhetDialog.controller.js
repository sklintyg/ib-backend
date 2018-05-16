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
angular.module('ibApp').controller('addEnhetDialogCtrl',
        [ '$scope', '$uibModalInstance', 'VardgivareProxy', function($scope, $uibModalInstance, VardgivareProxy) {
            'use strict';

            $scope.vm = {
                result: undefined,
                busy: false,
                searchTerm: 'TSTNMT2321000156-105N',
                selectedRegiForm: undefined,
                regiFormOptions: [{
                    id: undefined,
                    label: 'Välj i listan'
                },{
                    id: 'EGET_LANDSTING',
                    label: 'Eget landsting'
                },{
                    id: 'ANNAT_LANDSTING',
                    label: 'Annat landsting'
                },{
                    id: 'PRIVAT',
                    label: 'Privat'
                } ]
            };

            $scope.onSearch = function() {
                $scope.vm.busy = true;
                VardgivareProxy.findVardenhetByHsaId($scope.vm.searchTerm).then(function(result) {
                    //$uibModalInstance.close(result);
                    $scope.vm.result = result;
                }, function (error) {
                    $scope.vm.result.error = error;

                }).finally(function() { // jshint ignore:line
                    $scope.vm.busy = false;
                });

            };

        } ]);
