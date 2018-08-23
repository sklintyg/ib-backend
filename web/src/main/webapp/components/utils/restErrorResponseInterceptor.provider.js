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

/**
 * Response intercepter catching all responses coming back through the $http
 * service. On 500 status responses it will check if the config contains message keys to display.
 *
 * This provider will not actually display the error, but rather emit an event on the rootscope that
 * another component can react to and thus handle rest errors in a centralized fashion.
 *
 */
angular.module('ibApp').provider('restErrorResponseInterceptor',
    function() {
        'use strict';


        /**
         * Mandatory provider $get function. here we can inject the dependencies the
         * actual implementation needs.
         */
        this.$get = function($q, $rootScope, ObjectHelper) {

            function checkReturnErrorKey(value, defaultErrorKey) {
                if(!ObjectHelper.isDefined(value)) {
                    return defaultErrorKey;
                }

                return value;
            }

            function responseError(rejection) {

                if (rejection.status !== 403 && (rejection.status >= 400 || rejection.status === -1)) {

                    // If nothing else matches, use this config
                    var backupConfig = {
                        errorMessageConfig: {
                            errorTitleKey: 'server.error.ge.02.title',
                            errorTextKey: 'server.error.ge.02.text'
                        }
                    };

                    // check and fall back to generic messages if none are provided
                    if(rejection && rejection.config) {
                        if(rejection.config.errorMessageConfig) {

                            if(rejection.data.errorCode === 'BAD_STATE') {
                                rejection.config.errorMessageConfig.errorTitleKey =
                                    checkReturnErrorKey(rejection.config.errorMessageConfig.errorTitleKey,
                                        'server.error.ge.01.title');
                                rejection.config.errorMessageConfig.errorTextKey =
                                    checkReturnErrorKey(rejection.config.errorMessageConfig.errorTextKey,
                                        'server.error.ge.01.text');
                            } else {
                                rejection.config.errorMessageConfig.errorTitleKey =
                                    checkReturnErrorKey(rejection.config.errorMessageConfig.errorTitleKey,
                                        'server.error.ge.02.title');
                                rejection.config.errorMessageConfig.errorTextKey =
                                    checkReturnErrorKey(rejection.config.errorMessageConfig.errorTextKey,
                                        'server.error.ge.02.text');
                            }

                        } else {
                            rejection.config.errorMessageConfig = backupConfig.errorMessageConfig;
                        }
                    } else {
                        rejection.config = backupConfig;
                    }

                    $rootScope.$emit('ib.rest.exception', rejection.config.errorMessageConfig, rejection.data);
                }

                return $q.reject(rejection);
            }

            return {
                'responseError': responseError
            };
        };
    });
