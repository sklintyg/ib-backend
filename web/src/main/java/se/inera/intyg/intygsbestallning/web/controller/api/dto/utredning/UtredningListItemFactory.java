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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.Actor;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningFas;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.stateresolver.SlutDatumFasResolver;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Component
public class UtredningListItemFactory {

    @Value("${ib.externforfragan.paminnelse.arbetsdagar}")
    private int externforfraganPaminnelseArbetsdagar;

    @Value("${ib.utredning.paminnelse.arbetsdagar}")
    private int utredningPaminnelseArbetsdagar;

    @Autowired
    private BusinessDaysBean businessDays;

    public UtredningListItem from(Utredning utredning) {

        UtredningStatus utredningStatus = utredning.getStatus(); //utredningStatusResolver.resolveStatus(utredning);

        Optional<LocalDateTime> slutdatumFas = SlutDatumFasResolver.resolveSlutDatumFas(utredning, utredningStatus);
        return UtredningListItem.UtredningListItemBuilder.anUtredningListItem()
                .withFas(utredningStatus.getUtredningFas())
                .withSlutdatumFas(slutdatumFas.map(DateTimeFormatter.ISO_DATE::format).orElse(null))
                .withSlutdatumFasPaVagPasseras(
                        resolveSlutdatumFasPaVagPasseras(slutdatumFas, businessDays, utredningStatus))
                .withSlutdatumFasPasserat(resolveSlutdatumFasPasserat(slutdatumFas, utredningStatus))
                .withStatus(utredningStatus)
                .withKraverAtgard(utredningStatus.getNextActor() == Actor.SAMORDNARE)
                .withUtredningsId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp())
                .withVardenhetHsaId(resolveTilldeladVardenhetHsaId(utredning))

                // Is set later after HSA lookup.
                .withVardenhetNamn("")

                // Is used in xlsx export
                .withUtredning(utredning)
                .build();
    }

    private String resolveTilldeladVardenhetHsaId(Utredning utredning) {
        if (utredning.getExternForfragan().isPresent()) {
            Optional<String> optionalVardenhetHsaId = utredning.getExternForfragan().get().getInternForfraganList().stream()
                    .filter(intf -> nonNull(intf.getTilldeladDatum()))
                    .map(InternForfragan::getVardenhetHsaId)
                    .findFirst();

            return optionalVardenhetHsaId.orElse(null);
        }
        return null;
    }

    private boolean resolveSlutdatumFasPasserat(Optional<LocalDateTime> slutdatumFas, UtredningStatus utredningStatus) {
        if (!slutdatumFas.isPresent() || utredningStatus.getUtredningFas() == UtredningFas.REDOVISA_BESOK) {
            return false;
        }
        return LocalDate.now().compareTo(slutdatumFas.get().toLocalDate()) > 0;
    }

    private boolean resolveSlutdatumFasPaVagPasseras(Optional<LocalDateTime> slutdatumFas, BusinessDaysBean businessDays, UtredningStatus
            utredningStatus) {
        if (!slutdatumFas.isPresent()) {
            return false;
        }

        int paminnelseArbetsdagar = utredningPaminnelseArbetsdagar;
        if (utredningStatus.getUtredningFas() == UtredningFas.FORFRAGAN) {
            paminnelseArbetsdagar = externforfraganPaminnelseArbetsdagar;
        }

        // Om datumet redan passerats skall vi ej flagga.
        if (slutdatumFas.get().toLocalDate().compareTo(LocalDate.now()) < 0) {
            return false;
        }
        return businessDays.daysBetween(LocalDate.now(), slutdatumFas.get().toLocalDate(), false) < paminnelseArbetsdagar;
    }
}
