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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringMottagarTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.NotifieringTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningAndIntyg;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.service.notifiering.send.NotifieringSendService;
import se.inera.intyg.intygsbestallning.service.util.BusinessDaysBean;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;

@RunWith(MockitoJUnitRunner.class)
public class PaminnelseSistaDatumKompletteringJobTest {

    @Mock
    private UtredningRepository utredningRepository;

    @Mock
    private NotifieringSendService notifieringSendService;

    @Mock
    private BusinessDaysBean businessDaysBean;

    @InjectMocks
    private PaminnelseSistaDatumKompletteringJob job;

    @Test
    public void testPaminnelseSistaDatumKompletteringJobOk() {

        final LocalDateTime localDateTime = LocalDateTime.of(2018, 9, 9, 9, 9, 9, 9);

        Utredning utredning = TestDataGen.createUtredning();

        utredning.setIntygList(Lists.newArrayList(
                anIntyg()
                        .withKomplettering(false)
                        .withSistaDatum(localDateTime.minusDays(3))
                        .build(),
                anIntyg()
                        .withKomplettering(true)
                        .withSistaDatum(localDateTime.minusDays(2))
                        .build(),
                anIntyg()
                        .withKomplettering(true)
                        .withSistaDatum(localDateTime.minusDays(1))
                        .build(),
                anIntyg()
                        .withKomplettering(true)
                        .withSistaDatum(localDateTime.plusDays(10))
                        .build()
        ));


        doReturn(ImmutableList.of(new UtredningAndIntyg(utredning, utredning.getIntygList().get(3))))
                .when(utredningRepository)
                .findNonNotifiedSistadatumKompletteringBefore(any(LocalDateTime.class), eq(NotifieringTyp.PAMINNELSEDATUM_KOMPLETTERING_PASSERAS), eq(NotifieringMottagarTyp.VARDENHET));

        //hack to set @Value annotated field paminnelseArbetsdagar in PaminnelseSistaDatumKompletteringJob
        Field field = ReflectionUtils.findField(PaminnelseSistaDatumKompletteringJob.class, "paminnelseArbetsdagar");
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, job, 2);

        doReturn(localDateTime.toLocalDate())
                .when(businessDaysBean)
                .addBusinessDays(any(LocalDate.class), anyInt());

        job.executeJob();

        verify(utredningRepository, times(1))
                .findNonNotifiedSistadatumKompletteringBefore(
                        any(LocalDateTime.class), eq(NotifieringTyp.PAMINNELSEDATUM_KOMPLETTERING_PASSERAS), eq(NotifieringMottagarTyp.VARDENHET));

        verify(notifieringSendService, times(1))
                .notifieraVardenhetPaminnelseSlutdatumKomplettering(eq(utredning), eq(utredning.getIntygList().get(3)));
    }
}