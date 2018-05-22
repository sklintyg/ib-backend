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
angular.module('ibApp').factory('ibAuthorityService',
    function(UserModel, featureService) {
        'use strict';

        function _isAuthorityActive(options) {
            var feature = options.feature;
            var role = options.role;
            var authority = options.authority;
            var requestOrigin = options.requestOrigin;
            var intygstyp = options.intygstyp;

            return  check(feature, featureCheck, intygstyp) &&
                check(role, roleCheck) &&
                check(authority, privilegeCheck, intygstyp) &&
                check(requestOrigin, requestOriginCheck);
        }

        function checkEach(toCheck, fn, intygstyp) {
            var result = true;
            var array = toCheck.split(',');
            for(var i = 0; i < array.length; i++){
                result = fn(array[i].trim(), intygstyp);
                if(!result){
                    // as soon as we get a false result return it
                    return false;
                }
            }

            return true;
        }

        function check(toCheck, fn, intygstyp){
            var res = true;
            if(toCheck !== undefined && toCheck.length > 0) {
                if(toCheck.indexOf(',') > 0){
                    res = checkEach(toCheck, fn, intygstyp);
                } else {
                    res = fn(toCheck, intygstyp);
                }
            }

            return res;
        }

        function roleCheck(role){
            if (role !== undefined && role.length > 0) {
                if (role.indexOf('!') === 0) {
                    // we have a not
                    role = role.slice(1);
                    return !UserModel.hasRole(role);
                } else {
                    return UserModel.hasRole(role);
                }
            }

            return true;
        }

        function privilegeCheck(privilege, intygstyp) {
            if (privilege !== undefined && privilege.length > 0) {
                if (privilege.indexOf('!') === 0) {
                    // we have a not
                    privilege = privilege.slice(1);
                    return !UserModel.hasPrivilege(privilege, intygstyp);
                } else {
                    return UserModel.hasPrivilege(privilege, intygstyp);
                }
            }

            return true;
        }

        function featureCheck(feature, intygstyp){
            if (feature !== undefined && feature.length > 0) {
                if (feature.indexOf('!') === 0) {
                    // we have a not
                    feature = feature.slice(1);
                    return !featureService.isFeatureActive(feature, intygstyp);
                } else {
                    return featureService.isFeatureActive(feature, intygstyp);
                }
            }

            return true;
        }

        /**
         * Check the current user's origin.
         *
         * 1. Om requestOrigin finns måste användaren ha den
         *
         * @param requestOrigin
         */
        function requestOriginCheck(requestOrigin) {
            if (requestOrigin !== undefined && requestOrigin.length > 0) {
                if (requestOrigin.indexOf('!') === 0) {
                    // we have a not
                    requestOrigin = requestOrigin.slice(1);
                    return !UserModel.hasRequestOrigin(requestOrigin);
                } else {
                    return UserModel.hasRequestOrigin(requestOrigin);
                }
            }

            return true;
        }

        return {
            isAuthorityActive: _isAuthorityActive
        };
    });
