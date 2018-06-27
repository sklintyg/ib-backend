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
angular.module('ibApp').directive('ibBestallningButtonBar',
    function ($uibModal) {
        'use strict';

        return {
            restrict: 'E',
            scope: {
                bestallning: '=',
                bestallningVm: '='
            },
            templateUrl: '/app/vardadmin/visaBestallning/bestallningHeader/ibBestallningHeaderButtonBar/ibBestallningButtonBar.directive.html',
            link: function ($scope) {

                var openModal = function openModal(action, msgsKey) {
                    var modalInstance = $uibModal.open({
                        templateUrl: '/app/vardadmin/visaBestallning/bestallningHeader/ibBestallningHeaderButtonBar/registreraStatus/' +
                            'registreraStatus.modal.html',
                        size: 'md',
                        controller: 'RegistreraStatusModalCtrl',
                        resolve: {
                            modalOptions: {
                                action: action,
                                title: msgsKey + '.label.title',
                                info: msgsKey + '.label.info'
                            }
                        }
                    });
                    //angular > 1.5 warns if promise rejection is not handled (e.g backdrop-click == rejection)
                    modalInstance.result.catch(function () {}); //jshint ignore:line
                };

                $scope.vm = {
                    acceptInProgress: false
                };

                $scope.registerReceivedHandlingDisabled = function () {
                    if ($scope.bestallning.status.id === 'BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR' ||
                        $scope.bestallning.status.id === 'UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR') {
                        return false;
                    }
                    return true;
                };

                $scope.registerSentUtlatandeDisabled = function () {
                    return $scope.bestallning.status.id === 'UTLATANDE_SKICKAT' ||
                        $scope.bestallning.status.id === 'UTLATANDE_MOTTAGET';
                };

                $scope.registerReceivedKompletteringDisabled = function () {
                    return $scope.bestallning.status.id !== 'KOMPLETTERINGSBEGARAN_MOTTAGEN_VANTAR_PA_FRAGESTALLNING';
                };
                
                $scope.registerSentKompletteringDisabled = function () {
                    return $scope.bestallning.status.id !== 'KOMPLETTERINGSBEGARAN_MOTTAGEN_VANTAR_PA_FRAGESTALLNING' &&
                        $scope.bestallning.status.id !== 'KOMPLETTERANDE_FRAGESTALLNING_MOTTAGEN';
                };
                
                $scope.hasFasId = function (id) {
                    if ($scope.bestallning !== undefined && $scope.bestallning.fas.id === id) {
                        return true;
                    }
                    return false;
                };

                $scope.registerReceivedHandling = function () {
                    openModal('registerReceivedHandling', 'registrera-mottagen-handling');
                };

                $scope.registerReceivedKomplettering = function () {
                    openModal('registerReceivedKomplettering', 'registrera-mottagen-komplettering');
                };
                
                $scope.registerSentKomplettering = function () {
                    openModal('registerSentKomplettering', 'registrera-skickad-komplettering');
                };
                
                $scope.registerSentUtlatande = function () {
                    openModal('registerSentUtlatande', 'registrera-skickat-utlatande');
                };
            }
        };
    });
