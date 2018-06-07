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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.intygsbestallning.service.utredning.KompletteringService;
import se.riv.intygsbestallning.certificate.order.requestmedicalcertificatesupplement.v1.RequestMedicalCertificateSupplementResponseType;
import se.riv.intygsbestallning.certificate.order.requestmedicalcertificatesupplement.v1.RequestMedicalCertificateSupplementType;
import se.riv.intygsbestallning.certificate.order.v1.IIType;
import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestMedicalCertificateSupplementResponderImplTest {

    private static final String ERROR_MESSAGE = "some error message";

    @Mock
    private KompletteringService kompletteringService;

    @InjectMocks
    private RequestMedicalCertificateSupplementResponderImpl testee;

    @Test
    public void testOk() {
        when(kompletteringService.registerNewKomplettering(any(RequestMedicalCertificateSupplementType.class))).thenReturn(1L);
        RequestMedicalCertificateSupplementResponseType response = testee.requestMedicalCertificateSupplement("", buildRequest());
        assertEquals(ResultCodeType.OK, response.getResult().getResultCode());
    }

    @Test
    public void testWhenLastDateForSupplementReceivalIsNull() {
        when(kompletteringService.registerNewKomplettering(any(RequestMedicalCertificateSupplementType.class))).thenReturn(1L);

        RequestMedicalCertificateSupplementType request = buildRequest();
        buildRequest().setLastDateForSupplementReceival(null);

        RequestMedicalCertificateSupplementResponseType response = testee.requestMedicalCertificateSupplement("", request);
        assertEquals(ResultCodeType.OK, response.getResult().getResultCode());
    }

    @Test
    public void testExceptionsAreMappedOntoError() {
        when(kompletteringService.registerNewKomplettering(any(RequestMedicalCertificateSupplementType.class))).thenThrow(new IllegalStateException(ERROR_MESSAGE));
        RequestMedicalCertificateSupplementResponseType response = testee.requestMedicalCertificateSupplement("", buildRequest());
        assertEquals(ERROR_MESSAGE, response.getResult().getResultText());
        assertEquals(ResultCodeType.ERROR, response.getResult().getResultCode());
    }

    @Test
    public void testExceptionsWhenLastDateForSupplementReceivalIsNotParsable() {
        when(kompletteringService.registerNewKomplettering(any(RequestMedicalCertificateSupplementType.class))).thenThrow(new IllegalStateException(ERROR_MESSAGE));

        RequestMedicalCertificateSupplementType request = buildRequest();
        buildRequest().setLastDateForSupplementReceival("2018-06-aa"); // bad date

        RequestMedicalCertificateSupplementResponseType response = testee.requestMedicalCertificateSupplement("", request);
        assertEquals(ERROR_MESSAGE, response.getResult().getResultText());
        assertEquals(ResultCodeType.ERROR, response.getResult().getResultCode());
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
