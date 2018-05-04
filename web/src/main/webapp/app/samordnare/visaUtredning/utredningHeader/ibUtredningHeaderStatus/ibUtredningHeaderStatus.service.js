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

angular.module('ibApp').service('UtredningStatusService',
        function() {
            'use strict';
/*
            var service = this;

            this.getMessageKeyForIntyg = function(partialKey) {
                var key = IntygHeaderViewState.intygType + partialKey;
                if (!messageService.propertyExists(key)) {
                    key = 'common' + partialKey;
                }
                return key;
            };

            this.getMessageForIntygStatus = function(status, vars) {
                if (!status) {
                    return '';
                }
                return messageService.getProperty(service.getMessageKeyForIntyg('.label.intygstatus.' + status), vars);
            };

            this.intygStatusHasModal = function(intygStatus) {
                if (messageService.propertyExists(IntygHeaderViewState.intygType + '.modalbody.intygstatus.' + intygStatus) === '') {
                    return false;
                }
                if (messageService.propertyExists(IntygHeaderViewState.intygType + '.modalbody.intygstatus.' + intygStatus)) {
                    return true;
                }
                return Boolean(messageService.propertyExists('common.modalbody.intygstatus.' + intygStatus));
            };

            this.sortByStatusAndTimestamp = function(array) {
                /*jshint maxcomplexity:16*/
/*                array.sort(function(a, b) {
                    // Status without code should always be first
                    if (!a.code && b.code) {
                        return 1;
                    }
                    if (!b.code && a.code) {
                        return -1;
                    }

                    // Status is-001 should always be 2nd
                    if (a.code === 'is-001' && b.code !== 'is-001') {
                        return 1;
                    }
                    if (b.code === 'is-001' && a.code !== 'is-001') {
                        return -1;
                    }

                    // Status is-008 should always be 3rd
                    if (a.code === 'is-008' && b.code !== 'is-008') {
                        return 1;
                    }
                    if (b.code === 'is-008' && a.code !== 'is-008') {
                        return -1;
                    }

                    // Status is-002 should always be 4th
                    if (a.code === 'is-002' && b.code !== 'is-002') {
                        return 1;
                    }
                    if (b.code === 'is-002' && a.code !== 'is-002') {
                        return -1;
                    }

                    // Time sorts out the rest
                    if (a.timestamp < b.timestamp) {
                        return 1;
                    }
                    if (a.timestamp > b.timestamp) {
                        return -1;
                    }
                    return 0;
                });
            };
*/
        });

