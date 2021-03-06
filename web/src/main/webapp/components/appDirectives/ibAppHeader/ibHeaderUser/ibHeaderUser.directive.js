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
 Note: This directive is not rendered unless a valid userModel is available, so all access to $scope.userModel can skips such checks.
 */
angular.module('ibApp').directive('ibHeaderUser', [ 'UserModel', function(UserModel) {
    'use strict';

    return {
        restrict: 'E',
        scope: {},
        templateUrl: '/components/appDirectives/ibAppHeader/ibHeaderUser/ibHeaderUser.directive.html',
        link: function($scope) {

            $scope.user = UserModel.get();

        }
    };
} ]);
