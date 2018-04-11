package se.inera.intyg.intygsbestallning.integration.myndighet.stubs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.riv.intygsbestallning.certificate.order.updateassessment.v1.UpdateAssessmentResponseType;
import se.riv.intygsbestallning.certificate.order.updateassessment.v1.UpdateAssessmentType;
import se.riv.intygsbestallning.certificate.order.updateassessment.v1.rivtabp21.UpdateAssessmentResponderInterface;
import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;
import se.riv.intygsbestallning.certificate.order.v1.ResultType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UpdateAssessmentStub implements UpdateAssessmentResponderInterface {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateAssessmentStub.class);

    @Override
    public UpdateAssessmentResponseType updateAssessment(String s, UpdateAssessmentType updateAssessmentType) {
        LOG.info("UpdateAssessmentStub received request");
        UpdateAssessmentResponseType response = new UpdateAssessmentResponseType();
        response.setLastDateForCertificateReceival(LocalDateTime.now().plusDays(7L).format(DateTimeFormatter.ISO_DATE));
        ResultType rt = new ResultType();
        rt.setResultCode(ResultCodeType.OK);
        response.setResult(rt);
        return response;
    }
}
