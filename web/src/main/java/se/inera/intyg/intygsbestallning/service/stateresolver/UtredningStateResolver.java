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

import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.persistence.model.Bestallning;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.SvarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;

import java.util.List;

@Service
public class UtredningStateResolver {

    public UtredningStatus resolveStatus(Utredning utredning) {
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

        // Second phase - Utredning. We ALWAYS have a Bestallning here.

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
        if (utredning.getHandlingList().size() > 0 && utredning.getBesokList().size() > 0) {
            return UtredningStatus.UTREDNING_PAGAR;
        }

        // Third phase - Komplettering?


        throw new IllegalStateException("Unhandled state!");
    }

    private boolean isAccepteradAndTilldelad(List<InternForfragan> internForfraganList) {
        return internForfraganList.stream().anyMatch(internForfragan -> internForfragan.getTilldeladDatum() != null
                && internForfragan.getForfraganSvar().getSvarTyp() == SvarTyp.ACCEPTERA);
    }

    private boolean isAccepterad(List<InternForfragan> internForfraganList) {
        return internForfraganList.stream().filter(internForfragan -> internForfragan.getForfraganSvar() != null)
                .anyMatch(internForfragan -> internForfragan.getForfraganSvar().getSvarTyp() == SvarTyp.ACCEPTERA);
    }

    private boolean isTilldelad(List<InternForfragan> internForfraganList) {
        return internForfraganList.stream()
                .anyMatch(internForfragan -> internForfragan.getForfraganSvar() != null && internForfragan.getTilldeladDatum() != null);
    }
}
