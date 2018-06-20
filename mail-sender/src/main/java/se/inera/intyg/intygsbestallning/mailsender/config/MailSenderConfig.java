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
package se.inera.intyg.intygsbestallning.mailsender.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.connection.TransactionAwareConnectionFactoryProxy;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

@EnableJms
@Configuration
@ComponentScan(value = "se.inera.intyg.intygsbestallning.mailsender")
public class MailSenderConfig extends CamelConfiguration {

    @Value("${activemq.broker.url}")
    private String activeMqBrokerUrl;
    @Value("${activemq.broker.username}")
    private String activeMqBrokerUsername;
    @Value("${activemq.broker.password")
    private String activeMqBrokerPassword;

    @Bean
    public SpringTransactionPolicy myTxPolicy() {
        return new SpringTransactionPolicy(jmsTransactionManager());
    }

    @Bean
    public TransactionAwareConnectionFactoryProxy transactionAwareConnectionFactoryProxy() {
        JmsTemplate jmsTemplate = jmsTemplate(connectionFactory());
        ConnectionFactory connectionFactory = jmsTemplate.getConnectionFactory();
        if (connectionFactory != null) {
            return new TransactionAwareConnectionFactoryProxy(connectionFactory);
        }
        throw new IllegalStateException("Unable to create TransactionAwareConnectionFactoryProxy, ConnectionFactory was null.");
    }

    @Bean
    public JmsTransactionManager jmsTransactionManager() {
        return new JmsTransactionManager(transactionAwareConnectionFactoryProxy());
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(activeMqBrokerUsername, activeMqBrokerPassword, activeMqBrokerUrl);
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }
}
