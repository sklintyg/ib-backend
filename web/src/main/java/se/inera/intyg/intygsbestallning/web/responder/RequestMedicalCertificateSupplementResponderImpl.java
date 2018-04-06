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

import application.riv.intygsbestallning.certificate.order._1.IIType;
import application.riv.intygsbestallning.certificate.order._1.ResultCodeType;
import application.riv.intygsbestallning.certificate.order._1.ResultType;
import com.google.common.base.Preconditions;
import org.apache.cxf.annotations.SchemaValidation;
import se.riv.intygsbestallning.certificate.order.requestmedicalcertificatesupplement.rivtabp21.v1.RequestMedicalCertificateSupplementResponderInterface;
import se.riv.intygsbestallning.certificate.order.requestmedicalcertificatesupplement.v1.RequestMedicalCertificateSupplementResponseType;
import se.riv.intygsbestallning.certificate.order.requestmedicalcertificatesupplement.v1.RequestMedicalCertificateSupplementType;

@SchemaValidation
public class RequestMedicalCertificateSupplementResponderImpl implements RequestMedicalCertificateSupplementResponderInterface {
    @Override
    public RequestMedicalCertificateSupplementResponseType requestMedicalCertificateSupplement(
            final String logicalAddress, final RequestMedicalCertificateSupplementType request) {

        Preconditions.checkArgument(null != logicalAddress);
        Preconditions.checkArgument(null != request);

        return createDummyResponse();
    }

    private RequestMedicalCertificateSupplementResponseType createDummyResponse() {

        IIType iiType = new IIType();
        iiType.setRoot("DUMMY_ROOT");
        iiType.setExtension("DUMMY_EXTENSION");

        ResultType resultType = new ResultType();
        resultType.setResultText("DUMMY_RESULT_TEXT");
        resultType.setLogId("DUMMY_LOG_ID");
        resultType.setResultCode(ResultCodeType.OK);

        RequestMedicalCertificateSupplementResponseType response = new RequestMedicalCertificateSupplementResponseType();
        response.setResult(resultType);
        response.setSupplementRequestId(iiType);

        return response;
    }
}
