package se.inera.intyg.intygsbestallning.integration.myndighet.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"se.inera.intyg.intygsbestallning.integration.myndighet.client",
        "se.inera.intyg.intygsbestallning.integration.myndighet.service" })
public class MyndighetIntegrationConfiguration {
}
