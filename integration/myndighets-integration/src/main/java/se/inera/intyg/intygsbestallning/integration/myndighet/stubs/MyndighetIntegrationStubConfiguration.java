package se.inera.intyg.intygsbestallning.integration.myndighet.stubs;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan({ "se.inera.intyg.intygsbestallning.integration.myndighet.stubs" })
@Profile({"ib-stub" })
public class MyndighetIntegrationStubConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RespondToPerformerRequestStub respondToPerformerRequestStub;

    @Autowired
    private Bus bus;

    @Bean
    public EndpointImpl respondToPerformerRequest() {
        Object implementor = respondToPerformerRequestStub;
        EndpointImpl endpoint = new EndpointImpl(bus, implementor);
        endpoint.publish("/respond-to-performer-request");
        return endpoint;
    }

}
