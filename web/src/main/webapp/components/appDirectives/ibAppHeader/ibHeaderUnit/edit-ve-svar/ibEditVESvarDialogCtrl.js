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
angular.module('ibApp').controller('ibEditVESvarDialogCtrl', [ '$scope', '$log', 'VardenhetProxy', function($scope, $log, VardenhetProxy) {
    'use strict';
    $scope.vm = {
        model: null,
        loading: true,
        saving: false
    };

    VardenhetProxy.getVardenhetSvarPreference().then(function(vardenhetPreference) {
        $scope.vm.model = vardenhetPreference;
    }, function(error) {
        $log.error('failed to load preference!' + error);
    }).finally(function() { // jshint ignore:line
        $scope.vm.loading = false;
    });

    $scope.saveChanges = function() {
        $scope.vm.saving = true;
        var payload = {
            standardsvar: $scope.vm.model.standardsvar
        };
        VardenhetProxy.setVardenhetSvarPreference(payload).then(function() {
            $scope.$dismiss();
        }, function(error) {
            $log.error('failed to save preference!' + error);
        }).finally(function() { // jshint ignore:line
            $scope.vm.saving = false;
        });
    };

} ]);
