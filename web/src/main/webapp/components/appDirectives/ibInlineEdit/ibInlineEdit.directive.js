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

angular.module('ibApp').directive('ibInlineEdit',
    function($timeout) {
        'use strict';

        return {
            restrict: 'E',
            templateUrl: '/components/appDirectives/ibInlineEdit/ibInlineEdit.directive.html',
            scope: {
                model: '<',
                editable: '<',
                saveMethod: '&',
                busy: '='
            },
            link: function($scope, element) {
                $scope.editing = false;

                $scope.vm = {
                    model: $scope.model
                };

                $scope.edit = function() {
                    if ($scope.editable) {
                        $scope.editing = true;
                        $timeout(function() {
                            element.find('INPUT').focus();
                        });
                    }
                };
                $scope.cancel = function() {
                    $scope.vm.model = $scope.model;
                    $scope.editing = false;
                };
                $scope.save = function() {
                    $scope.model = $scope.vm.model;
                    $scope.saveMethod({model : $scope.vm.model});
                    $scope.editing = false;
                };
                $scope.onKeypress = function(keyEvent) {
                    if (keyEvent.which === 13) {
                        $scope.save();
                    }
                };
            }
        };
    });
