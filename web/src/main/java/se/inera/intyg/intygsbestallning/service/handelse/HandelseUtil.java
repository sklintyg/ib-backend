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
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.service.stateresolver.Actor;

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

    public static Handelse createForfraganBesvarad(SvarTyp svarTyp, String samordnare, String vardenhet) {

        String handelseText;
        if (svarTyp == SvarTyp.ACCEPTERA) {
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
}
