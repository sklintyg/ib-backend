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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.pdl.PDLActivityStore;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Handlaggare;
import se.inera.intyg.intygsbestallning.persistence.model.HandlingUrsprungTyp;
import se.inera.intyg.intygsbestallning.persistence.model.Invanare;
import se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
import se.inera.intyg.intygsbestallning.service.patient.PatientNameEnricher;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PDLLoggable;
import se.inera.intyg.intygsbestallning.service.stateresolver.Actor;
import se.inera.intyg.intygsbestallning.service.stateresolver.SortableLabel;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningFas;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStateResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.util.PagingUtil;
import se.inera.intyg.intygsbestallning.service.utredning.dto.AssessmentRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.Bestallare;
import se.inera.intyg.intygsbestallning.service.utredning.dto.EndUtredningRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.UpdateOrderRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.BestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FilterableListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FreeTextSearchable;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetForfraganResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListBestallningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListUtredningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.UtredningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListBestallningFilter;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListFilterStatus;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.SelectItem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static se.inera.intyg.intygsbestallning.persistence.model.Bestallning.BestallningBuilder.aBestallning;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Handlaggare.HandlaggareBuilder.aHandlaggare;
import static se.inera.intyg.intygsbestallning.persistence.model.Handling.HandlingBuilder.aHandling;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare.TidigareUtforareBuilder.aTidigareUtforare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;

@Service
@Transactional
public class UtredningServiceImpl implements UtredningService {

    private static final Logger LOG = LoggerFactory.getLogger(UtredningService.class);

    @Autowired
    private UtredningRepository utredningRepository;

    @Autowired
    private UtredningStateResolver utredningStateResolver;

    @Autowired
    private PatientNameEnricher patientNameEnricher;

    @Autowired
    private LogService logService;

    @Autowired
    private UserService userService;

    @Autowired
    private HsaOrganizationsService organizationUnitService;

    @Override
    public List<UtredningListItem> findExternForfraganByLandstingHsaId(String landstingHsaId) {
        return utredningRepository.findAllByExternForfragan_LandstingHsaId(landstingHsaId)
                .stream()
                .map(u -> UtredningListItem.from(u, utredningStateResolver.resolveStatus(u)))
                .collect(Collectors.toList());
    }

    @Override
    public GetUtredningListResponse findExternForfraganByLandstingHsaIdWithFilter(String landstingHsaId, ListUtredningRequest request) {
        List<UtredningListItem> list = utredningRepository.findByExternForfragan_LandstingHsaId_AndArkiveradFalse(landstingHsaId)
                .stream()
                .map(u -> UtredningListItem.from(u, utredningStateResolver.resolveStatus(u)))
                .collect(Collectors.toList());

        // Get status mapper
        Map<UtredningStatus, ListFilterStatus> statusToFilterStatus = buildStatusToListBestallningFilterStatusMap();

        // If filtering by freeText or ordering by vardenhetNamn we need all vardenhetNames
        boolean enrichedWithVardenhetNames = false;
        if (request.getFreeText() != null || request.getOrderBy().equals("vardenhetNamn")) {
            // Enrich with vardenhet namn from HSA
            enrichWithVardenhetNames(list);
            enrichedWithVardenhetNames = true;
        }

        // Start actual filtering. Order is important here. We must always filter out unwanted items _before_ sorting and
        // then finally paging.
        List<UtredningListItem> filtered = list.stream()
                .filter(uli -> buildFasPredicate(uli, request.getFas()))
                .filter(uli -> buildStatusPredicate(uli, request.getStatus(), statusToFilterStatus))
                .filter(uli -> buildToFromPredicateForUtredningar(uli, request.getFromDate(), request.getToDate()))
                .filter(uli -> buildFreeTextPredicate(uli, request.getFreeText()))

                .sorted((o1, o2) -> buildComparator(UtredningListItem.class, o1, o2, request.getOrderBy(), request.isOrderByAsc()))
                .collect(toList());

        // Paging. We need to perform some bounds-checking...
        int total = filtered.size();
        if (total == 0) {
            return new GetUtredningListResponse(filtered, total);
        }

        Pair<Integer, Integer> bounds = PagingUtil.getBounds(total, request.getPageSize(), request.getCurrentPage());
        List<UtredningListItem> paged = filtered.subList(bounds.getFirst(), bounds.getSecond() + 1);

        if (!enrichedWithVardenhetNames) {
            // Enrich with vardenhet namn from HSA
            enrichWithVardenhetNames(paged);
        }

        return new GetUtredningListResponse(paged, total);
    }

