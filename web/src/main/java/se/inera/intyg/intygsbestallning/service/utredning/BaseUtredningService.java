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
package se.inera.intyg.intygsbestallning.service.utredning;

import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.infra.integration.hsa.client.OrganizationUnitService;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.stateresolver.Actor;
import se.inera.intyg.intygsbestallning.service.stateresolver.InternForfraganStateResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FilterableListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FreeTextSearchable;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListFilterStatus;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class BaseUtredningService {

    @Autowired
    protected UtredningRepository utredningRepository;

    @Autowired
    protected UserService userService;

    @Autowired
    protected HsaOrganizationsService hsaOrganizationsService;

    @Autowired
    protected OrganizationUnitService organizationUnitService;

    protected UtredningStatusResolver utredningStatusResolver = new UtredningStatusResolver();
    protected InternForfraganStateResolver internForfraganStateResolver = new InternForfraganStateResolver();

    @NotNull
    protected Utredning getUtredningForLandsting(Long utredningId, String landstingHsaId, List<UtredningStatus> allowedStatuses) {
        Utredning utredning = utredningRepository.findById(utredningId).orElseThrow(
                () -> new IbNotFoundException("Could not find the assessment with id " + utredningId));

        if (!Objects.equals(utredning.getExternForfragan().getLandstingHsaId(), landstingHsaId)) {
            throw new IbAuthorizationException(
                    "Utredning with assessmentId '" + utredningId + "' does not have ExternForfragan for landsting with id '"
                            + landstingHsaId + "'");
        }

        UtredningStatus utredningStatus = utredningStatusResolver.resolveStatus(utredning);
        if  (allowedStatuses.stream().noneMatch(utredningStatus::equals)) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, MessageFormat.format(
                    "Assessment with id {0} is in an incorrect state", utredning.getUtredningId()));
        }

        return utredning;
    }


    protected Map<UtredningStatus, ListFilterStatus> buildStatusToListBestallningFilterStatusMap(Actor actor) {
        Map<UtredningStatus, ListFilterStatus> statusMap = new HashMap<>();
        for (UtredningStatus us : UtredningStatus.values()) {
            statusMap.put(us, resolveListFilterStatus(us, actor));
        }
        return statusMap;
    }

    protected boolean buildFreeTextPredicate(FreeTextSearchable bli, String freeText) {
        if (Strings.isNullOrEmpty(freeText)) {
            return true;
        }
        return bli.toSearchString().toLowerCase().contains(freeText.toLowerCase());
    }

    protected boolean buildStatusPredicate(FilterableListItem bli, String status,
                                         Map<UtredningStatus, ListFilterStatus> statusToFilterStatus) {

        if (Strings.isNullOrEmpty(status)) {
            return true;
        }
        try {
            ListFilterStatus actualStatus = ListFilterStatus.valueOf(status);
            if (actualStatus == ListFilterStatus.ALL) {
                return true;
            }

            // Returns true if the items status maps to the grouping from the actual status.
            return statusToFilterStatus.get(bli.getStatus()) == actualStatus;

        } catch (IllegalArgumentException e) {
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, "Unknown status: '" + status + "'");
        }
    }

    protected boolean buildVardgivareHsaIdPredicate(String thisVardgivareHsaId, String matchAgainstHsaId) {
        if (Strings.isNullOrEmpty(matchAgainstHsaId)) {
            return true;
        }
        return matchAgainstHsaId.equalsIgnoreCase(thisVardgivareHsaId);
    }

    protected boolean buildToFromPredicate(String compareTo, String fromDate, String toDate) {
        if (Strings.isNullOrEmpty(compareTo) || Strings.isNullOrEmpty(fromDate) || Strings.isNullOrEmpty(toDate)) {
            return true;
        }
        return fromDate.compareTo(compareTo) <= 0 && toDate.compareTo(compareTo) >= 0;
    }

    private ListFilterStatus resolveListFilterStatus(UtredningStatus us, Actor actor) {
        if (us.getNextActor() == actor) {
            return ListFilterStatus.KRAVER_ATGARD;
        } else {
            return ListFilterStatus.VANTAR_ANNAN_AKTOR;
        }
    }
}
