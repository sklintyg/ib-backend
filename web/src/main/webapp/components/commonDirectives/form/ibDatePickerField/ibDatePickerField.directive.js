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
angular.module('ibApp').directive('ibDatePickerField',['$timeout', '$parse', 'DateUtilsService',
    function($timeout, $parse, dateUtils) {
        'use strict';

        return {
            restrict: 'E',
            require:'^form',
            scope: {
                targetModel: '=',
                targetModelOptions: '=?',
                format: '@',
                dateFormatError: '@',
                domId: '@',
                onChange: '&',
                maxDate: '@',
                minDate: '@',
                minDateError: '@',
                addDateParser: '@',
                dateOptions: '@',
                onBlur: '&',
                appendToBody: '='
            },
            templateUrl: '/components/commonDirectives/form/ibDatePickerField/ibDatePickerField.directive.html',
            link: function($scope, element, attr, ctrls) {
                $scope.form = ctrls;
                //return errors for just this specific form element
                $scope.componentErrors = function() {
                    return $parse('form.' + $scope.domId + '.$error')($scope);
                };

                var activeDate = new Date();

                function setActiveDate(date) {
                    activeDate = date;
                }

                function getActiveDate() {
                    return activeDate;
                }

                $scope.dateOptions = {
                        minDate: undefined,
                        maxDate: undefined,
                        maxMode: 'day',
                        setActiveDate: setActiveDate,
                        getActiveDate: getActiveDate
                };

                if (!$scope.targetModelOptions) {
                    $scope.targetModelOptions = {};
                }

                if ($scope.minDate !== undefined) {
                    $scope.dateOptions.minDate = new Date($scope.minDate);
                }

                if ($scope.maxDate !== undefined) {
                    $scope.dateOptions.maxDate = new Date($scope.maxDate);
                }

                if($scope.format === undefined){
                    $scope.format = 'yyyy-MM-dd';
                }

                var PickerState = {
                    isOpen: false
                };
                $scope.pickerState = PickerState;

                $scope.toggleOpen = function($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    $timeout(function() {
                        $scope.pickerState.isOpen = !$scope.pickerState.isOpen;
                    });
                };

                $scope.isFocused = false;
                $scope.toggleFocus = function() {
                    if ($scope.isFocused) {
                        $scope.isFocused = false;
                    } else {
                        $scope.isFocused = true;
                    }
                };

                $scope.onDatepickerInputFieldBlur = function() {
                    $scope.toggleFocus();
                    $scope.onBlur();
                };

                $scope.focused = function() {
                    $scope.toggleFocus();
                };
                $scope.$watch('targetModel', function(newVal, oldVal) {
                    if (newVal || newVal !== oldVal) {
                        if(dateUtils.isDate(newVal)) {
                            $scope.dateOptions.initDate = new Date(newVal);
                        } else {
                            $scope.dateOptions.initDate = new Date();
                        }
                        $scope.dateOptions.setActiveDate($scope.dateOptions.initDate);
                    }
                });
            }
        };
    }])
    .directive('ibDatePickerFieldInput', ['$log', 'DateUtilsService',
    function($log, dateUtils ) {
        'use strict';
        return {
            priority: 1,
            restrict: 'A',
            require:['ngModel'],
            link: function(scope, element, attrs, ctrls) {
                var ngModel = ctrls[0];

                if (scope.addDateParser) {
                    if (scope.addDateParser === 'loose') {
                        dateUtils.addLooseDateParser(ngModel);
                    }
                    else if (scope.addDateParser === 'strict') {
                        dateUtils.addStrictDateParser(ngModel);
                    }
                    else {
                        $log.error('unknown dateparser method ' + scope.addDateParser);
                    }
                }
                var maximumDate = scope.maxDate || '2099-12-12';
                var minimumDate = scope.minDate || '1900-01-01';

                ngModel.$validators.maxDate = function() {
                    return dateUtils.isDateEmptyOrValidAndBefore(maximumDate, ngModel.$viewValue);
                };

                ngModel.$validators.minDate = function() {
                    return dateUtils.isDateEmptyOrValidAndBefore(ngModel.$viewValue, minimumDate);
                };

            }
        };
    }]);
