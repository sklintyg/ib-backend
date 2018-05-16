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
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Handlaggare.HandlaggareBuilder.aHandlaggare;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;

public class GetUtredningResponseTest {

    @Test
    public void testFrom() {
        Utredning utredning = anUtredning()
                .withUtredningId(123321L)
                .withUtredningsTyp(AFU)
                .withTolkBehov(true)
                .withTolkSprak("sv")
                .withExternForfragan(anExternForfragan()
                        .withBesvarasSenastDatum(LocalDateTime.of(2019, 1, 1, 0, 0))
                        .withInkomDatum(LocalDateTime.of(2018, 1, 1, 0, 0))
                        .withLandstingHsaId("landstingHsaId")
                        .build())
                .withHandlaggare(aHandlaggare()
                        .withTelefonnummer("telefonnummer")
                        .withFullstandigtNamn("fullstandigtnamn")
                        .withEmail("email")
                        .build())
                .withInvanare(anInvanare()
                        .withPostort("bostadsort")
                        .withPersonId("personnummer")
                        .build())
                .build();
        GetUtredningResponse response = GetUtredningResponse.from(utredning, UtredningStatus.FORFRAGAN_INKOMMEN);

        assertNotNull(response);
        assertEquals(Long.valueOf(123321L), response.getUtredningsId());
        assertEquals("2019-01-01", response.getBesvarasSenastDatum());
        assertEquals("email", response.getHandlaggareEpost());
        assertEquals("fullstandigtnamn", response.getHandlaggareNamn());
        assertEquals("telefonnummer", response.getHandlaggareTelefonnummer());
        assertEquals("2018-01-01", response.getInkomDatum());
        assertEquals("bostadsort", response.getBostadsort());
        assertEquals("sv", response.getTolkSprak());
        assertTrue(response.isBehovTolk());
    }
}
