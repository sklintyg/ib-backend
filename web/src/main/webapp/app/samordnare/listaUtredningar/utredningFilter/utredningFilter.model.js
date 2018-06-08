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

angular.module('ibApp').factory('ibUtredningFilterModel',
    function() {
        'use strict';

        /**
         * Constructor
         */
        function UtredningFilterModel() {
            this.reset();
            this.fasOptions = [{
                id: null,
                label: 'Visa alla'
            },{
                id: 'FORFRAGAN',
                label: 'Förfrågan'
            },{
                id: 'UTREDNING',
                label: 'Utredning'
            },{
                id: 'KOMPLETTERING',
                label: 'Komplettering'
            },{
                id: 'REDOVISA_TOLK',
                label: 'Redovisa tolk'
            }];

            this.statusOptions = [{
                id: null,
                label: 'Visa alla'
            },{
                id: 'BEHOVER_ATGARDAS',
                label: 'Behöver åtgärdas'
            },{
                id: 'VANTAR_ANNAN_AKTOR',
                label: 'Väntar på annan aktör'
            }];
        }

        var defaultFilter = {
            currentPage: 0,
            pageSize: 50,
            freetext: '',
            fas: null,
            status: null,
            slutdatumFas: {
                from:null,
                to:null
            },
            orderBy: 'slutdatumFas',
            orderByAsc: false
        };

        UtredningFilterModel.prototype.reset = function() {
            this.currentPage = defaultFilter.currentPage;
            this.pageSize = defaultFilter.pageSize;
            this.freetext = defaultFilter.freetext;
            this.fas = defaultFilter.fas;
            this.status = defaultFilter.status;
            this.slutdatumFas = angular.copy(defaultFilter.slutdatumFas);
            this.orderBy = defaultFilter.orderBy;
            this.orderByAsc = defaultFilter.orderByAsc;
        };

        UtredningFilterModel.prototype.isDefault = function() {
            return this.currentPage === defaultFilter.currentPage &&
                this.pageSize === defaultFilter.pageSize &&
                this.freetext === defaultFilter.freetext &&
                this.fas === defaultFilter.fas &&
                this.status === defaultFilter.status &&
                angular.equals(this.slutdatumFas, defaultFilter.slutdatumFas) &&
                this.orderBy === defaultFilter.orderBy &&
                this.orderByAsc === defaultFilter.orderByAsc;
        };

        UtredningFilterModel.prototype.convertToPayload = function() {
            return {
                // Pagination
                currentPage: this.currentPage,
                pageSize: this.pageSize,
                // Filter
                freeText: this.freetext,
                status: this.status,
                fas: this.fas,
                fromDate: this.slutdatumFas.from,
                toDate: this.slutdatumFas.to,
                // Sort
                orderBy: this.orderBy,
                orderByAsc: this.orderByAsc
            };
        };

        UtredningFilterModel.build = function(pageSize) {
            return new UtredningFilterModel(pageSize);
        };

        return UtredningFilterModel;
    });