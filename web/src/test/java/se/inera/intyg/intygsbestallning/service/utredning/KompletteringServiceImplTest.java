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
package se.inera.intyg.intygsbestallning.service.utredning;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.common.exception.IbNotFoundException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.Intyg;
import se.inera.intyg.intygsbestallning.persistence.model.Utredning;
import se.inera.intyg.intygsbestallning.persistence.model.type.HandlingUrsprungTyp;
import se.inera.intyg.intygsbestallning.persistence.repository.UtredningRepository;
import se.inera.intyg.intygsbestallning.testutil.TestDataGen;
import se.inera.intyg.intygsbestallning.web.responder.dto.ReportKompletteringMottagenRequest;
import se.riv.intygsbestallning.certificate.order.reportsupplementreceival.v1.ReportSupplementReceivalType;
import se.riv.intygsbestallning.certificate.order.requestmedicalcertificatesupplement.v1.RequestMedicalCertificateSupplementType;
import se.riv.intygsbestallning.certificate.order.v1.IIType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;
import static se.inera.intyg.intygsbestallning.persistence.model.Besok.BesokBuilder.aBesok;
import static se.inera.intyg.intygsbestallning.persistence.model.Handling.HandlingBuilder.aHandling;
import static se.inera.intyg.intygsbestallning.persistence.model.Intyg.IntygBuilder.anIntyg;

@RunWith(MockitoJUnitRunner.class)
public class KompletteringServiceImplTest {

    @Mock
    private UtredningRepository utredningRepository;

    @InjectMocks
    private KompletteringServiceImpl kompletteringService;

