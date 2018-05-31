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
package se.inera.intyg.intygsbestallning.integration.myndighet.config;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import se.riv.intygsbestallning.certificate.order.reportcarecontact.v1.rivtabp21.ReportCareContactResponderInterface;
import se.riv.intygsbestallning.certificate.order.reportdeviation.v1.rivtabp21.ReportDeviationResponderInterface;
import se.riv.intygsbestallning.certificate.order.respondtoperformerrequest.v1.rivtabp21.RespondToPerformerRequestResponderInterface;
import se.riv.intygsbestallning.certificate.order.updateassessment.v1.rivtabp21.UpdateAssessmentResponderInterface;

@Configuration
@Profile("!ib-stub")
public class MyndighetIntegrationClientConfiguration {

    @Value("${ib.myndighet.integration.url}")
    private String ntjpWsUrl;

    @Value("${respondtoperformerrequest.url}")
    private String respondtoperformerrequestUrl;

    @Value("${updateassessment.url}")
    private String updateassessmentUrl;

    @Value("${reportcarecontact.url}")
    private String reportcarecontactUrl;

    @Value("${reportdeviation.url}")
    private String reportdeviationUrl;

    @Bean
    public RespondToPerformerRequestResponderInterface respondToPerformerRequestResponderClient() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress(ntjpWsUrl + respondtoperformerrequestUrl);
        proxyFactoryBean.setServiceClass(RespondToPerformerRequestResponderInterface.class);
        return (RespondToPerformerRequestResponderInterface) proxyFactoryBean.create();
    }

    @Bean
    public UpdateAssessmentResponderInterface respondToUpdateAssessmentResponderClient() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress(ntjpWsUrl + updateassessmentUrl);
        proxyFactoryBean.setServiceClass(UpdateAssessmentResponderInterface.class);
        return (UpdateAssessmentResponderInterface) proxyFactoryBean.create();
    }

    @Bean
    public ReportCareContactResponderInterface respondToReportCareContactResponderClient() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress(ntjpWsUrl + reportcarecontactUrl);
        proxyFactoryBean.setServiceClass(ReportCareContactResponderInterface.class);
        return (ReportCareContactResponderInterface) proxyFactoryBean.create();
    }
    @Bean
    @Qualifier("reportDeviationResponderMyndighet")
    public ReportDeviationResponderInterface respondToReportDeviationResponderClient() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setAddress(ntjpWsUrl + reportdeviationUrl);
        proxyFactoryBean.setServiceClass(ReportDeviationResponderInterface.class);
        return (ReportDeviationResponderInterface) proxyFactoryBean.create();
    }
}
