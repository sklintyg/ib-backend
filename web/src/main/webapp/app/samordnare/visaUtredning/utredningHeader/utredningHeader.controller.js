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
angular.module('ibApp').controller('UtredningHeaderCtrl',
    function($scope, $state, $timeout,
        UtredningViewState, UtredningHeaderViewState, UtredningHeaderService) {
       'use strict';
/*
        $scope.utredningViewState = UtredningViewState;

        UtredningHeaderViewState.setUtredningViewState(UtredningViewState, intygType);

        $scope.$on('intyg.loaded', function(event, intyg){
            if (intyg === null) {
                UtredningHeaderViewState.intygLoaded = true;
            }
            // Wait for digest to remove buttons first
            $timeout(function(){
                UtredningHeaderViewState.intygLoaded = true;
            });
        });

        $scope.$on('arenden.updated', function() {
            // Wait for digest to remove buttons first
            $timeout(function(){
                UtredningHeaderViewState.arendenLoaded = true;
            });
        });

        //Potentially we are showing a copy/forny/ersatt dialog when exiting (clicked back etc)
        // - make sure it's closed properly
        $scope.$on('$destroy', function() {
            UtredningHeaderService.closeDialogs();
        });*/
    });
