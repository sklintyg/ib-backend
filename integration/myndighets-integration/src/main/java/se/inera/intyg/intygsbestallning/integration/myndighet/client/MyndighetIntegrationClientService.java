package se.inera.intyg.intygsbestallning.integration.myndighet.client;

import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.RespondToPerformerRequestResponseType;

public interface MyndighetIntegrationClientService {
    RespondToPerformerRequestResponseType respondToPerformerRequest(String assessmentId);
}
