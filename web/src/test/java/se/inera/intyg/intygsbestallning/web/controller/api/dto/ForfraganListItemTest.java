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
package se.inera.intyg.intygsbestallning.web.controller.api.dto;

import org.junit.Test;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.stateresolver.InternForfraganStateResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.InternForfraganStatus;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar.ForfraganSvarBuilder.aForfraganSvar;
import static se.inera.intyg.intygsbestallning.persistence.model.InternForfragan.InternForfraganBuilder.anInternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;

public class ForfraganListItemTest {

    @Test
    public void testFrom() {
        final String vardenhetHsaId = "enhet";
        final Utredning utredning = anUtredning()
                .withUtredningId("utredningId")
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId("landstingHsaId")
                        .withInternForfraganList(Collections.singletonList(anInternForfragan()
                                .withBesvarasSenastDatum(LocalDate.of(2019, 1, 1).atStartOfDay())
                                .withSkapadDatum(LocalDate.of(2018, 1, 1).atStartOfDay())
                                .withVardenhetHsaId(vardenhetHsaId)
                                .withForfraganSvar(aForfraganSvar()
                                        .withBorjaDatum(LocalDate.of(2020, 1, 1))
                                        .build())
                                .build()))
                        .build())
                .build();

        ForfraganListItem response = ForfraganListItem.from(utredning, vardenhetHsaId, new InternForfraganStateResolver());

        assertNotNull(response);
        assertEquals("utredningId", response.getUtredningsId());
        assertEquals(AFU.name(), response.getUtredningsTyp());
        assertEquals("2019-01-01", response.getBesvarasSenastDatum());
        assertEquals("2018-01-01", response.getInkomDatum());
        assertEquals("2020-01-01", response.getPlaneringsDatum());
        assertEquals(InternForfraganStatus.INKOMMEN, response.getStatus());
        assertEquals("landstingHsaId", response.getVardgivareNamn());
    }
}
