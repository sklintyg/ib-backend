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
package se.inera.intyg.intygsbestallning.service.handelse;

import se.inera.intyg.intygsbestallning.persistence.model.Handelse;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandelseTyp;
import se.inera.intyg.intygsbestallning.service.stateresolver.Actor;
import se.inera.intyg.intygsbestallning.web.responder.dto.ReportBesokAvvikelseRequest;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static se.inera.intyg.intygsbestallning.persistence.model.Handelse.HandelseBuilder.aHandelse;

public final class HandelseUtil {
    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    private static final String FK_LABEL = Actor.FK.getLabel();

    private HandelseUtil() {
    }

    public static Handelse createOrderReceived(String myndighet, LocalDate endDate) {
        return aHandelse()
                .withSkapad(LocalDateTime.now())
                .withHandelseTyp(HandelseTyp.BESTALLNING_MOTTAGEN)
                .withAnvandare(myndighet)
                .withHandelseText(MessageFormat.format("Beställning mottagen från {0}. Slutdatum: {1}",
                        myndighet,
                        Optional.ofNullable(endDate)
                                .map(d -> d.format(formatter))
                                .orElse("inte angivet")))
                .build();
    }

    public static Handelse createForfraganSkickad(String samordnare, String vardenhet) {
        return aHandelse()
                .withSkapad(LocalDateTime.now())
                .withHandelseTyp(HandelseTyp.FORFRAGAN_SKICKAD)
                .withAnvandare(samordnare)
                .withHandelseText(MessageFormat.format("Förfrågan skickades till {0}", vardenhet))
                .build();
    }

    public static Handelse createForfraganBesvarad(boolean accepted, String samordnare, String vardenhet) {

        String handelseText;
        if (accepted) {
            handelseText = String.format("Förfrågan accepterades av landstinget. Utredningen tilldelad till %s", vardenhet);
        } else {
            handelseText = "Förfrågan avvisades av landstinget.";
        }
        return aHandelse()
                .withSkapad(LocalDateTime.now())
                .withHandelseTyp(HandelseTyp.FORFRAGAN_BESVARAD)
                .withAnvandare(samordnare)
                .withHandelseText(handelseText)
                .build();
    }

    public static Handelse createInternForfraganBesvarad(boolean accepted, String vardadmin, String vardenhetNamn,
            String svarKommentar, LocalDate borjaDatum) {

        StringBuilder handelseText = new StringBuilder();
        if (accepted) {
            handelseText.append(String.format("Förfrågan accepterad av %s", vardenhetNamn));
            if (borjaDatum != null) {
                handelseText.append(String.format(" Startdatum: %s", borjaDatum.format(formatter)));
            }
        } else {
            handelseText.append(String.format("Förfrågan avvisades av %s", vardenhetNamn));
        }
        return aHandelse()
                .withSkapad(LocalDateTime.now())
                .withHandelseTyp(accepted ? HandelseTyp.FORFRAGAN_ACCEPTERAD : HandelseTyp.FORFRAGAN_AVVISAD)
                .withAnvandare(vardadmin)
                .withHandelseText(handelseText.toString())
                .withKommentar(svarKommentar)
                .build();
    }

    public static Handelse createBesokAvvikelse(final ReportBesokAvvikelseRequest request) {

        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("Avvikelse ");

        if (request.getHandelseTyp().equals(HandelseTyp.AVVIKELSE_MOTTAGEN)) {
            textBuilder.append("mottagen ");
        } else if (request.getHandelseTyp().equals(HandelseTyp.AVVIKELSE_RAPPORTERAD)) {
            textBuilder.append("rapporterad ");
        }

        textBuilder.append(request.getTidpunkt().format(formatter));
        textBuilder.append(". ");
        textBuilder.append(MessageFormat.format("Orsakad av {0}", request.getOrsakatAv().name()));

        if (request.getInvanareUteblev()) {
            textBuilder.append(" Invanare uteblev");
        }

        return aHandelse()
                .withSkapad(LocalDateTime.now())
                .withHandelseTyp(HandelseTyp.AVVIKELSE_MOTTAGEN)
                .withAnvandare(request.getSamordnare())
                .withHandelseText(textBuilder.toString())
                .withKommentar(request.getBeskrivning().orElse(null))
                .build();
    }

    public static Handelse createKompletteringBegard() {
        return aHandelse()
                .withSkapad(LocalDateTime.now())
                .withHandelseTyp(HandelseTyp.KOMPLETTERINGSBEGARAN_MOTTAGEN)
                .withAnvandare(FK_LABEL)
                .withHandelseText(MessageFormat.format("Kompletteringsbegäran mottagen från Försäkringskassan {0}",
                        LocalDateTime.now().format(formatter)))
                .build();
    }

    public static Handelse createKompletteringMottagen(final LocalDateTime mottagetDatum) {
        return aHandelse()
                .withSkapad(LocalDateTime.now())
                .withHandelseTyp(HandelseTyp.MOTTAGEN_KOMPLETTERING)
                .withAnvandare(FK_LABEL)
                .withHandelseText(MessageFormat.format("Kompletteringen mottagen {0}",
                        mottagetDatum.format(formatter)))
                .build();
    }

    public static Handelse createUtlatandeMottaget(final LocalDateTime mottagetDatum) {
        return aHandelse()
                .withSkapad(LocalDateTime.now())
                .withHandelseTyp(HandelseTyp.UTLATANDE_MOTTAGET)
                .withAnvandare(FK_LABEL)
                .withHandelseText(MessageFormat.format("Utlåtandet mottaget av Försäkringskassan {0}",
                        mottagetDatum.format(formatter)))
                .build();
    }

    public static Handelse createHandlingMottagen(final String vardadministrator, final String handlingarMottogsDatum) {
        return aHandelse()
                .withSkapad(LocalDateTime.now())
                .withAnvandare(vardadministrator)
                .withHandelseTyp(HandelseTyp.HANDLING_MOTTAGEN)
                .withHandelseText("Handlingar mottagna " + handlingarMottogsDatum)
                .build();
    }

    public static Handelse createOrderUpdated(final LocalDate nyttSlutDatum, final String nyHandlaggare, final boolean documentsSent) {
        StringBuilder text = new StringBuilder().append("Beställning uppdaterad av Försäkringskassan.");
        if (nyttSlutDatum != null) {
            text.append("Nytt slutdatum: ").append(nyttSlutDatum.format(DateTimeFormatter.ISO_DATE));
        }
        if (nyHandlaggare != null) {
            text.append("Ny handläggare: ").append(nyHandlaggare);
        }
        String kommentar = documentsSent ? "Handlingar skickade" : null;
        return aHandelse()
                .withSkapad(LocalDateTime.now())
                .withAnvandare(FK_LABEL)
                .withHandelseTyp(HandelseTyp.BESTALLNING_UPPDATERAD)
                .withHandelseText(text.toString())
                .withKommentar(kommentar)
                .build();
    }
}
