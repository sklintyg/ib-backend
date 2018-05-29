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

package se.inera.intyg.intygsbestallning.web.controller.api.dto.bestallning;

import org.junit.Test;
import org.mockito.InjectMocks;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;

/**
 * @author Magnus Ekstrand on 2018-05-24.
 */
public class AvslutadBestallningListItemFactoryTest {

    @InjectMocks
    AvslutadBestallningListItemFactory testee = new AvslutadBestallningListItemFactory(new BusinessDaysBean(""));

    @Test
    public void testWhenBestallningIsCancelled() {
        LocalDateTime nu = LocalDateTime.now();

        final Utredning utredning = anUtredning()
                .withUtredningId(21L)
                .withUtredningsTyp(AFU)
                .withInvanare(anInvanare()
                        .withPersonId("personnummer")
                        .build())
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId("landstingHsaId")
                        .withBesvarasSenastDatum(nu)
                        .build())
                .withIntygList(Collections.singletonList(anIntyg()
                        .withSistaDatum(nu.plusDays(1))
                        .withMottagetDatum(nu.minusDays(10))
                        .build()))
                .withAvbrutenDatum(nu)
                .build();

        AvslutadBestallningListItem item = testee.from(utredning);

        assertNotNull(item);
        assertEquals(nu.format(DateTimeFormatter.ISO_DATE), item.getAvslutsDatum());
    }

    @Test
    public void testMottagetDatumIsTheHighestOne() {
        LocalDateTime nu = LocalDateTime.now();

        final Utredning utredning = anUtredning()
                .withUtredningId(21L)
                .withUtredningsTyp(AFU)
                .withInvanare(anInvanare()
                        .withPersonId("personnummer")
                        .build())
                .withExternForfragan(anExternForfragan()
                        .withLandstingHsaId("landstingHsaId")
                        .withBesvarasSenastDatum(nu)
                        .build())
                .withIntygList(Arrays.asList(
                        anIntyg().withSistaDatum(nu.plusDays(2)).withMottagetDatum(nu.minusDays(10)).build(),
                        anIntyg().withSistaDatum(nu.plusDays(5)).withMottagetDatum(nu.minusDays(7)).build(),
                        anIntyg().withSistaDatum(nu.plusDays(6)).withMottagetDatum(nu.minusDays(3)).build()))
                .build();

        AvslutadBestallningListItem item = testee.from(utredning);

        assertNotNull(item);
        assertEquals(nu.minusDays(3).format(DateTimeFormatter.ISO_DATE), item.getAvslutsDatum());
    }

}