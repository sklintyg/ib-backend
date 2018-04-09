package se.inera.intyg.intygsbestallning.integration.myndighet.stubs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.RespondToPerformerRequestResponseType;
import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.RespondToPerformerRequestType;
import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.rivtabp21.RespondToPerformerRequestResponderInterface;
import se.riv.intygsbestallning.certificate.order.v1.ResultCodeType;
import se.riv.intygsbestallning.certificate.order.v1.ResultType;

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
