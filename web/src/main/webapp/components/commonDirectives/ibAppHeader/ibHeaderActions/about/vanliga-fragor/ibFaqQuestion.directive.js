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
angular.module('ibApp').directive('ibFaqQuestion', ['smoothScroll', function(smoothScrollService) {
    'use strict';

    return {
        require: '^^?ibFaqToggler',
        restrict: 'E',
        transclude: true,
        scope: {
            title: '@'
        },
        templateUrl: '/components/commonDirectives/ibAppHeader/ibHeaderActions/about/vanliga-fragor/ibFaqQuestion.directive.html',
        link: function($scope, elem, attrs, ibFaqToggler) {

            var scrollContainerId = 'ib-about-modal-body';
            //Local open/closed state
            $scope.vm = {
                open: false
            };

            // Local toggle (that also will reset parent's global state)
            $scope.toggle = function() {
                $scope.vm.open = !$scope.vm.open;
                //tell parent wcFaqToggler (if present) that i've toggled, so that it can update it's state
                if (ibFaqToggler) {
                    ibFaqToggler.someChildToggledItself();
                }
                if ($scope.vm.open) {
                    var offset = Math.floor($('#'+ scrollContainerId).height() / 2);
                    var options = {
                        containerId: scrollContainerId,
                        duration: 500,
                        easing: 'easeInOutQuart',
                        offset: offset
                    };
                    //scroll to this questions panel heading, centered vertically
                    var elementToScrollTo = elem.find('.panel-heading')[0];
                    smoothScrollService(elementToScrollTo, options);
                }
            };

            // React to changes made to parent wcFaqToggler's global opened/closed state
            if (ibFaqToggler) {
                $scope.$watch(function() {
                    return ibFaqToggler.getGlobalState();
                }, function(globalExpandState) {
                    if (!angular.isUndefined(globalExpandState)) {
                        $scope.vm.open = globalExpandState;
                    }

                });
            }


        }
    };
} ]);
