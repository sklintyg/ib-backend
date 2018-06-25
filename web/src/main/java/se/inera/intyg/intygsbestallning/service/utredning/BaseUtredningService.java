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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import javax.xml.ws.WebServiceException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import se.inera.intyg.infra.integration.hsa.client.OrganizationUnitService;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.Actor;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FilterableListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FreeTextSearchable;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardenhetEnrichable;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListFilterStatus;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.YesNoAllFilter;

public abstract class BaseUtredningService {
    private static final Logger LOG = LoggerFactory.getLogger(UtredningService.class);

    @Autowired
    protected UtredningRepository utredningRepository;

    @Autowired
    protected UserService userService;

    @Autowired
    protected HsaOrganizationsService hsaOrganizationsService;

    @Autowired
    protected OrganizationUnitService organizationUnitService;

    protected UtredningStatusResolver utredningStatusResolver = new UtredningStatusResolver();

    @NotNull
    protected Utredning getUtredningForLandsting(Long utredningId, String landstingHsaId, List<UtredningStatus> allowedStatuses) {
        Utredning utredning = utredningRepository.findById(utredningId).orElseThrow(
                () -> new IbNotFoundException("Could not find the assessment with id " + utredningId));

        if (!Objects.equals(utredning.getExternForfragan().map(ExternForfragan::getLandstingHsaId).orElse(null), landstingHsaId)) {
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

    protected GetUtredningResponse createGetUtredningResponse(Utredning utredning) {
        GetUtredningResponse getUtredningResponse = GetUtredningResponse.from(utredning, utredningStatusResolver.resolveStatus(utredning));

        enrichWithVardenhetNames(getUtredningResponse.getInternForfraganList());
        enrichWithVardenhetNames(getUtredningResponse.getTidigareEnheter());

        return getUtredningResponse;
    }

    protected void enrichWithVardenhetNames(List<? extends VardenhetEnrichable> items) {
        items.stream().forEach(item -> {
            if (!Strings.isNullOrEmpty(item.getVardenhetHsaId())) {
                try {
                    Vardenhet vardenhet = hsaOrganizationsService.getVardenhet(item.getVardenhetHsaId());
                    item.setVardenhetNamn(vardenhet.getNamn());
                } catch (WebServiceException e) {
                    item.setVardenhetFelmeddelande(e.getMessage());
                    LOG.warn("Could not fetch name for Vardenhet '{}' from HSA. ErrorMessage: '{}'", item.getVardenhetHsaId(),
                            e.getMessage());
                }
            }
        });
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
        if (Strings.isNullOrEmpty(compareTo) && !(Strings.isNullOrEmpty(fromDate) || Strings.isNullOrEmpty(toDate))) {
            return false;
        }
        if (Strings.isNullOrEmpty(fromDate) || Strings.isNullOrEmpty(toDate)) {
            return true;
        }
        return fromDate.compareTo(compareTo) <= 0 && toDate.compareTo(compareTo) >= 0;
    }

    protected boolean buildYesNoAllPredicate(String value, YesNoAllFilter filter) {
        if (filter == YesNoAllFilter.ALL) {
            return true;
        }
        if (filter == YesNoAllFilter.YES) {
            return !Strings.isNullOrEmpty(value);
        }
        if (filter == YesNoAllFilter.NO) {
            return Strings.isNullOrEmpty(value);
        }
        return false;
    }

    protected boolean buildYesNoAllPredicate(boolean value, YesNoAllFilter filter) {
        if (filter == YesNoAllFilter.ALL) {
            return true;
        }
        if (filter == YesNoAllFilter.YES) {
            return value;
        }
        if (filter == YesNoAllFilter.NO) {
            return !value;
        }
        return false;
    }

    private ListFilterStatus resolveListFilterStatus(UtredningStatus us, Actor actor) {
        if (us.getNextActor() == actor) {
            return ListFilterStatus.BEHOVER_ATGARDAS;
        } else {
            return ListFilterStatus.VANTAR_ANNAN_AKTOR;
        }
    }

    protected void checkUserVardenhetTilldeladToBestallning(Utredning utredning) {
        String userLoggedInAtHsaId = userService.getUser().getCurrentlyLoggedInAt().getId();
        if (!utredning.getBestallning().get().getTilldeladVardenhetHsaId().equals(userLoggedInAtHsaId)) {
            throw new IbAuthorizationException(MessageFormat.format(
                    "User is currently logged in at {0} and is not tilldelad to bestallning for utredning with id {1}",
                    userLoggedInAtHsaId, utredning.getUtredningId()));
        }
    }
}
