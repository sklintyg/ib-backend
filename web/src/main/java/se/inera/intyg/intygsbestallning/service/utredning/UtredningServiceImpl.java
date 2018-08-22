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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.MoreCollectors.toOptional;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static se.inera.intyg.intygsbestallning.persistence.model.Bestallning.BestallningBuilder.aBestallning;
import static se.inera.intyg.intygsbestallning.persistence.model.BestallningHistorik.BestallningHistorikBuilder.aBestallningHistorik;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Handlaggare.HandlaggareBuilder.aHandlaggare;
import static se.inera.intyg.intygsbestallning.persistence.model.Handling.HandlingBuilder.aHandling;
import static se.inera.intyg.intygsbestallning.persistence.model.InternForfragan.InternForfraganBuilder.anInternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare.TidigareUtforareBuilder.aTidigareUtforare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import se.inera.intyg.infra.integration.hsa.exception.HsaServiceCallException;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbExternalServiceException;
import se.inera.intyg.intygsbestallning.common.exception.IbExternalSystemEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.BestallningHistorik;
import se.inera.intyg.intygsbestallning.persistence.model.Betalning;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.Handlaggare;
import se.inera.intyg.intygsbestallning.persistence.model.Handling;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Invanare;
import se.inera.intyg.intygsbestallning.persistence.model.RegistreradVardenhet;
import se.inera.intyg.intygsbestallning.persistence.model.SkickadNotifiering;
import se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder;
import se.inera.intyg.intygsbestallning.persistence.model.status.Actor;
import se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.InternForfraganStatusResolver;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningFas;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvslutOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandlingUrsprungTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.MyndighetTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.RegiFormTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.RegistreradVardenhetRepository;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
import se.inera.intyg.intygsbestallning.service.notifiering.send.NotifieringSendService;
import se.inera.intyg.intygsbestallning.service.stateresolver.BesokStatus;
import se.inera.intyg.intygsbestallning.service.stateresolver.BesokStatusResolver;
import se.inera.intyg.intygsbestallning.service.util.GenericComparator;
import se.inera.intyg.intygsbestallning.service.util.PagingUtil;
import se.inera.intyg.intygsbestallning.service.utredning.dto.AssessmentRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.AvslutaUtredningRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.Bestallare;
import se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.UpdateOrderRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FilterableListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.AvslutadUtredningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.AvslutadUtredningListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.GetUtredningListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.ListAvslutadeUtredningarRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.ListUtredningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.SaveBetaldFkIdForUtredningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.SaveBetaldVeIdForUtredningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.SaveFakturaFkIdForUtredningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.UtredningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.UtredningListItemFactory;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListFilterStatus;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v1.HealthCareUnitType;

@Service
@Transactional
public class UtredningServiceImpl extends BaseUtredningService implements UtredningService {

    private static final String INTERPRETER_ERROR_TEXT = "May not set interpreter language if there is no need for interpreter";
    private static final Logger LOG = LoggerFactory.getLogger(UtredningService.class);

    @Autowired
    private RegistreradVardenhetRepository registreradVardenhetRepository;

    @Autowired
    private UtredningListItemFactory utredningListItemFactory;

    @Autowired
    private AvslutadUtredningListItemFactory avslutadUtredningListItemFactory;

    @Autowired
    private NotifieringSendService notifieringSendService;

