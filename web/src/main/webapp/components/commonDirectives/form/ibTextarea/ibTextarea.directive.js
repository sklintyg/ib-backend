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
angular.module('ibApp').directive('ibTextarea', [ function() {
    'use strict';

    return {
        restrict: 'E',
        scope: {
            form: '=',
            model: '=',
            labelKey: '@',
            helpKey: '@',
            placeholderText: '@',
            maxLength: '=',
            required: '=',
            requiredError: '@',
            colSize: '@'
        },
        templateUrl: '/components/commonDirectives/form/ibTextarea/ibTextarea.directive.html',
        link: function($scope, element, attr) {

            attr.$observe('id', function(id) {
                $scope.inputId = id + '_textarea';
            });

            //return errors for just this specific form element
            $scope.componentErrors = function() {
                if ($scope.required && $scope.form[$scope.inputId]) {
                    return $scope.form[$scope.inputId].$error;
                }
            };

            if (!$scope.colSize) {
                $scope.colSize = 12;
            }

        }
    };
} ]);
