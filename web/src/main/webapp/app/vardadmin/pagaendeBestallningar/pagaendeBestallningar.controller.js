/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

angular.module('ibApp')
    .controller('PagaendeBestallningarCtrl',
        function($log, $scope, $state, ibBestallningFilterModel, BestallningarProxy) {
            'use strict';

            $scope.filter = ibBestallningFilterModel.build();

            $scope.visaBestallning = function(utredningsId){
                $state.go('.visaBestallning', {utredningsId: utredningsId});
            };

            $scope.getBestallningarFiltered = function(appendResults) {

                if (!appendResults) {
                    $scope.filter.currentPage = 0;
                }

                BestallningarProxy.getBestallningarWithFilter($scope.filter.convertToPayload()).then(function(data) {
                    if (appendResults) {
                        $scope.bestallningar = $scope.bestallningar.concat(data.bestallningar);
                    }
                    else {
                        $scope.bestallningar = data.bestallningar;
                    }
                    $scope.bestallningarTotal = data.totalCount;
                }, function(error) {
                    $log.error(error);
                });
            };


            BestallningarProxy.getBestallningarFilterValues().then(function(data) {
               $scope.filter.populateFilter(data);
            }, function(error) {
                $log.error(error);
            });

            $scope.getMore = function() {
                $scope.filter.currentPage++;
                $scope.getBestallningarFiltered(true);
            };

            $scope.getBestallningarFiltered();
        }
    );
