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
angular.module('ibApp').directive('ibMainMenu', ['$state', 'UserModel',
    function($state, UserModel) {
    'use strict';

    return {
        restrict: 'E',
        scope: {},
        templateUrl: '/components/commonDirectives/ibMainMenu/ibMainMenu.directive.html',
        link: function($scope) {

            $scope.menuDefs = [];
            //Set default stats
            $scope.stat = {
                fragaSvarValdEnhet: 0,
                fragaSvarAndraEnheter: 0,
                intygValdEnhet: 0,
                intygAndraEnheter: 0,
                vardgivare: []
            };
            /**
             * Event listeners
             */
            $scope.$on('statService.stat-update', function(event, message) {
                $scope.stat = message;
            });

            $scope.isActive = function(stateName) {
                return stateName === $state.current.name;
            };

            function buildMenu() {

                var menu = [];

                if (UserModel.get().currentRole.name === 'FMU_SAMORDNARE') {
                    menu.push({
                        state: 'app.samordnare.listaUtredningar',
                        label: 'Pågående',
                        id: 'menu-samordnare-listaUtredningar'/*,
                        statNumberId: 'stat-unitstat-unhandled-question-count',
                        statTooltip: 'not set',
                        getStat: function() {
                            this.statTooltip = 'Vårdenheten har ' + $scope.stat.fragaSvarValdEnhet +
                                ' ej hanterade frågor och svar.';
                            return $scope.stat.fragaSvarValdEnhet || '';
                        }*/
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
                        id: 'menu-vardadministrator-listaForfragningar'
                    });

                    menu.push({
                        state: 'app.vardadmin.pagaendeUtredningar',
                        label: 'Pågående',
                        id: 'menu-vardadministrator-pagaendeUtredningar'
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
