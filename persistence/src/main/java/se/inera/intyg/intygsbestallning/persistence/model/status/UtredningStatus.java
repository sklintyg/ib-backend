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
package se.inera.intyg.intygsbestallning.persistence.model.status;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import static se.inera.intyg.intygsbestallning.persistence.model.status.Actor.FK;
import static se.inera.intyg.intygsbestallning.persistence.model.status.Actor.NONE;
import static se.inera.intyg.intygsbestallning.persistence.model.status.Actor.SAMORDNARE;
import static se.inera.intyg.intygsbestallning.persistence.model.status.Actor.UTREDARE;
import static se.inera.intyg.intygsbestallning.persistence.model.status.Actor.VARDADMIN;
import static se.inera.intyg.intygsbestallning.persistence.model.status.UtredningFas.FORFRAGAN;
import static se.inera.intyg.intygsbestallning.persistence.model.status.UtredningFas.KOMPLETTERING;
import static se.inera.intyg.intygsbestallning.persistence.model.status.UtredningFas.UTREDNING;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum UtredningStatus implements SortableLabel {

    // Statuses in FORFRAGAN phase
    FORFRAGAN_INKOMMEN("Förfrågan inkommen", FORFRAGAN, SAMORDNARE),
    VANTAR_PA_SVAR("Väntar på svar", FORFRAGAN, VARDADMIN),
    TILLDELA_UTREDNING("Tilldela utredning och acceptera förfrågan", FORFRAGAN, SAMORDNARE),
    TILLDELAD_VANTAR_PA_BESTALLNING("Tilldelad, väntar på beställning", FORFRAGAN, FK),

    // Statuses in UTREDNING phase
    BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR("Beställning mottagen, väntar på handlingar", UTREDNING, FK),
    UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR("Uppdaterad beställning, väntar på handlingar", UTREDNING, FK),
    HANDLINGAR_MOTTAGNA_BOKA_BESOK("Handlingar mottagna, boka besök", UTREDNING, VARDADMIN),
    UTREDNING_PAGAR("Utredning pågår", UTREDNING, UTREDARE),
    AVVIKELSE_MOTTAGEN("Avvikelse mottagen", UTREDNING, VARDADMIN),
    UTLATANDE_SKICKAT("Utlåtande skickat", UTREDNING, FK),
    UTLATANDE_MOTTAGET("Utlåtande mottaget", UTREDNING, FK),

    // Statuses in KOMPLETTERING phase
    KOMPLETTERINGSBEGARAN_MOTTAGEN_VANTAR_PA_FRAGESTALLNING("Kompletteringsbegäran mottagen, väntar på frågeställning", KOMPLETTERING, FK),
    KOMPLETTERANDE_FRAGESTALLNING_MOTTAGEN("Kompletterande frågeställning mottagen", KOMPLETTERING, UTREDARE),
    KOMPLETTERING_SKICKAD("Komplettering skickad", KOMPLETTERING, FK),
    KOMPLETTERING_MOTTAGEN("Komplettering mottagen", KOMPLETTERING, FK),

    // Statuses in REDOVISA_BESOK phase
    REDOVISA_BESOK("Redovisa besök", UtredningFas.REDOVISA_BESOK, VARDADMIN),

    // Statuses in AVSLUTAD
    AVVISAD("Avvisad", UtredningFas.AVSLUTAD, NONE),
    AVBRUTEN("Avbruten", UtredningFas.AVSLUTAD, NONE),
    AVSLUTAD("Avslutad", UtredningFas.AVSLUTAD, NONE),

    // Tillfälligt tillstånd som skall tas bort efter utveckling är klara med datamodell och state resolver.

    INVALID("Ogiltigt tillstånd", UtredningFas.AVSLUTAD, NONE);
    private final String id;
    private final String label;
    private final UtredningFas utredningFas;
    private final Actor nextActor;

    UtredningStatus(String label, UtredningFas utredningFas, Actor nextActor) {
        this.id = this.name();
        this.label = label;
        this.utredningFas = utredningFas;
        this.nextActor = nextActor;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @JsonIgnore
    public UtredningFas getUtredningFas() {
        return utredningFas;
    }

    @JsonIgnore
    public Actor getNextActor() {
        return nextActor;
    }
}
