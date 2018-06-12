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
 * ibDialog - Generic dialog service
 */
angular.module('ibApp').factory('ibDialog',
    function($log, $uibModal, $state, $q, ObjectHelper) {
        'use strict';

        var commonTemplateUrl = 'components/commonDirectives/ibDialog/dialogMessage.template.html';

        function StandardDialogCtrl($scope, $uibModalInstance, dialogModel) {
            $scope.dialogModel = dialogModel;

            $scope.ok = function(result) {
                $uibModalInstance.close(result);
            };
        }

        /* MESSAGE (OK) */
        function _message(domId, titleText, bodyText) {
            return _modal(commonTemplateUrl, StandardDialogCtrl, {
                domId: domId,
                titleText: titleText,
                bodyText: bodyText,
                confirmText: 'Stäng',
                allowCancel: false
            });
        }

        /* CONFIRM (OK, CANCEL)
        dialogModel properties:
        domId - unique id in dom used for testing
        titleText - text on title
        bodyText - main text
        confirmText - text on confirm button
        */
        function _confirm(dialogModel) {
            return _modal(commonTemplateUrl, StandardDialogCtrl, dialogModel);
        }

        function _modal(templateUrl, controller, resolveObject) {

            resolveObject.allowCancel = ObjectHelper.isDefined(resolveObject.allowCancel) ? resolveObject.allowCancel : true;

            var promise = $q.defer();
            var modalInstance = $uibModal.open({
                templateUrl: templateUrl,
                size: 'md',
                controller: controller,
                resolve: {
                    dialogModel: function() {
                        return angular.copy(resolveObject);
                    }
                }
            });

            //angular > 1.5 warns if promise rejection is not handled (e.g backdrop-click == rejection)
            modalInstance.result.catch(function () {}); //jshint ignore:line

            modalInstance.result.then(function(result) {
                $state.reload();
                promise.resolve(result);
            }, function(error) {
                promise.reject(error);
            });

            return promise.promise;
        }

        // Return public API for the service
        return {
            message: _message,
            confirm: _confirm,
            modal: _modal
        };
    });
