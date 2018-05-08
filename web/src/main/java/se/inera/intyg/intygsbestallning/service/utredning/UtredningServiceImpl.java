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
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.pu.services.PUService;
import se.inera.intyg.intygsbestallning.common.exception.IbAuthorizationException;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Handlaggare;
import se.inera.intyg.intygsbestallning.persistence.model.Invanare;
import se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandlingUrsprungTyp;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
import se.inera.intyg.intygsbestallning.service.stateresolver.Actor;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningFas;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.util.GenericComparator;
import se.inera.intyg.intygsbestallning.service.util.PagingUtil;
import se.inera.intyg.intygsbestallning.service.utredning.dto.AssessmentRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.Bestallare;
import se.inera.intyg.intygsbestallning.service.utredning.dto.EndUtredningRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.UpdateOrderRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.FilterableListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetForfraganResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningListResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ListUtredningRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.UtredningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListFilterStatus;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static se.inera.intyg.intygsbestallning.persistence.model.Bestallning.BestallningBuilder.aBestallning;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Handlaggare.HandlaggareBuilder.aHandlaggare;
import static se.inera.intyg.intygsbestallning.persistence.model.Handling.HandlingBuilder.aHandling;
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare.TidigareUtforareBuilder.aTidigareUtforare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;

@Service
@Transactional
public class UtredningServiceImpl extends BaseUtredningService implements UtredningService {

    private static final String INTERPRETER_ERROR_TEXT = "May not set interpreter language if there is no need for interpreter";
    private static final Logger LOG = LoggerFactory.getLogger(UtredningService.class);

    @Autowired
    private PUService puService;

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
        Map<UtredningStatus, ListFilterStatus> statusToFilterStatus = buildStatusToListBestallningFilterStatusMap(Actor.SAMORDNARE);

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

                .sorted((o1, o2) -> GenericComparator.compare(UtredningListItem.class, o1, o2, request.getOrderBy(),
                        request.isOrderByAsc()))
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

    @Override
    public GetUtredningResponse getExternForfragan(String utredningId, String landstingHsaId) {
        Utredning utredning = utredningRepository.findById(utredningId).orElseThrow(
                () -> new IbNotFoundException("Utredning with assessmentId '" + utredningId + "' does not exist."));

        if (!Objects.equals(utredning.getExternForfragan().getLandstingHsaId(), landstingHsaId)) {
            throw new IbAuthorizationException(
                    "Utredning with assessmentId '" + utredningId + "' does not have ExternForfragan for landsting with id '"
                            + landstingHsaId + "'");
        }

        return GetUtredningResponse.from(utredning, utredningStateResolver.resolveStatus(utredning));
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

        return GetUtredningResponse.from(utredning, utredningStateResolver.resolveStatus(utredning));
    }

    @Override
    public List<ForfraganListItem> findForfragningarForVardenhetHsaId(String vardenhetHsaId) {
        return utredningRepository.findAllByExternForfragan_InternForfraganList_VardenhetHsaId(vardenhetHsaId)
                .stream()
                .map(utr -> ForfraganListItem.from(utr, vardenhetHsaId, internForfraganStateResolver))
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
        utredning.getIntygList().add(anIntyg()
                .withSistaDatum(order.getLastDateIntyg().atStartOfDay())
                .build());
        updateInvanareFromOrder(utredning.getInvanare(), order);

        if (order.isHandling()) {
            utredning.getHandlingList().add(aHandling()
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
                        IbErrorCodeEnum.NOT_FOUND, MessageFormat.format("Assessment with id: {0} was not found", update.getUtredningId())));

        if (!utredning.getBestallning().isPresent()) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_REQUEST, "Assessment does not have a Bestallning");
        }

        final Utredning updatedUtredning = qualifyForUpdatering(update, utredning);
        return utredningRepository.save(updatedUtredning);
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
                .withHandelseList(Collections.singletonList(HandelseUtil.createOrderReceived(order.getBestallare().getMyndighet(), null)))
                .withIntygList(Collections.singletonList(anIntyg()
                        .withSistaDatum(Optional.ofNullable(order.getLastDateIntyg()).map(LocalDate::atStartOfDay).orElse(null))
                        .build()))
                .build();

        if (order.isHandling()) {
            utredning.getHandlingList().add(aHandling()
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
                .withArkiverad(false)
                .build());
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

    private Utredning qualifyForUpdatering(final UpdateOrderRequest update, final Utredning original) {

        Preconditions.checkArgument(nonNull(update));
        Preconditions.checkArgument(nonNull(original));
        Preconditions.checkArgument(original.getBestallning().isPresent());

        Utredning toUpdate = Utredning.copyFrom(original);

        update.getLastDateIntyg().ifPresent(date -> toUpdate.getIntygList().stream()
                .filter(i -> isNull(i.getKompletteringsId()))
                .findFirst()
                .ifPresent(i -> i.setSistaDatum(date)));
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

        if (!BooleanUtils.toBoolean(toUpdate.getTolkBehov()) && !isNullOrEmpty(toUpdate.getTolkSprak())) {
            throw new IbServiceException(
                    IbErrorCodeEnum.BAD_REQUEST, INTERPRETER_ERROR_TEXT);
        } else if (nonNull(toUpdate.getTolkSprak()) && !toUpdate.getTolkBehov() && !update.getTolkSprak().isPresent()) {
            toUpdate.setTolkSprak(null);
        }

        return toUpdate;
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

    private Invanare updateInvanareFromOrder(Invanare invanare, OrderRequest order) {
        invanare.setPersonId(order.getInvanarePersonnummer());
        invanare.setFullstandigtNamn(order.getInvanareFullstandigtNamn());
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
                .withOrderDatum(Optional.ofNullable(order.getOrderDate())
                        .map(LocalDate::atStartOfDay)
                        .orElse(null))
                .withKommentar(order.getKommentar())
                .build();
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

    private boolean buildFasPredicate(UtredningListItem uli, String fas) {
        if (Strings.isNullOrEmpty(fas)) {
            return true;
        }
        UtredningFas utredningFas = UtredningFas.valueOf(fas);
        return uli.getFas() == utredningFas;
    }

}
