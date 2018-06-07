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

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;

public final class NotifieringMailMeddelandeUtil {

    private NotifieringMailMeddelandeUtil() {
    }

    public static String externForfraganUrl(final Utredning utredning) {
        return "<URL to EXTERNFORFRAGAN>";
    }

    public static String internForfraganUrl(final Utredning utredning) {
        return "<URL to INTERNFORFRAGAN>";
    }

    public static String utredningUrl(final Utredning utredning) {
        return "<URL to UTREDNING>";
    }

    public static String landstingNyExternforfraganMessage() {
        return "Det har inkommit en ny förfrågan om en försäkringsmedicinsk utredning (FMU) från Försäkringskassan.";
    }

    public static String vardenhetNyInternforfraganMessage(final InternForfragan internForfragan) {
        return MessageFormat.format(
                "Det har inkommit en ny förfrågan om en försäkringsmedicinsk utredning (FMU) från {0}.",
                internForfragan.getVardenhetHsaId());
    }

    public static String landstingSamtligaInternForfraganBesvaradeforfraganMessage(final Utredning utredning) {
        return MessageFormat.format(
                "Samtliga vårdenheter som har tagit emot förfrågan om ett genomföra en försäkringsmedicinsk "
                + "utredning (FMU) har svarat i utredning {0}",
                utredning.getUtredningId());
    }

    public static String nyBestallningMessage(final Utredning utredning) {
        return MessageFormat.format(
                "Försäkringskassan har skickat en beställning av en Försäkringsmedicinsk utredning (FMU) för utredning {0}",
                utredning.getUtredningId());
    }

    public static String uppdateradBestallningMessage(final Utredning utredning) {
        return MessageFormat.format("Försäkringskassan har uppdaterad beställningen av utredningen {0} med ny information.",
                utredning.getUtredningId());
    }

    public static String paminnelseSlutdatumUtredningMessage(final Utredning utredning) {
        // Find the last sistaDatum on an intyg on the Utredning.
        Optional<Intyg> sistaDatumOpt = utredning.getIntygList().stream().filter(intyg -> intyg.getSistaDatum() != null
                && !intyg.isKomplettering())
                .max(Comparator.comparing(Intyg::getSistaDatum));

        // This should never happen...
        if (!sistaDatumOpt.isPresent()) {
            throw new IllegalStateException("Unable to send slutdatum på väg passeras notification, no intyg on Utredning "
                    + "has a sista datum.");
        }
        String sistaDatumForMottagning = sistaDatumOpt.get().getSistaDatum().format(DateTimeFormatter.ISO_DATE);

        return MessageFormat.format(
                "Slutdatum {0} för utredning {1} kommer snart att passeras. Om utlåtandet inte är mottaget av Försäkringskassan "
                        + "innan angivet slutdatum så kommer utredningen inte att ersättas.",
                sistaDatumForMottagning,
                utredning.getUtredningId());
    }

    public static String slutdatumPasseratUtredningMessage(final Utredning utredning) {
        // Find the last sistaDatum on an intyg on the Utredning.
        Optional<Intyg> sistaDatumOpt = utredning.getIntygList().stream().filter(intyg -> intyg.getSistaDatum() != null
                && !intyg.isKomplettering())
                .max(Comparator.comparing(Intyg::getSistaDatum));

        // This should never happen...
        if (!sistaDatumOpt.isPresent()) {
            throw new IllegalStateException("Unable to send slutdatum på väg passeras notification, no intyg on Utredning "
                    + "has a sista datum.");
        }

        return MessageFormat.format("Slutdatum {0} för utredning {1} har "
                        + "passerats. Utredningen kommer därför inte ersättas av Försäkringskassan.",
                sistaDatumOpt,
                utredning.getUtredningId());
    }

    public static String avvikelseRapporteradAvVardenMessage(final Utredning utredning, final Besok besok) {
        return MessageFormat.format(
                "En vårdenhet har rapporterat en avvikelse för ett besök som var inbokat {0} i utredning {1}",
                besok.getBesokStartTid().format(DateTimeFormatter.ISO_DATE),
                utredning.getUtredningId());
    }

    public static String vardenhetAvvikelseRapporteradAvFKMessage(final Utredning utredning, final Besok besok) {
        return MessageFormat.format(
                "Försäkringskassan har rapporterat en avvikelse för besöket som är inbokat {0} i utredning {1}. "
                        + "Tillse att besöket avbokas.",
                besok.getBesokStartTid().format(DateTimeFormatter.ISO_DATE),
                utredning.getUtredningId());
    }

    public static String landstingAvvikelseRapporteradAvFKMessage(final Utredning utredning, final Besok besok) {
        return MessageFormat.format(
                "Försäkringskassan har rapporterat en avvikelse för besöket som är inbokat {0} i utredning {1}.",
                besok.getBesokStartTid().format(DateTimeFormatter.ISO_DATE),
                utredning.getUtredningId());
    }

    public static String vardenhetTilldeladUtredning(final Utredning utredning, final String landstingNamn) {
        return MessageFormat.format(
                "Försäkringsmedicinsk utredning {0} har blivit tilldelad vårdenheten av {1}. Observera att "
                        + "Försäkringskassan ännu inte har beställt utredningen.",
                utredning.getUtredningId(), landstingNamn);
    }

    public static String ingenBeställningMessage(final Utredning utredning) {
        return MessageFormat.format("Försäkringskassan har meddelat att de inte kommer skicka någon "
                + "beställning utifrån förfrågan {0}. Utredningen avslutas.", utredning.getUtredningId());
    }

    public static String avslutaPgaJavMessage(final Utredning utredning) {
        return MessageFormat.format("Försäkringskassan har valt att avsluta utredning {0} utifrån "
                        + "att det råder jävsförhållanden.",
                utredning.getUtredningId());
    }
}
