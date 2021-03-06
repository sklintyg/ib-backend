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

import com.google.common.collect.Lists;
import se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.InternForfragan;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvslutOrsak;
import se.inera.intyg.intygsbestallning.persistence.model.type.SvarTyp;

/**
 * Class resposible for resolving the status of an InternForfragan.
 */
public class InternForfraganStatusResolver {

    public InternForfraganStatus resolveStatus(final Utredning utredning, final InternForfragan internForfragan) {
        return resolveStaticStatus(utredning, internForfragan);
    }

    public static InternForfraganStatus resolveStaticStatus(Utredning utredning, InternForfragan internForfragan) {

        // När externförfrågan avvisas - Systemet uppdaterar internförfrågningar i fas Förfrågan till status Ej tilldelad
        if (utredning.getExternForfragan().isPresent() && utredning.getExternForfragan().get().getAvvisatDatum() != null) {
            if (internForfragan.getForfraganSvar() == null || internForfragan.getForfraganSvar().getSvarTyp() != SvarTyp.AVBOJ) {
                return InternForfraganStatus.EJ_TILLDELAD;
            }
        }

        // Inkommen
        if (internForfragan.getForfraganSvar() == null) {
            return InternForfraganStatus.INKOMMEN;
        }

        // Inkommen, should only happen in badly written unit tests since svarTyp is not nullable.
        if (internForfragan.getForfraganSvar() != null && internForfragan.getForfraganSvar().getSvarTyp() == null) {
            return InternForfraganStatus.INKOMMEN;
        }

        // Avvisad.
        if (internForfragan.getForfraganSvar().getSvarTyp() == SvarTyp.AVBOJ) {
            return InternForfraganStatus.AVVISAD;
        }

        // Direkttilldelad, lite förvirrat då isTilldelad ej är true ännu. När detta sker går status över till
        // TILLDELAD_VANTAR_PA_BESTALLNING
        if (internForfragan.getDirekttilldelad() != null && internForfragan.getDirekttilldelad() && !isTilldelad(utredning)) {
            return InternForfraganStatus.DIREKTTILLDELAD;
        }

        // Accepterad - får ej vara tilldelad till någon alls.
        if (internForfragan.getForfraganSvar().getSvarTyp() == SvarTyp.ACCEPTERA && !isTilldelad(utredning)) {
            return InternForfraganStatus.ACCEPTERAD_VANTAR_PA_TILLDELNINGSBESLUT;
        }


        // Tilldelad till annan (ej tilldelad)
        if (internForfragan.getForfraganSvar().getSvarTyp() == SvarTyp.ACCEPTERA && internForfragan.getTilldeladDatum() == null
                && isTilldelad(utredning)) {
            return InternForfraganStatus.EJ_TILLDELAD;
        }

        // States with self having accepted + tilldelad
        if (internForfragan.getForfraganSvar().getSvarTyp() == SvarTyp.ACCEPTERA && internForfragan.getTilldeladDatum() != null) {

            // Tilldelad enligt ovanstående kriterier, men Ingen beställning
            if (utredning.getAvbrutenOrsak() != null && utredning.getAvbrutenOrsak() == AvslutOrsak.INGEN_BESTALLNING) {
                return InternForfraganStatus.INGEN_BESTALLNING;
            }

            // Tilldelad enligt ovanstående kriterier, Beställd
            if (internForfragan.getForfraganSvar().getSvarTyp() == SvarTyp.ACCEPTERA && internForfragan.getTilldeladDatum() != null
                    && utredning.getBestallning().isPresent()) {
                return InternForfraganStatus.BESTALLD;
            }

            // Tilldelad enligt ovanstående kriterier, kan komma från DIREKTTILLDELAD eller ACCEPTERAD_VANTAR_PA_TILLDELNINGSBESLUT.
            if (!utredning.getBestallning().isPresent()) {
                // Tilldelad, väntar på...
                return InternForfraganStatus.TILLDELAD_VANTAR_PA_BESTALLNING;
            }
        }

        throw new IllegalStateException("Unable to resolve InternForfraganStatus, unresolvable state.");
    }

    private static boolean isTilldelad(Utredning utredning) {
        return utredning.getExternForfragan()
                .map(ExternForfragan::getInternForfraganList)
                .orElse(Lists.newArrayList()).stream()
                .anyMatch(intf -> intf.getTilldeladDatum() != null);
    }

}
