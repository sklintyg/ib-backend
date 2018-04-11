/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

angular
    .module('ibApp')
    .run(function($log, $rootScope, $state, $window, messageService, dynamicLinkService, UserProxy,
        UserModel, USER_DATA, LINKS, $uibModalStack, $animate) {
    'use strict';

        // For testability
        $window.disableAnimations = function() {
            $animate.enabled(false);
        };

        // Always scroll to top
        $rootScope.$on('$stateChangeSuccess', function() {
            $('html, body').animate({
                scrollTop: 0
            }, 200);
        });

        $rootScope.lang = 'sv';
        $rootScope.DEFAULT_LANG = 'sv';

        // Populate user with resolved user state
        UserModel.init();
        if (angular.isDefined(USER_DATA)) {
            UserModel.set(USER_DATA);
        }

        /* jshint -W117 */
        messageService.addResources(ibMessages);// jshint ignore:line
        messageService.addLinks(LINKS);

        dynamicLinkService.addLinks(LINKS);


        //Configure app wide routing rules
        $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState/*, fromParams*/) {

            //Close any open dialogs on state change
            $uibModalStack.dismissAll();

            $log.debug('$stateChangeStart: from "' + fromState.name + '" to "' + toState.name + '"');


            if (!UserModel.get().loggedIn && toState.name !== 'app.login') {
                // app.login is the only valid route when not authenticated
                _redirect($state, toState.name, event, 'app.login', {}, {
                    location: 'replace'
                });
            } else if (UserModel.get().loggedIn && UserModel.get().currentRole === null && toState.name !== 'app.selectunit') {
                // app.selectunit is the only valid route when no vardenhet selected
                _redirect($state, toState.name, event, 'app.selectunit', {}, {
                    location: false
                });
            } if (toState.data && angular.isFunction(toState.data.rule)) {
                var result = toState.data.rule(fromState, toState, UserModel);
                if (result && result.to) {
                    _redirect($state, toState.name, event, result.to, result.params, result.options);
                }
            }
        });

        function _redirect($state,  originalTo, event, to, params , options) {
            $log.debug('Overriding ' + originalTo + ' --> ' + to);
            event.preventDefault();
            $state.go(to, params, options);
        }

        $rootScope.$on('$stateChangeError', function(event, toState/*, toParams, fromState, fromParams, error*/) {
            $log.log('$stateChangeError');
            $log.log(toState);
        });
    });
