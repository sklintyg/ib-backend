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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.handelse.HandelseUtil;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningFas;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.riv.intygsbestallning.certificate.order.requestmedicalcertificatesupplement.v1.RequestMedicalCertificateSupplementType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class KompletteringServiceImpl extends BaseUtredningService implements KompletteringService {

    @Override
    @Transactional
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
                            .withSistaDatum(LocalDate.parse(request.getLastDateForSupplementReceival(), DateTimeFormatter.ISO_DATE)
                                    .atStartOfDay())
                            .build();
                    utredning.getIntygList().add(komplt);
                    utredning.getHandelseList().add(HandelseUtil.createKompletteringBegard());
                    utredningRepository.save(utredning);
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
}
