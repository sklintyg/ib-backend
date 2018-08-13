/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

angular.module('ibApp').factory('ObjectHelper',
    function() {
        'use strict';

        return {
            isDefined: function(value) {
                return value !== null && typeof value !== 'undefined';
            },
            isEmpty: function(value) {
                return value === null || typeof value === 'undefined' || value === '';
            },
            returnJoinedArrayOrNull: function(value) {
                return value !== null && value !== undefined ? value.join(', ') : null;
            },
            valueOrNull: function(value) {
                return value !== null && value !== undefined ? value : null;
            },
            stringBoolToBool: function(value){
                return value === true || value === 'true';
            },
            isValidValue: function(value) {

                if (angular.isNumber(value)) {
                    return true;
                }

                if (angular.isString(value)) {
                    return value.length > 0;
                }

                if (angular.isArray(value)) {
                    return value.length > 0;
                }

                if (angular.isDefined(value) && angular.isObject(value)) {
                    return true;
                }

                if (value === true || value === false) {
                    return true;
                }

                return false;
            }
        };
    }
);
