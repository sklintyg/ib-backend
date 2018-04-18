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
    .controller('ListaUtredningarCtrl',
        function($log, $scope, UtredningarProxy) {
            'use strict';

            $scope.utredningarConfig = [{
                headerKey: 'label.table.utredningar.column.id',
                prop: 'utredningsId'
            },{
                headerKey: 'label.table.utredningar.column.typ',
                prop: 'utredningsTyp'
            },{
                headerKey: 'label.table.utredningar.column.vardenhet',
                prop: 'vardgivareNamn'
            },{
                headerKey: 'label.table.utredningar.column.fas',
                prop: 'fas'
            },{
                headerKey: 'label.table.utredningar.column.slutdatumFas',
                prop: 'slutdatumFas'
            },{
                headerKey: 'label.table.utredningar.column.status',
                prop: 'status'
            }];

            UtredningarProxy.getUtredningar().then(function(data) {
                $scope.utredningar = data.utredningar;
                $scope.utredningarTotal = data.totalCount;
            }, function(error) {
                $log.error(error);
            });

        }
    );