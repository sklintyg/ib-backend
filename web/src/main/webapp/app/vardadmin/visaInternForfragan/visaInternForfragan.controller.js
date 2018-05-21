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
        function($log, $scope, $filter, $stateParams, InternForfraganProxy) {
            'use strict';

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
            }, function(error) {
                $log.error(error);
            }).finally(function() { // jshint ignore:line
                $scope.vm.loading = false;
            });
        }
    );
