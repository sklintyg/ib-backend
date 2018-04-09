package se.inera.intyg.intygsbestallning.integration.myndighet.config;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.rivtabp21.RespondToPerformerRequestResponderInterface;

@Configuration
@Profile("!ib-stub")
public class MyndighetIntegrationClientConfiguration {

    @Value("${ib.myndighet.integration.url}")
    private String itWsUrl;

    @Bean
    public RespondToPerformerRequestResponderInterface respondToPerformerRequestResponderClient() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress(itWsUrl);
        proxyFactoryBean.setServiceClass(RespondToPerformerRequestResponderInterface.class);
        return (RespondToPerformerRequestResponderInterface) proxyFactoryBean.create();
    }
}
