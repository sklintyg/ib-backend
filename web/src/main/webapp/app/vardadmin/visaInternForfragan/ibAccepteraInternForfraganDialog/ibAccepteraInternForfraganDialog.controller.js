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

angular.module('ibApp').controller('ibAccepteraInternForfraganDlgController',
    function($log, $scope, $state, $stateParams, InternForfraganSvarViewState, VardenhetProxy, InternForfraganProxy, EMAIL_REGEXP_PATTERN) {
        'use strict';

        $scope.vm = {
            emailPattern: EMAIL_REGEXP_PATTERN,
            model: InternForfraganSvarViewState.getModel(),
            busy :false,
            minBorjaDatum: moment().format('YYYY-MM-DD')
        };

        //Connect form instance
        $scope.$watch('forfraganSvarForm', function(form) {
            InternForfraganSvarViewState.setForm(form);
        });


        $scope.onChangeUtforareTyp = function() {


            $scope.vm.fetchingPreferences = true;
            VardenhetProxy.getVardenhetKontaktPreference($scope.vm.model.utforareTyp).then(function(pref) {
                InternForfraganSvarViewState.clearUtforare();
                $scope.vm.model.utforareNamn = pref.mottagarNamn;
                $scope.vm.model.utforareAdress = pref.adress;
                // Lägger till mellanslag till postnumret
                $scope.vm.model.utforarePostnr = pref.postnummer.slice(0, 3) + ' ' + pref.postnummer.slice(3);;
                $scope.vm.model.utforarePostort = pref.postort;
                $scope.vm.model.utforareTelefon = pref.telefonnummer;
                $scope.vm.model.utforareEpost = pref.epost;
                $scope.vm.model.kommentar = pref.standardsvar;
            }, function(error) {
                $log.error('failed to load preference!' + error);
            }).finally(function() { // jshint ignore:line
                $scope.vm.fetchingPreferences = false;
            });
        };


        $scope.onChangeUtforareTyp();

        $scope.acceptButtonEnabled = function() {
            return InternForfraganSvarViewState.isValidToSubmit();
        };

        $scope.onConfirmAccept = function() {
            $log.debug('onConfirmAccept()');
            var model = angular.copy(InternForfraganSvarViewState.getModel());
            model.svarTyp='ACCEPTERA';
            // Tar bort mellanrummet i postnumret innan det sparas
            model.utforarePostnr = model.utforarePostnr.replace(/\s/g, '');
            $scope.vm.busy = true;
            InternForfraganProxy.accepteraInternForfragan($stateParams.utredningsId, model).then(function() {
                //Things in related utredningsstate could have changed - reload state to make sure we show correct state of everything
                $state.reload();
            }, function(error) {}).finally(function() { // jshint ignore:line
                $scope.vm.busy = false;
            });
        };

    });