    @Override
    @Transactional(readOnly = true)
    public GetUtredningListResponse findExternForfraganByLandstingHsaIdWithFilter(String landstingHsaId, ListUtredningRequest request) {
        long start = System.currentTimeMillis();
        List<Utredning> jpaList = utredningRepository.findByExternForfragan_LandstingHsaId_AndArkiverad(landstingHsaId, false);
        LOG.info("Loading findByExternForfragan_LandstingHsaId_AndArkiveradFalse took {} ms", (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        List<UtredningListItem> list = jpaList
                .stream()
                .map(utredningListItemFactory::from)
                .collect(toList());
        LOG.info("UtredningListItemFactory from:: took {} ms", (System.currentTimeMillis() - start));
        // Get status mapper
        Map<UtredningStatus, ListFilterStatus> statusToFilterStatus = buildStatusToListBestallningFilterStatusMap(Actor.SAMORDNARE);

        // If filtering by freeText or ordering by vardenhetNamn we need all vardenhetNames
        boolean enrichedWithVardenhetNames = false;
        if (request.getFreeText() != null || request.getOrderBy().equals("vardenhetNamn")) {
            // Enrich with vardenhet namn from HSA
            start = System.currentTimeMillis();
            enrichWithVardenhetNames(list);
            LOG.info("enrichWithVardenhetNames (first) took {} ms", (System.currentTimeMillis() - start));

            enrichedWithVardenhetNames = true;
        }

        // Start actual filtering. Order is important here. We must always filter out unwanted items _before_ sorting and
        // then finally paging.
        start = System.currentTimeMillis();
        List<UtredningListItem> filtered = list.stream()
                .filter(uli -> uli.getStatus().getUtredningFas() != UtredningFas.AVSLUTAD)
                .filter(uli -> buildFasPredicate(uli, request.getFas()))
                .filter(uli -> buildStatusPredicate(uli, request.getStatus(), statusToFilterStatus))
                .filter(uli -> buildToFromPredicateForUtredningar(uli, request.getFromDate(), request.getToDate()))
                .filter(uli -> buildFreeTextPredicate(uli, request.getFreeText()))

                .sorted((o1, o2) -> GenericComparator.compare(UtredningListItem.class, o1, o2, request.getOrderBy(),
                        request.isOrderByAsc()))
                .collect(toList());
        LOG.info("filtering of UtredningListItem took {} ms", (System.currentTimeMillis() - start));

        List<UtredningListItem> paged = filtered;
        int total = filtered.size();
        if (request.isPerformPaging()) {
            // Paging. We need to perform some bounds-checking...
            if (total == 0) {
                return new GetUtredningListResponse(filtered, total);
            }

            Pair<Integer, Integer> bounds = PagingUtil.getBounds(total, request.getPageSize(), request.getCurrentPage());
            paged = filtered.subList(bounds.getFirst(), bounds.getSecond() + 1);
        }

        if (!enrichedWithVardenhetNames) {
            // Enrich with vardenhet namn from HSA
            start = System.currentTimeMillis();
            enrichWithVardenhetNames(paged);
            LOG.info("enrichWithVardenhetNames (second) took {} ms", (System.currentTimeMillis() - start));
        }
        return new GetUtredningListResponse(paged, total);
    }

    @Override
    @Transactional(readOnly = true)
    public GetUtredningListResponse findAvslutadeExternForfraganByLandstingHsaIdWithFilter(String landstingHsaId,
                                                                                           ListAvslutadeUtredningarRequest request) {
        long start = System.currentTimeMillis();
        List<Utredning> jpaList = utredningRepository.findByExternForfragan_LandstingHsaId_AndArkiverad(landstingHsaId, true);
        LOG.info("Loading findByExternForfragan_LandstingHsaId_AndArkiveradTrue took {} ms", (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        List<AvslutadUtredningListItem> list = jpaList
                .stream()
                .map(avslutadUtredningListItemFactory::from)
                .collect(toList());
        LOG.info("UtredningListItemFactory from:: took {} ms", (System.currentTimeMillis() - start));

        // If filtering by freeText or ordering by vardenhetNamn we need all vardenhetNames
        boolean enrichedWithVardenhetNames = false;
        if (request.getFreeText() != null || request.getOrderBy().equals("vardenhetNamn")) {
            // Enrich with vardenhet namn from HSA
            start = System.currentTimeMillis();
            enrichWithVardenhetNames(list);
            LOG.info("enrichWithVardenhetNames (first) took {} ms", (System.currentTimeMillis() - start));

            enrichedWithVardenhetNames = true;
        }

        // Start actual filtering. Order is important here. We must always filter out unwanted items _before_ sorting and
        // then finally paging.
        start = System.currentTimeMillis();
        List<AvslutadUtredningListItem> filtered = list.stream()
                .filter(uli -> uli.getStatus().getUtredningFas() == UtredningFas.AVSLUTAD)
                .filter(uli -> buildToFromPredicate(uli.getAvslutsDatum(), request.getAvslutsDatumFromDate(),
                        request.getAvslutsDatumToDate()))
                .filter(uli -> buildFreeTextPredicate(uli, request.getFreeText()))
                .filter(uli -> buildYesNoAllPredicate(uli.getErsatts(), request.getErsatts()))
                .filter(uli -> buildYesNoAllPredicate(uli.getFakturaVeId(), request.getFakturaVeId()))
                .filter(uli -> buildYesNoAllPredicate(uli.getBetaldVeId(), request.getBetaldVeId()))
                .filter(uli -> buildYesNoAllPredicate(uli.getFakturaFkId(), request.getFakturaFkId()))
                .filter(uli -> buildYesNoAllPredicate(uli.getBetaldFkId(), request.getBetaldFkId()))

                .sorted((o1, o2) -> GenericComparator.compare(AvslutadUtredningListItem.class, o1, o2, request.getOrderBy(),
                        request.isOrderByAsc()))
                .collect(toList());
        LOG.info("filtering of AvslutadUtredningListItem took {} ms", (System.currentTimeMillis() - start));

        List<AvslutadUtredningListItem> paged = filtered;
        int total = filtered.size();
        if (request.isPerformPaging()) {
            // Paging. We need to perform some bounds-checking...
            if (total == 0) {
                return new GetUtredningListResponse(filtered, total);
            }

            Pair<Integer, Integer> bounds = PagingUtil.getBounds(total, request.getPageSize(), request.getCurrentPage());
            paged = filtered.subList(bounds.getFirst(), bounds.getSecond() + 1);
        }

        if (!enrichedWithVardenhetNames) {
            // Enrich with vardenhet namn from HSA
            start = System.currentTimeMillis();
            enrichWithVardenhetNames(paged);
            LOG.info("enrichWithVardenhetNames (second) took {} ms", (System.currentTimeMillis() - start));
        }

        return new GetUtredningListResponse(paged, total);
    }

    @Override
    public GetUtredningResponse getExternForfragan(Long utredningId, String landstingHsaId) {
        Utredning utredning = utredningRepository.findById(utredningId).orElseThrow(
                () -> new IbNotFoundException("Utredning with assessmentId '" + utredningId + "' does not exist."));

        if (!Objects.equals(utredning.getExternForfragan().map(ExternForfragan::getLandstingHsaId).orElse(null), landstingHsaId)) {
            throw new IbAuthorizationException(
                    "Utredning with assessmentId '" + utredningId + "' does not have ExternForfragan for landsting with id '"
                            + landstingHsaId + "'");
        }

        return createGetUtredningResponse(utredning);
    }

    @Override
    public Utredning registerOrder(OrderRequest order) {
        Utredning utredning = utredningRepository.findById(order.getUtredningId()).orElseThrow(
                () -> new IbNotFoundException(
                        MessageFormat.format("Felaktig utredningsid: {0}. Utredningen existerar inte.", order.getUtredningId())));

        // Validate the state
        if (utredning.getBestallning().isPresent()) {
            LOG.warn("Assessment '{}' already have a bestallning", utredning.getUtredningId());
            throw new IllegalArgumentException(
                    "Cannot create a order when one already exists for assessmentId " + order.getUtredningId());
        }

        // Utredning must have an ExternForfragan.
        if (!utredning.getExternForfragan().isPresent()) {
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
        utredning.getIntygList().add(anIntyg()
                .withKomplettering(false)
                .withSistaDatum(order.getLastDateIntyg().atStartOfDay())
                .build());

        updateOrgNrFromVardenhetHsaId(utredning.getBestallning().get());

        updateInvanareFromOrder(utredning.getInvanare(), order);

        if (order.isHandling()) {
            Handling handling = aHandling()
                    .withSkickatDatum(LocalDate.now().atStartOfDay())
                    .withUrsprung(HandlingUrsprungTyp.BESTALLNING)
                    .build();
            utredning.getHandlingList().add(handling);
        }
        utredning.getHandelseList().add(HandelseUtil
                .createOrderReceived(MyndighetTyp.of(order.getBestallare().getMyndighet()).getDescription(), order.getLastDateIntyg()));

        utredningRepository.saveUtredning(utredning);
        notifieringSendService.notifieraVardenhetNyBestallning(utredning);
        return utredning;
    }

    @Override
    public Utredning updateOrder(final UpdateOrderRequest update) {

        final Utredning utredning = utredningRepository.findById(update.getUtredningId())
                .orElseThrow(() -> new IbServiceException(
                        IbErrorCodeEnum.NOT_FOUND, MessageFormat.format("Assessment with id: {0} was not found", update.getUtredningId())));

        if (utredning.getStatus().getUtredningFas() == UtredningFas.AVSLUTAD) {
            throw new IbServiceException(
                    IbErrorCodeEnum.BAD_STATE,
                    MessageFormat.format("Utredning with id {0} is in an incorrect state", utredning.getUtredningId()));
        }

        if (!utredning.getBestallning().isPresent()) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, "Assessment does not have a Bestallning");
        }

        qualifyForUppdatering(update, utredning);

        utredningRepository.saveUtredning(utredning);
        notifieringSendService.notifieraVardenhetUppdateradBestallning(utredning);
        return utredning;
    }

    @Override
    public void updateStatusToRedovisaBesok(final Utredning utredning) {
        final Utredning savedUtredning = utredningRepository.saveUtredning(utredning);
        checkState(UtredningStatus.REDOVISA_BESOK == savedUtredning.getStatus());

        notifieringSendService.notifieraVardenhetRedovisaBesok(utredning);
    }

    @Override
    public Utredning registerNewUtredning(OrderRequest order) {
        Utredning utredning = anUtredning()
                .withUtredningsTyp(order.getUtredningsTyp())
                .withInvanare(updateInvanareFromOrder(new Invanare(), order))
                .withTolkBehov(order.isTolkBehov())
                .withTolkSprak(order.getTolkSprak())
                .withBestallning(createBestallning(order))
                .withHandlaggare(createHandlaggare(order.getBestallare()))
                .withHandelseList(Lists.newArrayList(HandelseUtil
                        .createOrderReceived(MyndighetTyp.of(order.getBestallare().getMyndighet()).getDescription(), null)))
                .withIntygList(Lists.newArrayList(anIntyg()
                        .withKomplettering(false)
                        .withSistaDatum(Optional.ofNullable(order.getLastDateIntyg()).map(LocalDate::atStartOfDay).orElse(null))
                        .build()))
                .build();

        final Utredning sparadUtredning;
        if (order.isHandling()) {
            utredning.getHandlingList().add(aHandling()
                    .withSkickatDatum(LocalDate.now().atStartOfDay())
                    .withUrsprung(HandlingUrsprungTyp.BESTALLNING)
                    .build());
            sparadUtredning = utredningRepository.saveUtredning(utredning);
            notifieringSendService.notifieraVardenhetNyBestallning(utredning);
        } else {
            sparadUtredning = utredningRepository.saveUtredning(utredning);

        }
        return sparadUtredning;
    }

    @Override
    public Utredning registerNewUtredning(final AssessmentRequest request) {

        ExternForfraganBuilder externForfragan = anExternForfragan()
                .withInkomDatum(LocalDateTime.now())
                .withBesvarasSenastDatum(request.getBesvaraSenastDatum())
                .withLandstingHsaId(request.getLandstingHsaId());

        List<TidigareUtforare> tidigareUtforareList = Lists.newArrayList();
        if (isNotEmpty(request.getInvanareTidigareUtforare())) {
            tidigareUtforareList = request.getInvanareTidigareUtforare().stream()
                    .map(tidigareUtforare -> aTidigareUtforare()
                            .withTidigareEnhetId(tidigareUtforare)
                            .build())
                    .collect(toList());
        }

        final Invanare invanare = anInvanare()
                .withPostort(request.getInvanarePostort())
                .withSarskildaBehov(request.getInvanareSarskildaBehov())
                .withTidigareUtforare(tidigareUtforareList)
                .build();

        final UtredningBuilder utredningBuilder = anUtredning()
                .withUtredningsTyp(request.getUtredningsTyp())
                .withExternForfragan(externForfragan.build())
                .withInvanare(invanare)
                .withHandlaggare(createHandlaggare(request.getBestallare()))
                .withTolkBehov(request.isTolkBehov())
                .withTolkSprak(request.getTolkSprak())
                .withArkiverad(false);

        final List<RegistreradVardenhet> byVardgivareHsaId = registreradVardenhetRepository
                .findByVardgivareHsaId(request.getLandstingHsaId());

        final Handelse handelse;
        final Utredning utredning;
        final Utredning sparadUtredning;


        //FMU-004: Alternativflöde 1 - Internförfrågan skickas direkt till (enda) vårdenhet i egen regi
        if (byVardgivareHsaId.size() == 1 && RegiFormTyp.EGET_LANDSTING.equals(byVardgivareHsaId.get(0).getVardenhetRegiForm())) {
            final String vardenhetHsaId = byVardgivareHsaId.iterator().next().getVardenhetHsaId();
            externForfragan.withInternForfraganList(Lists.newArrayList(anInternForfragan()
                    .withVardenhetHsaId(vardenhetHsaId)
                    .withBesvarasSenastDatum(request.getBesvaraSenastDatum())
                    .withSkapadDatum(LocalDateTime.now())
                    .withKommentar(request.getKommentar())
                    .build()));

            handelse = HandelseUtil.createInternForfraganSkickad(request.getLandstingHsaId(), vardenhetHsaId);
            utredningBuilder
                    .withExternForfragan(externForfragan.build())
                    .withHandelseList(Lists.newArrayList(handelse));
            utredning = utredningBuilder.build();
            checkState(Objects.equals(UtredningStatus.VANTAR_PA_SVAR, UtredningStatusResolver.resolveStaticStatus(utredning)));
            sparadUtredning = utredningRepository.saveUtredning(utredning);
            //In this case we know that this is the only internforfragan
            InternForfragan internForfragan = sparadUtredning.getExternForfragan().get().getInternForfraganList().get(0);
            notifieringSendService.notifieraVardenhetNyInternforfragan(sparadUtredning, internForfragan,
                    getLandstingNameOrHsaId(request.getLandstingHsaId()));
        } else {
            //FMU-004: Normalflöde 1 - Landstinget tar emot externförfrågan

            handelse = HandelseUtil.createExternForfraganMottagen(request.getLandstingHsaId());
            utredningBuilder
                    .withExternForfragan(externForfragan.build())
                    .withHandelseList(Lists.newArrayList(handelse));
            utredning = utredningBuilder.build();
            checkState(Objects.equals(UtredningStatus.FORFRAGAN_INKOMMEN, UtredningStatusResolver.resolveStaticStatus(utredning)));
            sparadUtredning = utredningRepository.saveUtredning(utredning);
            notifieringSendService.notifieraLandstingNyExternforfragan(sparadUtredning);
        }
        return sparadUtredning;
    }

    @Override
    public void avslutaUtredning(final AvslutaUtredningRequest request) {
        final Utredning utredning = utredningRepository.findById(request.getUtredningId())
                .orElseThrow(() -> new IbNotFoundException("Angivet utredningsid existerar inte"));

        if (nonNull(utredning.getAvbrutenDatum()) || nonNull(utredning.getAvbrutenOrsak())) {
            throw new IbServiceException(IbErrorCodeEnum.ALREADY_EXISTS,
                    MessageFormat.format("EndAssessment has already been performed for Utredning {0}", utredning.getUtredningId()));
        }

        final AvslutOrsak orsak = Optional.ofNullable(request.getAvslutOrsak()).orElse(AvslutOrsak.UTREDNING_AVBRUTEN);

        qualifyForAvslutaUtredning(orsak, utredning);

        final String vardAdministrator = request.getUser().map(IbUser::getNamn).orElse(null);
        utredning.setAvbrutenDatum(LocalDateTime.now());
        utredning.setAvbrutenOrsak(orsak);
        utredning.setArkiverad(true);

        final Handelse handelse = createHandelseUtredningAvslutad(orsak, vardAdministrator);
        utredning.getHandelseList().add(handelse);

        final Utredning uppdateradUtredning = utredningRepository.saveUtredning(utredning);
        final Optional<InternForfragan> internForfragan = verifyAvslutaUtredningStatusar(orsak, uppdateradUtredning);
        notifieraUtredningAvslutad(orsak, uppdateradUtredning, internForfragan.orElse(null));
    }

    @Override
    @Transactional
    public void saveBetaldVeIdForUtredning(Long utredningsId, SaveBetaldVeIdForUtredningRequest request, String loggedInAtLandstingHsaId) {
        Utredning utredning = utredningRepository.findById(utredningsId).orElseThrow(
                () -> new IbNotFoundException("Utredning with assessmentId '" + utredningsId + "' does not exist."));

        // Verify that the current vardenhet has a Bestallning.
        if (!utredning.getExternForfragan().isPresent()) {
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM,
                    "Utredning with assessmentId '" + utredningsId + "' does not have a ExternForfragan.");
        }

        if (!utredning.getExternForfragan().get().getLandstingHsaId().equals(loggedInAtLandstingHsaId)) {
            throw new IbAuthorizationException("The current user cannot mark Utredning with assessmentId '" + utredningsId
                    + "' as betald, ExternForfragan is for another landsting");
        }

        if (utredning.getBetalning() != null) {
            utredning.getBetalning().setBetaldVeId(request.getBetaldVeId());
        } else {
            Betalning betalning = Betalning.BetalningBuilder.aBetalning()
                    .withBetaldVeId(request.getBetaldVeId())
                    .build();
            utredning.setBetalning(betalning);
        }
        utredningRepository.saveUtredning(utredning);
    }

    @Override
    @Transactional
    public void saveBetaldFkIdForUtredning(Long utredningsId, SaveBetaldFkIdForUtredningRequest request,
                                               String loggedInAtLandstingHsaId) {
        Utredning utredning = utredningRepository.findById(utredningsId).orElseThrow(
                () -> new IbNotFoundException("Utredning with assessmentId '" + utredningsId + "' does not exist."));

        // Verify that the current vardenhet has a Bestallning.
        if (!utredning.getExternForfragan().isPresent()) {
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM,
                    "Utredning with assessmentId '" + utredningsId + "' does not have a ExternForfragan.");
        }

        if (!utredning.getExternForfragan().get().getLandstingHsaId().equals(loggedInAtLandstingHsaId)) {
            throw new IbAuthorizationException("The current user cannot mark Utredning with assessmentId '" + utredningsId
                    + "' as betald, ExternForfragan is for another landsting");
        }

        if (utredning.getBetalning() != null) {
            utredning.getBetalning().setBetaldFkId(request.getBetaldFkId());
        } else {
            Betalning betalning = Betalning.BetalningBuilder.aBetalning()
                    .withBetaldFkId(request.getBetaldFkId())
                    .build();
            utredning.setBetalning(betalning);
        }
        utredningRepository.saveUtredning(utredning);
    }

    @Override
    @Transactional
    public void saveFakturaFkIdForUtredning(Long utredningsId, SaveFakturaFkIdForUtredningRequest request,
                                           String loggedInAtLandstingHsaId) {
        Utredning utredning = utredningRepository.findById(utredningsId).orElseThrow(
                () -> new IbNotFoundException("Utredning with assessmentId '" + utredningsId + "' does not exist."));

        // Verify that the current vardenhet has a Bestallning.
        if (!utredning.getExternForfragan().isPresent()) {
            throw new IbServiceException(IbErrorCodeEnum.UNKNOWN_INTERNAL_PROBLEM,
                    "Utredning with assessmentId '" + utredningsId + "' does not have a ExternForfragan.");
        }

        if (!utredning.getExternForfragan().get().getLandstingHsaId().equals(loggedInAtLandstingHsaId)) {
            throw new IbAuthorizationException("The current user cannot mark Utredning with assessmentId '" + utredningsId
                    + "' as betald, ExternForfragan is for another landsting");
        }

        if (utredning.getBetalning() != null) {
            utredning.getBetalning().setFakturaFkId(request.getFakturaFkId());
        } else {
            Betalning betalning = Betalning.BetalningBuilder.aBetalning()
                    .withFakturaFkId(request.getFakturaFkId())
                    .build();
            utredning.setBetalning(betalning);
        }
        utredningRepository.saveUtredning(utredning);
    }

    private void qualifyForAvslutaUtredning(final AvslutOrsak orsak, final Utredning utredning) {
        if (orsak == AvslutOrsak.INGEN_KOMPLETTERING_BEGARD) {
            isKorrektStatusForIngenKompletteringBegard(utredning);
        } else if (orsak == AvslutOrsak.INGEN_BESTALLNING) {
            isKorrektStatusForIngenBestallning(utredning);
        } else if (orsak == AvslutOrsak.JAV) {
            isKorrektStatusForJav(utredning);
        } else if (orsak == AvslutOrsak.UTREDNING_AVBRUTEN) {
            isKorrektStatusForUtredningAvbruten(utredning);
        } else {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, MessageFormat.format("EndReason {0} is not supported", orsak));
        }
    }