    private Predicate<UtredningListItem> statusIsEligibleForListaUtredningar() {
        return uli -> uli.getStatus() != UtredningStatus.AVBRUTEN && uli.getStatus() != UtredningStatus.AVSLUTAD
                && uli.getStatus() != UtredningStatus.AVVISAD;
    }

    private boolean buildFasPredicate(UtredningListItem uli, String fas) {
        if (Strings.isNullOrEmpty(fas)) {
            return true;
        }
        UtredningFas utredningFas = UtredningFas.valueOf(fas);
        return uli.getFas() == utredningFas;
    }

    @Override
    public GetUtredningResponse getExternForfragan(String utredningId, String landstingHsaId) {
        Utredning utredning = utredningRepository.findById(utredningId).orElseThrow(
                () -> new IbNotFoundException("Utredning with assessmentId '" + utredningId + "' does not exist."));

        if (!Objects.equals(utredning.getExternForfragan().getLandstingHsaId(), landstingHsaId)) {
            throw new IbAuthorizationException(
                    "Utredning with assessmentId '" + utredningId + "' does not have ExternForfragan for landsting with id '"
                            + landstingHsaId + "'");
        }

        return GetUtredningResponse.from(utredning);
    }

    @Override
    public GetUtredningResponse getUtredning(String utredningId, String landstingHsaId) {
        Utredning utredning = utredningRepository.findById(utredningId).orElseThrow(
                () -> new IbNotFoundException("Utredning with assessmentId '" + utredningId + "' does not exist."));

        if (!Objects.equals(utredning.getExternForfragan().getLandstingHsaId(), landstingHsaId)) {
            throw new IbNotFoundException(
                    "Utredning with assessmentId '" + utredningId + "' does not have ExternForfragan for landsting with id '"
                            + landstingHsaId + "'");
        }

        return GetUtredningResponse.from(utredning);
    }

    @Override
    public List<ForfraganListItem> findForfragningarForVardenhetHsaId(String vardenhetHsaId) {
        return utredningRepository.findAllByExternForfragan_InternForfraganList_VardenhetHsaId(vardenhetHsaId)
                .stream()
                .map(utr -> ForfraganListItem.from(utr, vardenhetHsaId))
                .collect(toList());
    }

    @Override
    public GetForfraganResponse getForfragan(String utredningId, String vardenhetHsaId) {
        Utredning utredning = utredningRepository.findById(utredningId).orElseThrow(
                () -> new IbNotFoundException("Utredning with assessmentId '" + utredningId + "' does not exist."));

        boolean internForfraganExists = utredning.getExternForfragan().getInternForfraganList()
                .stream()
                .anyMatch(internForfragan -> Objects.equals(internForfragan.getVardenhetHsaId(), vardenhetHsaId));

        if (!internForfraganExists) {
            throw new IbNotFoundException(
                    "Utredning with id '" + utredningId + "' does not have an InternForfragan for enhet with id '" + vardenhetHsaId + "'");
        }

        return GetForfraganResponse.from(utredning, vardenhetHsaId);
    }

    @Override
    public Utredning registerOrder(OrderRequest order) {
        Utredning utredning = utredningRepository.findById(order.getUtredningId()).orElseThrow(
                () -> new IbNotFoundException("Could not find the assessment with id " + order.getUtredningId()));

        // Validate the state
        if (utredning.getBestallning().isPresent()) {
            LOG.warn("Assessment '{}' already have a bestallning", utredning.getUtredningId());
            throw new IllegalArgumentException(
                    "Cannot create a order when one already exists for assessmentId " + order.getUtredningId());
        }

        if (utredning.getExternForfragan() == null) {
            final String message = "Utredning with assessmentId '" + utredning.getUtredningId() + "' does not have an Förfrågan";
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }

        LOG.info("Saving new order for request '{}' with type '{}'", utredning.getUtredningId(), utredning.getUtredningsTyp());

        // Update old information (last wins!)
        utredning.setTolkBehov(order.isTolkBehov());
        utredning.setTolkSprak(order.getTolkSprak());
        if (order.getUtredningsTyp() != utredning.getUtredningsTyp()) {
            LOG.warn("Different utredningstyp for bestallning and externForfragan for assessment '{}', old type was '{}' and new is '{}'",
                    utredning.getUtredningId(), utredning.getUtredningsTyp(), order.getUtredningsTyp());
            utredning.setUtredningsTyp(order.getUtredningsTyp());
        }
        utredning.setHandlaggare(createHandlaggare(order.getBestallare()));

        // Inserts new information from order
        utredning.setBestallning(createBestallning(order));
        updateInvanareFromOrder(utredning.getInvanare(), order);

        if (order.isHandling()) {
            utredning.getHandlingList().add(
                    aHandling()
                            .withSkickatDatum(LocalDate.now().atStartOfDay())
                            .withUrsprung(HandlingUrsprungTyp.BESTALLNING)
                            .build());
        }
        utredning.getHandelseList().add(HandelseUtil.createOrderReceived(order.getBestallare().getMyndighet(), order.getOrderDate()));
        utredningRepository.save(utredning);
        return utredning;
    }

