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
 * Renders a TH element with underline on hover and a tooltip on mouseover
 */
angular.module('ibApp').directive('ibTableHead',
    function(messageService) {
        'use strict';

        return {
            restrict: 'A',
            templateUrl: '/components/appDirectives/ibTableHead/ibTableHead.directive.html',
            scope: {
                labelKey: '@',
                helpKey: '@',
                filter: '<',
                orderByKey: '@',
                filterChanged: '&'
            },
            link: function($scope) {

                $scope.text = messageService.getProperty($scope.labelKey);

                $scope.hasHelp = false;
                if ($scope.helpKey) {
                    $scope.hasHelp = true;
                    $scope.helpText = messageService.getProperty($scope.helpKey);
                }

                if ($scope.filter) {
                    $scope.toggleSort = function() {
                        if ($scope.filter.orderBy === $scope.orderByKey) {
                            $scope.filter.orderByAsc = !$scope.filter.orderByAsc;
                        }
                        else {
                            $scope.filter.orderByAsc = true;
                        }

                        $scope.filter.orderBy = $scope.orderByKey;

                        $scope.filterChanged();
                    };

                    $scope.getSortClass = function() {
                        if ($scope.filter.orderBy === $scope.orderByKey) {
                            return 'fa-sort-' + ($scope.filter.orderByAsc ? 'up' : 'down');
                        }
                        return 'fa-sort';
                    };
                }
            }
        };
    });