    private void isKorrektStatusForIngenKompletteringBegard(final Utredning utredning) {
        UtredningStatus status = UtredningStatusResolver.resolveStaticStatus(utredning);
        List<UtredningStatus> statesAllowedToTransitionToAvslutad = ImmutableList.of(UtredningStatus.UTLATANDE_MOTTAGET,
                UtredningStatus.KOMPLETTERING_MOTTAGEN, UtredningStatus.REDOVISA_BESOK);

        checkState(statesAllowedToTransitionToAvslutad.contains(status),
                MessageFormat.format("Utredning with id {0} is in an incorrect state", utredning.getUtredningId()));

        checkState(utredning.getBesokList().stream()
                        .map(BesokStatusResolver::resolveStaticStatus)
                        .noneMatch(besokStatus -> (besokStatus == BesokStatus.BOKAT) || (besokStatus == BesokStatus.OMBOKAT)),
                MessageFormat.format("Utredning with id {0} is in an incorrect state", utredning.getUtredningId()));
    }

    private void isKorrektStatusForIngenBestallning(final Utredning utredning) {
        checkState(utredning.getStatus() == UtredningStatus.TILLDELAD_VANTAR_PA_BESTALLNING,
                MessageFormat.format("Utredning with id {0} is in an incorrect state", utredning.getUtredningId()));
    }

