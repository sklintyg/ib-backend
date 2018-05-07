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
package se.inera.intyg.intygsbestallning.web.responder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.aCv;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.dto.AssessmentRequest;
import se.riv.intygsbestallning.certificate.order.requesthealthcareperformerforassessment.v1.RequestHealthcarePerformerForAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.requesthealthcareperformerforassessment.v1.RequestHealthcarePerformerForAssessmentType;
import se.riv.intygsbestallning.certificate.order.v1.AuthorityAdministrativeOfficialType;
import se.riv.intygsbestallning.certificate.order.v1.CitizenLimitedType;
import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;

import java.text.MessageFormat;

@RunWith(MockitoJUnitRunner.class)
public class RequestHealthcarePerformerForAssessmentResponderImplTest {

    public static final String LOGICAL_ADDRESS = "logicalAddress";

    @Mock
    private UtredningService utredningService;

    @InjectMocks
    private RequestHealthcarePerformerForAssessmentResponderImpl assessmentResponder;

    @Test
    public void requestHealthcarePerformerForAssessmentOk() {

        final String utredningId = "utredning-id";

        CitizenLimitedType citizen = new CitizenLimitedType();
        citizen.setPostalCity("11111");

        RequestHealthcarePerformerForAssessmentType request = new RequestHealthcarePerformerForAssessmentType();
        request.setCertificateType(aCv(AFU.name(), null, null));
        request.setAuthorityAdministrativeOfficial(new AuthorityAdministrativeOfficialType());
        request.setCitizen(citizen);

        doReturn(anUtredning()
                .withUtredningId(utredningId)
                .build()
        ).when(utredningService).registerNewUtredning(AssessmentRequest.from(request));

        final RequestHealthcarePerformerForAssessmentResponseType response =
                assessmentResponder.requestHealthcarePerformerForAssessment(LOGICAL_ADDRESS, request);

        assertNotNull(response);
        assertEquals(utredningId, response.getAssessmentId().getExtension());

        assertNotNull(response.getResult());
        assertEquals(ResultCodeType.OK, response.getResult().getResultCode());
    }

    @Test
    public void requestHealthcarePerformerForAssessmentOkandUtredningsTypNok() {

        final String okandUtredningsTyp = "okand-typ";
            RequestHealthcarePerformerForAssessmentType request = new RequestHealthcarePerformerForAssessmentType();
            request.setCertificateType(aCv(okandUtredningsTyp, null, null));

        assertThatThrownBy(() -> assessmentResponder.requestHealthcarePerformerForAssessment(LOGICAL_ADDRESS, request))
                .isExactlyInstanceOf(IbServiceException.class)
                .hasMessage(MessageFormat.format("CertificateType: {0} is not of a known type", okandUtredningsTyp));
    }

    @Test
    public void requestHealthcarePerformerForAssessmentFelaktigUtredningsTypNok() {

        final String felakrigUtredningsTyp = UtredningsTyp.LIAG.name();
        RequestHealthcarePerformerForAssessmentType request = new RequestHealthcarePerformerForAssessmentType();
        request.setCertificateType(aCv(felakrigUtredningsTyp, null, null));

        assertThatThrownBy(() -> assessmentResponder.requestHealthcarePerformerForAssessment(LOGICAL_ADDRESS, request))
                .isExactlyInstanceOf(IbServiceException.class)
                .hasMessage(MessageFormat.format(
                        "CertificateType: {0} is not a valid a valid type. Use one of the following types: {1})",
                        felakrigUtredningsTyp,
                        ImmutableList.of(UtredningsTyp.AFU, UtredningsTyp.AFU_UTVIDGAD)));
    }
}
