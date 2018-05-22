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
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.stateresolver.SlutDatumFasResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatusResolver;

import java.util.Optional;

import static java.util.Objects.nonNull;

@Component
public class UtredningListItemFactory {

    private UtredningStatusResolver utredningStatusResolver = new UtredningStatusResolver();

    public UtredningListItem from(Utredning utredning) {

        UtredningStatus utredningStatus = utredningStatusResolver.resolveStatus(utredning);

        return UtredningListItem.UtredningListItemBuilder.anUtredningListItem()
                .withFas(utredningStatus.getUtredningFas())
                .withSlutdatumFas(SlutDatumFasResolver.resolveSlutDatumFas(utredning, utredningStatus))
                .withStatus(utredningStatus)
                .withUtredningsId(utredning.getUtredningId())
                .withUtredningsTyp(utredning.getUtredningsTyp().name())
                .withVardenhetHsaId(resolveTilldeladVardenhetHsaId(utredning))
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
}
