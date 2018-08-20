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
            var besok = dialogModel.besok || {};
            $scope.besokId = besok.besokId;
            $scope.showReportErrorMessage = false;
            $scope.showUpdateAssessmentErrorMessage = false;
            $scope.professionList = [chooseOption];
            $scope.professionSelected = false;

            BesokProxy.getProffessionsTyper(dialogModel.utredningsId).then(function(result) {
                $scope.professionList = $scope.professionList.concat(result);
            }, function(error) {
                $log.error(error);
            });

            $scope.investigativePersonnel = 'Namn på utredande vårdpersonal';
            
            function getDateTime(time) {
                var dateTime = new Date();
                if(time.indexOf(':') !== -1) {
                    dateTime.setHours(time.split(':')[0]);
                    dateTime.setMinutes(time.split(':')[1]);
                }
                return dateTime;
            }

            $scope.besok = {
                utredandeVardPersonalNamn: besok.namn ? besok.namn : '',
                profession: besok.profession && besok.profession.id ? besok.profession.id : 'LK',
                tolkStatus: besok.tolkStatus ? besok.tolkStatus.id : 'EJ_BOKAT',
                kallelseForm: besok.kallelseForm && besok.kallelseForm.id ? besok.kallelseForm.id : 'BREVKONTAKT',
                kallelseDatum: besok.kallelseDatum ? besok.kallelseDatum : undefined,
                besokDatum: besok.besokDatum ? besok.besokDatum : undefined,
                besokStartTid: besok.besokStartTid ? getDateTime(besok.besokStartTid) : '',
                besokSlutTid: besok.besokSlutTid ? getDateTime(besok.besokSlutTid) : ''
            };

            $scope.besokStartTid = $scope.besok.besokStartTid;
            $scope.besokSlutTid = $scope.besok.besokSlutTid;

            $scope.besokKallelse = $scope.besok.kallelseDatum;
            $scope.besokDatum = $scope.besok.besokDatum;

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

                var besokCall;
                if(besok.besokId) {
                    besokCall = BesokProxy.updateBesok(dialogModel.utredningsId, besok.besokId, $scope.besok);
                } else {
                    besokCall = BesokProxy.createBesok(dialogModel.utredningsId, $scope.besok);
                }
                besokCall.then(function(result) {
                    $uibModalInstance.close(result);
                    $scope.skickar = false;
                }, function(error) {
                    $scope.skickar = false;
                    if (error.failingServiceMethod === 'UPDATE_ASSESSMENT') {
                        $scope.showUpdateAssessmentErrorMessage = true;
                    } else {
                        $scope.showReportErrorMessage = true;
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

            $scope.showKorrektTidsIntervall = function () {
                var start = $scope.besokStartTid;
                var slut = $scope.besokSlutTid;
                return start && slut && slut.setSeconds(0) <= start.setSeconds(0);
            };

            $scope.getKorrektTidsIntervallMessage = function () {
                return messageService.getProperty('lagg-till-besok.info.besoktidintervall');
            };
        });
