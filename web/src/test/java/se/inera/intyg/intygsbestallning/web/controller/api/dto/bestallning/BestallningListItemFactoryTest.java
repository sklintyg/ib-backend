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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.Actor;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysStub;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static se.inera.intyg.intygsbestallning.persistence.model.Bestallning.BestallningBuilder.aBestallning;
import static se.inera.intyg.intygsbestallning.persistence.model.ExternForfragan.ExternForfraganBuilder.anExternForfragan;
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;
import static se.inera.intyg.intygsbestallning.persistence.model.Invanare.InvanareBuilder.anInvanare;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;

/**
 * @author Magnus Ekstrand on 2018-05-24.
 */
public class BestallningListItemFactoryTest {

    private static final int UTREDNING_PAMINNELSE_DAGAR = 5;

    private static final String VARDENHET_HSA_ID = "enhet";

    private UtredningStatusResolver utredningStatusResolver = new UtredningStatusResolver();

    @Spy
    private BusinessDaysBean businessDays = new BusinessDaysStub();

    @InjectMocks
    private BestallningListItemFactory testee = new BestallningListItemFactory();

    @Before
    public void setup() {
    }

    @Before
    public void injectSpringBeans() {
        ReflectionTestUtils.setField(testee, "paminnelseDagar", UTREDNING_PAMINNELSE_DAGAR);
        // Since we are not using a Spring context, and, injectmocks doesnt seem to work on subclasses (?),
        // DP inject/Autowire manually.
        ReflectionTestUtils.setField(testee, "businessDays", new BusinessDaysStub());
    }

    @Test
    public void testSlutdatumPasserat() {
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
                .withIntygList(Collections.singletonList(anIntyg()
                        .withSistaDatum(LocalDateTime.now().minusDays(2))
                        .withKomplettering(false)
                        .build()))
                .build();
// use the resolver to set status even in the test...
        utredning.setStatus(utredningStatusResolver.resolveStatus(utredning));
        BestallningListItem bestallningListItem = testee.from(utredning, Actor.SAMORDNARE);

        assertNotNull(bestallningListItem);
        assertTrue(bestallningListItem.getSlutdatumPasserat());
        assertFalse(bestallningListItem.getSlutdatumPaVagPasseras());
        assertTrue(bestallningListItem.getKraverAtgard());
    }

    @Test
    public void testSlutdatumPaVagAttPasseras() {
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
                .withIntygList(Collections.singletonList(anIntyg()
                        .withSistaDatum(LocalDateTime.now().plusDays(2))
                        .withKomplettering(false)
                        .build()))
                .withBestallning(aBestallning().build())
                .build();
        utredning.setStatus(utredningStatusResolver.resolveStatus(utredning));
        BestallningListItem bestallningListItem = testee.from(utredning, Actor.UTREDARE);

        assertNotNull(bestallningListItem);
        assertFalse(bestallningListItem.getSlutdatumPasserat());
        assertTrue(bestallningListItem.getSlutdatumPaVagPasseras());
        assertFalse(bestallningListItem.getKraverAtgard());
    }

}
