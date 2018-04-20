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

angular.module('ibApp').directive('ibForfraganSvarsdatum',
    function(APP_CONFIG) {
        'use strict';

        return {
            restrict: 'E',
            templateUrl: '/components/appDirectives/ibForfraganSvarsdatum/ibForfraganSvarsdatum.directive.html',
            scope: {
                status: '<',
                svarsdatum: '<'
            },
            link: function($scope) {

                if ($scope.status === 'INKOMMEN') {
                    var dateTime = moment();
                    var todaydate = moment({
                        year: dateTime.year(),
                        month: dateTime.month(),
                        day: dateTime.date()
                    }).utc(true);

                    var daysToSvarsdatum = moment($scope.svarsdatum, 'YYYY-MM-DD').utc(true).diff(todaydate, 'days');

                    if (daysToSvarsdatum < 0) {
                        $scope.warningSvarsdatumPassed = true;
                    }
                    else if (daysToSvarsdatum < APP_CONFIG.forfraganPaminnelseDagar) {
                        $scope.warningSvarsdatumPaminnelsePassed = true;
                    }
                }
            }
        };
    });
