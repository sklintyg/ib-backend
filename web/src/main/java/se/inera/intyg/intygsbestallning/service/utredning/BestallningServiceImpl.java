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
import se.inera.intyg.intygsbestallning.auth.model.IbSelectableHsaEntity;
import se.inera.intyg.intygsbestallning.auth.model.IbVardenhet;
import se.inera.intyg.intygsbestallning.auth.pdl.PDLActivityStore;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Betalning;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningFas;
import se.inera.intyg.intygsbestallning.service.patient.PatientNameEnricher;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PDLLoggable;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PdlLogType;
import se.inera.intyg.intygsbestallning.persistence.model.status.Actor;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.service.util.GenericComparator;
import se.inera.intyg.intygsbestallning.service.util.PagingUtil;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FilterableListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.GetAvslutadeBestallningarListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.GetBestallningListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.SaveFakturaVeIdForUtredningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.VardgivareEnrichable;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.AvslutadBestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.AvslutadBestallningListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.BestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.BestallningListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.GetBestallningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.ListAvslutadeBestallningarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning.ListBestallningRequest;
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
@Transactional
public class BestallningServiceImpl extends BaseUtredningService implements BestallningService {

    private static final Logger LOG = LoggerFactory.getLogger(BestallningServiceImpl.class);

    @Autowired
    private PatientNameEnricher patientNameEnricher;

    @Autowired
    private LogService logService;

    @Autowired
    private BestallningListItemFactory bestallningListItemFactory;

    @Autowired
    private AvslutadBestallningListItemFactory avslutadBestallningListItemFactory;

    @Autowired
    private BusinessDaysBean businessDays;

    @Override
    public GetBestallningResponse getBestallning(Long utredningId, IbSelectableHsaEntity vardenhet) {
        Utredning utredning = utredningRepository.findById(utredningId).orElseThrow(
                () -> new IbNotFoundException("Utredning with assessmentId '" + utredningId + "' does not exist."));

        checkUserVardenhetTilldeladToBestallning(utredning);

        GetBestallningResponse getBestallningResponse = GetBestallningResponse.from(utredning,
                utredningStatusResolver.resolveStatus(utredning), businessDays);

        logService.log(getBestallningResponse, PdlLogType.UTREDNING_LAST);

        return getBestallningResponse;
    }

    @Override
    @Transactional
    public void saveFakturaVeIdForUtredning(Long utredningsId, SaveFakturaVeIdForUtredningRequest request,
                                            IbSelectableHsaEntity loggedInAtVardenhet) {
        Utredning utredning = utredningRepository.findById(utredningsId).orElseThrow(
                () -> new IbNotFoundException("Utredning with assessmentId '" + utredningsId + "' does not exist."));

        checkUserVardenhetTilldeladToBestallning(utredning);

        if (utredning.getBetalning() != null) {
            utredning.getBetalning().setFakturaVeId(request.getFakturaVeId());
        } else {
            Betalning betalning = Betalning.BetalningBuilder.aBetalning()
                    .withFakturaVeId(request.getFakturaVeId())
                    .build();
            utredning.setBetalning(betalning);
        }
        utredningRepository.saveUtredning(utredning);
    }

    @Override
    public ListBestallningFilter buildListBestallningFilter(IbVardenhet vardenhet) {
        List<SelectItem> distinctVardgivare = utredningRepository
                .findDistinctLandstingHsaIdByVardenhetHsaIdHavingBestallning(vardenhet.getId(), vardenhet.getVardgivareOrgnr())
                .stream()
                .map(vgHsaId -> new SelectItem(vgHsaId, hsaOrganizationsService.getVardgivareInfo(vgHsaId).getNamn()))
                .distinct()
                .sorted(Comparator.comparing(SelectItem::getLabel))
                .collect(Collectors.toList());

        List<ListFilterStatus> statuses = Arrays.asList(ListFilterStatus.values());

        return new ListBestallningFilter(distinctVardgivare, statuses);
    }

