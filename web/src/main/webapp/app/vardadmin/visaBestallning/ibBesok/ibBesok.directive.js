/* sklintyg is free software: you can redistribute it and/or modify
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

angular.module('ibApp').directive('ibBesok', function($log, $uibModal) {
    'use strict';

    return {
        restrict: 'E',
        scope: {
            'bestallning': '='
        },
        templateUrl: '/app/vardadmin/visaBestallning/ibBesok/ibBesok.directive.html',
        link: function($scope) {

            $scope.oppnaLaggTillBesok = function() {
                openModal('laggTillBesok.modal.html', 'LaggTillBesokModalCtrl');
            };

            /* TODO Redovisa tolk modal
            $scope.redovisaTolk = function() {
                openModal('redovisaTolk.modal.html', 'RedovisaTolkModalCtrl');
            };
            */

            function openModal(templateUrl, controller) {
                var modalInstance = $uibModal.open({
                    templateUrl: '/app/vardadmin/visaBestallning/ibBesok/' + templateUrl,
                    size: 'md',
                    controller: controller
                });
                //angular > 1.5 warns if promise rejection is not handled (e.g backdrop-click == rejection)
                modalInstance.result.catch(function () {}); //jshint ignore:line
            }
        }
    };
});