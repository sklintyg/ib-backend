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

angular.module('ibApp').factory('ibAvslutadeBestallningarFilterModel',
    function () {
        'use strict';

        /**
         * Constructor
         */
        function AvslutadeBestallningarFilterModel() {
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
            vardgivareHsaId: undefined,
            avslutsDatum: {
                from: moment().subtract(3, 'month'),
                to: moment()
            },
            ersatts: 'ALL',
            fakturerad: 'ALL',
            utbetald: 'ALL',
            orderBy: 'avslutsDatum',
            orderByAsc: false
        };

        AvslutadeBestallningarFilterModel.prototype.reset = function () {
            this.currentPage = defaultFilter.currentPage;
            this.pageSize = defaultFilter.pageSize;
            this.freetext = defaultFilter.freetext;
            this.vardgivareHsaId = defaultFilter.vardgivareHsaId;
            this.avslutsDatum = angular.copy(defaultFilter.avslutsDatum);
            this.ersatts = defaultFilter.ersatts;
            this.fakturerad = defaultFilter.fakturerad;
            this.utbetald = defaultFilter.utbetald;
            this.orderBy = defaultFilter.orderBy;
            this.orderByAsc = defaultFilter.orderByAsc;
        };

        AvslutadeBestallningarFilterModel.prototype.isDefault = function () {
            return this.currentPage === defaultFilter.currentPage &&
                this.pageSize === defaultFilter.pageSize &&
                this.freetext === defaultFilter.freetext &&
                this.vardgivareHsaId === defaultFilter.vardgivareHsaId &&
                angular.equals(this.avslutsDatum, defaultFilter.avslutsDatum) &&
                angular.equals(this.ersatts, defaultFilter.ersatts) &&
                angular.equals(this.fakturerad, defaultFilter.fakturerad) &&
                angular.equals(this.utbetald, defaultFilter.utbetald) &&
                this.orderBy === defaultFilter.orderBy &&
                this.orderByAsc === defaultFilter.orderByAsc;
        };

        AvslutadeBestallningarFilterModel.prototype.convertToPayload = function () {
            return {
                // Pagination
                currentPage: this.currentPage,
                pageSize: this.pageSize,
                // Filter
                freeText: this.freetext,
                vardgivareHsaId: this.vardgivareHsaId,
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

        AvslutadeBestallningarFilterModel.prototype.populateFilter = function (data) {
            // this.statusOptions = data.statuses;
            this.ersatts = this.yesNoOptions[0].id;
            this.fakturerad = this.yesNoOptions[0].id;
            this.utbetald = this.yesNoOptions[0].id;
            this.vardgivareNamnOptions = data.vardgivare;
            this.vardgivareNamnOptions.unshift({'id': undefined, 'label': 'Visa alla'});
        };

        AvslutadeBestallningarFilterModel.build = function (pageSize) {
            return new AvslutadeBestallningarFilterModel(pageSize);
        };

        return AvslutadeBestallningarFilterModel;
    });
