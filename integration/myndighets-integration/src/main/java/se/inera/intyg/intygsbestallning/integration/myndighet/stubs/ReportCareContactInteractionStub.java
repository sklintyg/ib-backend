package se.inera.intyg.intygsbestallning.integration.myndighet.stubs;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.riv.intygsbestallning.certificate.order.reportcarecontact.v1.ReportCareContactResponseType;
import se.riv.intygsbestallning.certificate.order.reportcarecontact.v1.ReportCareContactType;
import se.riv.intygsbestallning.certificate.order.reportcarecontact.v1.rivtabp21.ReportCareContactResponderInterface;
import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;
import se.riv.intygsbestallning.certificate.order.v1.ResultType;

import java.util.Objects;

@Service
public class ReportCareContactInteractionStub implements ReportCareContactResponderInterface {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateAssessmentStub.class);

    @Override
    public ReportCareContactResponseType reportCareContact(final String logicalAddress, final ReportCareContactType request) {

        LOG.info("ReportCareContactInteractionStub received request");

        Preconditions.checkArgument(!Strings.isNullOrEmpty(logicalAddress), "logicalAddress may not be null or empty");
        Preconditions.checkArgument(!Objects.isNull(request), "request may not be null");

        return createDummyResponse();
    }

    private ReportCareContactResponseType createDummyResponse() {

        ResultType resultType = new ResultType();
        resultType.setLogId("DUMMY_LOG_ID");
        resultType.setResultCode(ResultCodeType.OK);
        resultType.setResultText("DUMMY_RESULT_TEXT");

        ReportCareContactResponseType response = new ReportCareContactResponseType();
        response.setResult(resultType);

        return response;
    }


}
