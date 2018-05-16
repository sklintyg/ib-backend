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
    .controller('HanteraEnheterCtrl',
        function($scope, $rootScope, $log, $uibModal, ibEnhetFilterModel, VardgivareProxy) {
            'use strict';

            var _editModalInstance, _deleteModalInstance, _addModalInstance;

            $scope.filter = ibEnhetFilterModel.build();


            $scope.onAddUnit = function () {
                _addModalInstance = $uibModal.open({
                    templateUrl: '/app/samordnare/hanteraEnheter/addEnhet/addEnhetDialog.template.html',
                    controller: 'addEnhetDialogCtrl',
                    size: 'md',
                    id: 'editEnhetDialog',
                    windowClass: 'add-enhet-dialog-window-class',
                    backdrop: 'static'
                });
                _addModalInstance.result.then(function (newRow) {
                    //TODO: reload or add first instead?
                    $scope.vardenheter.push(newRow);
                }).catch(function () {}); //jshint ignore:line

            };

            $scope.onEditRow = function (row) {
                var srcRow = row;
                _editModalInstance = $uibModal.open({
                    templateUrl: '/app/samordnare/hanteraEnheter/editEnhet/editEnhetDialog.template.html',
                    controller: 'editEnhetDialogCtrl',
                    size: 'md',
                    id: 'editEnhetDialog',
                    windowClass: 'edit-enhet-dialog-window-class',
                    backdrop: 'static',
                    resolve: {
                        row: angular.copy(row)
                    }
                });
                _editModalInstance.result.then(function (updatedRow) {
                    $log.debug('updatedRow:' + updatedRow);
                    srcRow.regiForm = updatedRow.regiForm;
                    srcRow.regiFormLabel = updatedRow.regiFormLabel;
                }).catch(function () {}); //jshint ignore:line

            };

            $scope.onDeleteRow = function (row) {
                var srcRow = row;
                _deleteModalInstance = $uibModal.open({
                    templateUrl: '/app/samordnare/hanteraEnheter/deleteEnhet/deleteEnhetDialog.template.html',
                    controller: 'deleteEnhetDialogCtrl',
                    size: 'md',
                    id: 'deleteEnhetDialog',
                    windowClass: 'delete-enhet-dialog-window-class',
                    backdrop: 'static',
                    resolve: {
                        row: angular.copy(row)
                    }
                });
                _deleteModalInstance.result.then(function (deletedRow) {
                    $log.debug('deleted row :' + deletedRow);
                    //remove it from list locally
                    $scope.vardenheter.pop(srcRow);
                }).catch(function () {}); //jshint ignore:line
            };


            var unregisterFn = $rootScope.$on('$stateChangeStart', function() {
               angular.forEach([_addModalInstance, _editModalInstance, _deleteModalInstance], function (dlgInstance) {
                   if (dlgInstance) {
                       dlgInstance.close();
                   }
               });

            });
            $scope.$on('$destroy', unregisterFn);



            $scope.getVardenheterWithFilter = function(appendResults) {

                if (!appendResults) {
                    $scope.filter.currentPage = 0;
                }

                VardgivareProxy.getVardenheterWithFilter($scope.filter.convertToPayload()).then(function(data) {
                    if (appendResults) {
                      $scope.vardenheter = $scope.vardenheter.concat(data.vardenheter);
                    } else {
                      $scope.vardenheter = data.vardenheter;
                    }
                    $scope.vardenheterTotal = data.totalCount;
                }, function(error) {
                    $log.error(error);
                });
            };

            $scope.getMore = function() {
                $scope.filter.currentPage++;
                $scope.getVardenheterWithFilter(true);
            };

            $scope.getVardenheterWithFilter();

        }
    );