    private void isKorrektStatusForJav(final Utredning utredning) {
        final List<UtredningStatus> godkandaStatusar = ImmutableList.of(
                UtredningStatus.BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR,
                UtredningStatus.HANDLINGAR_MOTTAGNA_BOKA_BESOK,
                UtredningStatus.UTREDNING_PAGAR);
        checkState(godkandaStatusar.contains(utredning.getStatus()),
                MessageFormat.format("Utredning with id {0} is in an incorrect state", utredning.getUtredningId()));
    }

    private void isKorrektStatusForUtredningAvbruten(Utredning utredning) {
        final List<UtredningStatus> godkandaStatusar = ImmutableList.of(
                UtredningStatus.UTREDNING_PAGAR,
                UtredningStatus.AVVIKELSE_MOTTAGEN);
        checkState(godkandaStatusar.contains(UtredningStatusResolver.resolveStaticStatus(utredning)),
                MessageFormat.format("Utredning with id {0} is in an incorrect state", utredning.getUtredningId()));
    }

    private Optional<InternForfragan> verifyAvslutaUtredningStatusar(final AvslutOrsak orsak, final Utredning utredning) {
        UtredningStatus status = UtredningStatusResolver.resolveStaticStatus(utredning);
        checkState((status == UtredningStatus.AVBRUTEN || status == UtredningStatus.AVSLUTAD),
                MessageFormat.format("Utredning with id {0} is in an incorrect state", utredning.getUtredningId()));

        if (orsak == AvslutOrsak.INGEN_BESTALLNING) {
            checkState(utredning.getExternForfragan().isPresent());

            final Optional<InternForfragan> optionalInternForfragan = utredning.getExternForfragan().get()
                    .getInternForfraganList().stream()
                    .filter(iff -> nonNull(iff.getTilldeladDatum()))
                    .collect(toOptional());

            checkState(optionalInternForfragan.isPresent(),
                    MessageFormat.format("Utredning with id {0} is in an incorrect state", utredning.getUtredningId()));

            final InternForfragan internForfragan = optionalInternForfragan.get();
            final InternForfraganStatus internForfraganStatus =
                    InternForfraganStatusResolver.resolveStaticStatus(utredning, internForfragan);

            checkState(InternForfraganStatus.INGEN_BESTALLNING == internForfraganStatus,
                    MessageFormat.format("Utredning with id {0} is in an incorrect state", utredning.getUtredningId()));

            return optionalInternForfragan;
        }
        return Optional.empty();
    }

