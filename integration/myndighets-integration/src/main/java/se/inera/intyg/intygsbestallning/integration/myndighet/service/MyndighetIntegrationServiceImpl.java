package se.inera.intyg.intygsbestallning.integration.myndighet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.intygsbestallning.integration.myndighet.client.MyndighetIntegrationClientService;

@Service
public class MyndighetIntegrationServiceImpl implements MyndighetIntegrationService {

    @Autowired
    private MyndighetIntegrationClientService clientService;

    @Override
    public void respondToPerformerRequest(String id) {
        // Do something meaningful here
        clientService.respondToPerformerRequest(id);
    }
}
