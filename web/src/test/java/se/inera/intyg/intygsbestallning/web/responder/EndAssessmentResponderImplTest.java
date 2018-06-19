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
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.aCv;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.riv.intygsbestallning.certificate.order.endassessment.v1.EndAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.endassessment.v1.EndAssessmentType;
import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;
import se.inera.intyg.intygsbestallning.persistence.model.type.AvslutOrsak;
import se.inera.intyg.intygsbestallning.service.utredning.UtredningService;
import se.inera.intyg.intygsbestallning.web.responder.resulthandler.ResultFactory;

@RunWith(MockitoJUnitRunner.class)
public class EndAssessmentResponderImplTest implements ResultFactory {

    @Mock
    private UtredningService utredningService;

    @InjectMocks
    private EndAssessmentResponderImpl responder;

    @Test
    public void testEndAssessmentSuccess() {
        EndAssessmentType request = new EndAssessmentType();
        request.setAssessmentId(anII(null, "utredningId"));
        request.setEndingCondition(aCv(AvslutOrsak.JAV.name(), null, null));

        EndAssessmentResponseType response = responder.endAssessment("logicalAdress", request);

        assertNotNull(response);
        assertNotNull(response.getResult());
        assertEquals(ResultCodeType.OK, response.getResult().getResultCode());
    }

    @Test
    public void testEndAssessmentFailPreconditionLogicalAddress() {
        final EndAssessmentResponseType response = responder.endAssessment(null, new EndAssessmentType());
        assertThat(response.getResult().getResultCode()).isEqualTo(ResultCodeType.ERROR);
        assertThat(response.getResult().getResultText()).isEqualTo(LOGICAL_ADDRESS);
    }

    @Test
    public void testEndAssessmentFailPreconditionRequest() {
        final EndAssessmentResponseType response = responder.endAssessment("logicalAddress", null);
        assertThat(response.getResult().getResultCode()).isEqualTo(ResultCodeType.ERROR);
        assertThat(response.getResult().getResultText()).isEqualTo(REQUEST);
    }

    @Test
    public void testEndAssessmentFailConvert() {
        EndAssessmentType request = new EndAssessmentType();
        request.setAssessmentId(anII(null, "utredningId"));
        request.setEndingCondition(aCv("nonExistingCode", null, null));

        final EndAssessmentResponseType response = responder.endAssessment("logicalAdress", request);

        assertThat(response.getResult().getResultCode()).isEqualTo(ResultCodeType.ERROR);
        assertThat(response.getResult().getResultText()).isEqualTo("EndingCondition is not of a known type");
    }
}
