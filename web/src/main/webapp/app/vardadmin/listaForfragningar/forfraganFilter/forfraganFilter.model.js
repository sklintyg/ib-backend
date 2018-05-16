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

angular.module('ibApp').factory('ibForfraganFilterModel',
    function() {
        'use strict';

        /**
         * Constructor
         */
        function ForfraganFilterModel() {
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
            inkomDatum: {
                from:null,
                to:null
            },
            besvarasSenastDatum: {
                from:null,
                to:null
            },
            planeringsDatum: {
                from:null,
                to:null
            },
            orderBy: 'besvarasSenastDatum',
            orderByAsc: false
        };

        ForfraganFilterModel.prototype.reset = function() {
            this.currentPage = defaultFilter.currentPage;
            this.pageSize = defaultFilter.pageSize;
            this.freetext = defaultFilter.freetext;
            this.vardgivareNamn = defaultFilter.vardgivareNamn;
            this.status = defaultFilter.status;
            this.inkomDatum = angular.copy(defaultFilter.inkomDatum);
            this.besvarasSenastDatum = angular.copy(defaultFilter.besvarasSenastDatum);
            this.planeringsDatum = angular.copy(defaultFilter.planeringsDatum);
            this.orderBy = defaultFilter.orderBy;
            this.orderByAsc = defaultFilter.orderByAsc;
        };

        ForfraganFilterModel.prototype.isDefault = function() {
            return this.currentPage === defaultFilter.currentPage &&
                this.pageSize === defaultFilter.pageSize &&
                this.freetext === defaultFilter.freetext &&
                this.vardgivareNamn === defaultFilter.vardgivareNamn &&
                this.status === defaultFilter.status &&
                angular.equals(this.inkomDatum, defaultFilter.inkomDatum) &&
                angular.equals(this.besvarasSenastDatum, defaultFilter.besvarasSenastDatum) &&
                angular.equals(this.planeringsDatum, defaultFilter.planeringsDatum) &&
                this.orderBy === defaultFilter.orderBy &&
                this.orderByAsc === defaultFilter.orderByAsc;
        };

        ForfraganFilterModel.prototype.convertToPayload = function() {
            return {
                // Pagination
                currentPage: this.currentPage,
                pageSize: this.pageSize,
                // Filter
                freeText: this.freetext,
                status: this.status,
                vardgivareNamn: this.vardgivareNamn,
                inkommetFromDate: this.inkomDatum.from,
                inkommetToDate: this.inkomDatum.to,

                besvarasSenastDatumFromDate: this.besvarasSenastDatum.from,
                besvarasSenastDatumToDate: this.besvarasSenastDatum.to,

                planeringFromDate: this.planeringsDatum.from,
                planeringToDate: this.planeringsDatum.to,
                // Sort
                orderBy: this.orderBy,
                orderByAsc: this.orderByAsc
            };
        };

        ForfraganFilterModel.prototype.populateFilter = function(data) {
            this.statusOptions = data.statuses;
            this.status = this.statusOptions[0].id;
            this.vardgivareNamnOptions = data.vardgivare;
            this.vardgivareNamnOptions.unshift({'id': undefined, 'label': 'Visa alla'});
        };

        ForfraganFilterModel.build = function(pageSize) {
            return new ForfraganFilterModel(pageSize);
        };

        return ForfraganFilterModel;
    });
