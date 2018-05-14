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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static se.inera.intyg.intygsbestallning.common.util.ResultTypeUtil.ok;
import static se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto.ReportCareContactRequestDtoBuilder.aReportCareContactRequestDto;
import static se.inera.intyg.intygsbestallning.testutil.TestDataGen.DATE_TIME;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.integration.myndighet.client.MyndighetIntegrationClientServiceImpl;
import se.inera.intyg.intygsbestallning.integration.myndighet.dto.ReportCareContactRequestDto;
import se.inera.intyg.intygsbestallning.persistence.model.type.BesokStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.DeltagarProfessionTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.KallelseFormTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.TolkStatusTyp;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;
import se.riv.intygsbestallning.certificate.order.reportcarecontact.v1.ReportCareContactResponseType;
import se.riv.intygsbestallning.certificate.order.updateassessment.v1.UpdateAssessmentResponseType;

import java.time.format.DateTimeFormatter;

@RunWith(MockitoJUnitRunner.class)
public class MyndighetIntegrationServiceImplTest {

    @Mock
    private MyndighetIntegrationClientServiceImpl clientService;

    @InjectMocks
    private MyndighetIntegrationServiceImpl integrationService;

    @Test
    public void testReportCareContactInteraction() {

        final ReportCareContactRequestDto requestDto = aReportCareContactRequestDto()
                .withAssessmentId("assessment-id")
                .withAssessmentCareContactId("assessment-care-contact-id")
                .withParticipatingProfession(DeltagarProfessionTyp.LK.getLabel())
                .withInterpreterStatus(TolkStatusTyp.BOKAT.getLabel())
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
    public void testUpdateAssessment() {

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        final String assessmentId = "assessment-id";
        final String certificateType = UtredningsTyp.AFU_UTVIDGAD.name();

        UpdateAssessmentResponseType response = new UpdateAssessmentResponseType();
        response.setLastDateForCertificateReceival(formatter.format(DATE_TIME));
        response.setResult(ok());

        doReturn(response)
                .when(clientService)
                .updateAssessment(eq(assessmentId), eq(certificateType));

        integrationService.updateAssessment(assessmentId, certificateType);

        assertEquals(formatter.format(DATE_TIME), response.getLastDateForCertificateReceival());
    }
}