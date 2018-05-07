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
angular.module('ibApp').directive('ibUtredningHeaderStatus',
    function() {
        'use strict';

        return {
            restrict: 'E',
            scope: {
                utredning: '=',
                intygViewState: '='
            },
            templateUrl: '/app/samordnare/visaUtredning/utredningHeader/ibUtredningHeaderStatus/ibUtredningHeaderStatus.directive.html',
            link: function($scope) {
/*
                $scope.intygHeaderService = IntygHeaderService;
                $scope.intygHeaderViewState = IntygHeaderViewState;

                $scope.intygstatus1 = [];
                $scope.intygstatus2 = [];

                // Parent intyg cancelled status is needed to implement is-007
                var parentIntygStatuses;
                function checkParentIntyg() {
                    parentIntygStatuses = undefined;
                    if (CommonIntygViewState.isRevoked() && CommonIntygViewState.intygProperties.parent) {
                        IntygProxy.getIntyg(CommonIntygViewState.intygProperties.parent.intygsId,
                            IntygHeaderViewState.intygType,
                            // onSuccess
                            function(result) {
                                parentIntygStatuses = result.statuses;
                                updateIntygStatus();
                            },
                            // onError
                            function(error) {
                                CommonIntygViewState.inlineErrorMessageKey = 'common.error.intyg.status.failed.load';
                            });
                    }
                }

                $scope.$on('intyg.loaded', checkParentIntyg);
                $scope.$on('intygstatus.updated', checkParentIntyg);
                checkParentIntyg();

                function addIntygStatus1(intygStatus, timestamp, vars) {
                    $scope.intygstatus1.push({
                        code: intygStatus,
                        timestamp: timestamp,
                        text: IntygStatusService.getMessageForIntygStatus(intygStatus, vars),
                        modal: IntygStatusService.intygStatusHasModal(intygStatus)
                    });
                }

                function addIntygStatus2(intygStatus, timestamp, vars) {
                    $scope.intygstatus2.push({
                        code: intygStatus,
                        timestamp: timestamp,
                        text: IntygStatusService.getMessageForIntygStatus(intygStatus, vars),
                        modal: IntygStatusService.intygStatusHasModal(intygStatus)
                    });
                }

                $scope.$on('intyg.loaded', updateIntygStatus);
                $scope.$on('arenden.updated', updateIntygStatus);
                $scope.$on('intygstatus.updated', updateIntygStatus);
                updateIntygStatus();

                function updateIntygStatus() {
                    updateIntygStatus1();
                    updateIntygStatus2();
                }

                function updateIntygStatus1() {
                    $scope.intygstatus1 = [];

                    if (CommonIntygViewState.isComplementedByUtkast()) {
                        addIntygStatus1('is-012',
                            CommonIntygViewState.intygProperties.latestChildRelations.complementedByUtkast.skapad,
                            {
                                intygstyp: IntygHeaderViewState.intygType,
                                intygsid: CommonIntygViewState.intygProperties.latestChildRelations.complementedByUtkast.intygsId
                            });
                    }

                    if (CommonIntygViewState.isReplacedByUtkast()) {
                        addIntygStatus1('is-009',
                            CommonIntygViewState.intygProperties.latestChildRelations.replacedByUtkast.skapad,
                            {
                            intygstyp: IntygHeaderViewState.intygType,
                            intygsid: CommonIntygViewState.intygProperties.latestChildRelations.replacedByUtkast.intygsId
                        });
                    }

                    if (ArendeListViewStateService.getUnhandledKompletteringCount() > 0) {
                        angular.forEach(ArendeListViewStateService.getUnhandledKompletteringTimestamps(), function(timestamp) {
                            addIntygStatus1('is-006', timestamp);
                        });
                    }

                    if (CommonIntygViewState.isComplementedByIntyg()) {
                        addIntygStatus1('is-005',
                            CommonIntygViewState.intygProperties.latestChildRelations.complementedByIntyg.skapad,
                            {
                                intygstyp: IntygHeaderViewState.intygType,
                                intygsid: CommonIntygViewState.intygProperties.latestChildRelations.complementedByIntyg.intygsId
                            });
                    }

                    if (CommonIntygViewState.isRevoked()) {
                        addIntygStatus1('is-004', CommonIntygViewState.intygProperties.revokedTimestamp);
                    }

                    if (CommonIntygViewState.isReplaced()) {
                        addIntygStatus1('is-003',
                            CommonIntygViewState.intygProperties.latestChildRelations.replacedByIntyg.skapad,
                            {
                                intygstyp: IntygHeaderViewState.intygType,
                                intygsid: CommonIntygViewState.intygProperties.latestChildRelations.replacedByIntyg.intygsId
                            });
                    }

                    if (CommonIntygViewState.isSentIntyg()) {
                        addIntygStatus1('is-002',
                            CommonIntygViewState.intygProperties.sentTimestamp,
                            {
                                recipient: IntygHeaderViewState.recipientText
                            });
                    }

                    addIntygStatus1('is-001', CommonIntygViewState.intygProperties.signeringsdatum);

                    IntygStatusService.sortByStatusAndTimestamp($scope.intygstatus1);
                }

                function updateIntygStatus2() {
                    $scope.intygstatus2 = [];

                    if (CommonIntygViewState.isRevoked() && parentIntygStatuses && parentIntygStatuses[0].type !== 'CANCELLED') {
                        if (CommonIntygViewState.isReplacing()) {
                            addIntygStatus2('is-007',
                                CommonIntygViewState.intygProperties.revokedTimestamp,
                                {
                                    intygstyp: IntygHeaderViewState.intygType,
                                    intygsid: CommonIntygViewState.intygProperties.parent.intygsId
                                });
                        }
                        else if (CommonIntygViewState.isRenewing()) {
                            addIntygStatus2('is-010',
                                CommonIntygViewState.intygProperties.revokedTimestamp,
                                {
                                    intygstyp: IntygHeaderViewState.intygType,
                                    intygsid: CommonIntygViewState.intygProperties.parent.intygsId
                                });
                        }
                        else if (CommonIntygViewState.isComplementing()) {
                            addIntygStatus2('is-011',
                                CommonIntygViewState.intygProperties.revokedTimestamp,
                                {
                                    intygstyp: IntygHeaderViewState.intygType,
                                    intygsid: CommonIntygViewState.intygProperties.parent.intygsId
                                });
                        }
                    }

                    if(!CommonIntygViewState.isRevoked() && IntygHeaderViewState.intygType !== 'db' && IntygHeaderViewState.intygType !== 'doi') {
                        addIntygStatus2('is-008', CommonIntygViewState.intygProperties.signeringsdatum);
                    }

                    IntygStatusService.sortByStatusAndTimestamp($scope.intygstatus2);
                }

                $scope.openAllStatusesModal = function() {
                    var allStatuses = $scope.intygstatus1.concat($scope.intygstatus2);
                    if (CommonIntygViewState.intygProperties.created) {
                        allStatuses.push({
                           timestamp:CommonIntygViewState.intygProperties.created,
                           text:'Intyget är skapat'
                        });
                    }
                    IntygStatusService.sortByStatusAndTimestamp(allStatuses);
                    var allStatusesModalInstance = $uibModal.open({
                        templateUrl: '/web/webjars/common/webcert/intyg/intygHeader/ibUtredningStatus/ibUtredningHeaderStatusModal.template.html',
                        size: 'lg',
                        controller: function($scope) {
                            $scope.statuses = allStatuses;
                        }
                    }).result.then(function() {
                        allStatusesModalInstance = undefined;
                    },function() {
                        allStatusesModalInstance = undefined;
                    });
                };
*/
            }
        };
    });
