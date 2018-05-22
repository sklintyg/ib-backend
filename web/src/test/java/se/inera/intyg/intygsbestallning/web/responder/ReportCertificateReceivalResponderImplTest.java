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

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.service.utlatande.UtlatandeServiceImpl;
import se.inera.intyg.intygsbestallning.web.responder.dto.ReportUtlatandeMottagetRequest;
import se.riv.intygsbestallning.certificate.order.reportcertificatereceival.v1.ReportCertificateReceivalResponseType;
import se.riv.intygsbestallning.certificate.order.reportcertificatereceival.v1.ReportCertificateReceivalType;
import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static se.inera.intyg.intygsbestallning.common.util.RivtaTypesUtil.anII;

@RunWith(MockitoJUnitRunner.class)
public class ReportCertificateReceivalResponderImplTest {

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

        Assertions.assertThatThrownBy(() -> responder.reportCertificateReceival(null, type))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void reportCertificateReceivalNoRequest() {

        Assertions.assertThatThrownBy(() -> responder.reportCertificateReceival("address", null))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void reportCertificateReceivalEmptyRequest() {

        ReportCertificateReceivalType type = new ReportCertificateReceivalType();

        Assertions.assertThatThrownBy(() -> responder.reportCertificateReceival(null, type))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}