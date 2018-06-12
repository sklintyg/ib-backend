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

angular.module('ibApp').directive('ibBesok', function($log, ibDialog, BesokProxy, moment) {
    'use strict';

    return {
        restrict: 'E',
        scope: {
            'bestallning': '='
        },
        templateUrl: '/app/vardadmin/visaBestallning/ibBesok/ibBesok.directive.html',
        link: function($scope) {

            $scope.showErsatts = function() {
                return $scope.bestallning.status.id === 'AVSLUTAD' || $scope.bestallning.status.id === 'AVBRUTEN';
            };

            $scope.hanteraBesokDisabled = function() {
                return !$scope.bestallning || $scope.bestallning.fas.id !== 'UTREDNING' ||
                    $scope.bestallning.status.id === 'UTLATANDE_SKICKAT' || $scope.bestallning.status.id === 'UTLATANDE_MOTTAGET';
            };

            $scope.isAvvikelseMottagen = function(besokStatus) {
                return besokStatus.id === 'AVVIKELSE_MOTTAGEN';
            };

            $scope.oppnaLaggTillBesok = function() {
                openModal('laggTillBesok.modal.html', 'LaggTillBesokModalCtrl',
                        {utredningsId: $scope.bestallning.utredningsId, besok: undefined}).then(function(result) {
                    if (result.nyttSistaDatum) {
                        ibDialog.message('utredningsTypAndradModal', 'Utredningstyp ändrad',
                            'Utredningstypen är ändrad till AFU utvidgad. Nytt slutdatum är ' +
                            moment(result.nyttSistaDatum).format('YYYY-MM-DD'));
                    }
                }, function(error) {
                    if (error && error !== 'backdrop click') {
                        $log.error(error);
                    }
                });
            };

            $scope.openAndraModal = function(besok) {
                openModal('laggTillBesok.modal.html', 'LaggTillBesokModalCtrl',
                    {utredningsId: $scope.bestallning.utredningsId, besok: besok}).then(function(result) {
                    if (result.nyttSistaDatum) {
                        ibDialog.message('utredningsTypAndradModal', 'Utredningstyp ändrad',
                            'Utredningstypen är ändrad till AFU utvidgad. Nytt slutdatum är ' +
                            moment(result.nyttSistaDatum).format('YYYY-MM-DD'));
                    }
                }, function(error) {
                    if (error && error !== 'backdrop click') {
                        $log.error(error);
                    }
                });
            };

            $scope.oppnaRedovisaBesok = function() {
                openModal('redovisaBesok/redovisaBesok.modal.html', 'RedovisaBesokModalCtrl',
                    {bestallning: $scope.bestallning}).then(function(result) {
                    $log.info(result);
                }, function(error) {
                    $log.error(error);
                });
            };

            $scope.openAvvikelseModal = function(besokId) {
                openModal('avvikelse.modal.html', 'AvvikelseModalCtrl', {utredningId: $scope.bestallning.utredningsId, besokId: besokId});
            };

            $scope.openAvbokaModal = function(besokId) {
                ibDialog.confirm({
                    domId: 'avbokaModal',
                    titleText: 'Avboka besök',
                    bodyText: 'Är du säker på att du vill avboka besöket?',
                    confirmText: 'Bekräfta'
                }).then(function() {
                    BesokProxy.avboka($scope.bestallning.utredningsId, besokId).then(function(){
                        $log.info('Successfully requested avbokning');
                    }, function(){
                        ibDialog.message('failAvbokaModal',
                            'Tekniskt fel',
                            'Besöket kunde inte avbokas på grund av tekniskt fel.' +
                            ' Om problemet kvarstår, kontakta i första hand din lokala IT-avdelning' +
                            ' och i andra hand <LINK:ineraNationellKundservice>');
                    });
                });
            };

            function openModal(templateUrl, controller, resolveObject) {
                var templatePath = '/app/vardadmin/visaBestallning/ibBesok/';
                return  ibDialog.modal(templatePath + templateUrl, controller, resolveObject);
            }
        }
    };
});
