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
package se.inera.intyg.intygsbestallning.config;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.ServletContextAttributeExporter;
import java.util.HashMap;
import java.util.Map;

import se.inera.intyg.intygsbestallning.config.interceptor.SoapFaultToSoapResponseTransformerInterceptor;
import se.inera.intyg.intygsbestallning.service.monitoring.HealthCheckService;
import se.inera.intyg.intygsbestallning.service.monitoring.InternalPingForConfigurationResponderImpl;
import se.inera.intyg.intygsbestallning.web.responder.EndAssessmentResponderImpl;
import se.inera.intyg.intygsbestallning.web.responder.OrderAssessmentResponderImpl;
import se.inera.intyg.intygsbestallning.web.responder.ReportCertificateReceivalResponderImpl;
import se.inera.intyg.intygsbestallning.web.responder.ReportDeviationInteractionResponderImpl;
import se.inera.intyg.intygsbestallning.web.responder.ReportSupplementReceivalResponderImpl;
import se.inera.intyg.intygsbestallning.web.responder.RequestPerformerForAssessmentResponderImpl;
import se.inera.intyg.intygsbestallning.web.responder.RequestSupplementResponderImpl;
import se.inera.intyg.intygsbestallning.web.responder.UpdateOrderResponderImpl;

@Configuration
@ComponentScan({
        "se.inera.intyg.intygsbestallning.service",
        "se.inera.intyg.intygsbestallning.auth",
        "se.inera.intyg.intygsbestallning.common",
        "se.inera.intyg.intygsbestallning.jobs",
        "se.inera.intyg.intygsbestallning.web"})
public class ServiceConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HealthCheckService healtCheckService;

    @Autowired
    private Bus bus;

    @Autowired
    private InternalPingForConfigurationResponderImpl internalPingForConfigurationResponder;

    @Autowired
    private RequestPerformerForAssessmentResponderImpl requestPerformerForAssessmentResponder;

    @Autowired
    private UpdateOrderResponderImpl updateOrderResponder;

    @Autowired
    private RequestSupplementResponderImpl requestSupplementResponder;

    @Autowired
    private ReportSupplementReceivalResponderImpl reportSupplementReceivalResponder;

    @Autowired
    private ReportCertificateReceivalResponderImpl reportCertificateReceivalResponder;

    @Autowired
    private OrderAssessmentResponderImpl orderAssessmentResponder;

    @Autowired
    private ReportDeviationInteractionResponderImpl reportDeviationInteractionResponder;

    @Autowired
    private EndAssessmentResponderImpl endAssessmentResponder;

    @Bean
    public ServletContextAttributeExporter contextAttributes() {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put("healthcheck", healtCheckService);
        final ServletContextAttributeExporter exporter = new ServletContextAttributeExporter();
        exporter.setAttributes(attributes);
        return exporter;
    }

    @Bean
    public EndpointImpl pingForConfigurationEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, internalPingForConfigurationResponder);
        endpoint.publish("/internal-ping-for-configuration");
        return endpoint;
    }

    @Bean
    public EndpointImpl requestPerformerForAssessmentResponderEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, requestPerformerForAssessmentResponder);
        endpoint.publish("/request-performer-for-assessment-responder");
        endpoint.getOutFaultInterceptors().add(new SoapFaultToSoapResponseTransformerInterceptor(
                "transform/request-performer-for-assessment.xslt"));
        return endpoint;
    }

    @Bean
    public EndpointImpl updateOrderResponderEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, updateOrderResponder);
        endpoint.publish("/update-order-responder");
        endpoint.getOutFaultInterceptors().add(new SoapFaultToSoapResponseTransformerInterceptor("transform/update-order.xslt"));
        return endpoint;
    }

    @Bean
    public EndpointImpl requestCertificateSupplementEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, requestSupplementResponder);
        endpoint.publish("/request-supplement-responder");
        endpoint.getOutFaultInterceptors().add(new SoapFaultToSoapResponseTransformerInterceptor("transform/request-supplement.xslt"));
        return endpoint;
    }

    @Bean
    public EndpointImpl reportSupplementReceivalResponderEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, reportSupplementReceivalResponder);
        endpoint.publish("/report-supplement-receival-responder");
        endpoint.getOutFaultInterceptors().add(new SoapFaultToSoapResponseTransformerInterceptor(
                "transform/report-supplement-receival.xslt"));
        return endpoint;
    }

    @Bean
    public EndpointImpl reportCertificateReceivalResponderEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, reportCertificateReceivalResponder);
        endpoint.publish("/report-certificate-receival-responder");
        endpoint.getOutFaultInterceptors().add(new SoapFaultToSoapResponseTransformerInterceptor(
                "transform/report-certificate-receival.xslt"));
        return endpoint;
    }

    @Bean
    public EndpointImpl orderAssessmentResponderEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, orderAssessmentResponder);
        endpoint.publish("/order-assessment-responder");
        endpoint.getOutFaultInterceptors().add(new SoapFaultToSoapResponseTransformerInterceptor("transform/order-assessment.xslt"));
        return endpoint;
    }

    @Bean
    public EndpointImpl reportDeviationInteractionResponderEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, reportDeviationInteractionResponder);
        endpoint.publish("/report-deviation-interaction-responder");
        endpoint.getOutFaultInterceptors().add(new SoapFaultToSoapResponseTransformerInterceptor("transform/report-deviation.xslt"));
        return endpoint;
    }

    @Bean
    public EndpointImpl endAssessmentResponderEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, endAssessmentResponder);
        endpoint.publish("/end-assessment-responder");
        endpoint.getOutFaultInterceptors().add(new SoapFaultToSoapResponseTransformerInterceptor("transform/end-assessment.xslt"));
        return endpoint;
    }
}
