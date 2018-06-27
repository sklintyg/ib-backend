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
package se.inera.intyg.intygsbestallning.persistence.model.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandlingUrsprungTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UtredningStatusResolver {

    private static final Logger LOG = LoggerFactory.getLogger(UtredningStatusResolver.class);

    public UtredningStatus resolveStatus(Utredning utredning) {
        return resolveStaticStatus(utredning);
    }

    public static UtredningStatus resolveStaticStatus(Utredning utredning) {

        utredning.getExternForfragan()
                .ifPresent(ex -> ex.getInternForfraganList()
                        .forEach(in -> in.setStatus(InternForfraganStatusResolver.resolveStaticStatus(utredning, in))));

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
            Optional<UtredningStatus> utredningStatusOptional = handleUtredningFas(utredning);
            if (utredningStatusOptional.isPresent()) {
                return utredningStatusOptional.get();
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
            return resolveAvslutEllerRedovisaBesok(utredning);
        }

        if (utredning.getIntygList().stream().allMatch(intyg -> intyg.getMottagetDatum() != null)
                && LocalDateTime.now().isAfter(senasteDatumForKomplettering)) {

            // Alla besök måste vara redovisade
            return resolveAvslutEllerRedovisaBesok(utredning);
        } else if (utredning.getIntygList().stream().allMatch(intyg -> intyg.getMottagetDatum() != null)
                && !LocalDateTime.now().isAfter(senasteDatumForKomplettering)) {

            // Om det funnits någon komplettering...
            if (utredning.getIntygList().stream().anyMatch(Intyg::isKomplettering)) {
                return UtredningStatus.KOMPLETTERING_MOTTAGEN;
            } else {
                return UtredningStatus.UTLATANDE_MOTTAGET;
            }
        }

        // Kompletteringar.

        // Det finns komplettering, men ingen kompletterande frågetällning än samt att sistadatum ej har passerats.
        if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.isKomplettering()
                && intyg.getFragestallningMottagenDatum() == null
                && intyg.getSkickatDatum() == null
                && intyg.getSistaDatum().isAfter(LocalDateTime.now()))) {
            return UtredningStatus.KOMPLETTERINGSBEGARAN_MOTTAGEN_VANTAR_PA_FRAGESTALLNING;
        }

        // Det finns komplettering med kompletterande frågetällning, men sistadatum ej har passerats.
        if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.isKomplettering()
                && intyg.getFragestallningMottagenDatum() != null
                && intyg.getSkickatDatum() == null
                && intyg.getSistaDatum().isAfter(LocalDateTime.now()))) {
            return UtredningStatus.KOMPLETTERANDE_FRAGESTALLNING_MOTTAGEN;
        }

        // Komplettering är skickad till FK.
        if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.getSkickatDatum() != null
                && intyg.isKomplettering()
                && intyg.getMottagetDatum() == null)) {
            return UtredningStatus.KOMPLETTERING_SKICKAD;
        }

        // Komplettering mottagen av FK.
        if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.getSkickatDatum() != null
                && intyg.isKomplettering()
                && intyg.getMottagetDatum() != null)) {
            return UtredningStatus.KOMPLETTERING_MOTTAGEN;
        }

        LOG.error("Utredning '{}' is in an unhandled or invalid state. This indicates a bug in the state resolver or "
                + "invalid state in the Utredning. Returning ", utredning.getUtredningId());
        throw new IllegalStateException(MessageFormat.format("Invalid state in Utredning {0}", utredning.getUtredningId()));
    }

    private static UtredningStatus resolveAvslutEllerRedovisaBesok(Utredning utredning) {
        // Finns det bokade besök som inte är redovisade?
        if (utredning.getBesokList().stream()
                .noneMatch(besok -> besok.getBesokStatus() == BesokStatusTyp.TIDBOKAD_VARDKONTAKT)) {
            return UtredningStatus.AVSLUTAD;
        } else {
            // Alla besok måste redovisas som genomförda eller vara avbokade.
            // BesokStatusTyp.TIDBOKAD_VARDKONTAKT blir antingen BesokStatusTyp.AVSLUTAD_VARDKONTAKT eller
            // BesokStatusTyp.INSTALLD_VARDKONTAKT
            return UtredningStatus.REDOVISA_BESOK;
        }
    }

    private static Optional<UtredningStatus> handleUtredningFas(Utredning utredning) {
        // UTREDNING_PAGAR_AVVIKELSE
        if (utredning.getBesokList().stream().anyMatch(bl -> bl.getAvvikelse() != null)) {
            return Optional.of(UtredningStatus.AVVIKELSE_MOTTAGEN);
        }

        // Om det INTE finns några besök bokade...
        if (utredning.getBesokList().size() == 0) {
            // BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR
            Bestallning bestallning = utredning.getBestallning().get();
            if (utredning.getHandlingList().size() == 0 && bestallning.getUppdateradDatum() == null) {
                return Optional.of(UtredningStatus.BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR);
            }

            // Om det finns exakt 0 handlingar med mottaget-datum
            if (utredning.getHandlingList().stream().noneMatch(handling -> handling.getInkomDatum() != null)) {
                // BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR
                if (bestallning.getUppdateradDatum() == null) {
                    return Optional.of(UtredningStatus.BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR);
                }

                // BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR
                if (bestallning.getUppdateradDatum() != null) {
                    return Optional.of(UtredningStatus.UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR);
                }
            }
            // Om det inte finns någon handling taggad UPPDATERING och inga besök och ingen uppdaterad-stämpel
            if (bestallning.getUppdateradDatum() == null && utredning.getHandlingList().stream()
                    .noneMatch(handling -> handling.getUrsprung() == HandlingUrsprungTyp.UPPDATERING
                            && handling.getInkomDatum() == null)) {
                return Optional.of(UtredningStatus.HANDLINGAR_MOTTAGNA_BOKA_BESOK);
            }

            // Om det inte finns någon handling taggad UPPDATERING och inga besök MEN vi har uppdaterad-stämpel så väntar vi på fler
            // handlingar.
            if (bestallning.getUppdateradDatum() != null && utredning.getHandlingList().stream()
                    .noneMatch(handling -> handling.getUrsprung() == HandlingUrsprungTyp.UPPDATERING
                            && handling.getInkomDatum() != null)) {
                return Optional.of(UtredningStatus.UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR);
            }

            // Om det finns någon handling taggad UPPDATERING och inga besök
            if (utredning.getHandlingList().stream().anyMatch(
                    handling -> handling.getUrsprung() == HandlingUrsprungTyp.UPPDATERING && handling.getInkomDatum() != null)) {
                return Optional.of(UtredningStatus.HANDLINGAR_MOTTAGNA_BOKA_BESOK);
            }

            // BESTALLNING_MOTTAGEN_VANTAR_PA_HANDLINGAR
            // if (utredning.getHandlingList().size() > 0 && bestallning.getUppdateradDatum() != null) {
            // return UtredningStatus.UPPDATERAD_BESTALLNING_VANTAR_PA_HANDLINGAR;
            // }

            // HANDLINGAR_MOTTAGNA_BOKA_BESOK
            if (utredning.getHandlingList().stream().anyMatch(handling -> handling.getInkomDatum() != null)) {
                return Optional.of(UtredningStatus.HANDLINGAR_MOTTAGNA_BOKA_BESOK);
            }
        }

        // UTREDNING_PAGAR innan något intyg has skickats.
        if (utredning.getBesokList().size() > 0
                && utredning.getIntygList().stream().noneMatch(intyg -> intyg.getSkickatDatum() != null)) {
            return Optional.of(UtredningStatus.UTREDNING_PAGAR);
        }

        // Skickat - ursprungsintyget är skickat.
        if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.getSkickatDatum() != null
                && intyg.getMottagetDatum() == null
                && !intyg.isKomplettering()
                && (intyg.getSistaDatum() == null || intyg.getSistaDatum().isAfter(LocalDateTime.now())))) {
            return Optional.of(UtredningStatus.UTLATANDE_SKICKAT);
        }

        // Skickat - ursprungsintyget är mottaget.
        if (utredning.getIntygList().stream().anyMatch(intyg -> intyg.getSkickatDatum() != null
                && intyg.getMottagetDatum() != null
                && !intyg.isKomplettering()
                && (intyg.getSistaDatumKompletteringsbegaran() != null
                && intyg.getSistaDatumKompletteringsbegaran().isAfter(LocalDateTime.now())))) {
            return Optional.of(UtredningStatus.UTLATANDE_MOTTAGET);
        }

        // Om vi har enbart 1 intyg så finns ej någon komplettering.
        // Om intyget har skickats, men ej tagit emot av FK och ev. slutDatum passerats,
        // vad gör vi då?
        return Optional.empty();
    }

    private static UtredningStatus handleForfraganFas(Utredning utredning) {
        // AVVISAD
        if (utredning.getExternForfragan().isPresent() && utredning.getExternForfragan().get().getAvvisatDatum() != null) {
            return UtredningStatus.AVVISAD;
        }

        // FORFRAGAN_INKOMMEN - får ej finnas några internförfrågan alls.
        if (utredning.getExternForfragan().isPresent() && utredning.getExternForfragan().get().getInternForfraganList().size() == 0) {
            return UtredningStatus.FORFRAGAN_INKOMMEN;
        }

        // VANTAR_PA_SVAR - måste finnas internförfrågan - men ingen får vara tilldelad eller accepterad.
        if (utredning.getExternForfragan().isPresent() && !isAccepterad(utredning.getExternForfragan().get().getInternForfraganList())
                && !isTilldelad(utredning.getExternForfragan().get().getInternForfraganList())) {
            return UtredningStatus.VANTAR_PA_SVAR;
        }

        // TILLDELA_UTREDNING - måste finnas internförfrågan som är accepterad - men får ej vara tilldelad.
        if (utredning.getExternForfragan().isPresent() && isAccepterad(utredning.getExternForfragan().get().getInternForfraganList())
                && !isTilldelad(utredning.getExternForfragan().get().getInternForfraganList())) {
            return UtredningStatus.TILLDELA_UTREDNING;
        }

        // TILLDELAD_VANTAR_PA_BESTALLNING - måste finnas internförfrågan som är accepterad - som är tilldelad.
        if (utredning.getExternForfragan().isPresent()
                && isAccepteradAndTilldelad(utredning.getExternForfragan().get().getInternForfraganList())) {
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
