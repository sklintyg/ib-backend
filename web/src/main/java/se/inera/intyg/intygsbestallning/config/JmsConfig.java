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

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

/**
 * Creates connection factory and JMS templates for communicating with ActiveMQ. Note that this JmsConfig creates its
 * {@link ActiveMQConnectionFactory} directly rather than looking up a container-provided ConnectionFactory through
 * JNDI.
 *
 * Created by eriklupander on 2016-02-18.
 */
@Configuration
public class JmsConfig {

    @Value("${activemq.broker.url}")
    private String activeMqBrokerUrl;

    @Value("${activemq.broker.username}")
    private String activeMqBrokerUsername;

    @Value("${activemq.broker.password}")
    private String activeMqBrokerPassword;

    @Value("${pdl.logging.queue.name}")
    private String loggingQueueName;

    @Value("${aggregated.pdl.logging.queue.name}")
    private String aggregatedLoggingQueueName;

    @Value("${mailsender.queue.name}")
    private String mailSenderQueueName;

    @Bean
    public JmsTransactionManager jmsTransactionManager() {
        return new JmsTransactionManager(connectionFactory());
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(activeMqBrokerUsername, activeMqBrokerPassword, activeMqBrokerUrl);
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        return new JmsTemplate(connectionFactory);
    }

    @Bean
    public JmsTemplate jmsPDLLogTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestinationName(loggingQueueName);
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    @Bean
    public JmsTemplate jmsAggregatedPDLLogTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestinationName(aggregatedLoggingQueueName);
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    @Bean
    public JmsTemplate jmsNotificationMailTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestinationName(mailSenderQueueName);
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }
}
