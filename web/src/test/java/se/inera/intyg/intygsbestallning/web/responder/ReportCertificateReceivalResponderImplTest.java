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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.riv.intygsbestallning.certificate.order.reportcertificatereceival.v1.ReportCertificateReceivalResponseType;
import se.riv.intygsbestallning.certificate.order.reportcertificatereceival.v1.ReportCertificateReceivalType;
import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;
import se.inera.intyg.intygsbestallning.service.utlatande.UtlatandeServiceImpl;
import se.inera.intyg.intygsbestallning.web.responder.dto.ReportUtlatandeMottagetRequest;
import se.inera.intyg.intygsbestallning.web.responder.resulthandler.ResultFactory;

@RunWith(MockitoJUnitRunner.class)
public class ReportCertificateReceivalResponderImplTest implements ResultFactory {

    @Mock
    private UtlatandeServiceImpl utlatandeService;

    @InjectMocks
    private ReportCertificateReceivalResponderImpl responder;
    public static final String MOTTAGET_DATUM = "20180909";
    public static final String SISTA_DATUM = "20180910";

    @Test
    public void reportCertificateReceival() {
        ReportCertificateReceivalType type = new ReportCertificateReceivalType();
        type.setAssessmentId(anII("root", "1"));
        type.setReceivedDate(MOTTAGET_DATUM);
        type.setLastDateForSupplementRequest(SISTA_DATUM);

        ReportUtlatandeMottagetRequest request = ReportUtlatandeMottagetRequest.from(type);

        doNothing().when(utlatandeService).reportUtlatandeMottaget(eq(request));
        final ReportCertificateReceivalResponseType response = responder.reportCertificateReceival("address", type);

        assertEquals(ResultCodeType.OK, response.getResult().getResultCode());
    }

    @Test
    public void reportCertificateReceivalNoAddress() {

        ReportCertificateReceivalType type = new ReportCertificateReceivalType();
        type.setAssessmentId(anII("root", "1"));
        type.setReceivedDate(MOTTAGET_DATUM);
        type.setLastDateForSupplementRequest(SISTA_DATUM);

        final ReportCertificateReceivalResponseType response = responder.reportCertificateReceival(null, type);

        assertThat(response.getResult().getResultCode()).isEqualTo(ResultCodeType.ERROR);
        assertThat(response.getResult().getResultText()).isEqualTo(LOGICAL_ADDRESS);
    }

    @Test
    public void reportCertificateReceivalNoRequest() {

        final ReportCertificateReceivalResponseType response = responder.reportCertificateReceival("address", null);

        assertThat(response.getResult().getResultCode()).isEqualTo(ResultCodeType.ERROR);
        assertThat(response.getResult().getResultText()).isEqualTo(REQUEST);
    }

    @Test
    public void reportCertificateReceivalEmptyRequest() {

        ReportCertificateReceivalType type = new ReportCertificateReceivalType();
        final ReportCertificateReceivalResponseType response = responder.reportCertificateReceival("address", type);

        assertThat(response.getResult().getResultCode()).isEqualTo(ResultCodeType.ERROR);
        assertThat(response.getResult().getResultText()).isEqualTo("AssessmentId must be defined");
    }
}