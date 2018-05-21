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

angular.module('ibApp')
    .controller('AppPageCtrl',
        function($scope, $rootScope, $uibModal, $state, $log, messageService, UserProxy, UserModel) {
            'use strict';

            $scope.showErrorDialog = function(msgConfig) {
                $uibModal.open({
                    templateUrl: '/app/error/restErrorDialog.html',
                    controller: 'restErrorDialogCtrl',
                    size: 'md',
                    resolve: {
                        msgConfig: function() {
                            return msgConfig;
                        }
                    }
                });
            };

            // Eventhandler that takes care of showing rest exceptions
            var unregisterFn = $rootScope.$on('ib.rest.exception', function(event, msgConfig, response) {
                var texts = {
                    title: messageService.getProperty(msgConfig.errorTitleKey),
                    body: messageService.getProperty(msgConfig.errorTextKey)
                };
                if (response.externalSystemId === 'HSA' && msgConfig.errorHsaTextKey) {
                    texts.body = messageService.getProperty(msgConfig.errorHsaTextKey);
                }
                if (response.externalSystemId === 'MYNDIGHET' && msgConfig.errorMyndighetTextKey) {
                    texts.body = messageService.getProperty(msgConfig.errorMyndighetTextKey);
                }
                $scope.showErrorDialog(texts);
            });

            // Eventhandler that performs the actual switching of current unit/role regardless of where it was requested.
            // Selection of destination view is delegated to routing rules in app.run.js
            var unregisterFn2 = $rootScope.$on('new-active-unit-selected', function(event, newUnit) {
                $log.debug('new-active-unit-selected: switching to unit "' + newUnit.id + '"');
                UserProxy.changeSelectedUnit(newUnit.id).then(function(updatedUserModel) {
                    UserModel.set(updatedUserModel);
                    $state.go('app.login', {}, { reload : true });
                }, function() {
                    //Handle errors
                });
            });
             //rootscope on event listeners aren't unregistered automatically when 'this' directives
             //scope is destroyed, so let's take care of that.
             $scope.$on('$destroy', unregisterFn);
             $scope.$on('$destroy', unregisterFn2);

        });