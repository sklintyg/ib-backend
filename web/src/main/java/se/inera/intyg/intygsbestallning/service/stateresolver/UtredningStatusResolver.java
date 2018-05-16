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

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;

public class UtredningStatusResolver {

    private static final Logger LOG = LoggerFactory.getLogger(UtredningStatusResolver.class);

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
            return handleForfraganFas(utredning);
        }

        // Second phase - Utredning. We ALWAYS have a Bestallning here and one intyg. There can never be a komplettering
        // if intyg.size == 1.
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
                    && (intyg.getSistaDatumKompletteringsbegaran() != null
                            && intyg.getSistaDatumKompletteringsbegaran().isAfter(LocalDateTime.now())))) {
                return UtredningStatus.UTLATANDE_MOTTAGET;
            }
        }

        // Slutfas. Denna kontroll måste ske före vi tittar detaljerat på kompletteringar.

        // Om alla intyg/kompletteringar står som mottagna är vi klara OCH om sista datum för komplettering passerats.
        LocalDateTime senasteDatumForKomplettering = utredning.getIntygList().stream()
                .filter(intyg -> intyg.getSistaDatumKompletteringsbegaran() != null)
                .map(Intyg::getSistaDatumKompletteringsbegaran)
                .max(LocalDateTime::compareTo).orElse(null);

        // Om senasteDatumForKomplettering saknas (bör ej inträffa) så
        // betraktar vi den som att vara i avslutsfas.
        if (senasteDatumForKomplettering == null) {
            return resolveAvslutEllerTolk(utredning);
        }

        if (utredning.getIntygList().stream().allMatch(intyg -> intyg.getMottagetDatum() != null)
                && LocalDateTime.now().isAfter(senasteDatumForKomplettering)) {

            // Om något besök inkluderade deltagande tolk...
            return resolveAvslutEllerTolk(utredning);
        } else if (utredning.getIntygList().stream().allMatch(intyg -> intyg.getMottagetDatum() != null)
                && !LocalDateTime.now().isAfter(senasteDatumForKomplettering)) {

            // Om det funnits någon komplettering...
            if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.getKompletteringsId() != null)) {
                return UtredningStatus.KOMPLETTERING_MOTTAGEN;
            } else {
                return UtredningStatus.UTLATANDE_MOTTAGET;
            }
        }

        // Kompletteringar.

        // Det finns komplettering, men ingen kompletterande frågetällning än samt att sistadatum ej har passerats.
        if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.getKompletteringsId() != null
                && intyg.getFragestallningMottagenDatum() == null
                && intyg.getSistaDatum().isAfter(LocalDateTime.now()))) {
            return UtredningStatus.KOMPLETTERINGSBEGARAN_MOTTAGEN_VANTAR_PA_FRAGESTALLNING;
        }

        // Det finns komplettering med kompletterande frågetällning, men sistadatum ej har passerats.
        if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.getKompletteringsId() != null
                && intyg.getFragestallningMottagenDatum() != null
                && intyg.getSistaDatum().isAfter(LocalDateTime.now()))) {
            return UtredningStatus.KOMPLETTERANDE_FRAGESTALLNING_MOTTAGEN;
        }

        // Komplettering är skickad till FK.
        if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.getSkickatDatum() != null
                && intyg.getKompletteringsId() != null
                && intyg.getMottagetDatum() == null)) {
            return UtredningStatus.KOMPLETTERING_SKICKAD;
        }

        // Komplettering mottagen av FK.
        if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.getSkickatDatum() != null
                && intyg.getKompletteringsId() != null
                && intyg.getMottagetDatum() != null)) {
            return UtredningStatus.KOMPLETTERING_MOTTAGEN;
        }

        LOG.error("Utredning '{}' is in an unhandled or invalid state. This indicates a bug in the state resolver or "
                + "invalid state in the Utredning. Returning ", utredning.getUtredningId());
        return UtredningStatus.INVALID;
    }

    private static UtredningStatus resolveAvslutEllerTolk(Utredning utredning) {
        if (utredning.getBesokList().stream()
                .anyMatch(besok -> besok.getTolkStatus() != null)) {

            // Kolla om samtliga tolkar redovisats.
            if (utredning.getBesokList().stream()
                    .filter(besok -> besok.getTolkStatus() != null)
                    .allMatch(besok -> besok.getTolkStatus() == TolkStatusTyp.DELTAGIT)) {
                return UtredningStatus.AVSLUTAD;
            } else {

                // Dvs om vi har något TolkStatus BOKAD så måste denna redovisas (dvs sättas i DELTAGIT)
                return UtredningStatus.REDOVISA_TOLK;
            }
        } else {
            return UtredningStatus.AVSLUTAD;
        }
    }

    private static UtredningStatus handleForfraganFas(Utredning utredning) {
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
