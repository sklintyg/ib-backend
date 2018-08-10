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

import org.springframework.stereotype.Component;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.stateresolver.ErsattsResolver;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import static java.util.Objects.nonNull;

@Component
public class AvslutadBestallningListItemFactory {

    private BusinessDaysBean businessDays;

    public AvslutadBestallningListItemFactory(final BusinessDaysBean businessDays) {
        this.businessDays = businessDays;
    }

    public AvslutadBestallningListItem from(Utredning utredning) {

        return AvslutadBestallningListItem.AvslutadBestallningListItemBuilder.anAvslutadBestallningListItem()
                .withUtredningsId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp())
                .withStatus(utredning.getStatus())
                .withVardgivareHsaId(utredning.getExternForfragan().map(ExternForfragan::getLandstingHsaId).orElse(null))
                .withVardgivareNamn("Enriched later")
                .withAvslutsDatum(resolveAvslutsDatum(utredning))
                .withErsatts(ErsattsResolver.resolveUtredningErsatts(utredning, businessDays))
                .withFakturerad(nonNull(utredning.getBetalning()) ? utredning.getBetalning().getFakturaId() : null)
                .withUtbetald(nonNull(utredning.getBetalning()) ? utredning.getBetalning().getBetalningsId() : null)
                .build();
    }

    private String resolveAvslutsDatum(Utredning utredning) {
        if (utredning.getAvbrutenDatum() != null) {
            return utredning.getAvbrutenDatum().format(DateTimeFormatter.ISO_DATE);
        }

        // Find the highest mottaget datum.
        return utredning.getIntygList().stream()
                .filter(intyg -> intyg.getMottagetDatum() != null)
                .max(Comparator.comparing(Intyg::getMottagetDatum))
                .map(intyg -> intyg.getMottagetDatum().format(DateTimeFormatter.ISO_DATE))
                .orElse("Fixme");
    }
}
