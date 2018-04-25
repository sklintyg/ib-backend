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
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.Handlaggare;
import se.inera.intyg.intygsbestallning.persistence.model.Invanare;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.utredning.dto.Bestallare;
import se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.ForfraganListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetForfraganResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.GetUtredningResponse;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.UtredningListItem;
import se.riv.intygsbestallning.certificate.order.requesthealthcareperformerforassessment.v1.RequestHealthcarePerformerForAssessmentType;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static se.inera.intyg.intygsbestallning.persistence.model.Bestallning.BestallningBuilder.aBestallning;
import static se.inera.intyg.intygsbestallning.persistence.model.Handlaggare.HandlaggareBuilder.aHandlaggare;
import static se.inera.intyg.intygsbestallning.persistence.model.Handling.HandlingBuilder.aHandling;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;

@Service
@Transactional
public class UtredningServiceImpl implements UtredningService {

    private static final Logger LOG = LoggerFactory.getLogger(UtredningService.class);

    @Autowired
    private UtredningRepository utredningRepository;

    @Override public Utredning registerNewUtredning(RequestHealthcarePerformerForAssessmentType req) {
        return null;
    }

    @Override public List<UtredningListItem> findUtredningarByVardgivareHsaId(String vardgivareHsaId) {
        return null;
    }

    @Override
    public GetUtredningResponse getUtredning(String utredningId, String landstingHsaId) {
        Utredning utredning = utredningRepository.findById(utredningId).orElseThrow(
                () -> new IbNotFoundException("Utredning with assessmentId '" + utredningId + "' does not exist."));

        if (!Objects.equals(utredning.getExternForfragan().getLandstingHsaId(), landstingHsaId)) {
            throw new IbNotFoundException(
                    "Utredning with assessmentId '" + utredningId + "' does not have external förfrågan for landsting with id '"
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
                    "Utredning with id '" + utredningId + "' does not have an InternFörfrågan for enhet with id '" + vardenhetHsaId + "'");
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
