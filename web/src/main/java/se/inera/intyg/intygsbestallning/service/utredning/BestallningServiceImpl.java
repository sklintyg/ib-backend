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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.pdl.PDLActivityStore;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Betalning;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.patient.PatientNameEnricher;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PDLLoggable;
import se.inera.intyg.intygsbestallning.service.stateresolver.Actor;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.util.GenericComparator;
import se.inera.intyg.intygsbestallning.service.util.PagingUtil;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.AvslutadBestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.BestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FilterableListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetBestallningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListAvslutadeBestallningarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListBestallningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.SaveFakturaForUtredningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardgivareEnrichable;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListAvslutadeBestallningarFilter;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListBestallningFilter;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListFilterStatus;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.SelectItem;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.YesNoAllFilter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class BestallningServiceImpl extends BaseUtredningService implements BestallningService {

    @Autowired
    private PatientNameEnricher patientNameEnricher;

    @Autowired
    private LogService logService;

    private static final Logger LOG = LoggerFactory.getLogger(BestallningServiceImpl.class);

    @Override
    public GetBestallningResponse getBestallning(String utredningId, String vardenhetHsaId) {
        Utredning utredning = utredningRepository.findById(utredningId).orElseThrow(
                () -> new IbNotFoundException("Utredning with assessmentId '" + utredningId + "' does not exist."));

        if (!utredning.getExternForfragan().getInternForfraganList().stream()
                .anyMatch(internForfragan -> internForfragan.getVardenhetHsaId().equals(vardenhetHsaId)
                        && internForfragan.getTilldeladDatum() != null)) {
            throw new IbAuthorizationException(
                    "Utredning with assessmentId '" + utredningId + "' has not been tilldelad to vardgivare with id '"
                            + vardenhetHsaId + "'");
        }

        return GetBestallningResponse.from(utredning, utredningStatusResolver.resolveStatus(utredning));
    }

    @Override
    @Transactional
    public void saveFakturaIdForUtredning(String utredningsId, SaveFakturaForUtredningRequest request, String loggedInAtVardenhetHsaId) {
        Utredning utredning = utredningRepository.findById(utredningsId).orElseThrow(
                () -> new IbNotFoundException("Utredning with assessmentId '" + utredningsId + "' does not exist."));

        // Verify that the current vardenhet has a Bestallning.
        if (!utredning.getBestallning().isPresent()) {
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM,
                    "Utredning with assessmentId '" + utredningsId + "' does not have a Beställning.");
        }


        if (!utredning.getBestallning().get().getTilldeladVardenhetHsaId().equals(loggedInAtVardenhetHsaId)) {
            throw new IbAuthorizationException("The current user cannot mark Utredning with assessmentId '" + utredningsId
                    + "' as fakturerad, the Beställning is for another vardenhet");
        }

        if (utredning.getBetalning() != null) {
            utredning.getBetalning().setFakturaId(request.getFakturaId());
        } else {
            Betalning betalning = Betalning.BetalningBuilder.aBetalning()
                    .withFakturaId(request.getFakturaId())
                    .build();
            utredning.setBetalning(betalning);
        }
        utredningRepository.save(utredning);
    }

    @Override
    public ListBestallningFilter buildListBestallningFilter(String vardenhetHsaId) {
        List<SelectItem> distinctVardgivare = utredningRepository
                .findDistinctLandstingHsaIdByVardenhetHsaIdHavingBestallning(vardenhetHsaId)
                .stream()
                .map(vgHsaId -> new SelectItem(vgHsaId, organizationUnitService.getVardgivareInfo(vgHsaId).getNamn()))
                .distinct()
                .sorted(Comparator.comparing(SelectItem::getLabel))
                .collect(Collectors.toList());

        List<ListFilterStatus> statuses = Arrays.asList(ListFilterStatus.values());

        return new ListBestallningFilter(distinctVardgivare, statuses);
    }

    @Override
    public List<AvslutadBestallningListItem> findAvslutadeBestallningarForVardenhet(String vardenhetHsaId,
            ListAvslutadeBestallningarRequest requestFilter) {
        List<AvslutadBestallningListItem> avslutadeBestallningar = utredningRepository
                .findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradTrue(vardenhetHsaId)
                .stream().map(utr -> AvslutadBestallningListItem.from(utr, utredningStatusResolver))
                .collect(Collectors.toList());

        boolean enrichedWithVardgivareNames = false;
        if (!Strings.isNullOrEmpty(requestFilter.getFreeText()) || requestFilter.getOrderBy().equals("vardgivareNamn")) {
            enrichWithVardgivareNames(avslutadeBestallningar);
            enrichedWithVardgivareNames = true;
        }

        List<AvslutadBestallningListItem> filtered = avslutadeBestallningar.stream()
                .filter(bli -> buildVardgivareHsaIdPredicate(bli.getVardgivareHsaId(), requestFilter.getVardgivareHsaId()))
                .filter(bli -> buildToFromPredicate(bli.getAvslutsDatum(), requestFilter.getAvslutsDatumFromDate(),
                        requestFilter.getAvslutsDatumToDate()))
                .filter(bli -> buildFreeTextPredicate(bli, requestFilter.getFreeText()))
                .filter(bli -> buildErsattsPredicate(bli, requestFilter.getErsatts()))
                .filter(bli -> buildFaktureradPredicate(bli, requestFilter.getFakturerad()))
                .filter(bli -> buildBetaldPredicate(bli, requestFilter.getUtbetald()))

                .sorted((o1, o2) -> GenericComparator.compare(AvslutadBestallningListItem.class, o1, o2, requestFilter.getOrderBy(),
                        requestFilter.isOrderByAsc()))
                .collect(toList());

        // Paging. We need to perform some bounds-checking...
        int total = filtered.size();
        if (total == 0) {
            return filtered;
        }

        Pair<Integer, Integer> bounds = PagingUtil.getBounds(total, requestFilter.getPageSize(), requestFilter.getCurrentPage());
        List<AvslutadBestallningListItem> paged = filtered.subList(bounds.getFirst(), bounds.getSecond() + 1);

        if (!enrichedWithVardgivareNames) {
            enrichWithVardgivareNames(paged);
        }

        return paged;
    }

    private boolean buildBetaldPredicate(AvslutadBestallningListItem bli, String enumValue) {
        if (Strings.isNullOrEmpty(enumValue)) {
            return true;
        }
        try {
            YesNoAllFilter filterVal = YesNoAllFilter.valueOf(enumValue);
            if (filterVal == YesNoAllFilter.ALL) {
                return true;
            }
            if (filterVal == YesNoAllFilter.YES) {
                return !Strings.isNullOrEmpty(bli.getUtbetald());
            }
            if (filterVal == YesNoAllFilter.NO) {
                return Strings.isNullOrEmpty(bli.getUtbetald());
            }
        } catch (IllegalArgumentException e) {
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, "Unknown enum value for YesNoAll: '" + enumValue + "'");
        }
        return false;
    }

    private boolean buildFaktureradPredicate(AvslutadBestallningListItem bli, String enumValue) {
        if (Strings.isNullOrEmpty(enumValue)) {
            return true;
        }
        try {
            YesNoAllFilter filterVal = YesNoAllFilter.valueOf(enumValue);
            if (filterVal == YesNoAllFilter.ALL) {
                return true;
            }
            if (filterVal == YesNoAllFilter.YES) {
                return !Strings.isNullOrEmpty(bli.getFakturerad());
            }
            if (filterVal == YesNoAllFilter.NO) {
                return Strings.isNullOrEmpty(bli.getFakturerad());
            }
        } catch (IllegalArgumentException e) {
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, "Unknown enum value for YesNoAll: '" + enumValue + "'");
        }
        return false;
    }

    private boolean buildErsattsPredicate(AvslutadBestallningListItem bli, String enumValue) {
        if (Strings.isNullOrEmpty(enumValue)) {
            return true;
        }
        try {
            YesNoAllFilter filterVal = YesNoAllFilter.valueOf(enumValue);

            if (filterVal == YesNoAllFilter.ALL) {
                return true;
            }
            if (filterVal == YesNoAllFilter.YES) {
                return bli.getErsatts();
            }
            if (filterVal == YesNoAllFilter.NO) {
                return !bli.getErsatts();
            }
        } catch (IllegalArgumentException e) {
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, "Unknown enum value for YesNoAll: '" + enumValue + "'");
        }
        return false;
    }

    @Override
    public ListAvslutadeBestallningarFilter buildListAvslutadeBestallningarFilter(String vardenhetHsaId) {
        List<SelectItem> distinctVardgivare = utredningRepository
                .findDistinctLandstingHsaIdByVardenhetHsaIdHavingBestallningAndIsArkiverad(vardenhetHsaId)
                .stream()
                .map(vgHsaId -> new SelectItem(vgHsaId, organizationUnitService.getVardgivareInfo(vgHsaId).getNamn()))
                .distinct()
                .sorted(Comparator.comparing(SelectItem::getLabel))
                .collect(Collectors.toList());
        ListAvslutadeBestallningarFilter filter = new ListAvslutadeBestallningarFilter(distinctVardgivare);

        return filter;
    }

    @Override
    public List<BestallningListItem> findOngoingBestallningarForVardenhet(String vardenhetHsaId, ListBestallningRequest requestFilter) {
        List<BestallningListItem> bestallningListItems = utredningRepository
                .findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradFalse(vardenhetHsaId)
                .stream()
                .map(u -> BestallningListItem.from(u, utredningStatusResolver.resolveStatus(u), Actor.VARDADMIN))
                .collect(Collectors.toList());

        // We may need to fetch names for patients and vardgivare prior to filtering if free text search is
        // used or if either of those two columns are sorted.
        boolean enrichedWithPatientNames = false;
        boolean enrichedWithVardgivareNames = false;
        if (!Strings.isNullOrEmpty(requestFilter.getFreeText()) || requestFilter.getOrderBy().equals("vardgivareNamn")
                || requestFilter.getOrderBy().equals("patientNamn")) {
            enrichWithVardgivareNames(bestallningListItems);
            patientNameEnricher.enrichWithPatientNames(bestallningListItems);
            enrichedWithPatientNames = true;
            enrichedWithVardgivareNames = true;
        }

        // Get status mapper
        Map<UtredningStatus, ListFilterStatus> statusToFilterStatus = buildStatusToListBestallningFilterStatusMap(Actor.VARDADMIN);

        // Start actual filtering. Order is important here. We must always filter out unwanted items _before_ sorting and
        // then finally paging.
        List<BestallningListItem> filtered = bestallningListItems.stream()
                .filter(bli -> buildVardgivareHsaIdPredicate(bli.getVardgivareHsaId(), requestFilter.getVardgivareHsaId()))
                .filter(bli -> buildStatusPredicate(bli, requestFilter.getStatus(), statusToFilterStatus))
                .filter(bli -> buildToFromPredicateForBestallningar(bli, requestFilter.getFromDate(), requestFilter.getToDate()))
                .filter(bli -> buildFreeTextPredicate(bli, requestFilter.getFreeText()))

                .sorted((o1, o2) -> GenericComparator.compare(BestallningListItem.class, o1, o2, requestFilter.getOrderBy(),
                        requestFilter.isOrderByAsc()))
                .collect(toList());

        // Paging. We need to perform some bounds-checking...
        int total = filtered.size();
        if (total == 0) {
            return filtered;
        }

        Pair<Integer, Integer> bounds = PagingUtil.getBounds(total, requestFilter.getPageSize(), requestFilter.getCurrentPage());
        List<BestallningListItem> paged = filtered.subList(bounds.getFirst(), bounds.getSecond() + 1);

        // Fetch patient names and hsa names only for the selected subset, we want to minimize number of calls per invocation
        // of this API
        if (!enrichedWithPatientNames) {
            patientNameEnricher.enrichWithPatientNames(paged);
        }

        // Call HSA to get actual name(s) of Vardgivare.
        if (!enrichedWithVardgivareNames) {
            enrichWithVardgivareNames(paged);
        }

        // Only PDL-log what we actually are sending to the GUI
        pdlLogList(paged, ActivityType.READ, ResourceType.RESOURCE_TYPE_FMU);
        return paged;
    }

    // PDL logging. Important to only log after filtering and paging.
    private void pdlLogList(List<? extends PDLLoggable> loggableItems, ActivityType activityType, ResourceType resourceType) {
        if (loggableItems == null || loggableItems.size() == 0) {
            return;
        }

        IbUser user = userService.getUser();

        List<? extends PDLLoggable> bestallningarToLog = PDLActivityStore.getActivitiesNotInStore(user.getCurrentlyLoggedInAt().getId(),
                loggableItems, activityType, resourceType,
                user.getStoredActivities());

        logService.logVisaBestallningarLista(bestallningarToLog, activityType, resourceType);

        PDLActivityStore.addActivitiesToStore(user.getCurrentlyLoggedInAt().getId(), bestallningarToLog, activityType,
                resourceType, user.getStoredActivities());
    }

    /*
     * Utredningar vars slutdatum (intyg.sista datum för mottagning) ligger inom den valda datumperioden visas i tabellen.
     * För utredningar i fas Beställning matchas den valda tidsperioden mot Utredning.intyg.sista datum för mottagning
     * För utredningar i fas Komplettering machar den valda tidsperioden mot slutdatum för
     * Utredning.kompletteringbegäran.komplettering.sista datum för mottagning
     * Utredningar i fas Redovisa tolk inkluderas inte i söktresultatet om en datumperiod är angiven.
     */
    private boolean buildToFromPredicateForBestallningar(FilterableListItem bli, String fromDate, String toDate) {
        if (Strings.isNullOrEmpty(fromDate) || Strings.isNullOrEmpty(toDate)) {
            return true;
        }
        switch (bli.getStatus().getUtredningFas()) {
        case REDOVISA_TOLK:
            return Strings.isNullOrEmpty(fromDate);
        case UTREDNING:
        case KOMPLETTERING:
            return fromDate.compareTo(bli.getSlutdatumFas()) <= 0 && toDate.compareTo(bli.getSlutdatumFas()) >= 0;
        case AVSLUTAD:
            return true;
        case FORFRAGAN:
            return false;
        }
        return true;
    }

    private void enrichWithVardgivareNames(List<? extends VardgivareEnrichable> items) {
        items.stream().forEach(bli -> {
            if (!Strings.isNullOrEmpty(bli.getVardgivareHsaId())) {
                Vardgivare vardgivareInfo = organizationUnitService.getVardgivareInfo(bli.getVardgivareHsaId());
                if (vardgivareInfo != null) {
                    bli.setVardgivareNamn(vardgivareInfo.getNamn());
                } else {
                    LOG.warn("Could not fetch name for Vardgivare '{}' from HSA", bli.getVardgivareHsaId());
                }
            }
        });
    }

}
