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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysStub;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.UtredningListItem;
import se.inera.intyg.intygsbestallning.web.controller.api.dto.utredning.UtredningListItemFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;

@RunWith(MockitoJUnitRunner.class)
public class UtredningListItemFactoryTest {

    @InjectMocks
    private UtredningListItemFactory testee = new UtredningListItemFactory();

    @Before
    public void injectSpringBeans() {
        // Since we are not using a Spring context, and, injectmocks doesnt seem to work on subclasses (?),
        // DP inject/Autowire manually.
        ReflectionTestUtils.setField(testee, "businessDays", new BusinessDaysStub());
    }

    @Test
    public void testFrom() {

        final Utredning utredning = anUtredning()
                .withUtredningId(21L)
                .withUtredningsTyp(AFU)
                .withInvanare(anInvanare()
                        .withPersonId("personnummer")
                        .build())
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId("landstingHsaId")
                        .withBesvarasSenastDatum(LocalDateTime.now())
                        .build())
                .withStatus(UtredningStatus.FORFRAGAN_INKOMMEN)
                .build();

        UtredningListItem response = testee.from(utredning);

        assertNotNull(response);
        assertEquals("Förfrågan", response.getFas().getLabel());
        assertEquals(LocalDate.now().format(DateTimeFormatter.ISO_DATE), response.getSlutdatumFas());
        assertEquals("Förfrågan inkommen", response.getStatus().getLabel());
        assertEquals(Long.valueOf(21L), response.getUtredningsId());
        assertEquals(AFU, response.getUtredningsTyp());
        // assertEquals("landstingHsaId", response.getVardenhetNamn());
    }
}
