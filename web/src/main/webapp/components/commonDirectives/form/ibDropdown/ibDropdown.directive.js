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
 * Enable help marks with tooltip for other components than wcFields
 */
angular.module('ibApp').directive('ibDropdown',
    ['$document', '$window', '$timeout', function($document, $window, $timeout) {
            'use strict';

            return {
                restrict: 'E',
                transclude: false,
                scope: {
                    id: '@',
                    items: '=',
                    onSelect: '&?',
                    useDynamicLabel: '=',
                    disabled:'<ngDisabled',
                    labelKey: '@',
                    labelColSize: '@',
                    inputColSize: '@'
                },
                templateUrl: '/components/commonDirectives/form/ibDropdown/ibDropdown.directive.html',
                require: 'ngModel',
                link: function(scope, element, attrs, ctrl) {
                    var plate = $(element).find('.plate');

                    // HTML input components are disabled if a parent fieldset is disabled.
                    var parentFieldset = $(element).parents('fieldset');
                    if (parentFieldset && parentFieldset.attr('disabled') === 'disabled') {
                        scope.disabled = true;
                        $(element).attr('disabled', 'disabled');
                    }

                    function offset(element) {
                        var boundingClientRect = element[0].getBoundingClientRect();
                        return {
                            width: boundingClientRect.width || element.prop('offsetWidth'),
                            height: boundingClientRect.height || element.prop('offsetHeight'),
                            top: boundingClientRect.top + ($window.pageYOffset || $document[0].documentElement.scrollTop),
                            left: boundingClientRect.left + ($window.pageXOffset || $document[0].documentElement.scrollLeft)
                        };
                    }

                    function openPlate() {
                        $window.document.addEventListener('click', onDocumentClick, true);

                        $timeout(function() {
                            var rootElementOffset = offset(element);
                            var offsetDropdown = offset(plate);

                            var scrollTop = $document[0].documentElement.scrollTop || $document[0].body.scrollTop;

                            if (rootElementOffset.top + rootElementOffset.height + offsetDropdown.height > scrollTop +
                                $document[0].documentElement.clientHeight) {
                                plate[0].style.top = (offsetDropdown.height * -1) - 2 + 'px';
                            } else {
                                plate[0].style.top = rootElementOffset.height + 2 + 'px';
                            }
                            plate[0].style.opacity = 1;
                        });
                        plate[0].style.opacity = 0;
                        scope.isOpen = true;
                    }

                    function closePlate() {
                        scope.isOpen = false;
                        $window.document.removeEventListener('click', onDocumentClick, true);
                    }

                    function onDocumentClick(e) {
                        if (scope.isOpen && !element[0].contains(e.target)) {
                            closePlate();
                            scope.$digest();
                        }
                    }

                    function getSelectedItemLabel() {
                        if (scope.items) {
                            for (var i = 0; i < scope.items.length; i++) {
                                if (scope.items[i].id === ctrl.$viewValue) {
                                    return scope.items[i].label;
                                }
                            }
                        }
                    }

                    scope.togglePlate = function() {
                        if (scope.disabled) {
                            return;
                        }
                        if (scope.isOpen) {
                            closePlate();
                        } else {
                            openPlate();
                        }
                    };

                    scope.select = function(item) {
                        ctrl.$setViewValue(item.id);
                        scope.selectedItemLabel = getSelectedItemLabel();
                        $timeout(function() {
                            if (scope.onSelect) {
                                scope.onSelect();
                            }
                        });
                        closePlate();
                    };

                    scope.$watch('items', function(newVal) {
                       if (newVal) {
                           scope.selectedItemLabel = getSelectedItemLabel();
                       }
                    });

                    scope.$on('$destroy', function() {
                        $window.document.removeEventListener('click', onDocumentClick, true);
                    });

                    ctrl.$render = function () {
                        scope.selectedItemLabel = getSelectedItemLabel();
                    };
                }
            };
        }]);
