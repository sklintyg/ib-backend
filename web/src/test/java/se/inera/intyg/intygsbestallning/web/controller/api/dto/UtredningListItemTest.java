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
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStateResolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.UtredningsTyp.AFU;

public class UtredningListItemTest {

    private UtredningStateResolver utredningStateResolver = new UtredningStateResolver();

    @Test
    public void testFrom() {

        final Utredning utredning = anUtredning()
                .withUtredningId("utredningId")
                .withUtredningsTyp(AFU)
                .withInvanare(anInvanare()
                        .withPersonId("personnummer")
                        .build())
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId("landstingHsaId")
                        .build())
                .build();

        UtredningListItem response = UtredningListItem.from(utredning, utredningStateResolver.resolveStatus(utredning));

        assertNotNull(response);
        assertEquals("FORFRAGAN", response.getFas());
        assertEquals("personnummer", response.getPatientId());
        assertEquals("TODO", response.getSlutdatumFas());
        assertEquals("FORFRAGAN_INKOMMEN", response.getStatus());
        assertEquals("utredningId", response.getUtredningsId());
        assertEquals(AFU.name(), response.getUtredningsTyp());
        assertEquals("landstingHsaId", response.getVardgivareNamn());
    }
}
