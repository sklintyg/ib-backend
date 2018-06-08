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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.intygsbestallning.common.exception.IbErrorCodeEnum;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningFas;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
import se.inera.intyg.intygsbestallning.web.responder.dto.ReportKompletteringMottagenRequest;
import se.riv.intygsbestallning.certificate.order.requestmedicalcertificatesupplement.v1.RequestMedicalCertificateSupplementType;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@Transactional
public class KompletteringServiceImpl extends BaseUtredningService implements KompletteringService {

    private static final ImmutableList<UtredningStatus> GODKANDA_STATUSAR_KOMPLETTERING_MOTTAGEN = ImmutableList.of(
            UtredningStatus.KOMPLETTERINGSBEGARAN_MOTTAGEN_VANTAR_PA_FRAGESTALLNING,
            UtredningStatus.KOMPLETTERANDE_FRAGESTALLNING_MOTTAGEN,
            UtredningStatus.KOMPLETTERING_SKICKAD);

    private static Predicate<Utredning> isKorrektStatusForKompletteringMottagen() {
        return utr -> GODKANDA_STATUSAR_KOMPLETTERING_MOTTAGEN.contains(UtredningStatusResolver.resolveStaticStatus(utr));
    }

    private static Predicate<Intyg> isKomplettering() {
        return Intyg::isKomplettering;
    }

    @Override
    public long registerNewKomplettering(RequestMedicalCertificateSupplementType request) {
        try {
            Long assessmentId = Long.parseLong(request.getAssessmentId().getExtension());
            Optional<Utredning> utredningOptional = utredningRepository.findById(assessmentId);
            if (utredningOptional.isPresent()) {
                Utredning utredning = utredningOptional.get();

                // Verify state. Only utredning that has been bestalld and isn't finished etc can be ordered.
                UtredningStatus status = utredningStatusResolver.resolveStatus(utredning);
                if (status.getUtredningFas() == UtredningFas.AVSLUTAD || status.getUtredningFas() == UtredningFas.REDOVISA_TOLK) {
                    throw new IllegalStateException("Cannot add komplettering, utredning is in phase " + status.getId());
                }

                // Verify so there is at least one intyg entry that is eligible for komplettering
                if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.getSistaDatumKompletteringsbegaran() == null
                        || intyg.getSistaDatumKompletteringsbegaran().isAfter(LocalDateTime.now()))) {

                    Intyg komplt = Intyg.IntygBuilder.anIntyg()
                            .withKomplettering(true)
                            .withSistaDatum(getSistaDatum(request.getLastDateForSupplementReceival()))
                            .build();

                    utredning.getIntygList().add(komplt);
                    utredning.getHandelseList().add(HandelseUtil.createKompletteringBegard());
                    utredningRepository.saveUtredning(utredning);
                    utredningRepository.flush();

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

    /*
         lastDateForSupplementReceival might be null or empty string and if so,
         return null, otherwise try to parse the string as a valid date.
     */
    private LocalDateTime getSistaDatum(String lastDateForSupplementReceival) {
        if (StringUtils.isBlank(lastDateForSupplementReceival)) {
            return null;
        }
        try {
            return LocalDate.parse(lastDateForSupplementReceival, DateTimeFormatter.ISO_DATE).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

}
