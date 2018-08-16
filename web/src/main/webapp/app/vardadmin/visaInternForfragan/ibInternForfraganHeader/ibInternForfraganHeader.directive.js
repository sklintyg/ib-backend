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
angular.module('ibApp').directive('ibInternForfraganHeader', function($window, $state, $uibModal,
    InternForfraganSvarViewState, messageService) {
    'use strict';

    return {
        restrict: 'E',
        scope: {
            internForfragan: '<',
            onReject: '&'
        },
        templateUrl: '/app/vardadmin/visaInternForfragan/ibInternForfraganHeader/ibInternForfraganHeader.directive.html',
        link: function($scope) {
            $scope.back = function() {
                $state.go('^');
            };

            $scope.vm  = {
                model: InternForfraganSvarViewState.getModel(),
                widgetState: InternForfraganSvarViewState.getWidgetState()
            };


            $scope.rejectIsProhibited = function() {
                return $scope.internForfragan.rejectIsProhibited;
            };

            $scope.getRejectToolTipText = function() {
                if ($scope.rejectIsProhibited()) {
                    return messageService.getProperty('internforfragan.besvara.rejectbtn.rejectProhibited.tooltip');
                }
                return '';
            };

            $scope.openRejectDialogEnabled = function() {
                return !$scope.vm.model.svarTyp && !$scope.rejectIsProhibited();
            };

            $scope.onRejectClick = function() {

                var dlgInstance = $uibModal.open({
                    templateUrl: '/app/vardadmin/visaInternForfragan/ibAvvisaInternForfraganDialog/ibAvvisaInternForfraganDialog.html',
                    controller: 'ibAvvisaInternForfraganDlgController',
                    size: 'md',
                    id: 'ibVvisaInternForfraganDlg',
                    keyboard: true
                });
                //angular > 1.5 warns if promise rejection is not handled (e.g backdrop-click == rejection)
                dlgInstance.result.catch(function () {}); //jshint ignore:line

            };



            $scope.openAcceptDialogEnabled = function() {
                return !$scope.vm.model.svarTyp;
            };

            $scope.onAcceptClick = function() {

                var dlgInstance = $uibModal.open({
                    templateUrl: '/app/vardadmin/visaInternForfragan/ibAccepteraInternForfraganDialog/ibAccepteraInternForfraganDialog.html',
                    controller: 'ibAccepteraInternForfraganDlgController',
                    size: 'md',
                    id: 'ibAccepteraInternForfraganDlg',
                    keyboard: true,
                    windowClass: 'ib-acceptera-internforfragan-dialog-window-class'
                });

                // angular > 1.5 warns if promise rejection is not handled (e.g backdrop-click == rejection)
                dlgInstance.result.catch(function () {}); //jshint ignore:line

                // wait for animation before reloading state to prevent artifacts if state is reloaded before modal is invisible
                dlgInstance.closed.then(function() {
                    //Things in related utredningsstate could have changed - reload state to make sure we show correct state of everything
                    $state.reload();
                });

            };
        }
    };
});
