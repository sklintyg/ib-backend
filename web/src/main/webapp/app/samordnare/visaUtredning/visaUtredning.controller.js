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

angular.module('ibApp')
    .controller('VisaUtredningCtrl',
        function($scope) {
            'use strict';

            var config = {
                tabs: [],
                intygContext: {
                    type: 'fk7263',
                    id: null,
                }
            };

            config.tabs.push({
                id: 'wc-arende-panel-tab',
                title: 'common.supportpanel.arende.title',
                tooltip: 'common.supportpanel.arende.tooltip',
                config: {
                    intygContext: config.intygContext
                }
            });

            config.tabs.push({
                id: 'wc-help-tips-panel-tab',
                title: 'common.supportpanel.help.title',
                tooltip: 'common.supportpanel.help.tooltip',
                config: {
                    intygContext: config.intygContext
                }
            });

            $scope.config = config;
        }
    );
