package se.inera.intyg.intygsbestallning.integration.myndighet.stubs;

import application.riv.intygsbestallning.certificate.order._1.ResultCodeType;
import application.riv.intygsbestallning.certificate.order._1.ResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.rivtabp21.v1.RespondToPerformerRequestResponderInterface;
import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.RespondToPerformerRequestResponseType;
import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.RespondToPerformerRequestType;

@Service
@Profile({"ib-stub"})
public class RespondToPerformerRequestStub implements RespondToPerformerRequestResponderInterface {

    private static final Logger LOG = LoggerFactory.getLogger(RespondToPerformerRequestStub.class);

    @Override
    public RespondToPerformerRequestResponseType respondToPerformerRequest(String id, RespondToPerformerRequestType request) {
        LOG.info("RespondToPerformerRequestStub received request {}", id);
        RespondToPerformerRequestResponseType response = new RespondToPerformerRequestResponseType();
        ResultType rt = new ResultType();
        rt.setResultCode(ResultCodeType.OK);
        rt.setResultText("Result");
        response.setResult(rt);
        return response;
    }
}
