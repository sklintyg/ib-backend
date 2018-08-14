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

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;
import se.inera.intyg.intygsbestallning.service.notifiering.send.NotifieringSendService;
import se.riv.intygsbestallning.certificate.order.requestsupplement.v1.RequestSupplementType;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningFas;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
import se.inera.intyg.intygsbestallning.service.pdl.LogService;
import se.inera.intyg.intygsbestallning.service.pdl.dto.PdlLogType;
import se.inera.intyg.intygsbestallning.service.pdl.dto.UtredningPdlLoggable;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.komplettering.RegisterFragestallningMottagenRequest;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.komplettering.RegisterSkickadKompletteringRequest;
import se.inera.intyg.intygsbestallning.web.responder.dto.ReportKompletteringMottagenRequest;

@Service
@Transactional
public class KompletteringServiceImpl extends BaseUtredningService implements KompletteringService {

    @Autowired
    private LogService logService;

    private static final ImmutableList<UtredningStatus> GODKANDA_STATUSAR_KOMPLETTERING_MOTTAGEN = ImmutableList.of(
            UtredningStatus.KOMPLETTERINGSBEGARAN_MOTTAGEN_VANTAR_PA_FRAGESTALLNING,
            UtredningStatus.KOMPLETTERANDE_FRAGESTALLNING_MOTTAGEN,
            UtredningStatus.KOMPLETTERING_SKICKAD);

    // RequestSupplement can only be called in UTLATANDE_MOTTAGET or KOMPLETTERING_MOTTAGEN (FMU-G001 Statusflöde för utredning)
    private static final ImmutableList<UtredningStatus> GODKANDA_STATUSAR_REQUEST_SUPPLEMENT = ImmutableList.of(
            UtredningStatus.UTLATANDE_MOTTAGET,
            UtredningStatus.KOMPLETTERING_MOTTAGEN);

    @Autowired
    private NotifieringSendService notifieringSendService;

    private static Predicate<Utredning> isKorrektStatusForKompletteringMottagen() {
        return utr -> GODKANDA_STATUSAR_KOMPLETTERING_MOTTAGEN.contains(UtredningStatusResolver.resolveStaticStatus(utr));
    }

    private static Predicate<Intyg> isKomplettering() {
        return Intyg::isKomplettering;
    }

    @Override
    public long registerNewKomplettering(RequestSupplementType request) {
        try {
            Long assessmentId = Long.parseLong(request.getAssessmentId().getExtension());
            Optional<Utredning> utredningOptional = utredningRepository.findById(assessmentId);
            if (utredningOptional.isPresent()) {
                Utredning utredning = utredningOptional.get();

                // Verify state.
                if (!GODKANDA_STATUSAR_REQUEST_SUPPLEMENT.contains(utredning.getStatus())) {
                    throw new IllegalStateException("Cannot request supplement, utredning is in state " + utredning.getStatus().getId());
                }

                // Verify so there is at least one intyg entry that is eligible for komplettering
                if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.getSistaDatumKompletteringsbegaran() == null
                        || intyg.getSistaDatumKompletteringsbegaran().isAfter(LocalDateTime.now()))) {

                    Intyg komplt = Intyg.IntygBuilder.anIntyg()
                            .withKomplettering(true)
                            .withSistaDatum(SchemaDateUtil.toLocalDateTimeFromDateType(request.getLastDateForSupplementReceival()))
                            .build();

                    utredning.getIntygList().add(komplt);
                    utredning.getHandelseList().add(HandelseUtil.createKompletteringBegard());
                    utredningRepository.saveUtredning(utredning);
                    utredningRepository.flush();

                    notifieringSendService.notifieraVardenhetKompletteringBegard(utredning);

                    // This is a slightly awkward way to get the ID of the newly persisted komplettering.
                    Optional<Long> kompletteringsId = utredningRepository.findNewestKompletteringOnUtredning(assessmentId);
                    if (kompletteringsId.isPresent()) {
                        return kompletteringsId.get();
                    } else {
                        throw new IllegalStateException("Could not resolve latest kompletterings-id on Utredning.");
                    }

                } else {
                    throw new IllegalStateException(
                            "Cannot add komplettering, utredning has no previous intyg eligible for komplettering. "
                                    + "Either there is already an outstanding komplettering or any existing intyg or "
                                    + "kompletteringar has passed their last sistaDatumKompletteringsbegaran");
                }
            } else {
                throw new IllegalStateException("No utredning matching ID " + request.getAssessmentId().getExtension() + " found.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void reportKompletteringMottagen(final ReportKompletteringMottagenRequest request) {

        Optional<Utredning> optionalUtredning = utredningRepository.findById(request.getUtredningId());

        optionalUtredning
                .orElseThrow(() -> new IbNotFoundException(
                        MessageFormat.format("Utredning with id {0} was not found.", request.getUtredningId())));

        optionalUtredning.filter(isKorrektStatusForKompletteringMottagen())
                .orElseThrow(() -> new IbServiceException(
                        IbErrorCodeEnum.BAD_STATE,
                        MessageFormat.format("Utredning with id {0} is in an incorrect state.", request.getUtredningId())));

        Intyg intyg = optionalUtredning.get().getIntygList().stream()
                .filter(i -> i.getId().equals(request.getKompletteringId()))
                .filter(isKomplettering())
                .max(Comparator.comparing(Intyg::getSkickatDatum))
                .orElseThrow(() -> new IbNotFoundException(
                        MessageFormat.format("Utredning with id {0} is missing a kompletterande intyg with id {1}",
                                request.getKompletteringId(),
                                request.getKompletteringId())));

        intyg.setMottagetDatum(request.getMottagetDatum());
        intyg.setSistaDatumKompletteringsbegaran(request.getSistaKompletteringDatum());

        optionalUtredning.get().getHandelseList().add(HandelseUtil.createKompletteringMottagen(request.getMottagetDatum()));

        utredningRepository.saveUtredning(optionalUtredning.get());
    }

    @Override
    @Transactional
    public void registerFragestallningMottagen(Long utredningId, RegisterFragestallningMottagenRequest request) {
        request.validate();

        final Utredning utredning = utredningRepository.findById(utredningId)
                .orElseThrow(() -> new IbNotFoundException("Utredning with id '" + utredningId + "' does not exist."));

        if (utredning.getStatus() != UtredningStatus.KOMPLETTERINGSBEGARAN_MOTTAGEN_VANTAR_PA_FRAGESTALLNING) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, MessageFormat.format(
                    "Utredning with id {0} is in an incorrect state {1}", utredning.getUtredningId(), utredning.getStatus().getId()));
        }

