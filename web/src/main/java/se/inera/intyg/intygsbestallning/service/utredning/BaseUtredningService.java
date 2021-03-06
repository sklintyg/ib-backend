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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import javax.xml.ws.WebServiceException;

import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;

import se.inera.intyg.infra.integration.hsa.client.OrganizationUnitService;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.intygsbestallning.auth.model.IbVardenhet;
import se.inera.intyg.intygsbestallning.auth.model.SelectableHsaEntityType;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.SkickadNotifiering;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.Actor;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp;
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
    @Transactional(readOnly = true)
    protected Utredning getUtredningForLandsting(Long utredningId, String landstingHsaId, List<UtredningStatus> allowedStatuses) {
        Utredning utredning = utredningRepository.findById(utredningId).orElseThrow(
                () -> new IbNotFoundException("Angivet utredningsid existerar inte"));

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

    /**
     * Lenient retrival of landstings name. If an exception occurs or no name returned, an error is logged and the given
     * hsaId is echoed back.
     *
     * @param vardgivarHsaId
     * @return
     */
    protected String getLandstingNameOrHsaId(final String vardgivarHsaId) {
        try {
            Vardgivare vardgivareInfo = hsaOrganizationsService.getVardgivareInfo(vardgivarHsaId);
            if (vardgivareInfo != null) {
                return vardgivareInfo.getNamn();
            } else {
                LOG.warn(MessageFormat.format("getVardgivareInfo for hsaid {0} returned null", vardgivarHsaId));
                return vardgivarHsaId;
            }
        } catch (Exception e) {
            LOG.error(MessageFormat.format("Exception in hsaOrganizationsService.getVardgivareInfo for hsaid {0}", vardgivarHsaId, e));
            return vardgivarHsaId;
        }
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
            return ListFilterStatus.KRAVER_ATGARD;
        } else {
            return ListFilterStatus.VANTAR_ANNAN_AKTOR;
        }
    }

    protected void checkUserVardenhetTilldeladToBestallning(Utredning utredning) {

        // Verify that the current vardenhet has a Bestallning.
        if (!utredning.getBestallning().isPresent()) {
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM,
                    "Utredning with assessmentId '" + utredning.getUtredningId() + "' does not have a Beställning.");
        }

        if (userService.getUser().getCurrentlyLoggedInAt().getType() != SelectableHsaEntityType.VE) {
            String errorMsg = MessageFormat.format(
                    "User is currently logged in at {0} of type {1} which is not VE",
                    userService.getUser().getCurrentlyLoggedInAt().getId(), userService.getUser().getCurrentlyLoggedInAt().getId());
            throw new IbAuthorizationException(IbAuthorizationErrorCodeEnum.VARDENHET_MISMATCH, errorMsg);
        }

        // UTRVE.FEL01 - Användaren är inte behörig att se utredningen utifrån vald systemroll
        IbVardenhet userLoggedInAtVardenhet = (IbVardenhet) userService.getUser().getCurrentlyLoggedInAt();
        if (!utredning.getBestallning().get().getTilldeladVardenhetHsaId().equals(userLoggedInAtVardenhet.getId())) {
            String errorMsg = MessageFormat.format(
                    "User is currently logged in at {0} and is not tilldelad to bestallning for utredning with id {1}",
                    userLoggedInAtVardenhet.getId(), utredning.getUtredningId());
            throw new IbAuthorizationException(IbAuthorizationErrorCodeEnum.VARDENHET_MISMATCH, errorMsg);
        }

        // UTRVE.FEL02 - Utredningens organisationsnummer matchar inte organisationsnumret för den vårdgivare som vårdenheten tillhör
        if (!utredning.getBestallning().get().getTilldeladVardenhetOrgNr().equals(userLoggedInAtVardenhet.getVardgivareOrgnr())) {
            String errorMsg = MessageFormat.format(
                    "Access denied to utredning {0} tilldelad orgnr {1}. User is currently logged in with orgnr {2}",
                    utredning.getUtredningId(), utredning.getBestallning().get().getTilldeladVardenhetOrgNr(),
                    userLoggedInAtVardenhet.getVardgivareOrgnr());
            throw new IbAuthorizationException(IbAuthorizationErrorCodeEnum.VARDGIVARE_ORGNR_MISMATCH, errorMsg);
        }
    }

    protected Predicate<SkickadNotifiering> isSkickadPaminnelseNotifiering(Long id) {
        return notifiering -> notifiering.getId().equals(id)
                && notifiering.getTyp() == NotifieringTyp.PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS
                && BooleanUtils.isFalse(notifiering.getErsatts());
    }

}
