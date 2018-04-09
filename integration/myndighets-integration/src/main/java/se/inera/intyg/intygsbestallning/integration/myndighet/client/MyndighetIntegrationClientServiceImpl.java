package se.inera.intyg.intygsbestallning.integration.myndighet.client;

import application.riv.intygsbestallning.certificate.order._1.IIType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.rivtabp21.v1.RespondToPerformerRequestResponderInterface;
import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.RespondToPerformerRequestResponseType;
import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.RespondToPerformerRequestType;

@Service
public class MyndighetIntegrationClientServiceImpl implements MyndighetIntegrationClientService {

    @Autowired
    private RespondToPerformerRequestResponderInterface respondToPerformerRequestResponder;

    @Override
    public RespondToPerformerRequestResponseType respondToPerformerRequest(String assessmentId) {
        RespondToPerformerRequestType request = new RespondToPerformerRequestType();

        IIType assID = new IIType();
        assID.setExtension(assessmentId);
        request.setAssessmentId(assID);

        return respondToPerformerRequestResponder.respondToPerformerRequest("ID?", request);
    }
}
