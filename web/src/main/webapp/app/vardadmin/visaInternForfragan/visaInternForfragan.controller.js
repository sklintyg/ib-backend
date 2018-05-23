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
    .controller('VisaInternForfraganCtrl',
        function($log, $state, $scope, $filter, $stateParams, InternForfraganProxy, VardenhetProxy, InternForfraganSvarViewState) {
            'use strict';

            InternForfraganSvarViewState.reset();

            $scope.vm = {
                data: {},
                loading: true
            };

            function convertUtredningToViewModel(utredning) {
                var utredningViewModel = angular.copy(utredning);

                utredningViewModel.behovAvTolk = utredning.behovTolk ? $filter('ibBoolFilter')(utredning.behovTolk) +
                    ', ' + utredning.tolkSprak : $filter('ibBoolFilter')(utredning.behovTolk);

                utredningViewModel.tidigareEnheter = utredning.tidigareEnheter
                    .filter(function(tidigareEnhet) {
                        return tidigareEnhet.vardenhetFelmeddelande === null;
                    })
                    .map(function(tidigareEnhet) {
                        return tidigareEnhet.vardenhetNamn;
                    })
                    .join(', ');

                utredningViewModel.felaktigaEnheter = utredning.tidigareEnheter
                    .filter(function(tidigareEnhet) {
                        return tidigareEnhet.vardenhetFelmeddelande !== null;
                    })
                    .map(function(tidigareEnhet) {
                        return tidigareEnhet.vardenhetHsaId + ' - ' + tidigareEnhet.vardenhetFelmeddelande;
                    })
                    .join(', ');

                return utredningViewModel;
            }

            InternForfraganProxy.getInternForfragning($stateParams.utredningsId).then(function(response) {
                $scope.vm.data.internForfragan = response.internForfragan;
                $scope.vm.data.utredning = convertUtredningToViewModel(response.utredning);
                if (response.internForfraganSvar) {
                    InternForfraganSvarViewState.resetFromExisting(response.internForfraganSvar);
                    $scope.vm.loading = false;
                } else {
                    VardenhetProxy.getVardenhetPreference().then(function(vardenhetPreference) {
                        InternForfraganSvarViewState.resetFromPreference($scope.vm.data.internForfragan.forfraganId,vardenhetPreference);
                    }, function(error) {
                        $log.error('failed to load vardenhet preference!' + error);
                        InternForfraganSvarViewState.reset('common.error.SPI.FEL01');
                    }).finally(function() { // jshint ignore:line
                        $scope.vm.loading = false;
                    });
                }
            }, function(error) {
                $log.error(error);
                $scope.vm.loading = false;
            });

            $scope.onAccept = function() {
                $log.debug('onAccept()');
                var model = angular.copy(InternForfraganSvarViewState.getModel());
                model.svarTyp='ACCEPTERA';

                InternForfraganSvarViewState.getWidgetState().busyAccepting = true;
                InternForfraganProxy.accepteraInternForfragan($stateParams.utredningsId, model).then(function() {
                    //Things in related utredningsstate could have changed - reload state to make sure we show correct state of everything
                    $state.reload();
                }, function(error) {}).finally(function() { // jshint ignore:line
                    InternForfraganSvarViewState.getWidgetState().busyAccepting = false;
                });
            };


            $scope.onReject = function() {
                $log.debug('onReject()');
                var model = angular.copy(InternForfraganSvarViewState.getModel());
                model.svarTyp='AVBOJ';

                InternForfraganSvarViewState.getWidgetState().busyRejecting = true;
                InternForfraganProxy.avbojInternForfragan($stateParams.utredningsId, model).then(function() {
                    //Things in related utredningsstate could have changed - reload state to make sure we show correct state of everything
                    $state.reload();
                }, function(error) {}).finally(function() { // jshint ignore:line
                    InternForfraganSvarViewState.getWidgetState().busyRejecting = false;
                });
            };
        }
    );
