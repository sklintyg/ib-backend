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
package se.inera.intyg.intygsbestallning.service.notifiering.util;

public final class NotifieringMailSubjectConstants {

    private NotifieringMailSubjectConstants() {

    }

    //CHECKSTYLE:OFF LineLength

    public static final String SUBJECT_BESTALLNING_UPPDATERAD = "Försäkringskassan har uppdaterat en beställning";
    public static final String SUBJECT_BESTALLNING_AV_FRORSAKRINGSMEDICINSK_UTREDNING = "Beställning av Försäkringsmedicinsk utredning";
    public static final String SUBJECT_INGEN_BESTALLNING = "Försäkringskassan kommer inte skicka någon beställning";
    public static final String SUBJECT_NY_FMU_EXTERN_FORFRAGAN = "Ny FMU förfragan";
    public static final String SUBJECT_SAMTLIGA_INTERNFORFRAGAN_BESVARATS = "Tilldela FMU utredning";
    public static final String SUBJECT_NY_FMU_INTERN_FORFRAGAN = "Ny FMU förfragan";
    public static final String SUBJECT_FMU_UTREDNING_TILLDELAD_VARDENHETEN = "En FMU utredning har blivit tilldelad vårdenheten";
    public static final String SUBJECT_UTREDNING_SLUTDATUM_PAMINNELSE = "Påminnelse: Slutdatum för en utredning är på väg att passeras";
    public static final String SUBJECT_UTREDNING_SLUTDATUM_PASSERAT = "Slutdatum för en utredningen har passerats";
    public static final String SUBJECT_KOMPLETTERING_SLUTDATUM_PAMINNELSE = "Påminnelse: Slutdatum för kompletteringen är på väg att passeras";
    public static final String SUBJECT_KOMPLETTERING_SLUTDATUM_PASSERAT = "Slutdatum för kompletteringen har passerats";
    public static final String SUBJECT_AVVIKELSE_RAPPORTERAD_AV_VARDEN = "En vårdenhet har rapporterat en avvikelse";
    public static final String SUBJECT_AVVIKELSE_MOTTAGEN_FRAN_FK = "Försäkringskassan har rapporterat en avvikelse";
    public static final String SUBJECT_AVSLUTAD_PGA_JAV = "Utredning avslutad på grund av jävsförhållanden";
    public static final String SUBJECT_AVSLUTAD_UTREDNING = "Försäkringskassan har avbrutit utredningen";
    public static final String SUBJECT_REDOVISA_BESOK = "Redovisa besök";
    public static final String SUBJECT_KOMPLETTERING_BEGARD = "Försäkringskassan har begärt komplettering";
    public static final String SUBJECT_PAMINNELSE_SVARA_EXTERNFORFRAGAN = "Påminnelse: Svarsdatum för en FMU förfrågan är på väg att passeras";
    public static final String SUBJECT_PAMINNELSE_SVARA_INTERNFORFRAGAN = "Påminnelse: Svarsdatum för en FMU förfrågan är på väg att passeras";

    //CHECKSTYLE:ON LineLength
}
