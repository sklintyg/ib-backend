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
/**
 Note: This directive is not rendered unless a valid userModel & current unit is available, so all access to $scope.userModel can skips such checks.
 */
angular.module('ibApp').directive('ibHeaderUnit', [ '$uibModal', 'UserModel',
    function($uibModal, UserModel) {
    'use strict';

    return {
        restrict: 'E',
        scope: {},
        templateUrl: '/components/commonDirectives/ibAppHeader/ibHeaderUnit/ibHeaderUnit.directive.html',
        link: function($scope) {


/*
            $scope.statService = statService;
            $scope.statService.startPolling();
            $scope.stat = {
                fragaSvarValdEnhet: 0,
                fragaSvarAndraEnheter: 0,
                intygValdEnhet: 0,
                intygAndraEnheter: 0,
                vardgivare: []
            };
 */
            /**
             * Event listeners
             */
            /*
            $scope.$on('statService.stat-update', function(event, message) {
                $scope.stat = message;
            });

*/

            $scope.getUser = function () {
                return UserModel.get();
            };


            $scope.onChangeActiveUnitClick = function() {

                    var changeUnitDialogInstance = $uibModal.open({
                        templateUrl: '/components/commonDirectives/ibAppHeader/ibHeaderUnit/ibChangeActiveUnitDialog.html',
                        controller: 'ibChangeActiveUnitDialogCtrl',
                        size: 'md',
                        id: 'ibChangeActiveUnitDialog',
                        keyboard: true,
                        windowClass: 'ib-header-care-unit-dialog-window-class'
                    });
                //angular > 1.5 warns if promise rejection is not handled (e.g backdrop-click == rejection)
                changeUnitDialogInstance.result.catch(function () {}); //jshint ignore:line

            };

        }
    };
} ]);
