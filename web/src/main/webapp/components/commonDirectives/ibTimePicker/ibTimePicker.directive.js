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

angular.module('ibApp').directive('ibTimePicker',
    function(moment, $document, $window, $timeout, DateUtilsService) {
        'use strict';

        return {
            restrict: 'E',
            scope: {
                labelKey: '@',
                placeholderKey: '@',
                date: '='
            },
            templateUrl: '/components/commonDirectives/ibTimePicker/ibTimePicker.directive.html',
            link: function(scope, element) {
                var plate = $(element).find('.plate');

                scope.open = function() {
                    open();
                };

                function offset(element) {
                    var boundingClientRect = element[0].getBoundingClientRect();
                    return {
                        width: boundingClientRect.width || element.prop('offsetWidth'),
                        height: boundingClientRect.height || element.prop('offsetHeight'),
                        top: boundingClientRect.top + ($window.pageYOffset || $document[0].documentElement.scrollTop),
                        left: boundingClientRect.left + ($window.pageXOffset || $document[0].documentElement.scrollLeft)
                    };
                }

                function open() {
                    if (!DateUtilsService.isDate(scope.date)) {
                        scope.date = new Date();
                        setTimeStringFromDate(scope.date);
                    }
                    $window.document.addEventListener('click', onDocumentClick, true);

                    $timeout(function() {
                        var rootElementOffset = offset(element);
                        var offsetDropdown = offset(plate);

                        var scrollTop = $document[0].documentElement.scrollTop || $document[0].body.scrollTop;

                        if (rootElementOffset.top + rootElementOffset.height +
                            offsetDropdown.height > scrollTop + $document[0].documentElement.clientHeight) {
                            plate[0].style.top = (offsetDropdown.height * -1) - 2 + 'px';
                        } else {
                            plate[0].style.top = rootElementOffset.height + 2 + 'px';
                        }
                        plate[0].style.opacity = 1;
                    });
                    plate[0].style.opacity = 0;
                    scope.isOpen = true;
                }

                function close() {
                    scope.isOpen = false;
                    $window.document.removeEventListener('click', onDocumentClick, true);
                }

                function onDocumentClick(e) {
                    if (scope.isOpen && !element[0].contains(e.target)) {
                        close();
                        scope.$digest();
                    }
                }

                function setTimeStringFromDate(date) {
                    var hour = parseInt(date.getHours(), 10);
                    var min = parseInt(date.getMinutes(), 10);
                    scope.time = (hour > 9 ? hour : '0' + hour) + ':' +
                        (min > 9 ? min : '0' + min);
                }

                scope.updateDate = function() {
                    if (scope.time && scope.time.match(':').length > 0) {
                        var hour = parseInt(scope.time.split(':')[0], 10);
                        var min = parseInt(scope.time.split(':')[1], 10);
                        var date;
                        if (!DateUtilsService.isDate(scope.date)) {
                            date = new Date();
                        } else {
                            date = new Date(scope.date);
                        }

                        date.setHours(hour);
                        date.setMinutes(min);
                        scope.date = date;
                    } else if(scope.time === '') {
                        scope.date = undefined;
                    }
                };

                scope.updateTime = function() {
                    if(scope.date && DateUtilsService.isDate(scope.date)) {
                        setTimeStringFromDate(scope.date);
                    }
                };
            }
        };
    });