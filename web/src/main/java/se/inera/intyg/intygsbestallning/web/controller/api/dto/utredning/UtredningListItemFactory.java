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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.stateresolver.Actor;
import se.inera.intyg.intygsbestallning.service.stateresolver.SlutDatumFasResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningFas;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatusResolver;
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

    private BusinessDaysBean businessDays;

    private UtredningStatusResolver utredningStatusResolver = new UtredningStatusResolver();

    public UtredningListItemFactory(final BusinessDaysBean businessDays) {
        this.businessDays = businessDays;
    }

    public UtredningListItem from(Utredning utredning) {

        UtredningStatus utredningStatus = utredningStatusResolver.resolveStatus(utredning);

        LocalDateTime slutdatumFas = SlutDatumFasResolver.resolveSlutDatumFas(utredning, utredningStatus);
        return UtredningListItem.UtredningListItemBuilder.anUtredningListItem()
                .withFas(utredningStatus.getUtredningFas())
                .withSlutdatumFas(nonNull(slutdatumFas) ? slutdatumFas.format(DateTimeFormatter.ISO_DATE) : null)
                .withSlutdatumFasPaVagPasseras(
                        resolveSlutdatumFasPaVagPasseras(slutdatumFas, businessDays, utredningStatus))
                .withSlutdatumFasPasserat(resolveSlutdatumFasPasserat(slutdatumFas, utredningStatus))
                .withStatus(utredningStatus)
                .withKraverAtgard(utredningStatus.getNextActor() == Actor.SAMORDNARE)
                .withUtredningsId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp().name())
                .withVardenhetHsaId(resolveTilldeladVardenhetHsaId(utredning))

                // Is set later after HSA lookup.
                .withVardenhetNamn("")
                .build();
    }

    private String resolveTilldeladVardenhetHsaId(Utredning utredning) {
        if (utredning.getExternForfragan() != null) {
            Optional<String> optionalVardenhetHsaId = utredning.getExternForfragan().getInternForfraganList().stream()
                    .filter(intf -> nonNull(intf.getTilldeladDatum()))
                    .map(InternForfragan::getVardenhetHsaId)
                    .findFirst();

            return optionalVardenhetHsaId.orElse(null);
        }
        return null;
    }

    private boolean resolveSlutdatumFasPasserat(LocalDateTime slutdatumFas, UtredningStatus utredningStatus) {
        if (slutdatumFas == null || utredningStatus.getUtredningFas() == UtredningFas.REDOVISA_TOLK) {
            return false;
        }
        return LocalDate.now().compareTo(slutdatumFas.toLocalDate()) > 0;
    }

    private boolean resolveSlutdatumFasPaVagPasseras(LocalDateTime slutdatumFas, BusinessDaysBean businessDays, UtredningStatus
            utredningStatus) {
        if (slutdatumFas == null) {
            return false;
        }

        int paminnelseArbetsdagar = utredningPaminnelseArbetsdagar;
        if (utredningStatus.getUtredningFas() == UtredningFas.FORFRAGAN) {
            paminnelseArbetsdagar = externforfraganPaminnelseArbetsdagar;
        }

        // Om datumet redan passerats skall vi ej flagga.
        if (slutdatumFas.toLocalDate().compareTo(LocalDate.now()) < 0) {
            return false;
        }
        return businessDays.daysBetween(LocalDate.now(), slutdatumFas.toLocalDate(), false) < paminnelseArbetsdagar;
    }
}