    @Override
    public GetAvslutadeBestallningarListResponse findAvslutadeBestallningarForVardenhet(IbVardenhet vardenhet,
                                                                                        ListAvslutadeBestallningarRequest requestFilter) {
        List<AvslutadBestallningListItem> avslutadeBestallningar = utredningRepository
                .findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradTrue(vardenhet.getId(), vardenhet.getVardgivareOrgnr())
                .stream().map(utr -> avslutadBestallningListItemFactory.from(utr))
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
                .filter(bli -> buildFaktureradPredicate(bli, requestFilter.getFakturaVeId()))
                .filter(bli -> buildBetaldPredicate(bli, requestFilter.getBetaldVeId()))

                .sorted((o1, o2) -> GenericComparator.compare(AvslutadBestallningListItem.class, o1, o2, requestFilter.getOrderBy(),
                        requestFilter.isOrderByAsc()))
                .collect(toList());

        List<AvslutadBestallningListItem> paged = filtered;
        int total = filtered.size();
        if (requestFilter.isPerformPaging()) {
            // Paging. We need to perform some bounds-checking...
            if (total == 0) {
                return new GetAvslutadeBestallningarListResponse(filtered, total);
            }

            Pair<Integer, Integer> bounds = PagingUtil.getBounds(total, requestFilter.getPageSize(), requestFilter.getCurrentPage());
            paged = filtered.subList(bounds.getFirst(), bounds.getSecond() + 1);
        }

        if (!enrichedWithVardgivareNames) {
            enrichWithVardgivareNames(paged);
        }

        return new GetAvslutadeBestallningarListResponse(paged, total);
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
                return !Strings.isNullOrEmpty(bli.getBetaldVeId());
            }
            if (filterVal == YesNoAllFilter.NO) {
                return Strings.isNullOrEmpty(bli.getBetaldVeId());
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
                return !Strings.isNullOrEmpty(bli.getFakturaVeId());
            }
            if (filterVal == YesNoAllFilter.NO) {
                return Strings.isNullOrEmpty(bli.getFakturaVeId());
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
    public ListAvslutadeBestallningarFilter buildListAvslutadeBestallningarFilter(IbVardenhet vardenhet) {
        List<SelectItem> distinctVardgivare = utredningRepository
                .findDistinctLandstingHsaIdByVardenhetHsaIdHavingBestallningAndIsArkiverad(vardenhet.getId(),
                        vardenhet.getVardgivareOrgnr())
                .stream()
                .map(vgHsaId -> new SelectItem(vgHsaId, hsaOrganizationsService.getVardgivareInfo(vgHsaId).getNamn()))
                .distinct()
                .sorted(Comparator.comparing(SelectItem::getLabel))
                .collect(Collectors.toList());
        ListAvslutadeBestallningarFilter filter = new ListAvslutadeBestallningarFilter(distinctVardgivare);

        return filter;
    }

    @Override
    public GetBestallningListResponse findOngoingBestallningarForVardenhet(IbVardenhet vardenhet, ListBestallningRequest requestFilter) {
        long start = System.currentTimeMillis();
        List<Utredning> jpaList = utredningRepository
                .findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradFalse(vardenhet.getId(), vardenhet.getVardgivareOrgnr());
        LOG.info("Loading findAllByBestallning_TilldeladVardenhetHsaId_AndArkiveradFalse took {} ms", (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        List<BestallningListItem> bestallningListItems = jpaList
                .stream()
                .filter(u -> u.getStatus().getUtredningFas() != UtredningFas.AVSLUTAD)
                .map(u -> bestallningListItemFactory.from(u, Actor.VARDADMIN))
                .collect(Collectors.toList());
        LOG.info("bestallningListItemFactory ::from took {} ms", (System.currentTimeMillis() - start));

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
        start = System.currentTimeMillis();
        List<BestallningListItem> filtered = bestallningListItems.stream()
                .filter(bli -> buildVardgivareHsaIdPredicate(bli.getVardgivareHsaId(), requestFilter.getVardgivareHsaId()))
                .filter(bli -> buildStatusPredicate(bli, requestFilter.getStatus(), statusToFilterStatus))
                .filter(bli -> buildToFromPredicateForBestallningar(bli, requestFilter.getFromDate(), requestFilter.getToDate()))
                .filter(bli -> buildFreeTextPredicate(bli, requestFilter.getFreeText()))

                .sorted((o1, o2) -> GenericComparator.compare(BestallningListItem.class, o1, o2, requestFilter.getOrderBy(),
                        requestFilter.isOrderByAsc()))
                .collect(toList());
        LOG.info("filtering of BestallningListItem took {} ms", (System.currentTimeMillis() - start));

        List<BestallningListItem> paged = filtered;
        int total = filtered.size();
        if (requestFilter.isPerformPaging()) {
            // Paging. We need to perform some bounds-checking...
            if (total == 0) {
                return new GetBestallningListResponse(filtered, total);
            }

            Pair<Integer, Integer> bounds = PagingUtil.getBounds(total, requestFilter.getPageSize(), requestFilter.getCurrentPage());
            paged = filtered.subList(bounds.getFirst(), bounds.getSecond() + 1);
        }

        // Fetch patient names and hsa names only for the selected subset, we want to minimize number of calls per invocation
        // of this API
        if (!enrichedWithPatientNames) {
            start = System.currentTimeMillis();
            patientNameEnricher.enrichWithPatientNames(paged);
            LOG.info("enrichWithPatientNames took {} ms", (System.currentTimeMillis() - start));
        }

        // Call HSA to get actual name(s) of Vardgivare.
        if (!enrichedWithVardgivareNames) {
            start = System.currentTimeMillis();
            enrichWithVardgivareNames(paged);
            LOG.info("enrichWithVardgivareNames took {} ms", (System.currentTimeMillis() - start));
        }

        // Only PDL-log what we actually are sending to the GUI
        start = System.currentTimeMillis();
        pdlLogList(paged, ActivityType.READ, ResourceType.RESOURCE_TYPE_FMU);
        LOG.info("pdlLogList took {} ms", (System.currentTimeMillis() - start));
        return new GetBestallningListResponse(paged, total);
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

        logService.logList(bestallningarToLog, PdlLogType.UTREDNING_VISAD_I_LISTA);

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
        case REDOVISA_BESOK:
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
                Vardgivare vardgivareInfo = hsaOrganizationsService.getVardgivareInfo(bli.getVardgivareHsaId());
                if (vardgivareInfo != null) {
                    bli.setVardgivareNamn(vardgivareInfo.getNamn());
                } else {
                    LOG.warn("Could not fetch name for Vardgivare '{}' from HSA", bli.getVardgivareHsaId());
                }
            }
        });
    }

}
