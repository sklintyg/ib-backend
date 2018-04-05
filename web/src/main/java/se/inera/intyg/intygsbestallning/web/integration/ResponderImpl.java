package se.inera.intyg.intygsbestallning.web.integration;

import application.riv.intygsbestallning.certificate.order._1.IIType;
import application.riv.intygsbestallning.certificate.order._1.ResultCodeType;
import application.riv.intygsbestallning.certificate.order._1.ResultType;
import com.google.common.base.Preconditions;
import org.apache.cxf.annotations.SchemaValidation;
import se.riv.intygsbestallning.certificate.order.requesthealthcareperformerforassessment.rivtabp21.v1.RequestHealthcarePerformerForAssessmentResponderInterface;
import se.riv.intygsbestallning.certificate.order.requesthealthcareperformerforassessment.v1.RequestHealthcarePerformerForAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.requesthealthcareperformerforassessment.v1.RequestHealthcarePerformerForAssessmentType;

@SchemaValidation
public class ResponderImpl implements RequestHealthcarePerformerForAssessmentResponderInterface {

    @Override
    public RequestHealthcarePerformerForAssessmentResponseType requestHealthcarePerformerForAssessment(
            final String logicalAddress, final RequestHealthcarePerformerForAssessmentType request) {

        Preconditions.checkArgument(null != logicalAddress);
        Preconditions.checkArgument(null != request);

        return createDummyResponse();
    }

    private RequestHealthcarePerformerForAssessmentResponseType createDummyResponse() {
        RequestHealthcarePerformerForAssessmentResponseType response = new RequestHealthcarePerformerForAssessmentResponseType();

        ResultType resultType = new ResultType();
        resultType.setResultCode(ResultCodeType.OK);

        IIType iiType = new IIType();
        iiType.setExtension("DUMMY_EXTENSION");
        iiType.setRoot("DUMMY_ROOT");

        response.setAssessmentId(iiType);
        response.setResult(resultType);

        return response;
    }
}
