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
package se.inera.intyg.intygsbestallning.job;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.intygsbestallning.jobs.PaminnelseSlutdatumForUtredningPasserasJob;
import se.inera.intyg.intygsbestallning.persistence.model.Besok;
import se.inera.intyg.intygsbestallning.persistence.model.Handling;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandlingUrsprungTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.notifiering.send.NotifieringSendService;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatusResolver;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysStub;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaminnelseSlutdatumForUtredningPasserasJobTest {

    @Spy
    private BusinessDaysStub businessDaysBean;

    @Mock
    private UtredningRepository utredningRepository;

    @Mock
    private NotifieringSendService notifieringSendService;

    @InjectMocks
    private PaminnelseSlutdatumForUtredningPasserasJob testee;

    @Before
    public void init() {
        ReflectionTestUtils.setField(testee, "paminnelseArbetsdagar", 5);
    }

    @Test
    public void testJobNotifiesWhenNotNotified() {
        Utredning utredning = TestDataGen.createUtredningWithSkickatOchMottagetIntyg();
        utredning.getIntygList().get(0).setSistaDatum(LocalDateTime.now().plusDays(3L));
        when(utredningRepository.findNonNotifiedIntygSlutDatumBetween(any(LocalDateTime.class), any(LocalDateTime.class),
                any(NotifieringTyp.class), any(NotifieringMottagarTyp.class)))
                        .thenReturn(Arrays.asList(utredning));

        testee.executeJob();
        verify(notifieringSendService, times(1)).notifieraVardenhetPaminnelseSlutdatumUtredning(any(Utredning.class));
    }

    @Test
    public void testJobDoesNotNotifyWhenAlreadyNotified() {
        Utredning utredning = TestDataGen.createUtredningWithSkickatOchMottagetIntyg();
        utredning.getIntygList().get(0).setSistaDatum(LocalDateTime.now().plusDays(3L));
        when(utredningRepository.findNonNotifiedIntygSlutDatumBetween(any(LocalDateTime.class), any(LocalDateTime.class),
                any(NotifieringTyp.class), any(NotifieringMottagarTyp.class)))
                        .thenReturn(Collections.emptyList());

        testee.executeJob();
        verifyZeroInteractions(notifieringSendService);
    }

    @Test
    public void testJobDoesNotNotifyWhenInRedovisaTolk() {
        Utredning utredning = TestDataGen.createUtredningWithSkickatOchMottagetIntyg();
        utredning.getHandlingList().add(Handling.HandlingBuilder.aHandling()
                .withInkomDatum(LocalDateTime.now())
                .withSkickatDatum(LocalDateTime.now())
                .withUrsprung(HandlingUrsprungTyp.BESTALLNING).build());

        utredning.getBesokList().add(Besok.BesokBuilder.aBesok()
                .withBesokStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT)
                .withErsatts(true)
                .withTolkStatus(TolkStatusTyp.BOKAT)
                .build());
        utredning.getIntygList().get(0).setSistaDatum(LocalDateTime.now().plusDays(3L));
        utredning.getIntygList().get(0).setMottagetDatum(LocalDateTime.now());
        utredning.getIntygList().get(0).setSistaDatumKompletteringsbegaran(LocalDateTime.now().minusMonths(3));

        // Verify correct state
        UtredningStatus utredningStatus = new UtredningStatusResolver().resolveStatus(utredning);
        if (utredningStatus != UtredningStatus.REDOVISA_BESOK) {
            fail("Test setup must provide a Utredning in status REDOVISA_BESOK");
        }
        when(utredningRepository.findNonNotifiedIntygSlutDatumBetween(any(LocalDateTime.class), any(LocalDateTime.class),
                any(NotifieringTyp.class), any(NotifieringMottagarTyp.class)))
                        .thenReturn(Arrays.asList(utredning));

        testee.executeJob();
        verifyZeroInteractions(notifieringSendService);
    }
}
