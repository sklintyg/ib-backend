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
angular.module('ibApp').directive('ibMainMenu', ['$state', 'UserModel', 'StatPollService',
    function($state, UserModel, StatPollService) {
    'use strict';

    return {
        restrict: 'E',
        scope: {},
        templateUrl: '/components/commonDirectives/ibMainMenu/ibMainMenu.directive.html',
        link: function($scope) {

            $scope.menuDefs = [];

            //Set default stats
            $scope.stat = StatPollService.getLatestData();
            /**
             * Event listeners
             */
            $scope.$on('statService.stat-update', function(event, message) {
                $scope.stat = message;
            });

            $scope.isActive = function(stateName) {
                return $state.current.name.indexOf(stateName) !== -1;
            };

            function buildMenu() {

                var menu = [];

                if (UserModel.get().currentRole.name === 'FMU_SAMORDNARE') {
                    menu.push({
                        state: 'app.samordnare.listaUtredningar',
                        label: 'Pågående',
                        id: 'menu-samordnare-listaUtredningar',
                        helptext: 'Antal utredningar i en status som kräver åtgärd',
                        getStat: function() {
                               return $scope.stat.requireSamordnarActionCount || '';
                        }
                    });

                    menu.push({
                        state: 'app.samordnare.avslutadeArenden',
                        label: 'Avslutade',
                        id: 'menu-samordnare-avslutadeArenden'
                    });

                    menu.push({
                        state: 'app.samordnare.hanteraEnheter',
                        label: 'Vårdenheter',
                        id: 'menu-samordnare-hanteraEnheter'
                    });
                }
                else if (UserModel.get().currentRole.name === 'FMU_VARDADMIN') {

                    menu.push({
                        state: 'app.vardadmin.listaForfragningar',
                        label: 'Förfrågningar',
                        id: 'menu-vardadministrator-listaForfragningar',
                        helptext: 'Antalet förfrågningar från landstinget som kräver åtgärd av vårdenheten',
                        getStat: function() {
                            return $scope.stat.forfraganRequiringActionCount || '';
                        }
                    });

                    menu.push({
                        state: 'app.vardadmin.pagaendeUtredningar',
                        label: 'Pågående',
                        id: 'menu-vardadministrator-pagaendeUtredningar',
                        helptext: 'Antalet utredningar som kräver åtgärd av vårdenheten',
                        getStat: function() {
                            return $scope.stat.bestallningarRequiringActionCount || '';
                        }
                    });

                    menu.push({
                        state: 'app.vardadmin.avslutadeUtredningar',
                        label: 'Avslutade',
                        id: 'menu-vardadministrator-avslutadeUtredningar'
                    });
                }

                return menu;
            }

            $scope.menuDefs = buildMenu();

        }
    };
}]);
