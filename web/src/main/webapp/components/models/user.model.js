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

angular.module('ibApp').factory('UserModel',
    function($window, $timeout) {
        'use strict';

        var data = {};

        function _reset() {
            data.fakeSchemeId = 'urn:inera:intygsbestallning:siths:fake';

            data.authenticationScheme = null;
            data.authoritiesTree = [];
            data.currentRole = null;
            data.currentlyLoggedInAt = null;
            data.features = null;
            data.hsaId = null;
            data.namn = null;
            data.roles = [];
            data.titel = null;

            data.loggedIn = false;
            data.roleSwitchPossible = false;
            return data;
        }

        function _changeLocation(newLocation) {
            $timeout(function() {
                $window.location.href = newLocation;
            });
        }

        return {

            reset: _reset,
            init: function() {
                return _reset();
            },

            set: function(user) {
                _reset();
                data.authenticationScheme = user.authenticationScheme;
                data.authoritiesTree = user.authoritiesTree;
                data.currentRole = user.currentRole;
                data.currentlyLoggedInAt = user.currentlyLoggedInAt;
                data.features = user.features;
                data.hsaId = user.hsaId;
                data.namn = user.namn;
                data.roles = user.roles;
                data.titel = user.titel;

                data.loggedIn = true;
            },
            get: function() {
                return data;
            },
            getUnitNameById: function(id) {
                var result = '';
                if (angular.isArray(data.authoritiesTree)) {

                    angular.forEach(data.authoritiesTree, function(vg) {
                        angular.forEach(vg.vardenheter, function(ve) {
                            if (ve.id === id) {
                                result = ve.namn;
                                return;
                            }
                            angular.forEach(ve.mottagningar, function(m) {
                                if (m.id === id) {
                                    result = m.namn;
                                    return;
                                }
                            });
                        });
                    });
                }
                return result;
            },

            isRoleSwitchPossible: function() {
                return data.roleSwitchPossible;
            },

            fakeLogin: function() {
                if (data.authenticationScheme === data.fakeSchemeId) {
                    _changeLocation('/welcome.html');
                }
            },
            logout: function() {
                if (data.authenticationScheme === data.fakeSchemeId) {
                    _changeLocation('/logout');
                } else {
                    _changeLocation('/saml/logout/');
                }
            },
            getLogoutLocation: function() {
                if (data.authenticationScheme === data.fakeSchemeId) {
                    return '/logout';
                } else {
                    return '/saml/logout/';
                }
            },
            hasFeature: function(feature) {
                for (var a = 0; a < data.features.length; a++) {
                    if (data.features[a] === feature) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
);