    @Test
    public void testCreateOk() {
        Utredning utredning = ServiceTestUtil.buildBestallningar(1).get(0);
        Intyg i = utredning.getIntygList().get(0);
        utredning.setIntygList(new ArrayList<>());
        utredning.getIntygList().add(i);

        when(utredningRepository.findById(anyLong())).thenReturn(Optional.of(utredning));
        when(utredningRepository.findNewestKompletteringOnUtredning(anyLong())).thenReturn(Optional.of(2L));
        Long kompletteringsId = kompletteringService.registerNewKomplettering(buildRequest());

        assertEquals(new Long(2L), kompletteringsId);

        verify(utredningRepository).saveUtredning(any(Utredning.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateFailsDueToLastDateOfKompltPassed() {
        try {
            Utredning utredning = ServiceTestUtil.buildBestallningar(1).get(0);
            Intyg i = utredning.getIntygList().get(0);
            i.setSistaDatumKompletteringsbegaran(LocalDateTime.now().minusDays(100));
            utredning.setIntygList(new ArrayList<>());
            utredning.getIntygList().add(i);

            when(utredningRepository.findById(anyLong())).thenReturn(Optional.of(utredning));
            kompletteringService.registerNewKomplettering(buildRequest());
        } catch (Exception e) {
            verify(utredningRepository, times(0)).saveUtredning(any(Utredning.class));
            throw e;
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testUtredningDoesNotExist() {
        when(utredningRepository.findById(anyLong())).thenReturn(Optional.empty());
        kompletteringService.registerNewKomplettering(buildRequest());
    }

    @Test
    public void testReportKompletteringMottagenOk() {

        final String utredningId = "1";
        final String kompletteringId = "2";
        final String mottagetDatum = "20181111";
        final String sistaDatum = "20181211";

        ReportSupplementReceivalType type = new ReportSupplementReceivalType();
        type.setAssessmentId(anII("", utredningId));
        type.setReceivedDate(mottagetDatum);
        type.setSupplementRequestId(anII("", kompletteringId));
        type.setLastDateForSupplementRequest(sistaDatum);

        final ReportKompletteringMottagenRequest request = ReportKompletteringMottagenRequest.from(type);

        final LocalDateTime localDateTime = LocalDateTime.of(2018, 11, 10, 0, 0, 0, 0);

        Utredning utredning = TestDataGen.createUtredning();
        utredning.getHandlingList().add(aHandling()
                .withId(Long.valueOf(utredningId))
                .withSkickatDatum(localDateTime)
                .withInkomDatum(localDateTime)
                .withUrsprung(HandlingUrsprungTyp.BESTALLNING)
                .build());
        utredning.getBesokList().add(aBesok()
                .build());
        utredning.setIntygList(ImmutableList.of(
                anIntyg()
                        .withId(Long.valueOf(kompletteringId) + 1)
                        .withSkickatDatum(localDateTime)
                        .withSistaDatumKompletteringsbegaran(localDateTime.plusMonths(1))
                        .build(),
                anIntyg()
                        .withId(Long.valueOf(kompletteringId))
                        .withKomplettering(true)
                        .withSkickatDatum(localDateTime.plusMonths(2))
                        .withSistaDatumKompletteringsbegaran(localDateTime.plusMonths(4))
                        .build()));

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(Long.valueOf(utredningId));

        kompletteringService.reportKompletteringMottagen(request);
    }

    @Test
    public void testReportKompletteringMottagenUtredningNotFound() {

        final String utredningId = "1";
        final String kompletteringId = "2";
        final String mottagetDatum = "20181111";
        final String sistaDatum = "20181211";

        ReportSupplementReceivalType type = new ReportSupplementReceivalType();
        type.setAssessmentId(anII("", utredningId));
        type.setReceivedDate(mottagetDatum);
        type.setSupplementRequestId(anII("", kompletteringId));
        type.setLastDateForSupplementRequest(sistaDatum);

        final ReportKompletteringMottagenRequest request = ReportKompletteringMottagenRequest.from(type);

        final LocalDateTime localDateTime = LocalDateTime.of(2018, 11, 10, 0, 0, 0, 0);

        doReturn(Optional.empty())
                .when(utredningRepository)
                .findById(Long.valueOf(utredningId));

        assertThatThrownBy(() -> kompletteringService.reportKompletteringMottagen(request))
                .isExactlyInstanceOf(IbNotFoundException.class);
    }

    @Test
    public void testReportKompletteringMottagenUtredningNotInCorrectState() {

        final String utredningId = "1";
        final String kompletteringId = "2";
        final String mottagetDatum = "20181111";
        final String sistaDatum = "20181211";

        ReportSupplementReceivalType type = new ReportSupplementReceivalType();
        type.setAssessmentId(anII("", utredningId));
        type.setReceivedDate(mottagetDatum);
        type.setSupplementRequestId(anII("", kompletteringId));
        type.setLastDateForSupplementRequest(sistaDatum);

        final LocalDateTime localDateTime = LocalDateTime.of(2018, 11, 10, 0, 0, 0, 0);
        final ReportKompletteringMottagenRequest request = ReportKompletteringMottagenRequest.from(type);

        Utredning utredning = TestDataGen.createUtredning();
        utredning.getHandlingList().add(aHandling()
                .withId(Long.valueOf(utredningId))
                .withSkickatDatum(localDateTime)
                .withInkomDatum(localDateTime)
                .withUrsprung(HandlingUrsprungTyp.BESTALLNING)
                .build());

        doReturn(Optional.of(utredning))
                .when(utredningRepository)
                .findById(Long.valueOf(utredningId));

        assertThatThrownBy(() -> kompletteringService.reportKompletteringMottagen(request))
                .isExactlyInstanceOf(IbServiceException.class)
                .hasMessageEndingWith("is in an incorrect state.");
    }

    private RequestMedicalCertificateSupplementType buildRequest() {
        RequestMedicalCertificateSupplementType req = new RequestMedicalCertificateSupplementType();
        IIType id = new IIType();
        id.setExtension("1");
        req.setAssessmentId(id);
        req.setLastDateForSupplementReceival(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        return req;
    }
}