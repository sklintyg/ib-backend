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
angular.module('ibApp').directive('ibHorisontalTextfield', [ '$parse', function($parse) {
    'use strict';

    return {
        restrict: 'E',
        require: '^form',
        scope: {
            model: '=',
            labelKey: '@',
            placeholderKey: '@',
            maxLength: '=',
            pattern: '@',
            patternError: '@',
            required: '=',
            requiredError: '@',
            isDisabled: '=',
            onChange: '&'
        },
        templateUrl: '/components/commonDirectives/form/ibHorisontalTextfield/ibHorisontalTextfield.directive.html',
        link: function($scope, element, attr, formCtrl) {
            $scope.form = formCtrl;

            attr.$observe('id', function(id) {
                $scope.inputId = id + '_input';
            });

            //return errors for just this specific form element
            $scope.componentErrors = function() {
                return $parse('form.' + $scope.inputId + '.$error')($scope);
            };

        }
    };
} ]);
