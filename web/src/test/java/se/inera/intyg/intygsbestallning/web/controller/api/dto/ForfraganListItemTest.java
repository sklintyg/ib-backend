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
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.stateresolver.InternForfraganStateResolver;
import se.inera.intyg.intygsbestallning.service.stateresolver.InternForfraganStatus;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysStub;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.ForfraganSvar.ForfraganSvarBuilder.aForfraganSvar;
import static se.inera.intyg.intygsbestallning.persistence.model.InternForfragan.InternForfraganBuilder.anInternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;

@RunWith(MockitoJUnitRunner.class)
public class ForfraganListItemTest {

    private static final String VARDENHET_HSA_ID = "enhet";

    private BusinessDaysBean businessDays = new BusinessDaysStub();

    @Test
    public void testFrom() {

        final Utredning utredning = anUtredning()
                .withUtredningId(31L)
                .withUtredningsTyp(AFU)
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId("landstingHsaId")
                        .withInternForfraganList(Collections.singletonList(anInternForfragan()
                                .withBesvarasSenastDatum(LocalDate.of(2019, 1, 1).atStartOfDay())
                                .withSkapadDatum(LocalDate.of(2018, 1, 1).atStartOfDay())
                                .withVardenhetHsaId(VARDENHET_HSA_ID)
                                .withForfraganSvar(aForfraganSvar()
                                        .withBorjaDatum(LocalDate.of(2020, 1, 1))
                                        .build())
                                .build()))
                        .build())
                .build();

        ForfraganListItem response =
                ForfraganListItem.from(utredning, VARDENHET_HSA_ID, new InternForfraganStateResolver(), businessDays);

        assertNotNull(response);
        assertEquals(Long.valueOf(31L), response.getUtredningsId());
        assertEquals(AFU.name(), response.getUtredningsTyp());
        assertEquals("2019-01-01", response.getBesvarasSenastDatum());
        assertEquals("2018-01-01", response.getInkomDatum());
        // assertEquals("2020-01-01", response.getPlaneringsDatum());
        assertEquals(InternForfraganStatus.INKOMMEN, response.getStatus());
        assertEquals("landstingHsaId", response.getVardgivareNamn());
    }

    @Test
    public void testBesvaraSenastSnartFlaggaShowForClose() {
        final Utredning utredning = buildUtredningWithBesvaraSenastDatum(LocalDate.now().plusDays(1).atStartOfDay());
        ForfraganListItem response = ForfraganListItem.from(utredning, VARDENHET_HSA_ID, new InternForfraganStateResolver(), businessDays);

        assertNotNull(response);
        assertTrue(response.isBesvarasSenastDatumPaVagPasseras());
        assertFalse(response.isBesvarasSenastDatumPasserat());
    }

    @Test
    public void testBesvaraSenastSnartFlaggaNotShownForFar() {
        final Utredning utredning = buildUtredningWithBesvaraSenastDatum(LocalDate.now().plusYears(1).atStartOfDay());
        ForfraganListItem response = ForfraganListItem.from(utredning, VARDENHET_HSA_ID, new InternForfraganStateResolver(), businessDays);

        assertNotNull(response);
        assertFalse(response.isBesvarasSenastDatumPaVagPasseras());
        assertFalse(response.isBesvarasSenastDatumPasserat());
    }

    @Test
    public void testBesvaraSenastPasserat() {
        final Utredning utredning = buildUtredningWithBesvaraSenastDatum(LocalDate.now().minusDays(1).atStartOfDay());
        ForfraganListItem response = ForfraganListItem.from(utredning, VARDENHET_HSA_ID, new InternForfraganStateResolver(), businessDays);

        assertNotNull(response);
        assertFalse(response.isBesvarasSenastDatumPaVagPasseras());
        assertTrue(response.isBesvarasSenastDatumPasserat());
    }

    private Utredning buildUtredningWithBesvaraSenastDatum(LocalDateTime besvaraSenastDatum) {
        return anUtredning()
                    .withUtredningsTyp(AFU)
                    .withExternForfragan(anExternForfragan()

                            .withInternForfraganList(Collections.singletonList(anInternForfragan()
                                    .withVardenhetHsaId(VARDENHET_HSA_ID)
                                    .withBesvarasSenastDatum(besvaraSenastDatum)
                                    .build()))
                            .build())
                    .build();
    }

}
