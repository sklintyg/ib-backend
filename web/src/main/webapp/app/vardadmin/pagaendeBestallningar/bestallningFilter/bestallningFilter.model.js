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

angular.module('ibApp').factory('ibBestallningFilterModel',
    function() {
        'use strict';

        /**
         * Constructor
         */
        function BestallningFilterModel() {
            this.reset();
            this.vardgivareNamnOptions = [];
            this.statusOptions = [];
        }

        var defaultFilter = {
            currentPage: 0,
            pageSize: 50,
            freetext: '',
            vardgivareNamn: undefined,
            status: 'ALL',
            slutdatumFas: {
                from:null,
                to:null
            },
            orderBy: 'slutdatumFas',
            orderByAsc: false
        };

        BestallningFilterModel.prototype.reset = function() {
            this.currentPage = defaultFilter.currentPage;
            this.pageSize = defaultFilter.pageSize;
            this.freetext = defaultFilter.freetext;
            this.vardgivareNamn = defaultFilter.vardgivareNamn;
            this.status = defaultFilter.status;
            this.slutdatumFas = angular.copy(defaultFilter.slutdatumFas);
            this.orderBy = defaultFilter.orderBy;
            this.orderByAsc = defaultFilter.orderByAsc;
        };

        BestallningFilterModel.prototype.isDefault = function() {
            return this.currentPage === defaultFilter.currentPage &&
                this.pageSize === defaultFilter.pageSize &&
                this.freetext === defaultFilter.freetext &&
                this.vardgivareNamn === defaultFilter.vardgivareNamn &&
                this.status === defaultFilter.status &&
                angular.equals(this.slutdatumFas, defaultFilter.slutdatumFas) &&
                this.orderBy === defaultFilter.orderBy &&
                this.orderByAsc === defaultFilter.orderByAsc;
        };

        BestallningFilterModel.prototype.convertToPayload = function() {
            return {
                // Pagination
                currentPage: this.currentPage,
                pageSize: this.pageSize,
                // Filter
                freeText: this.freetext,
                status: this.status,
                vardgivareNamn: this.vardgivareNamn,
                fromDate: this.slutdatumFas.from,
                toDate: this.slutdatumFas.to,
                // Sort
                orderBy: this.orderBy,
                orderByAsc: this.orderByAsc
            };
        };

        BestallningFilterModel.prototype.populateFilter = function(data) {
            this.statusOptions = data.statuses;
            this.status = this.statusOptions[0].id;
            this.vardgivareNamnOptions = data.vardgivare;
            this.vardgivareNamnOptions.unshift({'id': undefined, 'label': 'Visa alla'});
        };

        BestallningFilterModel.build = function(pageSize) {
            return new BestallningFilterModel(pageSize);
        };

        return BestallningFilterModel;
    });
