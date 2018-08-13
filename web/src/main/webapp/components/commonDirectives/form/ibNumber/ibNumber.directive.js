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
/**
 * Directive to make sure value is only numbers
 */
angular.module('ibApp').directive('ibNumber', ['ObjectHelper',
    function(ObjectHelper) {
        'use strict';

        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs, ngModel) {

                var active = attrs.ibNumber;
                if(!ObjectHelper.stringBoolToBool(active)){
                    return;
                }

                function handleViewValueUpdate(newValue, oldValue) {

                    if(!newValue) {
                        return;
                    }

                    function preventUnwantedCharacters(newValue, oldValue) {

                        function updateViewValue(value) {
                            ngModel.$setViewValue(value);
                            ngModel.$render();
                        }

                        var lookingLikeNr = /^[0-9\s]*$/i;

                        // if new value is longer than older we care, otherwise something that we already approved was removed
                        if (!oldValue || (newValue.length > oldValue.length)) {
                            if (!newValue.match(lookingLikeNr)) {
                                // remove last addition if it doesn't match the pnr pattern or if dash was added prematurely/late
                                newValue = oldValue;
                                updateViewValue(newValue);
                            }
                        }
                    }

                    preventUnwantedCharacters(newValue, oldValue);
                }

                scope.$watch(function() {
                    return ngModel.$viewValue;
                }, handleViewValueUpdate);

            }
        };
    }]);
