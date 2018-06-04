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
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NotifieringTyp {
    // Notifieringar som endast berör Landsting (VG)
    NY_EXTERNFORFRAGAN("Ny förfrågan", NotifieringMottagarTyp.LANDSTING),
    SAMTLIGA_INTERNFORFRAGAN_BESVARATS("Förfrågan besvarad av vårdenheter", NotifieringMottagarTyp.LANDSTING),
    PAMINNELSE_SLUTDATUM_EXTERNFORFRAGAN_PASSERAS("Påminnelse svara på förfrågan", NotifieringMottagarTyp.LANDSTING),
    SVARSDATUM_EXTERNFORFRAGAN_PASSERAT("Svarsdatum passerat för förfrågan", NotifieringMottagarTyp.LANDSTING),
    AVVIKELSE_RAPPORTERAD_AV_VARDEN("Avvikelse rapporterad av vårdenhet", NotifieringMottagarTyp.LANDSTING),

    // Notifieringar som endast berör vårdenheter (VE)
    NY_INTERNFORFRAGAN("Ny förfrågan", NotifieringMottagarTyp.VARDENHET),
    UTREDNING_TILLDELAD("Tilldelad utredning", NotifieringMottagarTyp.VARDENHET),
    PAMINNELSE_SLUTDATUM_INTERNFORFRAGAN_PASSERAS("Påminnelse svara på förfrågan", NotifieringMottagarTyp.VARDENHET),
    SVARSDATUM_INTERNFORFRAGAN_PASSERAT("Svarsdatum passerat för förfrågan", NotifieringMottagarTyp.VARDENHET),
    NY_BESTALLNING("Ny beställning", NotifieringMottagarTyp.VARDENHET),
    UPPDATERAD_BESTALLNING("Uppdaterad beställning", NotifieringMottagarTyp.VARDENHET),
    PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS("Påminnelse skicka utlåtande", NotifieringMottagarTyp.VARDENHET),
    KOMPLETTERING_BEGARD("Komplettering begärd", NotifieringMottagarTyp.VARDENHET),
    PAMINNELSEDATUM_KOMPLETTERING_PASSERAS("Påminnelse svara på komplettering", NotifieringMottagarTyp.VARDENHET),
    PAMINNELSE_REDOVISA_BESOK("Påminnelse redovisa besök", NotifieringMottagarTyp.VARDENHET),

    // Notifieringar som är gemensamma för båda
    INGEN_BESTALLNING("Försäkringskassan kommer inte skicka någon beställning", NotifieringMottagarTyp.ALL),
    UTREDNING_AVSLUTAD_PGA_JAV("Utredning avslutad på grund av jäv", NotifieringMottagarTyp.ALL),
    AVVIKELSE_MOTTAGEN_AV_FK("Avvikelse rapporterad av Försäkringskassan", NotifieringMottagarTyp.ALL),
    UTREDNING_AVSLUTAD_PGA_AVBRUTEN("Utredning avbruten", NotifieringMottagarTyp.ALL),
    SLUTDATUM_UTREDNING_PASSERAT("Slutdatum för utredning passerat", NotifieringMottagarTyp.ALL),
    SLUTDATUM_KOMPLETTERING_PASSERAT("Slutdatum för komplettering passerat", NotifieringMottagarTyp.ALL);

    private final String id;
    private final String label;
    private final NotifieringMottagarTyp notifieringMottagarTyp;

    NotifieringTyp(String label, NotifieringMottagarTyp notifieringMottagarTyp) {
        this.id = this.name();
        this.notifieringMottagarTyp = notifieringMottagarTyp;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @JsonIgnore
    public NotifieringMottagarTyp getNotifieringMottagarTyp() {
        return notifieringMottagarTyp;
    }

}
