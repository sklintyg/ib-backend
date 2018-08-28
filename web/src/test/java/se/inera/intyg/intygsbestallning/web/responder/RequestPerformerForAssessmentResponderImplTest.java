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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.aCv;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.type.MyndighetTyp.FKASSA;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.common.exception.IbResponderValidationException;
import se.inera.intyg.intygsbestallning.common.exception.IbServiceException;
import se.inera.intyg.intygsbestallning.integration.myndighet.service.TjanstekontraktUtils;
import se.riv.intygsbestallning.certificate.order.requestperformerforassessment.v1.RequestPerformerForAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.requestperformerforassessment.v1.RequestPerformerForAssessmentType;
import se.riv.intygsbestallning.certificate.order.v1.AuthorityAdministrativeOfficialType;
import se.riv.intygsbestallning.certificate.order.v1.CitizenLimitedType;
import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;
import java.text.MessageFormat;
import se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.dto.AssessmentRequest;

@RunWith(MockitoJUnitRunner.class)
public class RequestPerformerForAssessmentResponderImplTest {

    public static final String LOGICAL_ADDRESS = "logicalAddress";

    @Mock
    private UtredningService utredningService;

    @InjectMocks
    private RequestPerformerForAssessmentResponderImpl assessmentResponder;

    @Test
    public void requestPerformerForAssessmentOk() {

        final Long utredningId = 1L;

        CitizenLimitedType citizen = new CitizenLimitedType();
        citizen.setPostalCity("11111");

        RequestPerformerForAssessmentType request = new RequestPerformerForAssessmentType();
        request.setCertificateType(aCv(AFU.name(), TjanstekontraktUtils.KV_INTYGSTYP, null));
        AuthorityAdministrativeOfficialType authorityAdministrativeOfficialType = new AuthorityAdministrativeOfficialType();
        authorityAdministrativeOfficialType.setAuthority(aCv(FKASSA.name(), TjanstekontraktUtils.KV_MYNDIGHET, null));
        request.setAuthorityAdministrativeOfficial(authorityAdministrativeOfficialType);
        request.setCitizen(citizen);

        doReturn(anUtredning()
                .withUtredningId(utredningId)
                .build()
        ).when(utredningService).registerNewUtredning(AssessmentRequest.from(request));

        final RequestPerformerForAssessmentResponseType response =
                assessmentResponder.requestPerformerForAssessment(LOGICAL_ADDRESS, request);

        assertNotNull(response);
        assertEquals(utredningId.toString(), response.getAssessmentId().getExtension());

        assertNotNull(response.getResult());
        assertEquals(ResultCodeType.OK, response.getResult().getResultCode());
    }

    @Test
    public void requestPerformerForAssessmentFelaktigUtredningstypCodesystemNok() {

        final String felaktigtCodesystem = "felaktig";
        RequestPerformerForAssessmentType request = new RequestPerformerForAssessmentType();
        request.setCertificateType(aCv(AFU.name(), felaktigtCodesystem, null));

        assertThatThrownBy(() -> assessmentResponder.requestPerformerForAssessment(LOGICAL_ADDRESS, request))
                .isExactlyInstanceOf(IbResponderValidationException.class)
                .hasMessage(MessageFormat.format("Unexpected codeSystem: {0}", felaktigtCodesystem));
    }

    @Test
    public void requestPerformerForAssessmentOkandUtredningsTypNok() {

        final String okandUtredningsTyp = "okand-typ";
        RequestPerformerForAssessmentType request = new RequestPerformerForAssessmentType();
        request.setCertificateType(aCv(okandUtredningsTyp, TjanstekontraktUtils.KV_INTYGSTYP, null));

        assertThatThrownBy(() -> assessmentResponder.requestPerformerForAssessment(LOGICAL_ADDRESS, request))
                .isExactlyInstanceOf(IbResponderValidationException.class)
                .hasMessage(MessageFormat.format("Unknown code: {0} for codeSystem: {1}",
                        "okand-typ", TjanstekontraktUtils.KV_INTYGSTYP));
    }

    @Test
    public void requestPerformerForAssessmentFelaktigUtredningsTypNok() {

        final String felaktigUtredningsTyp = UtredningsTyp.LIAG.name();
        RequestPerformerForAssessmentType request = new RequestPerformerForAssessmentType();
        request.setCertificateType(aCv(felaktigUtredningsTyp, TjanstekontraktUtils.KV_INTYGSTYP, null));

        assertThatThrownBy(() -> assessmentResponder.requestPerformerForAssessment(LOGICAL_ADDRESS, request))
                .isExactlyInstanceOf(IbResponderValidationException.class)
                .hasMessage(MessageFormat.format("Unknown code: {0} for codeSystem: {1}",
                        felaktigUtredningsTyp, TjanstekontraktUtils.KV_INTYGSTYP));
    }
}
