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
angular.module('ibApp').directive('ibUtredningForfragan', function($log, $uibModal, ibVardgivareService) {
    'use strict';

    return {
        restrict: 'E',
        scope: {
            utredning: '=',
            utredningVm: '='
        },
        templateUrl: '/app/samordnare/visaUtredning/ibUtredningForfragan/ibUtredningForfragan.directive.html',
        link: function($scope) {

            var veModel = $scope.veModel = ibVardgivareService.model;
            ibVardgivareService.getVardenheter();

            $scope.showForfraganButtons = function() {
                return !veModel.error &&
                    $scope.utredning.fas.id === 'FORFRAGAN' &&
                    $scope.utredning.status.id !== 'TILLDELAD_VANTAR_PA_BESTALLNING';
            };

            $scope.disableForfraganButtons = function() {
                // Inaktiverad om en vårdenhet har blivit direkttilldelad utredningen.
                return $scope.utredning.internForfraganList.filter(function(internForfragan) {
                    if (internForfragan.status.id === 'DIREKTTILLDELAD') {
                        return true;
                    }
                }).length > 0;
            };

            $scope.showTilldelaDirekt = function() {
                // Knappen är dold om landstinget inte har några vårdenheter i egen regi
                return veModel.hasEgnaVardenheter;
            };

            $scope.skickaForfragan = function() {
                openModal('skickaForfragan.modal.html', 'SkickaForfraganModalCtrl');
            };

            $scope.tilldelaDirekt = function() {
                openModal('tilldelaDirekt.modal.html', 'TilldelaDirektModalCtrl');
            };

            function openModal(templateUrl, controller) {
                var modalInstance = $uibModal.open({
                    templateUrl: '/app/samordnare/visaUtredning/ibUtredningForfragan/' + templateUrl,
                    size: 'md',
                    controller: controller,
                    resolve: {
                        utredning: $scope.utredning,
                        veModel: veModel
                    }
                });
                //angular > 1.5 warns if promise rejection is not handled (e.g backdrop-click == rejection)
                modalInstance.result.catch(function () {}); //jshint ignore:line
            }
        }
    };
});
