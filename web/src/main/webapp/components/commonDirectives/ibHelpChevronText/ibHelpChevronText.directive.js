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
 * Enable expandable help textsblocks
 */
angular.module('ibApp').directive('ibHelpChevronText',
    [ '$rootScope', '$log', 'messageService', 'ObjectHelper',
        function($rootScope, $log, messageService, ObjectHelper) {
            'use strict';

            return {
                restrict: 'AE',
                scope: {
                    helpTextKey: '@'
                },
                templateUrl: '/components/commonDirectives/ibHelpChevronText/ibHelpChevronText.directive.html',
                controller: function($scope) {

                    $scope.text = messageService.getProperty($scope.helpTextKey);
                    $scope.isCollapsed = true;

                   $scope.$on('help-chevron-' + $scope.helpTextKey, function(event, data){
                        if(!ObjectHelper.isDefined(data.id) || !ObjectHelper.isDefined($scope.helpTextKey) ||
                            data.id !== $scope.helpTextKey){
                            return;
                        }

                       $scope.isCollapsed = !$scope.isCollapsed;


                    });


                }
            };
        }]);
