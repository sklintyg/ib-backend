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

angular.module('ibApp').factory('ibEnhetFilterModel',
    function() {
        'use strict';

        /**
         * Constructor
         */
        function EnhetFilterModel() {
            this.reset();
        }

        var defaultFilter = {
            currentPage: 0,
            pageSize: 50,
            freetext: '',
            orderBy: 'namn',
            orderByAsc: false
        };

        EnhetFilterModel.prototype.reset = function() {
            this.currentPage = defaultFilter.currentPage;
            this.pageSize = defaultFilter.pageSize;
            this.freetext = defaultFilter.freetext;
            this.orderBy = defaultFilter.orderBy;
            this.orderByAsc = defaultFilter.orderByAsc;
        };

        EnhetFilterModel.prototype.isDefault = function() {
            return this.currentPage === defaultFilter.currentPage &&
                this.pageSize === defaultFilter.pageSize &&
                this.freetext === defaultFilter.freetext &&
                this.orderBy === defaultFilter.orderBy &&
                this.orderByAsc === defaultFilter.orderByAsc;
        };

        EnhetFilterModel.prototype.convertToPayload = function() {
            return {
                // Pagination
                currentPage: this.currentPage,
                pageSize: this.pageSize,
                // Filter
                freeText: this.freetext,
                // Sort
                orderBy: this.orderBy,
                orderByAsc: this.orderByAsc
            };
        };

        EnhetFilterModel.build = function(pageSize) {
            return new EnhetFilterModel(pageSize);
        };

        return EnhetFilterModel;
    });