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
package se.inera.intyg.intygsbestallning.jobs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static se.inera.intyg.intygsbestallning.persistence.model.Besok.BesokBuilder.aBesok;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.time.LocalDateTime;

import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.status.UtredningStatus;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.dto.AvslutaUtredningRequest;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;


@RunWith(MockitoJUnitRunner.class)
public class AvslutaUtredningJobTest {

    @Mock
    private UtredningRepository utredningRepository;

    @Mock
    private UtredningService utredningService;

    @InjectMocks
    private AvslutaUtredningJob avslutaUtredningJob;

    @Test
    public void test() {

        //Utredning 1 and 2 should have a Handling with Type Avslutad
        final Utredning utredning1 = TestDataGen.createUtredning();
        utredning1.setStatus(UtredningStatus.KOMPLETTERING_MOTTAGEN);
        utredning1.getIntygList().get(0).setSistaDatumKompletteringsbegaran(LocalDateTime.now().minusDays(1));

        final Utredning utredning2 = TestDataGen.createUtredning();
        utredning2.setStatus(UtredningStatus.UTLATANDE_MOTTAGET);
        utredning2.getIntygList().get(0).setSistaDatumKompletteringsbegaran(LocalDateTime.now().minusDays(1));

        //Should be an Utredning with incorrect conditions to be Avslutad
        final Utredning utredning3 = TestDataGen.createUtredning();
        utredning3.setStatus(UtredningStatus.AVVIKELSE_MOTTAGEN);
        utredning3.getIntygList().get(0).setSistaDatumKompletteringsbegaran(LocalDateTime.now().minusDays(1));

        //Should match the condition to notify Vardgivare - REDOVISA BESOK
        final Utredning utredning4 = TestDataGen.createUtredning();
        utredning4.setStatus(UtredningStatus.UTLATANDE_MOTTAGET);
        utredning4.getIntygList().get(0).setSistaDatumKompletteringsbegaran(LocalDateTime.now().minusDays(1));
        utredning4.getBesokList().add(aBesok()
                .withTolkStatus(TolkStatusTyp.BOKAD)
                .build());

        doReturn(ImmutableList.of(utredning1, utredning2, utredning3, utredning4))
                .when(utredningRepository)
                .findSistaDatumKompletteringsBegaranBefore(
                        any(LocalDateTime.class));

        avslutaUtredningJob.executeJob();

        verify(utredningService, times(2)).avslutaUtredning(any(AvslutaUtredningRequest.class));
        verify(utredningService, times(1)).updateStatusToRedovisaBesok(any(Utredning.class));
    }
}