    private Handelse createHandelseUtredningAvslutad(final AvslutOrsak orsak, final String vardAdministrator) {

        if (orsak == AvslutOrsak.INGEN_BESTALLNING) {
            return HandelseUtil.createIngenBestallning();
        } else if (orsak == AvslutOrsak.JAV) {
            return HandelseUtil.createJav();
        } else if (orsak == AvslutOrsak.UTREDNING_AVBRUTEN) {
            return HandelseUtil.createUtredningAvbruten();
        } else {
            return HandelseUtil.createAvslutadUtredning(vardAdministrator);
        }
    }

    private void notifieraUtredningAvslutad(final AvslutOrsak orsak, final Utredning utredning, final InternForfragan internForfragan) {

        if (orsak == AvslutOrsak.INGEN_BESTALLNING) {
            notifieringSendService.notifieraVardenhetIngenBestallning(utredning, internForfragan);
            notifieringSendService.notifieraLandstingIngenBestallning(utredning);
        } else if (orsak == AvslutOrsak.JAV) {
            notifieringSendService.notifieraLandstingAvslutadPgaJav(utredning);
            notifieringSendService.notifieraVardenhetAvslutadPgaJav(utredning);
        } else if (orsak == AvslutOrsak.UTREDNING_AVBRUTEN) {
            notifieringSendService.notifieraLandstingAvslutadUtredning(utredning);
            notifieringSendService.notifieraVardenhetAvslutadUtredning(utredning);
        } else {
            LOG.info(MessageFormat.format("Utredning with id {0} ended because of reason {1}", utredning.getUtredningId(), orsak));
        }
    }

