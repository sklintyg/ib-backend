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
    NY_EXTERNFORFRAGAN("Ny förfrågan", NotificationRecipientType.LANDSTING),
    SAMTLIGA_INTERNFORFRAGAN_BESVARATS("Förfrågan besvarad av vårdenheter", NotificationRecipientType.LANDSTING),
    PAMINNELSE_SLUTDATUM_EXTERNFORFRAGAN_PASSERAS("Påminnelse svara på förfrågan", NotificationRecipientType.LANDSTING),
    SVARSDATUM_EXTERNFORFRAGAN_PASSERAT("Svarsdatum passerat för förfrågan", NotificationRecipientType.LANDSTING),
    AVVIKELSE_RAPPORTERAD_AV_VARDEN("Avvikelse rapporterad av vårdenhet", NotificationRecipientType.LANDSTING),

    // Notifieringar som endast berör vårdenheter (VE)
    NY_INTERNFORFRAGAN("Ny förfrågan", NotificationRecipientType.VARDENHET),
    UTREDNING_TILLDELAD("Tilldelad utredning", NotificationRecipientType.VARDENHET),
    PAMINNELSE_SLUTDATUM_INTERNFORFRAGAN_PASSERAS("Påminnelse svara på förfrågan", NotificationRecipientType.VARDENHET),
    SVARSDATUM_INTERNFORFRAGAN_PASSERAT("Svarsdatum passerat för förfrågan", NotificationRecipientType.VARDENHET),
    NY_BESTALLNING("Ny beställning", NotificationRecipientType.VARDENHET),
    UPPDATERAD_BESTALLNING("Uppdaterad beställning", NotificationRecipientType.VARDENHET),
    PAMINNELSE_SLUTDATUM_UTREDNING_PASSERAS("Påminnelse skicka utlåtande", NotificationRecipientType.VARDENHET),
    KOMPLETTERING_BEGARD("Komplettering begärd", NotificationRecipientType.VARDENHET),
    PAMINNELSEDATUM_KOMPLETTERING_PASSERAS("Påminnelse svara på komplettering", NotificationRecipientType.VARDENHET),
    PAMINNELSE_REDOVISA_BESOK("Påminnelse redovisa besök", NotificationRecipientType.VARDENHET),

    // Notifieringar som är gemensamma för båda
    INGEN_BESTALLNING("Försäkringskassan kommer inte skicka någon beställning", NotificationRecipientType.ALL),
    UTREDNING_AVSLUTAD_PGA_JAV("Utredning avslutad på grund av jäv", NotificationRecipientType.ALL),
    AVVIKELSE_MOTTAGEN_AV_FK("Avvikelse rapporterad av Försäkringskassan", NotificationRecipientType.ALL),
    UTREDNING_AVSLUTAD_PGA_AVBRUTEN("Utredning avbruten", NotificationRecipientType.ALL),
    SLUTDATUM_UTREDNING_PASSERAT("Slutdatum för utredning passerat", NotificationRecipientType.ALL),
    SLUTDATUM_KOMPLETTERING_PASSERAT("Slutdatum för komplettering passerat", NotificationRecipientType.ALL);

    private final String id;
    private final String label;
    private final NotificationRecipientType recipient;

    NotifieringTyp(String label, NotificationRecipientType recipient) {
        this.id = this.name();
        this.recipient = recipient;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @JsonIgnore
    public NotificationRecipientType getRecipient() {
        return recipient;
    }

    public enum NotificationRecipientType {
        LANDSTING,
        VARDENHET,
        ALL
    }
}
