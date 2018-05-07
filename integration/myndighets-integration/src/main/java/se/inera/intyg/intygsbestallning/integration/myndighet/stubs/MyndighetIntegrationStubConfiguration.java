/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.intygsbestallning.integration.myndighet.stubs;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import se.riv.intygsbestallning.certificate.order.respondtoorder.v1.rivtabp21.RespondToOrderResponderInterface;

@Configuration
@ComponentScan("se.inera.intyg.intygsbestallning.integration.myndighet.stubs")
@Profile({"dev", "ib-stub" })
public class MyndighetIntegrationStubConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Bus bus;

    @Bean
    public RespondToPerformerInteractionStub respondToPerformerInteractionStub() {
        return new RespondToPerformerInteractionStub();
    }

    @Bean
    public UpdateAssessmentStub updateAssessmentStub() {
        return new UpdateAssessmentStub();
    }

    @Bean
    public ReportCareContactInteractionStub reportCareContactInteractionStub() {
        return new ReportCareContactInteractionStub();
    }

    @Bean
    public ReportDeviationInteractionStub reportDeviationInteractionStub() {
        return new ReportDeviationInteractionStub();
    }

    @Bean
    public RespondToOrderResponderInterface respondToOrderInteractionStub() {
        return new RespondToOrderInteractionStub();
    }


    @Bean
    public EndpointImpl respondToPerformerInteractionStubEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, respondToPerformerInteractionStub());
        endpoint.publish("/respond-to-performer-interaction-stub");
        return endpoint;
    }

    @Bean
    public EndpointImpl updateAssessmentInteractionStubEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, updateAssessmentStub());
        endpoint.publish("/update-assessment-interaction-stub");
        return endpoint;
    }

    @Bean
    public EndpointImpl reportCareContactInteractionStubEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, reportCareContactInteractionStub());
        endpoint.publish("/report-care-contact-interaction-stub");
        return endpoint;
    }

    @Bean
    public EndpointImpl reportDeviationInteractionStubEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, reportDeviationInteractionStub());
        endpoint.publish("/report-deviation-interaction-stub");
        return endpoint;
    }

    @Bean
    public EndpointImpl respondToOrderInteractionStubEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, respondToOrderInteractionStub());
        endpoint.publish("/respond-to-order-interaction-stub");
        return endpoint;
    }
}
