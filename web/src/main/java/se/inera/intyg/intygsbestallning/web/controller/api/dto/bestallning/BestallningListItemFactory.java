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
package se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.Actor;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.stateresolver.SlutDatumFasResolver;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;

@Component
public class BestallningListItemFactory {

    @Value("${ib.utredning.paminnelse.arbetsdagar}")
    private int paminnelseDagar;

    @Autowired
    private BusinessDaysBean businessDays;

    public BestallningListItem from(Utredning utredning, Actor actorInThisContext) {

        UtredningStatus utredningStatus = utredning.getStatus();

        Optional<LocalDateTime> slutdatumFas = SlutDatumFasResolver.resolveSlutDatumFas(utredning, utredningStatus);

        return BestallningListItem.BestallningListItemBuilder.anBestallningListItem()
                .withFas(utredningStatus.getUtredningFas())
                .withPatientId(utredning.getInvanare().getPersonId())
                .withPatientNamn(null)
                .withSlutdatumFas(slutdatumFas.map(DateTimeFormatter.ISO_DATE::format).orElse(null))
                .withSlutdatumPasserat(slutdatumFas.map(date -> LocalDate.now().isAfter(date.toLocalDate())).orElse(false))
                .withSlutdatumPaVagPasseras(resolveSlutDatumPaVagPasseras(slutdatumFas))
                .withStatus(utredningStatus)
                .withNextActor(utredningStatus.getNextActor().name())
                .withKraverAtgard(actorInThisContext == utredningStatus.getNextActor())
                .withUtredningsId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp())
                .withVardgivareHsaId(utredning.getExternForfragan().map(ExternForfragan::getLandstingHsaId).orElse(null))
                .withVardgivareNamn(null)
                .withUtredning(utredning) // Is used in xlsx export
                .build();
    }

    /**
     * Systemet ska signalera när en utredning eller komplettering snart kommer att passera sitt slutdatum.
     * <p>
     * Antalet arbetsdagar innan utredningens slutdatum som påminnelsen ska ske i systemet måste vara konfigurerbart
     * (UTREDNING_PAMINNELSE_DAGAR). Default är UTREDNING_PAMINNELSE_DAGAR= 5.
     * <p>
     * Systemet varnar om:
     * <p>
     * Utredningsfas inte är Redovisa tolk (se FMU-G001 Statusflöde för utredning)
     * Idag > (slutdatum - UTREDNING_PAMINNELSE_DAGAR arbetsdagar)
     * Idag <= slutdatum
     * där slutdatum avser slutdatum för utredningen (intyg.sista datum för mottagning) om ingen kompletteringsbegäran har
     * mottagits, annars slutdatum för kompletteringsbegäran (komplettering.sista datum för mottagning)
     */
    private boolean resolveSlutDatumPaVagPasseras(Optional<LocalDateTime> slutdatumFas) {
        if (!slutdatumFas.isPresent()) {
            return false;
        }
        LocalDate date = slutdatumFas.get().toLocalDate();
        return (LocalDate.now().isBefore(date) || LocalDate.now().isEqual(date))
                && LocalDate.now().isAfter(businessDays.minusBusinessDays(date, paminnelseDagar));
    }
}
