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
    .controller('VisaBestallningCtrl',
        function($log, $scope, $stateParams, $state, BestallningarProxy) {
            'use strict';

            $scope.vm = {
                loading: true
            };

            function getStartTab() {
                if ($stateParams.activeTab) {
                    return parseInt($stateParams.activeTab, 10);
                }
                switch($scope.bestallning.status.id) {
                case 'TILLDELAD_VANTAR_PA_BESTALLNING':
                case 'BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR':
                    return 0;
                case 'AVBRUTEN':
                case 'AVSLUTAD':
                    return 2;
                default:
                    return 1;
                }
            }
            function buildHandlaggarKontaktUppgifter(bestallning) {
                var result = [];
                if (bestallning.handlaggareNamn) {
                    result.push(bestallning.handlaggareNamn);
                }
                if (bestallning.handlaggareTelefonnummer) {
                    result.push(bestallning.handlaggareTelefonnummer);
                }
                if (bestallning.handlaggareEpost) {
                    result.push(bestallning.handlaggareEpost);
                }
                return result.join('<br>');

            }
            BestallningarProxy.getBestallning($stateParams.utredningsId).then(function(bestallning) {
                $scope.bestallning = bestallning;
                $scope.bestallning.handlaggarKontaktUppgifter = buildHandlaggarKontaktUppgifter(bestallning);
                $scope.active = getStartTab();

            }, function(error) {
                $log.error(error);
            }).finally(function() { // jshint ignore:line
                $scope.vm.loading = false;
            });

            $scope.setActive = function(index) {
                // state.go with notify false sets the stateparams but doesn't reload the view
                $state.go($state.current.name, {utredningsId: $stateParams.utredningsId, activeTab: index}, {location: 'replace', notify: false});
            };
        }
    );
