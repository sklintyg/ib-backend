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

angular.module('ibApp').factory('ibAvslutadeUtredningarFilterModel',
    function () {
        'use strict';

        /**
         * Constructor
         */
        function AvslutadeUtredningarFilterModel() {
            this.reset();
            this.vardgivareNamnOptions = [];
            this.yesNoOptions = [
                {'id': 'ALL', 'label': 'Visa alla'},
                {'id': 'YES', 'label': 'Ja'},
                {'id': 'NO', 'label': 'Nej'}
                ];
        }

        var defaultFilter = {
            currentPage: 0,
            pageSize: 50,
            freetext: '',
            vardgivareNamn: undefined,
            avslutsDatum: {
                from: null,
                to: null
            },
            ersatts: 'ALL',
            fakturerad: 'ALL',
            utbetald: 'ALL',
            orderBy: 'avslutsDatum',
            orderByAsc: false
        };

        AvslutadeUtredningarFilterModel.prototype.reset = function () {
            this.currentPage = defaultFilter.currentPage;
            this.pageSize = defaultFilter.pageSize;
            this.freetext = defaultFilter.freetext;
            this.vardgivareNamn = defaultFilter.vardgivareNamn;
            this.avslutsDatum = angular.copy(defaultFilter.avslutsDatum);
            this.ersatts = defaultFilter.ersatts;
            this.fakturerad = defaultFilter.fakturerad;
            this.utbetald = defaultFilter.utbetald;
            this.orderBy = defaultFilter.orderBy;
            this.orderByAsc = defaultFilter.orderByAsc;
        };

        AvslutadeUtredningarFilterModel.prototype.isDefault = function () {
            return this.currentPage === defaultFilter.currentPage &&
                this.pageSize === defaultFilter.pageSize &&
                this.freetext === defaultFilter.freetext &&
                this.vardgivareNamn === defaultFilter.vardgivareNamn &&
                angular.equals(this.avslutsDatum, defaultFilter.avslutsDatum) &&
                angular.equals(this.ersatts, defaultFilter.ersatts) &&
                angular.equals(this.fakturerad, defaultFilter.fakturerad) &&
                angular.equals(this.utbetald, defaultFilter.utbetald) &&
                this.orderBy === defaultFilter.orderBy &&
                this.orderByAsc === defaultFilter.orderByAsc;
        };

        AvslutadeUtredningarFilterModel.prototype.convertToPayload = function () {
            return {
                // Pagination
                currentPage: this.currentPage,
                pageSize: this.pageSize,
                // Filter
                freeText: this.freetext,
                vardgivareNamn: this.vardgivareNamn,
                avslutsDatumFromDate: this.avslutsDatum.from,
                avslutsDatumToDate: this.avslutsDatum.to,
                ersatts: this.ersatts,
                fakturerad: this.fakturerad,
                utbetald: this.utbetald,

                // Sort
                orderBy: this.orderBy,
                orderByAsc: this.orderByAsc
            };
        };

        AvslutadeUtredningarFilterModel.prototype.populateFilter = function (data) {
            // this.statusOptions = data.statuses;
            this.ersatts = this.yesNoOptions[0].id;
            this.fakturerad = this.yesNoOptions[0].id;
            this.utbetald = this.yesNoOptions[0].id;
            this.vardgivareNamnOptions = data.vardgivare;
            this.vardgivareNamnOptions.unshift({'id': undefined, 'label': 'Visa alla'});
        };

        AvslutadeUtredningarFilterModel.build = function (pageSize) {
            return new AvslutadeUtredningarFilterModel(pageSize);
        };

        return AvslutadeUtredningarFilterModel;
    });