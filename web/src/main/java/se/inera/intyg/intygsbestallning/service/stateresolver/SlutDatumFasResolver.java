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
package se.inera.intyg.intygsbestallning.service.stateresolver;

import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class SlutDatumFasResolver {

    private SlutDatumFasResolver() {

    }

    /*
     * Om utredningsfas = utredning är slutdatum= Utredning.intyg.sista datum för mottagning
     * Om utredningsfas = komplettering är slutdatum = Utredning.kompletteringsbegäran.komplettering.sista datum för
     * mottagning
     */
    public static String resolveSlutDatumFas(Utredning utredning, UtredningStatus utredningStatus) {
        switch (utredningStatus.getUtredningFas()) {
        case UTREDNING:
            return utredning.getIntygList().stream()
                    .filter(i -> !i.isKomplettering())
                    .findAny()
                    .map(Intyg::getSistaDatum)
                    .map(datum -> datum.format(DateTimeFormatter.ISO_DATE))
                    .orElseThrow(IllegalStateException::new);
        case KOMPLETTERING:
            return utredning.getIntygList().stream()
                    .map(Intyg::getSistaDatum)
                    .max(LocalDateTime::compareTo)
                    .map(datum -> datum.format(DateTimeFormatter.ISO_DATE))
                    .orElseThrow(IllegalStateException::new);
        default:
            return null;
        }
    }
}
