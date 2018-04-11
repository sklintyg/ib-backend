package se.inera.intyg.intygsbestallning.web.responder;

import com.google.common.base.Preconditions;
import org.apache.cxf.annotations.SchemaValidation;
import org.springframework.stereotype.Service;
import se.riv.intygsbestallning.certificate.order.ordermedicalassessment.v1.OrderMedicalAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.ordermedicalassessment.v1.OrderMedicalAssessmentType;
import se.riv.intygsbestallning.certificate.order.ordermedicalassessment.v1.rivtabp21.OrderMedicalAssessmentResponderInterface;
import se.riv.intygsbestallning.certificate.order.v1.IIType;
import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;
import se.riv.intygsbestallning.certificate.order.v1.ResultType;

@Service
@SchemaValidation
public class OrderMedicalAssessmentResponderImpl implements OrderMedicalAssessmentResponderInterface {

    @Override
    public OrderMedicalAssessmentResponseType orderMedicalAssessment(
            final String logicalAddress, final OrderMedicalAssessmentType request) {

        Preconditions.checkArgument(null != logicalAddress);
        Preconditions.checkArgument(null != request);

        return createDummyResponse();
    }

    private OrderMedicalAssessmentResponseType createDummyResponse() {

        IIType iiType = new IIType();
        iiType.setExtension("DUMMY_EXTENSION");
        iiType.setRoot("DUMMY_ROOT");

        ResultType resultType = new ResultType();
        resultType.setResultCode(ResultCodeType.OK);
        resultType.setLogId("DUMMY_LOG_ID");
        resultType.setResultText("DUMMY_RESULT_TEXT");

        OrderMedicalAssessmentResponseType response = new OrderMedicalAssessmentResponseType();
        response.setAssessmentId(iiType);
        response.setResult(resultType);

        return response;
    }
}
