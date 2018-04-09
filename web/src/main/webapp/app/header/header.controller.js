/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

angular.module('ibApp').controller('HeaderController',
    function($scope, $window, $state, $log, UserProxy, UserModel, $uibModal, sessionCheckService) {
        'use strict';

        //Expose 'now' as a model property for the template to render as todays date
        $scope.today = new Date();
        $scope.user = UserModel.get();

        sessionCheckService.startPolling();

        /**
         * Private functions
         */

        /**
         * Exposed scope interaction functions
         */

        $scope.logoutLocation = UserModel.getLogoutLocation();

        $scope.showRoleDescription = function(role) {
            return role.name === 'LAKARE';
        };
        $scope.getUnitContext = function() {
            var user = UserModel.get();
            if (user.valdVardenhet) {
                var vName = user.valdVardgivare.namn;
                var eName = user.valdVardenhet.namn;
                var mName = '';
                //Is valdvardenhet actually a mottagning?
                if (user.valdVardenhet.parentHsaId) {
                    eName =  UserModel.getUnitNameById(user.valdVardenhet.parentHsaId);
                    mName = user.valdVardenhet.namn;
                }
                return vName + ' - ' + eName + ((mName.length>0)?' - ' + mName : '');
            }
            return '';
        };



        $scope.openChangeCareUnitDialog = function() {
            var modalInstance = $uibModal.open({
                animation: true,
                templateUrl: '/app/header/careunit/select-care-unit-dialog.html',
                controller: 'SelectCareUnitCtrl',
                size: 'md',
                windowClass: 'select-care-unit-modal'
            });

            modalInstance.result.then(function(enhet) {
                $log.debug('SelectCareUnit Modal closed with a selection :' + enhet.id);
                UserProxy.changeSelectedUnit(enhet.id).then(function(updatedUserModel) {
                    UserModel.set(updatedUserModel);

                    $scope.$emit('SelectedUnitChanged', {enhet: enhet.id});
                }, function() {
                    //Handle errors
                });
            }, function() {
                $log.debug('SelectCareUnit Modal cancelled');
            });

        };

    }
);
