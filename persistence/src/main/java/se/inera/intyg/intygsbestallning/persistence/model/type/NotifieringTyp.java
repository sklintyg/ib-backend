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

/**
 * Defines all existing notification types for each {@link NotifieringMottagarTyp}. Some are unique for each
 * {@link NotifieringMottagarTyp} and some are common.
 *
 * NOTE: The declared order of items is important as it also ensures that the presentation order is as specified.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NotifieringTyp {

    // NILT01
    NY_EXTERNFORFRAGAN("Ny förfrågan", NotifieringMottagarTyp.LANDSTING),

    // NIVE01
    NY_INTERNFORFRAGAN("Ny förfrågan", NotifieringMottagarTyp.VARDENHET),

    // NIVE02
    UTREDNING_TILLDELAD("Tilldelad utredning", NotifieringMottagarTyp.VARDENHET),

    // NIVE03
    PAMINNELSE_SLUTDATUM_INTERNFORFRAGAN_PASSERAS("Påminnelse svara på förfrågan", NotifieringMottagarTyp.VARDENHET),

    // NIVE04
    SVARSDATUM_INTERNFORFRAGAN_PASSERAT("Svarsdatum passerat för förfrågan", NotifieringMottagarTyp.VARDENHET),

    // NILT02
    SAMTLIGA_INTERNFORFRAGAN_BESVARATS("Förfrågan besvarad av vårdenheter", NotifieringMottagarTyp.LANDSTING),

    // NILT03
    PAMINNELSE_SLUTDATUM_EXTERNFORFRAGAN_PASSERAS("Påminnelse svara på förfrågan", NotifieringMottagarTyp.LANDSTING),

    // NILT04
    SVARSDATUM_EXTERNFORFRAGAN_PASSERAT("Svarsdatum passerat för förfrågan", NotifieringMottagarTyp.LANDSTING),

    // NILT05 + NIVE05
    INGEN_BESTALLNING("Försäkringskassan kommer inte skicka någon beställning", NotifieringMottagarTyp.ALL),

    // NIVE06
    NY_BESTALLNING("Ny beställning", NotifieringMottagarTyp.VARDENHET),

    // NILT06 + NIVE07
    UTREDNING_AVSLUTAD_PGA_JAV("Utredning avslutad på grund av jäv", NotifieringMottagarTyp.ALL),

    // NIVE08
    UPPDATERAD_BESTALLNING("Uppdaterad beställning", NotifieringMottagarTyp.VARDENHET),

    // NILT07
    AVVIKELSE_RAPPORTERAD_AV_VARDEN("Avvikelse rapporterad av vårdenhet", NotifieringMottagarTyp.LANDSTING),

    // NILT08 + NIVE09
    AVVIKELSE_MOTTAGEN_AV_FK("Avvikelse rapporterad av Försäkringskassan", NotifieringMottagarTyp.ALL),

    // NILT09 + NIVE10
    UTREDNING_AVSLUTAD_PGA_AVBRUTEN("Utredning avbruten", NotifieringMottagarTyp.ALL),

    // NIVE11
    PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS("Påminnelse skicka utlåtande", NotifieringMottagarTyp.VARDENHET),

    // NILT10 + NIVE12
    SLUTDATUM_UTREDNING_PASSERAT("Slutdatum för utredning passerat", NotifieringMottagarTyp.ALL),

    // NIVE13
    KOMPLETTERING_BEGARD("Komplettering begärd", NotifieringMottagarTyp.VARDENHET),

    // NIVE14
    PAMINNELSEDATUM_KOMPLETTERING_PASSERAS("Påminnelse svara på komplettering", NotifieringMottagarTyp.VARDENHET),

    // NILT11 + NIVE15
    SLUTDATUM_KOMPLETTERING_PASSERAT("Slutdatum för komplettering passerat", NotifieringMottagarTyp.ALL),

    // NIVE16
    PAMINNELSE_REDOVISA_BESOK("Påminnelse redovisa besök", NotifieringMottagarTyp.VARDENHET);

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
