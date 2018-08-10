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

import org.springframework.stereotype.Component;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.stateresolver.ErsattsResolver;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import static java.util.Objects.nonNull;
import static se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.AvslutadUtredningListItem.AvslutadUtredningListItemBuilder.anAvslutadUtredningListItem;

@Component
public class AvslutadUtredningListItemFactory {
    private BusinessDaysBean businessDays;

    public AvslutadUtredningListItemFactory(final BusinessDaysBean businessDays) {
        this.businessDays = businessDays;
    }

    public AvslutadUtredningListItem from(Utredning utredning) {

        return anAvslutadUtredningListItem()
                .withUtredningsId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp())
                .withStatus(utredning.getStatus())
                .withVardenhetHsaId(utredning.getBestallning().map(Bestallning::getTilldeladVardenhetHsaId).orElse(null))
                .withVardenhetNamn("") // Enriched later
                .withAvslutsDatum(resolveAvslutsDatum(utredning))
                .withErsatts(ErsattsResolver.resolveUtredningErsatts(utredning, businessDays))
                .withFakturerad(nonNull(utredning.getBetalning()) ? utredning.getBetalning().getFakturaId() : null)
                .withBetald(nonNull(utredning.getBetalning()) ? utredning.getBetalning().getBetalningsId() : null)
                .withUtbetaldFk(nonNull(utredning.getBetalning()) ? utredning.getBetalning().getUtbetalningsId() : null)
                .withUtredning(utredning) // Is used in xlsx export
                .build();
    }

    private String resolveAvslutsDatum(Utredning utredning) {
        if (utredning.getAvbrutenDatum() != null) {
            return utredning.getAvbrutenDatum().format(DateTimeFormatter.ISO_DATE);
        }

        if (utredning.getExternForfragan().isPresent() && utredning.getExternForfragan().get().getAvvisatDatum() != null) {
            return utredning.getExternForfragan().get().getAvvisatDatum().format(DateTimeFormatter.ISO_DATE);
        }

        // Find the highest mottaget datum.
        return utredning.getIntygList().stream()
                .filter(intyg -> intyg.getMottagetDatum() != null)
                .max(Comparator.comparing(Intyg::getMottagetDatum))
                .map(intyg -> intyg.getMottagetDatum().format(DateTimeFormatter.ISO_DATE))
                .orElse("Fixme");
    }
}
