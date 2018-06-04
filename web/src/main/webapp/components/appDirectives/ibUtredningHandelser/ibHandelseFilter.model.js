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

angular.module('ibApp').factory('ibHandelseFilterModel',
    function() {
        'use strict';

        /**
         * Constructor
         */
        function HandelseFilterModel() {
            this.reset();
            this.handelseOptions = [
                {'id': undefined, 'label': 'Visa allt'},
                {'id': 'FORFRAGAN', 'label': 'Förfrågan'},
                {'id': 'BESTALLNING', 'label': 'Beställning'},
                {'id': 'HANDLINGAR', 'label': 'Handlingar'},
                {'id': 'BESOK', 'label': 'Besök'},
                {'id': 'UTLATANDE', 'label': 'Utlåtande'}
            ];
        }

        var defaultFilter = {
            currentPage: 0,
            pageSize: 50,
            handelse: undefined,
            orderBy: 'besvarasSenastDatum',
            orderByAsc: false
        };

        HandelseFilterModel.prototype.reset = function() {
            this.currentPage = defaultFilter.currentPage;
            this.pageSize = defaultFilter.pageSize;
            this.handelse = defaultFilter.handelse;
            this.orderBy = defaultFilter.orderBy;
            this.orderByAsc = defaultFilter.orderByAsc;
        };

        HandelseFilterModel.prototype.isDefault = function() {
            return this.currentPage === defaultFilter.currentPage &&
                this.pageSize === defaultFilter.pageSize &&
                this.handelse === defaultFilter.handelse &&
                this.orderBy === defaultFilter.orderBy &&
                this.orderByAsc === defaultFilter.orderByAsc;
        };

        HandelseFilterModel.prototype.convertToPayload = function() {
            return {
                // Pagination
                currentPage: this.currentPage,
                pageSize: this.pageSize,
                // Filter
                handelse: this.handelse,
                // Sort
                orderBy: this.orderBy,
                orderByAsc: this.orderByAsc
            };
        };

        HandelseFilterModel.build = function(pageSize) {
            return new HandelseFilterModel(pageSize);
        };

        return HandelseFilterModel;
    });
