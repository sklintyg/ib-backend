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

angular.module('ibApp').directive('ibDateRangePicker',
    function(moment) {
        'use strict';

        return {
            restrict: 'E',
            scope: {
                labelKey: '@',
                placeholderKey: '@',
                model: '=',
                onChange: '&'
            },
            templateUrl: '/components/commonDirectives/ibDateRangePicker/ibDateRangePicker.directive.html',
            link: function($scope, element) {

                $.dateRangePickerLanguages.se = {
                    'selected': 'Vald:',
                    'day': 'dag',
                    'days': 'dagar',
                    'apply': 'Stäng',
                    'week-1': 'M',
                    'week-2': 'T',
                    'week-3': 'O',
                    'week-4': 'T',
                    'week-5': 'F',
                    'week-6': 'L',
                    'week-7': 'S',
                    'month-name': ['januari', 'februari', 'mars', 'april', 'maj', 'juni', 'juli', 'augusti',
                        'september', 'oktober', 'november', 'december'],
                    'shortcuts': '',
                    'custom-values': 'Anpassade värden',
                    'past': 'över',
                    'following': 'följande',
                    'previous': 'förra',
                    'prev-week': 'vecka',
                    'prev-month': 'månad',
                    'prev-year': 'år',
                    'next': 'nästa',
                    'next-week': 'vecka',
                    'next-month': 'måned',
                    'next-year': 'år',
                    'less-than': 'Datumintervall bör inte vara mindre än %d dagar',
                    'more-than': 'Datumintervall bör inte vara mer än %d dagar',
                    'default-more': 'Välj ett datumintervall längre än %d dagar',
                    'default-single': 'Välj ett datum',
                    'default-less': 'Välj ett datumintervall mindre än %d dagar',
                    'default-range': 'Välj ett datumintervall mellan %d och %d dagar',
                    'default-default': 'Välj ett datumintervall',
                    'time': 'tid',
                    'hour': 'timme',
                    'minute': 'minut'
                };

                var options = {
                    autoClose: false,
                    separator: ' to ',
                    language: 'se',
                    startOfWeek: 'monday',
                    singleMonth: false,
                    showShortcuts: true,
                    customShortcuts: [{
                        name: '<button id="closeDatePicker" class="btn btn-primary">Stäng</button>',
                        dates: function() { return null; }
                    }, {
                        name: '<button id="clearDatePicker" class="btn btn-default">Rensa</button>',
                        dates: function() { return null; }
                    }],
                    showTopbar: true,
                    hoveringTooltip: false,
                    getValue: function() {
                        if (!moment($scope.model.from, 'YYYY-MM-DD').isValid()) {
                            return '';
                        }

                        var fromDate = moment($scope.model.from).format('YYYY-MM-DD');
                        var toDate = moment($scope.model.to).format('YYYY-MM-DD');

                        return fromDate + options.separator + toDate;
                    },
                    setValue: function() {}
                };

                var inputElement = element.find('input');
                inputElement.dateRangePicker(options)
                    .on('datepicker-change', function(event, obj) {
                        /* This event will be triggered when second date is selected */
                        $scope.model.from = moment(obj.date1).format('YYYY-MM-DD');
                        $scope.model.to = moment(obj.date2).format('YYYY-MM-DD');
                        $scope.onChange();

                        setDisplayValue(true);
                    })
                    .on('datepicker-open', function() {
                        $('#clearDatePicker').click(function() {
                            inputElement.data('dateRangePicker').clear();
                            inputElement.data('dateRangePicker').redraw();

                            $scope.model.from = null;
                            $scope.model.to = null;
                            $scope.onChange();

                            setDisplayValue(true);
                        });

                        $('#closeDatePicker').click(function() {
                            inputElement.data('dateRangePicker').close();
                        });
                    });

                $scope.open = function($event) {
                    $event.preventDefault();
                    $event.stopPropagation();
                    inputElement.data('dateRangePicker').open();
                };

                $scope.$on('$destroy', function() {
                    inputElement.data('dateRangePicker').destroy();
                });

                $scope.$watch('model', function() {
                    setDisplayValue();

                    if (!moment($scope.model.from, 'YYYY-MM-DD').isValid()) {
                        inputElement.data('dateRangePicker').clear();
                    }
                }, true);

                function setDisplayValue(apply) {
                    var value = getDisplayValue($scope.model);

                    inputElement.val(value);

                    if (apply) {
                        $scope.$apply();
                    }
                }

                function getDisplayValue(model) {
                    var fromDate = moment(model.from, 'YYYY-MM-DD');
                    if (!fromDate.isValid()) {
                        return '';
                    }

                    if (fromDate.isSame(model.to)) {
                        return fromDate.format('YYYY-MM-DD');
                    }

                    return fromDate.format('YYYY-MM-DD') + '...';
                }
            }
        };
    });