        checkUserVardenhetTilldeladToBestallning(utredning);

        Intyg intyg = utredning.getIntygList().stream()
                .filter(i -> i.isKomplettering()
                        && i.getFragestallningMottagenDatum() == null
                        && i.getSkickatDatum() == null
                        && i.getSistaDatum().isAfter(LocalDateTime.now()))
                .max(Comparator.comparing(Intyg::getId))
                .orElseThrow(() -> new IbServiceException(IbErrorCodeEnum.BAD_STATE, MessageFormat.format(
                        "Utredning with id {0} is missing a kompletterande intyg waiting for fragestallning", utredningId)));

        intyg.setFragestallningMottagenDatum(request.getFragestallningMottagenDatum().atStartOfDay());

        utredning.getHandelseList().add(HandelseUtil.createKompletterandeFragestallningMottagen(request.getFragestallningMottagenDatum(),
                userService.getUser().getNamn()));

        utredningRepository.saveUtredning(utredning);

        logService.log(new UtredningPdlLoggable(utredning), PdlLogType.UTREDNING_UPPDATERAD);
    }

    @Override
    public void registerSkickadKomplettering(Long utredningId, RegisterSkickadKompletteringRequest request) {
        request.validate();

        final Utredning utredning = utredningRepository.findById(utredningId)
                .orElseThrow(() -> new IbNotFoundException("Utredning with id '" + utredningId + "' does not exist."));

        if (utredning.getStatus().getUtredningFas() != UtredningFas.KOMPLETTERING
                || utredning.getStatus() == UtredningStatus.KOMPLETTERING_MOTTAGEN
                || utredning.getStatus() == UtredningStatus.KOMPLETTERING_SKICKAD) {
            throw new IbServiceException(IbErrorCodeEnum.BAD_STATE, MessageFormat.format(
                    "Utredning with id {0} is in an incorrect state {1}", utredning.getUtredningId(), utredning.getStatus().getId()));
        }

        checkUserVardenhetTilldeladToBestallning(utredning);

        Intyg intyg = utredning.getIntygList().stream()
                .filter(i -> i.isKomplettering()
                        && i.getSkickatDatum() == null
                        && i.getSistaDatum().isAfter(LocalDateTime.now()))
                .max(Comparator.comparing(Intyg::getId))
                .orElseThrow(() -> new IbServiceException(IbErrorCodeEnum.BAD_STATE, MessageFormat.format(
                        "Utredning with id {0} is missing a kompletterande intyg ready to be sent", utredningId)));

        intyg.setSkickatDatum(request.getKompletteringSkickadDatum().atStartOfDay());

        utredning.getHandelseList().add(HandelseUtil.createKompletteringSkickad(request.getKompletteringSkickadDatum(),
                userService.getUser().getNamn()));

        utredningRepository.saveUtredning(utredning);

        logService.log(new UtredningPdlLoggable(utredning), PdlLogType.UTREDNING_UPPDATERAD);
    }

}
