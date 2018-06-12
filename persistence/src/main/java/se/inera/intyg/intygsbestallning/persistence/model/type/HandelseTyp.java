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
package se.inera.intyg.intygsbestallning.persistence.model.type;

import com.fasterxml.jackson.annotation.JsonFormat;

public enum HandelseTyp {
    EXTERNFORFRAGAN_MOTTAGEN(Typ.FORFRAGAN),
    INTERNFORFRAGAN_SKICKAD(Typ.FORFRAGAN),
    INTERNFORFRAGAN_BESVARAD(Typ.FORFRAGAN),
    EXTERNFORFRAGAN_BESVARAD(Typ.FORFRAGAN),
    BESTALLNING_MOTTAGEN(Typ.BESTALLNING),
    BESTALLNING_UPPDATERAD(Typ.BESTALLNING),
    HANDLING_MOTTAGEN(Typ.HANDLINGAR),
    UTLATANDE_SKICKAT(Typ.UTLATANDE),
    MOTTAGEN_KOMPLETTERING(Typ.UTLATANDE),
    KOMPLETTERING_SKICKAD(Typ.UTLATANDE),
    NYTT_BESOK(Typ.BESOK),
    UPPDATERA_BESOK(Typ.BESOK),
    OMBOKAT_BESOK(Typ.BESOK),
    ANDRAD_UTREDNINGSTYP(Typ.UTREDNING),
    AVVIKELSE_MOTTAGEN(Typ.BESOK),
    AVVIKELSE_RAPPORTERAD(Typ.BESOK),
    UTLATANDE_MOTTAGET(Typ.UTLATANDE),
    KOMPLETTERINGSBEGARAN_MOTTAGEN(Typ.UTLATANDE),
    KOMPLETTERANDE_FRAGESTALLNING_MOTTAGEN(Typ.UTLATANDE),
    AVSLUTAD(Typ.UTREDNING),
    INGEN_BESTALLNING(Typ.BESTALLNING),
    JAV(Typ.BESTALLNING),
    UTREDNING_AVBRUTEN(Typ.UTREDNING),
    NY_ANTECKNING(Typ.ANTECKNING),
    AVBOKAT_BESOK(Typ.BESOK),
    REDOVISAT_BESOK(Typ.BESOK);

    private final Typ typ;

    HandelseTyp(Typ typ) {
        this.typ = typ;
    }

    public Typ getTyp() {
        return typ;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum Typ {
        FORFRAGAN("Förfrågan"),
        BESTALLNING("Beställning"),
        HANDLINGAR("Handlingar"),
        BESOK("Besök"),
        UTREDNING("Utredning"),
        UTLATANDE("Utlåtande"),
        ANTECKNING("Anteckning");

        private final String id;
        private final String label;

        Typ(String label) {
            this.id = name();
            this.label = label;
        }

        public String getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }
    };

}
