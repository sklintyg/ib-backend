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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.infra.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.infra.integration.pu.services.PUService;
import se.inera.intyg.infra.logmessages.ActivityType;
import se.inera.intyg.infra.logmessages.ResourceType;
import se.inera.intyg.intygsbestallning.auth.IbUser;
import se.inera.intyg.intygsbestallning.auth.model.IbVardgivare;
import se.inera.intyg.intygsbestallning.auth.pdl.PDLActivityStore;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Handlaggare;
import se.inera.intyg.intygsbestallning.persistence.model.Invanare;
import se.inera.intyg.intygsbestallning.persistence.model.TidigareUtforare;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PDLLoggable;
import se.inera.intyg.intygsbestallning.service.stateresolver.Actor;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStateResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.user.UserService;
import se.inera.intyg.intygsbestallning.service.utredning.dto.AssessmentRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.Bestallare;
import se.inera.intyg.intygsbestallning.service.utredning.dto.EndUtredningRequest;
import se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.BestallningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetForfraganResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.UtredningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListBestallningFilter;
import se.inera.intyg.intygsbestallning.web.controller.api.filter.ListBestallningFilterStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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
    private PUService puService;

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
    public GetUtredningResponse getExternForfragan(String utredningId, String landstingHsaId) {
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
        if (utredning.getBestallning() != null) {
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
        utredning.setSprakTolk(order.getTolkSprak());
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
                            .build());
        }
        utredning.getHandelseList().add(HandelseUtil.createOrderReceived(order.getBestallare().getMyndighet(), order.getOrderDate()));
        utredningRepository.save(utredning);
        return utredning;
    }

    @Override
    public Utredning registerNewUtredning(OrderRequest order) {
        Utredning utredning = anUtredning()
                .withUtredningId(UUID.randomUUID().toString())
                .withUtredningsTyp(order.getUtredningsTyp())
                .withInvanare(updateInvanareFromOrder(new Invanare(), order))
                .withSprakTolk(order.getTolkSprak())
                .withBestallning(createBestallning(order))
                .withHandlaggare(createHandlaggare(order.getBestallare()))
                .withHandelseList(Arrays.asList(HandelseUtil.createOrderReceived(order.getBestallare().getMyndighet(), null)))
                .build();

        if (order.isHandling()) {
            utredning.getHandlingList().add(
                    aHandling()
                            .withSkickatDatum(LocalDate.now().atStartOfDay())
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
                .withSprakTolk(request.getTolkSprak())
                .build());
    }

    @Override
    public List<BestallningListItem> findOngoingBestallningarForVardenhet(String vardenhetHsaId) {
        List<BestallningListItem> bestallningListItems = utredningRepository.findAllWithBestallningForVardenhetHsaId(vardenhetHsaId)
                .stream()
                .map(u -> BestallningListItem.from(u, utredningStateResolver.resolveStatus(u), "patientNamn-TODO"))
                .collect(Collectors.toList());

        pdlLogList(bestallningListItems, ActivityType.READ, ResourceType.RESOURCE_TYPE_FMU);
        return bestallningListItems;
    }

    @Override
    public ListBestallningFilter buildListBestallningFilter(String vardenhetHsaId) {
        List<IbVardgivare> distinctVardgivare = utredningRepository
                .findDistinctLandstingHsaIdByVardenhetHsaIdHavingBestallning(vardenhetHsaId)
                .stream()
                .map(vgHsaId -> new IbVardgivare(vgHsaId, organizationUnitService.getVardgivareInfo(vgHsaId).getNamn(), false))
                .distinct()
                .sorted(Comparator.comparing(IbVardgivare::getName))
                .collect(Collectors.toList());

        List<ListBestallningFilterStatus> statuses = Arrays.asList(ListBestallningFilterStatus.values());

        Map<ListBestallningFilterStatus, List<UtredningStatus>> statusMap = new HashMap<>();
        statusMap.put(ListBestallningFilterStatus.ALL, Arrays.asList(UtredningStatus.values()));
        statusMap.put(ListBestallningFilterStatus.KRAVER_ATGARD, Arrays.stream(UtredningStatus.values())
                .filter(us -> us.getNextActor() == Actor.VARDADMIN)
                .collect(toList()));
        statusMap.put(ListBestallningFilterStatus.VANTAR_ANNAN_AKTOR, Arrays.stream(UtredningStatus.values())
                .filter(us -> us.getNextActor() != Actor.VARDADMIN)
                .collect(toList()));

        return new ListBestallningFilter(distinctVardgivare, statuses, statusMap);
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
