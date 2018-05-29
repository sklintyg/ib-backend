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

import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;

public class BesokStatusResolver {

    public BesokStatus resolveStatus(Besok besok) {
        return resolveStaticStatus(besok);
    }

    public static BesokStatus resolveStaticStatus(Besok besok) {
        if (besok.getAvvikelse() != null && besok.getAvvikelse().getInvanareUteblev()) {
            return BesokStatus.PATIENT_UTEBLEV;
        }
        if (besok.getHandelseList().stream().map(Handelse::getHandelseTyp).anyMatch(HandelseTyp.AVBOKAT_BESOK::equals)) {
            return BesokStatus.AVBOKAT;
        }
        if (besok.getHandelseList().stream().map(Handelse::getHandelseTyp).anyMatch(HandelseTyp.AVVIKELSE_RAPPORTERAD::equals)) {
            return BesokStatus.AVVIKELSE_RAPPORTERAD;
        }
        if (besok.getHandelseList().stream().map(Handelse::getHandelseTyp).anyMatch(HandelseTyp.AVVIKELSE_MOTTAGEN::equals)) {
            return BesokStatus.AVVIKELSE_MOTTAGEN;
        }
        if (besok.getHandelseList().stream().map(Handelse::getHandelseTyp).anyMatch(HandelseTyp.OMBOKAT_BESOK::equals)) {
            return BesokStatus.OMBOKAT;
        }
        return BesokStatus.BOKAT;
    }
}
