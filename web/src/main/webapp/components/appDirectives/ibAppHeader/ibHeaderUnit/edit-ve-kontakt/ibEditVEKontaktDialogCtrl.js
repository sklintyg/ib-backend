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
angular.module('ibApp').controller('ibEditVEKontaktDialogCtrl', [ '$scope', '$log', 'VardenhetProxy', 'EMAIL_REGEXP_PATTERN', 'UtforareTyp',
    function($scope, $log, VardenhetProxy, EMAIL_REGEXP_PATTERN, UtforareTyp) {
    'use strict';
    $scope.vm = {
        emailPattern: EMAIL_REGEXP_PATTERN,
        utforareTyp: UtforareTyp,
        model: null,
        loading: true,
        saving: false,
        fetchinghsa: false
    };

    VardenhetProxy.getVardenhetKontaktPreference(UtforareTyp).then(function(vardenhetPreference) {
        $scope.vm.model = vardenhetPreference;

        // Presentera postnumret i format 3+2 siffror
        if ($scope.vm.model.postnummer) {
            $scope.vm.model.postnummer = $scope.vm.model.postnummer.slice(0, 3) + ' ' + $scope.vm.model.postnummer.slice(3);
        }

    }, function(error) {
        $log.error('failed to load ' + UtforareTyp + ' preference!' + error);
    }).finally(function() { // jshint ignore:line
        $scope.vm.loading = false;
    });

    $scope.saveChanges = function() {
        $scope.vm.saving = true;

        // Ta bort mellanslag innan sparas i db
        $scope.vm.model.postnummer = $scope.vm.model.postnummer.replace(/\s/g, '');
        $scope.vm.model.telefonnummer = $scope.vm.model.telefonnummer.replace(/\s/g, '');

          VardenhetProxy.setVardenhetKontaktPreference($scope.vm.model).then(function() {
            $scope.$dismiss();
        }, function(error) {
            $log.error('failed to save preference!' + error);
        }).finally(function() { // jshint ignore:line
            $scope.vm.saving = false;
        }); 
    };
    $scope.getFromHsa = function() {
        $scope.vm.fetchinghsa = true;
        VardenhetProxy.getHsaInfo().then(function(hsaResponse) {
            $scope.vm.model.mottagarNamn = hsaResponse.mottagarNamn;
            $scope.vm.model.adress = hsaResponse.adress;
            $scope.vm.model.postnummer = hsaResponse.postnummer.slice(0, 3) + ' ' + hsaResponse.postnummer.slice(3);
            $scope.vm.model.postort = hsaResponse.postort;
            $scope.vm.model.telefonnummer = hsaResponse.telefonnummer;
            $scope.vm.model.epost = hsaResponse.epost;
        }, function(error) {
            $log.error('failed to get from hsa preference!' + error);
        }).finally(function() { // jshint ignore:line
            $scope.vm.fetchinghsa = false;
        });
    };

} ]);
