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
    .config(
    /** @ngInject */
    function($stateProvider, $urlRouterProvider, $locationProvider, $uibTooltipProvider, $httpProvider,
    http403ResponseInterceptorProvider, stConfig, $compileProvider) {
    'use strict';



        //Handle app init routing logic not covered by internal app state configs.
        $urlRouterProvider.otherwise(function($injector, $location) {
            // handles server login flow error redirects, session timeouts etc
            if($location.absUrl().indexOf('?reason=') !== -1) {
                return '/app/exit';
            } else {
                // otherwise default to login landingpage
                return '/app/login';
            }

        });



        // Use /#/ syntax. True = regular / syntax
        $locationProvider.html5Mode(false);

        // Tooltip config
        $uibTooltipProvider.setTriggers({
            'show': 'hide'
        });

        // Configure 403 interceptor provider
        http403ResponseInterceptorProvider.setRedirectUrl('/?reason=inactivity-timeout');
        $httpProvider.interceptors.push('http403ResponseInterceptor');

        // Configure restErrorResponseInterceptorProvider interceptor provider
        $httpProvider.interceptors.push('restErrorResponseInterceptor');

        // Add replaceAll function to all strings.
        String.prototype.replaceAll = function(f, r) { // jshint ignore:line
            return this.split(f).join(r);
        };

        stConfig.sort.skipNatural = true;
        stConfig.sort.delay = 100;

        $compileProvider.commentDirectivesEnabled(false);
        $compileProvider.cssClassDirectivesEnabled(false);
        $compileProvider.preAssignBindingsEnabled(true);

        $locationProvider.hashPrefix('');
    });
