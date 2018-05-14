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

angular.module('ibApp').directive('ibFakturerad',
    function() {
        'use strict';

        return {
            restrict: 'E',
            templateUrl: '/components/appDirectives/ibFakturerad/ibFakturerad.directive.html',
            scope: {
                fakturerad: '<'
            },
            link: function($scope) {
                $scope.editing = false;

                $scope.edit = function() {
                    $scope.editing = true;
                };
                $scope.cancel = function() {
                    $scope.editing = false;
                };
                $scope.save = function() {
                    // Implement real saving...
                    $scope.editing = false;
                };
            }
        };
    });
