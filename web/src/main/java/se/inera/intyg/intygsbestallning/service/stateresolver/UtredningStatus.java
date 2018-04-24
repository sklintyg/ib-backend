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

import static se.inera.intyg.intygsbestallning.service.stateresolver.Actor.FK;
import static se.inera.intyg.intygsbestallning.service.stateresolver.Actor.NONE;
import static se.inera.intyg.intygsbestallning.service.stateresolver.Actor.SAMORDNARE;
import static se.inera.intyg.intygsbestallning.service.stateresolver.Actor.UTREDARE;
import static se.inera.intyg.intygsbestallning.service.stateresolver.Actor.VARDADMIN;
import static se.inera.intyg.intygsbestallning.service.stateresolver.UtredningFas.FORFRAGAN;
import static se.inera.intyg.intygsbestallning.service.stateresolver.UtredningFas.KOMPLETTERING;
import static se.inera.intyg.intygsbestallning.service.stateresolver.UtredningFas.UTREDNING;

public enum UtredningStatus {

    // Statuses in FORFRAGAN phase
    FORFRAGAN_INKOMMEN(FORFRAGAN, SAMORDNARE),
    VANTAR_PA_SVAR(FORFRAGAN, VARDADMIN),
    TILLDELA_UTREDNING(FORFRAGAN, SAMORDNARE),
    TILLDELAD_VANTAR_PA_BESTALLNING(FORFRAGAN, FK),

    // Statuses in UTREDNING phase
    BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR(UTREDNING, FK),
    UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR(UTREDNING, FK),
    HANDLINGAR_MOTTAGNA_BOKA_BESOK(UTREDNING, VARDADMIN),
    UTREDNING_PAGAR(UTREDNING, UTREDARE),
    VANTAR_PA_BESLUT_OM_FORTSATT_UTREDNING(UTREDNING, FK),
    BESLUT_OM_FORTSATT_UTREDNING_TAGET(UTREDNING, VARDADMIN),
    UTLATANDE_SKICKAT(UTREDNING, FK),
    UTLATANDE_MOTTAGET(UTREDNING, FK),

    // Statuses in KOMPLETTERING phase
    KOMPLETTERINGSBEGARAN_MOTTAGEN_VANTAR_PA_FRAGESTALLNING(KOMPLETTERING, FK),
    KOMPLETTERANDE_FRAGESTALLNING_MOTTAGEN(KOMPLETTERING, UTREDARE),
    KOMPLETTERING_SKICKAD(KOMPLETTERING, FK),
    KOMPLETTERING_MOTTAGEN(KOMPLETTERING, FK),

    // Statuses in REDOVISA_TOLK phase
    REDOVISA_TOLK(UtredningFas.REDOVISA_TOLK, VARDADMIN),

    // Statuses in AVSLUTAD
    AVVISAD(UtredningFas.AVSLUTAD, NONE),
    AVBRUTEN(UtredningFas.AVSLUTAD, NONE),
    AVSLUTAD(UtredningFas.AVSLUTAD, NONE);

    private final UtredningFas utredningFas;
    private final Actor nextActor;

    UtredningStatus(UtredningFas utredningFas, Actor nextActor) {
        this.utredningFas = utredningFas;
        this.nextActor = nextActor;
    }

    public UtredningFas getUtredningFas() {
        return utredningFas;
    }

    public Actor getNextActor() {
        return nextActor;
    }
}
