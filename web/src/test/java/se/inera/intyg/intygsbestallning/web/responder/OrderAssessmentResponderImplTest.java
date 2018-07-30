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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.aCv;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;
import static se.inera.intyg.intygsbestallning.persistence.model.Utredning.UtredningBuilder.anUtredning;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.AFU;
import static se.inera.intyg.intygsbestallning.persistence.model.type.UtredningsTyp.LIAG;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;
import se.riv.intygsbestallning.certificate.order.orderassessment.v1.OrderAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.orderassessment.v1.OrderAssessmentType;
import se.riv.intygsbestallning.certificate.order.v1.AuthorityAdministrativeOfficialType;
import se.riv.intygsbestallning.certificate.order.v1.CitizenType;
import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;
import java.lang.reflect.Field;
import java.util.Objects;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.service.utredning.dto.OrderRequest;

@RunWith(MockitoJUnitRunner.class)
public class OrderAssessmentResponderImplTest {

    @Mock
    private UtredningService utredningService;

    @InjectMocks
    private OrderAssessmentResponderImpl responder;

    @Test
    public void orderAssessmentSuccessFmu() {

        final Long utredningId = 1L;
        final String utredningIdString = utredningId.toString();
        final String utredningRoot = "utredningRoot";
        when(utredningService.registerOrder(any(OrderRequest.class))).thenReturn(anUtredning().withUtredningId(utredningId).build());

        OrderAssessmentType request = new OrderAssessmentType();
        request.setAssessmentId(anII(utredningRoot, utredningIdString));
        request.setCertificateType(aCv(AFU.name(), null, null));
        request.setAuthorityAdministrativeOfficial(new AuthorityAdministrativeOfficialType());
        request.setCareUnitId(anII(null, "enhet"));
        CitizenType citizen = new CitizenType();
        citizen.setPersonalIdentity(anII(null, "191212121212"));
        citizen.setFirstName("firstname");
        citizen.setMiddleName("middlename");
        citizen.setLastName("lastname");
        request.setCitizen(citizen);
        request.setLastDateForCertificateReceival("20180101");
        request.setOrderDate("20180101");
        OrderAssessmentResponseType response = responder.orderAssessment("address", request);

        assertNotNull(response);
        assertEquals(ResultCodeType.OK, response.getResult().getResultCode());
        assertEquals(utredningRoot, response.getAssessmentId().getRoot());
        assertEquals(utredningIdString, response.getAssessmentId().getExtension());
    }

    @Test
    public void orderAssessmentSuccessAf() {

        final Long utredningId = 1L;
        final String utredningRoot = "utredningRoot";
        Field field = Objects.requireNonNull(ReflectionUtils.findField(OrderAssessmentResponderImpl.class, "sourceSystemHsaId"));
        field.setAccessible(true);
        ReflectionUtils.setField(field, responder, utredningRoot);
        when(utredningService.registerNewUtredning(any(OrderRequest.class))).thenReturn(anUtredning().withUtredningId(utredningId).build());

        OrderAssessmentType request = new OrderAssessmentType();
        request.setCertificateType(aCv(LIAG.name(), null, null));
        request.setAuthorityAdministrativeOfficial(new AuthorityAdministrativeOfficialType());
        request.setCareUnitId(anII(null, "enhet"));
        CitizenType citizen = new CitizenType();
        citizen.setPersonalIdentity(anII(null, "191212121212"));
        request.setCitizen(citizen);
        OrderAssessmentResponseType response = responder.orderAssessment("address", request);

        assertNotNull(response);
        assertEquals(ResultCodeType.OK, response.getResult().getResultCode());
        assertEquals(utredningRoot, response.getAssessmentId().getRoot());
        assertEquals(utredningId.toString(), response.getAssessmentId().getExtension());
    }

    @Test
    public void orderAssessmentFailMissingCitizenPersonalIdentity() {

        OrderAssessmentType request = new OrderAssessmentType();
        request.setCertificateType(aCv(LIAG.name(), null, null));
        final OrderAssessmentResponseType response = responder.orderAssessment("address", request);

        assertThat(response.getResult().getResultCode()).isEqualTo(ResultCodeType.ERROR);
        assertThat(response.getResult().getResultText()).isEqualTo("Invalid Personal Identity Format for Citizen");
    }

    @Test
    public void orderAssessmentFailIncorrectFormatCitizenPersonalIdentity() {

        OrderAssessmentType request = new OrderAssessmentType();
        request.setCertificateType(aCv(LIAG.name(), null, null));

        CitizenType citizen = new CitizenType();
        citizen.setPersonalIdentity(anII(null, "Very Bad Format"));
        request.setCitizen(citizen);

        final OrderAssessmentResponseType response = responder.orderAssessment("address", request);

        assertThat(response.getResult().getResultCode()).isEqualTo(ResultCodeType.ERROR);
        assertThat(response.getResult().getResultText()).isEqualTo("Invalid Personal Identity Format for Citizen");
    }

    @Test
    public void orderAssessmentFailIncorrectCertificateType() {

        OrderAssessmentType request = new OrderAssessmentType();
        request.setCertificateType(aCv("NonExistingCode", null, null));

        CitizenType citizen = new CitizenType();
        citizen.setPersonalIdentity(anII(null, "191212121212"));
        request.setCitizen(citizen);
        final OrderAssessmentResponseType response = responder.orderAssessment("address", request);

        assertThat(response.getResult().getResultCode()).isEqualTo(ResultCodeType.ERROR);
        assertThat(response.getResult().getResultText()).isEqualTo("CertificateType is not of a known type");
    }
}