    private void qualifyForUppdatering(final UpdateOrderRequest update, final Utredning utredning) {

        checkArgument(nonNull(update));
        checkArgument(nonNull(utredning));
        checkArgument(utredning.getBestallning().isPresent());

        final Boolean tolkBehovOriginal = utredning.getTolkBehov();
        update.getTolkBehov().ifPresent(utredning::setTolkBehov);

        final String tolkSprakOriginal = utredning.getTolkSprak();
        update.getTolkSprak().ifPresent(utredning::setTolkSprak);

        AtomicLong intygsId = new AtomicLong();
        final LocalDateTime sistaDatumOriginal = utredning.getIntygList().stream()
                .filter(i -> !i.isKomplettering())
                .findFirst()
                .map(intyg -> {
                    intygsId.set(intyg.getId());
                    return intyg;
                })
                .map(Intyg::getSistaDatum)
                .orElse(null);

        update.getLastDateIntyg().ifPresent(sistaDatum ->
                utredning.getIntygList().stream()
                        .filter(i -> !i.isKomplettering())
                        .findFirst()
                        .ifPresent(i -> i.setSistaDatum(sistaDatum)));

        final Optional<Handlaggare> optionalHandlaggare = Optional.ofNullable(utredning.getHandlaggare());

        final Handlaggare handlaggareOriginal;

        if (optionalHandlaggare.isPresent()) {
            handlaggareOriginal = Handlaggare.copyFrom(optionalHandlaggare.get());
            Handlaggare handlaggare = optionalHandlaggare.get();

            update.getBestallare().ifPresent(bestallare -> {
                Optional.ofNullable(bestallare.getFullstandigtNamn())
                        .ifPresent(fullstandigtNamn -> handlaggare.setFullstandigtNamn(emptyToNull(fullstandigtNamn)));
                Optional.ofNullable(bestallare.getEmail())
                        .ifPresent(email -> handlaggare.setEmail(emptyToNull(email)));
                Optional.ofNullable(bestallare.getKontor())
                        .ifPresent(kontor -> handlaggare.setKontor(emptyToNull(kontor)));
                Optional.ofNullable(bestallare.getKostnadsstalle())
                        .ifPresent(kostnadsstalle -> handlaggare.setKostnadsstalle(emptyToNull(kostnadsstalle)));
                Optional.ofNullable(bestallare.getMyndighet())
                        .ifPresent(myndighet -> handlaggare.setMyndighet(emptyToNull(myndighet)));
                Optional.ofNullable(bestallare.getPostnummer())
                        .ifPresent(postnummer -> handlaggare.setPostnummer(emptyToNull(postnummer)));
                Optional.ofNullable(bestallare.getStad())
                        .ifPresent(stad -> handlaggare.setStad(emptyToNull(stad)));
                Optional.ofNullable(bestallare.getTelefonnummer())
                        .ifPresent(telefonnummer -> handlaggare.setTelefonnummer(emptyToNull(telefonnummer)));
            });

        } else {
            handlaggareOriginal = null;
            update.getBestallare().ifPresent(bestallare ->
                    utredning.setHandlaggare(aHandlaggare()
                            .withEmail(emptyToNull(bestallare.getEmail()))
                            .withFullstandigtNamn(emptyToNull(bestallare.getFullstandigtNamn()))
                            .withKontor(emptyToNull(bestallare.getKontor()))
                            .withKostnadsstalle(emptyToNull(bestallare.getKostnadsstalle()))
                            .withMyndighet(emptyToNull(bestallare.getMyndighet()))
                            .withPostnummer(emptyToNull(bestallare.getPostnummer()))
                            .withStad(emptyToNull(bestallare.getStad()))
                            .withTelefonnummer(emptyToNull(bestallare.getTelefonnummer()))
                            .build()));
        }

        final ImmutableList<ImmutablePair> fields = ImmutableList.of(
                ImmutablePair.of(tolkBehovOriginal, utredning.getTolkBehov()),
                ImmutablePair.of(tolkSprakOriginal, utredning.getTolkSprak()),
                ImmutablePair.of(handlaggareOriginal, utredning.getHandlaggare()),
                ImmutablePair.of(
                        sistaDatumOriginal,
                        utredning.getIntygList().stream()
                                .filter(i -> !i.isKomplettering())
                                .findFirst()
                                .map(Intyg::getSistaDatum)
                                .orElse(null))
        );

        if (fields.stream().allMatch(pair -> Objects.equals(pair.left, pair.right))) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, "No info to update");
        }

        final LocalDateTime now = LocalDateTime.now();
        update.getHandling()
                .filter(BooleanUtils::isTrue)
                .ifPresent(isHandling -> utredning.getHandlingList().add(aHandling()
                        .withInkomDatum(null)
                        .withSkickatDatum(now)
                        .withUrsprung(HandlingUrsprungTyp.UPPDATERING)
                        .build()));

        if (isFalse(utredning.getTolkBehov()) && !isNullOrEmpty(utredning.getTolkSprak())) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, INTERPRETER_ERROR_TEXT);
        } else if (nonNull(utredning.getTolkSprak()) && isFalse(utredning.getTolkBehov())) {
            utredning.setTolkSprak(null);
        }

        // Lägg till händelse, handläggare, sistadatum och markera beställningen som uppdaterad.
        final Optional<LocalDate> nyttSistaDatum = update.getLastDateIntyg().map(LocalDateTime::toLocalDate);
        final String nyHandlaggare = update.getBestallare().isPresent() ? update.getBestallare().get().getFullstandigtNamn() : null;
        final LocalDateTime uppdateradDatum = LocalDateTime.now();
        utredning.getHandelseList().add(HandelseUtil.createOrderUpdated(nyttSistaDatum, nyHandlaggare, update.getHandling().isPresent()));
        utredning.getBestallning().ifPresent(bestallning -> bestallning.setUppdateradDatum(uppdateradDatum));

        // Lägg till en BestallningHistorik om det finns en kommentar i UpdateRequestet
        update.getKommentar().ifPresent(kommentar -> {
            BestallningHistorik historik = aBestallningHistorik()
                    .withDatum(uppdateradDatum)
                    .withKommentar(kommentar)
                    .build();
            utredning.getBestallning().ifPresent(bestallning -> bestallning.getBestallningHistorikList().add(historik));
        });

        // Om nytt sista datum är satt i requestet -> markera eventuellt skickad notifiering gällande som ERSATTS = true
        nyttSistaDatum.ifPresent(localDate -> utredning.getSkickadNotifieringList().stream()
                .filter(isSkickadPaminnelseNotifiering(intygsId.get()))
                .collect(toOptional())
                .ifPresent(SkickadNotifiering::ersatts));
    }

    private void updateOrgNrFromVardenhetHsaId(Bestallning bestallning) {
        try {
            HealthCareUnitType vardenhet = organizationUnitService.getHealthCareUnit(bestallning.getTilldeladVardenhetHsaId());
            if (vardenhet == null || StringUtils.isBlank(vardenhet.getHealthCareProviderOrgNo())) {
                LOG.error("Failed to lookup orgnr for vardenhet " + bestallning.getTilldeladVardenhetHsaId());
                throw new IbExternalServiceException(IbErrorCodeEnum.EXTERNAL_ERROR, IbExternalSystemEnum.HSA,
                        "Det uppstod ett fel när HSA Katalogen anropades. Beställningen kunde därför inte tas emot.", null);
            }
            bestallning.setTilldeladVardenhetOrgNr(vardenhet.getHealthCareProviderOrgNo());
        } catch (HsaServiceCallException e) {
            LOG.error("Failed to lookup orgnr for vardenhet " + bestallning.getTilldeladVardenhetHsaId(), e);
            if (e.getErroIdEnum() == HsaServiceCallException.ErrorIdEnum.APPLICATION_ERROR) {
                throw new IbExternalServiceException(IbErrorCodeEnum.EXTERNAL_ERROR, IbExternalSystemEnum.HSA,
                        "Det uppstod ett fel när HSA Katalogen anropades. Beställningen kunde därför inte tas emot.", null);
            } else {
                throw new IbExternalServiceException(IbErrorCodeEnum.EXTERNAL_ERROR, IbExternalSystemEnum.HSA,
                        "Received technical error from HSA", null);
            }
        }
    }

    private Invanare updateInvanareFromOrder(final Invanare invanare, OrderRequest order) {
        invanare.setPersonId(order.getInvanarePersonnummer());
        invanare.setFornamn(order.getInvanareFornamn());
        invanare.setMellannamn(order.getInvanareMellannamn());
        invanare.setEfternamn(order.getInvanareEfternamn());
        invanare.setSarskildaBehov(order.getInvanareBehov());
        invanare.setBakgrundNulage(order.getInvanareBakgrund());
        return invanare;
    }

    private Handlaggare createHandlaggare(Bestallare source) {
        return aHandlaggare()
                .withMyndighet(MyndighetTyp.of(source.getMyndighet()).name())
                .withEmail(source.getEmail())
                .withFullstandigtNamn(source.getFullstandigtNamn())
                .withKontor(source.getKontor())
                .withKostnadsstalle(source.getKostnadsstalle())
                .withTelefonnummer(source.getTelefonnummer())
                .withAdress(source.getAdress())
                .withPostnummer(source.getPostnummer())
                .withStad(source.getStad())
                .build();
    }

    private Bestallning createBestallning(OrderRequest order) {
        LocalDateTime orderDate = LocalDateTime.now();
        return aBestallning()
                .withTilldeladVardenhetHsaId(order.getEnhetId())
                .withSyfte(order.getSyfte())
                .withPlaneradeAktiviteter(order.getAtgarder())
                .withOrderDatum(orderDate)
                .withBestallningHistorik(Lists.newArrayList(
                        aBestallningHistorik()
                                .withDatum(orderDate)
                                .withKommentar(order.getKommentar())
                                .build()))
                .build();
    }

    private boolean buildToFromPredicateForUtredningar(FilterableListItem bli, String fromDate, String toDate) {
        if (isNullOrEmpty(fromDate) || isNullOrEmpty(toDate)) {
            return true;
        }

        switch (bli.getStatus().getUtredningFas()) {
            case AVSLUTAD:
                return false;
            case REDOVISA_BESOK:
                return isNullOrEmpty(fromDate);
            case UTREDNING:
            case KOMPLETTERING:
            case FORFRAGAN:
                return fromDate.compareTo(bli.getSlutdatumFas()) <= 0 && toDate.compareTo(bli.getSlutdatumFas()) >= 0;
        }
        return true;
    }

    private boolean buildFasPredicate(UtredningListItem uli, String fas) {
        if (isNullOrEmpty(fas)) {
            return true;
        }
        UtredningFas utredningFas = UtredningFas.valueOf(fas);
        return uli.getFas() == utredningFas;
    }
}
