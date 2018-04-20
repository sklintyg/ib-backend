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
angular.module('ibApp').directive('ibHeaderActions',
        [ '$window', '$rootScope', '$uibModal', 'StringHelper', 'APP_CONFIG', 'UserModel',
            function($window, $rootScope, $uibModal, StringHelper, APP_CONFIG, UserModel) {
            'use strict';

            return {
                restrict: 'E',
                scope: {},
                templateUrl: '/components/commonDirectives/ibAppHeader/ibHeaderActions/ibHeaderActions.directive.html',
                link: function($scope) {

                    var aboutModalInstance;

                    $scope.user = UserModel.get();

                    function _canChangeSystemRole(user) {
                        return angular.isObject(user) && user.currentlyLoggedInAt &&
                            user.authoritiesTree && user.authoritiesTree.length > 1;
                    }
                    $scope.vm = {
                        expanded:  false
                    };

                    function updateVm() {
                        $scope.vm.showAbout = $scope.user.loggedIn;
                        $scope.vm.showLogout = $scope.user.loggedIn;
                        $scope.vm.showSettings = $scope.user.currentlyLoggedInAt;
                        $scope.vm.showChangeSystemRole = _canChangeSystemRole($scope.user);
                    }

                    updateVm();
                    $scope.$watch('user', function() { updateVm();}, true);

                    $scope.toggleMenu = function($event) {
                        $event.stopPropagation();
                        $scope.vm.expanded = !$scope.vm.expanded;

                    };

                    $scope.onLogoutClick = function() {
                        if (StringHelper.endsWith($scope.user.authenticationScheme, ':fake')) {
                            $window.location = '/logout';
                        } else {
                            $window.location = '/saml/logout/';
                        }
                    };

                    // About ----------------------------------------------------------------


                    //To make sure we close any open dialog we spawned, register a listener to state changes
                    // so that we can make sure we close them if user navigates using browser back etc.
                    var unregisterFn = $rootScope.$on('$stateChangeStart', function() {
                        if (aboutModalInstance) {
                            aboutModalInstance.close();
                            aboutModalInstance = undefined;
                        }
                    });

                    //Since rootscope event listeners aren't unregistered automatically when this directives
                    //scope is destroyed, let's take care of that.
                    // (currently this directive is used only in the wc-header which lives throughout an entire session,
                    // so not that critical right now)
                    $scope.$on('$destroy', unregisterFn);

                    $scope.onAboutClick = function() {
                        aboutModalInstance = $uibModal.open({
                            templateUrl: '/components/commonDirectives/ibAppHeader/ibHeaderActions/about/aboutDialog.template.html',
                            size: 'lg',
                            controller: function($scope, $uibModalInstance, user) {

                                $scope.version = APP_CONFIG.version;
                                $scope.user = user;

                                $scope.close = function() {
                                    $uibModalInstance.close();
                                };

                            },
                            resolve: {
                                user: UserModel.get()
                            }
                        });
                        //angular > 1.5 warns if promise rejection is not handled (e.g backdrop-click == rejection)
                        aboutModalInstance.result.catch(function () {}); //jshint ignore:line
                    };
                }
            };
        } ]);