    @Override
    public Utredning updateOrder(final UpdateOrderRequest update) {

        final Utredning utredning = utredningRepository.findById(update.getUtredningId())
                .orElseThrow(() -> new IbServiceException(
                        IbErrorCodeEnum.NOT_FOUND, MessageFormat.format("Assessment with id: {} was not found", update.getUtredningId())));

        if (!utredning.getBestallning().isPresent()) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, "Assessment does not have a Bestallning");
        }

        final Utredning updatedUtredning = qualifyForUpdatering(update, utredning);
        return utredningRepository.save(updatedUtredning);
    }

    private Utredning qualifyForUpdatering(final UpdateOrderRequest update, final Utredning original) {

        Preconditions.checkArgument(!isNull(update));
        Preconditions.checkArgument(!isNull(original));
        Preconditions.checkArgument(original.getBestallning().isPresent());

        Utredning toUpdate = Utredning.from(original);

        update.getLastDateIntyg().ifPresent(date -> toUpdate.getBestallning().get().setIntygKlartSenast(date));
        update.getTolkBehov().ifPresent(toUpdate::setTolkBehov);
        update.getTolkSprak().ifPresent(toUpdate::setTolkSprak);
        update.getBestallare().ifPresent(bestallare -> toUpdate.setHandlaggare(aHandlaggare()
                .withEmail(bestallare.getEmail())
                .withFullstandigtNamn(bestallare.getFullstandigtNamn())
                .withKontor(bestallare.getKontor())
                .withKostnadsstalle(bestallare.getKostnadsstalle())
                .withMyndighet(bestallare.getMyndighet())
                .withPostkod(bestallare.getPostkod())
                .withStad(bestallare.getStad())
                .withTelefonnummer(bestallare.getTelefonnummer())
                .build()));

        if (Objects.equals(original, toUpdate)) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, "No info to update");
        }

        update.getKommentar().ifPresent(kommentar -> toUpdate.getBestallning().get().setKommentar(kommentar));

        //update.getHandling().ifPresent(isHandling -> toUpdate.getHandelseList());

        return toUpdate;
    }

    @Override
    public Utredning registerNewUtredning(OrderRequest order) {
        Utredning utredning = anUtredning()
                .withUtredningId(UUID.randomUUID().toString())
                .withUtredningsTyp(order.getUtredningsTyp())
                .withInvanare(updateInvanareFromOrder(new Invanare(), order))
                .withTolkBehov(order.isTolkBehov())
                .withTolkSprak(order.getTolkSprak())
                .withBestallning(createBestallning(order))
                .withHandlaggare(createHandlaggare(order.getBestallare()))
                .withHandelseList(Arrays.asList(HandelseUtil.createOrderReceived(order.getBestallare().getMyndighet(), null)))
                .build();

        if (order.isHandling()) {
            utredning.getHandlingList().add(
                    aHandling()
                            .withSkickatDatum(LocalDate.now().atStartOfDay())
                            .withUrsprung(HandlingUrsprungTyp.BESTALLNING)
                            .build());
        }
        utredningRepository.save(utredning);
        return utredning;
    }

    @Override
    public Utredning registerNewUtredning(final AssessmentRequest request) {

        final ExternForfragan externForfragan = anExternForfragan()
                .withInkomDatum(LocalDateTime.now())
                .withBesvarasSenastDatum(request.getBesvaraSenastDatum())
                .withKommentar(request.getKommentar())
                .withLandstingHsaId(request.getLandstingHsaId())
                .build();

        final List<TidigareUtforare> tidigareUtforareList = request.getInvanareTidigareUtforare()
                .stream()
                .map(u -> aTidigareUtforare()
                        .withTidigareEnhetId(u)
                        .build())
                .collect(toList());

        final Invanare invanare = anInvanare()
                .withPostkod(request.getInvanarePostkod())
                .withSarskildaBehov(request.getInvanareSarskildaBehov())
                .withTidigareUtforare(tidigareUtforareList)
                .build();

        return utredningRepository.save(anUtredning()
                .withUtredningId(UUID.randomUUID().toString())
                .withUtredningsTyp(request.getUtredningsTyp())
                .withExternForfragan(externForfragan)
                .withInvanare(invanare)
                .withHandlaggare(createHandlaggare(request.getBestallare()))
                .withTolkBehov(request.isTolkBehov())
                .withTolkSprak(request.getTolkSprak())
                .build());
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
        Map<UtredningStatus, ListFilterStatus> statusToFilterStatus = buildStatusToListBestallningFilterStatusMap();

        // Start actual filtering. Order is important here. We must always filter out unwanted items _before_ sorting and
        // then finally paging.
        List<BestallningListItem> filtered = bestallningListItems.stream()
                .filter(bli -> buildVardgivareHsaIdPredicate(bli, requestFilter.getVardgivareHsaId()))
                .filter(bli -> buildStatusPredicate(bli, requestFilter.getStatus(), statusToFilterStatus))
                .filter(bli -> buildToFromPredicateForBestallningar(bli, requestFilter.getFromDate(), requestFilter.getToDate()))
                .filter(bli -> buildFreeTextPredicate(bli, requestFilter.getFreeText()))

                .sorted((o1, o2) -> buildComparator(BestallningListItem.class, o1, o2, requestFilter.getOrderBy(),
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

    private void enrichWithVardenhetNames(List<UtredningListItem> items) {
        items.stream().forEach(uli -> {
            if (!Strings.isNullOrEmpty(uli.getVardenhetHsaId())) {
                Vardenhet vardenhet = organizationUnitService.getVardenhet(uli.getVardenhetHsaId());
                if (vardenhet != null) {
                    uli.setVardenhetNamn(vardenhet.getNamn());
                } else {
                    LOG.warn("Could not fetch name for Vardenhet '{}' from HSA", uli.getVardenhetHsaId());
                }
            }
        });
    }

    private boolean buildFreeTextPredicate(FreeTextSearchable bli, String freeText) {
        if (Strings.isNullOrEmpty(freeText)) {
            return true;
        }
        return bli.toSearchString().toLowerCase().contains(freeText.toLowerCase());
    }

    private boolean buildToFromPredicateForUtredningar(FilterableListItem bli, String fromDate, String toDate) {
        if (Strings.isNullOrEmpty(fromDate) || Strings.isNullOrEmpty(toDate)) {
            return true;
        }

        switch (bli.getStatus().getUtredningFas()) {
            case AVSLUTAD:
                return false;
            case REDOVISA_TOLK:
                return Strings.isNullOrEmpty(fromDate);
            case UTREDNING:
            case KOMPLETTERING:
            case FORFRAGAN:
                return fromDate.compareTo(bli.getSlutdatumFas()) <= 0 && toDate.compareTo(bli.getSlutdatumFas()) >= 0;
        }
        return true;
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

    private int buildComparator(Class clazz, Object o1, Object o2, String orderBy, boolean orderByAsc) {
        if (Strings.isNullOrEmpty(orderBy)) {
            return 0;
        }

        try {
            // Reflection...
            Method m = clazz.getDeclaredMethod("get" + camelCase(orderBy), null);

            Object o1Value = m.invoke(o1);
            Object o2Value = m.invoke(o2);

            if (SortableLabel.class.isAssignableFrom(m.getReturnType())) {
                o1Value = ((SortableLabel) o1Value).getLabel();
                o2Value = ((SortableLabel) o2Value).getLabel();
            }

            if (orderByAsc) {

                if (o1Value instanceof Number) {
                    Number n1 = (Number) o1Value;
                    Number n2 = (Number) o2Value;
                    Integer i1 = n1.intValue();
                    Integer i2 = n2.intValue();
                    return i1.compareTo(i2);
                }
                if (o1Value instanceof String) {
                    return ((String) o1Value).compareToIgnoreCase((String) o2Value);
                }
                if (o1Value instanceof Boolean) {
                    return Boolean.compare((Boolean) o1Value, (Boolean) o2Value);
                }
            } else {
                if (o1Value instanceof Number) {
                    Number n1 = (Number) o1Value;
                    Number n2 = (Number) o2Value;
                    Integer i1 = n1.intValue();
                    Integer i2 = n2.intValue();
                    return i2.compareTo(i1);
                }
                if (o1Value instanceof String) {
                    return ((String) o2Value).compareToIgnoreCase((String) o1Value);
                }
                if (o1Value instanceof Boolean) {
                    return Boolean.compare((Boolean) o2Value, (Boolean) o1Value);
                }
            }
        } catch (NoSuchMethodException e) {
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM, "Unknown column to order by: '" + orderBy + "'");
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM,
                    "Unable to sort by column : '" + orderBy + "'. Message: " + e.getMessage());
        }
        // We should never come here...
        return 0;
    }

    private String camelCase(String orderBy) {
        return orderBy.substring(0, 1).toUpperCase() + orderBy.substring(1);
    }

    private boolean buildStatusPredicate(FilterableListItem bli, String status,
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

    private boolean buildVardgivareHsaIdPredicate(BestallningListItem bli, String vardgivareHsaId) {
        if (Strings.isNullOrEmpty(vardgivareHsaId)) {
            return true;
        }
        return vardgivareHsaId.equalsIgnoreCase(bli.getVardgivareHsaId());
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

    private Map<UtredningStatus, ListFilterStatus> buildStatusToListBestallningFilterStatusMap() {
        Map<UtredningStatus, ListFilterStatus> statusMap = new HashMap<>();
        for (UtredningStatus us : UtredningStatus.values()) {
            statusMap.put(us, resolveListBestallningFilterStatus(us, Actor.VARDADMIN));
        }
        return statusMap;
    }

    private ListFilterStatus resolveListBestallningFilterStatus(UtredningStatus us, Actor actor) {
        if (us.getNextActor() == actor) {
            return ListFilterStatus.KRAVER_ATGARD;
        } else {
            return ListFilterStatus.VANTAR_ANNAN_AKTOR;
        }
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

    @Override
    public void endUtredning(EndUtredningRequest endUtredningRequest) {
        Utredning utredning = utredningRepository.findById(endUtredningRequest.getUtredningId()).orElseThrow(
                () -> new IbNotFoundException("Could not find the assessment with id " + endUtredningRequest.getUtredningId()));

        if (!isNull(utredning.getAvbrutenDatum())) {
            throw new IbServiceException(IbErrorCodeEnum.ALREADY_EXISTS, "EndAssessment has already been performed for this Utredning");
        }

        utredning.setAvbrutenDatum(LocalDateTime.now());
        utredning.setAvbrutenAnledning(endUtredningRequest.getEndReason());
        utredningRepository.save(utredning);
    }

    private Invanare updateInvanareFromOrder(Invanare invanare, OrderRequest order) {
        invanare.setPersonId(order.getInvanarePersonnummer());
        invanare.setSarskildaBehov(order.getInvanareBehov());
        invanare.setBakgrundNulage(order.getInvanareBakgrund());
        return invanare;
    }

    private Handlaggare createHandlaggare(Bestallare source) {
        return aHandlaggare()
                .withMyndighet(source.getMyndighet())
                .withEmail(source.getEmail())
                .withFullstandigtNamn(source.getFullstandigtNamn())
                .withKontor(source.getKontor())
                .withKostnadsstalle(source.getKostnadsstalle())
                .withTelefonnummer(source.getTelefonnummer())
                .withAdress(source.getAdress())
                .withPostkod(source.getPostkod())
                .withStad(source.getStad())
                .build();
    }

    private Bestallning createBestallning(OrderRequest order) {
        return aBestallning()
                .withTilldeladVardenhetHsaId(order.getEnhetId())
                .withSyfte(order.getSyfte())
                .withPlaneradeAktiviteter(order.getAtgarder())
                .withOrderDatum(order.getOrderDate().atStartOfDay())
                .withKommentar(order.getKommentar())
                .withIntygKlartSenast(order.getLastDateIntyg().atStartOfDay())
                .build();
    }
}
