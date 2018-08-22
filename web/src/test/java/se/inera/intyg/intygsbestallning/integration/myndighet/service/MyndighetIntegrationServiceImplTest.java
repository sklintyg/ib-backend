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
package se.inera.intyg.intygsbestallning.integration.myndighet.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.common.exception.IbExternalServiceException;
import se.inera.intyg.intygsbestallning.common.util.SchemaDateUtil;
import se.inera.intyg.intygsbestallning.integration.myndighet.client.MyndighetIntegrationClientServiceImpl;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportDeviationRequestDto;
import se.inera.intyg.intygsbestallning.persistence.model.type.*;
import se.riv.intygsbestallning.certificate.order.reportcarecontact.v1.ReportCareContactResponseType;
import se.riv.intygsbestallning.certificate.order.reportdeviation.v1.ReportDeviationResponseType;
import se.riv.intygsbestallning.certificate.order.updateassessment.v1.UpdateAssessmentResponseType;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static se.inera.intyg.intygsbestallning.common.util.ResultTypeUtil.error;
import static se.inera.intyg.intygsbestallning.common.util.ResultTypeUtil.ok;
import static se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto.ReportCareContactRequestDtoBuilder.aReportCareContactRequestDto;
import static se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportDeviationRequestDto.ReportDeviationRequestDtoBuilder.aReportDeviationRequestDto;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.DATE_TIME;

@RunWith(MockitoJUnitRunner.class)
public class MyndighetIntegrationServiceImplTest {

    @Mock
    private MyndighetIntegrationClientServiceImpl clientService;

    @InjectMocks
    private MyndighetIntegrationServiceImpl integrationService;

    @Test
    public void testReportCareContactInteraction() {

        final ReportCareContactRequestDto requestDto = aReportCareContactRequestDto()
                .withAssessmentId(1L)
                .withAssessmentCareContactId("assessment-care-contact-id")
                .withParticipatingProfession(DeltagarProfessionTyp.LK.getLabel())
                .withInterpreterStatus(TolkStatusTyp.BOKAD.getLabel())
                .withInvitationDate(DATE_TIME.toString())
                .withInvitationChannel(KallelseFormTyp.TELEFONKONTAKT.getCvValue())
                .withStartTime(DATE_TIME)
                .withEndTime(DATE_TIME.plusHours(1))
                .withVisitStatus(BesokStatusTyp.TIDBOKAD_VARDKONTAKT.getCvValue())
                .build();

        final ReportCareContactResponseType response = new ReportCareContactResponseType();
        response.setResult(ok());

        doReturn(response)
                .when(clientService)
                .reportCareContact(eq(requestDto));

        integrationService.reportCareContactInteraction(requestDto);

        verify(clientService, times(1)).reportCareContact(eq(requestDto));
    }

    @Test
    public void testReportCareContactResultError() {

        ReportCareContactResponseType response = new ReportCareContactResponseType();
        response.setResult(error("error text"));

        doReturn(response)
                .when(clientService)
                .reportCareContact(any(ReportCareContactRequestDto.class));

        assertThatThrownBy(() -> integrationService.reportCareContactInteraction(aReportCareContactRequestDto().build()))
                .isExactlyInstanceOf(IbExternalServiceException.class);

        verify(clientService, times(1))
                .reportCareContact(any(ReportCareContactRequestDto.class));
    }

    @Test
    public void testUpdateAssessment() {

        final Long assessmentId = 1L;
        final String certificateType = UtredningsTyp.AFU_UTVIDGAD.name();

        UpdateAssessmentResponseType response = new UpdateAssessmentResponseType();
        response.setLastDateForCertificateReceival(SchemaDateUtil.toDateStringFromLocalDateTime(DATE_TIME));
        response.setResult(ok());

        doReturn(response)
                .when(clientService)
                .updateAssessment(eq(assessmentId), eq(certificateType));

        integrationService.updateAssessment(assessmentId, certificateType);

        assertEquals(SchemaDateUtil.toDateStringFromLocalDateTime(DATE_TIME), response.getLastDateForCertificateReceival());
    }

    @Test
    public void testUpdateAssessmentResultError() {

        UpdateAssessmentResponseType response = new UpdateAssessmentResponseType();
        response.setResult(error("error text"));

        doReturn(response)
                .when(clientService)
                .updateAssessment(anyLong(), anyString());

        assertThatThrownBy(() -> integrationService.updateAssessment(1L, "string"))
                .isExactlyInstanceOf(IbExternalServiceException.class);

        verify(clientService, times(1))
                .updateAssessment(anyLong(), anyString());
    }

    @Test
    public void testReportDevation() {

        final ReportDeviationRequestDto requestDto = aReportDeviationRequestDto()
                .withBesokId("1")
                .withAvvikelseId("1")
                .withOrsakatAv(AvvikelseOrsak.PATIENT.name())
                .withBeskrivning("beskrivning")
                .withTidpunkt(SchemaDateUtil.toDateStringFromLocalDateTime(DATE_TIME))
                .withInvanareUteblev(true)
                .withSamordnare("Sam Ordnare")
                .build();

        ReportDeviationResponseType response = new ReportDeviationResponseType();
        response.setResult(ok());

        doReturn(response)
                .when(clientService)
                .reportDeviation(eq(requestDto));

        integrationService.reportDeviation(requestDto);

        verify(clientService, times(1))
                .reportDeviation(eq(requestDto));
    }

    @Test
    public void testReportDeviationResultError() {

        ReportDeviationResponseType response = new ReportDeviationResponseType();
        response.setResult(error("error text"));

        doReturn(response)
                .when(clientService)
                .reportDeviation(any(ReportDeviationRequestDto.class));

        assertThatThrownBy(() -> integrationService.reportDeviation(aReportDeviationRequestDto().build()))
                .isExactlyInstanceOf(IbExternalServiceException.class);

        verify(clientService, times(1))
                .reportDeviation(any(ReportDeviationRequestDto.class));
    }
}
