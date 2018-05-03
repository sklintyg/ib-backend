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
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.pdl.PDLActivityStore;
import se.inera.intyg.intygsbestallning.service.patient.PatientNameEnricher;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PDLLoggable;
import se.inera.intyg.intygsbestallning.service.stateresolver.Actor;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.util.GenericComparator;
import se.inera.intyg.intygsbestallning.service.util.PagingUtil;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.BestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FilterableListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListBestallningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListBestallningFilter;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListFilterStatus;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.SelectItem;

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
    public List<BestallningListItem> findOngoingBestallningarForVardenhet(String vardenhetHsaId, ListBestallningRequest requestFilter) {
        List<BestallningListItem> bestallningListItems = utredningRepository.findAllWithBestallningForVardenhetHsaId(vardenhetHsaId)
                .stream()
                .map(u -> BestallningListItem.from(u, utredningStateResolver.resolveStatus(u), Actor.VARDADMIN))
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
                .filter(bli -> buildVardgivareHsaIdPredicate(bli, requestFilter.getVardgivareHsaId()))
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

    private void enrichWithVardgivareNames(List<BestallningListItem> items) {
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
