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
