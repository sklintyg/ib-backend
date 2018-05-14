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

import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class UtredningStatusResolver {

    public UtredningStatus resolveStatus(Utredning utredning) {
        return resolveStaticStatus(utredning);
    }

    public static UtredningStatus resolveStaticStatus(Utredning utredning) {
        // How to resolve statuses....

        if (utredning.getAvbrutenDatum() != null) {
            return UtredningStatus.AVBRUTEN;
        }

        // First phase - there can be no Bestallning - i.e. Forfragan
        if (!utredning.getBestallning().isPresent()) {

            // AVVISAD
            if (utredning.getExternForfragan() != null && utredning.getExternForfragan().getAvvisatDatum() != null) {
                return UtredningStatus.AVVISAD;
            }

            // FORFRAGAN_INKOMMEN - får ej finnas några internförfrågan alls.
            if (utredning.getExternForfragan() != null && utredning.getExternForfragan().getInternForfraganList().size() == 0) {
                return UtredningStatus.FORFRAGAN_INKOMMEN;
            }

            // VANTAR_PA_SVAR - måste finnas internförfrågan - men ingen får vara tilldelad eller accepterad.
            if (utredning.getExternForfragan() != null && !isAccepterad(utredning.getExternForfragan().getInternForfraganList())
                    && !isTilldelad(utredning.getExternForfragan().getInternForfraganList())) {
                return UtredningStatus.VANTAR_PA_SVAR;
            }

            // TILLDELA_UTREDNING - måste finnas internförfrågan som är accepterad - men får ej vara tilldelad.
            if (utredning.getExternForfragan() != null && isAccepterad(utredning.getExternForfragan().getInternForfraganList())
                    && !isTilldelad(utredning.getExternForfragan().getInternForfraganList())) {
                return UtredningStatus.TILLDELA_UTREDNING;
            }

            // TILLDELAD_VANTAR_PA_BESTALLNING - måste finnas internförfrågan som är accepterad - som är tilldelad.
            if (utredning.getExternForfragan() != null
                    && isAccepteradAndTilldelad(utredning.getExternForfragan().getInternForfraganList())) {
                return UtredningStatus.TILLDELAD_VANTAR_PA_BESTALLNING;
            }
            throw new IllegalStateException("Invalid sub-state in phase FORFRAGAN!");
        }

        // Second phase - Utredning. We ALWAYS have a Bestallning here and one intyg.
        if (utredning.getIntygList().size() == 1) {

            // UTREDNING_PAGAR_AVVIKELSE
            if (utredning.getBesokList().stream().anyMatch(bl -> bl.getAvvikelse() != null)) {
                return UtredningStatus.AVVIKELSE_MOTTAGEN;
            }

            // BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR
            Bestallning bestallning = utredning.getBestallning().get();
            if (utredning.getHandlingList().size() == 0 && bestallning.getUppdateradDatum() == null) {
                return UtredningStatus.BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR;
            }

            // BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR
            if (utredning.getHandlingList().size() == 0 && bestallning.getUppdateradDatum() != null) {
                return UtredningStatus.UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR;
            }

            // HANDLINGAR_MOTTAGNA_BOKA_BESOK
            if (utredning.getHandlingList().size() > 0 && utredning.getBesokList().size() == 0) {
                return UtredningStatus.HANDLINGAR_MOTTAGNA_BOKA_BESOK;
            }

            // UTREDNING_PAGAR
            if (utredning.getHandlingList().size() > 0 && utredning.getBesokList().size() > 0
                    && utredning.getIntygList().stream().noneMatch(intyg -> intyg.getSkickatDatum() != null)) {
                return UtredningStatus.UTREDNING_PAGAR;
            }

            // Skickat - ursprungsintyget är skickat.
            if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.getSkickatDatum() != null
                    && intyg.getMottagetDatum() == null
                    && intyg.getKompletteringsId() == null
                    && (intyg.getSistaDatum() == null || intyg.getSistaDatum().isAfter(LocalDateTime.now())))) {
                return UtredningStatus.UTLATANDE_SKICKAT;
            }

            // Skickat - ursprungsintyget är skickat.
            if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.getSkickatDatum() != null
                    && intyg.getMottagetDatum() != null
                    && intyg.getKompletteringsId() == null
                    && (intyg.getSistaDatum() == null || intyg.getSistaDatum().isAfter(LocalDateTime.now())))) {
                return UtredningStatus.UTLATANDE_MOTTAGET;
            }

        }

        if (utredning.getIntygList().size() > 0) {

            // Om sista datum för kompletteringsbegäran EJ passerats.
//            if (utredning.getIntygList().stream().noneMatch(intyg -> intyg.getSistaDatumKompletteringsbegaran() != null
//                    && LocalDate.now().isBefore(intyg.getSistaDatumKompletteringsbegaran().toLocalDate()))) {
//                return UtredningStatus.UTLATANDE_MOTTAGET;
//            }

            // Om ingen komplettering finns utstående.
            if (utredning.getIntygList().stream()
                    .noneMatch(intyg -> intyg.getSistaDatumKompletteringsbegaran() != null
                            && LocalDate.now().isAfter(intyg.getSistaDatumKompletteringsbegaran().toLocalDate()))) {

                // Om något besök inkluderade deltagande tolk...
                if (utredning.getBesokList().stream()
                        .anyMatch(besok -> besok.getTolkStatus() != null && besok.getTolkStatus() == TolkStatusTyp.DELTAGIT)) {

                    // Kolla om samtliga tolkar redovisats.
                    if (utredning.getBesokList().stream()
                            .filter(besok -> besok.getTolkStatus() != null && besok.getTolkStatus() == TolkStatusTyp.DELTAGIT)
                            .allMatch(besok -> besok.getErsatts() != null && besok.getErsatts())) {
                        return UtredningStatus.AVSLUTAD;
                    } else {
                        return UtredningStatus.REDOVISA_TOLK;
                    }
                } else {
                    return UtredningStatus.AVSLUTAD;
                }
            }

            // Komplettering är skickad.
            if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.getSkickatDatum() != null
                    && intyg.getKompletteringsId() != null
                    && intyg.getMottagetDatum() == null)) {
                return UtredningStatus.KOMPLETTERING_SKICKAD;
            }

            // Komplettering mottagen.
            if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.getSkickatDatum() != null
                    && intyg.getKompletteringsId() != null
                    && intyg.getMottagetDatum() != null)) {
                return UtredningStatus.KOMPLETTERING_MOTTAGEN;
            }


        }



        throw new IllegalStateException("Unhandled state!");
    }

    private static boolean isAccepteradAndTilldelad(List<InternForfragan> internForfraganList) {
        return internForfraganList.stream().anyMatch(internForfragan -> internForfragan.getTilldeladDatum() != null
                && internForfragan.getForfraganSvar().getSvarTyp() == SvarTyp.ACCEPTERA);
    }

    private static boolean isAccepterad(List<InternForfragan> internForfraganList) {
        return internForfraganList.stream().filter(internForfragan -> internForfragan.getForfraganSvar() != null)
                .anyMatch(internForfragan -> internForfragan.getForfraganSvar().getSvarTyp() == SvarTyp.ACCEPTERA);
    }

    private static boolean isTilldelad(List<InternForfragan> internForfraganList) {
        return internForfraganList.stream()
                .anyMatch(internForfragan -> internForfragan.getForfraganSvar() != null && internForfragan.getTilldeladDatum() != null);
    }
}