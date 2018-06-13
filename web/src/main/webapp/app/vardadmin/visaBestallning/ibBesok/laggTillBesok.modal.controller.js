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
    .controller('LaggTillBesokModalCtrl',
        function($log, $scope, $uibModalInstance, BesokProxy, dialogModel, APP_CONFIG, messageService, moment) {
            'use strict';

            var chooseOption = {
                id: undefined,
                label: 'Välj i listan'
            };

            $scope.showReportErrorMessage = false;
            $scope.showUpdateAssessmentErrorMessage = false;
            $scope.professionList = [chooseOption];

            BesokProxy.getProffessionsTyper(dialogModel.utredningsId).then(function(result) {
                $scope.professionList = $scope.professionList.concat(result);
            }, function(error) {
                $log.error(error);
            });

            $scope.investigativePersonnel = 'Namn på utredande vårdpersonal';

            $scope.besokStartTid = '';
            $scope.besokSlutTid = '';

            $scope.besokKallelse = undefined;
            $scope.besokDatum = undefined;

            $scope.besok = {
                utredandeVardPersonalNamn: '',
                profession: undefined,
                tolkStatus: undefined,
                kallelseForm: 'BREVKONTAKT',
                kallelseDatum: undefined,
                besokDatum: undefined,
                besokStartTid: '',
                besokSlutTid: ''
            };

            var kallelsedatumMedArbetsdagar;


            function formatTime(date) {
                var hours = date.getHours();
                var minutes = date.getMinutes();
                return (hours > 9 ? hours : '0' + hours) + ':' + 
                    (minutes > 9 ? minutes : '0' + minutes);
            }

            $scope.send = function () {
                $scope.showReportErrorMessage = false;
                $scope.showUpdateAssessmentErrorMessage = false;
                $scope.besok.kallelseDatum = new Date($scope.besokKallelse);
                $scope.besok.besokDatum = new Date($scope.besokDatum);
                $scope.besok.besokStartTid = formatTime($scope.besokStartTid);
                $scope.besok.besokSlutTid = formatTime($scope.besokSlutTid);
                $scope.skickar = true;
                BesokProxy.createBesok(dialogModel.utredningsId, $scope.besok).then(function(result) {
                    $uibModalInstance.close(result);
                    $scope.skickar = false;
                }, function(error) {
                    $scope.skickar = false;
                    if (error.failingServiceMethod === 'REPORT_CARE_CONTACT') {
                        $scope.showReportErrorMessage = true;
                    } else {
                        $scope.showUpdateAssessmentErrorMessage = true;
                    }
                });
                
            };

            function hamtaDagar() {
                var dagar;
                if($scope.besok.kallelseForm === 'BREVKONTAKT') {
                    dagar = parseInt(APP_CONFIG.kallelseArbetsdagar, 10) + parseInt(APP_CONFIG.postgangArbetsdagar, 10);
                } else {
                    dagar = parseInt(APP_CONFIG.kallelseArbetsdagar, 10);
                }
                return dagar;
            }

            $scope.$watch('besokKallelse', function(newVal, oldVal) {
                if(newVal && newVal !== oldVal) {
                    BesokProxy.addArbetsdagar(dialogModel.utredningsId, newVal, hamtaDagar()).then(function(result) {
                        kallelsedatumMedArbetsdagar = result;
                    }, function(error) {
                        $log.error(error);
                    });
                }
            });

            $scope.$watch('besok.kallelseForm', function(newVal, oldVal) {
                if(newVal && newVal !== oldVal && $scope.besokKallelse) {
                    BesokProxy.addArbetsdagar(dialogModel.utredningsId, $scope.besokKallelse, hamtaDagar()).then(function(result) {
                        kallelsedatumMedArbetsdagar = result;
                    }, function(error) {
                        $log.error(error);
                    });
                }
            });

            $scope.getMessageForKallelsedatum = function() {
                return messageService.getProperty('lagg-till-besok.info.kallelsedatum',
                    [hamtaDagar()]);
            };

            $scope.showMessageForKallelse = function () {
                if ($scope.besokDatum && kallelsedatumMedArbetsdagar &&
                    moment($scope.besokDatum) <= moment(kallelsedatumMedArbetsdagar)) {
                    return true;
                }
            };
        });
