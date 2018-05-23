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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.stateresolver.Actor;
import se.inera.intyg.intygsbestallning.service.stateresolver.SlutDatumFasResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatusResolver;

import java.time.LocalDateTime;

@Component
public class BestallningListItemFactory {

    @Value("${ib.utredning.paminnelse.dagar}")
    private long paminnelseDagar;

    private UtredningStatusResolver utredningStatusResolver = new UtredningStatusResolver();

    public BestallningListItem from(Utredning utredning, Actor actorInThisContext) {

        UtredningStatus utredningStatus = utredningStatusResolver.resolveStatus(utredning);

        return BestallningListItem.BestallningListItemBuilder.anBestallningListItem()
                .withFas(utredningStatus.getUtredningFas())
                .withPatientId(utredning.getInvanare().getPersonId())
                .withPatientNamn(null)
                .withSlutdatumFas(SlutDatumFasResolver.resolveSlutDatumFas(utredning, utredningStatus))
                .withSlutdatumPasserat(LocalDateTime.now().isAfter(utredning.getIntygList().stream()
                        .filter(i -> !i.isKomplettering())
                        .findFirst()
                        .map(Intyg::getSistaDatum)
                        .orElseThrow(IllegalStateException::new)))
                .withSlutdatumPaVagPasseras(resolveSlutDatumPaVagPasseras(utredning, utredningStatus))
                .withStatus(utredningStatus)
                .withNextActor(utredningStatus.getNextActor().name())
                .withKraverAtgard(actorInThisContext == utredningStatus.getNextActor())
                .withUtredningsId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp().name())
                .withVardgivareHsaId(utredning.getExternForfragan().getLandstingHsaId())
                .withVardgivareNamn(null)
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
     * där slutdatum avser slutdatum för utredningen (intyg.sista datum för mottagning) om ingen kompletteringsbegärans har
     * mottagits, annars slutdatum för kompletteringsbegäran (komplettering.sista datum för mottagning)
     */
    private boolean resolveSlutDatumPaVagPasseras(Utredning utredning, UtredningStatus utredningStatus) {
        LocalDateTime timestamp;
        switch (utredningStatus.getUtredningFas()) {
            case KOMPLETTERING:
                timestamp = utredning.getIntygList().stream()
                        .filter(Intyg::isKomplettering)
                        .map(Intyg::getSistaDatum)
                        .max(LocalDateTime::compareTo)
                        .orElseThrow(IllegalStateException::new);
                break;
            case UTREDNING:
                timestamp = utredning.getIntygList().stream()
                        .filter(i -> !i.isKomplettering())
                        .map(Intyg::getSistaDatum)
                        .findAny()
                        .orElseThrow(IllegalStateException::new);
                break;
            default:
                return false;
        }
        return LocalDateTime.now().isBefore(timestamp)
                && LocalDateTime.now().isAfter(timestamp.minusDays(paminnelseDagar));
    }
}
