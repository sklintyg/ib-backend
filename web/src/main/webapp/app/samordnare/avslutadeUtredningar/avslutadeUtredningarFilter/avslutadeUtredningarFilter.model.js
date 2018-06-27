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
            avslutsDatum: {
                from: moment().subtract(3, 'month').format('YYYY-MM-DD'),
                to: moment().format('YYYY-MM-DD')
            },
            ersatts: 'ALL',
            fakturerad: 'ALL',
            betald: 'ALL',
            utbetaldFk: 'ALL',
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
            this.betald = defaultFilter.betald;
            this.utbetaldFk = defaultFilter.utbetaldFk;
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
                angular.equals(this.betald, defaultFilter.betald) &&
                angular.equals(this.utbetaldFk, defaultFilter.utbetaldFk) &&
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
                avslutsDatumFromDate: this.avslutsDatum.from,
                avslutsDatumToDate: this.avslutsDatum.to,
                ersatts: this.ersatts,
                fakturerad: this.fakturerad,
                betald: this.betald,
                utbetaldFk: this.utbetaldFk,

                // Sort
                orderBy: this.orderBy,
                orderByAsc: this.orderByAsc
            };
        };

        AvslutadeUtredningarFilterModel.build = function (pageSize) {
            return new AvslutadeUtredningarFilterModel(pageSize);
        };

        return AvslutadeUtredningarFilterModel;
    });
